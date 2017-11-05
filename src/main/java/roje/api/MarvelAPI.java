package roje.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MarvelAPI {
	// TODO put the credentials in a separate config file
	private final static String publicKey = "4271aafb3ca71f40782943e5bfd585d1";
	private final static String privateKey = "f586f6c5d3140f9ef8c4ef41dd51cc577323c091";

	public static List<String> searchCharactersByNamePrefix(String prefix) throws Exception {
		String timestamp = new Long(System.currentTimeMillis()).toString();
		String hash = DigestUtils.md5Hex((timestamp + privateKey + publicKey).getBytes());
		String path = "https://gateway.marvel.com:443/v1/public/characters?nameStartsWith=" + prefix + "&apikey="
				+ publicKey + "&ts=" + timestamp + "&hash=" + hash;
		StringBuilder result = new StringBuilder();
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		rd.close();

		// TODO return more information (short description, image)
		List<String> names = new ArrayList<String>();
		JsonObject jsonObject = new JsonParser().parse(result.toString()).getAsJsonObject();
		JsonArray data = jsonObject.get("data").getAsJsonObject().get("results").getAsJsonArray();
		for (Iterator<JsonElement> it = data.iterator(); it.hasNext();) {
			JsonObject el = it.next().getAsJsonObject();
			String name = el.get("name").toString();
			// get rid of the quotes
			name = name.substring(1, name.length() - 1);
			names.add(name);
		}
		return names;
	}

	public static List<String> searchComicsByNamePrefix(String titleSearch) throws Exception {
		String timestamp = new Long(System.currentTimeMillis()).toString();
		String hash = DigestUtils.md5Hex((timestamp + privateKey + publicKey).getBytes());
		String path = "https://gateway.marvel.com:443/v1/public/comics?titleStartsWith=" + titleSearch + "&apikey="
				+ publicKey + "&ts=" + timestamp + "&hash=" + hash;
		StringBuilder result = new StringBuilder();
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		rd.close();

		// TODO return more information (short description, image)
		List<String> titles = new ArrayList<String>();
		JsonObject jsonObject = new JsonParser().parse(result.toString()).getAsJsonObject();
		JsonArray data = jsonObject.get("data").getAsJsonObject().get("results").getAsJsonArray();
		for (Iterator<JsonElement> it = data.iterator(); it.hasNext();) {
			JsonObject el = it.next().getAsJsonObject();
			String title = el.get("title").toString();
			// get rid of the quotes
			title = title.substring(1, title.length() - 1);
			titles.add(title);
		}
		System.out.println(titles.size());
		return titles;
	}

}
