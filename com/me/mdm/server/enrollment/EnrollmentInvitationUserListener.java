package com.me.mdm.server.enrollment;

import java.util.Hashtable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import java.util.Properties;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.core.UserEvent;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.core.ManagedUserListener;

public class EnrollmentInvitationUserListener implements ManagedUserListener
{
    public static Logger logger;
    
    @Override
    public void userAdded(final UserEvent userEvent) {
    }
    
    @Override
    public void userDeleted(final UserEvent userEvent) {
    }
    
    @Override
    public void userDetailsModified(final UserEvent userEvent) {
        try {
            if (isEmailAddressModified(userEvent.additionalDetails)) {
                final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedUser"));
                sQuery.addJoin(new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 2));
                sQuery.addJoin(new Join("ManagedUser", "DeviceEnrollmentRequest", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
                sQuery.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
                sQuery.addJoin(new Join("EnrollmentRequestToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 1));
                final Criteria userCriteria = new Criteria(Column.getColumn("ManagedUser", "MANAGED_USER_ID"), (Object)userEvent.resourceID, 0);
                final Criteria reqCriteria = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_TYPE"), (Object)1, 0).and(new Criteria(Column.getColumn("DeviceEnrollmentRequest", "REQUEST_STATUS"), (Object)new Integer[] { 1, 0 }, 8));
                final Criteria notUnmanagedCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)null, 0).or(new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)4, 1));
                sQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
                sQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
                sQuery.addSelectColumn(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"));
                sQuery.setCriteria(userCriteria.and(reqCriteria).and(notUnmanagedCriteria));
                final DataObject dobj = MDMUtil.getPersistence().get(sQuery);
                if (!dobj.isEmpty()) {
                    final Row resourceRow = dobj.getFirstRow("Resource");
                    final Iterator<Row> iter = dobj.getRows("DeviceEnrollmentRequest");
                    while (iter.hasNext()) {
                        final Row row = iter.next();
                        final Properties properties = new Properties();
                        ((Hashtable<String, Object>)properties).put("ENROLLMENT_REQUEST_ID", row.get("ENROLLMENT_REQUEST_ID"));
                        ((Hashtable<String, Long>)properties).put("USER_ID", userEvent.userID);
                        ((Hashtable<String, Integer>)properties).put("AUTH_MODE", EnrollmentSettingsHandler.getInstance().getAuthMode((Long)row.get("ENROLLMENT_REQUEST_ID")));
                        ((Hashtable<String, Boolean>)properties).put("regenerateDeviceToken", true);
                        MDMEnrollmentRequestHandler.getInstance().resendEnrollmentRequest(properties);
                    }
                }
            }
        }
        catch (final Exception ex) {
            EnrollmentInvitationUserListener.logger.log(Level.SEVERE, "Exeception in enrollment invitation Listener during user Details modification ", ex);
        }
    }
    
    @Override
    public void userTrashed(final UserEvent userEvent) {
    }
    
    public static boolean isEmailAddressModified(final JSONObject additionalDetails) {
        if (additionalDetails != null && additionalDetails.has("MODIFIED_FIELDS")) {
            try {
                final JSONArray jsonArray = additionalDetails.getJSONArray("MODIFIED_FIELDS");
                for (int i = 0; i < jsonArray.length(); ++i) {
                    final String field = String.valueOf(jsonArray.get(i));
                    if (field.equalsIgnoreCase("EMAIL_ADDRESS")) {
                        return true;
                    }
                }
            }
            catch (final JSONException ex) {
                Logger.getLogger(EnrollmentInvitationUserListener.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
            }
        }
        return false;
    }
    
    static {
        EnrollmentInvitationUserListener.logger = Logger.getLogger("MDMEnrollment");
    }
}
