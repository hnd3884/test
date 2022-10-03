package com.me.mdm.server.enrollment.ios;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import org.json.JSONObject;

public class MDMProfileInstallationHandler
{
    private static MDMProfileInstallationHandler mdmProfileInstallationHandler;
    public static final int PROFILE_NOT_INSTALLED = 1;
    public static final int PROFILE_INSTALLED = 2;
    public static final int DEVICE_ENROLLED = 3;
    public static final int DEVICE_ENROLLMENT_FAILED = 4;
    public static final String REQUEST_STATUS = "RequestStatus";
    public static final String SERVER_TIME = "ServerTime";
    public static final String FQDN = "FQDN";
    public static final String PROFILE_DOWNLOAD_TIME = "ProfileDownloadTime";
    
    private MDMProfileInstallationHandler() {
    }
    
    public static MDMProfileInstallationHandler getInstance() {
        if (MDMProfileInstallationHandler.mdmProfileInstallationHandler == null) {
            MDMProfileInstallationHandler.mdmProfileInstallationHandler = new MDMProfileInstallationHandler();
        }
        return MDMProfileInstallationHandler.mdmProfileInstallationHandler;
    }
    
    private Integer getMDMEnrollmentStatus(final Long erid, final Long downloadTime) throws Exception {
        final Boolean profileInstalled = this.getProfileInstallationStatus(erid, downloadTime);
        final JSONObject json = new JSONObject();
        json.put("ENROLLMENT_REQUEST_ID", (Object)erid);
        final JSONObject enrollmentStatus = MDMEnrollmentRequestHandler.getInstance().getEnrollmentRequestStatusAndErrorCode(json);
        int statusCode = 1;
        if (enrollmentStatus == null) {
            return null;
        }
        if (profileInstalled) {
            statusCode = 2;
            if (enrollmentStatus.getInt("REQUEST_STATUS") == 3) {
                statusCode = 3;
            }
            else if (enrollmentStatus.getInt("REQUEST_STATUS") == 0 && (enrollmentStatus.getInt("ERROR_CODE") == 51201 || enrollmentStatus.getInt("ERROR_CODE") == 12133L)) {
                statusCode = 4;
            }
            return statusCode;
        }
        return statusCode;
    }
    
    public JSONObject getMDMEnrollmentStatus(final JSONObject requestJSON) throws Exception {
        final JSONObject msgRequestJSON = requestJSON.getJSONObject("MsgRequest");
        final Long erid = msgRequestJSON.getLong("ENROLLMENT_REQUEST_ID");
        final Long downloadTime = msgRequestJSON.getLong("ProfileDownloadTime");
        final JSONObject responseJSON = new JSONObject();
        final JSONObject messageResponseJSON = new JSONObject();
        try {
            final Integer enrollmentStatus = this.getMDMEnrollmentStatus(erid, downloadTime);
            if (enrollmentStatus == null) {
                responseJSON.put("Status", (Object)"Error");
                messageResponseJSON.put("ErrorMsg", (Object)"Invalid Enrollment Id");
                messageResponseJSON.put("ErrorKey", (Object)"dc.mdm.actionlog.enrollment.invalid_enrollmentId");
                messageResponseJSON.put("ErrorCode", 12001);
            }
            else {
                responseJSON.put("Status", (Object)"Acknowledged");
                messageResponseJSON.put("RequestStatus", (Object)enrollmentStatus);
            }
        }
        catch (final Exception e) {
            responseJSON.put("Status", (Object)"Error");
            messageResponseJSON.put("ErrorMsg", (Object)"Invalid Enrollment Id");
            messageResponseJSON.put("ErrorKey", (Object)"dc.mdm.actionlog.enrollment.invalid_enrollmentId");
            messageResponseJSON.put("ErrorCode", 12001);
        }
        responseJSON.put("MsgResponse", (Object)messageResponseJSON);
        return responseJSON;
    }
    
    private boolean getProfileInstallationStatus(final Long erid, final Long downloadTime) throws Exception {
        final Row r = DBUtil.getRowFromDB("IOSMobileConfigInstallStatus", "ENROLLMENT_REQUEST_ID", (Object)erid);
        if (r == null) {
            return false;
        }
        final Long lastcheckinTime = (Long)r.get("LAST_CHECKIN_TIME");
        return downloadTime < lastcheckinTime && (boolean)r.get("IS_MDM_PROFILE_INSTALLED");
    }
    
    public void addOrUpdateProfileInstallationStatus(final Long erid, final Boolean isMdmProfileInstalled) throws DataAccessException {
        final SelectQuery squery = (SelectQuery)new SelectQueryImpl(Table.getTable("IOSMobileConfigInstallStatus"));
        squery.addSelectColumn(Column.getColumn("IOSMobileConfigInstallStatus", "ENROLLMENT_REQUEST_ID"));
        squery.addSelectColumn(Column.getColumn("IOSMobileConfigInstallStatus", "IS_MDM_PROFILE_INSTALLED"));
        squery.setCriteria(new Criteria(Column.getColumn("IOSMobileConfigInstallStatus", "ENROLLMENT_REQUEST_ID"), (Object)erid, 0));
        DataObject dobj = MDMUtil.getPersistence().get(squery);
        if (dobj.isEmpty()) {
            dobj = (DataObject)new WritableDataObject();
            final Row r = new Row("IOSMobileConfigInstallStatus");
            r.set("ENROLLMENT_REQUEST_ID", (Object)erid);
            r.set("IS_MDM_PROFILE_INSTALLED", (Object)isMdmProfileInstalled);
            r.set("LAST_CHECKIN_TIME", (Object)MDMUtil.getCurrentTimeInMillis());
            dobj.addRow(r);
        }
        else {
            final Row r = dobj.getRow("IOSMobileConfigInstallStatus");
            r.set("IS_MDM_PROFILE_INSTALLED", (Object)isMdmProfileInstalled);
            r.set("LAST_CHECKIN_TIME", (Object)MDMUtil.getCurrentTimeInMillis());
            dobj.updateRow(r);
        }
        MDMUtil.getPersistence().update(dobj);
    }
    
    public void clearProfileInstallationStatus(final Long erid) throws DataAccessException {
        MDMUtil.getPersistence().delete(new Criteria(Column.getColumn("IOSMobileConfigInstallStatus", "ENROLLMENT_REQUEST_ID"), (Object)erid, 0));
    }
    
    static {
        MDMProfileInstallationHandler.mdmProfileInstallationHandler = null;
    }
}
