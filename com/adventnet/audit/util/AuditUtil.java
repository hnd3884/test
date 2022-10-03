package com.adventnet.audit.util;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.audit.AuditException;
import java.util.logging.Level;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class AuditUtil
{
    private static Logger logger;
    
    public static DataObject getAuditConfiguration(final String moduleName) throws AuditException {
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("AuditConfig"));
        sq.addSelectColumn(Column.getColumn((String)null, "*"));
        sq.addJoin(new Join("AuditConfig", "AuditTableConfig", new String[] { "ID" }, new String[] { "AUDITCONFIG_ID" }, 1));
        sq.addJoin(new Join("AuditConfig", "AuditLevel", new String[] { "ID" }, new String[] { "AUDITCONFIG_ID" }, 1));
        sq.addJoin(new Join("AuditConfig", "AuditNotifyCriteria", new String[] { "ID" }, new String[] { "AUDITCONFIG_ID" }, 1));
        sq.addJoin(new Join("AuditConfig", "AuditSeverityLevel", new String[] { "ID" }, new String[] { "AUDITCONFIG_ID" }, 1));
        sq.addJoin(new Join("AuditConfig", "AuditConfigProperty", new String[] { "ID" }, new String[] { "AUDITCONFIG_ID" }, 1));
        sq.addJoin(new Join("AuditConfig", "Module", new String[] { "MODULE_ID" }, new String[] { "MODULE_ID" }, 1));
        sq.setCriteria(new Criteria(Column.getColumn("Module", "MODULENAME"), (Object)moduleName, 0));
        DataObject configDO = null;
        try {
            configDO = DataAccess.get(sq);
        }
        catch (final Exception e) {
            AuditUtil.logger.log(Level.SEVERE, "Exception while retrieving audit configuration", e);
            throw new AuditException(e.getMessage(), e);
        }
        return configDO;
    }
    
    static {
        AuditUtil.logger = Logger.getLogger(AuditUtil.class.getName());
    }
}
