/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uta.ak.usttmp.common.textmining;

import cc.mallet.types.Instance;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author zhangcong
 */
public class RawTextRsIterator implements Iterator<Instance> {
    
    ResultSet rawTextResultSet;
    Pattern lineRegex;
    int uriGroup, targetGroup, dataGroup;
    String currentLine;
    
    public RawTextRsIterator (ResultSet rawTextResultSet, Pattern lineRegex, 
                              int dataGroup, int targetGroup, int uriGroup)
    {
        this.rawTextResultSet= rawTextResultSet;
        this.lineRegex = lineRegex;
        this.targetGroup = targetGroup;
        this.dataGroup = dataGroup;
        this.uriGroup = uriGroup;
        
        if(rawTextResultSet==null){
            throw new RuntimeException ("rawTextSet is null.");
        }
        
        if (dataGroup <= 0)
                throw new IllegalStateException ("You must extract a data field.");
        try {
            this.rawTextResultSet.next();
            this.currentLine = this.rawTextResultSet.getString("mme_eid")
                               + " " + this.rawTextResultSet.getString("title")
                               + " " + this.rawTextResultSet.getString("text");
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
            if(this.rawTextResultSet.next()){
                this.currentLine = this.rawTextResultSet.getString("mme_eid")
                               + " " + this.rawTextResultSet.getString("title")
                               + " " + this.rawTextResultSet.getString("text");
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
