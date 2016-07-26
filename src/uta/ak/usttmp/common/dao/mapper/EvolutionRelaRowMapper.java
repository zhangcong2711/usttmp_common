/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uta.ak.usttmp.common.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import uta.ak.usttmp.common.model.EvolutionRelationship;
import uta.ak.usttmp.common.model.Topic;

/**
 *
 * @author zhangcong
 */
public class EvolutionRelaRowMapper implements RowMapper{
    
    private JdbcTemplate jdbcTemplate;

    public EvolutionRelaRowMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate=jdbcTemplate;
    }
    
    

    @Override
    public Object mapRow(ResultSet rs, 
                         int rowNum) throws SQLException {
        
        EvolutionRelationship er=new EvolutionRelationship();
        
        String queryTp="select * from c_topic where mme_eid=?";
        Topic preTopic=(Topic) this.jdbcTemplate.queryForObject(queryTp, 
                                                        new TopicRowMapper(), 
                                                        rs.getLong("pre_topic_id"));
        Topic nextTopic=(Topic) this.jdbcTemplate.queryForObject(queryTp, 
                                                        new TopicRowMapper(), 
                                                        rs.getLong("next_topic_id"));
        
        er.setNextTopic(nextTopic);
        er.setPreTopic(preTopic);
        er.setRankAgainstNextTopicInPreGroup(rs.getInt("rank_against_next_topic_in_pre_group"));
        er.setRankAgainstPreTopicInNextGroup(rs.getInt("rank_against_pre_topic_in_next_group"));
        er.setSimilarity(rs.getDouble("similarity"));
        er.setMiningTaskId(rs.getLong("miningtask_id"));
        er.setPreTopicSeq(rs.getInt("pre_topic_seq"));
        er.setNextTopicSeq(rs.getInt("next_topic_seq"));
        
        
        return er;
    }
    
}
