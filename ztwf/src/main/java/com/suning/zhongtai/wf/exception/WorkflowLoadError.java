package com.suning.zhongtai.wf.exception;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * 流程引擎加载错误信息，目前只做了节点错误信息，后面可以扩展
 * @author 18040994
 * @date 2019/8/7 16:50
 */
public class WorkflowLoadError implements Serializable {
    private static final long serialVersionUID = 2420978346608022749L;
    //没有业务实现Bean的节点名集合
    private Set<String> notImplNodeNames = new HashSet<>();
    //未定义的节点名集合
    private Set<String> notDefineNodeNames = new HashSet<>();
    //类型错误的节点名集合
    private Set<String> typeErrorNodeNames = new HashSet<>();

    public Set<String> getNotImplNodeNames() {
        return notImplNodeNames;
    }

    public Set<String> getNotDefineNodeNames() {
        return notDefineNodeNames;
    }

    public Set<String> getTypeErrorNodeNames() {
        return typeErrorNodeNames;
    }

    /**
    * 检查是否存在加载错误
     * @return  true-有错误   false-无错误
    * @author 18040994
    * @date 2019/8/7 16:29
    */
    public boolean checkErrorExist(){
        boolean result = false;
        if(notImplNodeNames.size()+notDefineNodeNames.size()+typeErrorNodeNames.size() > 0){
            result = true;
    }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("模板加载过程中发现");
        if(!notDefineNodeNames.isEmpty()){
            //模板中定义了，但是节点表中不存在
            sb.append("未定义的节点(请检查节点表是否存在如下节点信息)，节点编码如下："+notDefineNodeNames.toString()+", ");
        }
        if(!notImplNodeNames.isEmpty()){
            //节点表中存在，但是没有实现类
            sb.append("未实现的的节点(请检查如下节点对应的实现类是否存在或实现类节点编码是否正确)，节点编码如下："+notImplNodeNames.toString()+", ");
        }
        if(!typeErrorNodeNames.isEmpty()){
            //节点实现类存在，但是类型不对，比如组合节点子项又是组合节点
            sb.append("类型错误的的节点(请检查如下节点是否是合法的组合节点子项)，节点编码如下："+typeErrorNodeNames.toString()+", ");
        }
        int lastIndex = sb.lastIndexOf(",");
        if(lastIndex != -1){
            return sb.substring(0, lastIndex);
        }
        else{
            return "";
        }
    }
}
