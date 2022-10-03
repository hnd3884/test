package org.apache.xml.security.keys.content.keyvalues;

import org.apache.xml.security.exceptions.XMLSecurityException;
import java.security.PublicKey;

public interface KeyValueContent
{
    PublicKey getPublicKey() throws XMLSecurityException;
}
