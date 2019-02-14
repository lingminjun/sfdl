package com.lmj.sfdl.compiler.po;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: lingminjun
 * Date: 2019-01-05
 * Time: 7:40 PM
 */
public class Condition implements Serializable {
    protected LogicJoin join;

    //作用属性
    protected Field field;
    protected List<Field> fields = new ArrayList<Field>();

//    protected Set<String> views = new HashSet<String>();
    protected Set<String> tables = new HashSet<String>();

    //原始条件
    protected String condition;

    //不满足时异常
    protected Code noCode;

    protected ComplexCondition parentCondition;

    public LogicJoin getJoin() {
        return join;
    }

    public void setJoin(LogicJoin join) {
        this.join = join;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Code getNoCode() {
        return noCode;
    }

    public void setNoCode(Code noCode) {
        this.noCode = noCode;
    }

    public List<Field> getFields() {
        List<Field> list = new ArrayList<Field>();
        list.add(field);
        list.addAll(fields);
        return list;
    }

    public void addField(Field field) {
        if (field == null) {
            return;
        }
        if (this.field == null) {
            this.field = field;
            return;
        }
        this.fields.add(field);
    }

    public void addFields(List<Field> fields) {
        if (fields == null || fields.size() == 0) {
            return;
        }
        int idx = 0;
        if (this.field == null) {
            this.field = fields.get(0);
            idx = 1;
        }
        for (int i = idx; i < fields.size(); i++ ) {
            Field fld = fields.get(i);
            this.fields.add(fld);
        }
    }

    public ComplexCondition getParentCondition() {
        return parentCondition;
    }

    public void setParentCondition(ComplexCondition parentCondition) {
        this.parentCondition = parentCondition;
    }


//    public Set<String> getViews() {
//        return views;
//    }
//
//    public void addView(String view) {
//        if (!views.contains(view)) {
//            views.add(view);
//        }
//    }

    public Set<String> getTables() {
        return tables;
    }

    public void addTable(String table) {
        if (!tables.contains(table)) {
            tables.add(table);
        }
    }

    public String toDescription() {
        StringBuilder builder = new StringBuilder();
        if (join == LogicJoin.OR) {
            builder.append(" OR ");
        } else {
            builder.append(" AND ");
        }
        builder.append(this.condition);
        return builder.toString();
    }
}
