package com.hust.summary;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 标题权重计算，包含标题词语数达到一定阈值，权重设为1，否则权重为0
 * @author Jack
 *
 */
public class TitleWeight {
	/**
	 * 标题权重
	 */
	private double[] title_weight;
	
	/**
	 * 分词后的标题
	 */
	private List<String> title;
	
	/**
	 * 分词后的（句子（单词））文档
	 */
	private List<List<String>> docs;
	
	/**
	 * 阈值
	 * 句子包含标题中的词语个数占标题总词语个数比
	 * 默认为0.6
	 */
	private double threshold = 0.6;
	
	//初始化
	/**
	 * 初始化
	 * @param docs 分词后的（句子（单词））文档
	 * @param title 分词后的标题
	 */
	public TitleWeight(List<List<String>> docs, List<String> title){
		this.docs = docs;
		this.title = title;
		int d = docs.size();
		title_weight = new double[d];
		System.out.print("标题：");
		for (String string : title) {
			System.out.print(string+" ");
		}
		System.out.println();
	}
	
	//计算标题权重
	/**
	 * 计算标题权重，超过 阈值的设为1，否则为0
	 */
	public void caculate(){
		int total = title.size();
		int i = 0;
		for (List<String> list : docs) {
			int count = 0;
			List<String> t = new LinkedList<>();
			t.addAll(title);
			for (String string : list) {
				if(t.size()<=0){
					break;
				}
				int index = t.indexOf(string);
				if(index >= 0){
					t.remove(index);
					count++;
				}
			}
			System.out.println("第"+i+"句包含标题词语的个数："+count);
			if((count*1.0)/total >= threshold){
				title_weight[i] = 1;
			}
			i++;			
		}
	}
	
	//设置阈值
	/**
	 * 设置阈值
	 * @param threshold 阈值 默认为0.6
	 */
	public void setThresHold(double threshold){
		this.threshold = threshold;
	}
	
	//获取标题权重
	public double[] getTitleWeight(){
		return title_weight;
	}
}
