package sun.security.jgss.spi;

import com.sun.security.jgss.InquireType;
import org.ietf.jgss.MessageProp;
import java.io.OutputStream;
import java.io.InputStream;
import org.ietf.jgss.Oid;
import org.ietf.jgss.ChannelBinding;
import org.ietf.jgss.GSSException;
import java.security.Provider;

public interface GSSContextSpi
{
    Provider getProvider();
    
    void requestLifetime(final int p0) throws GSSException;
    
    void requestMutualAuth(final boolean p0) throws GSSException;
    
    void requestReplayDet(final boolean p0) throws GSSException;
    
    void requestSequenceDet(final boolean p0) throws GSSException;
    
    void requestCredDeleg(final boolean p0) throws GSSException;
    
    void requestAnonymity(final boolean p0) throws GSSException;
    
    void requestConf(final boolean p0) throws GSSException;
    
    void requestInteg(final boolean p0) throws GSSException;
    
    void requestDelegPolicy(final boolean p0) throws GSSException;
    
    void setChannelBinding(final ChannelBinding p0) throws GSSException;
    
    boolean getCredDelegState();
    
    boolean getMutualAuthState();
    
    boolean getReplayDetState();
    
    boolean getSequenceDetState();
    
    boolean getAnonymityState();
    
    boolean getDelegPolicyState();
    
    boolean isTransferable() throws GSSException;
    
    boolean isProtReady();
    
    boolean isInitiator();
    
    boolean getConfState();
    
    boolean getIntegState();
    
    int getLifetime();
    
    boolean isEstablished();
    
    GSSNameSpi getSrcName() throws GSSException;
    
    GSSNameSpi getTargName() throws GSSException;
    
    Oid getMech() throws GSSException;
    
    GSSCredentialSpi getDelegCred() throws GSSException;
    
    byte[] initSecContext(final InputStream p0, final int p1) throws GSSException;
    
    byte[] acceptSecContext(final InputStream p0, final int p1) throws GSSException;
    
    int getWrapSizeLimit(final int p0, final boolean p1, final int p2) throws GSSException;
    
    void wrap(final InputStream p0, final OutputStream p1, final MessageProp p2) throws GSSException;
    
    byte[] wrap(final byte[] p0, final int p1, final int p2, final MessageProp p3) throws GSSException;
    
    void unwrap(final InputStream p0, final OutputStream p1, final MessageProp p2) throws GSSException;
    
    byte[] unwrap(final byte[] p0, final int p1, final int p2, final MessageProp p3) throws GSSException;
    
    void getMIC(final InputStream p0, final OutputStream p1, final MessageProp p2) throws GSSException;
    
    byte[] getMIC(final byte[] p0, final int p1, final int p2, final MessageProp p3) throws GSSException;
    
    void verifyMIC(final InputStream p0, final InputStream p1, final MessageProp p2) throws GSSException;
    
    void verifyMIC(final byte[] p0, final int p1, final int p2, final byte[] p3, final int p4, final int p5, final MessageProp p6) throws GSSException;
    
    byte[] export() throws GSSException;
    
    void dispose() throws GSSException;
    
    Object inquireSecContext(final InquireType p0) throws GSSException;
}
