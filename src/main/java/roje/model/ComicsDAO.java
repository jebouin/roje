package roje.model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import roje.Main;

public class ComicsDAO {
	private static Connection connection;

	public static void init() {
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
			connection = DriverManager.getConnection("jdbc:derby:.\\DB\\library.db");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void addComic(Comics c) {
		try {
			deleteComic(c.getId());
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
			for (Creator creator : c.getCreators()) {
				addCreator(creator);
				// add to NxN table
				st = connection.prepareStatement("INSERT INTO comicsCreators (comicId, creatorName) VALUES (?, ?)");
				st.setInt(1, c.getId());
				st.setString(2, creator.getName());
				st.executeUpdate();
				st.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void executeUpdateWithComicId(final String statement, final int id)
			throws ClassNotFoundException, SQLException {
		PreparedStatement st = connection.prepareStatement(statement);
		st.setInt(1, id);
		st.executeUpdate();
		st.close();
	}

	public static void deleteComic(final int id) throws ClassNotFoundException, SQLException {
		executeUpdateWithComicId("DELETE FROM comicsCreators WHERE comicId = ?", id);
		executeUpdateWithComicId("DELETE FROM comics WHERE id = ?", id);
	}

	// fetches additional user-related data if userComic is true
	private static Comics resultSetToComic(ResultSet rs, boolean userComic) throws SQLException {
		Thumbnail thumbnail = new Thumbnail(rs.getString("thumbnailPartialPath"), rs.getString("thumbnailExtension"));
		Comics comic = new Comics(rs.getInt("id"), rs.getString("title"), rs.getString("description"),
				rs.getInt("pageCount"), thumbnail, rs.getString("format"),
				new DateTime(rs.getTimestamp("onSaleDate").getTime()), rs.getFloat("printPrice"),
				rs.getFloat("digitalPrice"), userComic ? rs.getInt("mark") : null,
				userComic ? rs.getDate("purchaseDate") : null, userComic ? rs.getString("location") : null,
				userComic ? rs.getString("comment") : null, userComic ? rs.getString("addprice") : null,
				FXCollections.observableArrayList());
		return comic;
	}

	public static Comics findComic(int id) {
		Comics comic = null;
		try {
			PreparedStatement st = connection.prepareStatement("SELECT * FROM comics WHERE id = ?");
			st.setInt(1, id);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				comic = resultSetToComic(rs, false);
			}
			st.close();
			st = connection.prepareStatement("SELECT * FROM comicsCreators WHERE comicId = ?");
			st.setInt(1, id);
			rs = st.executeQuery();
			while (rs.next()) {
				comic.addCreator(new Creator(rs.getString(1), rs.getString(2)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comic;
	}

	private static List<Comics> findComicsByStatement(final PreparedStatement st, final PreparedStatement stCreators) {
		ResultSet rs;
		List<Comics> result = new ArrayList<Comics>();
		try {
			rs = st.executeQuery();
			Map<Integer, Comics> comics = new HashMap<Integer, Comics>();
			while (rs.next()) {
				Comics comic = resultSetToComic(rs, false);
				comics.put(comic.getId(), comic);
			}
			st.close();
			rs = stCreators.executeQuery();
			while (rs.next()) {
				Creator creator = new Creator(rs.getString(2), rs.getString(3));
				comics.get(rs.getInt(1)).addCreator(creator);
			}
			for (Map.Entry<Integer, Comics> pair : comics.entrySet()) {
				result.add(pair.getValue());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Comics.sortCOmicListBySerie(result);
		return result;
	}

	public static List<Comics> findComicByPrefix(final String prefix) {
		try {
			PreparedStatement st = connection.prepareStatement("SELECT * FROM comics WHERE LOWER(title) LIKE ?");
			st.setString(1, (prefix + "%").toLowerCase());
			PreparedStatement stCreators = connection.prepareStatement(
					"SELECT comicId, name, role FROM comics JOIN comicsCreators ON id = comicId JOIN creators ON name = creatorName WHERE LOWER(title) LIKE ? ");
			stCreators.setString(1, (prefix + "%").toLowerCase());
			return findComicsByStatement(st, stCreators);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static List<Comics> findAllComics() {
		try {
			PreparedStatement st = connection.prepareStatement("SELECT * FROM comics");
			PreparedStatement stCreators = connection.prepareStatement(
					"SELECT comicId, name, role FROM comicsCreators JOIN creators ON creatorName = name");
			return findComicsByStatement(st, stCreators);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void addUserComic(final int id, Date purchaseDate, String location, String addprice) {
		try {
			PreparedStatement st = connection.prepareStatement(
					"INSERT INTO userComics (id, purchaseDate, location, addprice) VALUES (?, ?, ?, ?)");
			st.setInt(1, id);
			st.setDate(2, purchaseDate);
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

	public static List<Comics> findAllUserComics() {
		try {
			PreparedStatement st = connection.prepareStatement(
					"SELECT comics.id, title, description, pageCount, thumbnailPartialPath, thumbnailExtension, format, onSaleDate, printPrice, digitalPrice, mark, purchaseDate, location, comment, addprice FROM comics JOIN userComics ON comics.id = userComics.id");
			PreparedStatement stCreators = connection.prepareStatement(
					"SELECT comicId, name, role FROM userComics JOIN comicsCreators ON comicId = id JOIN creators ON creatorName = name");
			return findComicsByStatement(st, stCreators);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void addCreator(Creator c) {
		try {
			PreparedStatement st = connection.prepareStatement("SELECT COUNT(*) FROM creators WHERE name = ?");
			st.setString(1, c.getName());
			ResultSet rs = st.executeQuery();
			rs.next();
			if (rs.getInt(1) == 0) {
				st = connection.prepareStatement("INSERT INTO creators (name, role) VALUES (?, ?)");
				st.setString(1, c.getName());
				st.setString(2, c.getRole());
				st.executeUpdate();
			}
			st.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void executeUpdateWithCreatorName(final String statement, final String name)
			throws ClassNotFoundException, SQLException {
		PreparedStatement st = connection.prepareStatement(statement);
		st.setString(1, name);
		st.executeUpdate();
		st.close();
	}

	public static void deleteCreator(final String name) throws ClassNotFoundException, SQLException {
		executeUpdateWithCreatorName("DELETE FROM comicsCreators WHERE creatorName = ?", name);
		executeUpdateWithCreatorName("DELETE FROM creators WHERE name = ?", name);
	}

	public static List<Creator> findAllCreators() {
		ResultSet rs;
		List<Creator> result = new ArrayList<Creator>();
		try {
			PreparedStatement st = connection.prepareStatement("SELECT * FROM creators");
			rs = st.executeQuery();
			while (rs.next()) {
				Creator cr = new Creator(rs.getString(1), rs.getString(2));
				result.add(cr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String returnComment(final int id) {
		String comm1 = null;
		try {
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

	public static ObservableList<String> returnBookmarks(final int id) {
		String bookm = null;
		ObservableList<String> bookmList = FXCollections.observableArrayList();
		try {
			Connection connect = DriverManager.getConnection("jdbc:derby:.\\DB\\library.db");
			PreparedStatement st = connect.prepareStatement("SELECT bookmark FROM userBookmarks WHERE id = ?");
			st.setInt(1, id);
			ResultSet com = st.executeQuery();
			while (com.next()) {
				bookm = com.getString(1);
				bookmList.add(com.getString(1));
			}
			st.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bookmList;
	}

	public static void setMark(int id, int mark) throws IOException {
		try {
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

	public static void addBookmark(int id, String bookmark) throws IOException {
		try {
			Comics comic = findComic(id);
			if (comic == null) {
				throw new Exception("This comic doesn't exist");
			} else if (!findUserComic(id)) {
				throw new Exception("The user doesn't have this comic");
			} else {
				ObservableList<String> newBookm = FXCollections.observableArrayList();
				for (int i = 0; i < comic.getBookmarks().size(); i++) {
					newBookm.add(comic.getBookmarks().get(i));
				}
				comic.setBookmarks(newBookm);
				PreparedStatement st = connection
						.prepareStatement("INSERT INTO userBookmarks(id,bookmark) VALUES (?,?)");
				st.setInt(1, id);
				st.setString(2, bookmark);
				st.executeUpdate();
				st.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void deleteBookmark(int id, String bookmark) throws IOException {
		try {
			Comics comic = findComic(id);
			ObservableList<String> bookmList = FXCollections.observableArrayList();
			if (comic == null) {
				throw new Exception("This comic doesn't exist");
			} else if (!findUserComic(id)) {
				throw new Exception("The user doesn't have this comic");
			} else {
				PreparedStatement st = connection
						.prepareStatement("DELETE FROM userBookmarks WHERE id=? AND bookmark=?");
				st.setInt(1, id);
				st.setString(2, bookmark);
				st.executeUpdate();
				st.close();
				PreparedStatement st2 = connection.prepareStatement("SELECT bookmark FROM userBookmarks WHERE id = ?");
				st2.setInt(1, id);
				ResultSet com = st2.executeQuery();
				while (com.next()) {
					bookmList.add(com.getString(1));
				}
				comic.setBookmarks(bookmList);
				st2.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<Comics> getRecommendedComicsByCreator() {
		ResultSet rs;
		List<Comics> comics = new ArrayList<Comics>();
		try {
			PreparedStatement st = connection.prepareStatement(
					"SELECT comics.id, title, description, pageCount, thumbnailPartialPath, thumbnailExtension, format, onSaleDate, printPrice, digitalPrice, cacheDate\r\n"
							+ "FROM (SELECT comicId, scoreSum / scoreCount AS comicScore \r\n"
							+ "FROM (SELECT comicId, CAST(COUNT(withScore.score) AS FLOAT) AS scoreCount, SUM(withScore.score) AS scoreSum\r\n"
							+ "FROM comicsCreators\r\n" + "\r\n" + "\r\n" + "JOIN\r\n" + "\r\n" + "(SELECT name,\r\n"
							+ "(p + 1.9208 / n - 1.96 * SQRT((p * (1.0 - p) + .9604 / n) / n)) / (1.0 + 3.8416 / n) AS score\r\n"
							+ "FROM (SELECT name, n, s / n / 20.0 AS p\r\n"
							+ "FROM (SELECT comicsCreators.creatorName AS name, CAST(COUNT(title) AS FLOAT) AS n, CAST(SUM(mark) AS FLOAT) AS s\r\n"
							+ "FROM userComics\r\n" + "NATURAL JOIN comics\r\n" + "JOIN comicsCreators\r\n"
							+ "ON comicsCreators.comicId = comics.id\r\n"
							+ "GROUP BY comicsCreators.creatorName) AS stats) AS withP) AS withScore\r\n" + "\r\n"
							+ "ON withScore.name = comicsCreators.creatorName\r\n" + "\r\n" + "\r\n"
							+ "WHERE comicId NOT IN (SELECT id FROM userComics)\r\n"
							+ "GROUP BY comicsCreators.comicId) AS comicStats\r\n" + "ORDER BY comicScore DESC\r\n"
							+ "FETCH FIRST 100 ROWS ONLY) AS sortedIds\r\n"
							+ "JOIN comics ON sortedIds.comicId = comics.id");
			rs = st.executeQuery();
			while (rs.next()) {
				comics.add(resultSetToComic(rs, false));
			}
			st.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comics;
	}

	public static List<Comics> returnseries() {
		List<Comics> recommandations = new ArrayList<Comics>();
		try {
			HashMap<String, List<Comics>> series = new HashMap<String, List<Comics>>();
			List<Comics> comicListInLibrary = findAllUserComics();
			// put the ids of comics in library for constant time lookup
			Set<Integer> librarySet = new HashSet<Integer>();
			for (Comics c : comicListInLibrary) {
				librarySet.add(c.getId());
			}
			List<Comics> allComics = findAllComics();
			for (Comics c : allComics) {
				if (series.get(c.getSerieName()) == null) {
					series.put(c.getSerieName(), new ArrayList<Comics>());
				}
				series.get(c.getSerieName()).add(c);
			}
			for (Map.Entry<String, List<Comics>> pair : series.entrySet()) {
				if (pair.getKey() != null) {
					Comics.sortComicListByIssueNumber(pair.getValue());
					boolean hasone = false;
					for (Comics comic : pair.getValue()) {
						if (librarySet.contains(comic.getId())) {
							hasone = true;
							break;
						}
					}
					if (hasone) {
						for (Comics c : pair.getValue()) {
							if (!librarySet.contains(c.getId()) && c != pair.getValue().get(0)) {
								recommandations.add(c);
								break;
							}
						}
					}
				}
			}
		} catch (

		Exception e) {
			e.printStackTrace();
		}
		return recommandations;
	}

	public static void close() throws SQLException {
		connection.close();
	}
}