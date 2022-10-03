package com.maverick.ssh;

public interface SshContext
{
    void setChannelLimit(final int p0);
    
    int getChannelLimit();
    
    void setHostKeyVerification(final HostKeyVerification p0);
    
    HostKeyVerification getHostKeyVerification();
    
    void setSFTPProvider(final String p0);
    
    String getSFTPProvider();
    
    void setX11Display(final String p0);
    
    String getX11Display();
    
    byte[] getX11AuthenticationCookie() throws SshException;
    
    void setX11AuthenticationCookie(final byte[] p0);
    
    void setX11RealCookie(final byte[] p0);
    
    byte[] getX11RealCookie() throws SshException;
    
    void setX11RequestListener(final ForwardingRequestListener p0);
    
    ForwardingRequestListener getX11RequestListener();
    
    void enableFIPSMode() throws SshException;
}
