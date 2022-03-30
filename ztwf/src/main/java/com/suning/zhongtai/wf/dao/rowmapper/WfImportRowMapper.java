package com.suning.zhongtai.wf.dao.rowmapper;

import com.suning.zhongtai.wf.dao.entity.WfImportResult;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WfImportRowMapper implements RowMapper<WfImportResult> {
    @Override
    public WfImportResult mapRow(ResultSet rs, int rowNum) throws SQLException {
        WfImportResult importResult = new WfImportResult();
        importResult.setId(rs.getLong("ID"));
        importResult.setServerIp(rs.getString("SERVER_IP"));
        importResult.setVersionNum(rs.getString("VERSION_NUM"));
        importResult.setStatus(rs.getInt("STATUS"));
        importResult.setBeginTime(rs.getTimestamp("BEGIN_TIME"));
        importResult.setEndTime(rs.getTimestamp("END_TIME"));
        return importResult;
    }
}
