package roje.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ComicsDAO {
	

	public void create(Comics c) {
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
			Connection connect = DriverManager.getConnection("jdbc:derby:.\\DB\\library.db");
			PreparedStatement st = connect
					.prepareStatement("insert into comics (id,title,description,pageCount) values(?,?,?,?)");
			st.setInt(1, c.getId());
			st.setString(2, c.getTitle());
			st.setString(3, c.getDescription());
			st.setInt(4, c.getPageCount());
			st.executeUpdate();
			st.close();
			connect.close();

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public Comics find(int id) {
		Comics c = new Comics(id, null, null, 0);
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

	public void delete(Comics c) {
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

}
