package com.me.mdm.chrome.agent.core;

import com.me.mdm.chrome.agent.ChromeDeviceManager;
import com.me.mdm.chrome.agent.Context;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultProcessRequestHandler extends ProcessRequestHandler
{
    Logger logger;
    
    public DefaultProcessRequestHandler() {
        this.logger = Logger.getLogger("MDMChromeAgentLogger");
    }
    
    @Override
    public void processRequest(final Request request, final Response response) {
        this.logger.log(Level.INFO, "DefaultProcessRequestHandler : Request Command received : {0}", request.requestType);
        final Context context = request.getContainer().getContext();
        final ProcessRequestHandler handler = this.getRequestHandler(request.requestType, request.commandScope, context);
        if (handler != null) {
            this.logger.log(Level.INFO, "ProcessRequestHandler: {0}", handler.getClass().getName());
            try {
                handler.processRequest(request, response);
                if (response.getStatus().equalsIgnoreCase("NotNow")) {
                    this.logger.info("DefaultProcessRequestHandler: NotNow response: Registering for actions");
                }
            }
            catch (final Throwable noExp) {
                this.logger.log(Level.WARNING, "DefaultProcessRequestHandler: Unknown exception while processRequestForDEPToken() ", noExp);
                response.setErrorCode(12115);
            }
        }
        else {
            response.setErrorCode(12100);
        }
    }
    
    public ProcessRequestHandler getRequestHandler(final String requestCommand, final String scope, final Context context) {
        return ChromeDeviceManager.getInstance().getProcessRequestHandler(requestCommand, scope, context);
    }
}
