package com.unboundid.ldap.listener;

import com.unboundid.ldap.protocol.UnbindRequestProtocolOp;
import com.unboundid.ldap.protocol.SearchRequestProtocolOp;
import com.unboundid.ldap.protocol.ModifyDNRequestProtocolOp;
import com.unboundid.ldap.protocol.ModifyRequestProtocolOp;
import com.unboundid.ldap.protocol.ExtendedRequestProtocolOp;
import com.unboundid.ldap.protocol.DeleteRequestProtocolOp;
import com.unboundid.ldap.protocol.CompareRequestProtocolOp;
import com.unboundid.ldap.protocol.BindRequestProtocolOp;
import com.unboundid.ldap.protocol.LDAPMessage;
import com.unboundid.ldap.protocol.AddRequestProtocolOp;
import com.unboundid.ldap.sdk.Control;
import java.util.List;
import com.unboundid.ldap.protocol.AbandonRequestProtocolOp;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Extensible;

@Extensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public abstract class LDAPListenerRequestHandler
{
    public abstract LDAPListenerRequestHandler newInstance(final LDAPListenerClientConnection p0) throws LDAPException;
    
    public void closeInstance() {
    }
    
    public void processAbandonRequest(final int messageID, final AbandonRequestProtocolOp request, final List<Control> controls) {
    }
    
    public abstract LDAPMessage processAddRequest(final int p0, final AddRequestProtocolOp p1, final List<Control> p2);
    
    public abstract LDAPMessage processBindRequest(final int p0, final BindRequestProtocolOp p1, final List<Control> p2);
    
    public abstract LDAPMessage processCompareRequest(final int p0, final CompareRequestProtocolOp p1, final List<Control> p2);
    
    public abstract LDAPMessage processDeleteRequest(final int p0, final DeleteRequestProtocolOp p1, final List<Control> p2);
    
    public abstract LDAPMessage processExtendedRequest(final int p0, final ExtendedRequestProtocolOp p1, final List<Control> p2);
    
    public abstract LDAPMessage processModifyRequest(final int p0, final ModifyRequestProtocolOp p1, final List<Control> p2);
    
    public abstract LDAPMessage processModifyDNRequest(final int p0, final ModifyDNRequestProtocolOp p1, final List<Control> p2);
    
    public abstract LDAPMessage processSearchRequest(final int p0, final SearchRequestProtocolOp p1, final List<Control> p2);
    
    public void processUnbindRequest(final int messageID, final UnbindRequestProtocolOp request, final List<Control> controls) {
    }
}
