/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uta.ak.usttmp.common.model;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author zhangcong
 */
@XmlRootElement
public class MiningTask {
    
    public static final int STATUS_RUNNING=1;
    public static final int STATUS_COMPLETED=2;
    public static final int STATUS_STOPED=3;
    public static final int STATUS_NOT_STARTED=4;
    
    private long id;
    private String name;
    private Date startTime;
    private Date endTime;
    private int miningInterval;
    private int topicNum;
    private int keywordNum;
    private double alpha;
    private double beta;
    private String tag;
    private int status;
    private int qrtzJobExecCount;
    private int qrtzJobTotalCount;
    private String qrtzJobName;
    private String remark;

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
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the startTime
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * @param startTime the startTime to set
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the endTime
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * @param endTime the endTime to set
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * @return the miningInterval
     */
    public int getMiningInterval() {
        return miningInterval;
    }

    /**
     * @param miningInterval the miningInterval to set (hour)
     */
    public void setMiningInterval(int miningInterval) {
        this.miningInterval = miningInterval;
    }

    /**
     * @return the tag
     */
    public String getTag() {
        return tag;
    }

    /**
     * @param tag the tag to set
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * @return the topicNum
     */
    public int getTopicNum() {
        return topicNum;
    }

    /**
     * @param topicNum the topicNum to set
     */
    public void setTopicNum(int topicNum) {
        this.topicNum = topicNum;
    }

    /**
     * @return the keywordNum
     */
    public int getKeywordNum() {
        return keywordNum;
    }

    /**
     * @param keywordNum the keywordNum to set
     */
    public void setKeywordNum(int keywordNum) {
        this.keywordNum = keywordNum;
    }

    /**
     * @return the alpha
     */
    public double getAlpha() {
        return alpha;
    }

    /**
     * @param alpha the alpha to set
     */
    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    /**
     * @return the beta
     */
    public double getBeta() {
        return beta;
    }

    /**
     * @param beta the beta to set
     */
    public void setBeta(double beta) {
        this.beta = beta;
    }

    /**
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * @return the qrtzJobExecCount
     */
    public int getQrtzJobExecCount() {
        return qrtzJobExecCount;
    }

    /**
     * @param qrtzJobExecCount the qrtzJobExecCount to set
     */
    public void setQrtzJobExecCount(int qrtzJobExecCount) {
        this.qrtzJobExecCount = qrtzJobExecCount;
    }

    /**
     * @return the qrtzJobTotalCount
     */
    public int getQrtzJobTotalCount() {
        return qrtzJobTotalCount;
    }

    /**
     * @param qrtzJobTotalCount the qrtzJobTotalCount to set
     */
    public void setQrtzJobTotalCount(int qrtzJobTotalCount) {
        this.qrtzJobTotalCount = qrtzJobTotalCount;
    }

    /**
     * @return the qrtzJobName
     */
    public String getQrtzJobName() {
        return qrtzJobName;
    }

    /**
     * @param qrtzJobName the qrtzJobName to set
     */
    public void setQrtzJobName(String qrtzJobName) {
        this.qrtzJobName = qrtzJobName;
    }

    /**
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark the remark to set
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    
    
}
