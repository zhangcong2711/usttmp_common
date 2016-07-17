/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uta.ak.usttmp.common.service;

import java.util.Date;
import java.util.List;
import uta.ak.usttmp.common.model.Topic;

/**
 *
 * @author zhangcong
 */
public interface TopicMiningService {
    
    public static String tokenRegex="\\p{L}[\\p{L}\\p{P}]+\\p{L}";
    public static boolean keepSequenceBigrams=false;
    
    public static String lineRegex="^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$";
    public static int dataOption=3;
    public static int labelOption=2;
    public static int nameOption=1;
    
    
    public static int showTopicsInterval=50;
    //public static int topWords=20;
    public static int numIterations=1000;
    public static int optimizeInterval=0;
    public static int optimizeBurnIn=200;
    public static boolean useSymmetricAlpha=false;
    public static int numThreads=1;
    
    public List<Topic> generateTopics(long miningTaskId,
                                      Date startTime,
                                      Date endTime,
                                      String tag,
                                      int seqNo,
                                      int topicNum,
                                      int keywordNum,
                                      double alpha,
                                      double beta,
                                      String remark
                                      ) throws Exception;
    
    public List<Topic> getTopics(long miningTaskId,
                                 int seq);
  
    
}
