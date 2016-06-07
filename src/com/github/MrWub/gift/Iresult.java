package com.github.MrWub.gift;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Iresult {
	private ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>(),
										 ho = new ArrayList<ArrayList<String>>();
	@SuppressWarnings("unchecked")
	public Iresult(ResultSet res) {
		list.add(new ArrayList<String>());
		ho.add(new ArrayList<String>());
		try {
			if (res.isAfterLast()) return;
			ArrayList<String> tmp = new ArrayList<String>();
			while (res.next()) {
				tmp.clear();
				tmp.add(new String());
				for (int i=1; i<=res.getMetaData().getColumnCount(); i++) {
					tmp.add(res.getString(i));
				}
				list.add((ArrayList<String>)tmp.clone());
			};
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public int getRowCount() {
		return list.size()-1;
	}
	public ArrayList<ArrayList<String>> getAllTable() {
		return list;
	}
	public ArrayList<String> getRow(int row){
		return list.get(row);
	}
}
