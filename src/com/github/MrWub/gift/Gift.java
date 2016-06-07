package com.github.MrWub.gift;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;



public class Gift extends JavaPlugin {
	public MyConfig config;
	public Isql sql;
	public int itemSize, giftsSize;
	private Map<Integer,ItemStack> items = new HashMap<Integer,ItemStack>();
	private Map<String, Map<Integer,Integer>> gifts = new HashMap<String, Map<Integer,Integer>>();
	//<���֣�<myid������>>
	public void info(String s) {
		getLogger().info(s);
	}
	@Override
	public void onEnable() {
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
		Iresult result = sql.doSql("SELECT * FROM " + MyConfig.itemTableName);
		for (int i = 1; i<=result.getRowCount(); i++) {
			items.put(Integer.valueOf(result.getRow(1).get(1)),Idecode.redo(result.getRow(i).get(2)));
		}
	}
	
	private void initGifts() {
		Iresult result = sql.doSql("SELECT * FROM " + MyConfig.tableName);
		for (int i = 1; i<=result.getRowCount(); i++) {
			ArrayList<String> row = result.getRow(i);
			String name = row.get(1);
			String[] id = row.get(2).split(",");
			String[] amount = row.get(3).split(",");
			int j = 0;
			Map<Integer,Integer> map = new HashMap<Integer,Integer>();
			for(String s:id) {
				map.put(Integer.valueOf(s), Integer.valueOf(amount[j]));
				j++;
			}
			gifts.put(name, map);
		}
	}
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
		if (label.equalsIgnoreCase("gift")) {
			if (args.length == 1) return false;
			if (args.length == 2) {
				if (args[1].equalsIgnoreCase("help")) {
					String[] strs ={"gift�������",
									"/gift list �鿴����б� ��ҪȨ��gift.admin",
									"/gift get X ��ȡ���X ��ҪȨ��gift.admin��gift.get.X",
									"/gift del X ɾ�����X ��ҪȨ��gift.admin",
									"/gift all-add ���㱳�����������Ʒ����һ������� ��ҪȨ��gift.admin",
									"/gift give X Y ��X���һ��Y��� ��ҪȨ��gift.admin"};
					sender.sendMessage(strs);
				} else 
				if (args[1].equalsIgnoreCase("list")) {
					for (String key:gifts.keySet()) {
						sender.sendMessage(key);
					}
				}
			}
		}
		return false;
	}

	
}
