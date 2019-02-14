package com.lmj.sfdl.compiler.po;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: lingminjun
 * Date: 2019-02-12
 * Time: 9:33 PM
 */
public class EmptyCondition extends Condition {

    private boolean notEmpty; // null

    public boolean isNotEmpty() {
        return notEmpty;
    }

    public void setNotEmpty(boolean notEmpty) {
        this.notEmpty = notEmpty;
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
        if (this.notEmpty) {
            builder.append(" IS NOT EMPTY");
        } else {
            builder.append(" IS EMPTY");
        }
        return builder.toString();
    }
}
