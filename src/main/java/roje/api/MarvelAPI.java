package roje.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
			urlString += (hasOne ? "&" : "?") + parameter + "=" + value;
			hasOne = true;
		}
		return requestJSONFromUrl(urlString);
	}

	public static List<String> searchCharactersByNamePrefix(final String prefix) throws Exception {
		Map<String, String> parameters = new HashMap<String, String>() {
			{
				put("nameStartsWith", prefix);
			}
		};
		JsonObject jsonObject = requestJSONFromPath("characters", parameters);

		// TODO return more information (short description, image)
		List<String> names = new ArrayList<String>();
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

	public static List<String> searchComicsByNamePrefix(final String titleSearch) throws Exception {
		Map<String, String> parameters = new HashMap<String, String>() {
			{
				put("titleStartsWith", titleSearch);
			}
		};
		JsonObject jsonObject = requestJSONFromPath("comics", parameters);

		// TODO return more information (short description, image)
		List<String> titles = new ArrayList<String>();
		JsonArray data = jsonObject.get("data").getAsJsonObject().get("results").getAsJsonArray();
		for (Iterator<JsonElement> it = data.iterator(); it.hasNext();) {
			JsonObject el = it.next().getAsJsonObject();
			String title = el.get("title").toString();
			// get rid of the quotes
			title = title.substring(1, title.length() - 1);
			titles.add(title);
		}
		return titles;
	}

}
