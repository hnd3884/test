package com.adventnet.sym.server.mdm.config;

import java.util.logging.Level;
import com.me.mdm.server.doc.DocMgmt;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import java.util.Iterator;
import java.util.List;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.Collection;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import org.json.JSONArray;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.server.doc.DocMgmtDataHandler;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.group.MDMGroupMemberEvent;
import com.adventnet.sym.server.mdm.group.MDMGroupMemberListener;

public class DocCGMemberListener implements MDMGroupMemberListener
{
    private JSONObject prepareRequest(final MDMGroupMemberEvent groupEvent) throws JSONException, DataAccessException {
        final Long groupID = groupEvent.groupID;
        final JSONArray docsAssociatedToGroups = DocMgmtDataHandler.getInstance().getDocsAssociatedToDeviceGroup(groupID);
        if (docsAssociatedToGroups == null || docsAssociatedToGroups.length() == 0) {
            return null;
        }
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)groupEvent.memberIds, 8));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_TYPE"));
        final DataObject dobj = MDMUtil.getPersistenceLite().get(selectQuery);
        final List<Long> memberUserResID = new ArrayList<Long>();
        final List<Long> memberGroupResID = new ArrayList<Long>();
        final List<Long> memberDeviceResID = new ArrayList<Long>();
        if (dobj != null && !dobj.isEmpty()) {
            final Iterator iterator = dobj.getRows("Resource");
            while (iterator != null && iterator.hasNext()) {
                final Row resRow = iterator.next();
                final Long memberResID = (Long)resRow.get("RESOURCE_ID");
                final Integer memberResType = (Integer)resRow.get("RESOURCE_TYPE");
                if (memberResID != null && memberResType != null) {
                    if (memberResType == 2) {
                        memberUserResID.add(memberResID);
                    }
                    if (memberResType == 101) {
                        memberGroupResID.add(memberResID);
                    }
                    if (memberResType != 120) {
                        continue;
                    }
                    memberDeviceResID.add(memberResID);
                }
            }
        }
        final JSONArray jsonArray = new JSONArray();
        if (docsAssociatedToGroups != null && docsAssociatedToGroups.length() > 0) {
            final DataObject dObj = SyMUtil.getPersistenceLite().get("DocumentPolicyResourceRel", new Criteria(Column.getColumn("DocumentPolicyResourceRel", "RESOURCE_ID"), (Object)groupID, 0));
            for (int i = 0; i < docsAssociatedToGroups.length(); ++i) {
                final JSONObject obj = docsAssociatedToGroups.getJSONObject(i);
                if (memberUserResID.size() > 0) {
                    obj.put("MANAGED_USER_ID", (Collection)memberUserResID);
                }
                if (memberGroupResID.size() > 0) {
                    obj.put("CUSTOMGROUP_ID", (Collection)memberGroupResID);
                }
                if (memberDeviceResID.size() > 0) {
                    obj.put("MANAGEDDEVICE_ID", (Collection)memberDeviceResID);
                }
                final Long docId = obj.getLong("DOC_ID");
                final Row row = dObj.getRow("DocumentPolicyResourceRel", new Criteria(Column.getColumn("DocumentPolicyResourceRel", "DOC_ID"), (Object)docId, 0));
                if (row != null) {
                    final Long policyId = (Long)row.get("DEPLOYMENT_POLICY_ID");
                    if (policyId != null) {
                        obj.put("DEPLOYMENT_POLICY_ID", (Object)policyId);
                    }
                }
                jsonArray.put((Object)obj);
            }
        }
        final JSONObject docDeviceAssociation = new JSONObject();
        docDeviceAssociation.put("DOC_ID", (Object)jsonArray);
        docDeviceAssociation.put("HARD_REMOVE_DOC", (Object)Boolean.FALSE);
        return docDeviceAssociation;
    }
    
    @Override
    public void groupMemberAdded(final MDMGroupMemberEvent groupEvent) {
        try {
            final JSONObject docDeviceAssociation = this.prepareRequest(groupEvent);
            if (docDeviceAssociation == null) {
                return;
            }
            docDeviceAssociation.put("ASSOCIATE", true);
            final Long[] customerIDs = { groupEvent.customerId };
            DocMgmt.getInstance().saveDocDeviceAssociation(customerIDs, docDeviceAssociation);
        }
        catch (final Exception ex) {
            DocMgmt.logger.log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void groupMemberRemoved(final MDMGroupMemberEvent groupEvent) {
        try {
            final JSONObject docDeviceDisAssociation = this.prepareRequest(groupEvent);
            if (docDeviceDisAssociation == null) {
                return;
            }
            docDeviceDisAssociation.put("ASSOCIATE", false);
            final Long[] customerIDs = { groupEvent.customerId };
            DocMgmt.getInstance().saveDocDeviceAssociation(customerIDs, docDeviceDisAssociation);
        }
        catch (final Exception ex) {
            DocMgmt.logger.log(Level.SEVERE, null, ex);
        }
    }
}
