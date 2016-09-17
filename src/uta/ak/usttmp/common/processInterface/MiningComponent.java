/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uta.ak.usttmp.common.processInterface;

import java.util.Date;
import java.util.List;
import uta.ak.usttmp.common.model.MiningTask;
import uta.ak.usttmp.common.model.Text;
import uta.ak.usttmp.common.model.Topic;

/**
 *
 * @author zhangcong
 */
public interface MiningComponent {
    
    public List<Topic> generateTopics(MiningTask mt, List<Text> txList) throws Exception;
}
