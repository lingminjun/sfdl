package com.lmj.sfdl.compiler.po;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: lingminjun
 * Date: 2019-02-12
 * Time: 9:28 PM
 */
public class ComplexCondition extends Condition {
    protected List<Condition> subConditions = new ArrayList<Condition>();

    public List<Condition> getSubConditions() {
        return subConditions;
    }

    public void addSubConditions(Condition condition) {
        if (condition != null) {
            this.subConditions.add(condition);
        }
    }

    public String toDescription() {
        if (subConditions.size() > 0) {
            StringBuilder builder = new StringBuilder();
            if (join == LogicJoin.OR) {
                builder.append(" OR ");
            } else {
                builder.append(" AND ");
            }
            builder.append("( ");
            for (Condition cdn : subConditions) {
                builder.append(cdn.toDescription());
            }
            builder.append(" )");
            return builder.toString();
        } else {
            return super.toDescription();
        }
    }
}
