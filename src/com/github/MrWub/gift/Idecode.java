package com.github.MrWub.gift;

import java.util.HashMap;
import java.util.Map;
import net.sf.json.JSONObject;
import org.bukkit.inventory.ItemStack;

public class Idecode {

	public static ItemStack[] redo(String json) {
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
		ItemStack[] rs = new ItemStack[100];
    	JSONObject jobj = JSONObject.fromObject(json);
    	int size = 0;
		for (Object i : jobj.keySet()) {
			JSONObject itemJobj = JSONObject.fromObject(jobj.get(i));
			Map<String, Object> map = new HashMap<String, Object>();
	    	for (Object s : itemJobj.keySet()) {
	    		String str = s.toString();
	    		map.put(str, itemJobj.get(s));
	    	}
	    	rs[size] = ItemStack.deserialize(map);
	    	size++;
		}
		return rs;
	}
	
	public static String doZip(ItemStack[] items) {
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
			JSONObject jobj = JSONObject.fromObject(map);
			rs.put("item" + i, jobj.toString());
			i++;
		}
		return rs.toString();
	}

}
