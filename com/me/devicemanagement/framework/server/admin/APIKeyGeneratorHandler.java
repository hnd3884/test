package com.me.devicemanagement.framework.server.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class APIKeyGeneratorHandler
{
    private static String sourceClass;
    private final Logger logger;
    private List<APIKeyGeneratorListener> apiKeyGeneratorListenerList;
    private static APIKeyGeneratorHandler apiKeyGeneratorHandler;
    
    public APIKeyGeneratorHandler() {
        this.logger = Logger.getLogger(APIKeyGeneratorHandler.sourceClass);
        this.apiKeyGeneratorListenerList = new ArrayList<APIKeyGeneratorListener>();
    }
    
    public static APIKeyGeneratorHandler getInstance() {
        if (APIKeyGeneratorHandler.apiKeyGeneratorHandler == null) {
            APIKeyGeneratorHandler.apiKeyGeneratorHandler = new APIKeyGeneratorHandler();
        }
        return APIKeyGeneratorHandler.apiKeyGeneratorHandler;
    }
    
    public void addAPIKeyGeneratorListener(final APIKeyGeneratorListener listener) {
        this.apiKeyGeneratorListenerList.add(listener);
    }
    
    public void removeAPIKeyGeneratorListener(final APIKeyGeneratorListener listener) {
        this.apiKeyGeneratorListenerList.remove(listener);
    }
    
    public void invokeAPIKeyAddOrUpdateListeners(final String scope) {
        for (int listenerInt = 0; listenerInt < this.apiKeyGeneratorListenerList.size(); ++listenerInt) {
            final APIKeyGeneratorListener listener = this.apiKeyGeneratorListenerList.get(listenerInt);
            listener.addOrUpdateAPIKey(scope);
        }
    }
    
    static {
        APIKeyGeneratorHandler.sourceClass = APIKeyGeneratorHandler.class.getName();
        APIKeyGeneratorHandler.apiKeyGeneratorHandler = null;
    }
}
