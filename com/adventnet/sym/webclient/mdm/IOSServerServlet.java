package com.adventnet.sym.webclient.mdm;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.queue.DCQueue;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import com.adventnet.sym.server.mdm.queue.MDMDataQueueUtil;
import com.adventnet.sym.server.mdm.queue.QueueName;
import com.adventnet.sym.server.mdm.queue.QueueControllerHelper;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import org.json.JSONException;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Properties;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.JSONObject;
import com.me.mdm.server.seqcommands.SequentialSubCommand;
import java.util.List;
import java.util.Enumeration;
import java.util.HashMap;
import com.me.mdm.server.util.MDMSecurityLogger;
import com.me.mdm.server.enrollment.notification.EnrollmentNotificationHandler;
import com.adventnet.sym.server.mdm.command.CommandQueryGenerator;
import com.me.mdm.server.command.SeqCmdImmediateServerResponseProcessor;
import com.me.mdm.server.seqcommands.SeqCmdDBUtil;
import com.me.mdm.server.seqcommands.SeqCmdUtils;
import com.me.mdm.server.enrollment.task.InactiveDevicePolicyTask;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import com.adventnet.sym.server.mdm.command.CommandUtil;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.util.MDMiOSEntrollmentUtil;
import com.me.devicemanagement.framework.server.logger.DMSecurityLogger;
import java.util.Map;
import com.me.mdm.core.auth.APIKey;
import com.me.mdm.core.auth.MDMDeviceAPIKeyGenerator;
import com.adventnet.sym.server.mdm.PlistWrapper;
import com.me.mdm.webclient.filter.AuthenticationHandlerUtil;
import java.util.logging.Level;
import java.io.IOException;
import javax.servlet.ServletException;
import com.me.mdm.agent.handlers.DeviceRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.me.mdm.webclient.filter.DeviceAuthenticatedRequestServlet;

public class IOSServerServlet extends DeviceAuthenticatedRequestServlet
{
    public static final Logger logger;
    protected static final Logger ACCESSLOGGER;
    protected static final Logger MDM_DEVICE_DATA_LOGGER;
    protected final Logger queueLogger;
    protected String separator;
    protected String className;
    
    public IOSServerServlet() {
        this.queueLogger = Logger.getLogger("MDMQueueBriefLogger");
        this.separator = "\t";
        this.className = "IOSServerServlet";
    }
    
    public void doPut(final HttpServletRequest request, final HttpServletResponse response, final DeviceRequest deviceRequest) throws ServletException, IOException {
        this.processRequest(request, response, deviceRequest);
    }
    
    protected void processRequest(final HttpServletRequest request, final HttpServletResponse response, DeviceRequest deviceRequest) {
        IOSServerServlet.logger.log(Level.INFO, "{0} => (PUT) Received request from APPLE ", this.className);
        String commandUUID = null;
        String strData = null;
        String responseData = null;
        String strUDID = null;
        String strStatus = null;
        String strState = null;
        String strIdentifier = null;
        boolean isRedeemRequest = false;
        Long resourceID = null;
        boolean isQueueNeedToAdd = true;
        try {
            if (deviceRequest == null) {
                IOSServerServlet.logger.log(Level.WARNING, "Device Request null in {0}", IOSServerServlet.class.getName());
                deviceRequest = this.prepareDeviceRequest(request, IOSServerServlet.logger);
            }
            strData = (String)deviceRequest.deviceRequestData;
            IOSServerServlet.MDM_DEVICE_DATA_LOGGER.log(Level.INFO, "{1} => (PUT) Received data : {0}", new Object[] { strData, this.className });
            strData = AuthenticationHandlerUtil.sanitizeXML(strData);
            IOSServerServlet.logger.log(Level.INFO, "{1} => (PUT) Sanitized data : {0}", new Object[] { strData, this.className });
            final HashMap hashPlist = PlistWrapper.getInstance().getHashFromPlist(strData);
            String requestParams = "";
            final int serverVersion = MDMDeviceAPIKeyGenerator.getInstance().isClientVersion2_0(request.getServletPath()) ? APIKey.VERSION_2_0 : APIKey.VERSION_1_0;
            hashPlist.putAll(MDMDeviceAPIKeyGenerator.getInstance().fetchAPIKeyDetails(serverVersion, this.getParameterValueMap(request)));
            if (serverVersion == APIKey.VERSION_2_0) {
                requestParams = request.getQueryString();
            }
            else {
                final Enumeration enume = request.getParameterNames();
                while (enume.hasMoreElements()) {
                    final String key = enume.nextElement();
                    final String value = request.getParameter(key);
                    requestParams = requestParams + key + "=" + value;
                    if (enume.hasMoreElements()) {
                        requestParams += "&";
                    }
                }
                DMSecurityLogger.info(IOSServerServlet.logger, this.className, "processRequest", "IOS Server Servlet Parsed URL sensitive log: {0}", (Object)requestParams);
            }
            hashPlist.put("RequestURI", requestParams);
            hashPlist.put("ServletPath", request.getServletPath());
            strUDID = hashPlist.get("UDID");
            final String sEnrollmentRequestIDStr = this.addToMap(hashPlist, request, "erid", "ENROLLMENT_REQUEST_ID", Long.class);
            final Long erid = Long.parseLong(sEnrollmentRequestIDStr);
            final long customerIdFromRequestQueryParam = Long.parseLong(request.getParameter("customerId"));
            IOSServerServlet.logger.log(Level.INFO, "Customer Id obtained from request query param is : {0}", new Object[] { customerIdFromRequestQueryParam });
            final Long customerId = MDMiOSEntrollmentUtil.getInstance().optCustomerIdForErid(erid, customerIdFromRequestQueryParam);
            IOSServerServlet.logger.log(Level.INFO, "Customer Id obtained for erid is : {0}", new Object[] { customerId });
            hashPlist.put("customerId", customerId);
            hashPlist.put("CUSTOMER_ID", customerId);
            this.addToMap(hashPlist, request, "muid", "MANAGED_USER_ID", Long.class);
            this.addToMap(hashPlist, request, "isAppleConfig", "isAppleConfig", String.class);
            this.addToMap(hashPlist, request, "appleConfigId", "appleConfigId", String.class);
            strStatus = hashPlist.get("Status");
            strState = hashPlist.get("State");
            resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(strUDID);
            commandUUID = hashPlist.get("CommandUUID");
            strIdentifier = hashPlist.get("Identifier");
            if (commandUUID != null && commandUUID.contains("Enrollment")) {
                this.deleteIosScepEnrollmentOTP(erid);
                CommandUtil.getInstance().processCommand(strData, customerId, hashPlist, 100, null);
                resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(strUDID);
            }
            else if (strStatus != null && strStatus.equals("Idle")) {
                DeviceCommandRepository.getInstance().loadCommandsForDevice(strUDID, 1);
                if (resourceID != null) {
                    IOSServerServlet.ACCESSLOGGER.log(Level.INFO, "DEVICE-IN: IdleRequestReceived{0}{1}{2}{3}{4}IdleReceived{5}{6}", new Object[] { this.separator, resourceID, this.separator, strUDID, this.separator, this.separator, MDMUtil.getCurrentTimeInMillis() });
                    final List resourceList = new ArrayList();
                    resourceList.add(resourceID);
                    MDMUtil.getInstance();
                    MDMUtil.addOrupdateAgentLastContact(resourceID, new Long(System.currentTimeMillis()), null, null, new Long(0L), "");
                    new InactiveDevicePolicyTask().updateInactiveDeviceRemarksAfterContact(resourceID);
                }
                this.updateEnrollNotificationStatus(hashPlist, 1);
            }
            else if (strState != null && strState.equalsIgnoreCase("NeedsRedemption")) {
                isRedeemRequest = true;
            }
            else {
                final SequentialSubCommand sequentialSubCommand = SeqCmdUtils.getInstance().getIfSequentialCommandResponse(resourceID, commandUUID);
                if (!commandUUID.contains("DefaultWebClipsPayload") && sequentialSubCommand != null) {
                    SeqCmdDBUtil.getInstance().addorUpdateSeqcmdStatusAndQueue(resourceID, sequentialSubCommand.order, sequentialSubCommand.SequentialCommandID, 120);
                    if (sequentialSubCommand.isImmidiate) {
                        IOSServerServlet.logger.log(Level.FINE, "Immediate sequential processing for ios server servlet");
                        final SeqCmdImmediateServerResponseProcessor serverResponse = new SeqCmdImmediateServerResponseProcessor();
                        isQueueNeedToAdd = serverResponse.processSeqCmdResponse(strStatus, commandUUID, resourceID, strData, customerId, strUDID, sequentialSubCommand);
                    }
                }
                if (isQueueNeedToAdd) {
                    this.addToQueue(customerId, strUDID, strData, 100);
                }
            }
            if (isRedeemRequest) {
                IOSServerServlet.logger.log(Level.INFO, "{0}  going to call getRedmptionCodeQuery() ", this.className);
                responseData = CommandQueryGenerator.getInstance().getRedmptionCodeQuery(resourceID, strIdentifier, commandUUID);
            }
            else {
                responseData = CommandQueryGenerator.getInstance().getDeviceQuery(strUDID, resourceID, 1, hashPlist);
            }
            if (responseData == null && sEnrollmentRequestIDStr != null) {
                final JSONObject json = EnrollmentNotificationHandler.getInstance().getNotificationDetails(Long.valueOf(sEnrollmentRequestIDStr));
                json.put("RESOURCE_ID", (Object)String.valueOf(resourceID));
                this.invokePostWakeUpTask(json);
            }
            if (responseData != null) {
                response.setHeader("Content-Type", "application/x-plist");
                response.getWriter().write(responseData);
            }
            else {
                IOSServerServlet.ACCESSLOGGER.log(Level.INFO, "DEVICE-OUT: TerminatingSession{0}{1}{2}{3}{4}TerminateSession{5}{6}", new Object[] { this.separator, resourceID, this.separator, strUDID, this.separator, this.separator, MDMUtil.getCurrentTimeInMillis() });
            }
            MDMSecurityLogger.info(IOSServerServlet.MDM_DEVICE_DATA_LOGGER, "IOSServerServlet", "processRequest", "IOSServerServlet => (PUT) Response data for " + strUDID + " >> : {0}", responseData);
        }
        catch (final Exception ex) {
            IOSServerServlet.logger.log(Level.WARNING, ex, () -> this.className + " => (PUT) Exception occurred : {0}");
        }
    }
    
    private void deleteIosScepEnrollmentOTP(final Long enrollmentRequestId) throws DataAccessException {
        final Criteria enrollmentReqIdCriteria = new Criteria(Column.getColumn("MdmClientToken", "ENROLLMENT_REQUEST_ID"), (Object)enrollmentRequestId, 0);
        MDMUtil.getPersistence().delete(enrollmentReqIdCriteria);
    }
    
    private void invokePostWakeUpTask(final JSONObject json) throws JSONException, Exception {
        final Properties taskProps = new Properties();
        if (json.has("ENROLLMENT_REQUEST_ID")) {
            ((Hashtable<String, String>)taskProps).put("ENROLLMENT_REQUEST_ID", String.valueOf(json.getLong("ENROLLMENT_REQUEST_ID")));
            ((Hashtable<String, String>)taskProps).put("RESOURCE_ID", String.valueOf(json.getLong("RESOURCE_ID")));
            ((Hashtable<String, String>)taskProps).put("IS_SOURCE_TOKEN_UPDATE", String.valueOf(json.getBoolean("IS_SOURCE_TOKEN_UPDATE")));
            final HashMap taskInfoMap = new HashMap();
            taskInfoMap.put("taskName", "PostEnrollmentWakeUpTask");
            taskInfoMap.put("schedulerTime", System.currentTimeMillis());
            taskInfoMap.put("poolName", "mdmPool");
            ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.mdm.server.enrollment.task.PostEnrollmentWakeUpTask", taskInfoMap, taskProps);
        }
    }
    
    private void updateEnrollNotificationStatus(final HashMap hashPlist, final int status) {
        if (hashPlist.containsKey("ENROLLMENT_REQUEST_ID")) {
            final Long erid = hashPlist.get("ENROLLMENT_REQUEST_ID");
            final int enrollNotifStatus = EnrollmentNotificationHandler.getInstance().getNotificationStatus(erid);
            if (enrollNotifStatus == 0 || enrollNotifStatus == 2) {
                final Long mdid = ManagedDeviceHandler.getInstance().getManagedDeviceIDFromEnrollRequestID(erid);
                if (mdid != null) {
                    MDMEnrollmentRequestHandler.getInstance().updateDeviceRequestStatus(erid, 3, 1);
                }
                EnrollmentNotificationHandler.getInstance().updateNotificationStatus(erid, status);
            }
        }
    }
    
    protected String addToMap(final HashMap hashPlist, final HttpServletRequest request, final String key, final String mapKey, final Class classType) {
        final String value = request.getParameter(key);
        if (value != null) {
            if (classType.equals(Long.class)) {
                hashPlist.put(mapKey, Long.valueOf(value));
            }
            else if (classType.equals(String.class)) {
                hashPlist.put(mapKey, value);
            }
        }
        return value;
    }
    
    public void addToQueue(final Long customerId, final String strUDID, final String strData, final int queueDateType) throws Exception {
        final long postTime = MDMUtil.getCurrentTimeInMillis();
        final String qFileName = customerId + "-" + strUDID + "-" + postTime + ".txt";
        IOSServerServlet.logger.log(Level.INFO, "Command Status status update is received and going to add to the queue.");
        final DCQueueData queueData = new DCQueueData();
        queueData.fileName = qFileName;
        queueData.postTime = postTime;
        queueData.queueData = strData;
        queueData.customerID = customerId;
        final Map queueExtnTableData = new HashMap();
        queueExtnTableData.put("CUSTOMER_ID", customerId);
        queueData.queueExtnTableData = queueExtnTableData;
        queueData.queueDataType = queueDateType;
        final Long resourceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(strUDID, customerId);
        String queueName = QueueControllerHelper.getInstance().getQueueName(queueData.queueDataType, (String)queueData.queueData);
        if (resourceId != null) {
            queueExtnTableData.put("RESOURCE_ID", resourceId);
        }
        if (queueName.equals(QueueName.ASSET_DATA.getQueueName()) && resourceId != null) {
            IOSServerServlet.logger.log(Level.INFO, "IOSServerServlet: Checking whether the device id: {0} & customer id: {1} is a mac", new Object[] { resourceId, customerId });
            final boolean isMac = MDMUtil.getInstance().isMacDevice(resourceId, customerId);
            if (isMac) {
                queueName = QueueName.MODERN_MGMT_ASSET_DATA.getQueueName();
            }
        }
        this.queueLogger.log(Level.INFO, "QueueName : {0}{1}AddingToQueue{2}{3}{4}{5}{6}{7}", new Object[] { queueName, this.separator, this.separator, queueData.fileName, this.separator, MDMDataQueueUtil.getInstance().getPlatformNameForLogging(queueData.queueDataType), this.separator, String.valueOf(postTime) });
        final DCQueue queue = DCQueueHandler.getQueue(queueName);
        IOSServerServlet.MDM_DEVICE_DATA_LOGGER.log(Level.INFO, "Queue data added - FileName : {0}\t QueueDataType : {1}", new Object[] { queueData.fileName, queueData.queueDataType });
        queue.addToQueue(queueData);
    }
    
    static {
        logger = Logger.getLogger("MDMLogger");
        ACCESSLOGGER = Logger.getLogger("MDMCommandsLogger");
        MDM_DEVICE_DATA_LOGGER = Logger.getLogger("MDMDeviceDataLogger");
    }
}
