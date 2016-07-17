/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uta.ak.usttmp.common.textmining;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/*
 * 对每一个句子去停用词、去符号、去空格
 *  
 * ***/

public class FileExcludeStopWord{
    
    private static Set<String> stopWordsList;

    public FileExcludeStopWord() {
        
        //Load stopword file
        InputStreamReader isr = null;
        try {
            stopWordsList = new CopyOnWriteArraySet<>(); 
            Resource res = new ClassPathResource("StopWordTable2.txt");
//                File stopwords=res.getFile();
//		File stopwords=new File("/Users/zhangcong/dev/corpus/StopWordTable2.txt");
            isr = new InputStreamReader(res.getInputStream());
            BufferedReader stops = null;
            try {
                String tempString = null;
                stops = new BufferedReader(isr);
                tempString = stops.readLine();
                while ((tempString = stops.readLine()) != null) {
                    if(!tempString.isEmpty()){
                        stopWordsList.add(tempString.toLowerCase().trim());
                    }
                }
                
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } catch (IOException ex) {
                Logger.getLogger(FileExcludeStopWord.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                isr.close();
            } catch (IOException ex) {
                Logger.getLogger(FileExcludeStopWord.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public String doExForTweet(String text) throws IOException {

        List<String> allWords=new ArrayList<String>();

        String[] lines=text.toLowerCase().split("\n");
        for(String tl : lines){
            String[] stringList=tl.split(" ");
            for (String t : stringList) {
                allWords.add(t);
            }
        }



        for(Iterator<String> i=allWords.iterator();i.hasNext();){
            String w=i.next();
            if(w==null || w.equals("") || w.equals(" ")){
                i.remove();
            }
        }

        for(Iterator<String> i=allWords.iterator();i.hasNext();){
            String wd=i.next();
            for (String stopWord : stopWordsList) {
                if(stopWord.equals(wd) || wd.length()<2){
                    i.remove();
                    break;
                }
            }
        }

        for(Iterator<String> i=allWords.iterator();i.hasNext();){
            String wd=i.next();
            if(wd.startsWith("http")){
                i.remove();
            }
        }

        for(Iterator<String> i=allWords.iterator();i.hasNext();){
            String wd=i.next();
            if(wd.endsWith("\'s") || wd.endsWith("’s")){
                int index=allWords.indexOf(wd);
                wd=wd.substring(0, wd.length()-2);
                allWords.set(index, wd);
            }
        }

        String[] tweetStr = new String[] { "[", "]", ".", ",", ":", "\\",
                        "/", "?", "!", ";", "\"", "'", "\n", "\r", "<", ">",
                        "=", "#", "@", "(", ")", "*", "‘", "’", "“","”"};
        // 循环List ,对每一个元素替换
        for(Iterator<String> i=allWords.iterator();i.hasNext();){
            String wd=i.next();
            int index=allWords.indexOf(wd);
            for (String str : tweetStr) {
                wd = wd.replace(str, "");
            }
            allWords.set(index, wd);
        }

        //再过一遍停用词
        for(Iterator<String> i=allWords.iterator();i.hasNext();){
            String wd=i.next();
            for (String stopWord : stopWordsList) {
                if(stopWord.equals(wd) || wd.length()<2){
                    i.remove();
                    break;
                }
            }
        }

        StringBuffer sb=new StringBuffer();
        for (String t : allWords) {
            sb.append(" "+t);
        }
        System.out.println("Words are "+ sb.toString());
        return sb.toString();

    }
}
