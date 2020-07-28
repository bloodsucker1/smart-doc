package qdox;

import java.util.Random;

/**
 * TestClass
 *
 * @author llnn
 * @create 2020-07-28 15:02
 **/

interface Test {

}

/**
 * QDox 测试
 *
 * @author: jujun chen
 * @date: 2019/07/07
 */
public class TestClass extends BaseTestClass implements Test {

    /**
     * 登录接口
     *
     * @param userName 用户名
     * @param password 密码
     * @return Person对象
     */
    public Person Login(String userName, String password) {
        int age = new Random().nextInt();
        Person person = new Person(userName, password, age);
        System.out.println(person);
        return person;
    }
}

class Person {
    private String userName;
    private String password;
    private int age;

    public Person(String userName, String password, int age) {
        this.userName = userName;
        this.password = password;
        this.age = age;
    }
}

class BaseTestClass {

}