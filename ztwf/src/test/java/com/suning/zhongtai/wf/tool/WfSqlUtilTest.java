package com.suning.zhongtai.wf.tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

public class WfSqlUtilTest {

  @Test
  public void inSqlParam() {
      List<String> idList = new ArrayList<>();
      idList.add("1l");
      idList.add("2l");
      Map<String, Object> param = WfSqlUtil.inSqlParam(idList); 
      Assert.assertEquals(param.get("inSql"), ":var1,:var2");
      Assert.assertEquals(param.get("var1"), 1l);
      Assert.assertEquals(param.get("var2"), 2l);
  }
}
