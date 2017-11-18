package roje.model;

import com.google.gson.JsonObject;

public class Character {
	private int id;
	private String name;
	private String description;
	private Thumbnail thumbnail;

	public Character(final int id, final String name, final String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}
	
	public Character(final JsonObject json) {
		this.id = json.get("id").getAsInt();
		this.name = json.get("name").getAsString();
		this.description = json.get("description").getAsString();
		this.thumbnail = new Thumbnail(json.get("thumbnail").getAsJsonObject());
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Thumbnail getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(Thumbnail thumbnail) {
		this.thumbnail = thumbnail;
	}
	
	
}
