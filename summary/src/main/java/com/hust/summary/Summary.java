package com.hust.summary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

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
	
	private double[] total_weight;
	
	public Summary(String path){
		List<String> doc = new LinkedList<>();
		//读取文档
		/**
		 * 读取文档
		 */
		doc = FileIO.readFromFile(path);
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
		
		extra_weight = new double[D];
		total_weight =  new double[D];
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
	
	private double[] getTextRankWeight(){
		TextRankSentence ts = new TextRankSentence(docs);
		return ts.getVertex();
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
	 * 采用默认设置对文章排序摘要
	 */
	public void simpleSummary(){
		double[] title_weight = getTitleWeight();
		double[] position_weight = getPositionWeight();
		double[] clue_weight = getClueWeight();
		double[] textRank_weight = getTextRankWeight();
		int i = 0;
		//计算其他因子的权重
		for (double d : extra_weight) {
			extra_weight[i] = k_position*position_weight[i]+k_clue*clue_weight[i]+k_title*title_weight[i];
			total_weight[i] = k_extra*extra_weight[i]+k_rank*textRank_weight[i];
			++i;
		}
	}
	/**
	 * 设置选用哪些因子来摘要，并指定对应的调整因子，若为null则表示不要该种因子（标题因子，位置因子，线索因子的和推荐为1）
	 * @param k_title 标题因子，为null则表示不考虑该因子的影响
	 * @param k_position 位置因子，为null则表示不考虑该因子的影响
	 * @param k_clue 线索因子，为null则表示不考虑该因子的影响
	 * @param clue 线索词，若为null则表示使用默认线索词
	 * @param flag 线索词的加载方式，ture为追加的方式，false为覆盖的方式
	 * @param k_extra 影响因子的调整参数   若为null则使用默认参数
	 * @param k_rank textRank的调整参数  若为null则使用默认参数
	 */
	public void diySummary(Double k_title,Double k_position,Double k_clue,List<String> clue,Boolean flag,Double k_extra,Double k_rank){
		double[] title_weight = {0};
		double[] position_weight = {0};
		double[] clue_weight = {0};
		if(null != k_title){
			setK_title(k_title);
			title_weight = getTitleWeight();
		}
		if(null != k_position){
			setK_position(k_position);
			position_weight = getPositionWeight();
		}
		if(null!= k_clue){
			setK_clue(k_clue);
			if(null != clue){
				clue_weight = getClueWeight(clue,flag);
			}else{
				clue_weight = getClueWeight();
			}
		}		
		if(null != k_extra){
			setK_extra(k_extra);
		}
		if(null != k_rank){
			setK_rank(k_rank);
		}
		
		double[] textRank_weight = getTextRankWeight();
		
		int i = 0;
		//计算其他因子的权重
		for (double d : extra_weight) {
			extra_weight[i] = k_position*position_weight[i]+k_clue*clue_weight[i]+k_title*title_weight[i];
			total_weight[i] = k_extra*extra_weight[i]+k_rank*textRank_weight[i];
			++i;
		}
	}
	
	/**
	 * 获取前num重要的句子
	 * @param num
	 * @return
	 */
	public List<String> getTopSentences(int num){
		TreeMap<Double, Integer> top = new TreeMap<Double, Integer>(Collections.reverseOrder());
		List<String> topSentences =  new LinkedList<>();
		for (int i = 0; i < D; ++i)
        {
            top.put(total_weight[i], i);
        }
		num = num<=top.size()?num:top.size();
		Iterator<Integer> it = top.values().iterator();
		for(int i =0 ; i < num; ++i){
			String str = sentences.get(it.next());
			topSentences.add(str);
		}
		return topSentences;
	}
	
	/**
	 * 根据文章长度自动选择要获取的句子个数
	 * @return
	 */
	public List<String> getTopSentences(){
		int num = 0;
		if(D <= 2){
			num = 1;
		}else if(D <= 7){
			num = 2;
		}else{
			num = 3;
		}		
		return getTopSentences(num);
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
