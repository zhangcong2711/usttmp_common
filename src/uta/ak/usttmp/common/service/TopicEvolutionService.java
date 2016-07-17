/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uta.ak.usttmp.common.service;

import java.util.List;
import uta.ak.usttmp.common.model.Topic;
import uta.ak.usttmp.common.model.EvolutionRelationship;

/**
 *
 * @author zhangcong
 */
public interface TopicEvolutionService {
    
    public List<EvolutionRelationship> 
               calculateTopicEvolutionRelationships(long miningTaskId,
                                                    int preTopicSeq,
                                                    int nextTopicSeq) throws Exception;
    
    public List<EvolutionRelationship> 
               calculateTopicEvolutionRelationships( List<Topic> preTopics,
                                                     List<Topic> nextTopics) throws Exception;
    
    
    public double getSimilarity(Topic tp1, Topic tp2);
    
}
