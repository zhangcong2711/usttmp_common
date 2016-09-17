/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uta.ak.usttmp.common.service;

import java.util.Date;
import java.util.List;
import uta.ak.usttmp.common.model.MiningTask;
import uta.ak.usttmp.common.model.MiningTaskLog;
import uta.ak.usttmp.common.model.Topic;

/**
 *
 * @author zhangcong
 */
public interface TopicMiningService {
    
    public List<Topic> getTopics(long miningTaskId,
                                 int seq);
    
    public MiningTask getMiningTask(long miningTaskId);
    
    public List<MiningTaskLog> getMiningTaskLogs(long miningTaskId);
  
    
}
