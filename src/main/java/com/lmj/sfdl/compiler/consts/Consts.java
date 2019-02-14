package com.lmj.sfdl.compiler.consts;

import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: lingminjun
 * Date: 2018-12-18
 * Time: 9:36 PM
 */
public final class Consts {

    public static void main(String[] vs) {
        String str1 = " sku.stock_rate * (sku.stock + 5) ";//(?:b)

        String ptn1 = "\\+|\\-|\\*|/|％|(\\+\\+)|(\\-\\-)|&|\\||\\^|〜|(\\<\\<)|(\\>\\>\\>)|(\\>\\>)|\\(|\\)";

        String[] ss = str1.split(ptn1);
        for (String s : ss) {
            System.out.println(s);
        }


//        String ptn2 = "\\s*\\\".*?(?<!\\\\)\\\"";
//        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\sNOW\\(\\)\\s", java.util.regex.Pattern.CASE_INSENSITIVE);
//
////        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\s*((\\\".*?\\\")|('.*?(?<!\\')'))", java.util.regex.Pattern.CASE_INSENSITIVE);
//        Matcher m = pattern.matcher(str1);
//        if (m.find()) {
//            System.out.println("[" + str1.substring(m.start(),m.end()) + "]");
//        }

//        pattern = java.util.regex.Pattern.compile(ptn2, java.util.regex.Pattern.CASE_INSENSITIVE);
//        Matcher m2 = pattern.matcher(str2);
//        if (m2.find()) {
//            System.out.println(str2.substring(m2.start(),m2.end()));
//        }
    }


    public final static class Code {
        public static final int DEFAULT_CODE = -100;
    }

    public final static class Syntax {
        public static final int LIMIT_FUNC_PARAM_LENGTH = 100; //函数最多100个参数
    }

    public final static class TAG {
        public static final String VIEW = "VIEW";
        public static final String TABLE = "TABLE";
        public static final String INOUT = "INOUT";
        public static final String IN = "IN";
        public static final String OUT = "OUT";
        public static final String NULL = "NULL";
        public static final String EMPTY = "EMPTY";
        public static final String NOT = "NOT";
        public static final String UNIQUE = "UNIQUE";
        public static final String DEFAULT = "DEFAULT";
        public static final String WHERE = "WHERE";
        public static final String ORDER = "ORDER";
        public static final String GROUP = "GROUP";
        public static final String THROW = "THROW";
        public static final String FUNC = "FUNC";
        public static final String PATTERN = "PATTERN";
        public static final String AS = "AS";
        public static final String MATCH = "MATCH";
        public static final String FROM = "FROM";

        public static final String AND = "AND";
        public static final String OR = "OR";
        public static final String ASC = "ASC";
        public static final String DESC = "DESC";

        //整理括号问题
        public static final String SCOPE_BEGIN = " @(@ ";
        public static final String SCOPE_END = " @)@ ";
    }

    public final static class Pattern {
        public static final java.util.regex.Pattern IS_VIEW = java.util.regex.Pattern.compile("^\\s*VIEW\\s+", java.util.regex.Pattern.CASE_INSENSITIVE);
        public static final java.util.regex.Pattern IS_TABLE = java.util.regex.Pattern.compile("^\\s*TABLE\\s+", java.util.regex.Pattern.CASE_INSENSITIVE);

        public static final java.util.regex.Pattern CREATE_VIEW = java.util.regex.Pattern.compile("^\\s*VIEW\\s+", java.util.regex.Pattern.CASE_INSENSITIVE);
        public static final java.util.regex.Pattern CREATE_TABLE = java.util.regex.Pattern.compile("^\\s*TABLE\\s+", java.util.regex.Pattern.CASE_INSENSITIVE);

        public static final java.util.regex.Pattern INOUT = java.util.regex.Pattern.compile("^\\s*INOUT\\s+", java.util.regex.Pattern.CASE_INSENSITIVE);
        public static final java.util.regex.Pattern IN = java.util.regex.Pattern.compile("^\\s*IN\\s+", java.util.regex.Pattern.CASE_INSENSITIVE);
        public static final java.util.regex.Pattern OUT = java.util.regex.Pattern.compile("^\\s*OUT\\s+", java.util.regex.Pattern.CASE_INSENSITIVE);
        public static final java.util.regex.Pattern NOT_NULL = java.util.regex.Pattern.compile("^\\s*NOT\\s+NULL(?=(\\,|@\\)|;|\\s))", java.util.regex.Pattern.CASE_INSENSITIVE);
        public static final java.util.regex.Pattern NOT_EMPTY = java.util.regex.Pattern.compile("^\\s*NOT\\s+EMPTY(?=(\\,|@\\)|;|\\s))", java.util.regex.Pattern.CASE_INSENSITIVE);
        public static final java.util.regex.Pattern EMPTY = java.util.regex.Pattern.compile("^\\s*EMPTY(?=(\\,|@\\)|;|\\s))", java.util.regex.Pattern.CASE_INSENSITIVE);
        public static final java.util.regex.Pattern NULL = java.util.regex.Pattern.compile("^\\s*NULL(?=(\\,|@\\)|;|\\s))", java.util.regex.Pattern.CASE_INSENSITIVE);
        public static final java.util.regex.Pattern TRUE_OR_FALSE = java.util.regex.Pattern.compile("^\\s*TRUE|FALSE(?=(\\,|@\\)|;|\\s))", java.util.regex.Pattern.CASE_INSENSITIVE);
        public static final java.util.regex.Pattern UNIQUE = java.util.regex.Pattern.compile("^\\s*UNIQUE(?=(\\,|@\\)|;|\\s))", java.util.regex.Pattern.CASE_INSENSITIVE);

        public static final java.util.regex.Pattern DEFAULT = java.util.regex.Pattern.compile("^\\s*DEFAULT\\s+", java.util.regex.Pattern.CASE_INSENSITIVE);

        public static final java.util.regex.Pattern WHERE = java.util.regex.Pattern.compile("^\\s*WHERE\\s+", java.util.regex.Pattern.CASE_INSENSITIVE);

        public static final java.util.regex.Pattern THROW = java.util.regex.Pattern.compile("^\\s*THROW\\s+", java.util.regex.Pattern.CASE_INSENSITIVE);
        public static final java.util.regex.Pattern CODE = java.util.regex.Pattern.compile("^\\s*\\-{0,1}[0-9]+", java.util.regex.Pattern.CASE_INSENSITIVE);

        public static final java.util.regex.Pattern MATCH_FUNC = java.util.regex.Pattern.compile("^\\s*MATCH\\s+FUNC\\s+", java.util.regex.Pattern.CASE_INSENSITIVE);
        public static final java.util.regex.Pattern MATCH_PATTERN = java.util.regex.Pattern.compile("^\\s*MATCH\\s+PATTERN\\s+", java.util.regex.Pattern.CASE_INSENSITIVE);


        public static final java.util.regex.Pattern FUNC = java.util.regex.Pattern.compile("^\\s*FUNC\\s+", java.util.regex.Pattern.CASE_INSENSITIVE);
        public static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile("^\\s*PATTERN\\s+", java.util.regex.Pattern.CASE_INSENSITIVE);
        public static final java.util.regex.Pattern AS = java.util.regex.Pattern.compile("^\\s*AS\\s+", java.util.regex.Pattern.CASE_INSENSITIVE);
        public static final java.util.regex.Pattern MATCH = java.util.regex.Pattern.compile("^\\s*MATCH\\s+", java.util.regex.Pattern.CASE_INSENSITIVE);
        public static final java.util.regex.Pattern FROM = java.util.regex.Pattern.compile("^\\s*FROM\\s+", java.util.regex.Pattern.CASE_INSENSITIVE);


        public static final java.util.regex.Pattern AS_FUNC = java.util.regex.Pattern.compile("^\\s*AS\\s+FUNC\\s+", java.util.regex.Pattern.CASE_INSENSITIVE);

        public static final java.util.regex.Pattern AND = java.util.regex.Pattern.compile("^\\s*AND\\s+", java.util.regex.Pattern.CASE_INSENSITIVE);
        public static final java.util.regex.Pattern OR = java.util.regex.Pattern.compile("^\\s*OR\\s+", java.util.regex.Pattern.CASE_INSENSITIVE);

        //注解行识别
        public static final java.util.regex.Pattern NOTE = java.util.regex.Pattern.compile("\\s*(#|//).*", java.util.regex.Pattern.CASE_INSENSITIVE);

        //取名(不支持$符，其他语言不支持)
        public static final java.util.regex.Pattern NAME_MATCH = java.util.regex.Pattern.compile("(`){0,1}[a-zA-Z_]+[\\w]*(`){0,1}", java.util.regex.Pattern.CASE_INSENSITIVE);
        public static final java.util.regex.Pattern NAME = java.util.regex.Pattern.compile("^\\s*(`){0,1}[\\w\\.]+(`){0,1}(?=(\\,|\\(|\\)|;|\\s))", java.util.regex.Pattern.CASE_INSENSITIVE);

        //取内容，以单双引号串起来的内容
        public static final java.util.regex.Pattern CONTENT = java.util.regex.Pattern.compile("^\\s*(('.*?(?<!\\\\)')|(\\\".*?(?<!\\\\)\\\"))", java.util.regex.Pattern.CASE_INSENSITIVE);

        // 负数减号到数字中间有空格，如 - 13，可能不算一个数字
        public static final java.util.regex.Pattern VALUE = java.util.regex.Pattern.compile("^\\s*\\-?[0-9]+(\\.[0-9]+)?", java.util.regex.Pattern.CASE_INSENSITIVE);

        //语句结束
        public static final java.util.regex.Pattern STATEMENT_END = java.util.regex.Pattern.compile("^\\s*;", java.util.regex.Pattern.CASE_INSENSITIVE);
        //括号开始
        public static final java.util.regex.Pattern BRACKET_BEGIN = java.util.regex.Pattern.compile("^\\s*(\\(|@\\(@)", java.util.regex.Pattern.CASE_INSENSITIVE);
        //括号结束
        public static final java.util.regex.Pattern BRACKET_END = java.util.regex.Pattern.compile("^\\s*(\\)|@\\)@)", java.util.regex.Pattern.CASE_INSENSITIVE);


        //分割子句
        public static final java.util.regex.Pattern CLAUSE_END = java.util.regex.Pattern.compile("^\\s*,", java.util.regex.Pattern.CASE_INSENSITIVE);

        //逻辑判断 condition CONDITION
        public static final java.util.regex.Pattern LOGIC_CONDITION = java.util.regex.Pattern.compile("^\\s*.+?\\s*(==|=|\\<=|\\>=|\\<|\\>|\\!=)\\s*.+?\\s*(?=(throw|and|or|\\n|\\r|\\,|@\\)|;))", java.util.regex.Pattern.CASE_INSENSITIVE);
        public static final java.util.regex.Pattern HAS_FUNC_CONDITION = java.util.regex.Pattern.compile("^\\s*.+?\\s*MATCH\\s+FUNC\\s*", java.util.regex.Pattern.CASE_INSENSITIVE);
        public static final java.util.regex.Pattern HAS_PATTERN_CONDITION = java.util.regex.Pattern.compile("^\\s*.+?\\s*MATCH\\s+PATTERN\\s*", java.util.regex.Pattern.CASE_INSENSITIVE);
        public static final java.util.regex.Pattern ORDER_CONDITION = java.util.regex.Pattern.compile("^\\s*.+?\\s+ORDER\\s+(ASC|DESC)(?=(\\,|@\\)|;|\\s))", java.util.regex.Pattern.CASE_INSENSITIVE);
        public static final java.util.regex.Pattern GROUP_CONDITION = java.util.regex.Pattern.compile("^\\s*.+?\\s+GROUP(?=(\\,|@\\)|;|\\s))", java.util.regex.Pattern.CASE_INSENSITIVE);
        public static final java.util.regex.Pattern NULL_CONDITION = java.util.regex.Pattern.compile("^\\s*.+?\\s+IS\\s+(NOT\\s+){0,1}NULL(?=(\\,|@\\)|;|\\s))", java.util.regex.Pattern.CASE_INSENSITIVE);
        public static final java.util.regex.Pattern EMPTY_CONDITION = java.util.regex.Pattern.compile("^\\s*.+?\\s+IS\\s+(NOT\\s+){0,1}EMPTY(?=(\\,|@\\)|;|\\s))", java.util.regex.Pattern.CASE_INSENSITIVE);

        public static final java.util.regex.Pattern LOGIC_LEFT = java.util.regex.Pattern.compile("^\\s*.+?\\s*(?=(==|=|\\<=|\\>=|\\<|\\>|\\!=))", java.util.regex.Pattern.CASE_INSENSITIVE);
        public static final java.util.regex.Pattern LOGIC_RIGHT = java.util.regex.Pattern.compile("^\\s*.+?\\s*(==|=|\\<=|\\>=|\\<|\\>|\\!=)", java.util.regex.Pattern.CASE_INSENSITIVE);

        //分割无关内容
        public static final String LOGIC_SPLITE_PATTERN = "\\+|\\-|\\*|/|％|(\\+\\+)|(\\-\\-)|&|\\||\\^|〜|(\\<\\<)|(\\>\\>\\>)|(\\>\\>)|\\(|\\)";
        public static final java.util.regex.Pattern LOGIC_SPLITE_NULL = java.util.regex.Pattern.compile("^\\s*NULL\\s*", java.util.regex.Pattern.CASE_INSENSITIVE);
        public static final java.util.regex.Pattern LOGIC_SPLITE_TRUE_OR_FALSE = java.util.regex.Pattern.compile("^\\s*TRUE|FALSE\\s*", java.util.regex.Pattern.CASE_INSENSITIVE);

//        public static final java.util.regex.Pattern  LINE_END = java.util.regex.Pattern.compile("^\\s*(\\n|\\r)", java.util.regex.Pattern.CASE_INSENSITIVE);

//        public static final java.util.regex.Pattern  LOGIC_CONDITION = java.util.regex.Pattern.compile("^\\s*(.+?\\s*(==|=|\\<=|\\>=|\\<|\\>|\\!=)\\s*.+?\\s*)|", java.util.regex.Pattern.CASE_INSENSITIVE);


    }

    public final static class Type {

        public static final int DEFAULT_VARCHAR_LENGTH = 64;

        public static final java.util.regex.Pattern BOOL = java.util.regex.Pattern.compile("^\\s*BOOL\\s+", java.util.regex.Pattern.CASE_INSENSITIVE);
        public static final java.util.regex.Pattern INT = java.util.regex.Pattern.compile("^\\s*INT\\s+", java.util.regex.Pattern.CASE_INSENSITIVE);
        public static final java.util.regex.Pattern LONG = java.util.regex.Pattern.compile("^\\s*LONG\\s+", java.util.regex.Pattern.CASE_INSENSITIVE);
        public static final java.util.regex.Pattern FLOAT = java.util.regex.Pattern.compile("^\\s*FLOAT\\s+", java.util.regex.Pattern.CASE_INSENSITIVE);
        public static final java.util.regex.Pattern DOUBLE = java.util.regex.Pattern.compile("^\\s*DOUBLE\\s+", java.util.regex.Pattern.CASE_INSENSITIVE);
        public static final java.util.regex.Pattern VARCHAR = java.util.regex.Pattern.compile("^\\s*VARCHAR(\\([0-9]+\\)){0,1}\\s+", java.util.regex.Pattern.CASE_INSENSITIVE);
        public static final java.util.regex.Pattern STRING = java.util.regex.Pattern.compile("^\\s*STRING\\s+", java.util.regex.Pattern.CASE_INSENSITIVE);
        public static final java.util.regex.Pattern DATETIME = java.util.regex.Pattern.compile("^\\s*DATETIME\\s+", java.util.regex.Pattern.CASE_INSENSITIVE);
        public static final java.util.regex.Pattern TIMESTAMP = java.util.regex.Pattern.compile("^\\s*TIMESTAMP\\s+", java.util.regex.Pattern.CASE_INSENSITIVE);

    }

    public final static class Func {

        public static final String SYS_FUNC_NOW = " SFDL_SYS_FUNC_01 ";
        public static final String SYS_FUNC_DATE = " SFDL_SYS_FUNC_02 ";
//        public static Set<String> FUNCS = new HashSet<String>();
        public static boolean contains(String o) {
            if (o.startsWith("SFDL_SYS_FUNC_")) {return true;}
            return false;
        }
        public static final java.util.regex.Pattern FUNC_NOW = java.util.regex.Pattern.compile("\\sNOW\\(\\)", java.util.regex.Pattern.CASE_INSENSITIVE);
        public static final java.util.regex.Pattern FUNC_DATETIME = java.util.regex.Pattern.compile("\\sDATETIME\\(\\)", java.util.regex.Pattern.CASE_INSENSITIVE);
    }

    public final static class Field {
        public static final String ROW_ID = "rowid";
        public static final String CREATE_AT = "create_at";
        public static final String MODIFY_AT = "modify_at";
        public static final String DELETED = "deleted";
    }
}
