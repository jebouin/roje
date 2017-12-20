package roje.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import roje.api.MarvelAPI;

public class Character {
	private int id;
	private String name;
	private String description;
	private String wikiUrl;
	private Thumbnail thumbnail;
	private List<Comics> comics = new ArrayList<Comics>();

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
		JsonArray urls = json.get("urls").getAsJsonArray();
		for (Iterator<JsonElement> it = urls.iterator(); it.hasNext();) {
			JsonObject el = it.next().getAsJsonObject();
			String type = el.get("type").getAsString();
			if (type.equals("wiki")) {
				this.wikiUrl = el.get("url").getAsString();
			}
		}
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

	public void fetchComics() throws Exception {
		// don't fetch comics twice
		if (comics.size() > 0) {
			return;
		}
		this.comics = MarvelAPI.getComicsByCharacterId(id);
	}

	public List<Comics> getComics() {
		return comics;
	}

	public String getWikiUrl() {
		return wikiUrl;
	}

	public void setWikiUrl(String wikiUrl) {
		this.wikiUrl = wikiUrl;
	}
}
