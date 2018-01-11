package roje.view;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import roje.Main;
import roje.model.Comics;
import roje.model.ComicsDAO;

public class ComicSearchController {

	@FXML
	private TextField searchTextField;

	@FXML
	private Button searchButton;

	@FXML
	private ImageView loadingIcon;
	private Timeline loadingTimeline;

	@FXML
	private Label statusLabel;

	@FXML
	private TableView<Comics> comicsTableView;

	@FXML
	private TableColumn<Comics, String> titleColumn;

	@FXML
	private TableColumn<Comics, String> dateColumn;

	private ObservableList<Comics> comicsFound;
	private String lastSearch;

	/**
	 * Initializes the controller class. This method is automatically called after
	 * the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		statusLabel.setText("");
		loadingTimeline = Animations.getDiscreteRotation(loadingIcon, 800, 8);
		loadingIcon.setVisible(false);
		comicsFound = FXCollections.observableArrayList();
		comicsTableView.setItems(comicsFound);
		titleColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getTitle()));
		dateColumn.setCellValueFactory(
				cellData -> new ReadOnlyStringWrapper(cellData.getValue().getOnSaleDateAsString()));
	}

	/**
	 * Called when the user clicks on the search button.
	 */
	@FXML
	private void handleSearchButtonPressed() throws Exception {
		search();
	}

	@FXML
	private void handleEnterPressed(KeyEvent event) throws Exception {
		if (event.getCode() == KeyCode.ENTER) {
			search();
		}
	}

	@FXML
	private void handleListClick(MouseEvent event) throws Exception {
		System.out.println(event.getClickCount());
		if (event.getClickCount() == 2) {
			Comics selectedComic = comicsTableView.getSelectionModel().getSelectedItem();
			System.out.println("Id du comic :" + selectedComic.getId());
			if (selectedComic != null) {
				Main.instance.showComicCard(selectedComic);
			}
		}
	}

	private void sortComicTitles(List<String> titles) {
		titles.sort((String a, String b) -> {
			String s1 = a.replaceAll("#\\d+.*", "");
			String s2 = b.replaceAll("#\\d+.*", "");
			if (!s1.equals(s2)) {
				return s1.compareTo(s2);
			}
			Pattern pattern = Pattern.compile("#(\\d+)");
			Integer n1 = null, n2 = null;
			Matcher matcher = pattern.matcher(a);
			if (matcher.find()) {
				n1 = Integer.decode(matcher.group());
			} else {
				return -1;
			}
			matcher = pattern.matcher(b);
			if (matcher.find()) {
				n2 = Integer.decode(matcher.group());
			} else {
				return 1;
			}
			return -Integer.compare(n1, n2);
		});
	}

	private void search() throws Exception {
		String toSearch = searchTextField.getText();
		if (toSearch == lastSearch) {
			return;
		}
		lastSearch = toSearch;
		loadingIcon.setVisible(true);
		loadingTimeline.play();
		searchButton.setDisable(true);
		statusLabel.setText("Loading...");
		if (comicsFound != null) {
			comicsFound.clear();
		}

		Task<Void> downloadComics = new Task<Void>() {
			@Override
			public Void call() throws Exception {
				List<Comics> comics = ComicsDAO.findComicByPrefix(toSearch);
				comicsFound.clear();
				comicsFound.addAll(comics);
				Platform.runLater(() -> {
					if (comicsFound.size() == 0) {
						statusLabel.setText("No results found");
					} else if (comicsFound.size() == 1) {
						statusLabel.setText("1 result found");
					} else {
						statusLabel.setText(comicsFound.size() + " results found");
					}
					loadingTimeline.stop();
					loadingIcon.setVisible(false);
					searchButton.setDisable(false);
				});
				// cache them
				for (Comics comic : comics) {
					if (ComicsDAO.findComic(comic.getId()) == null) {
						ComicsDAO.addComic(comic);
					}
				}
				return null;
			}
		};
		new Thread(downloadComics).start();
	}
}
