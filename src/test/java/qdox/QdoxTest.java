package qdox;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * qdox 测试
 *
 * @author llnn
 * @create 2020-07-28 15:02
 **/
public class QdoxTest {
    @Test
    public void getMeClass() throws IOException {
        JavaProjectBuilder builder = new JavaProjectBuilder();
        builder.addSourceTree(new File("/Users/llnn/project_java/smart-doc-demo/src/main/java"));

        //目录下的所有class
        System.out.println("====目录下的所有class====");
        Collection<JavaClass> classes = builder.getClasses();
        System.out.println(classes + "\n");

        //根据class全限定名获取
        JavaClass javaClass = builder.getClassByName("qdox.TestClass");

        //获取类的注释
        System.out.println("====获取类的注释====");
        System.out.println(javaClass.getComment());
        List<DocletTag> classTags = javaClass.getTags();
        classTags.forEach(item -> {
            System.out.println(item.getName() + "" + item.getValue());
        });
        System.out.println("\n");

        //获取继承的父类
        System.out.println("====获取继承的父类====");
        System.out.println(javaClass.getSuperClass() + "\n");

        //获取接口
        System.out.println("====获取接口====");
        System.out.println(javaClass.getImplements() + "\n");

        //获取方法
        System.out.println("====获取方法====");
        List<JavaMethod> methods = javaClass.getMethods();
        System.out.println(methods + "\n");

        //get Login方法
        JavaMethod javaMethod = methods.get(0);
        //方法返回类型
        System.out.println("====获取方法返回类型====");
        System.out.println(javaMethod.getReturns() + "\n");
        //获取参数
        System.out.println("====获取参数====");
        List<JavaParameter> parameters = javaMethod.getParameters();
        System.out.println(parameters);
        //获取参数类型
        //get userName参数
        JavaParameter parameter = parameters.get(0);
        //参数名称
        System.out.println(parameter.getName());
        //参数类型
        System.out.println(parameter.getType() + "\n");
        System.out.println("====获取方法注释====");
        //获取方法注释
        System.out.println(javaMethod.getComment());
        //获取参数备注
        List<DocletTag> tags = javaMethod.getTags();
        tags.forEach(item -> {
            System.out.println(item.getName() + ":" + item.getValue());
        });

    }
}
