/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uta.ak.usttmp.common.service.impl;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.CharSequenceLowercase;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.Target2Label;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.InstanceList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import uta.ak.usttmp.common.dao.UsttmpDaoSupport;
import uta.ak.usttmp.common.dao.mapper.MiningTaskLogRowMapper;
import uta.ak.usttmp.common.dao.mapper.MiningTaskRowMapper;
import uta.ak.usttmp.common.dao.mapper.RawTextRowMapper;
import uta.ak.usttmp.common.dao.mapper.TextRowMapper;
import uta.ak.usttmp.common.dao.mapper.TopicRowMapper;
import uta.ak.usttmp.common.model.MiningTask;
import uta.ak.usttmp.common.model.MiningTaskLog;
import uta.ak.usttmp.common.model.RawText;
import uta.ak.usttmp.common.model.Text;
import uta.ak.usttmp.common.model.Topic;
import uta.ak.usttmp.common.service.TopicMiningService;
import uta.ak.usttmp.common.textmining.FileExcludeStopWord;
import uta.ak.usttmp.common.textmining.RawTextRsIterator;
import uta.ak.usttmp.common.textmining.TextListIterator;
import uta.ak.usttmp.common.textmining.USTTMPTopicModel;

/**
 *
 * @author zhangcong
 */
public class TopicMiningServiceImpl extends UsttmpDaoSupport implements TopicMiningService{

    @Override
    public List<Topic> getTopics(long miningTaskId, 
                                 int seq) {
        
        String sql="select * from c_topic " +
                       "where miningtask_id=? and seq_no=?";
        
        return this.getJdbcTemplate().query(sql, 
                                            new TopicRowMapper(),
                                            miningTaskId,
                                            seq);
    }

    @Override
    public MiningTask getMiningTask(long miningTaskId) {
        //Load Miningtask
        String querySql="select * from c_miningtask where mme_eid=?";
        MiningTask mt=(MiningTask) this.getJdbcTemplate().queryForObject(querySql, 
                              new Object[]{miningTaskId}, 
                              new MiningTaskRowMapper());
        
        return mt;
    }

    @Override
    public List<MiningTaskLog> getMiningTaskLogs(long miningTaskId) {
        
        String querySql="select * from c_miningtask_log where miningtask_id=? order by exception_time";
        
        return getJdbcTemplate().query(querySql, 
                                       new MiningTaskLogRowMapper(), 
                                       miningTaskId);
        
    }
    
}
