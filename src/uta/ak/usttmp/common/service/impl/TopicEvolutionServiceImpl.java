/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uta.ak.usttmp.common.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import uta.ak.usttmp.common.dao.UsttmpDaoSupport;
import uta.ak.usttmp.common.exception.UsttmpProcessException;
import uta.ak.usttmp.common.model.Topic;
import uta.ak.usttmp.common.model.EvolutionRelationship;
import uta.ak.usttmp.common.model.WordProbability;
import uta.ak.usttmp.common.service.TopicEvolutionService;
import uta.ak.usttmp.common.service.TopicMiningService;
import uta.ak.usttmp.common.util.SetUtil;

/**
 *
 * @author zhangcong
 */
public class TopicEvolutionServiceImpl extends UsttmpDaoSupport implements TopicEvolutionService {
    
    protected static double similarityThreshold = 0.3;
//    protected static int filterPositionForPreGroup = 3;
    protected static double ratioThresholdToMaxSimilarity = 0.5;
    
    @Autowired
    private TopicMiningService topicMiningService;
    
    
    @Override
    public List<EvolutionRelationship> calculateTopicEvolutionRelationships(long miningTaskId, 
                                                                            int preTopicSeq, 
                                                                            int nextTopicSeq) throws Exception {
        
        //Clear existed topic evolution rela
        String clearSQL="DELETE " +
                        "FROM " +
                        "	c_topicevolutionrela " +
                        "WHERE " +
                        "	miningtask_id =? " +
                        "AND pre_topic_seq =? " +
                        "AND next_topic_seq =?";
        this.getJdbcTemplate().update(clearSQL,
                                      miningTaskId,
                                      preTopicSeq,
                                      nextTopicSeq);
        
        List<Topic> preTopics=topicMiningService.getTopics(miningTaskId, preTopicSeq);
        List<Topic> nextTopics=topicMiningService.getTopics(miningTaskId, nextTopicSeq);
        
        if(null==preTopics || preTopics.isEmpty()){
            
            UsttmpProcessException upe
                = new UsttmpProcessException(UsttmpProcessException.TYPE_CALC_EVO_RELA_EXCEPTION);
            throw upe;
        }
        if(null==nextTopics || nextTopics.isEmpty()){
            UsttmpProcessException upe
                = new UsttmpProcessException(UsttmpProcessException.TYPE_CALC_EVO_RELA_EXCEPTION);
            throw upe;
        }
        
        List<EvolutionRelationship> evRelaList = 
            calculateTopicEvolutionRelationships(preTopics, nextTopics);
        
        String insertSql="INSERT INTO `c_topicevolutionrela` (  " +
                            "	`pre_topic_id`,  " +
                            "	`next_topic_id`,  " +
                            "	`rank_against_pre_topic_in_next_group`,  " +
                            "	`rank_against_next_topic_in_pre_group`,  " +
                            "	`similarity`  ," +
                            "	`miningtask_id`  ," +
                            "	`pre_topic_seq`  ," +
                            "	`next_topic_seq`  " +
                            ")  " +
                            "VALUES  " +
                            "	(?, ?, ?, ?, ?, ?, ?, ?)";
        
        List<Object[]> argsList=new ArrayList<>();
        
        for (EvolutionRelationship er : evRelaList){
            Object[] objarr=new Object[]{er.getPreTopic().getId(),
                                         er.getNextTopic().getId(),
                                         er.getRankAgainstPreTopicInNextGroup(),
                                         er.getRankAgainstNextTopicInPreGroup(),
                                         er.getSimilarity(),
                                         miningTaskId,
                                         preTopicSeq,
                                         nextTopicSeq};
            argsList.add(objarr);
        }
        this.getJdbcTemplate().batchUpdate(insertSql, argsList);
        
        return evRelaList;
    }
    
    @Override
    @Transactional
    public List<EvolutionRelationship> calculateTopicEvolutionRelationships(List<Topic> preTopics, 
                                                                            List<Topic> nextTopics) throws Exception{
        
        
        List<EvolutionRelationship> evRelaList=this.getTopicEvolutionRelationships(preTopics, nextTopics);
        
        return evRelaList;
    }
    
    protected List<EvolutionRelationship> getTopicEvolutionRelationships( List<Topic> preTopics,
                                                                          List<Topic> nextTopics){
        
        //Generate EvolutionRelationship List
        List<EvolutionRelationship> erList = new ArrayList<EvolutionRelationship>();
        
        
        for(Topic preTp : preTopics){
            
            List<EvolutionRelationship> tempErList = new ArrayList<EvolutionRelationship>();
            
            for(Topic nextTp : nextTopics){
                EvolutionRelationship er = new EvolutionRelationship();
                er.setPreTopic(preTp);
                er.setNextTopic(nextTp);
                er.setSimilarity(this.getSimilarity(preTp, nextTp));
                erList.add(er);
            }
        }
        
        
        for(Topic preTp : preTopics){
            List<EvolutionRelationship> nextErList = 
                this.getEvolutionRelaByPreTopicOrderBySimilarity(preTp, erList);
            int i=1;
            for(EvolutionRelationship tempEr : nextErList){
                tempEr.setRankAgainstPreTopicInNextGroup(i);
                i++;
            }
        }
        
        for(Topic nextTp : nextTopics){
            List<EvolutionRelationship> preErList = 
                this.getEvolutionRelaByNextTopicOrderBySimilarity(nextTp, erList);
            int i=1;
            for(EvolutionRelationship tempEr : preErList){
                tempEr.setRankAgainstNextTopicInPreGroup(i);
                i++;
            }
        }
        
        //Filter EvolutionRelationship
        //1. Filter by minimum similarity
        for(Iterator<EvolutionRelationship> i =erList.iterator();i.hasNext();){
            EvolutionRelationship ter=i.next();
            if(TopicEvolutionServiceImpl.similarityThreshold > ter.getSimilarity()){
                i.remove();
            }
        }
        
        //2. Filter by minimun ratioThresholdToMaxSimilarity
        List<String> removeList = new ArrayList<String>();
        for(Iterator<Topic> i=preTopics.iterator();i.hasNext();){
            Topic preT=i.next();
            List<EvolutionRelationship> tErList=this.getEvolutionRelaByPreTopicOrderBySimilarity(preT, erList);
            if(tErList!=null && (!tErList.isEmpty())){
                double maxSimThreshold=(tErList.get(0)).getSimilarity() *
                                       TopicEvolutionServiceImpl.ratioThresholdToMaxSimilarity;
                for(Iterator<EvolutionRelationship> j=tErList.iterator();j.hasNext();){
                    EvolutionRelationship tEr=j.next();
                    if(maxSimThreshold > tEr.getSimilarity()){
                        removeList.add(tEr.getPreTopic().getId() + "," + tEr.getNextTopic().getId());
                    }
                }
            }
        }
        for(Iterator<Topic> i=nextTopics.iterator();i.hasNext();){
            Topic nextT=i.next();
            List<EvolutionRelationship> tErList=this.getEvolutionRelaByNextTopicOrderBySimilarity(nextT, erList);
            if(tErList!=null && (!tErList.isEmpty())){
                double maxSimThreshold=(tErList.get(0)).getSimilarity() *
                                       TopicEvolutionServiceImpl.ratioThresholdToMaxSimilarity;
                for(Iterator<EvolutionRelationship> j=tErList.iterator();j.hasNext();){
                    EvolutionRelationship tEr=j.next();
                    if(maxSimThreshold > tEr.getSimilarity()){
                        removeList.add(tEr.getPreTopic().getId() + "," + tEr.getNextTopic().getId());
                    }
                }
            }
        }
        for(Iterator<EvolutionRelationship> i =erList.iterator();i.hasNext();){
            EvolutionRelationship tEr=i.next();
            String preTIDnextTID=tEr.getPreTopic().getId() + "," + tEr.getNextTopic().getId();
            if(removeList.contains(preTIDnextTID)){
                i.remove();
            }
        }
        
        //3. Filter by inappropriate ER
        removeList.clear();
        boolean removePositionStart=false;
        for(Iterator<Topic> i=preTopics.iterator();i.hasNext();){
            Topic preT=i.next();
            List<EvolutionRelationship> tErList=this.getEvolutionRelaByPreTopicOrderBySimilarity(preT, erList);
            removePositionStart=false;
            if(tErList!=null && (!tErList.isEmpty())){
                int k=1;
                for(Iterator<EvolutionRelationship> j=tErList.iterator();j.hasNext();){
                    EvolutionRelationship tEr=j.next();
                    if(removePositionStart){
                        removeList.add(tEr.getPreTopic().getId() + "," + tEr.getNextTopic().getId());
                    }else{
                        int rankIndex=tEr.getRankAgainstPreTopicInNextGroup();
                        if(k==rankIndex){
                            k++;
                        }else{
                            removeList.add(tEr.getPreTopic().getId() + "," + tEr.getNextTopic().getId());
                            removePositionStart=true;
                        }
                    }
                }
            }
        }
        for(Iterator<Topic> i=nextTopics.iterator();i.hasNext();){
            Topic nextT=i.next();
            List<EvolutionRelationship> tErList=this.getEvolutionRelaByNextTopicOrderBySimilarity(nextT, erList);
            removePositionStart=false;
            if(tErList!=null && (!tErList.isEmpty())){
                int k=1;
                for(Iterator<EvolutionRelationship> j=tErList.iterator();j.hasNext();){
                    EvolutionRelationship tEr=j.next();
                    if(removePositionStart){
                        removeList.add(tEr.getPreTopic().getId() + "," + tEr.getNextTopic().getId());
                    }else{
                        int rankIndex=tEr.getRankAgainstNextTopicInPreGroup();
                        if(k==rankIndex){
                            k++;
                        }else{
                            removeList.add(tEr.getPreTopic().getId() + "," + tEr.getNextTopic().getId());
                            removePositionStart=true;
                        }
                    }
                }
            }
        }
        for(Iterator<EvolutionRelationship> i =erList.iterator();i.hasNext();){
            EvolutionRelationship tEr=i.next();
            String preTIDnextTID=tEr.getPreTopic().getId() + "," + tEr.getNextTopic().getId();
            if(removeList.contains(preTIDnextTID)){
                i.remove();
            }
        }
        
        return (erList.size()>0) ? erList : null;
    }
    
    @Override
    public double getSimilarity(Topic tp1, Topic tp2){
        
        Set<String> tp1WordSet=tp1.getWordSet();
        Set<String> tp2WordSet=tp2.getWordSet();
        
        Set<String> allWords= SetUtil.union(tp1WordSet, tp2WordSet);
        
        //compose two wordprobability vectors
//        List<WordProbability> tp1WPVector =new ArrayList<WordProbability>();
//        List<WordProbability> tp2WPVector =new ArrayList<WordProbability>();
        double[] tp1WPVector=new double[allWords.size()];
        double[] tp2WPVector=new double[allWords.size()];
        int i=0;
        for(String word : allWords){
            WordProbability wp1 = tp1.getWordProbability(word);
            WordProbability wp2 = tp2.getWordProbability(word);
            if(wp1!=null){
                tp1WPVector[i]=wp1.getProbability();
            }else{
                tp1WPVector[i]=0;
            }
            if(wp2!=null){
                tp2WPVector[i]=wp2.getProbability();
            }else{
                tp2WPVector[i]=0;
            }
            i++;
        }
        
        //计算相似度  
         double vector1Modulo = 0.00;//向量1的模  
         double vector2Modulo = 0.00;//向量2的模  
         double vectorProduct = 0.00; //向量积  
         
         for(int j=0;j<allWords.size();j++){
             vector1Modulo += tp1WPVector[j] * tp1WPVector[j];  
             vector2Modulo += tp2WPVector[j] * tp2WPVector[j];  
             vectorProduct += tp1WPVector[j] * tp2WPVector[j];  
         }
           
         vector1Modulo = Math.sqrt(vector1Modulo);  
         vector2Modulo = Math.sqrt(vector2Modulo);  
           
         //返回相似度  
        return (vectorProduct/(vector1Modulo*vector2Modulo));  
    }
    
    protected List<EvolutionRelationship> getEvolutionRelaByPreTopicOrderBySimilarity(Topic preTopic,
                                                                                   List<EvolutionRelationship> erList){
    
        List<EvolutionRelationship> newErlist = new ArrayList<EvolutionRelationship>();
        for(EvolutionRelationship er : erList){
            if(er.getPreTopic().getId()==preTopic.getId()){
                newErlist.add(er);
            }
        }
        
        if(newErlist.size()>0){
            Collections.sort(newErlist, 
                             new Comparator<EvolutionRelationship>() {
                                 @Override
                                 public int compare(EvolutionRelationship er1, EvolutionRelationship er2) {
                                     return (er1.getSimilarity()>er2.getSimilarity()) ? -1 : 1;
                                 }
                             });
            return newErlist;
        }else{
            return null;
        }
    }
    
    protected List<EvolutionRelationship> getEvolutionRelaByNextTopicOrderBySimilarity(Topic nextTopic,
                                                                                   List<EvolutionRelationship> erList){
    
        List<EvolutionRelationship> newErlist = new ArrayList<EvolutionRelationship>();
        for(EvolutionRelationship er : erList){
            if(er.getNextTopic().getId()==nextTopic.getId()){
                newErlist.add(er);
            }
        }
        
        if(newErlist.size()>0){
            Collections.sort(newErlist, 
                             new Comparator<EvolutionRelationship>() {
                                 @Override
                                 public int compare(EvolutionRelationship er1, EvolutionRelationship er2) {
                                     return (er1.getSimilarity()>er2.getSimilarity()) ? -1 : 1;
                                 }
                             });
            return newErlist;
        }else{
            return null;
        }
    }

    /**
     * @return the topicMiningService
     */
    public TopicMiningService getTopicMiningService() {
        return topicMiningService;
    }

    /**
     * @param topicMiningService the topicMiningService to set
     */
    public void setTopicMiningService(TopicMiningService topicMiningService) {
        this.topicMiningService = topicMiningService;
    }

    
}
