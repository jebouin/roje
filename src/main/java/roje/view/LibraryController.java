package roje.view;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class LibraryController {

	@FXML
	private TextField searchTextField;

	@FXML
	private ImageView imageProfileView;

	@FXML
	private ListView<String> comicsListView;

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		Image img = new Image("spider_ham.jpeg");
		ImageView imgView = new ImageView(img);
		imageProfileView = imgView;
	}
}