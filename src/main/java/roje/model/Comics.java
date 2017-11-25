package roje.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

import roje.api.MarvelAPI;

public class Comics {
	private int id;
	private String title;
	private String description;
	private int pageCount;
	private Thumbnail thumbnail;
	private List<Character> characters = new ArrayList<Character>();

	public Comics(final int id, final String title, final String description, final int pageCount) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.pageCount = pageCount;
	}

	public Comics(final JsonObject json) {
		this.id = json.get("id").getAsInt();
		this.title = json.get("title").getAsString();
		if (!json.get("description").isJsonNull()) {
			this.description = json.get("description").getAsString();
		}

		this.thumbnail = new Thumbnail(json.get("thumbnail").getAsJsonObject());
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public Thumbnail getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(Thumbnail thumbnail) {
		this.thumbnail = thumbnail;
	}

	public void fetchCharacters() throws Exception {
		// don't fetch comics twice
		if (characters.size() > 0) {
			return;
		}
		this.characters = MarvelAPI.getCharactersByComicId(id);
	}

	public List<Character> getCharacters() {
		return characters;
	}
}
