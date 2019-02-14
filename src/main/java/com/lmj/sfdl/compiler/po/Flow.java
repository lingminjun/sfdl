package com.lmj.sfdl.compiler.po;

import com.lmj.sfdl.compiler.parser.Syntax;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description: 流描述（方法实现描述）
 * User: lingminjun
 * Date: 2018-12-12
 * Time: 下午1:44
 */
public final class Flow {

    private View result;//返回视图结构（只支持视图返回，结构嵌套如何解决）
    private Func func;//命名方法

    private List<Field> consts = new ArrayList<Field>();//常量变量
    private List<Field> params = new ArrayList<Field>();//参数，in 字段
    private List<Field> vars = new ArrayList<Field>();//参数，out 字段

    private List<Table> inStructs = new ArrayList<Table>();//关联写入结构，只能是Table
    private List<Table> outStructs = new ArrayList<Table>();//关联读取结构,table or view

    private List<Condition> conditions = new ArrayList<Condition>();

    private Code whereCode;


    public View getResult() {
        return result;
    }

    public void setResult(View result) {
        this.result = result;
    }

    public Func getFunc() {
        return func;
    }

    public void setFunc(Func func) {
        this.func = func;
    }

    public String getKeyName() {
        return getFunc().getKeyName();
    }

    public List<Field> getConsts() {
        return consts;
    }

    public boolean containConst(String name) {
        for (Field fld : this.consts) {
            if (fld.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public void addConst(Field cst) {
        if (!containConst(cst.getName())) {
            this.consts.add(cst);
        }
    }

    public Field getParam(String name) {
        return getParam(name,false);
    }
    public Field getParam(String name, boolean create) {
        for (Field fld : this.params) {
            if (fld.getName().equals(name)) {
                return fld;
            }
        }
        if (create) {
            Field fld = new Field();
            fld.setName(name);
            this.params.add(fld);
            return fld;
        }
        return null;
    }

    public boolean containParam(String name) {
        for (Field fld : this.params) {
            if (fld.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public void addParam(Field param) {
        if (!containParam(param.getName())) {
            this.params.add(param);
        }
    }

    public List<Field> getVars() {
        return vars;
    }

    public boolean containVar(String name) {
        for (Field fld : this.vars) {
            if (fld.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public void addVar(Field var) {
        if (!containVar(var.getName())) {
            this.vars.add(var);
        }
    }

    public List<Table> getInStructs() {
        return inStructs;
    }

    public boolean containInStruct(String keyName) {
        for (Struct stt : this.inStructs) {
            if (stt.getKeyName().equals(keyName)) {
                return true;
            }
        }
        return false;
    }

    public void addInStruct(Table inStruct) {
        if (!containInStruct(inStruct.getKeyName())) {
            this.inStructs.add(inStruct);
        }
    }

    public List<Table> getOutStructs() {
        return outStructs;
    }

    public boolean containOutStruct(String keyName) {
        for (Struct stt : this.outStructs) {
            if (stt.getKeyName().equals(keyName)) {
                return true;
            }
        }
        return false;
    }

    public void addOutStruct(Table outStruct) {
        if (!containOutStruct(outStruct.getKeyName())) {
            this.outStructs.add(outStruct);
        }
    }

    public Code getWhereCode() {
        return whereCode;
    }

    public void setWhereCode(Code whereCode) {
        this.whereCode = whereCode;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void addCondition(Condition condition) {
        if (condition != null) {
            this.conditions.add(condition);
        }
    }

    public void addConditions(List<Condition> conditions) {
        if (conditions != null) {
            this.conditions.addAll(conditions);
        }
    }

    public boolean containStruct(String keyName) {
        for (Struct stt : this.outStructs) {
            if (stt.getKeyName().equals(keyName)) {
                return true;
            }
        }
        for (Struct stt : this.inStructs) {
            if (stt.getKeyName().equals(keyName)) {
                return true;
            }
        }
        return false;
    }

    public final List<Field> getSortedParams() {
        List<Field> list = new ArrayList<Field>(params);
        Collections.sort(list, new Comparator<Field>() {
            public int compare(Field o1, Field o2) {
                return o1.getSorted().compareTo(o2.getSorted());
            }
        });
        return list;
    }

    // ((a > 0 || b > 0) && (a < 10 || b < 10) && z > 0)
    // (a > 0 && b < 0)
    // a > 0 || (b < 0 && z > 0)
    // 如何分解拆分
    public final List<Condition> getSortedCondition() {
        ComplexCondition parent = new ComplexCondition();
        List<Condition> list = parent.getSubConditions();
        for (Condition condition : conditions) {
            int rely = Syntax.relyNest(condition.getCondition());
            if (rely == 0) {
                condition.setParentCondition(parent);
                list.add(condition);
            } else if (rely > 0){
                //进层级
                for (int i = 0; i < rely; i++) {
                    ComplexCondition combination = new ComplexCondition();
                    condition.setParentCondition(parent);
                    list.add(combination);
                    parent = combination;
                    list = combination.getSubConditions();
                }
                condition.setParentCondition(parent);
                list.add(condition);
            } else if (rely < 0) {
                condition.setParentCondition(parent);
                list.add(condition);
                //解层级
                for (int i = 0; i < -rely; i++) {
                    parent = parent.getParentCondition();
                    list = parent.getSubConditions();
                }
            }
        }
        return list;
    }




    public String toDescription() {

        StringBuilder builder = new StringBuilder();
        builder.append("func ");
        builder.append(getKeyName());

        //参数
        if (params.isEmpty()){
            builder.append("()");
        } else {
            builder.append("(");

            for (Field pm : getSortedParams()) {
                builder.append(pm.toDescription());
                builder.append(",");
            }

            builder.append(")");
        }

        //返回值
        builder.append(" -> ");
        builder.append(result.getKeyName());
        builder.append(" {\n");

        List<Condition> conditions = getSortedCondition();
        builder.append(" WHERE ");
        for (Condition cdn : conditions) {
            builder.append(cdn.toDescription());
        }
        builder.append("\n");

//        List<Field> list = getFields();
//        Collections.sort(list, new Comparator<Field>() {
//            public int compare(Field o1, Field o2) {
//                return o1.getSorted().compareTo(o2.getSorted());
//            }
//        });
//
//        for (Field field : list) {
//            builder.append("    ");
//            builder.append(field.toDescription());
//            builder.append(",\n");
//        }

        builder.append("}\n");

        return builder.toString();
    }
}
