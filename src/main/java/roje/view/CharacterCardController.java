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

import roje.model.Character;

public class CharacterCardController {
	@FXML
	private Label titleLabel;
	
	@FXML
	private Label descriptionLabel;
	
	@FXML
	private ImageView imageView;
	
	private Character character;
	
	/**
	 * Initializes the controller class. This method is automatically called after
	 * the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		
	}
	
	void setCharacter(Character character) throws MalformedURLException, IOException {
		this.character = character;
		titleLabel.setText(character.getName());
		descriptionLabel.setText(character.getDescription());
		imageView.setImage(character.getThumbnail().downloadImage("portrait_xlarge"));
	}
}
