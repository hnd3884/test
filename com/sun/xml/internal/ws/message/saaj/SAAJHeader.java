package com.sun.xml.internal.ws.message.saaj;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.SOAPVersion;
import javax.xml.soap.SOAPHeaderElement;
import com.sun.xml.internal.ws.message.DOMHeader;

public final class SAAJHeader extends DOMHeader<SOAPHeaderElement>
{
    public SAAJHeader(final SOAPHeaderElement header) {
        super(header);
    }
    
    @NotNull
    @Override
    public String getRole(@NotNull final SOAPVersion soapVersion) {
        String v = this.getAttribute(soapVersion.nsUri, soapVersion.roleAttributeName);
        if (v == null || v.equals("")) {
            v = soapVersion.implicitRole;
        }
        return v;
    }
}
