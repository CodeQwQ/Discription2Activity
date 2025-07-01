package project.ucmeta;

import project.ucmeta.UCMetaModel.*;

/**
 * UCMeta解析器 - 用于创建示例UCMeta数据
 * 在实际项目中，这里应该从文件或数据库中解析UCMeta数据
 */
public class UCMetaParser {
    
    /**
     * 创建一个示例用例：用户登录系统
     */
    public static UseCase createLoginUseCase() {
        UseCase loginUseCase = new UseCase("UserLogin");
        loginUseCase.setDescription("用户登录系统的用例");
        
        // 添加前置条件
        loginUseCase.getPreconditions().add("系统已启动");
        loginUseCase.getPreconditions().add("用户未登录");
        
        // 添加后置条件
        loginUseCase.getPostconditions().add("用户已成功登录");
        loginUseCase.getPostconditions().add("系统显示主界面");
        
        // 主流程
        // 1. 用户输入用户名和密码
        SimpleSentence step1 = new SimpleSentence(
            "step1", 
            "用户输入用户名和密码", 
            "用户", 
            "输入", 
            "登录信息", 
            TransactionType.INITIATION
        );
        loginUseCase.getMainFlow().add(step1);
        
        // 2. 系统验证用户信息
        ConditionCheckSentence step2 = new ConditionCheckSentence(
            "step2", 
            "系统验证用户信息", 
            "用户信息有效"
        );
        
        // 备选流：用户信息无效
        SimpleSentence alt1 = new SimpleSentence(
            "alt1", 
            "系统显示错误信息", 
            "系统", 
            "显示", 
            "错误信息", 
            TransactionType.RESPONSE_TO_PRIMARY_ACTOR
        );
        SimpleSentence alt2 = new SimpleSentence(
            "alt2", 
            "用户重新输入", 
            "用户", 
            "重新输入", 
            "登录信息", 
            TransactionType.INITIATION
        );
        step2.getAlternativeFlow().add(alt1);
        step2.getAlternativeFlow().add(alt2);
        
        loginUseCase.getMainFlow().add(step2);
        
        // 3. 系统记录登录日志
        SimpleSentence step3 = new SimpleSentence(
            "step3", 
            "系统记录登录日志", 
            "系统", 
            "记录", 
            "登录日志", 
            TransactionType.INTERNAL_TRANSACTION
        );
        loginUseCase.getMainFlow().add(step3);
        
        // 4. 系统显示主界面
        SimpleSentence step4 = new SimpleSentence(
            "step4", 
            "系统显示主界面", 
            "系统", 
            "显示", 
            "主界面", 
            TransactionType.RESPONSE_TO_PRIMARY_ACTOR
        );
        loginUseCase.getMainFlow().add(step4);
        
        // 添加全局备选流：用户取消操作
        GlobalAlternativeFlow cancelFlow = new GlobalAlternativeFlow("用户按取消按钮");
        AbortSentence cancelAction = new AbortSentence("cancel", "用户取消登录操作");
        cancelFlow.getSentences().add(cancelAction);
        loginUseCase.getGlobalAlternativeFlows().add(cancelFlow);
        
        return loginUseCase;
    }
    
    /**
     * 创建一个复杂示例用例：在线购物
     */
    public static UseCase createOnlineShoppingUseCase() {
        UseCase shoppingUseCase = new UseCase("OnlineShopping");
        shoppingUseCase.setDescription("用户在线购物的用例");
        
        // 前置条件
        shoppingUseCase.getPreconditions().add("用户已登录");
        shoppingUseCase.getPreconditions().add("商品库存充足");
        
        // 后置条件
        shoppingUseCase.getPostconditions().add("订单已生成");
        shoppingUseCase.getPostconditions().add("库存已更新");
        
        // 主流程
        // 1. 用户浏览商品
        SimpleSentence step1 = new SimpleSentence(
            "shop_step1", 
            "用户浏览商品", 
            "用户", 
            "浏览", 
            "商品列表", 
            TransactionType.INITIATION
        );
        shoppingUseCase.getMainFlow().add(step1);
        
        // 2. 条件句：如果用户找到心仪商品
        ConditionalSentence step2 = new ConditionalSentence(
            "shop_step2", 
            "如果用户找到心仪商品", 
            "找到心仪商品"
        );
        
        // THEN分支：添加到购物车
        SimpleSentence then1 = new SimpleSentence(
            "then1", 
            "用户添加商品到购物车", 
            "用户", 
            "添加", 
            "商品", 
            TransactionType.INITIATION
        );
        step2.getThenBranch().add(then1);
        
        // ELSE分支：继续浏览
        SimpleSentence else1 = new SimpleSentence(
            "else1", 
            "用户继续浏览其他商品", 
            "用户", 
            "浏览", 
            "其他商品", 
            TransactionType.INITIATION
        );
        step2.getElseBranch().add(else1);
        
        shoppingUseCase.getMainFlow().add(step2);
        
        // 3. 并行句：同时进行库存检查和价格计算
        ParallelSentence step3 = new ParallelSentence(
            "shop_step3", 
            "同时进行库存检查和价格计算"
        );
        
        // 并行分支1：库存检查
        java.util.List<Sentence> branch1 = new java.util.ArrayList<>();
        SimpleSentence parallel1 = new SimpleSentence(
            "parallel1", 
            "系统检查商品库存", 
            "系统", 
            "检查", 
            "库存", 
            TransactionType.INTERNAL_TRANSACTION
        );
        branch1.add(parallel1);
        step3.getParallelBranches().add(branch1);
        
        // 并行分支2：价格计算
        java.util.List<Sentence> branch2 = new java.util.ArrayList<>();
        SimpleSentence parallel2 = new SimpleSentence(
            "parallel2", 
            "系统计算总价格", 
            "系统", 
            "计算", 
            "总价格", 
            TransactionType.INTERNAL_TRANSACTION
        );
        branch2.add(parallel2);
        step3.getParallelBranches().add(branch2);
        
        shoppingUseCase.getMainFlow().add(step3);
        
        // 4. 迭代句：重复确认订单直到用户满意
        IterativeSentence step4 = new IterativeSentence(
            "shop_step4", 
            "重复确认订单直到用户满意", 
            "用户不满意订单"
        );
        
        SimpleSentence iterate1 = new SimpleSentence(
            "iterate1", 
            "系统显示订单详情", 
            "系统", 
            "显示", 
            "订单详情", 
            TransactionType.RESPONSE_TO_PRIMARY_ACTOR
        );
        SimpleSentence iterate2 = new SimpleSentence(
            "iterate2", 
            "用户确认或修改订单", 
            "用户", 
            "确认", 
            "订单", 
            TransactionType.INITIATION
        );
        step4.getBody().add(iterate1);
        step4.getBody().add(iterate2);
        
        shoppingUseCase.getMainFlow().add(step4);
        
        // 5. 包含其他用例：支付处理
        IncludeSentence step5 = new IncludeSentence(
            "shop_step5", 
            "包含支付处理用例", 
            "PaymentProcessing"
        );
        shoppingUseCase.getMainFlow().add(step5);
        
        // 6. 最终确认
        SimpleSentence step6 = new SimpleSentence(
            "shop_step6", 
            "系统生成订单确认", 
            "系统", 
            "生成", 
            "订单确认", 
            TransactionType.RESPONSE_TO_PRIMARY_ACTOR
        );
        shoppingUseCase.getMainFlow().add(step6);
        
        return shoppingUseCase;
    }
    
    /**
     * 创建一个包含恢复步骤的示例用例
     */
    public static UseCase createFileUploadUseCase() {
        UseCase uploadUseCase = new UseCase("FileUpload");
        uploadUseCase.setDescription("文件上传用例");
        
        // 主流程
        SimpleSentence step1 = new SimpleSentence(
            "upload_step1", 
            "用户选择文件", 
            "用户", 
            "选择", 
            "文件", 
            TransactionType.INITIATION
        );
        uploadUseCase.getMainFlow().add(step1);
        
        SimpleSentence step2 = new SimpleSentence(
            "upload_step2", 
            "系统验证文件格式", 
            "系统", 
            "验证", 
            "文件格式", 
            TransactionType.INTERNAL_TRANSACTION
        );
        uploadUseCase.getMainFlow().add(step2);
        
        // 条件检查：文件格式是否正确
        ConditionCheckSentence step3 = new ConditionCheckSentence(
            "upload_step3", 
            "检查文件格式是否正确", 
            "文件格式正确"
        );
        
        // 备选流：格式错误时恢复到步骤1
        SimpleSentence alt1 = new SimpleSentence(
            "upload_alt1", 
            "系统显示格式错误信息", 
            "系统", 
            "显示", 
            "错误信息", 
            TransactionType.RESPONSE_TO_PRIMARY_ACTOR
        );
        ResumeStepSentence resume = new ResumeStepSentence(
            "upload_resume", 
            "恢复到文件选择步骤", 
            "upload_step1"
        );
        step3.getAlternativeFlow().add(alt1);
        step3.getAlternativeFlow().add(resume);
        
        uploadUseCase.getMainFlow().add(step3);
        
        SimpleSentence step4 = new SimpleSentence(
            "upload_step4", 
            "系统上传文件", 
            "系统", 
            "上传", 
            "文件", 
            TransactionType.INTERNAL_TRANSACTION
        );
        uploadUseCase.getMainFlow().add(step4);
        
        return uploadUseCase;
    }
}
