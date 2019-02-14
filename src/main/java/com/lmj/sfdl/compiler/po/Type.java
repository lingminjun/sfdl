package com.lmj.sfdl.compiler.po;

/**
 * Created with IntelliJ IDEA.
 * Description: 类型描述
 * User: lingminjun
 * Date: 2018-12-10
 * Time: 下午10:29
 */
public enum Type {
    BOOL(1,false,"TINYINT"),
    INT(4,false,"INT"),
    LONG(8,false,"BIGINT"),
    FLOAT(4,false,"FLOAT"),
    DOUBLE(8,false,"DOUBLE"),
    VARCHAR(1,true,"VARCHAR"),
    STRING(1,false,"LONGTEXT"),
    DATETIME(8,false,"DATETIME"),
    TIMESTAMP(4,false,"TIMESTAMP");

    public final int bit;//单位字节数
    public final boolean variable;//可变的
    public final String sqlType;//主要以MySQL为标准，

    Type(int bit,boolean variable, String sqlType) {
        this.bit = bit;
        this.variable = variable;
        this.sqlType = sqlType;
    }
}
