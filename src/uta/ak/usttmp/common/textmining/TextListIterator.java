/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uta.ak.usttmp.common.textmining;

import cc.mallet.types.Instance;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import uta.ak.usttmp.common.model.Text;

/**
 *
 * @author zhangcong
 */
public class TextListIterator implements Iterator<Instance>{
    
    List<Text> textList;
    Pattern lineRegex;
    int uriGroup, targetGroup, dataGroup;
    String currentLine;
    Iterator<Text> textIterator;
    
    public TextListIterator (List<Text> textList, Pattern lineRegex, 
                              int dataGroup, int targetGroup, int uriGroup)
    {
        this.textList= textList;
        this.lineRegex = lineRegex;
        this.targetGroup = targetGroup;
        this.dataGroup = dataGroup;
        this.uriGroup = uriGroup;
        
        if(null==textList || textList.size()==0){
            throw new RuntimeException ("rawTextSet is empty.");
        }
        
        textIterator=textList.iterator();
        
        if (dataGroup <= 0)
                throw new IllegalStateException ("You must extract a data field.");
        try {
            Text tt=this.textIterator.next();
            this.currentLine = tt.getId()
                               + " " + tt.getTitle()
                               + " " + tt.getText();
        } catch (Exception e) {
                throw new IllegalStateException ();
        }
    }

    @Override
    public boolean hasNext() {
        
        return this.currentLine != null;
    }

    @Override
    public Instance next() {
        
        String uriStr = null;
        String data = null;
        String target = null;
        Matcher matcher = lineRegex.matcher(currentLine);
        if (matcher.find()) {
                if (uriGroup > 0)
                        uriStr = matcher.group(uriGroup);
                if (targetGroup > 0)
                        target = matcher.group(targetGroup);
                if (dataGroup > 0)
                        data = matcher.group(dataGroup);
        } else {
                throw new IllegalStateException ("Line does not match regex:\n" +
                                                                                 currentLine);
        }

        String uri= uriStr;
        
        assert (data != null);
        Instance carrier = new Instance (data, target, uri, null);
        try {
            if(textIterator.hasNext()){
                Text tt=this.textIterator.next();
                this.currentLine = tt.getId()
                                   + " " + tt.getTitle()
                                   + " " + tt.getText();
            }else{
                this.currentLine =null;
            }
        } catch (Exception e) {
            throw new IllegalStateException ();
        }
        return carrier;
    }
    
    @Override
    public void remove () {
        throw new IllegalStateException ("This Iterator<Instance> does not support remove().");
    }
    
}
