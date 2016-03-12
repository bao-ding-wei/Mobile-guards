package com.boyzhang.projectmobilesafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 读取流的工具
 * 
 * @author HaiFeng
 * 
 */

public class StreamUtils {

	public static String readToString(InputStream in) throws IOException {

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int readLength = 0;
		byte[] buffer = new byte[1024];
		while ((readLength = in.read(buffer)) != -1) {
			out.write(buffer, 0, readLength);
		}

		// 释放资源
		in.close();
		out.close();

		return out.toString();
	}
}
