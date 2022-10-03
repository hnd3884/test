package com.me.devicemanagement.framework.server.orglock;

import java.lang.reflect.Proxy;
import org.json.JSONException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.utils.FrameworkConfigurations;
import java.lang.reflect.Method;
import java.util.logging.Logger;
import java.lang.reflect.InvocationHandler;

public class FairLockInvocationHandler implements InvocationHandler
{
    private final Object implementation;
    private static final Logger LOGGER;
    
    public FairLockInvocationHandler(final Object implementation) {
        this.implementation = implementation;
    }
    
    @Override
    public Object invoke(final Object proxy, final Method meth, final Object[] args) {
        FairLock fairLock = null;
        Object result = null;
        String key = null;
        String orgID = "globalLock";
        String LockType = null;
        try {
            LockType = (String)FrameworkConfigurations.getSpecificPropertyIfExists("Org_Lock_config", "enableFairLockTypeLock", (Object)"synLock");
            if (LockType.equals("orgFairLock")) {
                FairLockUtil fairLockUtil = (FairLockUtil)ApiFactoryProvider.getImplClassInstance("DM_FAIR_LOCK_MAP_CLASS");
                if (fairLockUtil == null) {
                    fairLockUtil = new FairLockUtil();
                }
                orgID = fairLockUtil.getUniqueId();
                key = args[0].getClass().getCanonicalName() + "@" + orgID + "@" + System.identityHashCode(args[0]);
                fairLock = FairLockFactory.getInstance().getFairLockForOrg(key);
                try {
                    fairLock.lock();
                    result = meth.invoke(this.implementation, args);
                }
                catch (final InterruptedException e) {
                    FairLockInvocationHandler.LOGGER.log(Level.SEVERE, "InterruptedException by Fairlock", e);
                }
                catch (final IllegalAccessException e2) {
                    FairLockInvocationHandler.LOGGER.log(Level.SEVERE, "IllegalAccessException by proxy method", e2);
                }
                catch (final InvocationTargetException e3) {
                    FairLockInvocationHandler.LOGGER.log(Level.SEVERE, "InvocationTargetException by proxy method", e3);
                }
                finally {
                    fairLock.unlock();
                }
            }
            else if (LockType.equals("globalLock")) {
                key = args[0].getClass().getCanonicalName() + "@" + orgID + "@" + System.identityHashCode(args[0]);
                fairLock = FairLockFactory.getInstance().getFairLockForOrg(key);
                try {
                    fairLock.lock();
                    result = meth.invoke(this.implementation, args);
                }
                catch (final InterruptedException e4) {
                    FairLockInvocationHandler.LOGGER.log(Level.SEVERE, "InterruptedException by Fairlock", e4);
                }
                catch (final IllegalAccessException e5) {
                    FairLockInvocationHandler.LOGGER.log(Level.SEVERE, "IllegalAccessException by proxy method", e5);
                }
                catch (final InvocationTargetException e6) {
                    FairLockInvocationHandler.LOGGER.log(Level.SEVERE, "InvocationTargetException by proxy method", e6);
                }
                finally {
                    fairLock.unlock();
                }
            }
            else if (LockType.equals("synLock")) {
                synchronized (args[0]) {
                    try {
                        result = meth.invoke(this.implementation, args);
                    }
                    catch (final IllegalAccessException e2) {
                        FairLockInvocationHandler.LOGGER.log(Level.SEVERE, "IllegalAccessException by proxy method", e2);
                    }
                    catch (final InvocationTargetException e3) {
                        FairLockInvocationHandler.LOGGER.log(Level.SEVERE, "InvocationTargetException by proxy method", e3);
                    }
                }
            }
            else if (LockType.equals("orgLock")) {
                FairLockUtil fairLockUtil = (FairLockUtil)ApiFactoryProvider.getImplClassInstance("DM_FAIR_LOCK_MAP_CLASS");
                if (fairLockUtil == null) {
                    fairLockUtil = new FairLockUtil();
                }
                orgID = fairLockUtil.getUniqueId();
                key = args[0].getClass().getCanonicalName() + "@" + orgID + "@" + System.identityHashCode(args[0]);
                fairLock = FairLockFactory.getInstance().getFairLockForOrg(key);
                synchronized (fairLock) {
                    try {
                        result = meth.invoke(this.implementation, args);
                    }
                    catch (final IllegalAccessException e7) {
                        FairLockInvocationHandler.LOGGER.log(Level.SEVERE, "IllegalAccessException by proxy method", e7);
                    }
                    catch (final InvocationTargetException e8) {
                        FairLockInvocationHandler.LOGGER.log(Level.SEVERE, "InvocationTargetException by proxy method", e8);
                    }
                }
            }
        }
        catch (final JSONException e9) {
            FairLockInvocationHandler.LOGGER.log(Level.SEVERE, "JSONException in proxy method", (Throwable)e9);
        }
        return result;
    }
    
    public static Object execute(final Object object, final FairLockImpAPI fairLockImpAPI) throws Exception {
        final InvocationHandler handler = new FairLockInvocationHandler(fairLockImpAPI);
        final ClassLoader loader = FairLockImpAPI.class.getClassLoader();
        final FairLockImpAPI proxy = (FairLockImpAPI)Proxy.newProxyInstance(loader, new Class[] { FairLockImpAPI.class }, handler);
        return proxy.run(object);
    }
    
    static {
        LOGGER = Logger.getLogger(FairLockInvocationHandler.class.getName());
    }
}
