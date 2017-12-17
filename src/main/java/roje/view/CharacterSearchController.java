package roje.view;

import java.util.List;

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
import roje.api.MarvelAPI;
import roje.model.Character;

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
	private TableView<Character> characterTableView;

	@FXML
	private TableColumn<Character, String> nameColumn;

	private ObservableList<Character> charactersFound;
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
		charactersFound = FXCollections.observableArrayList();
		characterTableView.setItems(charactersFound);
		nameColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getName()));
	}

	@FXML
	private void handleListClick(MouseEvent event) throws Exception {
		if (event.getClickCount() == 2) {
			Character selectedCharacter = characterTableView.getSelectionModel().getSelectedItem();
			if (selectedCharacter != null) {
				Main.instance.showCharacterCard(selectedCharacter);
			}
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
		if (charactersFound != null) {
			charactersFound.clear();
		}

		Task<Void> downloadCharacters = new Task<Void>() {
			@Override
			public Void call() throws Exception {
				List<Character> characters = MarvelAPI.searchCharactersByNamePrefix(toSearch);
				charactersFound.clear();
				charactersFound.addAll(characters);
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
