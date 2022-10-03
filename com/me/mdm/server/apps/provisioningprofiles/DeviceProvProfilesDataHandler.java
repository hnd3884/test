package com.me.mdm.server.apps.provisioningprofiles;

import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONObject;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONArray;

public class DeviceProvProfilesDataHandler
{
    public void clearAndUpdateInstalledProfiles(final Long resID, final JSONArray profilesJsonArray) throws Exception {
        try {
            MDMUtil.getUserTransaction().begin();
            this.clearInstalledProfiles(resID);
            this.setInstalledProfiles(resID, profilesJsonArray);
            MDMUtil.getUserTransaction().commit();
        }
        catch (final Exception e) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, "Error while clearAndUpdateInstalledProfiles() ", e);
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final Exception re) {
                Logger.getLogger("MDMLogger").log(Level.SEVERE, "Error while ROLLBACK clearAndUpdateInstalledProfiles() ", re);
            }
            throw e;
        }
    }
    
    protected void clearInstalledProfiles(final Long resource) throws Exception {
        final Criteria c = new Criteria(Column.getColumn("ResourceToProvProfile", "RESOURCE_ID"), (Object)resource, 0);
        DataAccess.delete("ResourceToProvProfile", c);
    }
    
    protected void setInstalledProfiles(final Long resource, final JSONArray profilesJsonArray) throws Exception {
        final DataObject dO = MDMUtil.getPersistence().constructDataObject();
        for (int i = 0; i < profilesJsonArray.length(); ++i) {
            final JSONObject json = profilesJsonArray.getJSONObject(i);
            final Row row = new Row("ResourceToProvProfile");
            row.set("RESOURCE_ID", (Object)resource);
            row.set("PROV_ID", json.get("PROV_ID"));
            row.set("INSTALLED_SOURCE", json.get("INSTALLED_SOURCE"));
            dO.addRow(row);
        }
        MDMUtil.getPersistence().update(dO);
    }
    
    public JSONArray getInstalledProvisioningProfiles(final Long resourceID) throws Exception {
        final JSONArray jsonArray = new JSONArray();
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("ResourceToProvProfile"));
            final Join join = new Join("ResourceToProvProfile", "AppleProvisioningProfiles", new String[] { "PROV_ID" }, new String[] { "PROV_ID" }, 2);
            sq.addJoin(join);
            final Criteria resC = new Criteria(Column.getColumn("ResourceToProvProfile", "RESOURCE_ID"), (Object)resourceID, 0);
            sq.setCriteria(resC);
            sq.addSelectColumn(Column.getColumn("ResourceToProvProfile", "RESOURCE_ID"));
            sq.addSelectColumn(Column.getColumn("ResourceToProvProfile", "PROV_ID"));
            sq.addSelectColumn(Column.getColumn("ResourceToProvProfile", "INSTALLED_SOURCE"));
            sq.addSelectColumn(Column.getColumn("AppleProvisioningProfiles", "PROV_ID"));
            sq.addSelectColumn(Column.getColumn("AppleProvisioningProfiles", "PROV_UUID"));
            sq.addSelectColumn(Column.getColumn("AppleProvisioningProfiles", "PROV_NAME"));
            sq.addSelectColumn(Column.getColumn("AppleProvisioningProfiles", "PROV_EXPIRY_DATE"));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(sq);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("AppleProvisioningProfiles");
                while (iterator.hasNext()) {
                    final Row appleProvisioningProfilesRow = iterator.next();
                    final JSONObject json = new JSONObject();
                    json.put("PROV_NAME", appleProvisioningProfilesRow.get("PROV_NAME"));
                    json.put("PROV_UUID", appleProvisioningProfilesRow.get("PROV_UUID"));
                    json.put("PROV_EXPIRY_DATE", appleProvisioningProfilesRow.get("PROV_EXPIRY_DATE"));
                    final Long provId = (Long)appleProvisioningProfilesRow.get("PROV_ID");
                    final Row resourceToProvProfileRow = dataObject.getRow("ResourceToProvProfile", new Criteria(Column.getColumn("ResourceToProvProfile", "PROV_ID"), (Object)provId, 0));
                    json.put("INSTALLED_SOURCE", resourceToProvProfileRow.get("INSTALLED_SOURCE"));
                    jsonArray.put((Object)json);
                }
            }
        }
        catch (final Exception e) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, "Error while getInstalledProvisioningProfiles() ", e);
            throw new Exception("Error while getInstalledProvisioningProfiles(). See trace. ", e);
        }
        return jsonArray;
    }
}
