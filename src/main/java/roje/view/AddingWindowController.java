
package roje.view;

import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
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
	private void handlePressedokButton() {
		location = Location.getText();
		ComicsDAO.setdateandlocation(location, Date.toString(), comic.getId());
	}

	public void setComic(Comics comic) {
		this.comic = comic;
	}
}