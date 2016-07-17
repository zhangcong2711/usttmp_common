/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uta.ak.usttmp.common.exception;

/**
 *
 * @author zhangcong
 */
public class UsttmpProcessException extends Exception {

    public UsttmpProcessException(String message) {
        super(message);
    }
    
    
    public static final String TYPE_PREPROCESS_EXCEPTION="type_preprocess_exception";
    public static final String TYPE_MINING_EXCEPTION="type_mining_exception";
    public static final String TYPE_CALC_EVO_RELA_EXCEPTION="type_calc_evo_rela_exception";
}
