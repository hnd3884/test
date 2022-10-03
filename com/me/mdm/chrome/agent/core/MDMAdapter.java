package com.me.mdm.chrome.agent.core;

import com.me.mdm.chrome.agent.core.communication.CommunicationManager;
import java.util.HashMap;
import com.me.mdm.chrome.agent.core.communication.CommunicationStatus;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public class MDMAdapter
{
    Logger logger;
    ProcessRequestHandler handler;
    MDMContainer container;
    public static int errorCode;
    static MDMAdapter mdmAdapter;
    
    public MDMAdapter() {
        this.logger = Logger.getLogger("MDMChromeAgentLogger");
        this.handler = null;
        this.container = null;
    }
    
    public static MDMAdapter getInstance() {
        if (MDMAdapter.mdmAdapter == null) {
            MDMAdapter.mdmAdapter = new MDMAdapter();
        }
        return MDMAdapter.mdmAdapter;
    }
    
    public void setRequestHandler(final ProcessRequestHandler handler) {
        this.handler = handler;
    }
    
    public void setContainer(final MDMContainer container) {
        this.container = container;
    }
    
    private void prepareRequest(final JSONObject requestObject) {
        try {
            final Request request = new Request();
            final JSONObject cmdObject = requestObject.getJSONObject("Command");
            request.commandUUID = String.valueOf(requestObject.get("CommandUUID"));
            request.requestType = String.valueOf(cmdObject.get("RequestType"));
            try {
                request.requestData = cmdObject.get("RequestData");
            }
            catch (final Exception e) {
                request.requestData = new JSONObject();
            }
            request.setContainer(this.container);
            try {
                request.commandScope = String.valueOf(requestObject.get("CommandScope"));
            }
            catch (final Exception e) {
                request.commandScope = "device";
            }
            this.container.setRequest(request);
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception ocurred : {0}", exp.getMessage());
        }
    }
    
    private void prepareResponse() {
        try {
            final Request request = this.container.getRequest();
            final Response response = new Response();
            response.setCommandUUID(request.commandUUID);
            response.setDeviceUDID(this.container.getServerContext().deviceUDID);
            response.setCommandVersion(this.container.getServerContext().commandVersion);
            response.setStatus("Acknowledged");
            response.setResponseType(request.requestType);
            response.setScope(request.commandScope);
            this.container.setResponse(response);
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "prepareResponse : Exception ocurred : {0}", exp.getMessage());
        }
    }
    
    private JSONObject prepareInitialHandShake() throws Exception {
        final JSONObject statusObject = new JSONObject();
        final MDMServerContext mdmServerContext = this.container.getServerContext();
        statusObject.put("UDID", (Object)mdmServerContext.deviceUDID);
        statusObject.put("CommandVersion", mdmServerContext.commandVersion);
        statusObject.put("Status", (Object)"Idle");
        return statusObject;
    }
    
    private void handleRequest() {
        try {
            final Request request = this.container.getRequest();
            final Response response = this.container.getResponse();
            if (this.handler != null) {
                this.handler.processRequest(request, response);
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "handleRequest : Exception ocurred : {0}", exp.getMessage());
        }
    }
    
    public void initializeMDMServerContext(final MDMServerContext serverContext) {
        this.container.setServerContext(serverContext);
    }
    
    public void start() {
        CommunicationStatus status = new CommunicationStatus(1);
        try {
            final JSONObject jsonObject = this.prepareInitialHandShake();
            final HashMap<String, String> paramsMap = new HashMap<String, String>();
            paramsMap.put("customerId", this.container.getServerContext().customerId.toString());
            status = CommunicationManager.getInstance().getCommunicationHandler().postData(jsonObject, paramsMap);
            while (status.getStatus() == 0 && status.getUrlDataBuffer() != null) {
                final JSONObject requestObject = new JSONObject(status.getUrlDataBuffer());
                if (requestObject.has("Status")) {
                    this.logger.info(requestObject.toString());
                }
                if (requestObject.isNull("Command")) {
                    break;
                }
                this.logger.log(Level.INFO, "Going to prepare response");
                this.prepareRequest(requestObject);
                this.prepareResponse();
                this.handleRequest();
                final JSONObject responseObject = this.container.getResponse().getResponseJSON();
                this.logger.log(Level.INFO, "Going to post response {0}", responseObject);
                status = CommunicationManager.getInstance().getCommunicationHandler().postData(responseObject, paramsMap);
                this.clearRequest();
                this.clearResponse();
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "MDMAdapter : Exception ocurred : ", exp);
            status.setErrorCode(-1);
            status.setErrorMessage(exp.toString());
        }
    }
    
    private void clearRequest() {
        this.container.setRequest(null);
    }
    
    private void clearResponse() {
        this.container.setResponse(null);
    }
    
    static {
        MDMAdapter.errorCode = 0;
        MDMAdapter.mdmAdapter = null;
    }
}
