package roje.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import roje.api.MarvelAPI;

public class Comics {
	private int id;
	private String title;
	private String description;
	private int pageCount;
	private Thumbnail thumbnail;
	private String format;
	private DateTime onSaleDate;
	private Float printPrice;
	private Float digitalPrice;
	private int mark;

	private List<Character> characters = new ArrayList<Character>();

	public Comics(final int id, final String title, final String description, final int pageCount, final String format,
			final DateTime onSaleData, final int mark) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.pageCount = pageCount;
		this.format = format;
		this.onSaleDate = onSaleDate;
		this.mark = mark;
	}

	public Comics(final JsonObject json) {
		this.id = json.get("id").getAsInt();
		this.title = json.get("title").getAsString();
		if (!json.get("description").isJsonNull()) {
			this.description = json.get("description").getAsString();
		}
		this.pageCount = json.get("pageCount").getAsInt();
		this.format = json.get("format").getAsString();
		JsonArray dates = json.get("dates").getAsJsonArray();
		for (Iterator<JsonElement> it = dates.iterator(); it.hasNext();) {
			JsonObject el = it.next().getAsJsonObject();
			String type = el.get("type").getAsString();
			if (type.equals("onsaleDate")) {
				String dateString = el.get("date").getAsString();
				onSaleDate = DateTime.parse(dateString);
			}
		}
		JsonArray prices = json.get("prices").getAsJsonArray();
		for (Iterator<JsonElement> it = prices.iterator(); it.hasNext();) {
			JsonObject el = it.next().getAsJsonObject();
			String type = el.get("type").getAsString();
			if (type.equals("printPrice")) {
				this.printPrice = el.get("price").getAsFloat();
			} else if (type.equals("digitalPrice")) {
				this.digitalPrice = el.get("price").getAsFloat();
			}
		}
		this.thumbnail = new Thumbnail(json.get("thumbnail").getAsJsonObject());
		this.mark = 0;
	}

	public int getId() {
		return id;
	}

	public String getFormat() {
		return format;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMark() {
		return mark;
	}

	public void setMark(int mark) {
		this.mark = mark;
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

	public Thumbnail getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(Thumbnail thumbnail) {
		this.thumbnail = thumbnail;
	}

	public DateTime getOnSaleDate() {
		return onSaleDate;
	}

	public String getOnSaleDateAsString() {
		if (onSaleDate != null) {
			return onSaleDate.toString(DateTimeFormat.forPattern("dd MMMM yyyy"));
		}
		return "Unknown";
	}

	public void setOnSaleDate(DateTime onSaleDate) {
		this.onSaleDate = onSaleDate;
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

	public void setPageCount(int nbPages) {
		this.pageCount = nbPages;
	}

	public Float getPrintPrice() {
		return printPrice;
	}

	public void setPrintPrice(Float printPrice) {
		this.printPrice = printPrice;
	}

	public Float getDigitalPrice() {
		return digitalPrice;
	}

	public void setDigitalPrice(Float digitalPrice) {
		this.digitalPrice = digitalPrice;
	}
}