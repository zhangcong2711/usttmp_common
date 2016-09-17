/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uta.ak.usttmp.common.processInterface;

import java.util.List;
import uta.ak.usttmp.common.model.MiningTask;
import uta.ak.usttmp.common.model.RawText;
import uta.ak.usttmp.common.model.Text;

/**
 *
 * @author zhangcong
 */
public interface PreprocessComponent {
    
    public List<Text> preprocess(MiningTask mt, List<RawText> rawTextList) throws Exception;
    
}
