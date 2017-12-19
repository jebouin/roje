package roje.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ComicsDAO {

	public static void create(Comics c) {
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
			Connection connect = DriverManager.getConnection("jdbc:derby:.\\DB\\library.db");
			PreparedStatement st = connect
					.prepareStatement("insert into comics (id,title,description,pageCount,mark) values(?,?,?,?,?)");
			st.setInt(1, c.getId());
			st.setString(2, c.getTitle());
			st.setString(3, c.getDescription());
			st.setInt(4, c.getPageCount());
			st.setInt(5, c.getMark());
			st.executeUpdate();
			st.close();
			connect.close();

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public static Comics find(int id) {
		Comics c = new Comics(id, null, null, 0, null, null, 0);
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
			Connection connect = DriverManager.getConnection("jdbc:derby:.\\DB\\library.db");
			PreparedStatement st = connect.prepareStatement("select * from comics where id=?");
			st.setInt(1, id);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				c.setDescription(rs.getString("description"));
				c.setTitle(rs.getString("title"));
				c.setPageCount(rs.getInt("pageCount"));
				c.setMark(rs.getInt("mark"));
			}
			st.close();
			connect.close();

			return c;

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return c;

	}

	public static void delete(Comics c) {
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
			Connection connect = DriverManager.getConnection("jdbc:derby:.\\DB\\library.db");
			PreparedStatement st = connect.prepareStatement("delete * from comics where id=?");
			st.setInt(1, c.getId());
			st.executeUpdate();
			st.close();
			connect.close();

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void addMark(Comics c, int m) {
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
			Connection connect = DriverManager.getConnection("jdbc:derby:.\\DB\\library.db");
			PreparedStatement st = connect.prepareStatement("update comics set mark=? where id=?");
			st.setInt(1, m);
			st.setInt(2, c.getId());
			st.executeUpdate();
			st.close();
			connect.close();

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static List<Comics> getComics() {
		ResultSet rs = null;
		List<Comics> result = new ArrayList<Comics>();
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
			Connection connect = DriverManager.getConnection("jdbc:derby:.\\DB\\library.db");
			PreparedStatement st = connect.prepareStatement("Select id,title,description,pageCount,mark from comics");
			rs = st.executeQuery();
			while (rs.next()) {
				Comics c = new Comics(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4), null, null,
						rs.getInt(5));
				result.add(c);
			}
			st.close();
			connect.close();

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
}