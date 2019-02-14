package com.lmj.sfdl.compiler.po;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: lingminjun
 * Date: 2019-02-12
 * Time: 9:31 PM
 */
public class PatternCondition extends Condition {
    private String pattern;

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
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
        builder.append(" MATCH PATTERN ");
        builder.append(this.pattern);
        return builder.toString();
    }
}
