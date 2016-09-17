/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uta.ak.usttmp.common.processInterface.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import uta.ak.usttmp.common.model.MiningTask;
import uta.ak.usttmp.common.model.RawText;
import uta.ak.usttmp.common.model.Text;
import uta.ak.usttmp.common.processInterface.PreprocessComponent;
import uta.ak.usttmp.common.textmining.FileExcludeStopWord;

/**
 *
 * @author zhangcong
 */
public class EnglishTwitterPreprocessService implements PreprocessComponent {

    @Override
    public List<Text> preprocess(MiningTask mt, List<RawText> rawTextList) throws Exception {
        
        FileExcludeStopWord fesw=new FileExcludeStopWord();

        Set<String> filterDupSet=new HashSet<>();
        
        List<Text> txList=new ArrayList<>();
        
        for(RawText rt : rawTextList){
            
            System.out.println(rt.getTitle() + "  " +
                         rt.getText() + "  " +
                         rt.getTag());
                
            String filteredText=fesw.doExForTweet(rt.getText());
            if(filteredText!=null && (!filteredText.trim().isEmpty())){

                Text tx=new Text();
                tx.setRawTextId(rt.getId());
                tx.setCreateTime(rt.getCreateTime());
                tx.setTag(mt.getId()+"_"+rt.getTag());
                tx.setText(filteredText);
                tx.setTitle(rt.getTitle());

                if(!filterDupSet.contains(filteredText)){
                    filterDupSet.add(filteredText);
                    txList.add(tx);
                }
            }
        }
        
        
        return txList;
    }
    
}
