package roje.model;

import com.google.gson.JsonObject;

public class Creator {
	private String name;
	private String role;

	public Creator(String name, String role) {
		super();
		this.name = name;
		this.role = role;
	}

	public Creator(final JsonObject json) {
		this.name = json.get("name").getAsString();
		this.role = json.get("role").getAsString();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}
