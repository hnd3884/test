package com.sun.org.apache.xml.internal.security.utils.resolver.implementations;

import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverContext;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.io.InputStream;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverSpi;

public class ResolverAnonymous extends ResourceResolverSpi
{
    private InputStream inStream;
    
    @Override
    public boolean engineIsThreadSafe() {
        return true;
    }
    
    public ResolverAnonymous(final String s) throws FileNotFoundException, IOException {
        this.inStream = Files.newInputStream(Paths.get(s, new String[0]), new OpenOption[0]);
    }
    
    public ResolverAnonymous(final InputStream inStream) {
        this.inStream = inStream;
    }
    
    @Override
    public XMLSignatureInput engineResolveURI(final ResourceResolverContext resourceResolverContext) {
        final XMLSignatureInput xmlSignatureInput = new XMLSignatureInput(this.inStream);
        xmlSignatureInput.setSecureValidation(resourceResolverContext.secureValidation);
        return xmlSignatureInput;
    }
    
    @Override
    public boolean engineCanResolveURI(final ResourceResolverContext resourceResolverContext) {
        return resourceResolverContext.uriToResolve == null;
    }
    
    @Override
    public String[] engineGetPropertyKeys() {
        return new String[0];
    }
}
