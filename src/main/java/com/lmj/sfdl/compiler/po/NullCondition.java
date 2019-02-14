package com.lmj.sfdl.compiler.po;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: lingminjun
 * Date: 2019-02-12
 * Time: 9:34 PM
 */
public class NullCondition extends Condition {
    private boolean notNvll; // null

    public boolean isNotNvll() {
        return notNvll;
    }

    public void setNotNvll(boolean notNvll) {
        this.notNvll = notNvll;
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
        if (this.notNvll) {
            builder.append(" IS NOT NULL");
        } else {
            builder.append(" IS NULL");
        }
        return builder.toString();
    }
}
