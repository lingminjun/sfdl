package com.lmj.sfdl.compiler.po;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description: 结构描述
 * User: lingminjun
 * Date: 2018-12-10
 * Time: 下午11:01
 */
public abstract class Struct implements Serializable {

    protected String group;//方法分组（服务组）
    protected String prefix;  //前缀
    protected String name;  //结构名（含前缀）
    protected List<Field> fields = new ArrayList<Field>();//字段列表

    @JSONField(serialize = false, deserialize = false)
    protected transient int autogenousIndex = 0;

    public int getAutogenousIndex() {
        return autogenousIndex++;
    }

    public String autogenousFuncName() {
        return getKeyName() + getAutogenousIndex();
    }

    public String displayName() {
        if (prefix != null && name != null && name.startsWith(prefix)) {
            return name.substring(prefix.length());
        }
        return name;
    }

    public String getKeyName() {
        if (group != null) {
            return group + "." + name;
        }
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

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

    public List<Field> getFields() {
        return fields;
    }


    public boolean containField(String name) {
        for (Field fld : this.fields) {
            if (fld.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public Field getField(String name) {
        return getField(name,false);
    }

    public Field getField(String name,boolean create) {
        for (Field fld : this.fields) {
            if (fld.getName().equals(name)) {
                return fld;
            }
        }

        if (create) {
            Field fld = new Field(name);
            this.fields.add(fld);
            return fld;
        }
        return null;
    }

    public void addField(Field field) {
        if (!containField(field.getName())) {
            this.fields.add(field);
        }
    }

    public abstract StructType getType();
    public abstract void autoFinish();


    public final List<Field> getSortedFields() {
        List<Field> list = getFields();
        Collections.sort(list, new Comparator<Field>() {
            public int compare(Field o1, Field o2) {
                return o1.getSorted().compareTo(o2.getSorted());
            }
        });
        return list;
    }

    public String toDescription() {

        StringBuilder builder = new StringBuilder();
        builder.append(getKeyName());
        builder.append(" (\n");

        for (Field field : getSortedFields()) {
            builder.append("    ");
            builder.append(field.toDescription());
            builder.append(",\n");
        }

        builder.append(");\n");

        return builder.toString();
    }
}
