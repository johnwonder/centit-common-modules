package com.centit.support.metadata.service;

import com.centit.framework.core.dao.PageQueryResult;
import com.centit.framework.ip.po.DatabaseInfo;
import com.centit.support.database.metadata.SimpleTableInfo;
import com.centit.support.database.utils.PageDesc;
import com.centit.support.metadata.po.MetaColumn;
import com.centit.support.metadata.po.MetaRelation;
import com.centit.support.metadata.po.MetaTable;

import javax.persistence.Column;
import java.util.List;

public interface MetaDataService {

    List<MetaTable> listMetaTables(String databaseCode, PageDesc pageDes);

    List<DatabaseInfo> listDatabase();

    List<SimpleTableInfo> listRealTables(String databaseCode);

    void syncTable(String databaseCode, String tableId);

    void syncDb(String databaseCode);

    void updateMetaTable(String tableName, String tableLabelName, String tableComment);

    MetaTable getMetaTable(String databaseCode, String tableName);

    List<MetaRelation> listMetaRelation(PageDesc pageDesc);

    List<MetaColumn> listMetaColumns(String databaseCode, String tableName, PageDesc pageDesc);

    void createRelation(MetaRelation relation);

    MetaColumn getMetaColumn(String tableId, String columnName);
}
