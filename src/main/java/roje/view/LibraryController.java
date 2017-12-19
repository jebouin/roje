package roje.view;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import roje.Main;
import roje.model.Comics;
import roje.model.ComicsDAO;

public class LibraryController {

	@FXML
	private TextField searchTextField;

	@FXML
	private ImageView imageProfileView;

	@FXML
	private TableView<Comics> libraryView;

	@FXML
	private ObservableList<Comics> comicsData;

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void handleRefreshButtonPressed() {
		comicsData = FXCollections.observableArrayList();
		for (int i = 0; i < libraryView.getItems().size(); i++) {
			libraryView.getItems().clear();
		}
		List<Comics> comicsList = ComicsDAO.getComics();
		for (Comics c : comicsList) {
			comicsData.add(c);
		}

		libraryView.getItems().addAll(comicsData);

	}

	@FXML
	private void handleListClick(MouseEvent event) throws Exception {
		System.out.println(event.getClickCount());
		if (event.getClickCount() == 2) {
			Comics selectedComic = libraryView.getSelectionModel().getSelectedItem();
			if (selectedComic != null) {
				Main.instance.showComicCard(selectedComic);
			}
		}
	}
}