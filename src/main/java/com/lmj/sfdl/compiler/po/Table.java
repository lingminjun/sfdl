package com.lmj.sfdl.compiler.po;

import com.lmj.sfdl.compiler.consts.Consts;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: lingminjun
 * Date: 2018-12-30
 * Time: 10:09 PM
 */
public final class Table extends Struct {
    public StructType getType() {
        return StructType.TABLE;
    }

    public boolean hasAutoIncrementPrimary() {
        for (Field fld : fields) {
            if (fld.isAutoIncrement() && fld.getType() == Type.LONG) {
                fld.setPrimary(true);
                return true;
            }
        }
        return false;
    }

    //自动完成
    public void autoFinish() {
        if (!hasAutoIncrementPrimary()) {
            this.getField(Consts.Field.ROW_ID,true);
        }
        this.getField(Consts.Field.CREATE_AT,true);
        this.getField(Consts.Field.MODIFY_AT,true);
        this.getField(Consts.Field.DELETED,true);
    }
}
