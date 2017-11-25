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

public class ComicCardController {
	@FXML
	private Label nameLabel;

	@FXML
	private Label descriptionLabel;

	@FXML
	private ImageView imageView;

	@FXML
	private TilePane charactersGrid;

	private Comics comic;

	/**
	 * Initializes the controller class. This method is automatically called after
	 * the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {

	}

	public void setComic(Comics comic) throws Exception {
		this.comic = comic;
		comic.fetchCharacters();
		nameLabel.setText(comic.getTitle());
		descriptionLabel.setText(comic.getDescription());
		imageView.setImage(comic.getThumbnail().downloadImage("portrait_xlarge"));

		Task<Void> downloadImagesTask = new Task<Void>() {
			@Override
			public Void call() throws Exception {
				for (Character c : comic.getCharacters()) {
					ImageView imageView = new ImageView();
					imageView.setImage(c.getThumbnail().downloadImage("portrait_xlarge"));
					imageView.setCursor(Cursor.HAND);
					Tooltip.install(imageView, new Tooltip(c.getName()));
					imageView.setOnMouseClicked((MouseEvent e) -> {
						try {
							Main.instance.showCharacterCard(c.getId());
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					});
					Platform.runLater(() -> {
						charactersGrid.getChildren().add(imageView);
					});
				}
				return null;
			}
		};
		new Thread(downloadImagesTask).start();
	}
}
