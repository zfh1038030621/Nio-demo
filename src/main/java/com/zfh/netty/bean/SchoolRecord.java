package com.zfh.netty.bean;

import java.io.Serializable;

/**
 * @auth zhangfanghui
 * @since 2019-06-25
 */
public class SchoolRecord implements Serializable {
    private static  final  long serialVersionUID = 1L;

    private int id;
    private  int stId;
    private  int score;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStId() {
        return stId;
    }

    public void setStId(int stId) {
        this.stId = stId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
