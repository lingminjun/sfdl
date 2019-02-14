package com.lmj.sfdl.compiler.po;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description: 方法
 * User: lingminjun
 * Date: 2018-12-18
 * Time: 5:32 PM
 */
public final class Func implements Serializable {
    private String name; //方法名
    private String group;//方法分组（服务组）
    private List<Field> params = new ArrayList<Field>(); //参数，暂不支持结构参数
    private View returnStruct;//返回结构 (只能返回View)
    private Field returnValue;//返回数据
    private boolean check;      //boolean返回值（属于校验方法）

    public String getName() {
        return name;
    }

    public String getGroup() {
        return group;
    }

    public void setName(String name) {
        String[] ss = name.split("\\.");
        if (ss.length > 1) {
            this.group = ss[0];
            this.name = name.substring(this.group.length() + 1);
        } else {
            this.name = name;
        }
    }

    public String getKeyName() {
        if (group != null) {
            return group + "." + name;
        }
        return name;
    }

    public List<Field> getParams() {
        return params;
    }

    public void setParams(List<Field> params) {
        this.params = params;
    }

    public boolean containParam(String name) {
        for (Field fld : this.params) {
            if (fld.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public void addParam(Field param) {
        if (!containParam(param.getName())) {
            this.params.add(param);
        }
    }

    public Field getParam(String name) {
        return getParam(name,false);
    }

    public Field getParam(String name,boolean create) {
        for (Field fld : this.params) {
            if (fld.getName().equals(name)) {
                return fld;
            }
        }

        if (create) {
            Field fld = new Field(name);
            this.params.add(fld);
            return fld;
        }
        return null;
    }

    public Struct getReturnStruct() {
        return returnStruct;
    }

    public void setReturnStruct(View returnStruct) {
        this.returnStruct = returnStruct;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public Field getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(Field returnValue) {
        this.returnValue = returnValue;
    }
}
