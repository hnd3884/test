package com.adventnet.db.adapter;

import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.Join;
import java.sql.SQLException;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.ds.query.BulkLoad;
import java.util.Properties;
import com.adventnet.ds.query.CreateTableLike;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.ds.query.Table;
import com.adventnet.db.persistence.metadata.IndexDefinition;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.ds.query.Query;
import java.util.List;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.AlterTableQuery;
import com.adventnet.ds.query.QueryConstructionException;
import java.util.Map;

public interface SQLGenerator
{
    String getSQLForInsert(final String p0, final Map p1) throws QueryConstructionException;
    
    String getSQLForAlterTable(final AlterTableQuery p0) throws QueryConstructionException;
    
    String getSQLForDelete(final String p0, final Criteria p1) throws QueryConstructionException;
    
    String getSQLForDelete(final DeleteQuery p0) throws QueryConstructionException;
    
    String getSQLForUpdate(final String p0, final Map p1, final Criteria p2) throws QueryConstructionException;
    
    String getSQLForUpdate(final String p0, final Map p1, final Criteria p2, final List p3) throws QueryConstructionException;
    
    String getSQLForUpdate(final List p0, final Map p1, final Criteria p2, final List p3) throws QueryConstructionException;
    
    String getSQLForSelect(final Query p0) throws QueryConstructionException;
    
    String getSQLForDrop(final String p0, final boolean p1) throws QueryConstructionException;
    
    String getSQLForTruncate(final String p0) throws QueryConstructionException;
    
    String getSQLForDrop(final String p0, final boolean p1, final boolean p2) throws QueryConstructionException;
    
    String getSQLForLock(final List p0) throws QueryConstructionException;
    
    String getSQLForLock(final String p0) throws QueryConstructionException;
    
    String getSQLForCreateTable(final TableDefinition p0) throws QueryConstructionException;
    
    String getSQLForCreateTable(final TableDefinition p0, final String p1) throws QueryConstructionException;
    
    String getDBSpecificColumnName(final String p0);
    
    String getDBSpecificTableName(final String p0);
    
    String getSQLForIndex(final String p0, final IndexDefinition p1) throws QueryConstructionException;
    
    String getSQLForDropIndex(final String p0, final String p1) throws QueryConstructionException;
    
    void setKey(final String p0);
    
    String getKey();
    
    String formGroupByString(final String[] p0, final Criteria p1) throws QueryConstructionException;
    
    String formOrderByString(final String[] p0, final boolean[] p1) throws QueryConstructionException;
    
    String formWhereClause(final Criteria p0) throws QueryConstructionException;
    
    String formJoinString(final List p0, final List<Table> p1) throws QueryConstructionException;
    
    String getGroupByClause(final GroupByClause p0, final List<Table> p1) throws QueryConstructionException;
    
    String formSelectClause(final List p0) throws QueryConstructionException;
    
    void fillUserDataRange(final Map p0) throws QueryConstructionException;
    
    List<String> getUpdateSQLForModifyColumnDataEncryption(final AlterTableQuery p0) throws QueryConstructionException;
    
    String getDBDataType(final ColumnDefinition p0) throws QueryConstructionException;
    
    String getSQLForCreateArchiveTable(final CreateTableLike p0, final String p1, final boolean p2) throws QueryConstructionException;
    
    String getIndexName(final String p0);
    
    void setFunctionTemplates(final Properties p0) throws Exception;
    
    String getBulkSql(final BulkLoad p0, final BulkInsertObject p1) throws MetaDataException, SQLException, QueryConstructionException;
    
    String formBulkUpdateSql(final BulkLoad p0) throws MetaDataException;
    
    String createTempTableSQL(final String p0);
    
    String insertSQLForTemp(final String p0);
    
    String getSQLForDelete(final String p0, final Join p1, final Criteria p2) throws QueryConstructionException;
    
    String formCountSQL(final String p0, final boolean p1) throws Exception;
    
    String getSchemaQuery();
    
    String getSQLForBatchInsert(final String p0) throws MetaDataException, QueryConstructionException;
    
    String getSQLForBatchUpdate(final String p0, final int[] p1) throws MetaDataException, QueryConstructionException;
    
    String getSQLForBatchUpdate(final UpdateQuery p0) throws MetaDataException, QueryConstructionException;
    
    String escapeSpecialCharacters(final String p0, final int p1);
    
    String getSQLWithHiddenEncryptionKey(final String p0);
    
    void initDCSQLGenerators();
    
    String getDBSpecificEncryptionString(final Column p0, final String p1);
    
    String getDBSpecificDecryptionString(final Column p0, final String p1);
    
    String getDBType();
    
    String getSQLForCount(final String p0, final Criteria p1) throws QueryConstructionException;
    
    DCSQLGenerator getDCSQLGeneratorForTable(final String p0);
    
    DCSQLGenerator getDCSQLGenerator(final String p0);
    
    String getDecryptSQL(final String p0, final String p1);
    
    String processDeleteSQLString(final String p0) throws QueryConstructionException;
    
    Map postamble(final List<String> p0) throws QueryConstructionException;
}
