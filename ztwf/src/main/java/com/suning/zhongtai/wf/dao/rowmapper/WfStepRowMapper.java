package com.suning.zhongtai.wf.dao.rowmapper;

import com.suning.zhongtai.wf.dao.entity.WfStep;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WfStepRowMapper implements RowMapper<WfStep> {
    @Override
    public WfStep mapRow(ResultSet rs, int rowNum) throws SQLException {
        WfStep wfStep = new WfStep();
        wfStep.setId(rs.getLong("ID"));
        wfStep.setWfId(rs.getLong("WF_ID"));
        wfStep.setWfCode(rs.getString("WF_CODE"));
        wfStep.setNodeCode(rs.getString("NODE_CODE"));
        wfStep.setRollbackFlag(rs.getInt("ROLLBACK_FLAG"));
        wfStep.setStepNum(rs.getInt("STEP_NUM"));
        wfStep.setNextStepN(rs.getInt("NEXT_STEP_N"));
        wfStep.setNextStepY(rs.getInt("NEXT_STEP_Y"));
        return wfStep;
    }
}
