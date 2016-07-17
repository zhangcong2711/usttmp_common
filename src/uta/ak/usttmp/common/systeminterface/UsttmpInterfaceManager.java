/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uta.ak.usttmp.common.systeminterface;

import uta.ak.usttmp.common.systeminterface.model.Message;

/**
 *
 * @author zhangcong
 */
public interface UsttmpInterfaceManager {
    
    public static final String INVOKE_TYPE_SYN="invoke_type_syn";
    public static final String INVOKE_TYPE_ASYN="invoke_type_asyn";
    public static final String TYPE_CALL="type_call";
    public static final String TYPE_RESPONSE="type_response";
    public static final String NAME_OF_INTERFACE_RESPONSER="interfaceResponser";
    
    
    
    public Message call(String targetName,
                        String invokeType,
                        String methodName, 
                        String methodBody) throws Exception;
    
    public Message response(String msg) throws Exception;
    
}
