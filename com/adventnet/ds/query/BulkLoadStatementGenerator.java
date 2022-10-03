package com.adventnet.ds.query;

import com.zoho.conf.tree.ConfTreeBuilder;
import java.io.File;
import com.zoho.conf.Configuration;
import java.sql.SQLException;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.migration.handler.NonMickeyTableHandlerUtil;
import com.adventnet.db.adapter.SQLGenerator;
import com.adventnet.db.adapter.BulkInsertObject;
import java.util.logging.Logger;
import com.zoho.conf.tree.ConfTree;

public class BulkLoadStatementGenerator
{
    private static ConfTree confTree;
    private static final Logger LOGGER;
    static String confFilePath;
    
    public static String getBulkSQL(final BulkLoad bulk, final BulkInsertObject bio, final SQLGenerator sqlGen) throws QueryConstructionException, MetaDataException, SQLException {
        final String bulkSQL = NonMickeyTableHandlerUtil.getSQLFor(BulkLoadStatementGenerator.confTree, "bulk.insert", bulk.getTableName(), bulk.geDBName());
        if (bulkSQL != null) {
            return bulkSQL;
        }
        return sqlGen.getBulkSql(bulk, bio);
    }
    
    public static String getBulkUpdateSQL(final BulkLoad bulk, final BulkInsertObject bio, final SQLGenerator sqlGen) throws QueryConstructionException, MetaDataException, SQLException {
        final String bulkUpdateSQL = NonMickeyTableHandlerUtil.getSQLFor(BulkLoadStatementGenerator.confTree, "bulk.update", bulk.getTableName(), bulk.geDBName());
        if (bulkUpdateSQL != null) {
            return bulkUpdateSQL;
        }
        return sqlGen.formBulkUpdateSql(bulk);
    }
    
    static {
        BulkLoadStatementGenerator.confTree = null;
        LOGGER = Logger.getLogger(BulkLoadStatementGenerator.class.getName());
        BulkLoadStatementGenerator.confFilePath = Configuration.getString("server.home") + File.separator + "conf" + File.separator + "bulk_statement.conf";
        try {
            final File nonMickeyConf = new File(BulkLoadStatementGenerator.confFilePath);
            if (!nonMickeyConf.exists()) {
                BulkLoadStatementGenerator.LOGGER.info("bulk_statement.conf file not found...");
            }
            else {
                BulkLoadStatementGenerator.confTree = ((ConfTreeBuilder)ConfTreeBuilder.confTree().fromConfFile(BulkLoadStatementGenerator.confFilePath)).build();
                BulkLoadStatementGenerator.LOGGER.info("bulk_statement.conf file is initialized");
            }
        }
        catch (final Exception e) {
            BulkLoadStatementGenerator.LOGGER.severe("Exception occurred while parsing bulk_statement.conf" + e.getMessage());
            e.printStackTrace();
        }
    }
}
