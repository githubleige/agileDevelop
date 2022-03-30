package com.suning.zhongtai.wf.dao.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import com.suning.zhongtai.wf.dao.entity.WfNode;

public class WfNodeRowMapper implements RowMapper<WfNode> {
    @Override
    public WfNode mapRow(ResultSet rs, int rowNum) throws SQLException {
        WfNode wfNode = new WfNode();
        wfNode.setId(rs.getLong("ID"));
        wfNode.setNodeCode(StringUtils.upperCase(rs.getString("NODE_CODE")));
        wfNode.setNodeName(rs.getString("NODE_NAME"));
        wfNode.setNodeState(rs.getInt("NODE_STATE"));
        wfNode.setNodeType(rs.getInt("NODE_TYPE"));
        wfNode.setRollbackFlag(rs.getInt("ROLLBACK_FLAG"));
        wfNode.setCndCallType(rs.getInt("CND_CALL_TYPE"));
        return wfNode;
    }
}
