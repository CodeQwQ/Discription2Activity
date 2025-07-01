package project.rules;

/**
 * UCMeta到活动图转换示例
 * 演示如何使用ActivityDiagramManager处理不同的转换规则
 */
public class UCMetaToActivityExample {
    
    public static void main(String[] args) throws Exception {
        // 创建活动图管理器
        ActivityDiagramManager manager = new ActivityDiagramManager("UCMetaModel", "UserCaseActivity");
        
        // 场景1：创建基本的活动图结构
        System.out.println("=== 场景1：创建基本结构 ===");
        createBasicStructure(manager);
        
        // 场景2：在指定节点后插入新流程
        System.out.println("\n=== 场景2：在节点后插入流程 ===");
        insertProcessAfterNode(manager);
        
        // 场景3：在指定节点前插入新流程
        System.out.println("\n=== 场景3：在节点前插入流程 ===");
        insertProcessBeforeNode(manager);
        
        // 场景4：连接两个现有节点
        System.out.println("\n=== 场景4：连接现有节点 ===");
        connectExistingNodes(manager);
        
        // 打印节点信息
        manager.printNodeInfo();
        
        // 保存到文件
        manager.saveToFile("ucmeta_activity_example.uml");
    }
    
    /**
     * 场景1：创建基本的活动图结构
     * 模拟：根据UCMeta的用例创建基本的开始-处理-结束流程
     */
    private static void createBasicStructure(ActivityDiagramManager manager) {
        // 创建初始节点
        manager.createInitialNode("start");
        
        // 创建主要处理节点
        manager.createOpaqueAction("login", "用户登录");
        manager.createOpaqueAction("validate", "验证用户信息");
        manager.createOpaqueAction("success", "登录成功");
        
        // 创建最终节点
        manager.createFinalNode("end");
        
        // 创建基本流程
        manager.createControlFlow("flow1", "start", "login", null);
        manager.createControlFlow("flow2", "login", "validate", null);
        manager.createControlFlow("flow3", "validate", "success", null);
        manager.createControlFlow("flow4", "success", "end", null);
        
        System.out.println("✅ 基本结构创建完成");
    }
    
    /**
     * 场景2：在指定节点后插入新流程
     * 模拟：根据UCMeta规则，在验证后添加额外的安全检查
     */
    private static void insertProcessAfterNode(ActivityDiagramManager manager) {
        // 在"validate"节点后插入安全检查
        manager.insertNodeAfter("validate", "security_check", "安全检查", "flow_to_security");
        
        System.out.println("✅ 在验证节点后插入了安全检查流程");
    }
    
    /**
     * 场景3：在指定节点前插入新流程
     * 模拟：根据UCMeta规则，在登录前添加预处理步骤
     */
    private static void insertProcessBeforeNode(ActivityDiagramManager manager) {
        // 在"login"节点前插入预处理
        manager.insertNodeBefore("login", "preprocess", "预处理", "flow_to_preprocess");
        
        System.out.println("✅ 在登录节点前插入了预处理流程");
    }
    
    /**
     * 场景4：连接两个现有节点
     * 模拟：根据UCMeta规则，添加异常处理流程
     */
    private static void connectExistingNodes(ActivityDiagramManager manager) {
        // 创建异常处理节点
        manager.createOpaqueAction("error_handler", "错误处理");
        manager.createOpaqueAction("retry", "重试");
        
        // 从安全检查到错误处理的异常流
        manager.createControlFlow("error_flow", "security_check", "error_handler", "安全检查失败");
        
        // 从错误处理到重试
        manager.createControlFlow("retry_flow", "error_handler", "retry", null);
        
        // 从重试回到登录（形成循环）
        manager.createControlFlow("retry_back", "retry", "login", "重新登录");
        
        System.out.println("✅ 添加了异常处理和重试机制");
    }
}

/**
 * UCMeta规则处理器接口
 * 定义不同规则如何转换为活动图元素
 */
interface UCMetaRuleProcessor {
    void processRule(ActivityDiagramManager manager, UCMetaRule rule);
}

/**
 * UCMeta规则数据结构（示例）
 */
class UCMetaRule {
    private String ruleType;
    private String sourceElement;
    private String targetElement;
    private String action;
    private String condition;
    
    // 构造函数和getter/setter方法
    public UCMetaRule(String ruleType, String sourceElement, String targetElement, String action) {
        this.ruleType = ruleType;
        this.sourceElement = sourceElement;
        this.targetElement = targetElement;
        this.action = action;
    }
    
    // Getters
    public String getRuleType() { return ruleType; }
    public String getSourceElement() { return sourceElement; }
    public String getTargetElement() { return targetElement; }
    public String getAction() { return action; }
    public String getCondition() { return condition; }
    
    // Setters
    public void setCondition(String condition) { this.condition = condition; }
}

/**
 * 具体的规则处理器实现
 */
class SequenceRuleProcessor implements UCMetaRuleProcessor {
    @Override
    public void processRule(ActivityDiagramManager manager, UCMetaRule rule) {
        // 处理顺序规则：创建从源到目标的控制流
        String flowId = "flow_" + rule.getSourceElement() + "_to_" + rule.getTargetElement();
        manager.createControlFlow(flowId, rule.getSourceElement(), rule.getTargetElement(), null);
    }
}

class InsertionRuleProcessor implements UCMetaRuleProcessor {
    @Override
    public void processRule(ActivityDiagramManager manager, UCMetaRule rule) {
        // 处理插入规则：在指定位置插入新节点
        if ("after".equals(rule.getRuleType())) {
            manager.insertNodeAfter(rule.getSourceElement(), rule.getTargetElement(), 
                                  rule.getAction(), "flow_insert_after");
        } else if ("before".equals(rule.getRuleType())) {
            manager.insertNodeBefore(rule.getSourceElement(), rule.getTargetElement(), 
                                   rule.getAction(), "flow_insert_before");
        }
    }
}
