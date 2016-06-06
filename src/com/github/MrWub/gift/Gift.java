package com.github.MrWub.gift;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
	public int itemSize, giftsSize;
	private ArrayList<ItemStack> im = new ArrayList<ItemStack>();
	private ArrayList<Map<Integer,Integer>> gifts = new ArrayList<Map<Integer,Integer>> ();
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
		if (sql.init())info("Loading SQL... "); else {
			info("Loading SQL... Error! Disable!");
			getServer().getPluginManager().disablePlugin(this);
		}
		info("Loading Items... ");
		initItems();
		info("Loading Gifts... ");
		initGifts(); 
		if(!getDataFolder().exists()) getDataFolder().mkdir(); 
	}
	
	private void initItems() {
		HashMap<Integer,ArrayList<String>> result = 
				sql.doSql("SELECT * FROM"
				+ MyConfig.itemTableName
				+" WHERE iid>0");
		ArrayList<String> l = result.get(1);
		ArrayList<String> imbyte = result.get(2);
		int i=0;
		for (String s:imbyte) {
			i++;
			im.set(Integer.valueOf(l.get(i)),Idecode.redo(s));
		}
		itemSize = i;
	}
	
	private void initGifts() {
		HashMap<Integer,ArrayList<String>> result = new HashMap<Integer,ArrayList<String>>();
		ArrayList<String> g = result.get(2);
		ArrayList<String> cnt = result.get(3);
		Map<Integer,Integer> map = new HashMap<Integer,Integer>();
		for (int i = 0; i<g.size(); i++) {
			map.clear();
			String[] id = g.get(i).split(","), count = cnt.get(i).split(",");
			for (int j = 0; j<id.length; j++) {
				map.put(Integer.valueOf(id[j]), Integer.valueOf(count[j]));
			}
			map.put(0, i);
			gifts.add(map);
		}
		giftsSize = g.size();
	}
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
		if (label.equalsIgnoreCase("gift")) {
			if (args.length == 1) return false;
			if (args.length == 2) {
				if (args[1].equalsIgnoreCase("help")) {
					String[] strs ={"gift命令帮助",
									"/gift list 查看礼包列表 需要权限gift.admin",
									"/gift get X 获取X号礼包 需要权限gift.admin",
									"/gift del X 删除X号礼包 需要权限gift.admin",
									"/gift additem X Y 将手上的东西*Y增加至X号礼包 需要权限gift.admin",
									"/gift delitem X Y 将从第X号礼包中减掉Y个你手上的东西，不足则减为0 需要权限gift.admin",
									"/gift all-add 用你背包里的所有物品创建一个新礼包 需要权限gift.admin"};
					sender.sendMessage(strs);
				} else 
				if (args[1].equalsIgnoreCase("list")) {
					for (Map<Integer,Integer> map:gifts) {
						sender.sendMessage("===礼包"+map.get(0));
						String msg = "";
						for (int x:map.keySet()) {
							msg = msg + im.get(x).getItemMeta().getDisplayName()+"*"+map.get(x)+"  ";
						}
						sender.sendMessage(msg);
					}
				}
			}
		}
		return false;
	}

	
}
