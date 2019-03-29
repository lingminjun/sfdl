package com.lmj.sfdl.main;

import com.lmj.sfdl.compiler.parser.Syntax;
import com.lmj.sfdl.compiler.po.*;
import com.lmj.sfdl.compiler.pool.Pool;

/**
 * Created with IntelliJ IDEA.
 * Description: 解析引擎
 * User: lingminjun
 * Date: 2018-12-10
 * Time: 下午10:06
 */
public final class Main {

    private static String sfdl = "#检查下次注释是否ok\n" +
            "//检查第二种注释是否ok\n" +
            "view `ss.token` as func `mobileSMSLogin` in `account`,`user` (\n" +
            "    in platform varchar(32) from 'mobile',\n" +
            "    in mobile string not null throw '手机号输入异常' where throw -101 '验证码未找到' mobile = captcha.mobile\n" +
            "                                                        and mobile MATCH func `matchMobileFormat` throw -103 '手机号格式不正确',\n" +
            "    in smscode string not null throw '验证码输入异常' where  smscode = captcha.code\n" +
            "                                                        and captcha.type = 'auth' \n" +
            "                                                        and mobile MATCH PATTERN 'xxxxxxxx' \n" +
            "                                                        and (captcha.created_at + 300) >= now() throw -102 '验证码超时' \n" +
            "                                                        and now() <= (captcha.created_at + 300) throw -102 '验证码超时' \n" +
            "                                                        and captcha.created_at order desc,\n" +
            "    in nick string default null , # 测试下注释在某个语句结尾\n" +
            "    in pswd string default null ,\n" +
            "    out acct from account.id,\n" +
            "    out uid from user.id,\n" +
            // 显然此处是一个特殊的插入符号，in的是某个字段，而不是整个表, 此处解析如何标识出来
            "    in account.uid where account.uid is null,\n" +
            "    out tk,key,refresh,expire from func `createToken` (account.id, user.id)\n" +
            ");\n" +
//            "\n" +
//            "//查询商品\n" +
//            "view product out spu (\n" +
//            "    in spuid not empty where spu.id = spuid and sku.spuid = spuid,\n" +
//            "    in skuid,\n" +
//            "    out sel_sku from view sku where sku.id = skuid and sku.spuid = spuid and sku.stock - 5 > 0,\n" +
//            "    out skus from view sku where sku.spuid = spuid,\n" +
//            ") where spu.delete = 0 ;\n" +
//            "\n" +
//            "table account (\n" +
//            "type not empty,\n" +
//            "openid not empty,\n" +
//            "nick,\n" +
//            "mobile,\n" +
//            "email,\n" +
//            "uid\n" +
//            ");\n" +
//            "\n" +
//            "table user (\n" +
//            "nick,\n" +
//            "mobile,\n" +
//            "email\n" +
//            ");\n" +
//            "\n" +
//            "\n" +
//            "table sku (\n" +
//            "spuid not empty,\n" +
//            "name not empty,\n" +
//            "color,\n" +
//            "size,\n" +
//            "image,\n" +
//            ");\n" +
//            "\n" +
//            "\n" +
//            "table spu (\n" +
//            "name not empty,\n" +
//            "image,\n" +
//            "barcode unique\n" +
//            ");\n" +
            "";

//    private static boolean startPattern(Pattern pattern, CharSequence str) {
//        Matcher m = Consts.Pattern.CREATE_TABLE.matcher(builder);
//        if (m.find() && m.start() == 0) {
//
//        }
//    }


    public static void main(String[] args) throws InterruptedException {

        Syntax.codeCompiling(sfdl);

        System.out.println("编译完，开始整理数据");

        System.out.println("得到的table");
        for (Table table : Pool.getTables()) {
            System.out.println(table.toDescription());
        }

        System.out.println("得到的view");
        for (View view : Pool.getViews()) {
            System.out.println(view.toDescription());
        }

        System.out.println("得到的Flow");
        for (Flow flow : Pool.getFlows()) {
            System.out.println(flow.toDescription());
        }

    }
}
