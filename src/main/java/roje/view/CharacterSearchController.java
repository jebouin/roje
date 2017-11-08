package roje.view;

import org.apache.derby.tools.sysinfo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import roje.api.MarvelAPI;

public class CharacterSearchController {

	@FXML
	private TextField searchTextField;

	@FXML
	private Label statusLabel;

	@FXML
	private ListView<String> characterListView;

	private ObservableList<String> charactersFound;
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
		if(event.getCode() == KeyCode.ENTER) {
			search();
		}
	}
	
	private void search() throws Exception {
		String toSearch = searchTextField.getText(); 
		if(toSearch == lastSearch) {
			return;
		}
		lastSearch = toSearch;
		statusLabel.setText("Loading...");
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
