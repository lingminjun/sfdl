package com.lmj.sfdl.compiler.po;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * Description: 异常描述
 * User: lingminjun
 * Date: 2018-12-10
 * Time: 下午11:07
 */
public final class Code implements Serializable {
    private int code = -100;      //异常码
    private String message;//异常消息

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Code() {}
    public Code(String message) {
        this.message = message;
    }
    public Code(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
