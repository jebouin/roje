package roje.view;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import roje.api.MarvelAPI;
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
	
	void setComic(Comics comic) throws Exception {
		this.comic = comic;
		comic.fetchCharacters();
		nameLabel.setText(comic.getTitle());
		descriptionLabel.setText(comic.getDescription());
		imageView.setImage(comic.getThumbnail().downloadImage("portrait_xlarge"));
		for(Character c : comic.getCharacters()) {
			ImageView imageView = new ImageView();
			imageView.setImage(c.getThumbnail().downloadImage("portrait_xlarge"));
			charactersGrid.getChildren().add(imageView);
		}
	}
}
