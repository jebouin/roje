package roje.model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.joda.time.DateTime;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import roje.Main;

public class ComicsDAO {
	private static Connection connection;

	public static void init() {
		try {
			connection = DriverManager.getConnection("jdbc:derby:.\\DB\\library.db");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void addComic(Comics c) {
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
			PreparedStatement st = connection.prepareStatement(
					"INSERT INTO comics (id, title, description, pageCount, thumbnailPartialPath, thumbnailExtension, format, onSaleDate, printPrice, digitalPrice) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			st.setInt(1, c.getId());
			st.setString(2, c.getTitle());
			st.setString(3, c.getDescription());
			st.setInt(4, c.getPageCount());
			st.setString(5, c.getThumbnail().getPartialPath());
			st.setString(6, c.getThumbnail().getExtension());
			st.setString(7, c.getFormat());
			st.setTimestamp(8, Timestamp.from(java.time.Instant.ofEpochMilli(c.getOnSaleDate().getMillis())),
					Calendar.getInstance());
			if (c.getPrintPrice() == null) {
				st.setNull(9, java.sql.Types.FLOAT);
			} else {
				st.setFloat(9, c.getPrintPrice());
			}
			if (c.getDigitalPrice() == null) {
				st.setNull(10, java.sql.Types.FLOAT);
			} else {
				st.setFloat(10, c.getDigitalPrice());
			}
			st.executeUpdate();
			st.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// fetches additional user-related data if userComic is true
	private static Comics resultSetToComic(ResultSet rs, boolean userComic) throws SQLException {
		Thumbnail thumbnail = new Thumbnail(rs.getString("thumbnailPartialPath"), rs.getString("thumbnailExtension"));
		Timestamp purchaseDate = userComic ? rs.getTimestamp("purchaseDate") : null;
		Comics comic = new Comics(rs.getInt("id"), rs.getString("title"), rs.getString("description"),
				rs.getInt("pageCount"), thumbnail, rs.getString("format"),
				new DateTime(rs.getTimestamp("onSaleDate").getTime()), rs.getFloat("printPrice"),
				rs.getFloat("digitalPrice"), userComic ? rs.getInt("mark") : null,
				purchaseDate == null ? null : new DateTime(rs.getTimestamp("purchaseDate").getTime()),
				userComic ? rs.getString("location") : null, userComic ? rs.getString("comment") : null,
				userComic ? rs.getString("addprice") : null);
		return comic;
	}

	public static Comics findComic(int id) {
		Comics comic = null;
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
			PreparedStatement st = connection.prepareStatement("SELECT * FROM comics WHERE id = ?");
			st.setInt(1, id);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				comic = resultSetToComic(rs, false);
			}
			st.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comic;
	}

	public static List<Comics> findAllComics() {
		ResultSet rs;
		List<Comics> result = new ArrayList<Comics>();
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
			PreparedStatement st = connection.prepareStatement("SELECT * FROM comics");
			rs = st.executeQuery();
			while (rs.next()) {
				Comics comic = resultSetToComic(rs, false);
				result.add(comic);
			}
			st.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void addUserComic(final int id, DateTime purchaseDate, String location, String addprice) {
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
			PreparedStatement st = connection.prepareStatement(
					"INSERT INTO userComics (id, purchaseDate, location, addprice) VALUES (?, ?, ?, ?)");
			st.setInt(1, id);
			st.setTimestamp(2, purchaseDate == null ? null
					: Timestamp.from(java.time.Instant.ofEpochMilli(purchaseDate.getMillis())));
			st.setString(3, location);
			st.setString(4, addprice);
			st.executeUpdate();
			st.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void deleteUserComic(final int id) {
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
			Connection connect = DriverManager.getConnection("jdbc:derby:.\\DB\\library.db");
			PreparedStatement st = connect.prepareStatement("DELETE FROM userComics WHERE id = ?");
			st.setInt(1, id);
			st.executeUpdate();
			st.close();
			connect.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean findUserComic(final int id) {
		boolean found = false;
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
			PreparedStatement st = connection.prepareStatement("SELECT id FROM userComics WHERE id = ?");
			st.setInt(1, id);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				found = true;
			}
			st.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return found;
	}

	public static String returnComment(final int id, TextArea comment) {
		String comm1 = null;
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
			Connection connect = DriverManager.getConnection("jdbc:derby:.\\DB\\library.db");
			PreparedStatement st = connect.prepareStatement("SELECT comment FROM userComics WHERE id = ?");
			st.setInt(1, id);
			ResultSet com = st.executeQuery();
			while (com.next()) {
				comm1 = com.getString(1);
			}
			st.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comm1;
	}

	public static void setMark(int id, int mark) throws IOException {
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
			Comics comic = findComic(id);
			if (comic == null) {
				throw new Exception("This comic doesn't exist");
			} else if (!findUserComic(id)) {
				throw new Exception("The user doesn't have this comic");
			} else {
				PreparedStatement st = connection.prepareStatement("UPDATE userComics SET mark = ? WHERE id = ?");
				st.setInt(1, mark);
				st.setInt(2, id);
				st.executeUpdate();
				st.close();
				System.out.println(mark);
				// TODO: move this to controller
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(Main.class.getResource("view/SuccessfulView.fxml"));
				AnchorPane pane = (AnchorPane) loader.load();
				Stage stage = new Stage();
				stage.setScene(new Scene(pane, 300, 95));
				stage.show();
				//
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setComment(int id, String comment) throws IOException {
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
			Comics comic = findComic(id);
			if (comic == null) {
				throw new Exception("This comic doesn't exist");
			} else if (!findUserComic(id)) {
				throw new Exception("The user doesn't have this comic");
			} else {
				PreparedStatement st = connection.prepareStatement("UPDATE userComics SET comment = ? WHERE id = ?");
				st.setString(1, comment);
				st.setInt(2, id);
				st.executeUpdate();
				st.close();
				System.out.println(comment);
				// TODO: move this to controller
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(Main.class.getResource("view/SuccessfulView.fxml"));
				AnchorPane pane = (AnchorPane) loader.load();
				Stage stage = new Stage();
				stage.setScene(new Scene(pane, 300, 95));
				stage.show();
				//
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<Comics> findAllUserComics() {
		ResultSet rs;
		List<Comics> result = new ArrayList<Comics>();
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
			PreparedStatement st = connection.prepareStatement(
					"SELECT comics.id, title, description, pageCount, thumbnailPartialPath, thumbnailExtension, format, onSaleDate, printPrice, digitalPrice, mark, purchaseDate, addprice, location, comment FROM comics JOIN userComics ON comics.id = userComics.id");
			rs = st.executeQuery();
			while (rs.next()) {
				Comics comic = resultSetToComic(rs, true);
				result.add(comic);
				System.out.println(comic.getMark());
				System.out.println(comic.getComment());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void close() throws SQLException {
		connection.close();
	}
}