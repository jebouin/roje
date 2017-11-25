package roje.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import roje.model.Character;
import roje.model.Comics;

public class MarvelAPI {
	// TODO put the credentials in a separate config file
	private final static String publicKey = "4271aafb3ca71f40782943e5bfd585d1";
	private final static String privateKey = "f586f6c5d3140f9ef8c4ef41dd51cc577323c091";

	public static JsonObject requestJSONFromUrl(String urlString) throws Exception {
		StringBuilder result = new StringBuilder();
		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		rd.close();
		return new JsonParser().parse(result.toString()).getAsJsonObject();
	}

	/**
	 * @param path
	 *            The path added to the base API url as such :
	 *            https://gateway.marvel.com:443/v1/public/{path}
	 * @param parameters
	 *            The GET parameters sent to the API. Don't include neither the
	 *            timestamp nor the hash, these parameters are added by this
	 *            function.
	 * @return The JSON object sent back by the API.
	 * @throws Exception
	 */
	public static JsonObject requestJSONFromPath(String path, Map<String, String> parameters) throws Exception {
		String timestamp = new Long(System.currentTimeMillis()).toString();
		String hash = DigestUtils.md5Hex((timestamp + privateKey + publicKey).getBytes());
		parameters.put("apikey", publicKey);
		parameters.put("ts", timestamp);
		parameters.put("hash", hash);
		String urlString = "https://gateway.marvel.com:443/v1/public/" + path;
		boolean hasOne = false;
		for (String parameter : parameters.keySet()) {
			String value = parameters.get(parameter);
			urlString += (hasOne ? "&" : "?") + parameter + "=" + URLEncoder.encode(value, "UTF-8");
			hasOne = true;
		}
		return requestJSONFromUrl(urlString);
	}

	/**
	 * 
	 * @param prefix
	 * @return A map that maps character names that start by prefix to their Marvel
	 *         ID
	 * @throws Exception
	 */
	public static Map<String, Integer> searchCharactersByNamePrefix(final String prefix) throws Exception {
		Map<String, String> parameters = new HashMap<String, String>() {
			{
				put("nameStartsWith", prefix);
			}
		};
		JsonObject jsonObject = requestJSONFromPath("characters", parameters);

		// TODO return more information (short description, image)
		Map<String, Integer> characters = new HashMap<String, Integer>();
		JsonArray data = jsonObject.get("data").getAsJsonObject().get("results").getAsJsonArray();
		for (Iterator<JsonElement> it = data.iterator(); it.hasNext();) {
			JsonObject el = it.next().getAsJsonObject();
			String name = el.get("name").toString();
			Integer id = Integer.parseInt(el.get("id").toString());
			// get rid of the quotes
			name = name.substring(1, name.length() - 1);
			characters.put(name, id);
		}
		return characters;
	}

	// todo: test if character exists
	public static Character getCharacterById(final Integer id) throws Exception {
		JsonObject jsonObject = requestJSONFromPath("characters/" + id.toString(), new HashMap<String, String>());
		JsonArray data = jsonObject.get("data").getAsJsonObject().get("results").getAsJsonArray();
		if (data.size() == 1) {
			JsonObject el = data.get(0).getAsJsonObject();
			return new Character(el);
		}
		return null;
	}

	public static List<Comics> getComicsByCharacterId(final Integer id) throws Exception {
		JsonObject jsonObject = requestJSONFromPath("characters/" + id.toString() + "/comics",
				new HashMap<String, String>());
		JsonArray data = jsonObject.get("data").getAsJsonObject().get("results").getAsJsonArray();
		List<Comics> comics = new ArrayList<Comics>();
		for (Iterator<JsonElement> it = data.iterator(); it.hasNext();) {
			JsonObject el = it.next().getAsJsonObject();
			comics.add(new Comics(el));
		}
		return comics;
	}

	public static Map<String, Integer> searchComicsByNamePrefix(final String titleSearch) throws Exception {
		Map<String, String> parameters = new HashMap<String, String>() {
			{
				put("titleStartsWith", titleSearch);
			}
		};
		JsonObject jsonObject = requestJSONFromPath("comics", parameters);

		// TODO return more information (short description, image)
		Map<String, Integer> comics = new HashMap<String, Integer>();
		JsonArray data = jsonObject.get("data").getAsJsonObject().get("results").getAsJsonArray();
		for (Iterator<JsonElement> it = data.iterator(); it.hasNext();) {
			JsonObject el = it.next().getAsJsonObject();
			String title = el.get("title").toString();
			Integer id = Integer.parseInt(el.get("id").toString());
			// get rid of the quotes
			title = title.substring(1, title.length() - 1);
			comics.put(title, id);
		}
		return comics;
	}

	public static Comics getComicById(final Integer id) throws Exception {
		JsonObject jsonObject = requestJSONFromPath("comics/" + id.toString(), new HashMap<String, String>());
		JsonArray data = jsonObject.get("data").getAsJsonObject().get("results").getAsJsonArray();
		if (data.size() == 1) {
			JsonObject el = data.get(0).getAsJsonObject();
			return new Comics(el);
		}
		return null;
	}

	public static List<Character> getCharactersByComicId(final Integer id) throws Exception {
		JsonObject jsonObject = requestJSONFromPath("comics/" + id.toString() + "/characters",
				new HashMap<String, String>());
		JsonArray data = jsonObject.get("data").getAsJsonObject().get("results").getAsJsonArray();
		List<Character> characters = new ArrayList<Character>();
		for (Iterator<JsonElement> it = data.iterator(); it.hasNext();) {
			JsonObject el = it.next().getAsJsonObject();
			characters.add(new Character(el));
		}
		return characters;
	}
}
