package com.adventnet.customview.service;

import com.adventnet.customview.CustomViewException;
import com.adventnet.model.Model;
import com.adventnet.customview.ViewData;
import com.adventnet.customview.CustomViewRequest;
import com.adventnet.customview.CustomViewManagerContext;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultServerServiceProvider implements ServiceProvider
{
    private static int instanceIdCounter;
    private int instanceId;
    private static byte[] lockObj;
    private static final Logger OUT;
    ServiceProvider nextSP;
    
    public DefaultServerServiceProvider() {
        this.nextSP = null;
        synchronized (DefaultServerServiceProvider.lockObj) {
            this.instanceId = DefaultServerServiceProvider.instanceIdCounter++;
            DefaultServerServiceProvider.OUT.log(Level.INFO, " Inside DefaultServerServiceProvider[{0}]", new Integer(this.instanceId));
        }
    }
    
    @Override
    public void setNextServiceProvider(final ServiceProvider sp) {
        this.nextSP = sp;
    }
    
    @Override
    public String getServiceName() {
        return "DEFAULT_SERVER_SERVICE_PROVIDER";
    }
    
    @Override
    public void setCustomViewManagerContext(final CustomViewManagerContext customViewManagerContext) {
        DefaultServerServiceProvider.OUT.log(Level.INFO, " DefaultServerServiceProvider[{0}] : Inside setCustomViewManagerContext", new Integer(this.instanceId));
    }
    
    @Override
    public ViewData process(final CustomViewRequest customViewRequest) throws CustomViewException {
        DefaultServerServiceProvider.OUT.log(Level.INFO, " DefaultServerServiceProvider[{0}] : Inside process", new Integer(this.instanceId));
        final ViewData vd = new ViewData(customViewRequest.getCustomViewConfiguration(), null);
        vd.put("\n" + this.toString(), "I was created in thro DefaultServerServiceProvider[" + this.instanceId + "]");
        DefaultServerServiceProvider.OUT.log(Level.INFO, " DefaultServerServiceProvider[{0}] : returning viewData", new Integer(this.instanceId));
        return vd;
    }
    
    @Override
    public void cleanup() {
        this.nextSP = null;
    }
    
    static {
        DefaultServerServiceProvider.instanceIdCounter = 0;
        DefaultServerServiceProvider.lockObj = new byte[0];
        OUT = Logger.getLogger(DefaultServerServiceProvider.class.getName());
    }
}
