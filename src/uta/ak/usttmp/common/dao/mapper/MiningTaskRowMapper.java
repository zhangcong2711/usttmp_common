/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uta.ak.usttmp.common.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.jdbc.core.RowMapper;
import uta.ak.usttmp.common.model.MiningTask;

/**
 *
 * @author zhangcong
 */
public class MiningTaskRowMapper implements RowMapper{

    @Override
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
        
        try {
            MiningTask mt=new MiningTask();
            mt.setId(rs.getLong("mme_eid"));
            
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            mt.setStartTime(formatter.parse(rs.getString("starttime")));
            mt.setEndTime(formatter.parse(rs.getString("endtime")));
            
            mt.setKeywordNum(rs.getInt("keyword_num"));
            mt.setMiningInterval(rs.getInt("mininginterval"));
            mt.setName(rs.getString("name"));
            mt.setStatus(rs.getInt("status"));
            mt.setTag(rs.getString("tag"));
            mt.setTopicNum(rs.getInt("topic_num"));
            mt.setQrtzJobName(rs.getString("qrtz_job_name"));
            mt.setQrtzJobExecCount(rs.getInt("qrtz_job_exec_count"));
            mt.setQrtzJobTotalCount(rs.getInt("qrtz_job_total_count"));
            mt.setPreprocessComponent(rs.getString("preprocess_component"));
            mt.setMiningComponent(rs.getString("mining_component"));
            mt.setTrackingComponent(rs.getString("tracking_component"));
            
            mt.setAlpha(rs.getDouble("alpha"));
            mt.setBeta(rs.getDouble("beta"));
            
            return mt;
        } catch (ParseException ex) {
            Logger.getLogger(MiningTaskRowMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
}
