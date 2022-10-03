package org.apache.coyote;

import org.apache.tomcat.util.net.SSLHostConfig;
import java.util.concurrent.Executor;

public interface ProtocolHandler
{
    Adapter getAdapter();
    
    void setAdapter(final Adapter p0);
    
    Executor getExecutor();
    
    void init() throws Exception;
    
    void start() throws Exception;
    
    void pause() throws Exception;
    
    void resume() throws Exception;
    
    void stop() throws Exception;
    
    void destroy() throws Exception;
    
    void closeServerSocketGraceful();
    
    boolean isAprRequired();
    
    boolean isSendfileSupported();
    
    void addSslHostConfig(final SSLHostConfig p0);
    
    SSLHostConfig[] findSslHostConfigs();
    
    void addUpgradeProtocol(final UpgradeProtocol p0);
    
    UpgradeProtocol[] findUpgradeProtocols();
}
