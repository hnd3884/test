package com.me.mdm.server.migration;

import com.me.mdm.server.windows.profile.payload.WindowsConfigurationPayload;
import com.me.mdm.server.windows.profile.payload.WindowsPayloadHandler;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;
import java.util.regex.Matcher;
import java.util.logging.Logger;

public abstract class MigrationFileRegeneration
{
    public static final Logger LOGGER;
    
    public abstract String generateFile(final Matcher p0) throws Exception;
    
    public DataObject getProfileDetailsQuery(final Long collectionId) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
        selectQuery.addJoin(new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PLATFORM_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_PAYLOAD_IDENTIFIER"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("ProfileToCollection", "COLLECTION_ID"), (Object)collectionId, 0));
        selectQuery.addJoin(new Join("ProfileToCollection", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 1));
        selectQuery.addJoin(new Join("MdAppToCollection", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 1));
        selectQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppDetails", "IDENTIFIER"));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        return dataObject;
    }
    
    public String getWindowsAppPayloads(final Long collectionId, final String commandName) {
        String payloadData = null;
        final WindowsPayloadHandler payloadHandler = WindowsPayloadHandler.getInstance();
        final WindowsConfigurationPayload[] payloads = payloadHandler.generateAppPayloads(collectionId);
        for (int i = 0; i < payloads.length; ++i) {
            final WindowsConfigurationPayload cfgPayload = payloads[i];
            if (cfgPayload != null && cfgPayload.getConfigurationPayloadType().equalsIgnoreCase(commandName)) {
                final String commandUUID = commandName + ";" + "Collection=" + collectionId.toString();
                cfgPayload.setCommandUUID(commandUUID);
                payloadData = cfgPayload.toString();
            }
        }
        return payloadData;
    }
    
    static {
        LOGGER = Logger.getLogger(MigrationFileRegeneration.class.getName());
    }
}
