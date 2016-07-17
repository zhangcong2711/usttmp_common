/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uta.ak.usttmp.common.textmining;

import cc.mallet.topics.ParallelTopicModel;
import static cc.mallet.topics.ParallelTopicModel.logger;
import cc.mallet.topics.TopicAssignment;
import cc.mallet.types.IDSorter;
import cc.mallet.types.LabelAlphabet;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import net.didion.jwnl.dictionary.Dictionary;
import uta.ak.usttmp.common.model.Topic;

/**
 *
 * @author zhangcong
 */
public class USTTMPTopicModel extends ParallelTopicModel {
    
    public USTTMPTopicModel(int numberOfTopics) {
        super(numberOfTopics);
    }
    
    public USTTMPTopicModel (int numberOfTopics, double alphaSum, double beta) {
        super(numberOfTopics, alphaSum, beta);
    }

    public List<Topic> getTopicResult(int numWords){
        
        List<Map> topicWordWeightList=new ArrayList<>();
        
        for (int topic = 0; topic < numTopics; topic++) {
            
            Map mp=new HashMap();
            
            for (int type = 0; type < numTypes; type++) {

                    int[] topicCounts = typeTopicCounts[type];

                    double weight = beta;

                    int index = 0;
                    while (index < topicCounts.length &&
                               topicCounts[index] > 0) {

                            int currentTopic = topicCounts[index] & topicMask;


                            if (currentTopic == topic) {
                                    weight += topicCounts[index] >> topicBits;
                                    break;
                            }

                            index++;
                    }
                    mp.put(alphabet.lookupObject(type).toString(), weight);
            }
            
            topicWordWeightList.add(mp);
        }
        
        ArrayList<TreeSet<IDSorter>> topicSortedWords = getSortedWords();

        List<Topic> topicResultList=new ArrayList<>();
        // Print results for each topic
        for (int topic = 0; topic < numTopics; topic++) {
            
            TreeSet<IDSorter> sortedWords = topicSortedWords.get(topic);
            int word = 0;
            
            Date date = new Date();
            
            Iterator<IDSorter> iterator = sortedWords.iterator();
            Map wordWeight = topicWordWeightList.get(topic);
            Topic topicItem= new Topic();
            topicItem.setName("topic__"+String.valueOf(topic+1)+"__"+date.toString());
            
            while (iterator.hasNext() && word < numWords) {
                    IDSorter info = iterator.next();
                    String wordStr=alphabet.lookupObject(info.getID()).toString();
                    double weight=(double) wordWeight.get(wordStr);
                    //Uppercase first
                    topicItem.addWordProbability(wordStr.substring(0,1).toUpperCase()+wordStr.substring(1), weight);
                    word++;
            }
            
            topicResultList.add(topicItem);
        }
        
        return (topicResultList.size()>0) ? topicResultList : null;
    }
    

}
