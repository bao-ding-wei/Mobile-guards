package com.boyzhang.projectmobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DbAddress {

	private static String dBaddressPath = "data/data/com.boyzhang.projectmobilesafe/files/address.db";

	/**
	 * 获取归属地
	 * 
	 * @param phoneNum
	 *            手机号
	 * @return 归属地信息
	 */
	public static String getaddress(String phoneNum) {
		String address = "null";
		if (phoneNum.matches("^1[3-8]\\d{9}$")) {
			SQLiteDatabase database = SQLiteDatabase.openDatabase(
					dBaddressPath, null, SQLiteDatabase.OPEN_READONLY);
			String sql = "select location from data2 where id = (select outkey from data1 where id = ?)";
			Cursor rawQuery = database.rawQuery(sql,
					new String[] { phoneNum.substring(0, 7) });
			if (rawQuery.moveToNext()) {
				address = rawQuery.getString(rawQuery
						.getColumnIndex("location"));
			}
			// 关闭资源
			rawQuery.close();
			database.close();
		}

		return address;
	}
}
