/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uta.ak.usttmp.common.model;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 *
 * @author zhangcong
 */
public class Topic {
    
    private long id;
    private String name;
    private Set<WordProbability> wordProbabilityMaps;
    private String remark;
    private int seq;
    private long miningTaskId;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark the remark to set
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the wordProbabilityMaps
     */
    public Set<WordProbability> getWordProbabilityMaps() {
        return wordProbabilityMaps;
    }

    /**
     * @param wordProbabilityMaps the wordProbabilityMaps to set
     */
    public void setWordProbabilityMaps(Set<WordProbability> wordProbabilityMaps) {
        this.wordProbabilityMaps = wordProbabilityMaps;
    }
    
    public Set<String> getWordSet(){
        Set<String> wSet=new CopyOnWriteArraySet<String>();
        
        for(WordProbability wp : this.wordProbabilityMaps){
            wSet.add(wp.getWord());
        }
        
        return wSet;
    }
    
    public void addWordProbability(WordProbability wp){
        
        if(null!=this.wordProbabilityMaps){
            this.wordProbabilityMaps.add(wp);
        }else{
            this.wordProbabilityMaps=new CopyOnWriteArraySet<WordProbability>();
            this.wordProbabilityMaps.add(wp);
        }
    }
    
    public void addWordProbability(String word, double weight){
        
        WordProbability wp=new WordProbability();
        wp.setWord(word);
        wp.setProbability(weight);
        this.addWordProbability(wp);
    }
    
    public WordProbability getWordProbability(String word){
        
        Iterator<WordProbability> i=wordProbabilityMaps.iterator();
        for(;i.hasNext();){
            WordProbability wp=i.next();
            if(word.equals(wp.getWord())){
                return wp;
            }else{
                continue;
            }
        }
        return null;
    }

    @Override
    public String toString(){
        
        StringBuffer sb=new StringBuffer();
        Iterator<WordProbability> i=wordProbabilityMaps.iterator();
        for(;i.hasNext();){
            WordProbability wp=i.next();
            sb.append(" " + wp.getWord() + ":" + String.format("%.5f", wp.getProbability())+" ,");
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }

    /**
     * @return the heat
     */
    public double getHeat() {
        
        double heat=0;
        for(WordProbability wp : wordProbabilityMaps){
            heat+=wp.getProbability();
        }
        return heat;
    }

    /**
     * @return the seq
     */
    public int getSeq() {
        return seq;
    }

    /**
     * @param seq the seq to set
     */
    public void setSeq(int seq) {
        this.seq = seq;
    }

    /**
     * @return the miningTaskId
     */
    public long getMiningTaskId() {
        return miningTaskId;
    }

    /**
     * @param miningTaskId the miningTaskId to set
     */
    public void setMiningTaskId(long miningTaskId) {
        this.miningTaskId = miningTaskId;
    }
}
