package com.adventnet.customview.service;

import com.adventnet.customview.CustomViewException;
import com.adventnet.customview.ViewData;
import com.adventnet.customview.CustomViewRequest;
import com.adventnet.customview.CustomViewManagerContext;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultClientServiceProvider implements ServiceProvider
{
    private static int instanceIdCounter;
    private int instanceId;
    private static byte[] lockObj;
    private static final Logger OUT;
    ServiceProvider nextSP;
    
    public DefaultClientServiceProvider() {
        this.nextSP = null;
        synchronized (DefaultClientServiceProvider.lockObj) {
            this.instanceId = DefaultClientServiceProvider.instanceIdCounter++;
            DefaultClientServiceProvider.OUT.log(Level.FINER, " Inside DefaultClientServiceProvider[{0}]", new Integer(this.instanceId));
        }
    }
    
    @Override
    public void setNextServiceProvider(final ServiceProvider sp) {
        this.nextSP = sp;
    }
    
    @Override
    public String getServiceName() {
        return "DEFAULT_CLIENT_SERVICE_PROVIDER";
    }
    
    @Override
    public void setCustomViewManagerContext(final CustomViewManagerContext customViewManagerContext) {
        DefaultClientServiceProvider.OUT.log(Level.FINER, " DefaultClientServiceProvider[{0}] : Inside setCustomViewManagerContext", new Integer(this.instanceId));
    }
    
    @Override
    public ViewData process(final CustomViewRequest customViewRequest) throws CustomViewException {
        DefaultClientServiceProvider.OUT.log(Level.FINER, " DefaultClientServiceProvider[{0}] : Inside process", new Integer(this.instanceId));
        final ViewData toReturn = this.nextSP.process(customViewRequest);
        if (toReturn != null) {
            toReturn.put("\n" + this.toString(), "I passed thro DefaultClientServiceProvider[" + this.instanceId + "]");
        }
        DefaultClientServiceProvider.OUT.log(Level.FINER, " DefaultClientServiceProvider[{0}] : returning viewData", new Integer(this.instanceId));
        return toReturn;
    }
    
    @Override
    public void cleanup() {
        this.nextSP = null;
    }
    
    static {
        DefaultClientServiceProvider.instanceIdCounter = 0;
        DefaultClientServiceProvider.lockObj = new byte[0];
        OUT = Logger.getLogger(DefaultClientServiceProvider.class.getName());
    }
}
