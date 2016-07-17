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
public class Message {
    
    private String source;
    private String target;
    private String type;
    private String invokeType;
    private String timeStamp;
    private String methodName;
    private String methodBody;

    /**
     * @return the timeStamp
     */
    public String getTimeStamp() {
        return timeStamp;
    }

    /**
     * @param timeStamp the timeStamp to set. Format should be yyyy-MM-dd HH:mm:ss
     */
    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * @return the methodName
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * @param methodName the methodName to set
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * @return the methodBody
     */
    public String getMethodBody() {
        return methodBody;
    }

    /**
     * @param methodBody the methodBody to set
     */
    public void setMethodBody(String methodBody) {
        this.methodBody = methodBody;
    }

    /**
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * @return the target
     */
    public String getTarget() {
        return target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type Set the type: invoke message or response message
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the invokeType
     */
    public String getInvokeType() {
        return invokeType;
    }

    /**
     * @param invokeType Set invoke type: Asyn or Synch
     */
    public void setInvokeType(String invokeType) {
        this.invokeType = invokeType;
    }
    
    
    
    
}
