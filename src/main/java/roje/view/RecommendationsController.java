package roje.view;

import java.sql.Connection;
import java.util.List;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import roje.Main;
import roje.model.Comics;
import roje.model.ComicsDAO;

public class RecommendationsController {

	private static Connection connection;

	@FXML
	private TableView<Comics> authorRecommendationsTable;

	@FXML
	private TableView<Comics> serieRecommendationsTable;

	@FXML
	private TableColumn<Comics, String> authorRecommendationsTitleColumn;

	@FXML
	private TableColumn<Comics, String> serieRecommendationsTitleColumn;

	@FXML
	private ObservableList<Comics> authorRecommendationsData;

	@FXML
	private ObservableList<Comics> serieRecommendationsData;

	public void refresh() {
		authorRecommendationsData.clear();
		serieRecommendationsData.clear();
		List<Comics> comicsList = ComicsDAO.getRecommendedComicsByCreator();
		for (Comics c : comicsList) {
			authorRecommendationsData.add(c);
		}
		List<Comics> serieList = ComicsDAO.returnseries();
		for (Comics c : serieList) {
			serieRecommendationsData.add(c);
		}
	}

	/**
	 * Initializes the controller class. This method is automatically called after
	 * the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		Main.instance.setRecommendationsController(this);
		authorRecommendationsData = FXCollections.observableArrayList();
		serieRecommendationsData = FXCollections.observableArrayList();
		authorRecommendationsTitleColumn
				.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getTitle()));
		serieRecommendationsTitleColumn
				.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getTitle()));
		authorRecommendationsTable.setItems(authorRecommendationsData);
		serieRecommendationsTable.setItems(serieRecommendationsData);
		refresh();
	}

	@FXML
	private void handleAuthorRecommendationsListClick(MouseEvent event) throws Exception {
		if (event.getClickCount() == 2) {
			Comics selectedComic = authorRecommendationsTable.getSelectionModel().getSelectedItem();
			if (selectedComic != null) {
				Main.instance.showComicCard(selectedComic);
			}
		}
	}

	@FXML
	private void handleSerieRecommendationsListClick(MouseEvent event) throws Exception {
		if (event.getClickCount() == 2) {
			Comics selectedComic = serieRecommendationsTable.getSelectionModel().getSelectedItem();
			if (selectedComic != null) {
				Main.instance.showComicCard(selectedComic);
			}
		}
	}
}