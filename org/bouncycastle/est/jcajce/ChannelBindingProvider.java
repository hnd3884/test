package org.bouncycastle.est.jcajce;

import java.net.Socket;

public interface ChannelBindingProvider
{
    boolean canAccessChannelBinding(final Socket p0);
    
    byte[] getChannelBinding(final Socket p0, final String p1);
}
