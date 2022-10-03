package com.me.mdm.agent.servlets.doc;

import java.io.IOException;
import javax.servlet.ServletException;
import com.me.mdm.core.auth.APIKey;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.Map;
import com.me.mdm.core.auth.MDMDeviceAPIKeyGenerator;
import com.me.mdm.server.doc.DocMgmtDataHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.logging.Level;
import com.google.json.JsonSanitizer;
import com.me.mdm.server.doc.DocMgmt;
import com.me.mdm.agent.handlers.DeviceRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONArray;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import org.json.JSONObject;
import com.me.mdm.webclient.filter.DeviceAuthenticatedRequestServlet;

public class DocServlet extends DeviceAuthenticatedRequestServlet
{
    public long extractLastSyncTime(final JSONObject msgRequestJS) {
        Long lastSyncTime = -1L;
        final Object lastSyncTimeObj = msgRequestJS.get("LastSyncTime");
        if (lastSyncTimeObj instanceof String) {
            if (SyMUtil.isStringValid((String)lastSyncTimeObj)) {
                lastSyncTime = Long.valueOf((String)lastSyncTimeObj);
            }
        }
        else if (lastSyncTimeObj instanceof Integer || lastSyncTimeObj instanceof Long) {
            lastSyncTime = Long.valueOf(String.valueOf(lastSyncTimeObj));
        }
        return lastSyncTime;
    }
    
    public Long[] extractDocMDIds(final JSONObject msgRequestJS) {
        final List<Long> docMDidsList = new ArrayList<Long>();
        final JSONArray docMDidsAr = (JSONArray)msgRequestJS.get("DOC_MD_ID");
        final List<String> docMDidsStr = JSONUtil.getInstance().convertJSONArrayTOList(docMDidsAr);
        for (int i = 0; i < docMDidsStr.size(); ++i) {
            docMDidsList.add(Long.valueOf(docMDidsStr.get(i)));
        }
        return docMDidsList.toArray(new Long[docMDidsList.size()]);
    }
    
    public void doPost(final HttpServletRequest request, final HttpServletResponse response, DeviceRequest deviceRequest) throws ServletException, IOException {
        try {
            deviceRequest = this.prepareDeviceRequest(request, DocMgmt.logger);
            deviceRequest.deviceRequestData = JsonSanitizer.sanitize((String)deviceRequest.deviceRequestData);
            final String strData = (String)deviceRequest.deviceRequestData;
            final JSONObject deviceRequestJS = new JSONObject(strData);
            final JSONObject msgRequestJS = (JSONObject)deviceRequestJS.get("MsgRequest");
            final Long lastSyncTime = this.extractLastSyncTime(msgRequestJS);
            final String udid = (String)deviceRequestJS.get("UDID");
            DocMgmt.logger.log(Level.INFO, "DocServlet-POST =>  Received request from agent {0} : {1}", new Object[] { udid, strData });
            final Long resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
            final boolean ackSupported = msgRequestJS.optBoolean("ACK_SUPPORTED", false);
            DocMgmt.logger.log(Level.INFO, "DocServlet-POST =>  Processing request from agent {0} at {1}", new Object[] { udid, System.currentTimeMillis() });
            final JSONObject docDiffData = DocMgmtDataHandler.getInstance(MDMUtil.getInstance().getDeviceDetailsFromUDID(udid).get("PLATFORM_TYPE")).getDiffAndUpdateStatus(resourceID, lastSyncTime, ackSupported);
            final APIKey key = MDMDeviceAPIKeyGenerator.getInstance().getAPIKeyFromMap(this.getParameterValueMap(request));
            String docDiffDataString = null;
            if (key != null) {
                docDiffDataString = MDMDeviceAPIKeyGenerator.getInstance().replaceDeviceAPIKeyPlaceHolder(docDiffData.toString(), key, false, udid);
            }
            response.setHeader("Content-Type", "application/json");
            response.getWriter().write(docDiffDataString);
            DocMgmt.logger.log(Level.INFO, "DocServlet-POST : Response data to the agent {0} : {1} ", new Object[] { udid, MDMUtil.getPrettyJSON(docDiffDataString) });
            DeviceCommandRepository.getInstance().deleteResourceCommand("SyncDocuments", resourceID);
        }
        catch (final Exception ex) {
            DocMgmt.logger.log(Level.SEVERE, null, ex);
        }
    }
    
    public void doPut(final HttpServletRequest request, final HttpServletResponse response, DeviceRequest deviceRequest) throws ServletException, IOException {
        try {
            deviceRequest = this.prepareDeviceRequest(request, DocMgmt.logger);
            deviceRequest.deviceRequestData = JsonSanitizer.sanitize((String)deviceRequest.deviceRequestData);
            final String strData = (String)deviceRequest.deviceRequestData;
            final JSONObject deviceRequestJS = new JSONObject(strData);
            final JSONObject msgRequestJS = (JSONObject)deviceRequestJS.get("MsgRequest");
            final String udid = (String)deviceRequestJS.get("UDID");
            DocMgmt.logger.log(Level.INFO, "DocServlet-PUT =>  Received request from agent {0} : {1}", new Object[] { udid, strData });
            final Long[] docMDids = this.extractDocMDIds(msgRequestJS);
            final Long lastSyncTime = this.extractLastSyncTime(msgRequestJS);
            final Long resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
            final boolean acknowledged = DocMgmtDataHandler.getInstance().processAck(resourceID, lastSyncTime, docMDids);
            DocMgmt.logger.log(Level.INFO, "DocServlet-PUT =>  Acknowledgement status : {0} for udid {1} with lastsynctime {2}", new Object[] { acknowledged, udid, lastSyncTime });
            if (acknowledged) {
                final JSONObject serverAck = new JSONObject();
                serverAck.put("LastSyncTime", (Object)lastSyncTime);
                serverAck.put("MsgResponseType", (Object)"AckDocuments");
                response.setHeader("Content-Type", "application/json");
                response.getWriter().write(serverAck.toString());
            }
            else {
                response.setStatus(500);
            }
        }
        catch (final Exception ex) {
            DocMgmt.logger.log(Level.SEVERE, null, ex);
        }
    }
}
