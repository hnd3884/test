package com.me.devicemanagement.onpremise.tools.backuprestore.util;

import com.adventnet.mfw.bean.BeanUtil;
import com.adventnet.persistence.Persistence;
import com.adventnet.ds.query.UpdateQuery;
import java.util.Iterator;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.UpdateQueryImpl;
import java.util.LinkedHashMap;
import java.util.Map;
import java.io.File;

public class DCNativeSQLUtility
{
    private static DCNativeSQLUtility objDCNativeSQLUtility;
    
    public static DCNativeSQLUtility getInstance() {
        if (DCNativeSQLUtility.objDCNativeSQLUtility == null) {
            DCNativeSQLUtility.objDCNativeSQLUtility = new DCNativeSQLUtility();
        }
        return DCNativeSQLUtility.objDCNativeSQLUtility;
    }
    
    public void updateDCNativeSQLBasedOnConfigDB() throws Exception {
        final DCNativeSQLHandler objDCNativeSQLHandler = new DCNativeSQLHandler();
        final String sDCNativeSQLConfFilePath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "DesktopCentral" + File.separator + "sql" + File.separator + "NativeSQLStringDC.xml";
        final String sMDMNativeSQLConfFilePath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "MDM" + File.separator + "sql" + File.separator + "NativeSQLStringMDM.xml";
        File file = new File(sDCNativeSQLConfFilePath);
        final Map queryId_SQL_Map = objDCNativeSQLHandler.parse(file.toURL());
        file = new File(sMDMNativeSQLConfFilePath);
        queryId_SQL_Map.putAll(objDCNativeSQLHandler.parse(file.toURL()));
        this.updateDCNativeSQL(queryId_SQL_Map);
    }
    
    private void updateDCNativeSQL(final Map queryId_SQL_Map) throws Exception {
        final Iterator sqlIDKeySet = queryId_SQL_Map.keySet().iterator();
        while (sqlIDKeySet.hasNext()) {
            final String sqlMap = String.valueOf(sqlIDKeySet.next());
            if (!sqlMap.equalsIgnoreCase("stopPro")) {
                final LinkedHashMap detailsMap = queryId_SQL_Map.get(sqlMap);
                final String sqlID = String.valueOf(detailsMap.get("sql_id"));
                final String sql = String.valueOf(detailsMap.get("sql_command"));
                final String remarks = String.valueOf(detailsMap.get("sql_remarks"));
                final String sql_remarks = (remarks == null || remarks.equalsIgnoreCase("null")) ? "--" : remarks;
                final UpdateQuery query = (UpdateQuery)new UpdateQueryImpl("DCNativeSQLString");
                query.setUpdateColumn("SQL_COMMAND", (Object)sql);
                query.setUpdateColumn("SQL_REMARKS", (Object)sql_remarks);
                final Criteria sqlIDCriteria = new Criteria(Column.getColumn("DCNativeSQLString", "SQL_ID"), (Object)sqlID, 0);
                query.setCriteria(sqlIDCriteria);
                this.getPersistence().update(query);
            }
        }
    }
    
    private Persistence getPersistence() {
        Persistence pers = null;
        try {
            pers = (Persistence)BeanUtil.lookup("Persistence");
            return pers;
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            return pers;
        }
    }
    
    static {
        DCNativeSQLUtility.objDCNativeSQLUtility = null;
    }
}
