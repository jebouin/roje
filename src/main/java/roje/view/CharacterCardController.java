package roje.view;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
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

	private Character character;

	/**
	 * Initializes the controller class. This method is automatically called after
	 * the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {

	}

	public void setCharacter(Character character) throws Exception {
		this.character = character;
		character.fetchComics();
		nameLabel.setText(character.getName());
		descriptionLabel.setText(character.getDescription());
		imageView.setImage(character.getThumbnail().downloadImage("portrait_xlarge"));

		Task<Void> downloadImagesTask = new Task<Void>() {
			@Override
			public Void call() throws Exception {
				for (Comics c : character.getComics()) {
					ImageView imageView = new ImageView();
					imageView.setImage(c.getThumbnail().downloadImage("portrait_xlarge"));
					imageView.setCursor(Cursor.HAND);
					Tooltip.install(imageView, new Tooltip(c.getTitle()));
					imageView.setOnMouseClicked((MouseEvent e) -> {
						try {
							Main.instance.showComicCard(c.getId());
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					});
					Platform.runLater(() -> {
						comicsGrid.getChildren().add(imageView);
					});
				}
				return null;
			}
		};
		new Thread(downloadImagesTask).start();
	}
}
