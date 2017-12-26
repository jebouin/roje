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
				stComics.executeUpdate(
						"CREATE TABLE comics (id INT PRIMARY KEY, title VARCHAR(100), description LONG VARCHAR, pageCount INT, thumbnailPartialPath VARCHAR(256), thumbnailExtension VARCHAR(16), format VARCHAR(64), onSaleDate TIMESTAMP, printPrice FLOAT, digitalPrice FLOAT)");
				stUserComics.executeUpdate(
						"CREATE TABLE userComics (id INT PRIMARY KEY REFERENCES COMICS(id), mark INT, comment VARCHAR(256), location VARCHAR(100), purchaseDate TIMESTAMP)");
				stComics.close();
				stUserComics.close();
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