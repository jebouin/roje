package roje.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


 
public class DB{
	
	Connection connect;
	Statement st;
	int idCount;
	
	public DB() {
		connect=null;
		st=null;
		idCount=1;
		createDB();
	}
	
	public void createDB() {
		
		String creationStatement = "Create table comics (id int primary key, name varchar(45),authors varchar(45))";
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
			connect=DriverManager.getConnection("jdbc:derby:.\\DB\\library.db;create=true");
			st=connect.createStatement();
			st.executeUpdate(creationStatement);
			st.close();
			connect.close();
			
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public void addComic(String comicName, String comicAuthors) {
		String s="insert into comics values(" + idCount +",'"+ comicName +"','"+ comicAuthors +"')";
		
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
			connect=DriverManager.getConnection("jdbc:derby:.\\DB\\library.db");
			st=connect.createStatement();
			st.executeUpdate(s);
			idCount+=1;
			st.close();
			connect.close();
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	

	public void deleteComic(int idComic) {
		String s="delete from comics where 'id'="+idComic;
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
			connect=DriverManager.getConnection("jdbc:derby:.\\DB\\library.db");
			st=connect.createStatement();
			st.executeUpdate(s);
			st.close();
			connect.close();
		
		
		
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	public ResultSet getValues() {
		String s="Select name, authors from comics";
		ResultSet rs=null;
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
			connect=DriverManager.getConnection("jdbc:derby:.\\DB\\library.db");
			st=connect.createStatement();
			rs=st.executeQuery(s);
			st.close();
			connect.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
		
	}
	
	
}

