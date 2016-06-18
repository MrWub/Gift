package com.github.MrWub.gift;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONObject;

import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public class Idecode {

	@SuppressWarnings("unchecked")
	public static List<ItemStack> redo(String json) {
	    /*byte[] data = Base64.getMimeDecoder().decode(imbyte);
		
		BukkitObjectInputStream ins = null;
		try {
			ins = new BukkitObjectInputStream( new ByteArrayInputStream( data ));
		} catch (IOException e) {
			e.printStackTrace();
		}
		ItemStack im = null;
		try {
			im = (ItemStack)ins.readObject();
			ins.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return im;*/
		List<ItemStack> rs = new ArrayList<ItemStack>();
    	JSONObject jobj = JSONObject.fromObject(json);
		for (Object i : jobj.keySet()) {
			System.out.println(jobj.get(i));
			JSONObject itemJobj = JSONObject.fromObject(jobj.get(i));
			Map<String, Object> map = new HashMap<String,Object>();
	    	for (Object s : itemJobj.keySet()) {
	    		String str = s.toString();
	    		ItemMeta meta = null;
	    		if (str.equalsIgnoreCase("meta")) {
		    		String va = (String)itemJobj.get(s);
		    		try {
		    			BukkitObjectInputStream ins = new BukkitObjectInputStream(new ByteArrayInputStream(
		    					Base64.getUrlDecoder().decode(va)));
		    			meta = (ItemMeta)ins.readObject();
		    			ins.close();
		    		} catch (IOException e) {
		    			e.printStackTrace();
		    		} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
		    		map.put(str, meta);
	    		} else 
	    		map.put(str, itemJobj.get(s));
	    	}
	    	rs.add(ItemStack.deserialize(map));
		}
		return rs;
	}
	
	public static String doZip(List<ItemStack> items) {
		/*ByteArrayOutputStream bop = new ByteArrayOutputStream();
		try {
			BukkitObjectOutputStream out = new BukkitObjectOutputStream(bop);
			out.writeObject(item);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		byte[] data = bop.toByteArray();
		return Base64.getMimeEncoder().encodeToString(data);*/
		JSONObject rs = new JSONObject();
		int i = 0;
		for (ItemStack item : items) {
			Map<String, Object> map = item.serialize();
			
			for (Entry<String, Object> entry: map.entrySet()) {
				Object obj = entry.getValue();
				if (obj instanceof ItemMeta) {
					String mStr = null;
		    		try {
			    		ByteArrayOutputStream bop = new ByteArrayOutputStream();
		    			BukkitObjectOutputStream out = new BukkitObjectOutputStream(bop);
		    			out.writeObject(obj);
		    			mStr = Base64.getUrlEncoder().encodeToString(bop.toByteArray());
		    			out.close();
		    		} catch (IOException e) {
		    			e.printStackTrace();
		    		}
					map.replace(entry.getKey(), mStr);
				}
			}
			JSONObject jobj = JSONObject.fromObject(map);
			rs.put("item" + i, jobj.toString());
			i++;
		}
		return rs.toString();
	}

}
