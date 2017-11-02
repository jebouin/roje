package roje.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import roje.api.MarvelAPI;

public class CharacterSearchController {

	@FXML
	private TextField searchTextField;

	@FXML
	private Label statusLabel;

	@FXML
	private ListView<String> characterListView;

	private ObservableList<String> charactersFound;

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
	private void handleSearch() throws Exception {
		statusLabel.setText("Loading...");
		String toSearch = searchTextField.getText();
		charactersFound = FXCollections.observableList(MarvelAPI.searchCharactersByNamePrefix(toSearch));
		characterListView.setItems(charactersFound);
		if (charactersFound.size() == 0) {
			statusLabel.setText("No results found");
		} else if (charactersFound.size() == 1) {
			statusLabel.setText("1 result found");
		} else {
			statusLabel.setText(charactersFound.size() + " results found");
		}
	}
}
