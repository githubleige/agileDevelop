package com.suning.zhongtai.wf.dao.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.suning.zhongtai.wf.dao.entity.WfDefinition;

public class WfDefinitionRowMapper implements RowMapper<WfDefinition> {
    @Override
    public WfDefinition mapRow(ResultSet rs, int rowNum) throws SQLException {
        WfDefinition wfDefinition = new WfDefinition();
        wfDefinition.setId(rs.getLong("ID"));
        wfDefinition.setWfId(rs.getLong("WF_ID"));
        wfDefinition.setWfCode(rs.getString("WF_CODE"));
        wfDefinition.setWfName(rs.getString("WF_NAME"));
        wfDefinition.setWfState(rs.getInt("WF_STATE"));
        wfDefinition.setWfVersion(rs.getString("WF_VERSION"));
        wfDefinition.setWfTypeCode(rs.getString("WF_TYPE_CODE"));
        return wfDefinition;
    }
}
