
package roje.view;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;

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

	private LocalDate date = LocalDate.parse("2500-01-01");

	private String addprice;

	private String location;

	private Comics comic;

	@FXML
	private Label ValidateLabel;

	@FXML
	private Label PriceLabel;

	@FXML
	private TextField Price;

	private LocalDate date1;

	@FXML
	private void handlePressedCalendar() {
		date1 = Calendar.getValue();
		date = java.sql.Date.valueOf(date1);
	}

	@FXML
	private void handlePressedokButton() throws IOException {
		LocalDate testDate = LocalDate.of(2500, 1, 1);
		location = Location.getText();
		addprice = Price.getText();
		DateTime datePurchase = new DateTime(Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
		if (!date.equals(testDate)) {
			ComicsDAO.addUserComic(comic.getId(), datePurchase, location, addprice);
			ValidateLabel.setText("Add complete !");
		} else {
			ValidateLabel.setText("Error : select a date");
		}

	}

	public void setComic(Comics comic) {
		this.comic = comic;
	}
}