package com.lmj.sfdl.compiler.pool;

import com.lmj.sfdl.compiler.po.*;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: lingminjun
 * Date: 2018-12-18
 * Time: 9:21 PM
 */
public final class Pool {
    private static HashMap<String,View> views = new HashMap<String, View>();
    private static HashMap<String,Table> tables = new HashMap<String, Table>();
    private static HashMap<String,Flow> flows = new HashMap<String, Flow>();

    //
    private static String getKey(String name) {
        String[] ss = name.split("\\.");
        String key = name;
        if (ss.length > 1) {//考虑后期前缀 prex.group.table ;
            key = name.substring(ss[0].length() + 1);
        }
        return key;
    }

    public static View getView(String name) {
        return getView(name,true);
    }

    public static View getView(String name, boolean create) {
        String key = getKey(name);
        View struct = views.get(key);
        if (struct != null) {
            return struct;
        }
        if (create) {
            struct = new View();
            struct.setName(name);
            views.put(key, struct);
        }
        return struct;
    }

    public static Table getTable(String name) {
        return getTable(name,true);
    }
    public static Table getTable(String name, boolean create) {
        String key = getKey(name);
        Table struct = tables.get(key);
        if (struct != null) {
            return struct;
        }
        if (create) {
            struct = new Table();
            struct.setName(name);
            tables.put(key, struct);

            //关联生成View
            getView(name).addRelyTable(name);
        }
        return struct;
    }

    public static Flow getFlow(String name) {
        String key = getKey(name);
        Flow flow = flows.get(key);
        if (flow != null) {
            return flow;
        }
        flow = new Flow();
        flow.setFunc(new Func());
        flow.getFunc().setName(name);
        flows.put(key,flow);
        return flow;
    }


    public static List<View> getViews() {
        List<View> list = new ArrayList<View>(views.values());
        Collections.sort(list, new Comparator<View>() {
            public int compare(View o1, View o2) {
                return o1.getKeyName().compareTo(o2.getKeyName());
            }
        });
        for (View view : list) {
            Table table = tables.get(view.getKeyName());
            if (table != null) {
                view.addRelyTable(view.getKeyName());
            }
        }
        for (View view : list) {
            view.autoFinish();
        }
        return list;
    }

    public static List<Table> getTables() {
        List<Table> list = new ArrayList<Table>(tables.values());
        Collections.sort(list, new Comparator<Table>() {
            public int compare(Table o1, Table o2) {
                return o1.getKeyName().compareTo(o2.getKeyName());
            }
        });
        for (Table table : list) {
            table.autoFinish();
        }
        return list;
    }

    public static List<Flow> getFlows() {
        List<Flow> list = new ArrayList<Flow>(flows.values());
        Collections.sort(list, new Comparator<Flow>() {
            public int compare(Flow o1, Flow o2) {
                return o1.getKeyName().compareTo(o2.getKeyName());
            }
        });
        return list;
    }
}
