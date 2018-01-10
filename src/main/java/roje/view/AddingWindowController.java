
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

	private Date date;

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
		location = Location.getText();
		addprice = Price.getText();
		ComicsDAO.addUserComic(comic.getId(), (java.sql.Date) date, location, addprice);
		ValidateLabel.setText("Add complete !");
	}

	public void setComic(Comics comic) {
		this.comic = comic;
	}
}