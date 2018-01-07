package roje.api;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import roje.model.Comics;
import roje.model.ComicsDAO;
import roje.model.DB;

public class Scraper {
	public static void main(String[] args) throws Exception {
		DB.createDB();
		ComicsDAO.init();
		final Integer limit = 100;
		Integer offset = 0;
		Integer comicsCount = null;
		Integer calls = 0;
		while (comicsCount == null || offset < comicsCount) {
			Integer percentage = comicsCount == null ? 0 : offset * 100 / comicsCount;
			System.out.println("scraping " + percentage.toString() + "%");
			Map<String, String> parameters = new HashMap<String, String>();
			parameters.put("limit", limit.toString());
			parameters.put("offset", offset.toString());
			JsonObject data = MarvelAPI.requestJSONFromPath("comics", parameters).get("data").getAsJsonObject();
			if (comicsCount == null) {
				comicsCount = data.get("total").getAsInt();
			}
			offset += limit;
			JsonArray results = data.get("results").getAsJsonArray();
			for (Iterator<JsonElement> it = results.iterator(); it.hasNext();) {
				JsonObject el = it.next().getAsJsonObject();
				Comics comic = new Comics(el);
				ComicsDAO.addComic(comic);
			}
		}
		System.out.println("scraped " + comicsCount.toString() + " comics in " + calls.toString() + " calls");
	}
}
