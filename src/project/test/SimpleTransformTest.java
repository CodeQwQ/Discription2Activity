package project.test;

import project.ucmeta.UCMetaModel.*;
import project.rules.UCMetaToActivityTransformer;
import project.rules.ActivityDiagramManager;

/**
 * 简化的转换测试程序
 * 用于验证基本功能是否正常工作
 */
public class SimpleTransformTest {
    
    public static void main(String[] args) {
        System.out.println("=== UCMeta到活动图转换测试 ===\n");
        
        try {
            // 创建一个简单的测试用例
            project.ucmeta.UCMetaModel.UseCase testUseCase = createSimpleTestUseCase();
            
            // 执行转换
            UCMetaToActivityTransformer transformer = new UCMetaToActivityTransformer(true);
            ActivityDiagramManager manager = transformer.transformUseCase(testUseCase);
            
            // 显示结果
            System.out.println("转换完成！生成的活动图包含以下节点：");
            manager.printNodeInfo();
            
            // 保存文件
            manager.saveToFile("simple_test.uml");
            
            System.out.println("\n✅ 测试成功完成！");
            
        } catch (Exception e) {
            System.err.println("❌ 测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 创建一个简单的测试用例
     */
    private static project.ucmeta.UCMetaModel.UseCase createSimpleTestUseCase() {
        project.ucmeta.UCMetaModel.UseCase testCase = new project.ucmeta.UCMetaModel.UseCase("SimpleTest");
        testCase.setDescription("简单测试用例");
        
        // 添加前置条件
        testCase.getPreconditions().add("系统已准备就绪");
        
        // 添加后置条件
        testCase.getPostconditions().add("操作已完成");
        
        // 添加简单句
        SimpleSentence step1 = new SimpleSentence(
            "test_step1",
            "用户启动操作",
            "用户",
            "启动",
            "操作",
            TransactionType.INITIATION
        );
        testCase.getMainFlow().add(step1);
        
        // 添加条件检查句
        ConditionCheckSentence step2 = new ConditionCheckSentence(
            "test_step2",
            "系统检查权限",
            "用户有权限"
        );
        
        // 添加备选流
        SimpleSentence alt = new SimpleSentence(
            "test_alt",
            "系统拒绝操作",
            "系统",
            "拒绝",
            "操作",
            TransactionType.RESPONSE_TO_PRIMARY_ACTOR
        );
        step2.getAlternativeFlow().add(alt);
        
        testCase.getMainFlow().add(step2);
        
        // 添加最终步骤
        SimpleSentence step3 = new SimpleSentence(
            "test_step3",
            "系统完成操作",
            "系统",
            "完成",
            "操作",
            TransactionType.RESPONSE_TO_PRIMARY_ACTOR
        );
        testCase.getMainFlow().add(step3);
        
        return testCase;
    }
    
    /**
     * 测试所有句子类型
     */
    public static void testAllSentenceTypes() {
        System.out.println("=== 测试所有句子类型 ===\n");

        project.ucmeta.UCMetaModel.UseCase comprehensiveTest = new project.ucmeta.UCMetaModel.UseCase("ComprehensiveTest");
        
        // 1. 简单句
        SimpleSentence simple = new SimpleSentence(
            "simple1", "用户执行操作", "用户", "执行", "操作", TransactionType.INITIATION
        );
        comprehensiveTest.getMainFlow().add(simple);
        
        // 2. 条件检查句
        ConditionCheckSentence condCheck = new ConditionCheckSentence(
            "condCheck1", "系统验证条件", "条件满足"
        );
        comprehensiveTest.getMainFlow().add(condCheck);
        
        // 3. 条件句
        ConditionalSentence conditional = new ConditionalSentence(
            "conditional1", "如果条件成立", "条件成立"
        );
        SimpleSentence thenAction = new SimpleSentence(
            "then1", "执行A操作", "系统", "执行", "A操作", TransactionType.INTERNAL_TRANSACTION
        );
        SimpleSentence elseAction = new SimpleSentence(
            "else1", "执行B操作", "系统", "执行", "B操作", TransactionType.INTERNAL_TRANSACTION
        );
        conditional.getThenBranch().add(thenAction);
        conditional.getElseBranch().add(elseAction);
        comprehensiveTest.getMainFlow().add(conditional);
        
        // 4. 并行句
        ParallelSentence parallel = new ParallelSentence("parallel1", "同时执行多个操作");
        
        java.util.List<Sentence> branch1 = new java.util.ArrayList<>();
        branch1.add(new SimpleSentence("p1", "操作1", "系统", "执行", "操作1", TransactionType.INTERNAL_TRANSACTION));
        
        java.util.List<Sentence> branch2 = new java.util.ArrayList<>();
        branch2.add(new SimpleSentence("p2", "操作2", "系统", "执行", "操作2", TransactionType.INTERNAL_TRANSACTION));
        
        parallel.getParallelBranches().add(branch1);
        parallel.getParallelBranches().add(branch2);
        comprehensiveTest.getMainFlow().add(parallel);
        
        // 5. 迭代句
        IterativeSentence iterative = new IterativeSentence("iterative1", "重复操作", "继续条件");
        SimpleSentence iterBody = new SimpleSentence(
            "iter1", "重复执行", "系统", "重复", "操作", TransactionType.INTERNAL_TRANSACTION
        );
        iterative.getBody().add(iterBody);
        comprehensiveTest.getMainFlow().add(iterative);
        
        // 6. 包含句
        IncludeSentence include = new IncludeSentence("include1", "包含其他用例", "OtherUseCase");
        comprehensiveTest.getMainFlow().add(include);
        
        // 7. 扩展句
        ExtendSentence extend = new ExtendSentence("extend1", "被其他用例扩展", "ExtendingUseCase");
        comprehensiveTest.getMainFlow().add(extend);
        
        // 8. 中止句
        AbortSentence abort = new AbortSentence("abort1", "中止操作");
        comprehensiveTest.getMainFlow().add(abort);
        
        try {
            UCMetaToActivityTransformer transformer = new UCMetaToActivityTransformer(true);
            ActivityDiagramManager manager = transformer.transformUseCase(comprehensiveTest);
            
            System.out.println("综合测试完成！生成的活动图包含：");
            manager.printNodeInfo();
            
            manager.saveToFile("comprehensive_test.uml");
            System.out.println("\n✅ 综合测试成功！");
            
        } catch (Exception e) {
            System.err.println("❌ 综合测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
