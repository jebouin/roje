package roje.view;

import java.io.IOException;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import roje.Main;
import roje.model.Character;
import roje.model.Comics;
import roje.model.ComicsDAO;

public class ComicCardController {
	@FXML
	private Label nameLabel;

	@FXML
	private Label descriptionLabel;

	@FXML
	private ImageView imageView;

	@FXML
	private TilePane charactersGrid;

	@FXML
	private Label pageCount;

	@FXML
	private Slider mark;

	@FXML
	private Label formatLabel;

	@FXML
	private Pane pricesPane;

	@FXML
	private Label pricesLabel;

	@FXML
	private Pane digitalPricePane;

	@FXML
	private Pane printPricePane;

	@FXML
	private Label digitalAmoutLabel;

	@FXML
	private Label printAmountLabel;

	@FXML
	private Label labelCharacters;
	@FXML
	private Button addLibraryButton;
	@FXML
	private Button addMarkButton;

	private Comics comic;
	@FXML
	private TextArea comment;

	@FXML
	private Button ButtonSave;

	@FXML
	private Label CommentLabel;

	/**
	 * Initializes the controller class. This method is automatically called after
	 * the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {

	}

	@FXML
	private void handleAddButtonPressed() throws IOException {
		if (ComicsDAO.findUserComic(comic.getId())) {
			ComicsDAO.deleteUserComic(comic.getId());
			addLibraryButton.setText("Add to library");
			comment.setText("");
			comment.setVisible(false);
			ButtonSave.setVisible(false);
			CommentLabel.setVisible(false);
			mark.setVisible(false);

			addMarkButton.setVisible(false);
		} else {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/AddingWindow.fxml"));
			AnchorPane pane = (AnchorPane) loader.load();
			loader.<AddingWindowController>getController().setComic(comic);
			Stage stage = new Stage();
			stage.setScene(new Scene(pane, 460, 200));
			stage.show();
			// TODO: fix bug!
			addLibraryButton.setText("Delete from library");
			ButtonSave.setVisible(true);
			comment.setVisible(true);
			mark.setVisible(true);
			addMarkButton.setVisible(true);
			CommentLabel.setVisible(true);
		}
	}

	@FXML
	public void handleAddMarkButtonPressed() throws IOException {
		System.out.println(String.valueOf((int) mark.getValue()));
		ComicsDAO.setMark(comic.getId(), (int) mark.getValue());
	}

	public void setMark(int m) {
		this.mark.setValue(m);
	}

	public void setComment(String text) {
		this.comment.setText(text);
	}

	@FXML
	public void handleSaveButtonPressed() throws IOException {
		System.out.println(String.valueOf(comment.getText()));
		ComicsDAO.setComment(comic.getId(), comment.getText());
	}

	public void setComic(Comics comic) throws Exception {
		this.comic = comic;
		comic.fetchCharacters();
		comment.setText(ComicsDAO.returnComment(comic.getId(), comment));
		nameLabel.setText(comic.getTitle());
		formatLabel.setText(comic.getFormat());
		pageCount.setText(Integer.toString(comic.getPageCount()));
		imageView.setImage(comic.getThumbnail().downloadImage("portrait_xlarge"));
		if (!ComicsDAO.findUserComic(comic.getId())) {
			mark.setVisible(false);
			addMarkButton.setVisible(false);
		}
		Task<Void> downloadImagesTask = new Task<Void>() {
			@Override
			public Void call() throws Exception {

				if (comic.getCharacters().size() == 0) {
					labelCharacters.setText("");
				} else {
					if (comic.getCharacters().size() == 1) {
						labelCharacters.setText("Character");
					} else {
						labelCharacters.setText("Characters");
					}
				}
				for (Character c : comic.getCharacters()) {
					FlowPane pane = new FlowPane(Orientation.VERTICAL);
					pane.setCursor(Cursor.HAND);
					Tooltip.install(pane, new Tooltip(c.getName()));
					ImageView imageView = new ImageView();
					Image image = c.getThumbnail().downloadImage("portrait_xlarge");
					imageView.setImage(image);
					imageView.setFitHeight(image.getHeight());
					pane.getChildren().add(imageView);

					Label title = new Label();
					title.setText(c.getName());
					title.setWrapText(true);
					title.setMaxWidth(image.getWidth());
					title.setTextAlignment(TextAlignment.CENTER);
					pane.getChildren().add(title);
					pane.setPrefHeight(image.getHeight() + 100);
					pane.setOnMouseClicked((MouseEvent e) -> {
						try {
							Main.instance.showCharacterCard(c);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					});
					Platform.runLater(() -> {
						charactersGrid.getChildren().add(pane);
					});
				}
				return null;
			}
		};
		new Thread(downloadImagesTask).start();
		if (ComicsDAO.findUserComic(comic.getId())) {
			addLibraryButton.setText("Delete from library");
		} else {
			addLibraryButton.setText("Add to library");
		}
		Float digitalPrice = comic.getDigitalPrice();
		Float printPrice = comic.getPrintPrice();
		if (digitalPrice == null && printPrice == null) {
			pricesPane.setVisible(false);
		} else {
			digitalPricePane.setVisible(false);
			printPricePane.setVisible(false);
			if (digitalPrice != null) {
				digitalPricePane.setVisible(true);
				digitalAmoutLabel.setText("$" + digitalPrice.toString());
			}
			if (printPrice != null) {
				printPricePane.setVisible(true);
				if (digitalPrice == null) {
					printPricePane.setLayoutY(0);
				}
				printAmountLabel.setText("$" + printPrice.toString());
			}
		}
		if (digitalPrice == null || printPrice == null) {
			pricesLabel.setText("Price :");
		}

	}
}
