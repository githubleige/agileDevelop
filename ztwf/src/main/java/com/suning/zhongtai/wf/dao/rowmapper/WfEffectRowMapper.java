package com.suning.zhongtai.wf.dao.rowmapper;

import com.suning.zhongtai.wf.dao.entity.WfEffectiveResult;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WfEffectRowMapper implements RowMapper<WfEffectiveResult> {
    @Override
    public WfEffectiveResult mapRow(ResultSet rs, int rowNum) throws SQLException {
        WfEffectiveResult effectiveresult = new WfEffectiveResult();
        effectiveresult.setId(rs.getLong("ID"));
        effectiveresult.setServerIp(rs.getString("SERVER_IP"));
        effectiveresult.setVersionNum(rs.getString("VERSION_NUM"));
        effectiveresult.setStatus(rs.getInt("STATUS"));
        effectiveresult.setBeginTime(rs.getTimestamp("BEGIN_TIME"));
        effectiveresult.setEndTime(rs.getTimestamp("END_TIME"));
        return effectiveresult;
    }
}
