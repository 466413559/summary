package com.hust.summary;

import java.util.ArrayList;
import java.util.List;

/**
 * 对文档计算线索权重  
 * 存在线索词则句子权重加一，没有则为零
 * 
 * 使用方法1.先new对象初始化，2.若有其他线索词可以选择添加，3.计算得分，4.获取得分
 * @author Jack
 *
 */
public class ClueWeight {
	/**
	 * 线索权重
	 */
	private double[] clue_weight;
	
	/**
	 * 默认线索词存放路径
	 */
	private static String cluePath = "data/dictionary/clue/clue.txt";
	
	/**
	 * （句子）文档
	 */
	private List<String> docs;
	
	/**
	 * 线索词
	 */
	private List<String> clue_word;
	
	//初始化
	/**
	 * 初始化
	 * @param docs 拆分后的（句子）文档
	 * @param path 线索词存放路径，若为null则读取默认线索词
	 */
	public ClueWeight(List<String> docs,String path){
		this.docs = docs;
		int d = docs.size();
		clue_weight = new double[d];
		//加载线索词
		if(null != path){
			cluePath = path;
		}		
		clue_word = loadClueWord(cluePath);
	}
	
	//加载线索词
	private List<String> loadClueWord(String path){
		List<String> list  = new ArrayList<>();
		//读取线索词
		list =FileIO.readFromFile(path);
		return list;
	}
	
	//加载线索词
	/**
	 * 以追加的方式添加自定义线索词
	 * @param word
	 */
	public void addClueWord(List<String> word){
		clue_word.addAll(word);
	}
	
	//加载线索词
	/**
	 * 以覆盖的方式添加线索词
	 * @param word
	 */
	public void resetClueWord(List<String> word){
		clue_word = word;
	}
	
	//计算线索权重
	public void caculate(){
		int count = 0;
		for (String sentence : docs) {
			for (String word : clue_word) {
				if(sentence.indexOf(word)<0){
					continue;
				}else{
					clue_weight[count] = 1;
					break;
				}
			}
			++count;
		}
	}
	
	//获取权重
	public double[] getClueWeight(){
		return this.clue_weight;
	}
	
}
