package com.lmj.sfdl.compiler.parser;

import com.lmj.sfdl.compiler.consts.Consts;
import com.lmj.sfdl.compiler.po.*;
import com.lmj.sfdl.compiler.pool.Pool;
import com.lmj.sfdl.utils.TR;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: lingminjun
 * Date: 2018-12-20
 * Time: 12:02 PM
 */
public final class Syntax {

    public static void fileCompiling(File file) {

    }

    public static void codeCompiling(String code) {

        StringBuilder builder = new StringBuilder(code);

        //注释清除
        Syntax.replace(Consts.Pattern.NOTE,builder," ");
        Syntax.replaceSysFunc(builder);

        //开始解析
        Stack<List<Struct>> stack = new Stack<List<Struct>>();
        Stack<Flow> flows = new Stack<Flow>();
        Stack<Scope> scopes = new Stack<Scope>(); //操作，true表示属性，false表示结构
        scopes.push(Scope.GLOBAL);

        int deep = 0;
        boolean isCreateView = false;

        while (builder.length() > 0) {
            if (builder.toString().trim().length() == 0) {
                break;
            }

            //语句结束
            if (Syntax.match(Consts.Pattern.STATEMENT_END,builder) != null) {
                if (scopes.size() > 1) {//最后一个就是全局区
                    scopes.pop();
                }
                if (!stack.isEmpty()) {
                    stack.pop();
                }
                if (!flows.isEmpty()) {
                    flows.pop();
                }
                isCreateView = false;
                if (!stack.isEmpty()) {
                    isCreateView = stack.peek().get(0).getType() == StructType.VIEW;
                }
                continue;
            }

            // 子句结束
            if (Syntax.match(Consts.Pattern.CLAUSE_END,builder) != null) {
                continue;
            }

            //深度进入
            if (Syntax.match(Consts.Pattern.BRACKET_BEGIN,builder) != null) {
                deep += 1;
                if (stack.peek().get(0).getType() == StructType.VIEW) {
                    scopes.push(Scope.VIEW);
                } else {
                    scopes.push(Scope.TABLE);
                }
                continue;
            }

            //括号返回
            if (Syntax.match(Consts.Pattern.BRACKET_END,builder) != null) {

                // 检查属性where限定
                Flow flow = null;
                if (!flows.isEmpty()) {
                    flow = flows.peek();
                }
                if (flow != null) {
                    Scope scope = scopes.peek();
                    Syntax.where(builder, scope, flow);
                }

                deep -= 1;
                if (deep < 0) {
                    throw Syntax.exception(") the writing is not symmetrical", builder);
                }
                if (scopes.size() > 1) {//最后一个就是全局区
                    scopes.pop();
                }
                continue;
            }

            //进入create table命令，
            if (Syntax.match(Consts.Pattern.CREATE_TABLE,builder) != null) {
                //解析create table首部
                List<Struct> structs = Syntax.parseCreateTableHeader(builder);
                if (structs != null && structs.size() > 0) {
                    stack.push(structs);
                }
                continue;
            }

            //进入create view命令，
            if (Syntax.match(Consts.Pattern.CREATE_VIEW,builder) != null) {
                //解析create view首部
                Flow flow = Syntax.parseCreateViewHeader(builder);
                if (flow != null) {
                    flows.push(flow);
                    List<Struct> strs = new ArrayList<Struct>();
                    strs.add(flow.getResult());
                    stack.push(strs);
                    isCreateView = true;
                }
                continue;
            }

            // in、out、inout
            Option option = isCreateView ? Option.IN : Option.INOUT;
            if (Syntax.match(Consts.Pattern.INOUT,builder) != null) {
                option = Option.INOUT;
            }

            if (Syntax.match(Consts.Pattern.IN,builder) != null) {
                option = Option.IN;
            }

            if (Syntax.match(Consts.Pattern.OUT,builder) != null) {
                option = Option.OUT;
            }

            // 取名 group.table.field
            Scope scope = scopes.peek();
            List<Struct> structs = stack.peek();
            Flow flow = null;
            if (!flows.isEmpty()) {
                flow = flows.peek();
            }
            List<Field> fields = Syntax.parseFields(builder,option,scope,structs,flow);

            // 检查字段限定符:notnull、notempty、default、unique、comment、pattern、func
            Syntax.parseFieldQualifier(builder,fields);

            // 检查属性值来源
            Syntax.parseFieldFrom(builder,fields);

            // 检查属性where限定
            Syntax.where(builder,scope,flow);
        }
    }


    public static String match(Pattern pattern, StringBuilder builder, boolean readonly) {
        Matcher m = pattern.matcher(builder);
        if (m.find() && m.start() == 0) {
            String sub = builder.substring(m.start(),m.end());
            String cmd = sub.trim();
            if (!readonly) {
                System.out.println("$ " + cmd);
                builder.delete(m.start(), m.end());
            }
            return cmd;
        }
        return null;
    }

    public static String match(Pattern pattern, String string) {
        return match(pattern,string,false);
    }
    public static String match(Pattern pattern, String string, boolean except) {
        return match(pattern,string,except,false);
    }
    public static String match(Pattern pattern, String string, boolean except, boolean all) {
        Matcher m = pattern.matcher(string);
        if (m.find() && m.start() == 0) {
            if (all && m.end() < string.length()) {
                return null;
            }
            if (except) {
                return string.substring(m.end()).trim();
            } else {
                return string.substring(m.start(), m.end()).trim();
            }
        }
        return null;
    }

    //过滤掉所有匹配到的子串
    public static void filter(Pattern pattern, StringBuilder builder) {
        replace(pattern,builder,null);
    }

    //过滤掉所有匹配到的子串
    public static void replace(Pattern pattern, StringBuilder builder, String replace) {
        Matcher m = pattern.matcher(builder);
        List<Integer[]> range = new ArrayList<Integer[]>();
        while (m.find()) {
            Integer[] rg = new Integer[] {m.start(),m.end()};
            range.add(rg);
        }

        //从后往前
        for (int i = range.size(); i > 0; i--) {
            Integer[] rg = range.get(i - 1);
            System.out.println(builder.substring(rg[0],rg[1]));
            if (replace != null) {
                builder.replace(rg[0],rg[1],replace);
            } else {
                builder.delete(rg[0],rg[1]);
            }
        }
    }

    //过滤掉所有匹配到的子串
    public static void replaceSysFunc(StringBuilder builder) {
        replace(Consts.Func.FUNC_NOW,builder,Consts.Func.SYS_FUNC_NOW);
        replace(Consts.Func.FUNC_DATETIME,builder,Consts.Func.SYS_FUNC_DATE);
    }

    public static void parseFieldQualifier(StringBuilder builder, List<Field> fields) {
        // 是否为设置限定,强制设置
        boolean force = false;
        if (Syntax.match(Consts.Pattern.AS,builder) != null) {
            force = true;
        }

        // 判断类型
        Syntax.TypeLength len = new Syntax.TypeLength();
        Type type = Syntax.type(builder,len);
        if (type != null) {//为每个字符限定类型
            for (Field fld : fields) {
                if (fld.isSetted() || force) {
                    fld.setType(type);
                    fld.setLength(len.length);
                }
            }
        }

        // 检查字段限定符:notnull、notempty、default、unique、comment、pattern、func
        do {
            //不为空
            if (Syntax.match(Consts.Pattern.NOT_NULL,builder) != null) {
                //检查异常
                Code code = Syntax.throwCode(builder);
                for (Field fld : fields) {
                    if (!fld.isNotNull() || force) {
                        fld.setNotNull(true);

                        if (code != null) {
                            fld.setNullCode(code);
                        }
                    }
                }
                continue;
            } else if (Syntax.match(Consts.Pattern.NULL,builder) != null) {//无意义定义，强制设置同样忽略
                continue;
            }

            //不为空
            if (Syntax.match(Consts.Pattern.NOT_EMPTY,builder) != null) {
                //检查异常
                Code code = Syntax.throwCode(builder);
                for (Field fld : fields) {
                    if (!fld.isNotEmpty() || force) {
                        fld.setNotEmpty(true);

                        if (code != null) {
                            fld.setNullCode(code);
                        }
                    }
                }
                continue;
            } else if (Syntax.match(Consts.Pattern.EMPTY,builder) != null) {//无意义定义，强制设置同样忽略
                continue;
            }

            // 默认值
            String defaultValue = Syntax.defaultValue(builder);
            if (defaultValue != null) {
                for (Field fld : fields) {
                    if (fld.getDefaultValue() != null || force) {
                        fld.setDefaultValue(defaultValue);
                    }
                }
                continue;
            }

            // 唯一性
            if (Syntax.match(Consts.Pattern.UNIQUE,builder) != null) {
                //检查异常
                Code code = Syntax.throwCode(builder);
                for (Field fld : fields) {
                    if (!fld.isUnique() || force) {
                        fld.setUnique(true);

                        if (code != null) {
                            fld.setUniqueCode(code);
                        }
                    }
                }
                continue;
            }

            // 注释
            String comment = Syntax.comment(builder);
            if (comment != null) {
                for (Field fld : fields) {
                    if (fld.getComment() == null || force) {
                        fld.setComment(comment);
                    }
                }
                continue;
            }

            // match pattern
            String regex = Syntax.matchPattern(builder);
            if (regex != null) {
                //检查异常
                Code code = Syntax.throwCode(builder);
                for (Field fld : fields) {
                    if (fld.getPattern() == null || force) {
                        fld.setPattern(regex);

                        if (code != null) {
                            fld.setPatternCode(code);
                        }
                    }

                }
                continue;
            }

            // match func
            Field last = null;
            if (fields.size() > 0) {
                last = fields.get(fields.size() - 1);
            }
            Func func = Syntax.matchFunc(builder, last);
            if (func != null) {
                //检查异常
                Code code = Syntax.throwCode(builder);

                for (Field fld : fields) {
                    if (fld.getFunc() == null || force) {

                        if (code != null) {
                            fld.setFuncCode(code);
                        }
                    }
                }
                continue;
            }

            break;
        } while (true);
    }


    public static List<Field> parseFields(StringBuilder builder, Option option, Scope scope,List<Struct> structs, Flow flow) {
        // 强制增加属性场景
        List<Field> fields = new ArrayList<Field>();//每个结构一个field实例

        // 多个名字定义，逗号隔开
        do {
            String nameStr = Syntax.name(builder,3);
            if (nameStr == null) {
                break;
            }

            // 全局
            Name name = Syntax.analyzeName(nameStr,scope);

            if (name.getStruct() != null && name.getField() != null) {//table.field场景
                Struct struct = null;
                if (scope == Scope.VIEW) {
                    struct = Pool.getView(name.getStruct());
                } else {
                    struct = Pool.getTable(name.getStruct());

                    if (flow != null) {
                        if (Option.IN == option || Option.INOUT == option) {
                            flow.addInStruct((Table) struct);
                        }

                        if (Option.OUT == option || Option.INOUT == option) {
                            flow.addOutStruct((Table) struct);
                        }

                    }
                }
                //自动创建属性
                fields.add(struct.getField(name.getField(),true));
            }

            //todo
            else if (name.getField() != null) {//定义属性场景:函数参数，结构属性

                // 根据 in、out、inout
                for (Struct struct : structs) {
                    //给结构创建属性
                    if (struct.getType() == StructType.VIEW) {
                        //属性构建
                        if (option == Option.OUT || option == Option.INOUT) {
                            fields.add(struct.getField(name.getField(),true));

                            // 写入参数
                            if (flow != null && option == Option.INOUT) {
                                flow.getFunc().addParam(struct.getField(name.getField()));
                            }
                        }
                    } else if (struct.getType() == StructType.TABLE) {
                        //创建view的场景下，不主动添加
                        if (option == Option.IN || option == Option.INOUT) {
                            fields.add(struct.getField(name.getField(), true));
                        } else if (option == Option.OUT) {
                            //不应该有out场景
                            throw Syntax.exception("定义TABLE场景,不应该有out参数",builder);
                        }
                    }
                }

                //考虑flow实现
                if (flow != null) {
                    if (option == Option.IN) {
                        fields.add(flow.getFunc().getParam(name.getField(),true));
                    }
                }

            } else if (name.getStruct() != null) {//结构名处理
                if (scope == Scope.VIEW) {
                    //添加view依赖table输出关系
                    for (Struct struct : structs) {
                        //给结构创建属性
                        if (struct.getType() == StructType.VIEW) {
                            if (option == Option.OUT || option == Option.INOUT) {
                                Table table = Pool.getTable(name.getStruct());//创建table
                                ((View)struct).addRelyTable(name.getStruct());
                            }
                        }
                    }

                    //开始考虑flow实现
                    if (flow != null) {

                        if (option == Option.OUT || option == Option.INOUT) {
                            //返回值设置依赖
                            Table table = Pool.getTable(name.getStruct());//创建table
                            flow.getResult().addRelyTable(name.getStruct());

                            flow.addOutStruct(table);
                        }

                        if (option == Option.IN || option == Option.INOUT) {
                            Table table = Pool.getTable(name.getStruct());//创建table
                            flow.addInStruct(table);
                        }
                    }
                } else {//不应该出现此种场景，全局区无故创建table
                    Table table = Pool.getTable(name.getStruct());//创建table
                    if (flow != null) {
                        if (Option.IN == option || Option.INOUT == option) {
                            flow.addInStruct(table);
                        }

                        if (Option.OUT == option || Option.INOUT == option) {
                            flow.addOutStruct(table);
                        }

                    }
//                    Syntax.exception("Ambiguous name content", name.getStruct());
                }
            }

            // 子句结束继续前面的内容
            if (Syntax.match(Consts.Pattern.CLAUSE_END,builder) != null) {
                continue;
            }

            break;

        } while (true);

        return fields;
    }

    // 解析字段来源
    public static void parseFieldFrom(StringBuilder builder, List<Field> fields) {
        if (Syntax.match(Consts.Pattern.FROM,builder) == null) {
            return;
        }

        //1.1 来自于某方法的值
        // out full_name from func `combinationName` (firstName,lastName)
        Func func = Syntax.func(builder,null,null,false);
        if (func != null) {
            for (Field fld : fields) {
                if (fld.getFunc() == null) {
                    fld.setFunc(func);
                }
            }
        }

        //1.2 常量
        String value = Syntax.value(builder);
        if (value != null) {
            //if (!value.equalsIgnoreCase("NULL")) {
            for (Field fld : fields) {
                //不改变已经设置过默认值得数据
                if (fld.getDefaultValue() == null) {
                    fld.setConstant(true);
                    fld.setDefaultValue(value);
                }
            }
            //}
        }

        //1.3 来自于某个结构的属性
        boolean isView = Syntax.match(Consts.Pattern.IS_VIEW,builder) != null;
        boolean isTable = Syntax.match(Consts.Pattern.IS_TABLE,builder) != null;

        String name = Syntax.name(builder,3);
        if (name != null) { //暂时只支持table

            // 全局
            String[] ss = name.split("\\.");
            String structName = name;
            String fieldName = name;
            if (ss.length == 3) {//肯定是结构+属性
                structName = ss[0] + "." + ss[1];
                fieldName = ss[2];
            } else if (ss.length == 2) {//不能确定 FIXME:暂时确定为结构+属性，也有可能是结构
                structName = ss[0];
                fieldName = ss[1];
            } else {
                if (isView) {
                    //补全
                    builder.insert(0," " + name + " ");
                    parseCreateViewHeader(builder);
                    return;
                } else if (isTable) {
                    builder.insert(0," " + name + " ");
                    parseCreateTableHeader(builder);
                    return;
                } else {
                    throw Syntax.exception("属性来源，必须标明来自某个结构，否则无法解析", builder);
                }
            }

            Field field = null;
            if (isView) {
                field = Pool.getView(structName).getField(fieldName,true);
            } else {
                field = Pool.getTable(structName).getField(fieldName,true);
            }
            for (Field fld : fields) {
                //拷贝类型和限定符
                if (!fld.isSetted() && fld.getType() != field.getType()) {
                    fld.setType(field.getType());
                    fld.setLength(field.getLength());
                }
                if (fld.getDefaultValue() == null && field.getDefaultValue() != null) {
                    fld.setDefaultValue(field.getDefaultValue());
                }
                if (!fld.isNotNull() && field.isNotNull()) {
                    fld.setNotNull(field.isNotNull());
                    fld.setNullCode(field.getNullCode());
                }
                if (!fld.isNotEmpty() && field.isNotEmpty()) {
                    fld.setNotEmpty(field.isNotEmpty());
                    fld.setNullCode(field.getNullCode());
                }

                if (fld.getComment() == null && field.getComment() != null) {
                    fld.setComment(field.getComment());
                }
                if (!fld.isUnique() && field.isUnique()) {
                    fld.setUnique(field.isUnique());
                    fld.setUniqueCode(field.getUniqueCode());
                }
            }
        }

    }


    // 1.1 同时描述多个table
    // table [inout] table1 [,table2,table3] (
    //      ...
    // );
    //
    // 1.2 仅仅申明table
    // table [inout] table;
    public static List<Struct> parseCreateTableHeader(StringBuilder builder) {
        Matcher m = null;

        //先匹配out、in、inout符号
        Syntax.match(Consts.Pattern.INOUT,builder);
        Syntax.match(Consts.Pattern.IN,builder);

        //异常定义
        if (Syntax.match(Consts.Pattern.OUT,builder) != null) {//
            Syntax.warning("CREATE TABLE must priority define INOUT structure!",builder);
        }

        List<Struct> list = new ArrayList<Struct>();
        do {
            //获取table名
            String name = Syntax.name(builder,2);
            if (name == null) {
                throw Syntax.exception("CREATE TABLE must define INOUT structure!",builder);
            }

            //取组名
            Table struct = Pool.getTable(name);
            if (struct != null) {
                list.add(struct);
            } else {
                break;
            }

            //可以理解为仅仅声明存储对象，返回空
            if (Syntax.match(Consts.Pattern.STATEMENT_END,builder) != null) {
                return null;//
            }

            //子句结束,继续下一个名字
            if (Syntax.match(Consts.Pattern.CLAUSE_END,builder) != null) {
                continue;
            }

            break;

        } while (true);

        //处理开始和结束scope
        Syntax.checkScope(builder);

        return list;
    }

    // 2.1 多表写,平铺
    // view [out|inout] view_name [as func func_name] [in table1,table2,table3] (
    //      ...
    // ) [where ...] ;

    // 2.2 多表读(平铺与，嵌套)
    // view [out|inout] view_name [as func func_name] [out table1,table2,table3] (
    //      ...
    //      [out column_name from create view sub_view (
    //          ...
    //      )]
    // ) [where ...] ;

    // 2.3 多表读写，阶段
    // view [out|inout] view_name [as func func_name] in table1 (
    //      ...
    // ) [where ...]
    // , out table2 (
    //      ...
    // ) [where ...]
    // ;
    public static Flow parseCreateViewHeader(StringBuilder builder) {
        Matcher m = null;
        //先匹配out、in、inout符号
        Syntax.match(Consts.Pattern.OUT,builder);
        Syntax.match(Consts.Pattern.INOUT,builder);


        //异常定义
        if (Syntax.match(Consts.Pattern.IN,builder) != null) {//
            Syntax.warning("CREATE VIEW must priority define OUT structure!",builder);
        }

        //获取View名
        List<View> structs = new ArrayList<View>();
        do {
            String name = Syntax.name(builder,2);
            if (name == null) {
                throw Syntax.exception("CREATE VIEW must define OUT structure!",builder);
            }

            //取组名
            View struct = Pool.getView(name);
            structs.add(struct);

            //可以理解为仅仅声明存储对象，返回空
            if (Syntax.match(Consts.Pattern.STATEMENT_END,builder) != null) {
                return null;//
            }

            //子句结束,继续下一个名字
            if (Syntax.match(Consts.Pattern.CLAUSE_END,builder) != null) {
                continue;
            }

            break;

        } while (true);

        // 不允许同时定义多个输出结构
        if (structs.size() > 1) {
            throw Syntax.exception("CREATE VIEW just one OUT structure!",builder);
        }
        View struct = structs.get(0);

        // 取方法名（你们方法如何设置）
        String method = null;
        if (Syntax.match(Consts.Pattern.AS_FUNC,builder) != null) {
            method = Syntax.name(builder,2);
            //自动分组
            if (struct.getGroup() != null && !method.contains(".")) {
                method = struct.getGroup() + "." + method;
            }
        } else {//自动生成名字
            method = struct.autogenousFuncName();
        }

        //压栈flow
        Flow flow = Pool.getFlow(method);
        flow.setResult(struct);
        flow.getFunc().setReturnStruct(struct);

        //处理开始和结束scope
        Syntax.checkScope(builder);

        return flow;

    }


    public static Name analyzeName(String name, Scope scope) {
        // 全局
        String[] ss = name.split("\\.");
        String structName = name;
        String fieldName = name;
        if (ss.length == 3) {
            structName = ss[0] + "." + ss[1];
            fieldName = ss[2];
        } else if (ss.length == 2) {
            structName = ss[0];
            fieldName = ss[1];
        } else {
            // 全局情况下，取到名字，默认都是结构
            if (scope == Scope.GLOBAL) {
                structName = name;
                fieldName = null;
            } else {
                structName = null;
                fieldName = name;
            }
        }
        return new Name(structName,fieldName);
    }

    // 匹配名字，支持``字符包含以及`dd`.`aaa`或`dd.aaaa`
    public static String name(StringBuilder builder, int limit) {
        String name = Syntax.match(Consts.Pattern.NAME,builder);
        if (name == null) {
            return null;
        }

        return name(name,limit);
    }
    public static String name(String name, int limit) {

        String[] ss = name.split("\\.",-1);
        if (ss.length > limit) {
            throw Syntax.exception("Too many . symbol!",name);
        }

        StringBuilder nameBuilder = new StringBuilder();
        int times = 0;
        for (String s : ss) {
            Matcher m = Consts.Pattern.NAME_MATCH.matcher(s);
            if (!m.find()) {
                throw Syntax.exception("Illegal character!", name);
            }

            if (s.startsWith("`") && s.endsWith("`")) {
                s = s.substring(1, s.length() - 1);
                times += 2;
            } else if (s.startsWith("`")) {
                s = s.substring(1, s.length());
                times += 1;
            } else if (s.endsWith("`")) {
                s = s.substring(0, s.length() - 1);
                times += 1;
            }

            if (nameBuilder.length() > 0) {
                nameBuilder.append(".");
            }
            nameBuilder.append(s);
        }

        if (times%2 != 0) {
            throw Syntax.exception("Asymmetric ` symbol!", name);
        }

        return nameBuilder.toString();
    }

    public static void where(StringBuilder builder, Scope scope, Flow flow) {
        // 检查属性where限定
        if (Syntax.match(Consts.Pattern.WHERE,builder) != null) {
            if (scope == Scope.TABLE) {//
                throw Syntax.exception("定义TABLE时不应该出现WHERE子句", builder);
            }

            //先处理异常
            Code whereCode = Syntax.throwCode(builder);
            if (whereCode != null) {
                flow.setWhereCode(whereCode);
            }

            //处理逻辑条件
            List<Condition> cds = parseWhereCondition(builder,flow);
            flow.addConditions(cds);
        }
    }

    private static Field parseConditionField(String string, Condition condition, Flow flow) {
        if (string != null) {
            List<Field> fields = analyzeField(string, flow, condition);
            if (fields != null) {
                condition.addFields(fields);
            }
            if (fields.size() == 1) {
                return fields.get(0);
            }
        }
        return null;
    }

    // smscode [=|<|>|>=|<=|!=|==] captcha.code
    // (captcha.created_at + 300) >= now()
    // mobile MATCH func `matchMobileFormat` throw -103 '手机号格式不正确'
    private static List<Condition> parseWhereCondition(StringBuilder builder, Flow flow) {
        List<Condition> list = new ArrayList<Condition>();
        do {

            if (Syntax.match(Consts.Pattern.STATEMENT_END,builder) != null) {
                break;
            }
            if (Syntax.match(Consts.Pattern.CLAUSE_END,builder) != null) {
                break;
            }

            LogicJoin join = LogicJoin.AND;
            if (Syntax.match(Consts.Pattern.AND,builder) != null) {
                join = LogicJoin.AND;
            } else if (Syntax.match(Consts.Pattern.OR,builder) != null) {
                join = LogicJoin.OR;
            }

            String cdt = Syntax.match(Consts.Pattern.LOGIC_CONDITION,builder);
            if (cdt != null) {
                Condition condition = new Condition();
                condition.setJoin(join);

                condition.setCondition(cdt);

                //先处理异常
                Code code = Syntax.throwCode(builder);
                if (code != null) {
                    condition.setNoCode(code);
                }

                list.add(condition);

                //TODO 分析逻辑左右 属性字段，这个最难，还需要支持四则运算，如： sku.stock - 5 > 0
                String left = Syntax.match(Consts.Pattern.LOGIC_LEFT,cdt);
                parseConditionField(left,condition,flow);

                String right = Syntax.match(Consts.Pattern.LOGIC_RIGHT,cdt,true);
                parseConditionField(right,condition,flow);

                continue;
            }

            // is null
            String nullStr = Syntax.match(Consts.Pattern.NULL_CONDITION,builder);
            if (nullStr != null) {
                NullCondition condition = new NullCondition();
                condition.setJoin(join);

                int idx = nullStr.toUpperCase().lastIndexOf("IS");

                parseConditionField(nullStr.substring(0,idx),condition,flow);

                condition.setNotNvll(nullStr.substring(idx).toUpperCase().contains("NOT"));

                //先处理异常
                Code code = Syntax.throwCode(builder);
                if (code != null) {
                    condition.setNoCode(code);
                }

                list.add(condition);

                continue;
            }
            // not empty
            String emptyStr = Syntax.match(Consts.Pattern.EMPTY_CONDITION,builder);
            if (emptyStr != null) {
                EmptyCondition condition = new EmptyCondition();
                condition.setJoin(join);

                int idx = emptyStr.toUpperCase().lastIndexOf("IS");
                parseConditionField(emptyStr.substring(0,idx),condition,flow);

                condition.setNotEmpty(emptyStr.substring(idx).toUpperCase().contains("NOT"));

                //先处理异常
                Code code = Syntax.throwCode(builder);
                if (code != null) {
                    condition.setNoCode(code);
                }

                list.add(condition);

                continue;
            }

            String str1 = Syntax.match(Consts.Pattern.HAS_FUNC_CONDITION,builder);
            if (str1 != null) {
                FuncCondition condition = new FuncCondition();
                condition.setJoin(join);

                int idx = str1.toUpperCase().lastIndexOf("MATCH");
                Field field = parseConditionField(str1.substring(0,idx),condition,flow);
                Func func = Syntax.matchFunc(builder.insert(0,"match func "),field);
                if (func != null) {
                    condition.setFunc(func);
                }

                //先处理异常
                Code code = Syntax.throwCode(builder);
                if (code != null) {
                    condition.setNoCode(code);
                }

                list.add(condition);
                continue;
            }

            String str2 = Syntax.match(Consts.Pattern.HAS_PATTERN_CONDITION,builder);
            if (str2 != null) {
                PatternCondition condition = new PatternCondition();
                condition.setJoin(join);

                int idx = str2.toUpperCase().lastIndexOf("MATCH");
                parseConditionField(str2.substring(0,idx),condition,flow);
                String pattern = Syntax.matchPattern(builder.insert(0,"match pattern "));
                if (pattern != null) {
                    condition.setPattern(pattern);
                }

                //先处理异常
                Code code = Syntax.throwCode(builder);
                if (code != null) {
                    condition.setNoCode(code);
                }

                list.add(condition);
                continue;
            }

            // order by
            String str3 = Syntax.match(Consts.Pattern.ORDER_CONDITION,builder);
            if (str3 != null) {
                OrderCondition condition = new OrderCondition();
                condition.setJoin(join);

                int idx = str3.toUpperCase().lastIndexOf("ORDER");
                parseConditionField(str3.substring(0,idx),condition,flow);
                condition.setOrder(str3.substring(idx).toUpperCase().contains("ASC") ? Order.ASC : Order.DESC);
                list.add(condition);
                continue;
            }

            // group by
            String str4 = Syntax.match(Consts.Pattern.GROUP_CONDITION,builder);
            if (str4 != null) {
                GroupCondition condition = new GroupCondition();
                condition.setJoin(join);

                int idx = str4.toUpperCase().lastIndexOf("GROUP");
                parseConditionField(str4.substring(0,idx),condition,flow);
                list.add(condition);
                continue;
            }

            if (builder.toString().trim().length() == 0) {
                break;
            }
        } while (true);

        return list;
    }

    private static List<Field> analyzeField(String string, Flow flow, Condition condition) {
        List<Field> list = new ArrayList<Field>();

        //四则运算，开始拆分
        String[] ss = string.split(Consts.Pattern.LOGIC_SPLITE_PATTERN);
        for (String nameString : ss) {
            nameString = nameString.trim();
            if (nameString.length() <= 0) {
                continue;
            }

            if (Consts.Func.contains(nameString)) {
                continue;
            }

            //判断是否为数字
            String value = Syntax.valueChecked(nameString);
            if (value != null) {
                continue;
            }

            Name name = Syntax.analyzeName(nameString, Scope.VIEW);
            Field field = null;
            if (name.getStruct() != null && name.getField() != null) {
                //where子句中只取table
                Table table = Pool.getTable(name.getStruct());
                field = table.getField(name.getField(), true);
                //flow没有记录此存储结构，则默认加入到输出存储结构中
                if (!flow.containStruct(table.getKeyName())) {
                    flow.addOutStruct(table);
                }
                if (condition != null) {
                    condition.addTable(table.getKeyName());
                }
            } else if (name.getField() != null) {
                if (flow.containParam(name.getField())) {//从参数查找
                    field = flow.getParam(name.getField());
                } else if (flow.getResult().containField(name.getField())) {//从返回值查找
                    field = flow.getResult().getField(name.getField());
                } else {//从输出参数
                    field = flow.getParam(name.getField(), true);
                }
            } else if (name.getStruct() != null) {
                throw Syntax.exception("此时不应该出现单个结构信息", name.getStruct());
            }

            list.add(field);
        }

        return list;

    }

    public static class TypeLength {
        public Integer length;
    }
    public static Type type(StringBuilder builder, TypeLength length) {

        if (match(Consts.Type.BOOL,builder) != null) {
            return Type.BOOL;
        }
        if (match(Consts.Type.INT,builder) != null) {
            return Type.INT;
        }
        if (match(Consts.Type.LONG,builder) != null) {
            return Type.LONG;
        }
        if (match(Consts.Type.FLOAT,builder) != null) {
            return Type.FLOAT;
        }
        if (match(Consts.Type.DOUBLE,builder) != null) {
            return Type.DOUBLE;
        }
        if (match(Consts.Type.DOUBLE,builder) != null) {
            return Type.DOUBLE;
        }
        if (match(Consts.Type.STRING,builder) != null) {
            return Type.STRING;
        }
        if (match(Consts.Type.DATETIME,builder) != null) {
            return Type.DATETIME;
        }
        if (match(Consts.Type.TIMESTAMP,builder) != null) {
            return Type.TIMESTAMP;
        }

        String type = match(Consts.Type.VARCHAR,builder);
        if (type != null) {
            if (length != null) {
                int idx = type.indexOf("(");
                if (idx > 0 && idx < type.length()) {
                    length.length = TR.integer(type.substring(idx + 1, type.length() - 1), Consts.Type.DEFAULT_VARCHAR_LENGTH);
                } else {
                    length.length = Consts.Type.DEFAULT_VARCHAR_LENGTH;
                }
            }
            return Type.VARCHAR;
        }

        return null;
//        // 默认类型
//        if (length != null) {
//            length.length = Consts.Type.DEFAULT_VARCHAR_LENGTH;
//        }
//        return Type.VARCHAR;
    }

    public static Code throwCode(StringBuilder builder) {
        if (match(Consts.Pattern.THROW,builder) == null) {
            return null;
        }

        String codeStr = match(Consts.Pattern.CODE,builder);
        int code = Consts.Code.DEFAULT_CODE;
        if (codeStr != null) {
            code = TR.integer(codeStr,Consts.Code.DEFAULT_CODE);
        }

        //含单双引号
        String msg = match(Consts.Pattern.CONTENT,builder);
        if (msg == null || msg.length() == 0) {
            throw Syntax.exception("An exception thrown must have a message!", builder);
        }
        return new Code(code,msg.substring(1,msg.length()-1));
    }

    public static String defaultValue(StringBuilder builder) {
        if (match(Consts.Pattern.DEFAULT,builder) == null) {
            return null;
        }
        //取内容
        return value(builder);
    }

    public static String valueChecked(String string) {
        if (string == null || string.length() == 0) {
            return null;
        }
        string = string.trim();

        //是数字
        String value = match(Consts.Pattern.VALUE,string,false,true);
        if (value != null) {
            return value;
        }
        value = match(Consts.Pattern.LOGIC_SPLITE_NULL,string,false,true);//无意义的null
        if (value != null) {
            return "NULL";
        }
        value = match(Consts.Pattern.LOGIC_SPLITE_TRUE_OR_FALSE,string,false,true);//无意义的null
        if (value != null) {
            return value.equalsIgnoreCase("true") ? "1" : "0";//更加通用
        }

        //取字符串
        value = match(Consts.Pattern.CONTENT,string,false,true);
        if (value != null) {
            return value.substring(1,value.length()-1);
        }

        return null;
    }

    public static String value(StringBuilder builder) {
        String value = match(Consts.Pattern.VALUE,builder);
        if (value != null) {
            return value;
        }
        value = match(Consts.Pattern.NULL,builder);//无意义的null
        if (value != null) {
            return null;
        }
        value = match(Consts.Pattern.TRUE_OR_FALSE,builder);//无意义的null
        if (value != null) {
            return value.equalsIgnoreCase("true") ? "1" : "0";//更加通用
        }
        //取内容
        return content(builder);
    }

    public static String content(StringBuilder builder) {
        String msg = match(Consts.Pattern.CONTENT,builder);
        if (msg == null) {
            return null;
        }
        return msg.substring(1,msg.length()-1);
    }

    public static String comment(StringBuilder builder) {
        return content(builder);
    }

    /**
     * 匹配方法，修饰原有属性
     * @param builder
     * @param field
     * @return
     */
    public static Func matchFunc(StringBuilder builder, Field field) {
        return func(builder,field,null,true);
    }
    public static Func func(StringBuilder builder, Field field, View struct, boolean check) {
        if (match(Consts.Pattern.MATCH_FUNC,builder) != null) {//ok
            check = true;
        } else if (match(Consts.Pattern.FUNC,builder) != null) {//ok

        } else {
            return null;
        }
        String method = Syntax.name(builder,2);
        if (method == null) {
            throw Syntax.exception("MATCH FUNC must define function name!", builder);
        }

        Func func = new Func();
        func.setName(method);
        if (struct != null) {
            func.setReturnStruct(struct);
        } else if (check) {
            func.setCheck(true);
        } else {//默认返回值类型
            func.setReturnValue(new Field("return"));
        }

        // 读取参数
        // 1.1 多个参数，括号括起来
        List<String> params = new ArrayList<String>();
        if (match(Consts.Pattern.BRACKET_BEGIN,builder) != null) {
            int limit = 0;
            do {
                if (match(Consts.Pattern.CLAUSE_END,builder) != null) {
                }

                if (match(Consts.Pattern.IN,builder) != null) {
                }

                if (match(Consts.Pattern.INOUT,builder) != null) {
                    throw Syntax.exception("The function does not allow output parameters!", builder);
                }
                if (match(Consts.Pattern.OUT,builder) != null) {
                    throw Syntax.exception("The function does not allow output parameters!", builder);
                }

                boolean isView = match(Consts.Pattern.IS_VIEW,builder) != null;


                String nameStr = name(builder,3);
                if (nameStr == null) {
                    break;
                }
                Name name = Syntax.analyzeName(nameStr,isView?Scope.VIEW:Scope.TABLE);
                Field fld = null;
                if (name.getStruct() != null && name.getField() != null) {//table.field场景
                    Struct temStruct = null;
                    if (isView) {
                        temStruct = Pool.getView(name.getStruct());
                    } else {
                        temStruct = Pool.getTable(name.getStruct());
                    }
                    //自动创建属性
                    fld = temStruct.getField(name.getField(),true);
                } else {
                    fld = new Field(name.getField());
                }

                func.addParam(fld);
                params.add(name.getField());

                limit++;
                if (limit > Consts.Syntax.LIMIT_FUNC_PARAM_LENGTH) {
                    throw Syntax.exception("The number of function arguments is limited to " + Consts.Syntax.LIMIT_FUNC_PARAM_LENGTH + "!", builder);
                }
            } while (match(Consts.Pattern.BRACKET_END,builder) == null);
        }

        // 默认将属性作为参数
        if (func.getParams().size() == 0 && field != null) {
            func.addParam(field/*.copy()*/);
        }

        return func;
    }

    public static final java.util.regex.Pattern SUB_STRING = java.util.regex.Pattern.compile("(('.*?(?<!\\\\)')|(\\\".*?(?<!\\\\)\\\"))", java.util.regex.Pattern.CASE_INSENSITIVE);
    public static void checkScope(StringBuilder builder) {
        int begin = -1;
        int end = -1;
        int deep = 0;
        for (int i = 0; i < builder.length(); i++) {
            char c = builder.charAt(i);
            //遇到字符，需要跳过
            if (c == '\'' || c == '\"') {
                Matcher m = SUB_STRING.matcher(builder.substring(i));
                if (m.find() && m.start() == 0) {
                    i += m.end() - 1;
                }
                continue;
            }

            if (c == '(' && begin == -1) {
                begin = i;
                continue;
            }
            if (c == '(') {
                deep++;
            } else if (c == ')') {
                deep--;
            }
            if (deep == 0) {
                end = i;
                break;
            }
        }

        if (begin != -1 && end != -1) {
            builder.replace(end, end + 1, Consts.TAG.SCOPE_END);
            builder.replace(begin, begin + 1, Consts.TAG.SCOPE_BEGIN);
        }
    }

    public static String matchPattern(StringBuilder builder) {
        if (match(Consts.Pattern.MATCH_PATTERN,builder) == null) {
            return null;
        }
        String pattern = Syntax.content(builder);
        if (pattern == null) {
            throw Syntax.exception("MATCH PATTERN must define the regular expression!", builder);
        }
        return pattern;
    }



    public static int relyNest(String cdt) {
        if (cdt == null || cdt.length() == 0) {
            return 0;
        }
        int idx = 0;
        int rt = 0;
        do {
            char c = cdt.charAt(idx);
            //直接计算到结束
            if (c == '\'' || c == '\"') {
                Matcher m = SUB_STRING.matcher(cdt.substring(idx));
                if (m.find() && m.start() == 0) {
                    idx += m.end() - 1;
                }
            } else if (c == '(') {
                rt += 1;
            } else if (c == ')') {
                rt -= 1;
            }
            idx++;
        } while (idx < cdt.length());
        return rt;
    }

    public static String match(Pattern pattern, StringBuilder builder) {
        return match(pattern,builder,false);
    }

    public static RuntimeException exception(String msg,StringBuilder builder) {
        throw new RuntimeException("Syntax error！" + msg + " near by " + builder.substring(0,10));
    }

    public static RuntimeException exception(String msg,String location) {
        throw new RuntimeException("Syntax error！" + msg + " near by " + location);
    }

    public static void warning(String msg,StringBuilder builder) {
        System.out.println("Warning! Syntax error！[ignore] " + msg + " near by " + builder.substring(0,10));
    }

    public static void warning(String msg,String location) {
        System.out.println("Warning! Syntax error！[ignore] " + msg + " near by " + location);
    }
}
