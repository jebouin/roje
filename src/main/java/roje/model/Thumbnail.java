package roje.model;

import com.google.gson.JsonObject;

import javafx.scene.image.Image;

public class Thumbnail {
	private String partialPath;
	private String extension;

	public Thumbnail(final String partialPath, final String extension) {
		this.partialPath = partialPath;
		this.extension = extension;
	}
	
	public Thumbnail(final JsonObject json) {
		this.extension = json.get("extension").getAsString();
		this.partialPath = json.get("path").getAsString();
	}

	public String getPartialPath() {
		return partialPath;
	}

	public void setPath(String path) {
		this.partialPath = path;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}
	
	/**
	 * See https://developer.marvel.com/documentation/images
	 * @param variantName
	 * @return The thumbnail downloaded with the specified image variant, as a JavaFX image.
	 */
	public Image downloadImage(String variantName) {
		String path = partialPath + "/" + variantName + "." + extension;
		return new Image(path);
	}
}
