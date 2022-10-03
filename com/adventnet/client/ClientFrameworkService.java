package com.adventnet.client;

import com.adventnet.client.util.web.WebClientUtil;
import com.adventnet.persistence.DataObject;
import com.adventnet.mfw.service.Service;

public class ClientFrameworkService implements Service
{
    public void start() throws Exception {
    }
    
    public void create(final DataObject serviceDO) throws Exception {
        final String className = System.getProperty("AuthInterface");
        AuthInterface authImpl;
        if (className == null) {
            authImpl = new StandAloneAuth();
        }
        else {
            try {
                final Class<?> authenticationUtil = Class.forName(className);
                authImpl = (AuthInterface)authenticationUtil.newInstance();
            }
            catch (final Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
        WebClientUtil.setAuthImpl(authImpl);
    }
    
    public void stop() throws Exception {
    }
    
    public void destroy() throws Exception {
    }
}
