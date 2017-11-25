package roje.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import roje.Main;
import roje.api.MarvelAPI;

public class CharacterSearchController {

	@FXML
	private TextField searchTextField;

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
		/*
		 * final ListView lv = new
		 * ListView(FXCollections.observableList(Arrays.asList("one", "2", "3")));
		 * lv.setOnMouseClicked(new EventHandler<MouseEvent>() {
		 * 
		 * @Override public void handle(MouseEvent event) {
		 * System.out.println("clicked on " + lv.getSelectionModel().getSelectedItem());
		 * } });
		 */
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
		statusLabel.setText("Loading...");
		characters = MarvelAPI.searchCharactersByNamePrefix(toSearch);
		List<String> names = new ArrayList<String>(characters.keySet());
		charactersFound = FXCollections.observableList(names);
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
