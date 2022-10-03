package org.apache.coyote;

import org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler;
import org.apache.tomcat.util.net.SocketWrapperBase;

public interface UpgradeProtocol
{
    String getHttpUpgradeName(final boolean p0);
    
    byte[] getAlpnIdentifier();
    
    String getAlpnName();
    
    Processor getProcessor(final SocketWrapperBase<?> p0, final Adapter p1);
    
    InternalHttpUpgradeHandler getInternalUpgradeHandler(final Adapter p0, final Request p1);
    
    boolean accept(final Request p0);
}
