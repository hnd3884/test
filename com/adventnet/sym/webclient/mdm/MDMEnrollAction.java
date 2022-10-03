package com.adventnet.sym.webclient.mdm;

import java.util.Hashtable;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.List;
import com.adventnet.sym.server.mdm.message.MDMMessageHandler;
import com.me.mdm.server.onelinelogger.MDMOneLineLogger;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.adventnet.sym.server.mdm.core.EnrollmentRequestHandler;
import com.adventnet.sym.server.mdm.core.EREvent;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.api.EvaluatorAPI;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.mdm.server.android.agentmigrate.AgentMigrationHandler;
import com.me.mdm.webclient.home.MDMHomePageUtils;
import com.me.mdm.server.apps.blacklist.BlacklistAppHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.webclient.common.GettingStartedUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import java.util.Properties;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import com.me.devicemanagement.framework.webclient.factory.WebclientAPIFactoryProvider;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

public class MDMEnrollAction
{
    public Logger logger;
    
    public MDMEnrollAction() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public void showHomePage(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        request.setAttribute("selectedTab", (Object)"MDM");
        WebclientAPIFactoryProvider.getSessionAPI().addToSession(request, "selectedTab", (Object)"MDM");
        final Long customerID = MSPWebClientUtil.getCustomerID(request);
        final MessageProvider msgPro = MessageProvider.getInstance();
        final Properties msgProperty = msgPro.getPropertiesList("MDM_HOME_PAGE", (Properties)null, customerID, request);
        request.setAttribute("messageProperties", (Object)msgProperty);
        final int managedCount = MDMEnrollmentRequestHandler.getInstance().getAddedEnrollmentRequestCount(new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)7, 1).or(new Criteria(Column.getColumn("DeviceEnrollmentRequest", "REQUEST_STATUS"), (Object)1, 0)).or(new Criteria(Column.getColumn("DeviceEnrollmentRequest", "REQUEST_STATUS"), (Object)0, 0)));
        this.logger.log(Level.INFO, "certificate :");
        try {
            final String closeStr = request.getParameter("close");
            final Long userID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            boolean close = false;
            close = GettingStartedUtil.isGettingStartedClosed(request, "MDM_GETTING_STARTED_CLOSE");
            request.setAttribute("rootedDeviceCount", (Object)MDMUtil.getInstance().getRootedDeviceCount(customerID));
            request.setAttribute("jailBrokenDeviceCount", (Object)MDMUtil.getInstance().getJailBrokenDeviceCount(customerID));
            final BlacklistAppHandler blacklistAppHandler = new BlacklistAppHandler();
            request.setAttribute("blacklistAppCount", (Object)MDMUtil.getInstance().getBlackListAppCount(customerID));
            MDMHomePageUtils.homePageDetails(request);
            final int safeMigrationDeviceCount = AgentMigrationHandler.getInstance().getYetToUpgradeDeviceCount();
            if (safeMigrationDeviceCount > 0) {
                request.setAttribute("migrationDeviceCount", (Object)safeMigrationDeviceCount);
            }
            final EvaluatorAPI evaluatorApi = ApiFactoryProvider.getEvaluatorAPI();
            if (managedCount > 0 || close || CustomerInfoUtil.getInstance().isMSP()) {
                if (evaluatorApi != null) {
                    evaluatorApi.addOrIncrementClickCountForTrialUsers("MDM_Module", "MDM_Home_Page");
                }
                response.sendRedirect("/webclient#/uems/mdm/home");
                return;
            }
            if (evaluatorApi != null) {
                evaluatorApi.addOrIncrementClickCountForTrialUsers("MDM_Module", "MDM_Getting_Started_Page");
            }
            request.setAttribute("loadGettingStarted", (Object)true);
            response.sendRedirect("/webclient#/uems/mdm/gettingStarted");
        }
        catch (final SyMException ex) {
            this.logger.log(Level.WARNING, "Exception while closing getting started image", (Throwable)ex);
            response.sendRedirect("/webclient#/uems/mdm/home");
        }
    }
    
    public JSONObject addEnrollmentRequest(Properties properties) {
        String enrollStatus = "2";
        final JSONObject json = new JSONObject();
        try {
            json.put("ENROLL_STATUS", (Object)enrollStatus);
            String sUserName = ((Hashtable<K, String>)properties).get("NAME");
            final String sEmailID = ((Hashtable<K, String>)properties).get("EMAIL_ADDRESS");
            final Long customerID = ((Hashtable<K, Long>)properties).get("CUSTOMER_ID");
            if (sUserName != null && !sUserName.equals("")) {
                if (!properties.containsKey("USER_ID")) {
                    ((Hashtable<String, Long>)properties).put("USER_ID", MDMUtil.getInstance().getCurrentlyLoggedOnUserID());
                }
                final EREvent erEvent = new EREvent(customerID, sUserName, sEmailID, properties);
                final String status = EnrollmentRequestHandler.getInstance().invokeEnrollmentRequestListeners(erEvent, 2);
                if (status != null && status.contains("failure")) {
                    this.logger.log(Level.INFO, "MDMEnrollAction :: addEnrollmentRequest :: status :: {0} ", status);
                    enrollStatus = "9" + status;
                    json.put("ENROLL_STATUS", (Object)enrollStatus);
                    return json;
                }
                ((Hashtable<String, Integer>)properties).put("ENROLLMENT_TYPE", 1);
                ((Hashtable<String, Boolean>)properties).put("IS_SELF_ENROLLMENT", Boolean.FALSE);
                properties = MDMEnrollmentRequestHandler.getInstance().sendEnrollmentRequest(properties);
                enrollStatus = ((Hashtable<K, Object>)properties).get("ENROLL_STATUS").toString();
                json.put("ENROLL_STATUS", (Object)enrollStatus);
                json.put("erid", (Object)((Hashtable<K, Long>)properties).get("ENROLLMENT_REQUEST_ID"));
                final String sEventLogRemarks = "dc.mdm.actionlog.enrollment.request_created";
                sUserName = MDMUtil.getInstance().decodeURIComponentEquivalent(sUserName);
                final Object remarksArgs = sEmailID + "@@@" + sUserName;
                final String sLoggedOnUserName = MDMUtil.getInstance().getCurrentlyLoggedOnUserName();
                MDMEventLogHandler.getInstance().MDMEventLogEntry(2001, null, sLoggedOnUserName, sEventLogRemarks, remarksArgs, customerID);
                final org.json.simple.JSONObject logJSON = new org.json.simple.JSONObject();
                logJSON.put((Object)"REMARKS", (Object)"create-success");
                logJSON.put((Object)"ENROLLMENT_REQUEST_ID", ((Hashtable<K, Object>)properties).get("ENROLLMENT_REQUEST_ID"));
                MDMOneLineLogger.log(Level.INFO, "CREATE_ENROLLMENT_REQUEST", logJSON);
                MDMMessageHandler.getInstance().messageAction("NO_DEVICE_ENROLLED", customerID);
            }
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
        return json;
    }
    
    public boolean validateEnrollmentRequest(final List reqID, final Long customerId) throws DataAccessException {
        boolean isValid = true;
        Criteria criteria = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)reqID.toArray(), 8);
        final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria userNotInTrashCriteria = new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1);
        criteria = criteria.and(customerCriteria).and(userNotInTrashCriteria);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        selectQuery.addJoin(new Join("Resource", "ManagedUser", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedUser", "DeviceEnrollmentRequest", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
        selectQuery.setCriteria(criteria);
        selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (dataObject.isEmpty()) {
            isValid = false;
        }
        else {
            final Iterator<Row> enrollmentRequestIds = dataObject.getRows("DeviceEnrollmentRequest");
            final List enrollementRequestList = DBUtil.getColumnValuesAsList((Iterator)enrollmentRequestIds, "ENROLLMENT_REQUEST_ID");
            if (enrollementRequestList.size() != reqID.size()) {
                isValid = false;
            }
        }
        return isValid;
    }
}
