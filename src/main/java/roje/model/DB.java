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
			String creationStatement = "Create table comics (id int primary key, title varchar(100),description long varchar,pageCount int,mark int)";
			try {
				Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
				Connection connect = DriverManager.getConnection("jdbc:derby:.\\DB\\library.db;create=true");
				Statement st = connect.createStatement();
				st.executeUpdate(creationStatement);
				st.close();
				connect.close();

			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				return;
			}
		}

	}
}