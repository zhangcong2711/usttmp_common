/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uta.ak.usttmp.common.textmining;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.CharSequenceLowercase;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.Target2Label;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.tui.TopicTrainer;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.InstanceList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import uta.ak.usttmp.common.model.Topic;

/**
 *
 * @author zhangcong
 */
public class TestMallet {
    
    private static String tokenRegex="\\p{L}[\\p{L}\\p{P}]+\\p{L}";
    private static boolean keepSequenceBigrams=false;
    
    private static String lineRegex="^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$";
    private static int dataOption=3;
    private static int labelOption=2;
    private static int nameOption=1;
    
    
    private static int showTopicsInterval=50;
    private static int topWords=20;
    private static int numIterations=1000;
    private static int optimizeInterval=0;
    private static int optimizeBurnIn=200;
    private static boolean useSymmetricAlpha=false;
    private static int numThreads=1;
    
    
    public static void main (String[] args) throws java.io.IOException {
        

        try{
            
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
                                            new TokenSequenceRemoveStopwords(false, keepSequenceBigrams);
            pipeList.add(stopwordFilter);

            pipeList.add(new TokenSequence2FeatureSequence());

            instancePipe = new SerialPipes(pipeList);

            InstanceList instances = new InstanceList (instancePipe);


            Connection con = null; //定义一个MYSQL链接对象
            Class.forName("com.mysql.jdbc.Driver").newInstance(); //MYSQL驱动
            con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/USTTMP", "root", "root.123"); //链接本地MYSQL
            System.out.println("connection yes");

            System.out.println("query records...");
            String querySQL="SELECT\n" +
                                "	* " +
                                "FROM " +
                                "	c_text " +
                                "WHERE " +
                                "tag like 'mallet integrated 02%'";
            
            PreparedStatement preparedStmt = con.prepareStatement(querySQL);
            ResultSet rs=preparedStmt.executeQuery();

            instances.addThruPipe (new RawTextRsIterator (rs, Pattern.compile(lineRegex),
                                                    dataOption, labelOption, nameOption));
            
            
            
            
            /**
             * Start to train topics
             */
            USTTMPTopicModel topicModel = null;
            topicModel = new USTTMPTopicModel (30, 5, 0.01);
            
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
            
            topicModel.setTopicDisplay(showTopicsInterval, topWords);
            topicModel.setNumIterations(numIterations);
            topicModel.setOptimizeInterval(optimizeInterval);
            topicModel.setBurninPeriod(optimizeBurnIn);
            topicModel.setSymmetricAlpha(useSymmetricAlpha);
            topicModel.setNumThreads(numThreads);
            
            topicModel.estimate();
            
            List<Topic> topicList=topicModel.getTopicResult(20);
            
            String insertSQL="INSERT INTO c_topic(mme_lastupdate, mme_updater, name, content, remark) "+
                                 "VALUES (NOW(), \"AK\", ?, ?, ?)";
            PreparedStatement insertPS = con.prepareStatement(insertSQL);
            
            for (Topic tm : topicList){
                insertPS.setString(1, tm.getName());
                insertPS.setString(2, tm.toString());
                insertPS.setString(3, "Mallet integrated 02");
                insertPS.addBatch();
            }
            
            System.out.println("Inserting records into the c_topic table...");
            insertPS.clearParameters();
            int[] results = insertPS.executeBatch();
            
            
        }catch(Exception e){
            e.printStackTrace();
        }
        
    } 
}
