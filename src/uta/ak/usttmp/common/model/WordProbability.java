/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uta.ak.usttmp.common.model;

/**
 *
 * @author zhangcong
 */
public class WordProbability {
    
    private String word;
    private double probability;

    /**
     * @return the probability
     */
    public double getProbability() {
        return probability;
    }

    /**
     * @param probability the probability to set
     */
    public void setProbability(double probability) {
        this.probability = probability;
    }

    /**
     * @return the word
     */
    public String getWord() {
        return word;
    }

    /**
     * @param word the word to set
     */
    public void setWord(String word) {
        this.word = word;
    }
    
    
    @Override
    public int hashCode(){
        
        char[] charArr=this.getWord().toCharArray();
        
        StringBuffer asciiArr=new StringBuffer();
        for(char c : charArr){
            asciiArr.append(String.valueOf((int)c));
        }
        
        return Integer.parseInt(asciiArr.toString());
        
    }
    
    @Override
    public boolean equals(Object wp) {
        if(! (wp instanceof WordProbability)){
            throw new RuntimeException("Only can be compared with the same Class!");
        }
        
        return (this.getWord().equals(((WordProbability)wp).getWord()));
    }
    
}
