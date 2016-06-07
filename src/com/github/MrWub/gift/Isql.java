package com.github.MrWub.gift;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class Isql {
	private static Connection c = null;
	public Isql(Gift gift) {
	}

	public boolean init() {
		String url=
				"jdbc:mysql://"
				+ MyConfig.host
				+ ":"
				+ MyConfig.port
				+ "/"
				+ MyConfig.dbName
				+ "?user="
				+ MyConfig.userName
				+ "&password="
				+ MyConfig.passwd
				+"&useUnicode=true&characterEncoding=UTF8";
		Statement st = null;
		try{
			Class.forName("com.mysql.jdbc.Driver");
			c = DriverManager.getConnection(url);
			if (!tableExists(MyConfig.tableName)) {
				createTable(MyConfig.tableName,"giftname text not null,id text,amount text");
			}
			if (!tableExists(MyConfig.itemTableName)) {
				createTable(MyConfig.itemTableName,"id int not null,map text");
			}
			return true;
		} catch(Exception e) {
			e.printStackTrace();
		} finally{
			try {
				if ((st != null) && (!st.isClosed())) st.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		return false;
	}
	private void createTable(String tableName,String args) {
		doSql("create table "+ MyConfig.tableName + " (" 
				+ args
				+ ") CHARACTER SET utf8 COLLATE utf8_general_ci");
	}
	
	public Iresult doSql(String cmd) {
		Iresult result = null;
	    Statement st = null;
	    ResultSet res = null;
	    try {
	    	st = c.createStatement();
	    	res = st.executeQuery(cmd);
	    	result = new Iresult(res);
	    } catch(Exception e) {
	    	e.printStackTrace();
	    } finally {
	    	try {
				if ((st != null) && (!st.isClosed())) st.close();
		    	if ((res != null) && (!res.isClosed())) res.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    }
	    return result;
	}
	
	private boolean tableExists(String tableName) {
		Iresult result = doSql("SHOW TABLES");
	    for(ArrayList<String> s:result.getAllTable()) {
	    	if (s.get(1).equalsIgnoreCase(tableName))return true;
	    }
	    return false;
	}


}
