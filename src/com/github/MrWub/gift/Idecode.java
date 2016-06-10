package com.github.MrWub.gift;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.com.google.gson.JsonObject;
import net.sf.json.JSONObject;

import org.bukkit.inventory.ItemStack;

public class Idecode {

	public static ItemStack redo(String json) {
		
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
		Map<String, Object> map = new HashMap<String, Object>();
    	JSONObject jobj = JSONObject.fromObject(json);
    	for (Object s : jobj.keySet()) {
    		String str = s.toString();
    		map.put(str, jobj.get(s));
    	}
    	return ItemStack.deserialize(map);
	}
	
	public static String doZip(ItemStack item) {
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
		Map<String, Object> map = item.serialize();
		JSONObject jobj = JSONObject.fromObject(map);
		return jobj.toString();
	}

}
