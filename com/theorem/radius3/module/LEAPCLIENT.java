package com.theorem.radius3.module;

import com.theorem.radius3.EAPException;
import com.theorem.radius3.ClientReceiveException;
import com.theorem.radius3.ClientSendException;
import java.net.SocketException;
import com.theorem.radius3.AttributeList;
import com.theorem.radius3.RADIUSClient;

public abstract class LEAPCLIENT
{
    protected RADIUSClient a;
    
    public LEAPCLIENT(final RADIUSClient a) {
        this.a = a;
    }
    
    public abstract void setUserName(final String p0);
    
    public abstract void setLEAPIdentity(final String p0);
    
    public abstract void setPassword(final byte[] p0);
    
    public abstract void setCommonAttributes(final AttributeList p0);
    
    public abstract boolean authenticate() throws SocketException, ClientSendException, ClientReceiveException, EAPException;
    
    public abstract byte[] getSessionKey();
}
