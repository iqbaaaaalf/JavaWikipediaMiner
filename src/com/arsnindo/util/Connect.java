package com.arsnindo.util;

import java.sql.*;

public class Connect {
	
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver"; 
	/*
	 * edit sebelum memulai koneksi ke beda database
	 */
	static final String DB_URL = "jdbc:mysql://localhost:3306/wikicorpus2";
	
	static final String USER = "root";
	static final String PASS = "";
	Connection conn = null;
	
	PreparedStatement stmtDoc = null;
	PreparedStatement stmtCat = null;
	PreparedStatement stmtBox = null;
	Statement stmt = null;
	
	public void connect(){
		
		try{
		      //Register JDBC driver
		      Class.forName(JDBC_DRIVER);

		      //open connecttion
		      System.out.println("Connecting to database...");
		      conn = DriverManager.getConnection(DB_URL,USER,PASS);
		      stmtCat();
		      stmtDoc();
		      stmtBox();
		   
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
	 * untuk mengambil id artikel yang dimana dari kategori tertentu dan label tertentu
	 */
	
	public ResultSet getArticleByCategoryNLabel(){
		String sql = "SELECT `category`.`id` ,`box`.`label`, `box`.`value` FROM `category` LEFT JOIN `box` ON `category`.`id` = `box`.`id` WHERE `category`.`categoryName` LIKE '%dalam tahun%' AND `box`.`label` LIKE '%location%'";
		ResultSet rs = null;
		
		try {
		 stmt = conn.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try{
		rs = stmt.executeQuery(sql);
		}catch(SQLException se){
			
		}
		return rs ;
	}
	
	private PreparedStatement stmtDoc(){
		String sql = "INSERT INTO document (id, title, content)" +
				"VALUES (?, ?, ?)";
		
		try {
		 stmtDoc = conn.prepareStatement(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return stmtDoc;
	}
	
	private PreparedStatement stmtCat(){
		String sql = "INSERT INTO category (categoryName, id)" +
				"VALUES (?, ?)";
		
		try {
		 stmtCat = conn.prepareStatement(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return stmtCat;
	}
	
	private PreparedStatement stmtBox(){
		String sql = "INSERT INTO box (id, label, value, boxType, boxCategory)" +
				"VALUES (?, ?, ?, ?, ?)";
		
		try {
		 stmtBox = conn.prepareStatement(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return stmtBox;
	}
	/*
	 * CREATE DOC BASH
	 * 
	 */
	
	public void docBatch(int id, String title, String content){
		
		try{
		conn.setAutoCommit(false);
		
		stmtDoc.setInt(1, id);
		stmtDoc.setString(2, title);
		stmtDoc.setString(3, content);
		
		stmtDoc.addBatch();
		
		//update notif
		
		//int rows = stmt.executeUpdate();
	    //System.out.println("Rows impacted : " + rows );
		
		}catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
			      //Handle errors for Class.forName
			      e.printStackTrace();
		   }
		
	}
	/*
	 * to execute doc batch query
	 */
	public void exeDocBatch(){
		System.out.println("\nExecuting Document miner batch ......");
		try{
		int[] count = stmtDoc.executeBatch();
		System.out.println(count);
		
		conn.commit();
		
		}catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
			      //Handle errors for Class.forName
			      e.printStackTrace();
			   }finally{
			      //finally block used to close resources
				   try{
				         if(stmtDoc!=null)
				            stmtDoc.close();
				      }catch(SQLException se2){
				      }
			   }
		
	}
	
	/*
	 * CREATE CATEGORY SQL BATCH
	 */
	
	public void catBatch(String categoryName, int id){

		try{
		stmtCat.setString(1, categoryName);
		stmtCat.setInt(2, id);
		
		stmtCat.addBatch();
		
		//update notif
		
		//int rows = stmt.executeUpdate();
	    //System.out.println("Rows impacted : " + rows );
		
		}catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
			      //Handle errors for Class.forName
			      e.printStackTrace();
		   }
		
	}
	/*
	 * to execute doc batch query
	 */
	public void exeCatBatch(){
		System.out.println("\nExecuting category miner batch ......");
		try{
		int[] count = stmtCat.executeBatch();
		System.out.println(count);
		
		conn.commit();
		
		}catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
			      //Handle errors for Class.forName
			      e.printStackTrace();
			   }finally{
			      //finally block used to close resources
				   try{
				         if(stmtCat!=null)
				        	 stmtCat.close();
				      }catch(SQLException se2){
				      }
			   }
		
	}
	
	/*
	 * CREATE BOX SQL BATCH
	 */
	
	public void boxBatch(int id, String label, String value, int boxType, String boxCategory){
		
		try{
		conn.setAutoCommit(false);
		stmtBox.setInt(1, id);
		stmtBox.setString(2, label);
		stmtBox.setString(3, value);
		stmtBox.setInt(4, boxType);
		stmtBox.setString(5, boxCategory);
		
		stmtBox.addBatch();
		
		}catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
			      //Handle errors for Class.forName
			      e.printStackTrace();
			   }
	}
	
	/*
	 * execute BOX BATCH
	 */
	public void exeBoxBatch(){
		System.out.println("\nExecuting Box miner batch ......");
		try{
		int[] count = stmtBox.executeBatch();
		System.out.println(count);
		
		conn.commit();
		
		}catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
			      //Handle errors for Class.forName
			      e.printStackTrace();
			   }finally{
			      //finally block used to close resources
				   try{
				         if(stmtBox!=null)
				        	 stmtBox.close();
				      }catch(SQLException se2){
				      }
			   }
		
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