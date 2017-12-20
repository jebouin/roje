
package roje.view;

import java.io.IOException;
import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import roje.Main;
import roje.model.Comics;
import roje.model.ComicsDAO;

public class AddingWindowController {
	@FXML
	private DatePicker Calendar;

	@FXML
	private TextField Location;

	@FXML
	private Button okButton;

	private LocalDate Date;

	private String location;

	private Comics comic;

	@FXML
	private void handlePressedCalendar() {
		Date = Calendar.getValue();
	}

	@FXML
	private void handlePressedokButton() throws IOException {
		location = Location.getText();
		ComicsDAO.create(comic);
		ComicsDAO.setdateandlocation(location, Date.toString(), comic.getId());

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("view/SuccessfulView.fxml"));
		AnchorPane pane = (AnchorPane) loader.load();
		Stage stage = new Stage();
		stage.setScene(new Scene(pane, 200, 100));
		stage.show();

	}

	public void setComic(Comics comic) {
		this.comic = comic;
	}
}