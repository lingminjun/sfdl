package com.lmj.sfdl.compiler.po;

import com.lmj.sfdl.compiler.consts.Consts;
import com.lmj.sfdl.utils.Injects;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * Description: 字段，变量，常量
 * User: lingminjun
 * Date: 2018-12-10
 * Time: 下午10:26
 */
public final class Field implements Serializable {

    private String name;   //属性名称
    private String comment; //注释

    // 类型方面的控制
    private boolean setted = false;
    private Type type = Type.VARCHAR; // 类型限定,默认verchar64
    private int length = Consts.Type.DEFAULT_VARCHAR_LENGTH;          //长度限制（仅仅提示，varchar时强制）

    // isNull限定
    private String defaultValue;//默认值

    private boolean notNull;    //是否可以为空
    private boolean notEmpty;   //是否可以为空(字符串专利)
    private Code nullCode;      //为空时异常描述

    // 唯一限定
    private boolean unique;     //唯一的
    private Code uniqueCode;    //唯一冲突时异常

    // 正则限定
    private String pattern;
    private Code patternCode;    //格式错误时异常
//    private static Pattern pattern= Pattern.compile("^(\\+86)?1[123456789]\\d{9}$");

    // 方法限定
    private Func func;
    private Code funcCode;       //函数校验不过时异常

    private boolean constant;    //是常量
    private Func refFunc;        //关联了某个方法

    private boolean primary;     //主键
    private boolean autoIncrement; // 自增

    // 排序一些规则
    private final static int NORMAL_FIELD = 5;
    private final static int PRIMARY_FIELD = 0;
    private final static int RECORD_FIELD = 8;
    private final static int DELETED_FIELD = 9;

    private int sorted = NORMAL_FIELD;

    @Override
    public int hashCode() {
        if (name != null) {
            return name.hashCode();
        }
        return "null".hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Field) {
            if (this.name != null) {
                return this.name.equals(((Field) obj).name);
            } else if (this.name == null && ((Field) obj).name == null){
                return true;
            }
        }
        return false;
    }

    public Field() {}
    public Field(String name) {
        this.name = name;
        this.special(name);
    }

    private void special(String name) {
        if (Consts.Field.ROW_ID.equals(name)) {
            this.setType(Type.LONG);
            this.setLength(20);
            this.setPrimary(true);
            this.sorted = PRIMARY_FIELD;
        } else if (Consts.Field.CREATE_AT.equals(name)) {
            this.setType(Type.LONG);
            this.setLength(20);
            if (this.comment == null) {
                this.comment = "创建时间";
            }
            this.sorted = RECORD_FIELD;
        } else if (Consts.Field.MODIFY_AT.equals(name)) {
            this.setType(Type.LONG);
            this.setLength(20);
            if (this.comment == null) {
                this.comment = "修改时间";
            }
            this.sorted = RECORD_FIELD;
        } else if (Consts.Field.DELETED.equals(name)) {
            this.setType(Type.BOOL);
            this.setLength(1);
            this.setDefaultValue("0");
            if (this.comment == null) {
                this.comment = "已被删除";
            }
            this.sorted = DELETED_FIELD;
        }
    }

    public Field copy() {
        Field field = new Field();
        Injects.fill(this,field);
        return field;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.special(name);
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isConstant() {
        return constant;
    }

    public void setConstant(boolean constant) {
        this.constant = constant;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.setted = true;
        this.type = type;
    }

    public void setSetted(boolean setted) {
        this.setted = setted;
    }

    public String getSorted() {
        return sorted + name;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
        if (primary) {
            if (!this.setted || this.type == Type.LONG) {
                this.setType(Type.LONG);//被限定
                this.autoIncrement = true;
            }
            this.setNotEmpty(true);
            if (this.comment == null) {
                this.comment = "主键";
            }
            this.sorted = PRIMARY_FIELD;
        } else {
            if (this.sorted < NORMAL_FIELD) {
                this.sorted = NORMAL_FIELD;
            }
        }
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public boolean isSetted() {
        return setted;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isNotNull() {
        return notNull || notEmpty;
    }

    public boolean isNotEmpty() {
        return notEmpty || (type != Type.VARCHAR && type != Type.STRING && notNull);
    }

    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }
    public void setNotEmpty(boolean notEmpty) {
        if (notEmpty) {
            this.notNull = notEmpty;
        }
        this.notEmpty = notEmpty;
    }

    public Code getNullCode() {
        return nullCode;
    }

    public void setNullCode(Code nullCode) {
        this.nullCode = nullCode;
    }

    public void setNullCode(int code, String message) {
        this.nullCode = new Code();
        this.nullCode.setCode(code);
        this.nullCode.setMessage(message);
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public Code getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(Code uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public void setUniqueCode(int code, String message) {
        this.uniqueCode = new Code();
        this.uniqueCode.setCode(code);
        this.uniqueCode.setMessage(message);
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Func getFunc() {
        return func;
    }

    public void setFunc(Func func) {
        this.func = func;
    }

    public Code getPatternCode() {
        return patternCode;
    }

    public void setPatternCode(Code patternCode) {
        this.patternCode = patternCode;
    }

    public void setPatternCode(int code, String message) {
        this.patternCode = new Code();
        this.patternCode.setCode(code);
        this.patternCode.setMessage(message);
    }

    public Code getFuncCode() {
        return funcCode;
    }

    public void setFuncCode(Code funcCode) {
        this.funcCode = funcCode;
    }

    public void setFuncCode(int code, String message) {
        this.funcCode = new Code();
        this.funcCode.setCode(code);
        this.funcCode.setMessage(message);
    }

    public Func getRefFunc() {
        return refFunc;
    }

    public void setRefFunc(Func refFunc) {
        this.refFunc = refFunc;
    }

    public String toDescription() {
        return "" +
                name + " " +
                type + "(" +
                length + ") " +
                (notEmpty ? "not empty" : notNull ? "not null" : "") + " " +
                (defaultValue != null ? "default " + defaultValue : "") + " " +
                (primary ? "primary" : unique ? "unique" : "") + " " +
                (comment != null ? "comment '" + comment + "'" : "") ;
    }
}
