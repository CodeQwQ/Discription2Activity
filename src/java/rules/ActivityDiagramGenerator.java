package java.rules;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.uml2.uml.*;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;

import java.io.File;
import java.util.Collections;

public class ActivityDiagramGenerator {
    public static void main(String[] args) throws Exception {
        // 初始化 UML 工厂
        UMLFactory factory = UMLFactory.eINSTANCE;

        // 创建 UML 模型
        Model model = factory.createModel();
        model.setName("TestModel");

        // 创建一个活动图
        Activity activity = factory.createActivity();
        activity.setName("TestActivity");
        model.getPackagedElements().add(activity);

        // 创建两个 OpaqueAction 节点
        OpaqueAction startAction = factory.createOpaqueAction();
        startAction.setName("Start Action");

        OpaqueAction endAction = factory.createOpaqueAction();
        endAction.setName("End Action");

        activity.getNodes().add(startAction);
        activity.getNodes().add(endAction);

        // 创建一条控制流连接两个节点
        ControlFlow flow = factory.createControlFlow();
        flow.setName("Flow");
        flow.setSource(startAction);
        flow.setTarget(endAction);
        activity.getEdges().add(flow);

        // 注册 .uml 扩展名的工厂
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap()
                .put("uml", new XMIResourceFactoryImpl());

        // 创建 ResourceSet
        ResourceSet resourceSet = new ResourceSetImpl();
        UMLResourcesUtil.init(resourceSet);  // 注册 UML 所需元模型资源

        // 创建 UML 文件并保存
        File outputFile = new File("test.uml");
        URI outputURI = URI.createFileURI(outputFile.getAbsolutePath());
        Resource resource = resourceSet.createResource(outputURI);
        resource.getContents().add(model);
        resource.save(Collections.emptyMap());

        System.out.println("✅ 活动图已保存为: " + outputFile.getAbsolutePath());
    }
}

