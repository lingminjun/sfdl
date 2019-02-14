package com.lmj.sfdl.compiler.po;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: lingminjun
 * Date: 2019-01-06
 * Time: 2:58 PM
 */
public final class Name implements Serializable {
    private String struct;
    private String field;

    public Name() {}
    public Name(String struct,String field) {
        this.struct = struct;
        this.field = field;
    }

    public String getStruct() {
        return struct;
    }

    public void setStruct(String struct) {
        this.struct = struct;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}
