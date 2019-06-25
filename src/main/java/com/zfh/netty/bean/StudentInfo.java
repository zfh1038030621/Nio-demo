package com.zfh.netty.bean;

import java.io.Serializable;

/**
 * @auth zhangfanghui
 * @since 2019-06-25
 */
public class StudentInfo implements Serializable {
    private static  final  long serialVersionUID = 1L;
    private  int id;
    private  String name;
    private  int age;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
