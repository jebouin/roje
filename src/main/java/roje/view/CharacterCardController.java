package roje.view;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.TextAlignment;
import roje.Main;
import roje.model.Character;
import roje.model.Comics;

public class CharacterCardController {
	@FXML
	private Label nameLabel;

	@FXML
	private Label descriptionLabel;

	@FXML
	private ImageView imageView;

	@FXML
	private TilePane comicsGrid;
	@FXML
	private Character character;
	@FXML
	private Hyperlink wikiLink;
	@FXML
	private Label appearsIn;

	/**
	 * Initializes the controller class. This method is automatically called after
	 * the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		comicsGrid.setVgap(10);
	}

	public void setCharacter(Character character) throws Exception {
		this.character = character;
		character.fetchComics();
		nameLabel.setText(character.getName());
		Pattern p = Pattern.compile(".");
		Matcher m = p.matcher(character.getDescription());
		if (m.find()) {
			descriptionLabel.setText(character.getDescription());
		} else {
			descriptionLabel.setText("No description");
		}
		imageView.setImage(character.getThumbnail().downloadImage("portrait_xlarge"));

		Task<Void> downloadImagesTask = new Task<Void>() {
			@Override
			public Void call() throws Exception {
				List<Comics> comics = character.getComics();
				if (comics.size() == 0) {
					appearsIn.setText("");
				} else {
					appearsIn.setText("Appears in");
				}
				for (Comics c : comics) {
					FlowPane pane = new FlowPane(Orientation.VERTICAL);
					pane.setCursor(Cursor.HAND);
					Tooltip.install(pane, new Tooltip(c.getTitle()));
					ImageView imageView = new ImageView();
					Image image = c.getThumbnail().downloadImage("portrait_xlarge");
					imageView.setImage(image);
					imageView.setFitHeight(image.getHeight());
					pane.getChildren().add(imageView);

					Label title = new Label();
					title.setText(c.getTitle());
					title.setWrapText(true);
					title.setMaxWidth(image.getWidth());
					title.setTextAlignment(TextAlignment.CENTER);
					pane.getChildren().add(title);

					pane.setPrefHeight(image.getHeight() + 100);
					pane.setOnMouseClicked((MouseEvent e) -> {
						try {
							Main.instance.showComicCard(c);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					});
					Platform.runLater(() -> {
						comicsGrid.getChildren().add(pane);
					});
				}
				return null;
			}
		};
		new Thread(downloadImagesTask).start();
	}

	@FXML
	private void handleWikiLinkCliked() throws Exception {
		Main.instance.getHostServices().showDocument(character.getWikiUrl());
	}
}
