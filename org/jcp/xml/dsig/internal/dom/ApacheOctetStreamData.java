package org.jcp.xml.dsig.internal.dom;

import java.io.IOException;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.signature.XMLSignatureInput;
import javax.xml.crypto.OctetStreamData;

public class ApacheOctetStreamData extends OctetStreamData implements ApacheData
{
    private XMLSignatureInput xi;
    
    public ApacheOctetStreamData(final XMLSignatureInput xi) throws CanonicalizationException, IOException {
        super(xi.getOctetStream(), xi.getSourceURI(), xi.getMIMEType());
        this.xi = xi;
    }
    
    public XMLSignatureInput getXMLSignatureInput() {
        return this.xi;
    }
}
