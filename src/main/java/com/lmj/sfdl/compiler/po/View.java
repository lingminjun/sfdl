package com.lmj.sfdl.compiler.po;

import com.lmj.sfdl.compiler.pool.Pool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: lingminjun
 * Date: 2018-12-30
 * Time: 10:10 PM
 */
public final class View extends Struct {
    // 仅仅View使用属性
    private List<String> relyTables = new ArrayList<String>();

    public void addRelyTable(String relyStructName) {
        for (String name : relyTables) {
            if (name.equals(relyStructName)) {
                return;
            }
        }
        this.relyTables.add(relyStructName);
    }

    public List<String> getRelyTables() {
        return relyTables;
    }

    @Override
    public boolean containField(String name) {
        if (super.containField(name)) {
            return true;
        }
        // 依赖的table是否包含
        for (String table : this.relyTables) {
            Struct tb = Pool.getTable(table);
            if (tb.containField(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Field> getFields() {
        List<Field> flds = new ArrayList<Field>();
        flds.addAll(fields);
        for (String table : this.relyTables) {
            Struct tb = Pool.getTable(table);
            List<Field>  tbflds = tb.getFields();
            for (Field f : tbflds) {
                if (!flds.contains(f)) {
                    flds.add(f);
                }
            }
        }
        return flds;
    }

    @Override
    public Field getField(String name,boolean create) {
        for (Field fld : this.fields) {
            if (fld.getName().equals(name)) {
                return fld;
            }
        }

        //查找rly
        if (relyTables != null) {
            for (String table : this.relyTables) {
                Field fld = Pool.getTable(table).getField(name,false);
                if (fld != null) {//是否需要复制，保持一样的约束？？？
                    addField(fld/*.copy()*/);
                    return fld;
                }
            }
        }

        if (create) {
            Field fld = new Field(name);
            this.fields.add(fld);
            return fld;
        }
        return null;
    }

    public StructType getType() {
        return StructType.VIEW;
    }

    public void autoFinish() {

    }
}
