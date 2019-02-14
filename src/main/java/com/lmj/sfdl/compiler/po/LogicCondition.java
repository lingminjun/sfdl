package com.lmj.sfdl.compiler.po;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: lingminjun
 * Date: 2019-02-12
 * Time: 9:25 PM
 */
public class LogicCondition extends Condition {
    //原始条件
    private String condition;

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
