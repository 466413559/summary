package com.hust.summary;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class Test {
	public static void main(String[] args) {
		String docPath = "";
		String summaryPath = "";
		List<String> filePath = FileIO.getFileName(docPath);
		for (String string : filePath) {
			Summary s = new Summary(string);
			s.simpleSummary();
			List<String> list = s.getTopSentences();
			writeFile(summaryPath+string, list);
		}
		
	}
	
	private static void writeFile(String path,List<String> list){
		File file = new File(path);
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println(path+"文件创建失败");
			}
		}
		try {
			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
			BufferedWriter bw = new BufferedWriter(osw);
			for (String string : list) {
				bw.write(string+"\r\n");
			}
			bw.close();
			osw.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			System.out.println(path+"文件编码写入错误");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println(path+"文件未找到");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(path+"文件写入出错");
		}		
	}
}
