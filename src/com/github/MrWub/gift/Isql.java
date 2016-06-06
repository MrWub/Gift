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
				createTable(MyConfig.tableName,"gid int not null,id text,amount text");
			}
			if (!tableExists(MyConfig.itemTableName)) {
				createTable(MyConfig.itemTableName,"iid int not null,map text");
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
	
	public HashMap<Integer,ArrayList<String>> doSql(String cmd) {
		HashMap<Integer,ArrayList<String>> result = new HashMap<Integer,ArrayList<String>>();
		ArrayList<String> tmp = new ArrayList<String>();
	    Statement st = null;
	    ResultSet res = null;
	    try {
	    	st = c.createStatement();
	    	res = st.executeQuery(cmd);
	    	int lt = res.getMetaData().getColumnCount();
	    	while (res.next()) {
	    		for (int i=1; i<=lt; i++) {
	    			tmp = result.get(i);
	    			tmp.add(res.getString(i));
		    		result.replace(i,tmp);
	    		}
	    	};
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
		HashMap<Integer,ArrayList<String>> result = doSql("SHOW TABLES");
	    for(String s:result.get(1)) {
	    	if (s.equalsIgnoreCase(tableName))return true;
	    }
	    return false;
	}


}
