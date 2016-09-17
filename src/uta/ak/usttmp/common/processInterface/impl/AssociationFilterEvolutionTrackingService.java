/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uta.ak.usttmp.common.processInterface.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.springframework.transaction.annotation.Transactional;
import uta.ak.usttmp.common.model.EvolutionRelationship;
import uta.ak.usttmp.common.model.MiningTask;
import uta.ak.usttmp.common.model.Topic;
import uta.ak.usttmp.common.model.WordProbability;
import uta.ak.usttmp.common.processInterface.TrackingComponent;
import uta.ak.usttmp.common.util.SetUtil;

/**
 *
 * @author zhangcong
 */
public class AssociationFilterEvolutionTrackingService implements TrackingComponent {

    protected final static double similarityThreshold = 0.6;
//    protected static int filterPositionForPreGroup = 3;
    protected final static double ratioThresholdToMaxSimilarity = 0.7;
    
    
    
    
    @Override
    @Transactional
    public List<EvolutionRelationship> getTopicEvolutionRelationships( MiningTask mt,
                                                                       List<Topic> preTopics,
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
            if(AssociationFilterEvolutionTrackingService.similarityThreshold > ter.getSimilarity()){
                i.remove();
            }
        }
        
        
        List<String> removeList = new ArrayList<String>();
        
        //2.1 Filter by inappropriate ER
        removeList.clear();
        for(Topic preTp : preTopics){
            
            Topic tPostTp=this.getPostTopic(preTp, erList);
            if(null==tPostTp){
                continue;
            }
//            String preTIDnextTID=preTp.getId() + "," + tPostTp.getId();
            
            List<EvolutionRelationship> tERList=
                    this.getEvolutionRelaByNextTopicOrderBySimilarity(tPostTp, 
                                                                      erList);
            
            boolean removeSign=false;
            for(EvolutionRelationship tER : tERList){
                if(tER.getPreTopic().getId()==preTp.getId()){
                    break;
                }else{
                    Topic kPostTp=this.getPostTopic(tER.getPreTopic(), erList);
                    if(kPostTp==null || kPostTp.getId()!=tPostTp.getId()){
                        removeSign=true;
                        break;
                    }
                }
            }
            if(removeSign){
                removeER(preTp.getId(),tPostTp.getId(),erList);
//                removeList.add(preTIDnextTID);
            }
        }
        
//        for(Iterator<EvolutionRelationship> i =erList.iterator();i.hasNext();){
//            EvolutionRelationship tEr=i.next();
//            String preTIDnextTID=tEr.getPreTopic().getId() + "," + tEr.getNextTopic().getId();
//            if(removeList.contains(preTIDnextTID)){
//                i.remove();
//            }
//        }
        
        
        //2.2 Filter by minimun ratioThresholdToMaxSimilarity
        removeList.clear();
        for(Iterator<Topic> i=preTopics.iterator();i.hasNext();){
            Topic preT=i.next();
            List<EvolutionRelationship> tErList=this.getEvolutionRelaByPreTopicOrderBySimilarity(preT, erList);
            if(tErList!=null && (!tErList.isEmpty())){
                double maxSimThreshold=(tErList.get(0)).getSimilarity() *
                                       AssociationFilterEvolutionTrackingService.ratioThresholdToMaxSimilarity;
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
                                       AssociationFilterEvolutionTrackingService.ratioThresholdToMaxSimilarity;
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
        
        /*
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
        }*/
        
        return (erList.size()>0) ? erList : null;
    }
    
    protected void removeER(long preTopicId, long postTopicId, List<EvolutionRelationship> erList){
        
        String preTIDnextTID=preTopicId + "," + postTopicId;
        for(Iterator<EvolutionRelationship> i=erList.iterator();i.hasNext();){
            EvolutionRelationship er=i.next();
            if(preTIDnextTID.equals(er.toString())){
                i.remove();
            }
        }
    }
    
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
    
    protected Topic getPostTopic(Topic preTopic, List<EvolutionRelationship> erList){
        
        List<EvolutionRelationship> postList=getEvolutionRelaByPreTopicOrderBySimilarity(preTopic,erList);
        
        return (null!=postList) ? postList.get(0).getNextTopic() : null;
        
    }
    
    protected Topic getPriorTopic(Topic postTopic, List<EvolutionRelationship> erList){
        
        List<EvolutionRelationship> preList=getEvolutionRelaByNextTopicOrderBySimilarity(postTopic,erList);
        
        return (null!=preList) ? preList.get(0).getPreTopic() : null;
        
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

}
