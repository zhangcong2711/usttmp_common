/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uta.ak.usttmp.common.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import org.springframework.jdbc.core.RowMapper;
import uta.ak.usttmp.common.model.Topic;
import uta.ak.usttmp.common.model.WordProbability;

/**
 *
 * @author zhangcong
 */
public class TopicRowMapper implements RowMapper {

    @Override
    public Object mapRow(ResultSet rs, 
                         int rowNum) throws SQLException {
        
        Topic tp =new Topic();
        tp.setId(rs.getLong("mme_eid"));
        tp.setName(rs.getString("name"));
        tp.setRemark(rs.getString("remark"));
        
        String content=rs.getString("content");
        String[] wordProbs=content.split(",");
        for(String wpstr : wordProbs){
            String[] wp=wpstr.split(":");
            tp.addWordProbability(wp[0], Double.parseDouble(wp[1]));
        }
        
        return tp;
    }
    
}
