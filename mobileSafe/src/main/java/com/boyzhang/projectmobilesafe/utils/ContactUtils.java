package com.boyzhang.projectmobilesafe.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 获取系统联系人
 * 
 * @author HaiFeng
 * 
 */
public class ContactUtils {

	/**
	 * 获取系统联系人的号码和名字
	 * 
	 * @param context
	 *            上下文对象
	 * @return
	 */
	public static ArrayList<HashMap<String, String>> getContact(Context context) {

		ArrayList<HashMap<String, String>> contacts = new ArrayList<HashMap<String, String>>();

		ContentResolver contentResolver = context.getContentResolver();
		int i = 1;

		// 首先,从raw_contacts中读取联系人的id("contact_id")
		// 其次, 根据contact_id从data表中查询出相应的电话号码和联系人名称
		// 然后,根据mimetype来区分哪个是联系人,哪个是电话号码
		Uri rawContactsUri = Uri
				.parse("content://com.android.contacts/raw_contacts");
		Uri dataUri = Uri.parse("content://com.android.contacts/data");

		// 先从raw_contacts表中获取到联系人ID
		Cursor raw_contacts_cursor = contentResolver.query(rawContactsUri,
				new String[] { "contact_id" }, null, null, null);
		if (raw_contacts_cursor != null) {
			while (raw_contacts_cursor.moveToNext()) {
				String contact_id = raw_contacts_cursor
						.getString(raw_contacts_cursor
								.getColumnIndex("contact_id"));
				// 如果contact_id是null就跳过
				if (contact_id == null) {
					continue;
				}
				// 在根据raw_contacts中取出的contact_id去data表中取数据
				Cursor data_cursor = contentResolver
						.query(dataUri, new String[] { "mimetype", "data1" },
								"raw_contact_id = ?",
								new String[] { contact_id }, null);
				if (data_cursor != null) {
					HashMap<String, String> contact = new HashMap<String, String>();
					while (data_cursor.moveToNext()) {
						String mimeType = data_cursor.getString(data_cursor
								.getColumnIndex("mimetype"));
						String data1 = data_cursor.getString(data_cursor
								.getColumnIndex("data1"));
						if (data1 == null) {
							continue;
						}
						if ("vnd.android.cursor.item/phone_v2".equals(mimeType)) {
							contact.put("phone", data1);
						} else if ("vnd.android.cursor.item/name"
								.equals(mimeType)) {
							contact.put("name", data1 + (i++));
						}
					}
					if (!contact.isEmpty()) {
						contacts.add(contact);
					}
					data_cursor.close();
				}
			}
			raw_contacts_cursor.close();
		}
		return contacts;
	}
}
