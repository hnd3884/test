package org.jcp.xml.dsig.internal.dom;

import org.apache.xml.security.signature.XMLSignatureInput;
import javax.xml.crypto.Data;

public interface ApacheData extends Data
{
    XMLSignatureInput getXMLSignatureInput();
}
