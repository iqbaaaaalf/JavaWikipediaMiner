package com.arsnindo.util;

import java.sql.*;

public class Connect {
	
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver"; 
	/*
	 * edit sebelum memulai koneksi ke beda database
	 */
	static final String DB_URL = "jdbc:mysql://localhost:3306/wikicorpus1";
	
	static final String USER = "root";
	static final String PASS = "";
	Connection conn = null;
	
	
	public void connect(){
		
		try{
		      //Register JDBC driver
		      Class.forName(JDBC_DRIVER);

		      //open connecttion
		      System.out.println("Connecting to database...");
		      conn = DriverManager.getConnection(DB_URL,USER,PASS);
		   
		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
			      //Handle errors for Class.forName
			      e.printStackTrace();
		   }
	}
	
	public void close(){
		try{
	         if(conn!=null)
	            conn.close();
	      }catch(SQLException se){
	         se.printStackTrace();
	      }
	}

	/*
	 * @param sql adalah syntax sql yang ingin di execute dalam get sesuatu
	 * @return result set dari query yang diminta
	 */
	
	public ResultSet queryGet(String sql){
		Statement stmt = null;
		ResultSet rs = null;
	
		
		try{
		stmt = conn.createStatement();
		rs = stmt.executeQuery(sql);
		
		//clean up requirement
		rs.close();
		stmt.close();
		}catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
			      //Handle errors for Class.forName
			      e.printStackTrace();
			   }finally{
			      //finally block used to close resources
				   try{
				         if(stmt!=null)
				            stmt.close();
				      }catch(SQLException se2){
				      }// nothing we can do
			   }
		
		
		return rs;
	}
	
	/*
	 * CREATE RECORD TO TABLE DOCUMENT
	 */
	
	public void createDoc(int id, String title, String content){
		PreparedStatement stmt = null;
		String sql = "INSERT INTO document (id, title, content)" +
						"VALUES (?, ?, ?)";
		
		try{
		stmt = conn.prepareStatement(sql);
		stmt.setInt(1, id);
		stmt.setString(2, title);
		stmt.setString(3, content);
		
		//update notif
		
		int rows = stmt.executeUpdate();
	    //System.out.println("Rows impacted : " + rows );
		
		}catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
			      //Handle errors for Class.forName
			      e.printStackTrace();
			   }finally{
			      //finally block used to close resources
				   try{
				         if(stmt!=null)
				            stmt.close();
				      }catch(SQLException se2){
				      }
			   }
		
	}
	
	/*
	 * INSERT RECORD TO TABLE CATEGORY
	 */
	
	public void createCat(String categoryName, int id){
		PreparedStatement stmt = null;
		String sql = "INSERT INTO category (categoryName, id)" +
						"VALUES (?, ?)";
		
		try{
		stmt = conn.prepareStatement(sql);
		stmt.setString(1, categoryName);
		stmt.setInt(2, id);
		
		//update notif
		
		int rows = stmt.executeUpdate();
	    //System.out.println("Rows impacted : " + rows );
		
		}catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
			      //Handle errors for Class.forName
			      e.printStackTrace();
			   }finally{
			      //finally block used to close resources
				   try{
				         if(stmt!=null)
				            stmt.close();
				      }catch(SQLException se2){
				      }
			   }
		
	}
	
	/*
	 * INSERT RECORD TO TABLE BOX
	 */
	
	public void createBox(int id, String label, String value, int boxType, String boxCategory){
		PreparedStatement stmt = null;
		String sql = "INSERT INTO box (id, label, value, boxType, boxCategory)" +
						"VALUES (?, ?, ?, ?, ?)";
		
		try{
		stmt = conn.prepareStatement(sql);
		stmt.setInt(1, id);
		stmt.setString(2, label);
		stmt.setString(3, value);
		stmt.setInt(4, boxType);
		stmt.setString(5, boxCategory);
		
		//update notif
		
		int rows = stmt.executeUpdate();
	    //System.out.println("Rows impacted : " + rows );
		
		}catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
			      //Handle errors for Class.forName
			      e.printStackTrace();
			   }finally{
			      //finally block used to close resources
				   try{
				         if(stmt!=null)
				            stmt.close();
				      }catch(SQLException se2){
				      }
			   }
		
	}
	
	

}