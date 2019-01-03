package com.centit.support.metadata.dao;

import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.support.metadata.po.MetaTable;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class MetaTableDao extends BaseDaoImpl<MetaTable, String> {
    @Override
    public Map<String, String> getFilterField() {
        return null;
    }

    public void updateMetaTable(String tableName, String tableLabelName, String tableComment) {
        String sql = "update F_META_TABLE set TABLE_LABEL_NAME = ?, TABLE_COMMENT = ? where TABLE_NAME = ?";
        DatabaseOptUtils.doExecuteSql(this, sql, new Object[]{tableLabelName, tableComment, tableName});
    }
}
