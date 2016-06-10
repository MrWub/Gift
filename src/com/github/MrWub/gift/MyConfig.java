package com.github.MrWub.gift;

import org.bukkit.configuration.file.FileConfiguration;

public class MyConfig {
	private static Gift main;
	private FileConfiguration f;
	public static String host,port,dbName ,tableName,userName,passwd;
	public MyConfig(Gift m) {
		main=m;
	}

	public boolean load() {
		try {
			main.saveDefaultConfig();
			main.reloadConfig();
			f=main.getConfig();
			
			host=f.getString("mysql.host");
			port=f.getString("mysql.port");
			dbName=f.getString("mysql.dbname");
			tableName=f.getString("mysql.tablename");
			userName=f.getString("mysql.username");
			passwd=f.getString("mysql.passwd");
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
