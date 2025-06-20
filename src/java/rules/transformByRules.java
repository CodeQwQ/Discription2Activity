// 这个文件主要编写，各个规则到活动图xmi文件的转化输出
package java.rules;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class transformByRules {
    /*
    * 定义：为每个用例生成一个活动图实例
    * 参数：1. UCMeta的的用例名称进行读取。
    *      2. 输出文件（目前先用print代替）
    * 返回：无
    * */
    public  void rule1c(String caseName, File file){
        try {
            // 如果文件不存在，则创建
            if (file.createNewFile()) {
                System.out.println("文件已创建: " + file.getName());
            } else {
                System.out.println("文件已存在.");
            }

            // 开始写入文件
            FileWriter writer = new FileWriter(file);

            // 要写入的XML内容
            String xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<uml:Model xmi:version=\"20131001\" xmlns:xmi=\"http://www.omg.org/spec/XMI/20131001\" xmlns:uml=\"http://www.eclipse.org/uml2/5.0.0/UML\" xmi:id=\"model\" name=\"activityDaigramXmitest\">\n" +
                    "  <packageImport xmi:type=\"uml:PackageImport\" xmi:id=\"package\">\n" +
                    "    <importedPackage xmi:type=\"uml:Model\" href=\"pathmap://UML_LIBRARIES/UMLPrimitiveTypes.library.uml#_0\"/>\n" +
                    "  </packageImport>\n" +
                    "</uml:Model>";

            writer.write(xmlContent);
            writer.close();
        } catch (IOException e) {
            System.out.println("发生IO异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        transformByRules t = new transformByRules();
        t.rule1c("case1", new File("case1.xmi"));
    }
}