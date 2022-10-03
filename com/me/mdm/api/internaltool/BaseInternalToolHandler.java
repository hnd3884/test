package com.me.mdm.api.internaltool;

import org.json.JSONArray;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public abstract class BaseInternalToolHandler implements InternalToolInterface
{
    private Logger logger;
    
    public BaseInternalToolHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    @Override
    public JSONObject simulateDevices(final JSONObject jsonObject) throws Exception {
        try {
            final Class simClass = Class.forName("com.adventnet.sym.webclient.mdm.simulator.MDMSimulator");
            final Long customerId = APIUtil.getCustomerID(jsonObject);
            final Constructor constructor = simClass.getDeclaredConstructor(Long.class);
            constructor.newInstance(customerId);
            final Class simulatorClass = Class.forName("com.adventnet.sym.webclient.mdm.simulator.SimulatorUtil");
            final Object o = simulatorClass.newInstance();
            final Method method = simulatorClass.getMethod("replaceProperties", String.class);
            method.invoke(o, APIUtil.getRequestURL(jsonObject));
            final Class deviceClass = Class.forName("com.adventnet.sym.webclient.mdm.simulator.MDMDeviceSimulator");
            final Object o2 = deviceClass.newInstance();
            final Method method2 = deviceClass.getDeclaredMethod("simulateDevices", JSONObject.class);
            method2.setAccessible(Boolean.TRUE);
            method2.invoke(o2, jsonObject.getJSONObject("msg_body"));
        }
        catch (final Exception e) {
            this.logger.log(Level.INFO, "Issue in device simulation", e);
            throw new APIHTTPException("COM0014", new Object[0]);
        }
        return null;
    }
    
    @Override
    public JSONObject simulateGroups(final JSONObject jsonObject) throws Exception {
        try {
            final Class deviceClass = Class.forName("com.adventnet.sym.webclient.mdm.simulator.MDMGroupSimulator");
            final Object o1 = deviceClass.newInstance();
            final Method method1 = deviceClass.getDeclaredMethod("simulateGroups", JSONObject.class);
            method1.setAccessible(Boolean.TRUE);
            method1.invoke(o1, jsonObject);
        }
        catch (final Exception e) {
            this.logger.log(Level.INFO, "Issue in group simulation", e);
            throw new APIHTTPException("COM0014", new Object[0]);
        }
        return null;
    }
    
    @Override
    public JSONObject simulateScanDevices(final JSONObject jsonObject) throws Exception {
        try {
            final Class deviceClass = Class.forName("com.adventnet.sym.webclient.mdm.simulator.MDMSimulator");
            final Long customerId = APIUtil.getCustomerID(jsonObject);
            final Constructor constructor = deviceClass.getDeclaredConstructor(Long.class);
            final Object o1 = constructor.newInstance(customerId);
            final Method method1 = deviceClass.getDeclaredMethod("simulateDeviceNotification", JSONArray.class);
            final JSONArray platformNames = new JSONArray();
            final String platform = APIUtil.getStringFilter(jsonObject, "platform");
            final String[] platformTypes = platform.split(",");
            if (platform != null) {
                for (int i = 0; i < platformTypes.length; ++i) {
                    final int temp = Integer.parseInt(platformTypes[i]);
                    switch (temp) {
                        case 2: {
                            platformNames.put((Object)"safe");
                            platformNames.put((Object)"Android");
                            break;
                        }
                        case 3: {
                            platformNames.put((Object)"Windows");
                            break;
                        }
                        case 1: {
                            platformNames.put((Object)"ios");
                            break;
                        }
                        default: {
                            throw new APIHTTPException("COM0014", new Object[0]);
                        }
                    }
                }
            }
            else {
                platformNames.put((Object)"ios");
                platformNames.put((Object)"safe");
                platformNames.put((Object)"Android");
                platformNames.put((Object)"Windows");
            }
            method1.invoke(o1, platformNames);
        }
        catch (final Exception e) {
            this.logger.log(Level.INFO, "Issue in group simulation", e);
            throw new APIHTTPException("COM0014", new Object[0]);
        }
        return null;
    }
}
