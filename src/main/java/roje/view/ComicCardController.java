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
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
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
	private Label labelCharacters;

	private Comics comic;
	@FXML
	private Button addLibraryButton;

	/**
	 * Initializes the controller class. This method is automatically called after
	 * the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
	}

	@FXML
	private void handleAddButtonPressed() throws IOException {

		if (ComicsDAO.find(comic.getId()).getTitle() != null) {
			ComicsDAO.delete(this.comic);
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/SuccessfulView.fxml"));
			AnchorPane pane = (AnchorPane) loader.load();
			Stage stage = new Stage();
			stage.setScene(new Scene(pane, 300, 95));
			stage.show();
			addLibraryButton.setText("Add to library");
		} else {
			ComicsDAO.create(this.comic);
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/SuccessfulView.fxml"));
			AnchorPane pane = (AnchorPane) loader.load();
			Stage stage = new Stage();
			stage.setScene(new Scene(pane, 300, 95));
			stage.show();
			addLibraryButton.setText("Delete to library");
		}
	}

	@FXML
	public void handleAddMarkButtonPressed() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		System.out.println(String.valueOf((int) mark.getValue()));
		loader.setLocation(Main.class.getResource("view/SuccessfulView.fxml"));
		ComicsDAO.addMark(this.comic, (int) mark.getValue());
		AnchorPane pane = (AnchorPane) loader.load();
		Stage stage = new Stage();
		stage.setScene(new Scene(pane, 300, 95));
		stage.show();
	}

	public void setMark(int m) {
		this.mark.setValue(m);
	}

	public void setComic(Comics comic) throws Exception {
		this.comic = comic;
		comic.fetchCharacters();
		nameLabel.setText(comic.getTitle());
		formatLabel.setText(comic.getFormat());
		pageCount.setText(Integer.toString(comic.getPageCount()));
		imageView.setImage(comic.getThumbnail().downloadImage("portrait_xlarge"));
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
		if (ComicsDAO.find(comic.getId()).getTitle() != null) {
			addLibraryButton.setText("Delete to library");
		} else {
			addLibraryButton.setText("Add to Library");
		}
	}
}
