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
import uta.ak.usttmp.common.dao.mapper.MiningTaskRowMapper;
import uta.ak.usttmp.common.dao.mapper.RawTextRowMapper;
import uta.ak.usttmp.common.dao.mapper.TextRowMapper;
import uta.ak.usttmp.common.dao.mapper.TopicRowMapper;
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
    @Transactional
    public List<Topic> generateTopics(long miningTaskId, 
                                      Date startTime, 
                                      Date endTime, 
                                      String tag, 
                                      int seqNo, 
                                      int topicNum, 
                                      int keywordNum, 
                                      double alpha, 
                                      double beta,
                                      String remark) throws Exception{
        
        this.preprocessData(miningTaskId, 
                            startTime, 
                            endTime, 
                            tag, 
                            seqNo, 
                            topicNum, 
                            keywordNum, 
                            alpha, 
                            beta);
        
//        throw new RuntimeException("人为停止");
        
        return this.miningTopic(miningTaskId, 
                                startTime, 
                                endTime, 
                                tag, 
                                seqNo, 
                                topicNum, 
                                keywordNum, 
                                alpha, 
                                beta,
                                remark);
    }
    
    
    protected void preprocessData(long miningTaskId, 
                                  Date startTime, 
                                  Date endTime, 
                                  String tag, 
                                  int seqNo, 
                                  int topicNum, 
                                  int keywordNum, 
                                  double alpha, 
                                  double beta) throws Exception{
        
        JdbcTemplate jt=this.getJdbcTemplate();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        //Clear the text table if there is existed text.
        String clearSQL="DELETE " +
                            "FROM " +
                            "	c_text " +
                            "WHERE " +
                            "	( " +
                            "       text_createdate " + 
                            "		BETWEEN ? " +
                            "		AND ? " +
                            "	) " +
                            "AND tag = ?";
        jt.update(clearSQL, 
                  formatter.format(startTime),
                  formatter.format(endTime),
                  tag);

        System.out.println("Query raw text records...");
        String querySQL="SELECT " +
                            "	* " +
                            "FROM " +
                            "	c_rawtext " +
                            "WHERE " +
                            "	( " +
                            "       text_createdate " + 
                            "		BETWEEN ? " +
                            "		AND ? " +
                            "	) " +
                            "AND tag = ?";

        List<RawText> rawTextList=jt.query(querySQL, 
                                            new Object[]{formatter.format(startTime),
                                                         formatter.format(endTime),
                                                         tag}, 
                                            new RawTextRowMapper());

        FileExcludeStopWord fesw=new FileExcludeStopWord();
        
        List<Object[]> text_lines=new ArrayList<>();

        Set<String> filterDupSet=new HashSet<>();
        
        for(RawText rt : rawTextList){
            
            System.out.println(rt.getTitle() + "  " +
                               rt.getText() + "  " +
                               rt.getTag());
            String filteredText=fesw.doExForTweet(rt.getText());
            if(filteredText!=null && (!filteredText.trim().isEmpty())){
                
                Object[] ojarr=new Object[]{ rt.getTitle(),
                                              filteredText,
                                              rt.getTag(),
                                              String.valueOf(rt.getId()),
                                              formatter.format(rt.getCreateTime()) };

                if(!filterDupSet.contains(filteredText)){
                    filterDupSet.add(filteredText);
                    text_lines.add(ojarr);
                }
            }
        }
        
        String insertSQL="INSERT INTO c_text(mme_lastupdate, mme_updater, title, text, tag, rawtext_id, text_createdate) "
                            + "VALUES (NOW(), \"USTTMP\", ?, ?, ?, ?, ?)";
        
        System.out.println("Start to insert text records...");
        jt.batchUpdate(insertSQL, text_lines);
        
    }
    
    protected List<Topic> miningTopic(long miningTaskId, 
                                      Date startTime, 
                                      Date endTime, 
                                      String tag, 
                                      int seqNo, 
                                      int topicNum, 
                                      int keywordNum, 
                                      double alpha, 
                                      double beta,
                                      String remark) throws Exception{
        
        //Clear the existed topics.
        String clearSQL="DELETE " +
                            "FROM " +
                            "	c_topic " +
                            "WHERE " +
                            "miningtask_id=? AND seq_no = ?";
        this.getJdbcTemplate().update(clearSQL, 
                                      miningTaskId,
                                      seqNo);
        
        /**
         * Preprocess data
         */
        Pipe instancePipe;

        ArrayList<Pipe> pipeList = new ArrayList<Pipe>();
        pipeList.add(new Target2Label());
        pipeList.add(new CharSequenceLowercase());



        Pattern tokenPattern = null;
        tokenPattern = Pattern.compile(tokenRegex);
        pipeList.add(new CharSequence2TokenSequence(tokenPattern));


        TokenSequenceRemoveStopwords stopwordFilter =
            new TokenSequenceRemoveStopwords(false, 
                                             keepSequenceBigrams);
        pipeList.add(stopwordFilter);

        pipeList.add(new TokenSequence2FeatureSequence());

        instancePipe = new SerialPipes(pipeList);

        InstanceList instances = new InstanceList (instancePipe);

        System.out.println("Query text records...");
        String querySQL="SELECT " +
                            "	* " +
                            "FROM " +
                            "	c_text " +
                            "WHERE " +
                            "	( " +
                            "       text_createdate " + 
                            "		BETWEEN ? " +
                            "		AND ? " +
                            "	) " +
                            "AND tag = ?";
        
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Text> textList=this.getJdbcTemplate().query(querySQL, 
                                                         new Object[]{
                                                             formatter.format(startTime),
                                                             formatter.format(endTime),
                                                             tag}, 
                                                         new TextRowMapper());

        instances.addThruPipe (new TextListIterator(textList, 
                                                    Pattern.compile(lineRegex),
                                                    dataOption, 
                                                    labelOption, 
                                                    nameOption));

        /**
         * Start to train topics
         */
        USTTMPTopicModel topicModel = null;
        topicModel = new USTTMPTopicModel (topicNum, alpha, beta);

        InstanceList training = instances;
        System.out.println("Data loaded.");
        if (training.size() > 0 &&
                training.get(0) != null) {
                Object data = training.get(0).getData();
                if (! (data instanceof FeatureSequence)) {
                        throw new RuntimeException("Topic modeling currently only supports feature sequences: use --keep-sequence option when importing data.");
                }
        }
        topicModel.addInstances(training);

        topicModel.setTopicDisplay(showTopicsInterval, keywordNum);
        topicModel.setNumIterations(numIterations);
        topicModel.setOptimizeInterval(optimizeInterval);
        topicModel.setBurninPeriod(optimizeBurnIn);
        topicModel.setSymmetricAlpha(useSymmetricAlpha);
        topicModel.setNumThreads(numThreads);

        topicModel.estimate();

        List<Topic> topicList=topicModel.getTopicResult(keywordNum);

//        String insertSQL="INSERT INTO c_topic(mme_lastupdate, mme_updater, name, content, miningtask_id, seq_no) "+
//                             "VALUES (NOW(), \"USTTMP\", ?, ?, ?, ?)";
        String insertSQL="INSERT INTO `c_topic` ( " +
                            "	`mme_lastupdate`, " +
                            "	`mme_updater`, " +
                            "	`name`, " +
                            "	`content`, " +
                            "	`remark`, " + 
                            "	`miningtask_id`, " +
                            "	`seq_no` " +
                            ") " +
                            "VALUES " +
                            "	(NOW(), 'USTTMP' ,?,?,?,?,?)";

        List<Object[]> argsList=new ArrayList<>();
        
        for (Topic tm : topicList){
            Object[] objarr=new Object[]{tm.getName(),
                                         tm.toString(),
                                         (null!=remark) ? remark : "",
                                         miningTaskId,
                                         seqNo};
            argsList.add(objarr);
        }

        System.out.println("Inserting records into the c_topic table...");
        this.getJdbcTemplate().batchUpdate(insertSQL, argsList);
        
        return topicList;
    }

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
    
}
