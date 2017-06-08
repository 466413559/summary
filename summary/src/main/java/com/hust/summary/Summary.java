package com.hust.summary;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.summary.TextRankSentence;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;

/**
 * 对给定文档进行摘要
 * 文档格式为（标题\n段落\n段落···）
 * @author Jack
 *
 */
public class Summary {
	/**
	 * 文档存放路径
	 */
	private String path;
	
	/**
	 * textRank权重的平衡因子
	 * 默认为1
	 */
	private double k_rank = 1;
	
	/**
	 * 其他因子的平衡因子
	 * 默认为1
	 */
	private double k_extra = 1;
	
	/**
	 * 标题权重的调节因子
	 * 默认为0.5
	 */
	private double k_title;
	
	/**
	 * 位置权重的调节因子
	 * 默认为0.25
	 */
	private double k_position;
	
	/**
	 * 线索的调节因子
	 * 默认为0.25
	 */
	private double k_clue;
	
	/**
	 * （段落（句子））文档
	 */
	private List<String[]> doc;
	
	/**
	 * 分词后的Title
	 */
	private List<String> title;
	/**
	 * （句子）文档
	 */
	private List<String> sentences;
	/**
	 * (句子(单词))文档
	 */
	private List<List<String>> docs;
	/**
	 * 文档正文包含的句子个数
	 */
	private int D;
	
	/**
	 * 其他因子权重
	 */
	private double[] extra_weight;
	
	public Summary(String path){
		List<String> doc = new LinkedList<>();
		//读取文档
		/**
		 * 读取文档
		 */
		//获取标题
		List<Term> termList = StandardTokenizer.segment(doc.remove(0).toCharArray());
		this.title = new LinkedList<>();
        for (Term term : termList)
        {
            if (CoreStopWordDictionary.shouldInclude(term))
            {
                this.title.add(term.word);
            }
        }
		//初始化文档内容
		this.doc = new LinkedList<>();	
		this.sentences = new LinkedList<>();
		for (String string : doc) {
			String[] sentences = string.split("[。|？|！]");
			this.doc.add(sentences);
			for (String string2 : sentences) {
				this.sentences.add(string2);
			}
		}
		this.D = this.sentences.size();	
		this.docs = convertSentenceListToDocument(this.sentences);
	}
	
	/**
	 * 获取位置权重
	 * @return
	 */
	private double[] getPositionWeight(){
		PositionWeight pw = new PositionWeight(doc,D);		
		return pw.getPosition_weight();
	}
	
	/**
	 * 获取标题权重
	 */
	private double[] getTitleWeight(){
		TitleWeight tw = new TitleWeight(docs,title);
		return tw.getTitleWeight();
	}
	
	/**
	 * 获取线索权重
	 * 默认配置
	 * @return
	 */
	private double[] getClueWeight(){
		ClueWeight cw = new ClueWeight(sentences, null);
		cw.caculate();
		return cw.getClueWeight();
	}
	
	/**
	 * 使用自定义线索词典获取线索权重
	 * @param path
	 * @return
	 */
	private double[] getClueWeight(String path){
		ClueWeight cw = new ClueWeight(sentences, path);
		cw.caculate();
		return cw.getClueWeight();
	}
	/**
	 * 以追加或覆盖的方式添加线索词
	 * @param clue 线索词集合
	 * @param flag true为追加的方式，false为覆盖的方式
	 * @return
	 */
	private double[] getClueWeight(List<String> clue,boolean flag){
		ClueWeight cw = new ClueWeight(sentences, null);
		if(flag){
			cw.addClueWord(clue);
		}else{
			cw.resetClueWord(clue);
		}
		cw.caculate();
		return cw.getClueWeight();
	}
	/**
     * 将句子列表转化为文档
     *
     * @param sentenceList
     * @return
     */
    private static List<List<String>> convertSentenceListToDocument(List<String> sentenceList)
    {
        List<List<String>> docs = new ArrayList<List<String>>(sentenceList.size());
        for (String sentence : sentenceList)
        {
            List<Term> termList = StandardTokenizer.segment(sentence.toCharArray());
            List<String> wordList = new LinkedList<String>();
            for (Term term : termList)
            {
                if (CoreStopWordDictionary.shouldInclude(term))
                {
                    wordList.add(term.word);
                }
            }
            docs.add(wordList);
        }
        return docs;
    }

	public void setK_rank(double k_rank) {
		this.k_rank = k_rank;
	}

	public void setK_extra(double k_extra) {
		this.k_extra = k_extra;
	}

	public void setK_title(double k_title) {
		this.k_title = k_title;
	}

	public void setK_position(double k_position) {
		this.k_position = k_position;
	}

	public void setK_clue(double k_clue) {
		this.k_clue = k_clue;
	}
    
    
    
}
