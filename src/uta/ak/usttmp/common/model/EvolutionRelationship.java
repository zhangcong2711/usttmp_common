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
public class EvolutionRelationship {
    
    private Topic preTopic;
    private Topic nextTopic;
    private double similarity;
    private int rankAgainstPreTopicInNextGroup;
    private int rankAgainstNextTopicInPreGroup;

    /**
     * @return the preTopic
     */
    public Topic getPreTopic() {
        return preTopic;
    }

    /**
     * @param preTopic the preTopic to set
     */
    public void setPreTopic(Topic preTopic) {
        this.preTopic = preTopic;
    }

    /**
     * @return the nextTopic
     */
    public Topic getNextTopic() {
        return nextTopic;
    }

    /**
     * @param nextTopic the nextTopic to set
     */
    public void setNextTopic(Topic nextTopic) {
        this.nextTopic = nextTopic;
    }

    /**
     * @return the similarity
     */
    public double getSimilarity() {
        return similarity;
    }

    /**
     * @param similarity the similarity to set
     */
    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    /**
     * @return the rankAgainstPreTopicInNextGroup
     */
    public int getRankAgainstPreTopicInNextGroup() {
        return rankAgainstPreTopicInNextGroup;
    }

    /**
     * @param rankAgainstPreTopicInNextGroup the rankAgainstPreTopicInNextGroup to set
     */
    public void setRankAgainstPreTopicInNextGroup(int rankAgainstPreTopicInNextGroup) {
        this.rankAgainstPreTopicInNextGroup = rankAgainstPreTopicInNextGroup;
    }

    /**
     * @return the rankAgainstNextTopicInPreGroup
     */
    public int getRankAgainstNextTopicInPreGroup() {
        return rankAgainstNextTopicInPreGroup;
    }

    /**
     * @param rankAgainstNextTopicInPreGroup the rankAgainstNextTopicInPreGroup to set
     */
    public void setRankAgainstNextTopicInPreGroup(int rankAgainstNextTopicInPreGroup) {
        this.rankAgainstNextTopicInPreGroup = rankAgainstNextTopicInPreGroup;
    }

    
    
    
    
}
