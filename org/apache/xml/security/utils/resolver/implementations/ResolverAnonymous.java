package org.apache.xml.security.utils.resolver.implementations;

import org.w3c.dom.Attr;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.FileInputStream;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;

public class ResolverAnonymous extends ResourceResolverSpi
{
    private XMLSignatureInput _input;
    
    public ResolverAnonymous(final String s) throws FileNotFoundException, IOException {
        this._input = null;
        this._input = new XMLSignatureInput(new FileInputStream(s));
    }
    
    public ResolverAnonymous(final InputStream inputStream) {
        this._input = null;
        this._input = new XMLSignatureInput(inputStream);
    }
    
    public XMLSignatureInput engineResolve(final Attr attr, final String s) {
        return this._input;
    }
    
    public boolean engineCanResolve(final Attr attr, final String s) {
        return attr == null;
    }
    
    public String[] engineGetPropertyKeys() {
        return new String[0];
    }
}
