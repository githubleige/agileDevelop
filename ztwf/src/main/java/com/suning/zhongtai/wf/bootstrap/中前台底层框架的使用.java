��ǰ̨�ײ��ܵ�ʹ�ã�
���Ⱥ������ű�
-- gomstpub.wf_node definition
�������������׼�ڵ����Ͻ�㡣��׼�ڵ�ָ��������ֻ��һ�����ִ���߼���
Ϊʲô��Ҫ�ֱ�׼������Ͻ�����֣���ʵ������ִ�е�λ���Ǳ�׼��㡣��׼�ڵ����Ͻ���������ǣ�Ϊ�˰�һЩ���п��ԾۺϵĽڵ����һ���������Լ���
���̵Ĳ��賤�ȣ���ά�������������ٴε��������߼��Ļ��Ϳ��ԣ�ֱ�Ӱ���Ͻ���ù���ʹ�á�ͬʱ��Ͻ�������е������߼����Բ�����ȥִ�У����ִ��Ч�ʡ�����
��ִ����Ͻ���ʱ���ǲ���ִ�л��Ǵ���ִ�У���ҵ��Ͳ�Ʒ������������ֻҪ�����ű��Ӧ����Ͻ���CND_CALL_TYPE�ֶ�������0����1�Ϳ�����
CREATE TABLE `wf_node` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `NODE_CODE` varchar(50) NOT NULL COMMENT '�ڵ����',
  `NODE_NAME` varchar(256) NOT NULL COMMENT '�ڵ�����',
  `NODE_TYPE` int(1) NOT NULL COMMENT '�ڵ����� 0 ��׼�ڵ� 1 ��Ͻڵ�',
  `NODE_STATE` int(1) NOT NULL COMMENT '�ڵ�״̬ 0 δ���� 1 ����',
  `ROLLBACK_FLAG` int(1) NOT NULL COMMENT '�Ƿ�֧�ֻع� 0 ��֧�� 1 ֧��',
  `CND_CALL_TYPE` int(1) NOT NULL COMMENT '��Ͻڵ�ִ�з�ʽ 0 ���� 1 ����',
  `NODE_DESCRIPTION` varchar(512) DEFAULT NULL COMMENT '�ڵ�����',
  `WF_VERSION` varchar(100) NOT NULL DEFAULT '' COMMENT '�汾��',
  `CREATED_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '����ʱ��',
  `LAST_UPD_TIME` timestamp NOT NULL DEFAULT '2018-12-10 00:00:00' COMMENT '����ʱ��',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UX1_WF_NODE` (`NODE_CODE`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=12283 DEFAULT CHARSET=utf8mb4 COMMENT='��������ڵ��';

-- gomstpub.wf_cnd_item definition
Ȼ���о�����Ͻ���ˣ�������Ͻ������ڻ���������չ�����ģ������ڵ���е���Ͻ����Ҫ�����ű��н������ã�����Ͻ���Ӧ�ı�׼�ڵ����ó�����ͬʱ
��������Ͻ���еı�׼�ڵ��ִ�в��衣�����Ͻ������ı�׼�ڵ㲻�ܲ���ִ�У�����������Ҫ����˳��Ž���˳��ִ�С��ڽ���ģ������ʱ�����ǻᷢ��
��Ͻ���������һ�� private final Map<Integer, StandardWorkflowNode> cndNodes = new TreeMap<>();���ԣ���Ҫ������������Ͻڵ��Ӧ�ĽӴ����ġ�
key��ִ�е�˳��ţ�value��StandardWorkflowNode
CREATE TABLE `wf_cnd_item` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `CND_CODE` varchar(50) NOT NULL COMMENT '��Ͻڵ����',
  `BND_CODE` varchar(50) NOT NULL COMMENT '��׼�ڵ����',
  `STEP_NUM` int(10) NOT NULL COMMENT '�����',
  `CREATED_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '����ʱ��',
  `LAST_UPD_TIME` timestamp NOT NULL DEFAULT '2018-12-10 00:00:00' COMMENT '����ʱ��',
  `WF_VERSION` varchar(100) NOT NULL DEFAULT '' COMMENT '�汾��',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UX1_WF_CND_ITEM` (`CND_CODE`,`BND_CODE`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=12283 DEFAULT CHARSET=utf8mb4 COMMENT='��Ͻڵ������';
Ȼ�����ִ�в�����ˡ���Ӧ��WorkflowStep�����м����Ƚ���Ҫ�����ԣ�stepNum��nextStepNumY��nextStepNumN��Ȼ�����ά��һ��private BaseWorkflowNode node;
����ִ��Ҳ����ִ��BaseWorkflowNode����� protected IWorkflowNodeExecutable workflowNodeExecutable;

-- gomstpub.wf_step definition

CREATE TABLE `wf_step` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `WF_ID` bigint(15) DEFAULT NULL COMMENT 'ģ��id',
  `WF_CODE` varchar(50) NOT NULL,
  `NODE_CODE` varchar(50) NOT NULL COMMENT '�ڵ����',
  `STEP_NUM` int(10) NOT NULL COMMENT '�����',
  `NEXT_STEP_N` int(10) NOT NULL COMMENT '����ֵΪNʱ����һ�������',
  `NEXT_STEP_Y` int(10) NOT NULL COMMENT '����ֵΪYʱ����һ�������',
  `ROLLBACK_FLAG` int(1) NOT NULL COMMENT '�Ƿ�֧�ֻع� 0 ��֧�� 1 ֧��',
  `CREATED_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '����ʱ��',
  `LAST_UPD_TIME` timestamp NOT NULL DEFAULT '2018-12-10 00:00:00' COMMENT '����ʱ��',
  `WF_VERSION` varchar(100) NOT NULL DEFAULT '' COMMENT '�汾��',
  PRIMARY KEY (`ID`),
  KEY `IX1_WF_STEP` (`WF_ID`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=24571 DEFAULT CHARSET=utf8mb4 COMMENT='���̲��趨���';

-- gomstpub.wf_definition definition

CREATE TABLE `wf_definition` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `WF_CODE` varchar(50) NOT NULL COMMENT '���̱���',
  `WF_ID` int(11) DEFAULT NULL COMMENT 'ģ��id',
  `WF_NAME` varchar(256) NOT NULL COMMENT '��������',
  `WF_VERSION` varchar(100) NOT NULL COMMENT '���̰汾',
  `WF_STATE` int(1) NOT NULL COMMENT '����״̬ 0 δ���� 1 ����',
  `WF_TYPE_CODE` varchar(50) NOT NULL COMMENT '�������ͱ���',
  `BIZ_SCENE_CODE` varchar(2048) NOT NULL COMMENT 'ҵ�񳡾�����',
  `WF_DESCRIPTION` varchar(512) DEFAULT NULL COMMENT '��������',
  `CREATED_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '����ʱ��',
  `LAST_UPD_TIME` timestamp NOT NULL DEFAULT '2018-12-10 00:00:00' COMMENT '����ʱ��',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UX1_WF_DEFINITION` (`WF_CODE`,`WF_VERSION`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1531 DEFAULT CHARSET=utf8mb4 COMMENT='����ģ�嶨���';
('P_ORD_SBMT_OLN',1,'�����ύ���׹���׼��','GOMST_20211011_10',1,'ORD_SBMT','���ϱ�׼,����ƴ��,�����ں�,������,���±�׼,���¼��ָ�','�����ύ���׹���׼��','2021-09-28 11:25:32.0','2021-09-28 11:25:32.0')



@Documented
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface WorkflowNode {
    /**
     * �ڵ����
     */
    public String nodeCode();
}
���Ƕ�����һ��ע�⣬���ע����Ҫ��spring���ʹ�ã����������ʵ��ApplicationListener<ApplicationContextEvent>��������������ɺ����ǻᴥ��
onApplicationEvent���������еļ���������̾�����������ɵģ����ǻ�ɨ�����е�bean,����ע��WorkflowNode���εĽ��ȫ�����Ž�һ��map����ȥ��
//����һ��map,map��key�ڵ�@WorkflowNode(nodeCode = "ND_SO_CHK_MN")�е�nodeCode����ֵ��
//Object����bean��Ӧ�Ľڵ����
public static final Map<String, Object> EXECUTABLES = new HashMap<>();��
���map���ں����������ʱ��ʹ�ã���WorkflowNode���εĽ��һ����Ҫʵ��һ����ڣ�
public interface IWorkflowNodeExecutable extends Serializable {
	//ʵ�ֵ�ҵ���߼���ͬʱWorkflowDataContextWrapper�����и�����workflowDataContext��workflowDataContext�����������ִ����Ҫ�����ݣ������װ��List<OrderDTO>���ݶ���
    int execute(WorkflowDataContextWrapper var1);
	//�ع��߼���Ҳ�ǽ���ҵ��ȥʵ��
    int rollback(WorkflowDataContextWrapper var1);
}
������������ʱ�򣬾�������ʵ��IWorkflowNodeExecutable�Ľ�㸳ֵ��BndWorkflowNode��protected IWorkflowNodeExecutable workflowNodeExecutable;���Ե�
Ȼ���ٽ��ִ�е�ʱ��Ҳ�ǻ�ȡ�����workflowNodeExecutable��ִ�е�
