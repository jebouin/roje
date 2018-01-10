package roje.model;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DB {
	public static void createDB() {
		File f = new File(".\\DB\\library.db");
		if (!f.exists()) {
			try {
				Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
				Connection connect = DriverManager.getConnection("jdbc:derby:.\\DB\\library.db;create=true");
				Statement stComics = connect.createStatement();
				Statement stUserComics = connect.createStatement();
				Statement stUserBookmarks = connect.createStatement();
				Statement stCreators = connect.createStatement();
				Statement stComicsCreators = connect.createStatement();
				stComics.executeUpdate(
						"CREATE TABLE comics (id INT PRIMARY KEY, title VARCHAR(1000), description LONG VARCHAR, pageCount INT, thumbnailPartialPath VARCHAR(256), thumbnailExtension VARCHAR(16), format VARCHAR(64), onSaleDate TIMESTAMP, printPrice FLOAT, digitalPrice FLOAT, cacheDate TIMESTAMP)");
				stUserComics.executeUpdate(
						"CREATE TABLE userComics (id INT PRIMARY KEY REFERENCES COMICS(id), mark INT, comment VARCHAR(256), location VARCHAR(100), addprice VARCHAR(100), purchaseDate TIMESTAMP, bookmarks VARCHAR(256))");
				stUserBookmarks.executeUpdate("CREATE TABLE userBookmarks(id INT, bookmark VARCHAR(100))");
				stCreators.executeUpdate("CREATE TABLE creators (name VARCHAR(1000) PRIMARY KEY, role VARCHAR(1000))");
				stComicsCreators.executeUpdate("CREATE TABLE comicsCreators (comicId INT, creatorName VARCHAR(1000),"
						+ "FOREIGN KEY (comicId) REFERENCES comics(id),"
						+ "FOREIGN KEY (creatorName) REFERENCES creators(name),"
						+ "PRIMARY KEY(comicId, creatorName))");
				stComics.close();
				stUserComics.close();
				stUserBookmarks.close();
				stCreators.close();
				stComicsCreators.close();
				connect.close();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void deleteDB(File f) {
		f.delete();

	}
}