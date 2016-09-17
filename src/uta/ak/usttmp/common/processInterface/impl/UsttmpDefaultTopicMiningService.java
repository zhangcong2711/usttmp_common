/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uta.ak.usttmp.common.processInterface.impl;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.CharSequenceLowercase;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.Target2Label;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.InstanceList;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import uta.ak.usttmp.common.dao.mapper.TextRowMapper;
import uta.ak.usttmp.common.model.MiningTask;
import uta.ak.usttmp.common.model.Text;
import uta.ak.usttmp.common.model.Topic;
import uta.ak.usttmp.common.processInterface.MiningComponent;
import uta.ak.usttmp.common.textmining.TextListIterator;
import uta.ak.usttmp.common.textmining.USTTMPTopicModel;

/**
 *
 * @author zhangcong
 */
public class UsttmpDefaultTopicMiningService implements MiningComponent {
    
    public final static String tokenRegex="\\p{L}[\\p{L}\\p{P}]+\\p{L}";
    public final static boolean keepSequenceBigrams=false;
    
    public final static String lineRegex="^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$";
    public final static int dataOption=3;
    public final static int labelOption=2;
    public final static int nameOption=1;
    
    
    public final static int showTopicsInterval=50;
    //public static int topWords=20;
    public final static int numIterations=1000;
    public final static int optimizeInterval=0;
    public final static int optimizeBurnIn=200;
    public final static boolean useSymmetricAlpha=false;
    public final static int numThreads=1;

    @Override
    public List<Topic> generateTopics(MiningTask mt, List<Text> textList) throws Exception {
        
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
        
        instances.addThruPipe (new TextListIterator(textList, 
                                                    Pattern.compile(lineRegex),
                                                    dataOption, 
                                                    labelOption, 
                                                    nameOption));

        /**
         * Start to train topics
         */
        USTTMPTopicModel topicModel = null;
        topicModel = new USTTMPTopicModel (mt.getTopicNum(), mt.getAlpha(), mt.getBeta());

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

        topicModel.setTopicDisplay(showTopicsInterval, mt.getKeywordNum());
        topicModel.setNumIterations(numIterations);
        topicModel.setOptimizeInterval(optimizeInterval);
        topicModel.setBurninPeriod(optimizeBurnIn);
        topicModel.setSymmetricAlpha(useSymmetricAlpha);
        topicModel.setNumThreads(numThreads);

        topicModel.estimate();

        List<Topic> topicList=topicModel.getTopicResult(mt.getKeywordNum());
        
        return topicList;
    }
    
}
