package com.sun.xml.internal.ws.model.wsdl;

import com.sun.xml.internal.ws.resources.UtilMessages;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLObject;
import javax.xml.ws.WebServiceException;
import org.xml.sax.Locator;
import javax.xml.namespace.QName;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashSet;
import javax.xml.stream.XMLStreamReader;
import java.util.List;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLExtension;
import java.util.Set;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLExtensible;

abstract class AbstractExtensibleImpl extends AbstractObjectImpl implements WSDLExtensible
{
    protected final Set<WSDLExtension> extensions;
    protected List<UnknownWSDLExtension> notUnderstoodExtensions;
    
    protected AbstractExtensibleImpl(final XMLStreamReader xsr) {
        super(xsr);
        this.extensions = new HashSet<WSDLExtension>();
        this.notUnderstoodExtensions = new ArrayList<UnknownWSDLExtension>();
    }
    
    protected AbstractExtensibleImpl(final String systemId, final int lineNumber) {
        super(systemId, lineNumber);
        this.extensions = new HashSet<WSDLExtension>();
        this.notUnderstoodExtensions = new ArrayList<UnknownWSDLExtension>();
    }
    
    @Override
    public final Iterable<WSDLExtension> getExtensions() {
        return this.extensions;
    }
    
    @Override
    public final <T extends WSDLExtension> Iterable<T> getExtensions(final Class<T> type) {
        final List<T> r = new ArrayList<T>(this.extensions.size());
        for (final WSDLExtension e : this.extensions) {
            if (type.isInstance(e)) {
                r.add(type.cast(e));
            }
        }
        return r;
    }
    
    @Override
    public <T extends WSDLExtension> T getExtension(final Class<T> type) {
        for (final WSDLExtension e : this.extensions) {
            if (type.isInstance(e)) {
                return type.cast(e);
            }
        }
        return null;
    }
    
    @Override
    public void addExtension(final WSDLExtension ex) {
        if (ex == null) {
            throw new IllegalArgumentException();
        }
        this.extensions.add(ex);
    }
    
    @Override
    public List<? extends UnknownWSDLExtension> getNotUnderstoodExtensions() {
        return this.notUnderstoodExtensions;
    }
    
    @Override
    public void addNotUnderstoodExtension(final QName extnEl, final Locator locator) {
        this.notUnderstoodExtensions.add(new UnknownWSDLExtension(extnEl, locator));
    }
    
    @Override
    public boolean areRequiredExtensionsUnderstood() {
        if (this.notUnderstoodExtensions.size() != 0) {
            final StringBuilder buf = new StringBuilder("Unknown WSDL extensibility elements:");
            for (final UnknownWSDLExtension extn : this.notUnderstoodExtensions) {
                buf.append('\n').append(extn.toString());
            }
            throw new WebServiceException(buf.toString());
        }
        return true;
    }
    
    protected static class UnknownWSDLExtension implements WSDLExtension, WSDLObject
    {
        private final QName extnEl;
        private final Locator locator;
        
        public UnknownWSDLExtension(final QName extnEl, final Locator locator) {
            this.extnEl = extnEl;
            this.locator = locator;
        }
        
        @Override
        public QName getName() {
            return this.extnEl;
        }
        
        @NotNull
        @Override
        public Locator getLocation() {
            return this.locator;
        }
        
        @Override
        public String toString() {
            return this.extnEl + " " + UtilMessages.UTIL_LOCATION(this.locator.getLineNumber(), this.locator.getSystemId());
        }
    }
}
