
package roje.view;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.joda.time.DateTime;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
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

	private LocalDate date;

	private String location;

	private Comics comic;

	@FXML
	private Label ValidateLabel;

	@FXML
	private void handlePressedCalendar() {
		date = Calendar.getValue();
	}

	@FXML
	private void handlePressedokButton() throws IOException {
		location = Location.getText();

		ComicsDAO.addUserComic(comic.getId(),
				new DateTime(Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())), location);
		ValidateLabel.setText("Add complete !");
	}

	public void setComic(Comics comic) {
		this.comic = comic;
	}
}