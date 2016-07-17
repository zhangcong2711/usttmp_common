/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uta.ak.usttmp.common.systeminterface.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author zhangcong
 */
@XmlRootElement
public class CallResult {
    
    public static final String RESULT_SUCCESS="result_success";
    public static final String RESULT_FAILED="result_failed";
    
    private String resultStatus;
    private String info;
    private String error;

    /**
     * @return the resultStatus
     */
    public String getResultStatus() {
        return resultStatus;
    }

    /**
     * @param resultStatus the resultStatus to set
     */
    public void setResultStatus(String resultStatus) {
        this.resultStatus = resultStatus;
    }

    /**
     * @return the info
     */
    public String getInfo() {
        return info;
    }

    /**
     * @param info the info to set
     */
    public void setInfo(String info) {
        this.info = info;
    }

    /**
     * @return the error
     */
    public String getError() {
        return error;
    }

    /**
     * @param error the error to set
     */
    public void setError(String error) {
        this.error = error;
    }
    
    
}
