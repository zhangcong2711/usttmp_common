/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uta.ak.usttmp.common.model;

/**
 *
 * @author zhangcong
 */
public class Text extends RawText{
    
    private long rawTextId;

    /**
     * @return the rawTextId
     */
    public long getRawTextId() {
        return rawTextId;
    }

    /**
     * @param rawTextId the rawTextId to set
     */
    public void setRawTextId(long rawTextId) {
        this.rawTextId = rawTextId;
    }
}
