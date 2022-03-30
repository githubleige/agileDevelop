package com.suning.zhongtai.wf.dao.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.suning.zhongtai.wf.dao.entity.WfCndNodeItem;
import org.springframework.stereotype.Service;


public class WfCndItemRowMapper implements RowMapper<WfCndNodeItem> {
    @Override
    public WfCndNodeItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        WfCndNodeItem cndItem = new WfCndNodeItem();
        cndItem.setId(rs.getLong("ID"));
        cndItem.setCndCode(rs.getString("CND_CODE"));
        cndItem.setBndCode(rs.getString("BND_CODE"));
        cndItem.setStepNum(rs.getInt("STEP_NUM"));
        return cndItem;
    }
}
