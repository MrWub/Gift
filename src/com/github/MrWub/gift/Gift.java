package com.github.MrWub.gift;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;



public class Gift extends JavaPlugin {
	public MyConfig config;
	public Isql sql;
	private ArrayList<ItemStack> im = new ArrayList<ItemStack>();
	public void info(String s) {
		getLogger().info(s);
	}
	@Override
	public void onEnable() {
		info("gift is enable!");
		MyConfig config = new MyConfig(this);
		if (config.load()) info("Loading config... OK!"); else {
			info("Loading config... Error! Disable!");
			getServer().getPluginManager().disablePlugin(this);
		}
		
		sql=new Isql(this);
		if (sql.init())info("Loading SQL... OK!"); else {
			info("Loading SQL... Error! Disable!");
			getServer().getPluginManager().disablePlugin(this);
		}
		if (initItems()) info("Loading Items... OK!"); else {
			info("Loading Items... Error! Disable!");
			getServer().getPluginManager().disablePlugin(this);
		}
		if (initGifts()) info("Loading Gifts... OK!"); else {
			info("Loading Gifts... Error! Disable!");
			getServer().getPluginManager().disablePlugin(this);
		}
		if(!getDataFolder().exists()) getDataFolder().mkdir(); 
	}
	private boolean initItems() {
		int lt = Integer.valueOf(sql.doSql("SELECT * FROM item WHERE id=0").get(1));
		for (int i=1; i<=lt; i++) {
			String imbyte = sql.doSql("SELECT * FROM item WHERE id="+i).get(1);
			i++;
		}
		return false;
	}
	private boolean initGifts() {
		return false;
	}
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
		return false;
	}

	
}
