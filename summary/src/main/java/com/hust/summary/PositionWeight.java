package com.hust.summary;

import java.util.ArrayList;
import java.util.List;

/**
 * 段首句权重+1，第2句和段尾句权重+1
 * @author Jack
 *
 */
public class PositionWeight {
	/**
	 * 位置权重
	 */
	private double[] position_weight;
	/**
	 * （段落）已段落为分割的文本
	 */
	private List<String[]> doc;	
	
	/**
	 * 初始化
	 * @param doc (段落（句子）)文档
	 * @param D 句子总数
	 */
	public PositionWeight(List<String[]> doc,int D) {
		this.doc = doc;		
		this.position_weight = new double[D];
		caculate(doc);
	}
	
	/**
	 * 计算位置权重
	 * @param sentences (段落(句子))分隔后的文档
	 */
	private void caculate(List<String[]> sentences){
		int index = 0;
		for (String[] strings : sentences) {
			int size = strings.length;
			//段首句权重+1
			position_weight[index] += 1;
			//第2句权重+0.5
			if(size >= 2){
				position_weight[index+1] += 0.5;
			}
			//段尾句权重+0.5
			position_weight[index+size-1] += 0.5;
			index += size;
		}
		//首段和尾段所有句子权重+1和0.5
		String[] first = sentences.get(0);
		for(int i = 0; i < first.length; i++){
			position_weight[i] += 1;
		}
		if(sentences.size() >= 2){
			int n = sentences.size()-1;
			String[] last = sentences.get(n);
			for(int i = 0; i< last.length; i++){
				position_weight[index-last.length+i] += 0.5;
			}
		}
		
	}

	public double[] getPosition_weight() {
		return position_weight;
	}	
}
