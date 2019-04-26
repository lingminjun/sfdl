package com.lmj.sfdl.compiler.po;

import com.lmj.sfdl.compiler.parser.Syntax;

import java.util.*;

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


    // table => type => condition
    private static final String PURE_TABLE_KEY = "null";
    private static Map<String,Map<String,List<Condition>>> groupConditionMap(List<Condition> conditions) {
        Map<String,Map<String,List<Condition>>> tables = new HashMap<String, Map<String, List<Condition>>>();
        for (Condition cdn : conditions) {
            String type = cdn.getClass().getName();
            if (cdn.tables.isEmpty()) {
                Map<String,List<Condition>> maps = tables.get(PURE_TABLE_KEY);
                if (maps == null) {
                    maps = new HashMap<String, List<Condition>>();
                    tables.put(PURE_TABLE_KEY, maps);
                }
                List<Condition> list = maps.get(type);
                if (list == null) {
                    list = new ArrayList<Condition>();
                    maps.put(type,list);
                }
                list.add(cdn);
            } else {
                Iterator<String> iterator = cdn.tables.iterator();
                while (iterator.hasNext()) {
                    String table = iterator.next();
                    Map<String,List<Condition>> maps = tables.get(table);
                    if (maps == null) {
                        maps = new HashMap<String, List<Condition>>();
                        tables.put(table, maps);
                    }
                    List<Condition> list = maps.get(type);
                    if (list == null) {
                        list = new ArrayList<Condition>();
                        maps.put(type,list);
                    }
                    list.add(cdn);
                }
            }
        }
        return tables;
    }

    public Table hitInTable(String table) {
        for (Table tb : inStructs) {
            if (tb.getKeyName().equals(table)) {
                return tb;
            }
        }
        return null;
    }

    public Table hitOutTable(String table) {
        for (Table tb : outStructs) {
            if (tb.getKeyName().equals(table)) {
                return tb;
            }
        }
        return null;
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


        // table => type => condition
        Map<String,Map<String,List<Condition>>> groups = groupConditionMap(conditions);
        builder.append("\n");

        //分组
        builder.append(" TABLES WHERE \n");

        // 核心逻辑演练，
        // 1、流输入参数一律叫做待保存属性（+待输出字段）。
        // 2、全局属性条件（不与表关联的），一律做参数输入校验，函数最新的语句
        // 3、优先out查询，继续判断插入的有效性，查询返回值，留作待保存字段（+待输出字段）
        // 4、由于sql不支持null和pattern和func，故查出数据后继续判断数据有效性
        // 5、若存在全局保存表(定义flow时in修饰的)，开始按照表顺序选出保存字段（从所有待保存字段中找，与表字段名匹配的）
        // 6、由于保存字段由于表修饰不能为空，则优化非空字段作为第2或者第4条件
        // 7、其他嵌套in保存语句最后执行（in显示的修饰）
        // 8、最后返回flow定义时的out对象，若未定义，则输出第3 out的所有内容


        // 其他补充
        // 1、纯的条件先判断
        // 2、除了逻辑条件、分组、排序以外，其他条件作用于表查询
        // 3、表属性条件分析判断：唯一性、非空性、其他满足条件，参数提前判断，而不是到sql才判断


        for (Map.Entry<String,Map<String,List<Condition>>> en : groups.entrySet()) {

            String table = en.getKey();
            Map<String,List<Condition>> types = en.getValue();

            builder.append("    ");
            if (hitInTable(table) != null) {
                builder.append("in ");
            } else {
                builder.append("out ");
            }
            builder.append(table);
            builder.append("\n");
            for (Map.Entry<String,List<Condition>> entry : types.entrySet()) {
                String type = entry.getKey();

                List<Condition> cdns = entry.getValue();
                //排序cdns

                builder.append("        ");
                builder.append(type);
                builder.append("\n");

                for (Condition cdn : cdns) {
                    builder.append("            ");
                    builder.append(cdn.toDescription());
                    builder.append("\n");
                }
            }

        }
        builder.append("\n");

        //in
        builder.append(" IN TABLES \n");
        builder.append("    ");
        for (int idx = 0; idx < inStructs.size(); idx++) {
            Table tb = inStructs.get(idx);
            if (idx > 0) {
                builder.append(",");
            }
            builder.append(tb.getKeyName());

        }
        builder.append("\n\n");

        //out
        builder.append(" OUT TABLES \n");
        builder.append("    ");
        for (int idx = 0; idx < outStructs.size(); idx++) {
            Table tb = outStructs.get(idx);
            if (idx > 0) {
                builder.append(",");
            }
            builder.append(tb.getKeyName());

        }
        builder.append("\n\n");

        //分组
        builder.append(" OUT VIEW \n");
        builder.append("    ");
        builder.append(getResult().getKeyName());

        builder.append("\n");

        builder.append("}\n");

        return builder.toString();
    }
}
