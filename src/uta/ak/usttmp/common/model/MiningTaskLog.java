/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uta.ak.usttmp.common.model;

import java.util.Date;

/**
 *
 * @author zhangcong
 */
public class MiningTaskLog {
    
    public final static int TYPE_INFO=1;
    public final static int TYPE_EXCEPTION=2;
    
    
    private long id;
    private int type;
    private Date occurrenceTime;
    private String info;
    private long miningTaskId;

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @return the occurrenceTime
     */
    public Date getOccurrenceTime() {
        return occurrenceTime;
    }

    /**
     * @param occurrenceTime the occurrenceTime to set
     */
    public void setOccurrenceTime(Date occurrenceTime) {
        this.occurrenceTime = occurrenceTime;
    }

    /**
     * @return the info
     */
    public String getInfo() {
        return info.replaceAll("[\\t\\n\\r]", "      ");
    }

    /**
     * @param info the info to set
     */
    public void setInfo(String info) {
        this.info = info;
    }

    /**
     * @return the miningTaskId
     */
    public long getMiningTaskId() {
        return miningTaskId;
    }

    /**
     * @param miningTaskId the miningTaskId to set
     */
    public void setMiningTaskId(long miningTaskId) {
        this.miningTaskId = miningTaskId;
    }
    
    
    
    
}
