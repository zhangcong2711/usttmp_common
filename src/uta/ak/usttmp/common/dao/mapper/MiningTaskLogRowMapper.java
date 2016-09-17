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
import uta.ak.usttmp.common.model.MiningTaskLog;

/**
 *
 * @author zhangcong
 */
public class MiningTaskLogRowMapper implements RowMapper{

    @Override
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
        
        try {
            MiningTaskLog mtl=new MiningTaskLog();
            
            mtl.setId(rs.getLong("mme_eid"));
            mtl.setInfo(rs.getString("exception_info"));
            
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            mtl.setOccurrenceTime(formatter.parse(rs.getString("exception_time")));
            
            mtl.setMiningTaskId(rs.getLong("miningtask_id"));
            mtl.setType(rs.getInt("type"));
            
            return mtl;
        } catch (ParseException ex) {
            Logger.getLogger(MiningTaskLogRowMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
}
