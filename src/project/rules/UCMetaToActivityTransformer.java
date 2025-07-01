package project.rules;

import project.ucmeta.UCMetaModel.*;
import org.eclipse.uml2.uml.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * UCMeta到活动图的转换引擎
 * 实现所有转换规则
 */
public class UCMetaToActivityTransformer {
    
    private ActivityDiagramManager manager;
    private Map<String, String> stepToNodeMapping; // 步骤ID到节点ID的映射
    private int nodeCounter = 0;
    private boolean generateDetailedDiagram = true; // 是否生成详细活动图
    
    public UCMetaToActivityTransformer(boolean generateDetailedDiagram) {
        this.generateDetailedDiagram = generateDetailedDiagram;
        this.stepToNodeMapping = new HashMap<>();
    }
    
    /**
     * 规则1c：为每个用例生成一个活动图实例
     */
    public ActivityDiagramManager transformUseCase(project.ucmeta.UCMetaModel.UseCase useCase) {
        // 创建活动图管理器
        manager = new ActivityDiagramManager(useCase.getName() + "_Model", useCase.getName() + "_Activity");
        
        // 规则1.5a：处理前置条件
        for (String precondition : useCase.getPreconditions()) {
            manager.addPrecondition("Precondition_" + nodeCounter++, precondition);
        }
        
        // 创建初始节点
        String startNodeId = "start_" + useCase.getName();
        manager.createInitialNode(startNodeId);
        
        // 处理主流程
        String lastNodeId = startNodeId;
        for (Sentence sentence : useCase.getMainFlow()) {
            String currentNodeId = processSentence(sentence, lastNodeId);
            lastNodeId = currentNodeId;
        }
        
        // 创建最终节点
        String endNodeId = "end_" + useCase.getName();
        manager.createFinalNode(endNodeId);
        
        // 连接到最终节点
        if (lastNodeId != null && !lastNodeId.equals(endNodeId)) {
            manager.createControlFlow("flow_to_end", lastNodeId, endNodeId, null);
        }
        
        // 规则1.6a：处理后置条件
        for (String postcondition : useCase.getPostconditions()) {
            manager.addPostcondition("Postcondition_" + nodeCounter++, postcondition);
        }
        
        // 规则1.4c：处理全局备选流
        for (GlobalAlternativeFlow globalFlow : useCase.getGlobalAlternativeFlows()) {
            processGlobalAlternativeFlow(globalFlow);
        }
        
        return manager;
    }
    
    /**
     * 处理单个句子，返回最后一个节点的ID
     */
    private String processSentence(Sentence sentence, String previousNodeId) {
        switch (sentence.getType()) {
            case SIMPLE:
                return processSimpleSentence((SimpleSentence) sentence, previousNodeId);
            case CONDITION_CHECK:
                return processConditionCheckSentence((ConditionCheckSentence) sentence, previousNodeId);
            case CONDITIONAL:
                return processConditionalSentence((ConditionalSentence) sentence, previousNodeId);
            case PARALLEL:
                return processParallelSentence((ParallelSentence) sentence, previousNodeId);
            case ITERATIVE:
                return processIterativeSentence((IterativeSentence) sentence, previousNodeId);
            case INCLUDE:
                return processIncludeSentence((IncludeSentence) sentence, previousNodeId);
            case EXTEND:
                return processExtendSentence((ExtendSentence) sentence, previousNodeId);
            case ABORT:
                return processAbortSentence((AbortSentence) sentence, previousNodeId);
            case RESUME_STEP:
                return processResumeStepSentence((ResumeStepSentence) sentence, previousNodeId);
            default:
                throw new IllegalArgumentException("Unknown sentence type: " + sentence.getType());
        }
    }
    
    /**
     * 规则1.1a：处理简单句
     */
    private String processSimpleSentence(SimpleSentence sentence, String previousNodeId) {
        String nodeId = "action_" + sentence.getId();
        
        // 创建CallOperationAction节点
        CallOperationAction action = manager.createCallOperationAction(nodeId, sentence.getContent());
        
        // 记录步骤映射
        stepToNodeMapping.put(sentence.getId(), nodeId);
        
        // 连接到前一个节点
        if (previousNodeId != null) {
            manager.createControlFlow("flow_" + nodeCounter++, previousNodeId, nodeId, null);
        }
        
        // 规则2：处理数据流
        processDataFlow(sentence, nodeId);
        
        return nodeId;
    }
    
    /**
     * 规则1.2.1c：处理条件检查句
     */
    private String processConditionCheckSentence(ConditionCheckSentence sentence, String previousNodeId) {
        // 创建CallOperationAction用于条件检查
        String checkNodeId = "check_" + sentence.getId();
        CallOperationAction checkAction = manager.createCallOperationAction(checkNodeId, sentence.getContent());
        
        // 连接到前一个节点
        if (previousNodeId != null) {
            manager.createControlFlow("flow_" + nodeCounter++, previousNodeId, checkNodeId, null);
        }
        
        // 创建DecisionNode
        String decisionNodeId = "decision_" + sentence.getId();
        DecisionNode decisionNode = manager.createDecisionNode(decisionNodeId);
        
        // 连接检查节点到决策节点
        manager.createControlFlow("flow_" + nodeCounter++, checkNodeId, decisionNodeId, null);
        
        // 处理备选流
        String mergeNodeId = "merge_" + sentence.getId();
        MergeNode mergeNode = manager.createMergeNode(mergeNodeId);
        
        if (generateDetailedDiagram) {
            // 详细图：递归处理备选流中的句子
            String lastAlternativeNodeId = decisionNodeId;
            for (Sentence altSentence : sentence.getAlternativeFlow()) {
                lastAlternativeNodeId = processSentence(altSentence, lastAlternativeNodeId);
            }
            // 连接备选流到合并节点
            manager.createControlFlow("flow_" + nodeCounter++, lastAlternativeNodeId, mergeNodeId, "alternative");
        } else {
            // 概览图：使用CallBehaviorAction
            String altActionId = "alt_behavior_" + sentence.getId();
            CallBehaviorAction altAction = manager.createCallBehaviorAction(altActionId, "Alternative Flow");
            manager.createControlFlow("flow_" + nodeCounter++, decisionNodeId, altActionId, "alternative");
            manager.createControlFlow("flow_" + nodeCounter++, altActionId, mergeNodeId, null);
        }
        
        // 主流程继续
        manager.createControlFlow("flow_" + nodeCounter++, decisionNodeId, mergeNodeId, "main");
        
        // 规则2.3a：为条件检查句添加InputPin
        manager.addInputPin(checkNodeId, "condition_input", "Boolean");
        
        return mergeNodeId;
    }
    
    /**
     * 规则1.2.2c：处理条件句
     */
    private String processConditionalSentence(ConditionalSentence sentence, String previousNodeId) {
        // 创建DecisionNode
        String decisionNodeId = "conditional_" + sentence.getId();
        DecisionNode decisionNode = manager.createDecisionNode(decisionNodeId);
        
        // 连接到前一个节点
        if (previousNodeId != null) {
            manager.createControlFlow("flow_" + nodeCounter++, previousNodeId, decisionNodeId, null);
        }
        
        // 创建MergeNode用于汇合
        String mergeNodeId = "merge_conditional_" + sentence.getId();
        MergeNode mergeNode = manager.createMergeNode(mergeNodeId);
        
        // 处理THEN分支
        String lastThenNodeId = decisionNodeId;
        if (generateDetailedDiagram) {
            for (Sentence thenSentence : sentence.getThenBranch()) {
                lastThenNodeId = processSentence(thenSentence, lastThenNodeId);
            }
        } else {
            String thenActionId = "then_behavior_" + sentence.getId();
            CallBehaviorAction thenAction = manager.createCallBehaviorAction(thenActionId, "Then Branch");
            manager.createControlFlow("flow_" + nodeCounter++, decisionNodeId, thenActionId, "then");
            lastThenNodeId = thenActionId;
        }
        manager.createControlFlow("flow_" + nodeCounter++, lastThenNodeId, mergeNodeId, null);
        
        // 处理ELSE分支
        if (!sentence.getElseBranch().isEmpty()) {
            String lastElseNodeId = decisionNodeId;
            if (generateDetailedDiagram) {
                for (Sentence elseSentence : sentence.getElseBranch()) {
                    lastElseNodeId = processSentence(elseSentence, lastElseNodeId);
                }
            } else {
                String elseActionId = "else_behavior_" + sentence.getId();
                CallBehaviorAction elseAction = manager.createCallBehaviorAction(elseActionId, "Else Branch");
                manager.createControlFlow("flow_" + nodeCounter++, decisionNodeId, elseActionId, "else");
                lastElseNodeId = elseActionId;
            }
            manager.createControlFlow("flow_" + nodeCounter++, lastElseNodeId, mergeNodeId, null);
        }
        
        // 处理ELSEIF分支
        for (ConditionalSentence.ConditionalBranch elseIfBranch : sentence.getElseIfBranches()) {
            String lastElseIfNodeId = decisionNodeId;
            if (generateDetailedDiagram) {
                for (Sentence elseIfSentence : elseIfBranch.getSentences()) {
                    lastElseIfNodeId = processSentence(elseIfSentence, lastElseIfNodeId);
                }
            } else {
                String elseIfActionId = "elseif_behavior_" + sentence.getId() + "_" + nodeCounter;
                CallBehaviorAction elseIfAction = manager.createCallBehaviorAction(elseIfActionId, "ElseIf Branch");
                manager.createControlFlow("flow_" + nodeCounter++, decisionNodeId, elseIfActionId, "elseif");
                lastElseIfNodeId = elseIfActionId;
            }
            manager.createControlFlow("flow_" + nodeCounter++, lastElseIfNodeId, mergeNodeId, null);
        }
        
        return mergeNodeId;
    }
    
    /**
     * 规则1.2.3c：处理并行句
     */
    private String processParallelSentence(ParallelSentence sentence, String previousNodeId) {
        // 创建ForkNode
        String forkNodeId = "fork_" + sentence.getId();
        ForkNode forkNode = manager.createForkNode(forkNodeId);
        
        // 连接到前一个节点
        if (previousNodeId != null) {
            manager.createControlFlow("flow_" + nodeCounter++, previousNodeId, forkNodeId, null);
        }
        
        // 创建JoinNode
        String joinNodeId = "join_" + sentence.getId();
        JoinNode joinNode = manager.createJoinNode(joinNodeId);
        
        // 处理每个并行分支
        for (int i = 0; i < sentence.getParallelBranches().size(); i++) {
            List<Sentence> branch = sentence.getParallelBranches().get(i);
            String lastBranchNodeId = forkNodeId;
            
            for (Sentence branchSentence : branch) {
                lastBranchNodeId = processSentence(branchSentence, lastBranchNodeId);
            }
            
            // 连接分支的最后一个节点到JoinNode
            manager.createControlFlow("flow_" + nodeCounter++, lastBranchNodeId, joinNodeId, null);
        }
        
        return joinNodeId;
    }
    
    /**
     * 规则1.2.4c：处理迭代句
     */
    private String processIterativeSentence(IterativeSentence sentence, String previousNodeId) {
        // 创建DecisionNode用于循环条件判断
        String decisionNodeId = "loop_decision_" + sentence.getId();
        DecisionNode decisionNode = manager.createDecisionNode(decisionNodeId);
        
        // 连接到前一个节点
        if (previousNodeId != null) {
            manager.createControlFlow("flow_" + nodeCounter++, previousNodeId, decisionNodeId, null);
        }
        
        // 处理循环体
        String lastBodyNodeId = decisionNodeId;
        for (Sentence bodySentence : sentence.getBody()) {
            lastBodyNodeId = processSentence(bodySentence, lastBodyNodeId);
        }
        
        // 创建循环回边
        manager.createControlFlow("flow_" + nodeCounter++, lastBodyNodeId, decisionNodeId, "continue");
        
        return decisionNodeId; // 循环结束时从决策节点继续
    }
    
    /**
     * 处理数据流（规则2）
     */
    private void processDataFlow(SimpleSentence sentence, String nodeId) {
        switch (sentence.getTransactionType()) {
            case INITIATION:
            case RESPONSE_TO_PRIMARY_ACTOR:
            case RESPONSE_TO_SECONDARY_ACTOR:
                // 规则2.1a：添加OutputPin
                manager.addOutputPin(nodeId, "output", sentence.getObject());
                break;
            case INTERNAL_TRANSACTION:
                // 规则2.2a：添加InputPin和OutputPin
                manager.addInputPin(nodeId, "input", sentence.getObject());
                manager.addOutputPin(nodeId, "output", sentence.getObject());
                break;
        }
    }
    
    /**
     * 规则1.3.1a：处理包含句
     */
    private String processIncludeSentence(IncludeSentence sentence, String previousNodeId) {
        String nodeId = "include_" + sentence.getId();
        CallBehaviorAction action = manager.createCallBehaviorAction(nodeId, "Include: " + sentence.getIncludedUseCase());
        
        if (previousNodeId != null) {
            manager.createControlFlow("flow_" + nodeCounter++, previousNodeId, nodeId, null);
        }
        
        return nodeId;
    }
    
    /**
     * 规则1.3.2a：处理扩展句
     */
    private String processExtendSentence(ExtendSentence sentence, String previousNodeId) {
        String nodeId = "extend_" + sentence.getId();
        CallBehaviorAction action = manager.createCallBehaviorAction(nodeId, "Extended by: " + sentence.getExtendingUseCase());
        
        if (previousNodeId != null) {
            manager.createControlFlow("flow_" + nodeCounter++, previousNodeId, nodeId, null);
        }
        
        return nodeId;
    }
    
    /**
     * 规则1.3.3a：处理中止句
     */
    private String processAbortSentence(AbortSentence sentence, String previousNodeId) {
        String nodeId = "abort_" + sentence.getId();
        FlowFinalNode finalNode = manager.createFlowFinalNode(nodeId);
        
        if (previousNodeId != null) {
            manager.createControlFlow("flow_" + nodeCounter++, previousNodeId, nodeId, null);
        }
        
        return nodeId;
    }
    
    /**
     * 规则1.3.4a：处理恢复步骤句
     */
    private String processResumeStepSentence(ResumeStepSentence sentence, String previousNodeId) {
        String targetNodeId = stepToNodeMapping.get(sentence.getTargetStepId());
        if (targetNodeId != null && previousNodeId != null) {
            manager.createControlFlow("flow_resume_" + nodeCounter++, previousNodeId, targetNodeId, "resume");
        }
        return targetNodeId;
    }
    
    /**
     * 规则1.4c：处理全局备选流
     */
    private void processGlobalAlternativeFlow(GlobalAlternativeFlow globalFlow) {
        // 创建AcceptEventAction
        String eventNodeId = "global_event_" + nodeCounter++;
        AcceptEventAction eventAction = manager.createAcceptEventAction(eventNodeId, "Trigger: " + globalFlow.getTriggerEvent());
        
        // 处理全局备选流中的句子
        String lastNodeId = eventNodeId;
        for (Sentence sentence : globalFlow.getSentences()) {
            lastNodeId = processSentence(sentence, lastNodeId);
        }
        
        // 注意：InterruptibleActivityRegion的创建需要更复杂的逻辑，这里简化处理
    }
}
