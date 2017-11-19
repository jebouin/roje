package roje.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import roje.Main;
import roje.api.MarvelAPI;
import roje.model.Comics;

public class ComicSearchController {

	@FXML
	private TextField searchTextField;

	@FXML
	private Label statusLabel;

	@FXML
	private ListView<String> comicsListView;

	private ObservableList<String> comicsFound;
	private Map<String, Integer> comics;
	private String lastSearch;

	/**
	 * Initializes the controller class. This method is automatically called after
	 * the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		statusLabel.setText("");
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
		if (event.getClickCount() == 2) {
			String item = comicsListView.getSelectionModel().getSelectedItem();
			Integer id = comics.get(item);
			System.out.println(id);

			Comics comic = MarvelAPI.getComicById(id);

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/ComicCardView.fxml"));
			ScrollPane pane = (ScrollPane) loader.load();
			loader.<ComicCardController>getController().setComic(comic);
			Stage stage = new Stage();
			stage.setTitle(comic.getTitle());
			stage.setScene(new Scene(pane, 800, 500));
			stage.show();

		}
	}

	private void search() throws Exception {
		String toSearch = searchTextField.getText();
		if (toSearch == lastSearch) {
			return;
		}
		lastSearch = toSearch;
		statusLabel.setText("Loading...");
		comics = MarvelAPI.searchComicsByNamePrefix(toSearch);
		List<String> titles = new ArrayList<String>(comics.keySet());
		comicsFound = FXCollections.observableList(titles);
		comicsListView.setItems(comicsFound);
		if (comicsFound.size() == 0) {
			statusLabel.setText("No results found");
		} else if (comicsFound.size() == 1) {
			statusLabel.setText("1 result found");
		} else {
			statusLabel.setText(comicsFound.size() + " results found");
		}
	}
}
