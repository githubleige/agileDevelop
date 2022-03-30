中前台底层框架的使用：
首先核心四张表：
-- gomstpub.wf_node definition
基础结点表包括标准节点和组合结点。标准节点指的是里面只有一个结点执行逻辑。
为什么需要分标准结点和组合结点两种，其实基本的执行单位都是标准结点。标准节点和组合结点的区别就是，为了把一些具有可以聚合的节点放在一起，这样可以减少
流程的步骤长度，好维护。其他流程再次调用相似逻辑的话就可以，直接把组合结点拿过来使用。同时组合结点里面有的相似逻辑可以并发的去执行，提高执行效率。具体
再执行组合结点的时候是并发执行还是串行执行，由业务和产品量决定。配置只要在这张表对应的组合结点的CND_CALL_TYPE字段来配置0还是1就可以了
CREATE TABLE `wf_node` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `NODE_CODE` varchar(50) NOT NULL COMMENT '节点编码',
  `NODE_NAME` varchar(256) NOT NULL COMMENT '节点名称',
  `NODE_TYPE` int(1) NOT NULL COMMENT '节点类型 0 标准节点 1 组合节点',
  `NODE_STATE` int(1) NOT NULL COMMENT '节点状态 0 未启用 1 启用',
  `ROLLBACK_FLAG` int(1) NOT NULL COMMENT '是否支持回滚 0 不支持 1 支持',
  `CND_CALL_TYPE` int(1) NOT NULL COMMENT '组合节点执行方式 0 串行 1 并行',
  `NODE_DESCRIPTION` varchar(512) DEFAULT NULL COMMENT '节点描述',
  `WF_VERSION` varchar(100) NOT NULL DEFAULT '' COMMENT '版本号',
  `CREATED_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `LAST_UPD_TIME` timestamp NOT NULL DEFAULT '2018-12-10 00:00:00' COMMENT '更新时间',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UX1_WF_NODE` (`NODE_CODE`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=12283 DEFAULT CHARSET=utf8mb4 COMMENT='流程引擎节点表';

-- gomstpub.wf_cnd_item definition
然后还有就是组合结点了，这张组合结点表是在基础结点表扩展出来的，基础节点表中的组合结点需要在这张表中进行配置，把组合结点对应的标准节点配置出来。同时
给出在组合结点中的标准节点的执行步骤。如果组合结点里面的标准节点不能并发执行，我们这里需要根据顺序号进行顺序执行。在进行模板填充的时候，我们会发现
组合结点里面多了一个 private final Map<Integer, StandardWorkflowNode> cndNodes = new TreeMap<>();属性，主要就是用来放组合节点对应的接触结点的。
key是执行的顺序号，value是StandardWorkflowNode
CREATE TABLE `wf_cnd_item` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `CND_CODE` varchar(50) NOT NULL COMMENT '组合节点编码',
  `BND_CODE` varchar(50) NOT NULL COMMENT '标准节点编码',
  `STEP_NUM` int(10) NOT NULL COMMENT '步骤号',
  `CREATED_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `LAST_UPD_TIME` timestamp NOT NULL DEFAULT '2018-12-10 00:00:00' COMMENT '更新时间',
  `WF_VERSION` varchar(100) NOT NULL DEFAULT '' COMMENT '版本号',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UX1_WF_CND_ITEM` (`CND_CODE`,`BND_CODE`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=12283 DEFAULT CHARSET=utf8mb4 COMMENT='组合节点子项表';
然后就是执行步骤表了。对应的WorkflowStep里面有几个比较重要的属性：stepNum、nextStepNumY、nextStepNumN，然后就是维护一个private BaseWorkflowNode node;
真正执行也是先执行BaseWorkflowNode里面的 protected IWorkflowNodeExecutable workflowNodeExecutable;

-- gomstpub.wf_step definition

CREATE TABLE `wf_step` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `WF_ID` bigint(15) DEFAULT NULL COMMENT '模板id',
  `WF_CODE` varchar(50) NOT NULL,
  `NODE_CODE` varchar(50) NOT NULL COMMENT '节点编码',
  `STEP_NUM` int(10) NOT NULL COMMENT '步骤号',
  `NEXT_STEP_N` int(10) NOT NULL COMMENT '返回值为N时的下一个步骤号',
  `NEXT_STEP_Y` int(10) NOT NULL COMMENT '返回值为Y时的下一个步骤号',
  `ROLLBACK_FLAG` int(1) NOT NULL COMMENT '是否支持回滚 0 不支持 1 支持',
  `CREATED_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `LAST_UPD_TIME` timestamp NOT NULL DEFAULT '2018-12-10 00:00:00' COMMENT '更新时间',
  `WF_VERSION` varchar(100) NOT NULL DEFAULT '' COMMENT '版本号',
  PRIMARY KEY (`ID`),
  KEY `IX1_WF_STEP` (`WF_ID`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=24571 DEFAULT CHARSET=utf8mb4 COMMENT='流程步骤定义表';

-- gomstpub.wf_definition definition

CREATE TABLE `wf_definition` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `WF_CODE` varchar(50) NOT NULL COMMENT '流程编码',
  `WF_ID` int(11) DEFAULT NULL COMMENT '模板id',
  `WF_NAME` varchar(256) NOT NULL COMMENT '流程名称',
  `WF_VERSION` varchar(100) NOT NULL COMMENT '流程版本',
  `WF_STATE` int(1) NOT NULL COMMENT '流程状态 0 未启用 1 启用',
  `WF_TYPE_CODE` varchar(50) NOT NULL COMMENT '流程类型编码',
  `BIZ_SCENE_CODE` varchar(2048) NOT NULL COMMENT '业务场景编码',
  `WF_DESCRIPTION` varchar(512) DEFAULT NULL COMMENT '流程描述',
  `CREATED_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `LAST_UPD_TIME` timestamp NOT NULL DEFAULT '2018-12-10 00:00:00' COMMENT '更新时间',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UX1_WF_DEFINITION` (`WF_CODE`,`WF_VERSION`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1531 DEFAULT CHARSET=utf8mb4 COMMENT='流程模板定义表';
('P_ORD_SBMT_OLN',1,'订单提交（易购标准）','GOMST_20211011_10',1,'ORD_SBMT','线上标准,线上拼购,线上融合,零售云,线下标准,线下家乐福','订单提交（易购标准）','2021-09-28 11:25:32.0','2021-09-28 11:25:32.0')



@Documented
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface WorkflowNode {
    /**
     * 节点编码
     */
    public String nodeCode();
}
我们定义了一个注解，这个注解需要和spring配合使用，根启动类会实现ApplicationListener<ApplicationContextEvent>，在容器加载完成后，我们会触发
onApplicationEvent方法，所有的加载填充流程就是在这里完成的，我们会扫描所有的bean,把用注解WorkflowNode修饰的结点全部都放进一个map里面去，
//这是一个map,map的key节点@WorkflowNode(nodeCode = "ND_SO_CHK_MN")中的nodeCode属性值，
//Object就是bean对应的节点对象
public static final Map<String, Object> EXECUTABLES = new HashMap<>();：
这个map会在后面的填充结点的时候使用，用WorkflowNode修饰的结点一定需要实现一个借口，
public interface IWorkflowNodeExecutable extends Serializable {
	//实现的业务逻辑，同时WorkflowDataContextWrapper里面有个属性workflowDataContext，workflowDataContext里面放着上文执行需要的数据，里面封装了List<OrderDTO>数据对象
    int execute(WorkflowDataContextWrapper var1);
	//回滚逻辑，也是交给业务去实现
    int rollback(WorkflowDataContextWrapper var1);
}
我们在填充结点的时候，就是拿着实现IWorkflowNodeExecutable的结点赋值给BndWorkflowNode的protected IWorkflowNodeExecutable workflowNodeExecutable;属性的
然后再结点执行的时候也是获取到这个workflowNodeExecutable来执行的
