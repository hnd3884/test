package com.me.mdm.server.profiles.mac.configNotApplicableHandler;

import java.util.Set;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.HashSet;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import java.util.logging.Level;
import java.util.Collection;
import java.util.List;
import com.me.mdm.server.profiles.MDMConfigNotApplicable;
import java.util.logging.Logger;
import com.me.mdm.server.profiles.MDMConfigNotApplicableListener;

public class MacFileVaultProfileAlreadyExistsNotApplicableHandler implements MDMConfigNotApplicableListener
{
    private static final Logger LOGGER;
    
    @Override
    public List<Long> getNotApplicableDeviceList(final MDMConfigNotApplicable configNotApplicable) {
        final List resourceList = configNotApplicable.resourceList;
        final Long collnID = configNotApplicable.collectionId;
        final List<Long> notApplicableList = getFileVaultEnabledDevices(resourceList);
        final List<Long> devciewithThiscollectionAlready = getNAListExludingCurrentCollection(notApplicableList, collnID);
        notApplicableList.removeAll(devciewithThiscollectionAlready);
        MacFileVaultProfileAlreadyExistsNotApplicableHandler.LOGGER.log(Level.INFO, "FileVaultLog: FileVault not applicable in these devices as Profile is already distributed :{0}", notApplicableList);
        return notApplicableList;
    }
    
    @Override
    public void setNotApplicableStatus(final List resourceIDList, final Long collectionID) {
        try {
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceIDList, collectionID, 8, "mdm.profile.filevault_already_exists");
        }
        catch (final Exception ex) {
            MacFileVaultProfileAlreadyExistsNotApplicableHandler.LOGGER.log(Level.SEVERE, "FileVaultLog: Exception in MacFileVaultProfileAlreadyExistsNotApplicableHandler setNotApplicableStatus : ", ex);
        }
    }
    
    public static List<Long> getFileVaultEnabledDevices(final List<Long> resourceIDList) {
        List<Long> notApplicableList = new ArrayList<Long>();
        final SelectQuery fvSq = (SelectQuery)new SelectQueryImpl(Table.getTable("ResourceToConfigProfiles"));
        fvSq.addJoin(new Join("ResourceToConfigProfiles", "IOSConfigProfilePayloads", new String[] { "PROFILE_PAYLOAD_ID" }, new String[] { "PROFILE_PAYLOAD_ID" }, 2));
        fvSq.addJoin(new Join("IOSConfigProfilePayloads", "IOSConfigPayload", new String[] { "PAYLOAD_PAYLOAD_ID" }, new String[] { "PAYLOAD_ID" }, 2));
        final Criteria resIDCri = new Criteria(new Column("ResourceToConfigProfiles", "RESOURCE_ID"), (Object)resourceIDList.toArray(), 8);
        final Criteria payloadTypeCri = new Criteria(new Column("IOSConfigPayload", "PAYLOAD_TYPE"), (Object)new String[] { "com.apple.security.FDERecoveryKeyEscrow", "com.apple.MCX.FileVault2" }, 8);
        fvSq.setCriteria(resIDCri.and(payloadTypeCri));
        fvSq.addSelectColumn(new Column("ResourceToConfigProfiles", "RESOURCE_ID"));
        fvSq.addSelectColumn(new Column("ResourceToConfigProfiles", "PROFILE_PAYLOAD_ID"));
        fvSq.addSelectColumn(new Column("ResourceToConfigProfiles", "INSTALLED_SOURCE"));
        DataObject dO = null;
        try {
            dO = MDMUtil.getPersistence().get(fvSq);
            if (!dO.isEmpty()) {
                notApplicableList = DBUtil.getColumnValuesAsList(dO.getRows("ResourceToConfigProfiles"), "RESOURCE_ID");
            }
        }
        catch (final Exception e) {
            MacFileVaultProfileAlreadyExistsNotApplicableHandler.LOGGER.log(Level.SEVERE, "FileVaultLog: Exception in MacFileVaultProfileAlreadyExistsNotApplicableHandler getFileVaultEnabledDevices : ", e);
        }
        final Set<Long> removedduplicatedEnries = new HashSet<Long>(notApplicableList);
        return new ArrayList<Long>(removedduplicatedEnries);
    }
    
    public static List<Long> getNAListExludingCurrentCollection(final List<Long> resourceList, final Long collnID) {
        List devciewithThiscollectionAlready = new ArrayList();
        final SelectQuery fvSq = (SelectQuery)new SelectQueryImpl(Table.getTable("ResourceToConfigProfiles"));
        fvSq.addJoin(new Join("ResourceToConfigProfiles", "IOSConfigPayload", new String[] { "PROFILE_PAYLOAD_ID" }, new String[] { "PAYLOAD_ID" }, 2));
        fvSq.addJoin(new Join("IOSConfigPayload", "IOSConfigProfile", new String[] { "PAYLOAD_ID" }, new String[] { "PAYLOAD_ID" }, 2));
        fvSq.addJoin(new Join("IOSConfigPayload", "Profile", new String[] { "PAYLOAD_IDENTIFIER" }, new String[] { "PROFILE_PAYLOAD_IDENTIFIER" }, 2));
        fvSq.addJoin(new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        final Criteria resIDCri = new Criteria(new Column("ResourceToConfigProfiles", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
        final Criteria collectionIDCri = new Criteria(new Column("ProfileToCollection", "COLLECTION_ID"), (Object)collnID, 0);
        fvSq.setCriteria(resIDCri.and(collectionIDCri));
        fvSq.addSelectColumn(new Column("ResourceToConfigProfiles", "RESOURCE_ID"));
        fvSq.addSelectColumn(new Column("ResourceToConfigProfiles", "PROFILE_PAYLOAD_ID"));
        fvSq.addSelectColumn(new Column("ResourceToConfigProfiles", "INSTALLED_SOURCE"));
        fvSq.addSelectColumn(new Column("Profile", "PROFILE_ID"));
        DataObject dO = null;
        try {
            dO = MDMUtil.getPersistence().get(fvSq);
            if (!dO.isEmpty()) {
                devciewithThiscollectionAlready = DBUtil.getColumnValuesAsList(dO.getRows("ResourceToConfigProfiles"), "RESOURCE_ID");
            }
        }
        catch (final Exception e) {
            MacFileVaultProfileAlreadyExistsNotApplicableHandler.LOGGER.log(Level.SEVERE, "FileVaultLog: Exception in MacFileVaultProfileAlreadyExistsNotApplicableHandler getNAListExludingCurrentCollection : ", e);
        }
        final Set<Long> removedduplicatedEnries = new HashSet<Long>(devciewithThiscollectionAlready);
        return new ArrayList<Long>(removedduplicatedEnries);
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
