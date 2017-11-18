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
	
	void setCharacter(Character character) throws Exception {
		this.character = character;
		character.fetchComics();
		nameLabel.setText(character.getName());
		descriptionLabel.setText(character.getDescription());
		imageView.setImage(character.getThumbnail().downloadImage("portrait_xlarge"));
		for(Comics c : character.getComics()) {
			ImageView imageView = new ImageView();
			imageView.setImage(c.getThumbnail().downloadImage("portrait_xlarge"));
			comicsGrid.getChildren().add(imageView);
		}
	}
}
