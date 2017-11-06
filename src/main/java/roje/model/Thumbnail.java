package roje.model;

public class Thumbnail {
	private String path;
	private String extension;

	public Thumbnail(final String path, final String extension) {
		this.path = path;
		this.extension = extension;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}
}
