package com.me.idps.core.util;

import java.util.Hashtable;
import java.util.Properties;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.DBUtil;
import org.json.simple.JSONObject;

public class DirectoryDiffDetailsHandler
{
    private static DirectoryDiffDetailsHandler directoryDiffDetailsHandler;
    
    public static DirectoryDiffDetailsHandler getInstance() {
        if (DirectoryDiffDetailsHandler.directoryDiffDetailsHandler == null) {
            DirectoryDiffDetailsHandler.directoryDiffDetailsHandler = new DirectoryDiffDetailsHandler();
        }
        return DirectoryDiffDetailsHandler.directoryDiffDetailsHandler;
    }
    
    public JSONObject getDirectorySyncDiffState(final Long domainID) throws Exception {
        final JSONObject directoryDiffDetails = new JSONObject();
        final Row directoryDiffDetailsRow = DBUtil.getRowFromDB("DirectoryDiffDetails", "DM_DOMAIN_ID", (Object)domainID);
        directoryDiffDetails.put((Object)"ATTRIBUTE_1", DirectoryUtil.getInstance().extractValue(directoryDiffDetailsRow, "ATTRIBUTE_1", ""));
        directoryDiffDetails.put((Object)"ATTRIBUTE_2", DirectoryUtil.getInstance().extractValue(directoryDiffDetailsRow, "ATTRIBUTE_2", ""));
        directoryDiffDetails.put((Object)"ATTRIBUTE_3", DirectoryUtil.getInstance().extractValue(directoryDiffDetailsRow, "ATTRIBUTE_3", ""));
        return directoryDiffDetails;
    }
    
    public void addOrUpdateDirectorySyncDiffState(final Long domainID, final String deltaLink) throws DataAccessException {
        final DataObject dObj = SyMUtil.getPersistenceLite().get("DirectoryDiffDetails", new Criteria(new Column("DirectoryDiffDetails", "DM_DOMAIN_ID"), (Object)domainID, 0));
        if (dObj.isEmpty()) {
            final Row directorySyncStateRow = new Row("DirectoryDiffDetails");
            directorySyncStateRow.set("DM_DOMAIN_ID", (Object)domainID);
            directorySyncStateRow.set("ATTRIBUTE_3", (Object)deltaLink);
            directorySyncStateRow.set("ADDED_AT", (Object)System.currentTimeMillis());
            dObj.addRow(directorySyncStateRow);
            SyMUtil.getPersistenceLite().add(dObj);
        }
        else {
            final Row directorySyncStateRow = dObj.getFirstRow("DirectoryDiffDetails");
            directorySyncStateRow.set("ATTRIBUTE_3", (Object)deltaLink);
            directorySyncStateRow.set("ADDED_AT", (Object)System.currentTimeMillis());
            dObj.updateRow(directorySyncStateRow);
            SyMUtil.getPersistenceLite().update(dObj);
        }
    }
    
    public void addOrUpdateDirectorySyncDiffState(final Properties dmDomainProps, final String deltaLink) throws DataAccessException {
        this.addOrUpdateDirectorySyncDiffState(((Hashtable<K, Long>)dmDomainProps).get("DOMAIN_ID"), deltaLink);
    }
    
    static {
        DirectoryDiffDetailsHandler.directoryDiffDetailsHandler = null;
    }
}
