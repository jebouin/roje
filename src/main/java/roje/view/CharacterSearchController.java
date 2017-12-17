package roje.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import roje.Main;
import roje.api.MarvelAPI;
import roje.model.Comics;

public class CharacterSearchController {

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
	private ListView<String> characterListView;

	private ObservableList<String> charactersFound;
	private Map<String, Integer> characters;
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
	}

	// todo: Fix when the list is clicked but no element is selected
	@FXML
	private void handleListClick(MouseEvent event) throws Exception {
		if (event.getClickCount() == 2) {
			String item = characterListView.getSelectionModel().getSelectedItem();
			Integer id = characters.get(item);
			Main.instance.showCharacterCard(id);
		}
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
		if(charactersFound != null) {
			charactersFound.clear();
		}
		
		Task<Void> downloadCharacters = new Task<Void>() {
			@Override
			public Void call() throws Exception {
				characters = MarvelAPI.searchCharactersByNamePrefix(toSearch);
				List<String> names = new ArrayList<String>(characters.keySet());
				charactersFound = FXCollections.observableList(names);
				characterListView.setItems(charactersFound);
				Platform.runLater(() -> {
					if (charactersFound.size() == 0) {
						statusLabel.setText("No results found");
					} else if (charactersFound.size() == 1) {
						statusLabel.setText("1 result found");
					} else {
						statusLabel.setText(charactersFound.size() + " results found");
					}
					loadingTimeline.stop();
					loadingIcon.setVisible(false);
					searchButton.setDisable(false);
				});
				return null;
			}
		};
		new Thread(downloadCharacters).start();
	}
}
