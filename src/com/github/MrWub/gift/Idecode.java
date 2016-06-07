package com.github.MrWub.gift;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public class Idecode {

	public static ItemStack redo(String imbyte) {
		byte[] data = Base64.getMimeDecoder().decode(imbyte);
		
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
		return im;

	}
	
	public static String doZip(ItemStack item) {
		ByteArrayOutputStream bop = new ByteArrayOutputStream();
		try {
			BukkitObjectOutputStream out = new BukkitObjectOutputStream(bop);
			out.writeObject(item);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		byte[] data = bop.toByteArray();
		return Base64.getMimeEncoder().encodeToString(data);
	}

}
