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
	private Map<String, ItemStack[]> gifts = new HashMap<String, ItemStack[]>();
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
		info("Loading Gifts... ");
		initGifts(); 
		if(!getDataFolder().exists()) getDataFolder().mkdir(); 
	}
	
	private void initGifts() {
		Iresult result = sql.doSqlQuery("SELECT * FROM " + MyConfig.tableName);
		for (int i = 1; i<=result.getRowCount(); i++) {
			ArrayList<String> row = result.getRow(i);
			gifts.put(row.get(1), Idecode.redo(row.get(2)));
		}
	}
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
		if (label.equalsIgnoreCase("gift")) {
			if (args.length == 0) return false; else 
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("help")) {
					String[] strs ={"gift命令帮助",
									"/gift list 查看礼包列表 需要权限gift.admin",
									"/gift get X 获取礼包X 需要权限gift.admin或gift.get.X",
									"/gift del X 删除礼包X 需要权限gift.admin",
									"/gift all-add X用你背包里的所有物品创建一个X礼包 需要权限gift.admin",
									"/gift give X Y 给X玩家一个Y礼包 需要权限gift.admin"};
					sender.sendMessage(strs);
					return true;
				} else 
				if (args[0].equalsIgnoreCase("list")) {
					if (sender.hasPermission("gift.admin")) {
						for (String key:gifts.keySet()) {
							sender.sendMessage(key);
						}
					} else sender.sendMessage("无权操作");
					return true;
				}
			} else 
			if (args.length == 2) {
				if (args[0].equals("all-add")) {
					if (sender.hasPermission("gift.admin")) {
						if (sender instanceof Player) {
							Player p = (Player)sender;
							ItemStack[] tmp = new ItemStack[100];
							int i = 0;
							for (ItemStack item:p.getInventory()) {
								if (item != null) tmp[i]=item;
								i++;
							}
							addGifts(args[1], tmp);
							p.sendMessage("成功创建礼包 " + args[1]);
						} else info("控制台不支持");
					} else sender.sendMessage("无权操作");
					return true;
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
					return true;
				}
				if (args[0].equalsIgnoreCase("del")) {
					if (sender.hasPermission("gift.admin")) {
						if (gifts.containsKey(args[1])) {
							delGifts(args[1]);
							sender.sendMessage("已删除礼包" + args[1]);
						} else sender.sendMessage("礼包不存在");
					} else sender.sendMessage("无权操作");
					return true;
				}
			} else 
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
					return true;
				}
			}
		}
		return false;
	}
	private void giveGift(Player p, String name) {
		 p.sendMessage("获得礼包 " + name);
		 for (ItemStack item:gifts.get(name)) {
			 if (!(p.getInventory().addItem(item).isEmpty())) {
				p.sendMessage("背包空间不足，物品掉落在地");
				p.getWorld().dropItem(p.getLocation(), item);
			 };
		 }
		
	}
	private void delGifts(String name) {
		gifts.remove(name);
		sql.doSql("DELETE FROM gifts WHERE giftname=" + name);
	}
	private void addGifts(String name, ItemStack[] goods) {
		gifts.put(name, goods);
		String json = Idecode.doZip(goods);
		sql.doSql("INSERT INTO "+MyConfig.tableName 
				  + "("
				  + "`giftname`,"
				  + "`json`"
				  + ")"
				  + "VALUES"
				  + "("
				  + "'" + name + "'" + ","
				  + "'" + json + "'"
				  + ")");
	}
	
}
