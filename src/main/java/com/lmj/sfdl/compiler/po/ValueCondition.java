package com.lmj.sfdl.compiler.po;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: lingminjun
 * Date: 2019-02-12
 * Time: 9:36 PM
 */
public class ValueCondition extends Condition {
    //等式值或者方法
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toDescription() {
        StringBuilder builder = new StringBuilder();
        if (join == LogicJoin.OR) {
            builder.append(" OR ");
        } else {
            builder.append(" AND ");
        }
        builder.append(field.getName());
        builder.append(" = ");
        if (field != null && (field.getType() == Type.VARCHAR || field.getType() == Type.STRING)) {
            builder.append('\'');
            builder.append(value);
            builder.append('\'');
        } else {
            builder.append(value);
        }
        return builder.toString();
    }
}
