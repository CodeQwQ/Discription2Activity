package project.rules;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.uml2.uml.*;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;

import java.io.File;
import java.util.*;

/**
 * 活动图管理器 - 用于管理活动图的创建、修改和元素定位
 */
public class ActivityDiagramManager {
    private Model model;
    private Activity activity;
    private UMLFactory factory;
    private Map<String, ActivityNode> nodeRegistry; // 节点注册表，用于快速定位
    private Map<String, ActivityEdge> edgeRegistry; // 边注册表
    private ResourceSet resourceSet;
    
    public ActivityDiagramManager(String modelName, String activityName) {
        this.factory = UMLFactory.eINSTANCE;
        this.nodeRegistry = new HashMap<>();
        this.edgeRegistry = new HashMap<>();
        
        // 初始化资源集
        this.resourceSet = new ResourceSetImpl();
        UMLResourcesUtil.init(resourceSet);
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap()
                .put("uml", new XMIResourceFactoryImpl());
        
        // 创建模型和活动
        this.model = factory.createModel();
        this.model.setName(modelName);
        
        this.activity = factory.createActivity();
        this.activity.setName(activityName);
        this.model.getPackagedElements().add(activity);
    }
    
    /**
     * 创建一个带有自定义ID的OpaqueAction节点
     */
    public OpaqueAction createOpaqueAction(String customId, String name) {
        OpaqueAction action = factory.createOpaqueAction();
        action.setName(name);
        
        // 将节点添加到活动中
        activity.getOwnedNodes().add(action);
        
        // 注册到节点表中，使用自定义ID
        nodeRegistry.put(customId, action);
        
        return action;
    }
    
    /**
     * 创建初始节点
     */
    public InitialNode createInitialNode(String customId) {
        InitialNode initialNode = factory.createInitialNode();
        activity.getOwnedNodes().add(initialNode);
        nodeRegistry.put(customId, initialNode);
        return initialNode;
    }
    
    /**
     * 创建最终节点
     */
    public ActivityFinalNode createFinalNode(String customId) {
        ActivityFinalNode finalNode = factory.createActivityFinalNode();
        activity.getOwnedNodes().add(finalNode);
        nodeRegistry.put(customId, finalNode);
        return finalNode;
    }
    
    /**
     * 创建决策节点
     */
    public DecisionNode createDecisionNode(String customId) {
        DecisionNode decisionNode = factory.createDecisionNode();
        activity.getOwnedNodes().add(decisionNode);
        nodeRegistry.put(customId, decisionNode);
        return decisionNode;
    }
    
    /**
     * 创建合并节点
     */
    public MergeNode createMergeNode(String customId) {
        MergeNode mergeNode = factory.createMergeNode();
        activity.getOwnedNodes().add(mergeNode);
        nodeRegistry.put(customId, mergeNode);
        return mergeNode;
    }

    /**
     * 创建分叉节点（用于并行）
     */
    public ForkNode createForkNode(String customId) {
        ForkNode forkNode = factory.createForkNode();
        activity.getOwnedNodes().add(forkNode);
        nodeRegistry.put(customId, forkNode);
        return forkNode;
    }

    /**
     * 创建汇合节点（用于并行）
     */
    public JoinNode createJoinNode(String customId) {
        JoinNode joinNode = factory.createJoinNode();
        activity.getOwnedNodes().add(joinNode);
        nodeRegistry.put(customId, joinNode);
        return joinNode;
    }

    /**
     * 创建CallOperationAction节点
     */
    public CallOperationAction createCallOperationAction(String customId, String name) {
        CallOperationAction action = factory.createCallOperationAction();
        action.setName(name);
        activity.getOwnedNodes().add(action);
        nodeRegistry.put(customId, action);
        return action;
    }

    /**
     * 创建CallBehaviorAction节点
     */
    public CallBehaviorAction createCallBehaviorAction(String customId, String name) {
        CallBehaviorAction action = factory.createCallBehaviorAction();
        action.setName(name);
        activity.getOwnedNodes().add(action);
        nodeRegistry.put(customId, action);
        return action;
    }

    /**
     * 创建FlowFinalNode节点
     */
    public FlowFinalNode createFlowFinalNode(String customId) {
        FlowFinalNode finalNode = factory.createFlowFinalNode();
        activity.getOwnedNodes().add(finalNode);
        nodeRegistry.put(customId, finalNode);
        return finalNode;
    }

    /**
     * 创建AcceptEventAction节点
     */
    public AcceptEventAction createAcceptEventAction(String customId, String name) {
        AcceptEventAction action = factory.createAcceptEventAction();
        action.setName(name);
        activity.getOwnedNodes().add(action);
        nodeRegistry.put(customId, action);
        return action;
    }
    
    /**
     * 根据自定义ID获取节点
     */
    public ActivityNode getNodeById(String customId) {
        return nodeRegistry.get(customId);
    }

    /**
     * 为CallOperationAction添加InputPin
     */
    public InputPin addInputPin(String nodeId, String pinName, String typeName) {
        ActivityNode node = nodeRegistry.get(nodeId);
        if (node instanceof CallOperationAction) {
            CallOperationAction action = (CallOperationAction) node;
            InputPin inputPin = factory.createInputPin();
            inputPin.setName(pinName);
            if (typeName != null) {
                // 这里可以设置类型，暂时用名称表示
                inputPin.setType(null); // 需要具体的Type对象
            }
            action.getArguments().add(inputPin);
            return inputPin;
        }
        throw new IllegalArgumentException("Node is not a CallOperationAction: " + nodeId);
    }

    /**
     * 为CallOperationAction添加OutputPin
     */
    public OutputPin addOutputPin(String nodeId, String pinName, String typeName) {
        ActivityNode node = nodeRegistry.get(nodeId);
        if (node instanceof CallOperationAction) {
            CallOperationAction action = (CallOperationAction) node;
            OutputPin outputPin = factory.createOutputPin();
            outputPin.setName(pinName);
            if (typeName != null) {
                // 这里可以设置类型，暂时用名称表示
                outputPin.setType(null); // 需要具体的Type对象
            }
            action.getResults().add(outputPin);
            return outputPin;
        }
        throw new IllegalArgumentException("Node is not a CallOperationAction: " + nodeId);
    }

    /**
     * 创建约束
     */
    public Constraint createConstraint(String name, String specification) {
        Constraint constraint = factory.createConstraint();
        constraint.setName(name);

        // 创建约束规范
        LiteralString spec = factory.createLiteralString();
        spec.setValue(specification);
        constraint.setSpecification(spec);

        return constraint;
    }

    /**
     * 为活动添加前置条件
     */
    public void addPrecondition(String name, String specification) {
        Constraint precondition = createConstraint(name, specification);
        activity.getPreconditions().add(precondition);
    }

    /**
     * 为活动添加后置条件
     */
    public void addPostcondition(String name, String specification) {
        Constraint postcondition = createConstraint(name, specification);
        activity.getPostconditions().add(postcondition);
    }
    
    /**
     * 创建控制流连接两个节点
     */
    public ControlFlow createControlFlow(String customId, String sourceId, String targetId, String name) {
        ActivityNode source = nodeRegistry.get(sourceId);
        ActivityNode target = nodeRegistry.get(targetId);
        
        if (source == null || target == null) {
            throw new IllegalArgumentException("Source or target node not found");
        }
        
        ControlFlow flow = factory.createControlFlow();
        if (name != null) {
            flow.setName(name);
        }
        flow.setSource(source);
        flow.setTarget(target);
        
        activity.getEdges().add(flow);
        edgeRegistry.put(customId, flow);
        
        return flow;
    }
    
    /**
     * 在指定节点后插入新的节点和流程
     */
    public void insertNodeAfter(String afterNodeId, String newNodeId, String newNodeName, String flowId) {
        ActivityNode afterNode = nodeRegistry.get(afterNodeId);
        if (afterNode == null) {
            throw new IllegalArgumentException("After node not found: " + afterNodeId);
        }
        
        // 创建新节点
        OpaqueAction newNode = createOpaqueAction(newNodeId, newNodeName);
        
        // 找到原来从afterNode出发的所有边
        List<ActivityEdge> outgoingEdges = new ArrayList<>(afterNode.getOutgoings());
        
        // 将这些边的源改为新节点
        for (ActivityEdge edge : outgoingEdges) {
            edge.setSource(newNode);
        }
        
        // 创建从afterNode到newNode的新边
        createControlFlow(flowId, afterNodeId, newNodeId, null);
    }
    
    /**
     * 在指定节点前插入新的节点和流程
     */
    public void insertNodeBefore(String beforeNodeId, String newNodeId, String newNodeName, String flowId) {
        ActivityNode beforeNode = nodeRegistry.get(beforeNodeId);
        if (beforeNode == null) {
            throw new IllegalArgumentException("Before node not found: " + beforeNodeId);
        }
        
        // 创建新节点
        OpaqueAction newNode = createOpaqueAction(newNodeId, newNodeName);
        
        // 找到原来到beforeNode的所有边
        List<ActivityEdge> incomingEdges = new ArrayList<>(beforeNode.getIncomings());
        
        // 将这些边的目标改为新节点
        for (ActivityEdge edge : incomingEdges) {
            edge.setTarget(newNode);
        }
        
        // 创建从newNode到beforeNode的新边
        createControlFlow(flowId, newNodeId, beforeNodeId, null);
    }
    
    /**
     * 获取所有注册的节点ID
     */
    public Set<String> getAllNodeIds() {
        return nodeRegistry.keySet();
    }
    
    /**
     * 保存活动图到文件
     */
    public void saveToFile(String filename) throws Exception {
        File outputFile = new File(filename);
        URI outputURI = URI.createFileURI(outputFile.getAbsolutePath());
        Resource resource = resourceSet.createResource(outputURI);
        resource.getContents().add(model);
        // 设置 UTF-8 编码
        Map<Object, Object> options = new HashMap<>();
        options.put(org.eclipse.emf.ecore.xmi.XMLResource.OPTION_ENCODING, "UTF-8");
        resource.save(options);
        System.out.println("✅ 活动图已保存为: " + outputFile.getAbsolutePath());
    }
    
    /**
     * 打印当前所有节点的信息
     */
    public void printNodeInfo() {
        System.out.println("=== 活动图节点信息 ===");
        for (Map.Entry<String, ActivityNode> entry : nodeRegistry.entrySet()) {
            ActivityNode node = entry.getValue();
            System.out.println("ID: " + entry.getKey() + 
                             ", Type: " + node.getClass().getSimpleName() + 
                             ", Name: " + (node.getName() != null ? node.getName() : "unnamed"));
        }
    }
}
