package com.me.idps.core.factory;

import java.util.Iterator;
import java.util.List;
import java.lang.reflect.Method;
import com.me.idps.core.crud.DMDomainHandler;
import com.me.idps.core.crud.DMDomainListener;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import java.util.HashSet;
import java.util.HashMap;

class IdpsImplRegistrar
{
    private static final HashMap<IdpsFactoryConstant, HashSet<Class>> IMPL_REGISTER;
    
    private IdpsImplRegistrar() {
        final IdpsFactoryConstant[] values;
        final IdpsFactoryConstant[] idpsFactoryConstants = values = IdpsFactoryConstant.values();
        for (final IdpsFactoryConstant idpsFactoryConstant : values) {
            IdpsImplRegistrar.IMPL_REGISTER.put(idpsFactoryConstant, new HashSet<Class>());
        }
    }
    
    static IdpsImplRegistrar getInstance() {
        return Holder.INSTANCE;
    }
    
    void register(final IdpsRegAPI idpsRegAPIimpl) {
        final IdpsFactoryConstant[] values;
        final IdpsFactoryConstant[] idpsFactoryConstants = values = IdpsFactoryConstant.values();
        for (final IdpsFactoryConstant idpsFactoryConstant : values) {
            try {
                final String methodName = idpsFactoryConstant.getInitializationMethod();
                final Method method = idpsRegAPIimpl.getClass().getMethod(methodName, (Class<?>[])new Class[0]);
                final Class impl = (Class)method.invoke(idpsRegAPIimpl, new Object[0]);
                if (impl != null) {
                    final HashSet<Class> existingImpls = IdpsImplRegistrar.IMPL_REGISTER.get(idpsFactoryConstant);
                    existingImpls.add(impl);
                    IdpsImplRegistrar.IMPL_REGISTER.put(idpsFactoryConstant, existingImpls);
                }
            }
            catch (final Exception e) {
                IDPSlogger.ERR.log(Level.SEVERE, null, e);
            }
        }
        final List<DMDomainListener> dmDomainListenerList = idpsRegAPIimpl.getDMDomainListener();
        if (dmDomainListenerList != null && !dmDomainListenerList.isEmpty()) {
            for (final DMDomainListener dmDomainListener : dmDomainListenerList) {
                DMDomainHandler.getInstance().addDomainListener(dmDomainListener);
            }
        }
    }
    
    HashSet<Class> getImplClassName(final IdpsFactoryConstant key) {
        return IdpsImplRegistrar.IMPL_REGISTER.get(key);
    }
    
    static {
        IMPL_REGISTER = new HashMap<IdpsFactoryConstant, HashSet<Class>>();
    }
    
    private static class Holder
    {
        private static final IdpsImplRegistrar INSTANCE;
        
        static {
            INSTANCE = new IdpsImplRegistrar(null);
        }
    }
}
