package com.lmj.sfdl.compiler.po;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: lingminjun
 * Date: 2019-02-12
 * Time: 9:29 PM
 */
public class FuncCondition extends Condition {
    private Func func;

    public Func getFunc() {
        return func;
    }

    public void setFunc(Func func) {
        this.func = func;
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
        builder.append(" MATCH FUNC ");
        builder.append(this.func.getKeyName());
        return builder.toString();
    }
}
