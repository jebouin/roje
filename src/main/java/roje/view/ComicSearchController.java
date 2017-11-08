package roje.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import roje.api.MarvelAPI;

public class ComicSearchController {

	@FXML
	private TextField searchTextField;

	@FXML
	private Label statusLabel;

	@FXML
	private ListView<String> comicsListView;

	private ObservableList<String> comicsFound;
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
		comicsFound = FXCollections.observableList(MarvelAPI.searchComicsByNamePrefix(toSearch));
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
