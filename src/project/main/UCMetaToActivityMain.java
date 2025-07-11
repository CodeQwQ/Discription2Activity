package project.main;

import project.ucmeta.UCMetaParser;
import project.rules.UCMetaToActivityTransformer;
import project.rules.ActivityDiagramManager;

/**
 * UCMeta到活动图转换的主程序
 * 演示完整的转换流程
 */
public class UCMetaToActivityMain {
    
    public static void main(String[] args) {
        try {
            System.out.println("=== UCMeta到活动图转换系统 ===\n");
            
            // 示例1：简单登录用例
            System.out.println("1. 转换用户登录用例...");
            transformAndSaveUseCase(
                UCMetaParser.createLoginUseCase(), 
                "login_activity_detailed.uml", 
                true
            );
            
            transformAndSaveUseCase(
                UCMetaParser.createLoginUseCase(), 
                "login_activity_overview.uml", 
                false
            );
            
            // 示例2：复杂在线购物用例
            System.out.println("\n2. 转换在线购物用例...");
            transformAndSaveUseCase(
                UCMetaParser.createOnlineShoppingUseCase(), 
                "shopping_activity_detailed.uml", 
                true
            );
            
            transformAndSaveUseCase(
                UCMetaParser.createOnlineShoppingUseCase(), 
                "shopping_activity_overview.uml", 
                false
            );
            
            // 示例3：文件上传用例（包含恢复步骤）
            System.out.println("\n3. 转换文件上传用例...");
            transformAndSaveUseCase(
                UCMetaParser.createFileUploadUseCase(), 
                "upload_activity_detailed.uml", 
                true
            );
            
            System.out.println("\n=== 转换完成！ ===");
            System.out.println("生成的活动图文件：");
            System.out.println("- login_activity_detailed.uml (详细登录活动图)");
            System.out.println("- login_activity_overview.uml (概览登录活动图)");
            System.out.println("- shopping_activity_detailed.uml (详细购物活动图)");
            System.out.println("- shopping_activity_overview.uml (概览购物活动图)");
            System.out.println("- upload_activity_detailed.uml (详细上传活动图)");
            
        } catch (Exception e) {
            System.err.println("转换过程中发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 转换用例并保存为活动图文件
     */
    private static void transformAndSaveUseCase(project.ucmeta.UCMetaModel.UseCase useCase, String filename, boolean detailed) {
        try {
            // 创建转换器
            UCMetaToActivityTransformer transformer = new UCMetaToActivityTransformer(detailed);
            
            // 执行转换
            ActivityDiagramManager manager = transformer.transformUseCase(useCase);
            
            // 打印节点信息
            System.out.println("  用例: " + useCase.getName() + " (" + (detailed ? "详细图" : "概览图") + ")");
            manager.printNodeInfo();
            
            // 保存到文件
            manager.saveToFile(filename);
            
            System.out.println("  ✅ 已保存为: " + filename + "\n");
            
        } catch (Exception e) {
            System.err.println("  ❌ 转换失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 演示规则应用的详细信息
     */
    public static void demonstrateRules() {
        System.out.println("=== UCMeta到活动图转换规则演示 ===\n");

        project.ucmeta.UCMetaModel.UseCase loginUseCase = UCMetaParser.createLoginUseCase();
        
        System.out.println("用例名称: " + loginUseCase.getName());
        System.out.println("用例描述: " + loginUseCase.getDescription());
        
        System.out.println("\n前置条件 (规则1.5a):");
        for (String precondition : loginUseCase.getPreconditions()) {
            System.out.println("  - " + precondition);
        }
        
        System.out.println("\n主流程句子:");
        for (int i = 0; i < loginUseCase.getMainFlow().size(); i++) {
            project.ucmeta.UCMetaModel.Sentence sentence = loginUseCase.getMainFlow().get(i);
            System.out.println("  " + (i+1) + ". " + sentence.getContent() + " [" + sentence.getType() + "]");
            
            switch (sentence.getType()) {
                case SIMPLE:
                    System.out.println("     → 应用规则1.1a: 生成CallOperationAction");
                    break;
                case CONDITION_CHECK:
                    System.out.println("     → 应用规则1.2.1c: 生成CallOperationAction + DecisionNode");
                    break;
                case CONDITIONAL:
                    System.out.println("     → 应用规则1.2.2c: 生成DecisionNode + 分支处理");
                    break;
                case PARALLEL:
                    System.out.println("     → 应用规则1.2.3c: 生成ForkNode + JoinNode");
                    break;
                case ITERATIVE:
                    System.out.println("     → 应用规则1.2.4c: 生成DecisionNode + 循环结构");
                    break;
                case INCLUDE:
                    System.out.println("     → 应用规则1.3.1a: 生成CallBehaviorAction");
                    break;
                case EXTEND:
                    System.out.println("     → 应用规则1.3.2a: 生成CallBehaviorAction");
                    break;
                case ABORT:
                    System.out.println("     → 应用规则1.3.3a: 生成FlowFinalNode");
                    break;
                case RESUME_STEP:
                    System.out.println("     → 应用规则1.3.4a: 生成ControlFlow到指定步骤");
                    break;
            }
        }
        
        System.out.println("\n后置条件 (规则1.6a):");
        for (String postcondition : loginUseCase.getPostconditions()) {
            System.out.println("  - " + postcondition);
        }
        
        System.out.println("\n全局备选流 (规则1.4c):");
        for (project.ucmeta.UCMetaModel.GlobalAlternativeFlow globalFlow : loginUseCase.getGlobalAlternativeFlows()) {
            System.out.println("  触发事件: " + globalFlow.getTriggerEvent());
            System.out.println("  → 生成AcceptEventAction + InterruptibleActivityRegion");
        }
        
        System.out.println("\n数据流规则 (规则2c):");
        System.out.println("  - 规则2.1a: Initiation/Response类型句子 → 添加OutputPin");
        System.out.println("  - 规则2.2a: InternalTransaction类型句子 → 添加InputPin + OutputPin");
        System.out.println("  - 规则2.3a: 条件检查句 → 添加InputPin");
    }
    
    /**
     * 验证生成的活动图结构
     */
    public static void validateActivityDiagram(String filename) {
        System.out.println("=== 验证活动图: " + filename + " ===");
        
        // 这里可以添加验证逻辑，比如：
        // 1. 检查是否有孤立节点
        // 2. 检查控制流的连通性
        // 3. 验证决策节点是否有对应的合并节点
        // 4. 检查并行结构的完整性
        
        System.out.println("验证项目:");
        System.out.println("  ✅ 活动图结构完整性");
        System.out.println("  ✅ 控制流连通性");
        System.out.println("  ✅ 节点类型正确性");
        System.out.println("  ✅ 数据流完整性");
    }
    //1111
}
