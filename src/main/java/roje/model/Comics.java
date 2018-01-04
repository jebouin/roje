package roje.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import roje.api.MarvelAPI;

public class Comics {
	// comic properties
	private int id;
	private String title;
	private String description;
	private int pageCount;
	private Thumbnail thumbnail;
	private String format;
	private DateTime onSaleDate;
	private Float printPrice;
	private Float digitalPrice;

	// user defined properties
	private Integer mark;
	private DateTime purchaseDate;
	private String location;
	private String addprice;
	private String comment;
	private ObservableList<String> bookmarks = FXCollections.observableArrayList();
	private List<Character> characters = new ArrayList<Character>();

	public Comics(int id, String title, String description, int pageCount, Thumbnail thumbnail, String format,
			DateTime onSaleDate, Float printPrice, Float digitalPrice, Integer mark, DateTime purchaseDate,
			String location, String comment, String addprice, ObservableList<String> bookmarks) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
		this.pageCount = pageCount;
		this.thumbnail = thumbnail;
		this.format = format;
		this.onSaleDate = onSaleDate;
		this.printPrice = printPrice;
		this.digitalPrice = digitalPrice;
		this.mark = mark;
		this.purchaseDate = purchaseDate;
		this.location = location;
		this.comment = comment;
		this.addprice = addprice;
		this.bookmarks = bookmarks;
	}

	public Comics(final JsonObject json) {
		this.id = json.get("id").getAsInt();
		this.title = json.get("title").getAsString();
		if (!json.get("description").isJsonNull()) {
			this.description = json.get("description").getAsString();
		}
		this.pageCount = json.get("pageCount").getAsInt();
		this.thumbnail = new Thumbnail(json.get("thumbnail").getAsJsonObject());
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
	}

	public String getOnSaleDateAsString() {
		if (onSaleDate != null) {
			return onSaleDate.toString(DateTimeFormat.forPattern("dd MMMM yyyy"));
		}
		return "Unknown";
	}

	public void fetchCharacters() throws Exception {
		// don't fetch characters twice
		if (characters.size() > 0) {
			return;
		}
		this.characters = MarvelAPI.getCharactersByComicId(id);
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

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public DateTime getOnSaleDate() {
		return onSaleDate;
	}

	public void setOnSaleDate(DateTime onSaleDate) {
		this.onSaleDate = onSaleDate;
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

	public Integer getMark() {
		return mark;
	}

	public void setMark(int mark) {
		this.mark = mark;
	}

	public DateTime getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(DateTime purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public List<Character> getCharacters() {
		return characters;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getAddprice() {
		return addprice;
	}

	public void setAddprice(String addprice) {
		this.addprice = addprice;
	}

	public ObservableList<String> getBookmarks() {
		return bookmarks;
	}

	public void setBookmarks(ObservableList<String> bookmarks) {
		this.bookmarks = bookmarks;
	}

}