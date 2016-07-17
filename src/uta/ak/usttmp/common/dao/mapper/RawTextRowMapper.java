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
import uta.ak.usttmp.common.model.RawText;

/**
 *
 * @author zhangcong
 */
public class RawTextRowMapper implements RowMapper{

    @Override
    public Object mapRow(ResultSet rs, 
                         int rowNum) throws SQLException {
        
        try {
            RawText rt=new RawText();
            rt.setId(rs.getLong("mme_eid"));
            
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            rt.setCreateTime(formatter.parse(rs.getString("text_createdate")));
            
            rt.setText(rs.getString("text"));
            rt.setTitle(rs.getString("title"));
            rt.setTag(rs.getString("tag"));
            
            return rt;
        } catch (ParseException ex) {
            Logger.getLogger(RawTextRowMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
}
