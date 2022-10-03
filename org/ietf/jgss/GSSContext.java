package org.ietf.jgss;

import java.io.OutputStream;
import java.io.InputStream;

public interface GSSContext
{
    public static final int DEFAULT_LIFETIME = 0;
    public static final int INDEFINITE_LIFETIME = Integer.MAX_VALUE;
    
    byte[] initSecContext(final byte[] p0, final int p1, final int p2) throws GSSException;
    
    int initSecContext(final InputStream p0, final OutputStream p1) throws GSSException;
    
    byte[] acceptSecContext(final byte[] p0, final int p1, final int p2) throws GSSException;
    
    void acceptSecContext(final InputStream p0, final OutputStream p1) throws GSSException;
    
    boolean isEstablished();
    
    void dispose() throws GSSException;
    
    int getWrapSizeLimit(final int p0, final boolean p1, final int p2) throws GSSException;
    
    byte[] wrap(final byte[] p0, final int p1, final int p2, final MessageProp p3) throws GSSException;
    
    void wrap(final InputStream p0, final OutputStream p1, final MessageProp p2) throws GSSException;
    
    byte[] unwrap(final byte[] p0, final int p1, final int p2, final MessageProp p3) throws GSSException;
    
    void unwrap(final InputStream p0, final OutputStream p1, final MessageProp p2) throws GSSException;
    
    byte[] getMIC(final byte[] p0, final int p1, final int p2, final MessageProp p3) throws GSSException;
    
    void getMIC(final InputStream p0, final OutputStream p1, final MessageProp p2) throws GSSException;
    
    void verifyMIC(final byte[] p0, final int p1, final int p2, final byte[] p3, final int p4, final int p5, final MessageProp p6) throws GSSException;
    
    void verifyMIC(final InputStream p0, final InputStream p1, final MessageProp p2) throws GSSException;
    
    byte[] export() throws GSSException;
    
    void requestMutualAuth(final boolean p0) throws GSSException;
    
    void requestReplayDet(final boolean p0) throws GSSException;
    
    void requestSequenceDet(final boolean p0) throws GSSException;
    
    void requestCredDeleg(final boolean p0) throws GSSException;
    
    void requestAnonymity(final boolean p0) throws GSSException;
    
    void requestConf(final boolean p0) throws GSSException;
    
    void requestInteg(final boolean p0) throws GSSException;
    
    void requestLifetime(final int p0) throws GSSException;
    
    void setChannelBinding(final ChannelBinding p0) throws GSSException;
    
    boolean getCredDelegState();
    
    boolean getMutualAuthState();
    
    boolean getReplayDetState();
    
    boolean getSequenceDetState();
    
    boolean getAnonymityState();
    
    boolean isTransferable() throws GSSException;
    
    boolean isProtReady();
    
    boolean getConfState();
    
    boolean getIntegState();
    
    int getLifetime();
    
    GSSName getSrcName() throws GSSException;
    
    GSSName getTargName() throws GSSException;
    
    Oid getMech() throws GSSException;
    
    GSSCredential getDelegCred() throws GSSException;
    
    boolean isInitiator() throws GSSException;
}
