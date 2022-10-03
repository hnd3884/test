package com.me.mdm.chrome.agent.commands.profiles;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import java.util.Set;
import org.json.JSONException;
import java.util.Iterator;
import java.util.logging.Level;
import com.me.mdm.chrome.agent.db.MDMAgentParamsTableHandler;
import org.json.JSONObject;
import com.me.mdm.chrome.agent.Context;
import com.me.mdm.chrome.agent.core.Response;
import com.me.mdm.chrome.agent.core.Request;
import java.util.logging.Logger;

public abstract class PayloadRequestHandler
{
    public Logger logger;
    public static final String PAYLOAD_DATA = "PayloadData";
    
    public PayloadRequestHandler() {
        this.logger = Logger.getLogger("MDMChromeAgentLogger");
    }
    
    public abstract void processInstallPayload(final Request p0, final Response p1, final PayloadRequest p2, final PayloadResponse p3);
    
    public abstract void processModifyPayload(final Request p0, final Response p1, final PayloadRequest p2, final PayloadRequest p3, final PayloadResponse p4);
    
    public abstract void processRemovePayload(final Request p0, final Response p1, final PayloadRequest p2, final PayloadResponse p3);
    
    public boolean checkPayloadCompatible(final Request request, final Response response, final PayloadRequest payloadReq, final PayloadResponse payloadResp) {
        return true;
    }
    
    public void backupPayload() {
    }
    
    public void restorePayload(final Context context) {
    }
    
    public void processRevertToPreviousPayload(final Context context, final JSONObject payloadData) {
    }
    
    public void updateDB(final Context context, final PayloadRequest payloadReq) {
        this.persistPayloadRequestData(context, payloadReq);
    }
    
    public void removeDB(final Context context, final PayloadRequest payloadReq) {
        this.RemovePayloadRequestData(context, payloadReq);
    }
    
    public void persistPayloadRequestData(final Context context, final PayloadRequest payloadRequest) {
        try {
            final MDMAgentParamsTableHandler tableHandler = new MDMAgentParamsTableHandler(context);
            final String payloadType = payloadRequest.getPayloadType();
            final String payloadIdentifier = payloadRequest.getPayloadIdentifier();
            JSONObject joPayLoadList = new JSONObject();
            if (payloadIdentifier != null) {
                final String oldPayloads = tableHandler.getStringValue(payloadType);
                if (oldPayloads != null) {
                    joPayLoadList = new JSONObject(oldPayloads);
                }
                joPayLoadList.put(payloadIdentifier, (Object)payloadRequest.getPayloadData());
                tableHandler.addStringValue(payloadType, joPayLoadList.toString());
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception ocurred in persisting payload data{0}", exp.getMessage());
        }
    }
    
    public JSONObject getPayloadRequestData(final Context context, final PayloadRequest payloadRequest) {
        return payloadRequest.getPayloadData();
    }
    
    public boolean isLastPayload(final Context context, final String payloadType) {
        boolean isLast = true;
        try {
            JSONObject joPayLoadList = new JSONObject();
            final String oldPayloads = new MDMAgentParamsTableHandler(context).getStringValue(payloadType);
            if (oldPayloads != null) {
                joPayLoadList = new JSONObject(oldPayloads);
                if (joPayLoadList.length() > 1) {
                    isLast = false;
                }
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception ocurred in isLastPayload data", exp.getMessage());
        }
        return isLast;
    }
    
    public void RemovePayloadRequestData(final Context context, final PayloadRequest payloadRequest) {
        try {
            JSONObject joPayLoadList = new JSONObject();
            final String payloadType = payloadRequest.getPayloadType();
            final String oldPayloads = new MDMAgentParamsTableHandler(context).getStringValue(payloadType);
            if (oldPayloads != null) {
                joPayLoadList = new JSONObject(oldPayloads);
                joPayLoadList.put(payloadRequest.getPayloadIdentifier(), (Object)null);
                new MDMAgentParamsTableHandler(context).addStringValue(payloadType, joPayLoadList.toString());
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception ocurred in persisting payload data", exp.getMessage());
        }
    }
    
    public JSONObject getLastPayloadData(final Context context, final String payloadType) {
        JSONObject joPayloadData = new JSONObject();
        try {
            final String executedPayloadList = new MDMAgentParamsTableHandler(context).getStringValue(payloadType);
            if (executedPayloadList != null) {
                joPayloadData = (JSONObject)this.getLastValue(new JSONObject(executedPayloadList));
            }
            else {
                joPayloadData.put("PayloadType", (Object)payloadType);
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception Occurred while getting persistProfileRequestData", exp.getMessage());
        }
        return joPayloadData;
    }
    
    public Object getLastValue(final JSONObject jsonObject) {
        Object lastValue = null;
        try {
            final Iterator<?> it = jsonObject.keys();
            while (it.hasNext()) {
                final String key = (String)it.next();
                lastValue = jsonObject.get(key);
            }
        }
        catch (final Exception exp) {
            this.logger.info("Exception occurred while getting last value" + exp.getMessage());
        }
        return lastValue;
    }
    
    public JSONObject getConfiguredProfilesForPayloadType(final Context context, final String payloadType) {
        JSONObject joConfiguredProfiles = new JSONObject();
        final String configuredProfiles = new MDMAgentParamsTableHandler(context).getStringValue(payloadType);
        if (configuredProfiles != null) {
            try {
                joConfiguredProfiles = new JSONObject(configuredProfiles);
            }
            catch (final JSONException e) {
                this.logger.log(Level.WARNING, (Throwable)e, () -> "PayloadRequestHandler: Exception while parsing configured profiles list for payload type=" + s);
            }
        }
        return joConfiguredProfiles;
    }
    
    protected String getUpdateMask(final Set<String> keySet) {
        final StringBuilder builder = new StringBuilder();
        for (final String s : keySet) {
            builder.append(s + ",");
        }
        return builder.toString();
    }
    
    protected List<String> jsonArrayToList(final JSONArray arr) throws JSONException {
        final List<String> list = new ArrayList<String>();
        if (arr != null) {
            for (int i = 0; i < arr.length(); ++i) {
                list.add(String.valueOf(arr.get(i)));
            }
        }
        return list;
    }
}
