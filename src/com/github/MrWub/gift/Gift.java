package com.github.MrWub.gift;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
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
			return;
		}
		
		sql=new Isql(this);
		if (sql.init())info("Loading SQL... "); else {
			info("Loading SQL... Error! Disable!");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		info("Loading Items... ");
		initItems();
		info("Loading Gifts... ");
		initGifts(); 
		if(!getDataFolder().exists()) getDataFolder().mkdir(); 
	}
	private void initItems() {
		Iresult result = sql.doSqlQuery("SELECT * FROM " + MyConfig.itemTableName);
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
		Iresult result = sql.doSqlQuery("SELECT * FROM " + MyConfig.tableName);
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
			if (args.length == 0) return false;
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("help")) {
					String[] strs ={"gift命令帮助",
									"/gift list 查看礼包列表 需要权限gift.admin",
									"/gift get X 获取礼包X 需要权限gift.admin或gift.get.X",
									"/gift del X 删除礼包X 需要权限gift.admin",
									"/gift all-add X用你背包里的所有物品创建一个X礼包 需要权限gift.admin",
									"/gift give X Y 给X玩家一个Y礼包 需要权限gift.admin"};
					sender.sendMessage(strs);
				} else 
				if (args[0].equalsIgnoreCase("list")) {
					if (sender.hasPermission("gift.admin")) {
						for (String key:gifts.keySet()) {
							sender.sendMessage(key);
						}
					} else sender.sendMessage("无权操作");
				}
			}
			if (args.length == 2) {
				if (args[0].equals("all-add")) {
					if (sender.hasPermission("gift.admin")) {
						if (sender instanceof Player) {
							Player p = (Player)sender;
							ArrayList<Integer> tmp = new ArrayList<Integer>();
							for (ItemStack item:p.getInventory()) {
								if (item != null) tmp.add(addItem(item));
							}
							addGifts(args[1], tmp);
							p.sendMessage("成功创建礼包 " + args[1]);
							
						} else info("控制台不支持");
					} else sender.sendMessage("无权操作");
				} else 
				if (args[0].equalsIgnoreCase("get")) {
					if (sender.hasPermission("gift.admin") || sender.hasPermission("gift.get."+args[1])){
						if (sender instanceof Player) {
							 Player p = (Player)sender;
							 if (gifts.containsKey(args[1])) {
								 giveGift(p, args[1]);
							 } else sender.sendMessage("礼包不存在");
						}else sender.sendMessage("控制台不支持");
					}else sender.sendMessage("无权操作");
				}
				if (args[0].equalsIgnoreCase("del")) {
					if (sender.hasPermission("gift.admin")) {
						if (gifts.containsKey(args[1])) {
							delGifts(args[1]);
							sender.sendMessage("已删除礼包" + args[1]);
						} else sender.sendMessage("礼包不存在");
					} else sender.sendMessage("无权操作");
				}
			}
			if (args.length == 3) {
				if (args[0].equalsIgnoreCase("give")) {
					if (sender.hasPermission("gift.admin")) {
						Player p = Bukkit.getPlayer(args[1]);
						if (p != null) {
							if (gifts.containsKey(args[2])) {
								giveGift(p, args[2]);
								sender.sendMessage("已将礼包" + args[2] + "发送至" + args[1]);
							} else sender.sendMessage("礼包不存在");
						} else sender.sendMessage("玩家不存在或不在线");
					} else sender.sendMessage("无权操作");
				}
			}
		}
		return true;
	}
	private void giveGift(Player p, String name) {
		 p.sendMessage("获得礼包 " + name);
		 for (int id:gifts.get(name)) {
			 if (!(p.getInventory().addItem(items.get(id))).isEmpty()) {
				p.sendMessage("背包空间不足，物品掉落在地");
				p.getWorld().dropItem(p.getLocation(), items.get(id));
			 };
		 }
		
	}
	private void delGifts(String name) {
		for (int a:gifts.get(name)) {
			delItem(a);
		}
		gifts.remove(name);
		sql.doSql("DELETE FROM gifts WHERE giftname=" + name);
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
				  + "'" + name + "'" + ","
				  + "'" + goodIds + "'"
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
				  + "'" + Idecode.doZip(item) + "'" 
				  + ")");
		return itemSize;
	}
	private void delItem(int id) {
		items.remove(id);
		sql.doSql("DELETE FROM items WHERE id=" + id);
	}
	
}
