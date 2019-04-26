package com.lmj.sfdl.compiler.executer;

import com.lmj.sfdl.compiler.po.Flow;
import com.lmj.sfdl.compiler.po.Table;
import com.lmj.sfdl.compiler.po.View;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: lingminjun
 * Date: 2019-04-24
 * Time: 10:16 PM
 */
public final class SFDLContext {

    // 记录整个区域内的输出模型，存储模型，和结构描述
    private static HashMap<String,View> views = new HashMap<String, View>();
    private static HashMap<String,Table> tables = new HashMap<String, Table>();
    private static HashMap<String,Flow> flows = new HashMap<String, Flow>();

}
