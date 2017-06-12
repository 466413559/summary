package com.hust.summary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileIO {	
	/**
	 * 通过事件文件夹拿到事件下面的所有小文件名称
	 */
	public static List<String> getFileName(String rootPath) {
		List<String> list = new ArrayList<>();
		File f = new File(rootPath);
		if (!f.exists()) {
			System.out.println(rootPath + " not exists");
			return null;
		}
		File fa[] = f.listFiles();
		for (int i = 0; i < fa.length; i++) {
			File fs = fa[i];
			if (fs.isFile()) {
				list.add(fs.getName());
			}
		}
		return list;
	}

	/**
	 * 读取文件内容
	 * 
	 * @throws IOException
	 */
	public static List<List<String>> readFile(String rootPath)
			throws IOException {
		InputStreamReader read = null;
		BufferedReader reader = null;
		List<List<String>> list = new ArrayList<List<String>>();
		List<String> fileList = getFileName(rootPath);
		for (String fileName : fileList) {
			List<String> l = new ArrayList<String>();
			String pathName = rootPath + "/" + fileName;
			read = new InputStreamReader(
					new FileInputStream(new File(pathName)), "UTF-8");
			reader = new BufferedReader(read);
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.replace((char) 12288, ' ');
				line = line.trim();
				if (!line.isEmpty() && !line.equals("") && !line.equals(' ')
						&& !line.equals("	") && !line.equals("  ")
						&& !line.equals("   ") && !line.equals("    ")
						&& !line.equals("     ")) {
					l.add(line);
				}
			}
			list.add(l);
		}
		return list;
	}

	/**
	 * 一行一行读取文件内容
	 * 
	 * @param 文件路径
	 * @return 返回List<String>集合
	 */
	public static List<String> readFromFile(String path) {
		List<String> list = new ArrayList<String>();
		FileReader reader = null;
		BufferedReader br = null;
		try {
			reader = new FileReader(path);
			br = new BufferedReader(reader);
			String line = null;
			while ((line = br.readLine()) != null) {
				line = line.replace((char) 12288, ' ');
				line = line.trim();
				if (!line.isEmpty() && !line.equals("") && !line.equals(' ')
						&& !line.equals("	") && !line.equals("  ")
						&& !line.equals("   ") && !line.equals("    ")
						&& !line.equals("     ")) {
				//	System.out.println(line);
					list.add(line);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return list;
	}
}
