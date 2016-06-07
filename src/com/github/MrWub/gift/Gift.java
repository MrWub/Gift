package com.github.MrWub.gift;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;



public class Gift extends JavaPlugin {
	public MyConfig config;
	public Isql sql;
	public int itemSize, giftsSize;
	private Map<Integer,ItemStack> items = new HashMap<Integer,ItemStack>();
	private Map<String, ArrayList<Integer>> gifts = new HashMap<String, ArrayList<Integer>>();
	//<名字，<myid，数量>>
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
		int max = 0;
		for (int i = 1; i<=result.getRowCount(); i++) {
			ArrayList<String> row = result.getRow(i);
			items.put(Integer.valueOf(row.get(1)),Idecode.redo(row.get(2)));
			if (max<Integer.valueOf(row.get(1))) {
				max = Integer.valueOf(row.get(1));
			}
		}
		itemSize = max;
	}
	
	private void initGifts() {
		Iresult result = sql.doSql("SELECT * FROM " + MyConfig.tableName);
		for (int i = 1; i<=result.getRowCount(); i++) {
			ArrayList<String> row = result.getRow(i);
			String name = row.get(1);
			String[] id = row.get(2).split(",");
			ArrayList<Integer> tmp = new ArrayList<Integer>();
			for(String s:id) {
				tmp.add(Integer.valueOf(s));
			}
			gifts.put(name, tmp);
		}
	}
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
		if (label.equalsIgnoreCase("gift")) {
			if (args.length == 1) return false;
			if (args.length == 2) {
				if (args[1].equalsIgnoreCase("help")) {
					String[] strs ={"gift命令帮助",
									"/gift list 查看礼包列表 需要权限gift.admin",
									"/gift get X 获取礼包X 需要权限gift.admin或gift.get.X",
									"/gift del X 删除礼包X 需要权限gift.admin",
									"/gift all-add X用你背包里的所有物品创建一个X礼包 需要权限gift.admin",
									"/gift give X Y 给X玩家一个Y礼包 需要权限gift.admin"};
					sender.sendMessage(strs);
				} else 
					if (args[1].equalsIgnoreCase("list")) {
						if (sender.hasPermission("gift.admin")) {
							for (String key:gifts.keySet()) {
								sender.sendMessage(key);
							}
						} else sender.sendMessage("无权操作");
					}
				}
			if (args.length == 3) {
				if (args[1].equals("all-add")) {
					if (sender.hasPermission("gift.admin")) {
						if (sender instanceof Player) {
							Player p = (Player)sender;
							ArrayList<Integer> tmp = new ArrayList<>();
							for (ItemStack item:p.getInventory()) {
								tmp.add(addItem(item));
							}
							addGifts(args[2], tmp);
							p.sendMessage("成功创建礼包 " + args[2]);
							
						} else info("控制台不支持");
					} else sender.sendMessage("无权操作");
				}
			}
		}
		return false;
	}
	private void addGifts(String name, ArrayList<Integer> goods) {
		gifts.put(name, goods);
		String goodIds = "";
		for (int i:goods) {
			goodIds = goodIds + i + ",";
		}
		sql.doSql("INSERT INTO "+MyConfig.tableName 
				  + "("
				  + "`giftname`,"
				  + "`id`"
				  + ")"
				  + "VALUES"
				  + "("
				  + name + ","
				  + goodIds
				  + ")");
	}
	private int addItem(ItemStack item) {
		items.put(++itemSize, item);
		sql.doSql("INSERT INTO "+MyConfig.itemTableName 
				  + "("
				  + "`id`,"
				  + "`map`"
				  + ")"
				  + "VALUES"
				  + "("
				  + itemSize + ","
				  + Idecode.doZip(item)
				  + ")");
		return itemSize;
	}

	
}
