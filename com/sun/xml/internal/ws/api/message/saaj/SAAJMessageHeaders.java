package com.sun.xml.internal.ws.api.message.saaj;

import java.util.Collections;
import com.sun.xml.internal.ws.binding.SOAPBindingImpl;
import com.sun.xml.internal.ws.api.WSBinding;
import org.w3c.dom.Node;
import javax.xml.soap.SOAPException;
import java.util.List;
import java.util.ArrayList;
import com.sun.xml.internal.ws.message.saaj.SAAJHeader;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import javax.xml.soap.SOAPHeader;
import java.util.Set;
import com.sun.xml.internal.ws.api.SOAPVersion;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.api.message.Header;
import javax.xml.soap.SOAPHeaderElement;
import java.util.Map;
import javax.xml.soap.SOAPMessage;
import com.sun.xml.internal.ws.api.message.MessageHeaders;

public class SAAJMessageHeaders implements MessageHeaders
{
    SOAPMessage sm;
    Map<SOAPHeaderElement, Header> nonSAAJHeaders;
    Map<QName, Integer> notUnderstoodCount;
    SOAPVersion soapVersion;
    private Set<QName> understoodHeaders;
    
    public SAAJMessageHeaders(final SOAPMessage sm, final SOAPVersion version) {
        this.sm = sm;
        this.soapVersion = version;
        this.initHeaderUnderstanding();
    }
    
    private void initHeaderUnderstanding() {
        final SOAPHeader soapHeader = this.ensureSOAPHeader();
        if (soapHeader == null) {
            return;
        }
        final Iterator allHeaders = soapHeader.examineAllHeaderElements();
        while (allHeaders.hasNext()) {
            final SOAPHeaderElement nextHdrElem = allHeaders.next();
            if (nextHdrElem == null) {
                continue;
            }
            if (!nextHdrElem.getMustUnderstand()) {
                continue;
            }
            this.notUnderstood(nextHdrElem.getElementQName());
        }
    }
    
    @Override
    public void understood(final Header header) {
        this.understood(header.getNamespaceURI(), header.getLocalPart());
    }
    
    @Override
    public void understood(final String nsUri, final String localName) {
        this.understood(new QName(nsUri, localName));
    }
    
    @Override
    public void understood(final QName qName) {
        if (this.notUnderstoodCount == null) {
            this.notUnderstoodCount = new HashMap<QName, Integer>();
        }
        Integer count = this.notUnderstoodCount.get(qName);
        if (count != null && count > 0) {
            --count;
            if (count <= 0) {
                this.notUnderstoodCount.remove(qName);
            }
            else {
                this.notUnderstoodCount.put(qName, count);
            }
        }
        if (this.understoodHeaders == null) {
            this.understoodHeaders = new HashSet<QName>();
        }
        this.understoodHeaders.add(qName);
    }
    
    @Override
    public boolean isUnderstood(final Header header) {
        return this.isUnderstood(header.getNamespaceURI(), header.getLocalPart());
    }
    
    @Override
    public boolean isUnderstood(final String nsUri, final String localName) {
        return this.isUnderstood(new QName(nsUri, localName));
    }
    
    @Override
    public boolean isUnderstood(final QName name) {
        return this.understoodHeaders != null && this.understoodHeaders.contains(name);
    }
    
    public boolean isUnderstood(final int index) {
        return false;
    }
    
    @Override
    public Header get(final String nsUri, final String localName, final boolean markAsUnderstood) {
        final SOAPHeaderElement h = this.find(nsUri, localName);
        if (h != null) {
            if (markAsUnderstood) {
                this.understood(nsUri, localName);
            }
            return new SAAJHeader(h);
        }
        return null;
    }
    
    @Override
    public Header get(final QName name, final boolean markAsUnderstood) {
        return this.get(name.getNamespaceURI(), name.getLocalPart(), markAsUnderstood);
    }
    
    @Override
    public Iterator<Header> getHeaders(final QName headerName, final boolean markAsUnderstood) {
        return this.getHeaders(headerName.getNamespaceURI(), headerName.getLocalPart(), markAsUnderstood);
    }
    
    @Override
    public Iterator<Header> getHeaders(final String nsUri, final String localName, final boolean markAsUnderstood) {
        final SOAPHeader soapHeader = this.ensureSOAPHeader();
        if (soapHeader == null) {
            return null;
        }
        final Iterator allHeaders = soapHeader.examineAllHeaderElements();
        if (markAsUnderstood) {
            final List<Header> headers = new ArrayList<Header>();
            while (allHeaders.hasNext()) {
                final SOAPHeaderElement nextHdr = allHeaders.next();
                if (nextHdr != null && nextHdr.getNamespaceURI().equals(nsUri) && (localName == null || nextHdr.getLocalName().equals(localName))) {
                    this.understood(nextHdr.getNamespaceURI(), nextHdr.getLocalName());
                    headers.add(new SAAJHeader(nextHdr));
                }
            }
            return headers.iterator();
        }
        return new HeaderReadIterator(allHeaders, nsUri, localName);
    }
    
    @Override
    public Iterator<Header> getHeaders(final String nsUri, final boolean markAsUnderstood) {
        return this.getHeaders(nsUri, null, markAsUnderstood);
    }
    
    @Override
    public boolean add(final Header header) {
        try {
            header.writeTo(this.sm);
        }
        catch (final SOAPException e) {
            return false;
        }
        this.notUnderstood(new QName(header.getNamespaceURI(), header.getLocalPart()));
        if (this.isNonSAAJHeader(header)) {
            this.addNonSAAJHeader(this.find(header.getNamespaceURI(), header.getLocalPart()), header);
        }
        return true;
    }
    
    @Override
    public Header remove(final QName name) {
        return this.remove(name.getNamespaceURI(), name.getLocalPart());
    }
    
    @Override
    public Header remove(final String nsUri, final String localName) {
        final SOAPHeader soapHeader = this.ensureSOAPHeader();
        if (soapHeader == null) {
            return null;
        }
        SOAPHeaderElement headerElem = this.find(nsUri, localName);
        if (headerElem == null) {
            return null;
        }
        headerElem = (SOAPHeaderElement)soapHeader.removeChild(headerElem);
        this.removeNonSAAJHeader(headerElem);
        final QName hdrName = (nsUri == null) ? new QName(localName) : new QName(nsUri, localName);
        if (this.understoodHeaders != null) {
            this.understoodHeaders.remove(hdrName);
        }
        this.removeNotUnderstood(hdrName);
        return new SAAJHeader(headerElem);
    }
    
    private void removeNotUnderstood(final QName hdrName) {
        if (this.notUnderstoodCount == null) {
            return;
        }
        final Integer notUnderstood = this.notUnderstoodCount.get(hdrName);
        if (notUnderstood != null) {
            int intNotUnderstood = notUnderstood;
            if (--intNotUnderstood <= 0) {
                this.notUnderstoodCount.remove(hdrName);
            }
        }
    }
    
    private SOAPHeaderElement find(final QName qName) {
        return this.find(qName.getNamespaceURI(), qName.getLocalPart());
    }
    
    private SOAPHeaderElement find(final String nsUri, final String localName) {
        final SOAPHeader soapHeader = this.ensureSOAPHeader();
        if (soapHeader == null) {
            return null;
        }
        final Iterator allHeaders = soapHeader.examineAllHeaderElements();
        while (allHeaders.hasNext()) {
            final SOAPHeaderElement nextHdrElem = allHeaders.next();
            if (nextHdrElem.getNamespaceURI().equals(nsUri) && nextHdrElem.getLocalName().equals(localName)) {
                return nextHdrElem;
            }
        }
        return null;
    }
    
    private void notUnderstood(final QName qName) {
        if (this.notUnderstoodCount == null) {
            this.notUnderstoodCount = new HashMap<QName, Integer>();
        }
        final Integer count = this.notUnderstoodCount.get(qName);
        if (count == null) {
            this.notUnderstoodCount.put(qName, 1);
        }
        else {
            this.notUnderstoodCount.put(qName, count + 1);
        }
        if (this.understoodHeaders != null) {
            this.understoodHeaders.remove(qName);
        }
    }
    
    private SOAPHeader ensureSOAPHeader() {
        try {
            final SOAPHeader header = this.sm.getSOAPPart().getEnvelope().getHeader();
            if (header != null) {
                return header;
            }
            return this.sm.getSOAPPart().getEnvelope().addHeader();
        }
        catch (final Exception e) {
            return null;
        }
    }
    
    private boolean isNonSAAJHeader(final Header header) {
        return !(header instanceof SAAJHeader);
    }
    
    private void addNonSAAJHeader(final SOAPHeaderElement headerElem, final Header header) {
        if (this.nonSAAJHeaders == null) {
            this.nonSAAJHeaders = new HashMap<SOAPHeaderElement, Header>();
        }
        this.nonSAAJHeaders.put(headerElem, header);
    }
    
    private void removeNonSAAJHeader(final SOAPHeaderElement headerElem) {
        if (this.nonSAAJHeaders != null) {
            this.nonSAAJHeaders.remove(headerElem);
        }
    }
    
    @Override
    public boolean addOrReplace(final Header header) {
        this.remove(header.getNamespaceURI(), header.getLocalPart());
        return this.add(header);
    }
    
    @Override
    public void replace(final Header old, final Header header) {
        if (this.remove(old.getNamespaceURI(), old.getLocalPart()) == null) {
            throw new IllegalArgumentException();
        }
        this.add(header);
    }
    
    @Override
    public Set<QName> getUnderstoodHeaders() {
        return this.understoodHeaders;
    }
    
    @Override
    public Set<QName> getNotUnderstoodHeaders(final Set<String> roles, final Set<QName> knownHeaders, final WSBinding binding) {
        final Set<QName> notUnderstoodHeaderNames = new HashSet<QName>();
        if (this.notUnderstoodCount == null) {
            return notUnderstoodHeaderNames;
        }
        for (final QName headerName : this.notUnderstoodCount.keySet()) {
            final int count = this.notUnderstoodCount.get(headerName);
            if (count <= 0) {
                continue;
            }
            final SOAPHeaderElement hdrElem = this.find(headerName);
            if (!hdrElem.getMustUnderstand()) {
                continue;
            }
            final SAAJHeader hdr = new SAAJHeader(hdrElem);
            boolean understood = false;
            if (roles != null) {
                understood = !roles.contains(hdr.getRole(this.soapVersion));
            }
            if (understood) {
                continue;
            }
            if (binding != null && binding instanceof SOAPBindingImpl) {
                understood = ((SOAPBindingImpl)binding).understandsHeader(headerName);
                if (!understood && knownHeaders != null && knownHeaders.contains(headerName)) {
                    understood = true;
                }
            }
            if (understood) {
                continue;
            }
            notUnderstoodHeaderNames.add(headerName);
        }
        return notUnderstoodHeaderNames;
    }
    
    @Override
    public Iterator<Header> getHeaders() {
        final SOAPHeader soapHeader = this.ensureSOAPHeader();
        if (soapHeader == null) {
            return null;
        }
        final Iterator allHeaders = soapHeader.examineAllHeaderElements();
        return new HeaderReadIterator(allHeaders, null, null);
    }
    
    @Override
    public boolean hasHeaders() {
        final SOAPHeader soapHeader = this.ensureSOAPHeader();
        if (soapHeader == null) {
            return false;
        }
        final Iterator allHeaders = soapHeader.examineAllHeaderElements();
        return allHeaders.hasNext();
    }
    
    @Override
    public List<Header> asList() {
        final SOAPHeader soapHeader = this.ensureSOAPHeader();
        if (soapHeader == null) {
            return Collections.emptyList();
        }
        final Iterator allHeaders = soapHeader.examineAllHeaderElements();
        final List<Header> headers = new ArrayList<Header>();
        while (allHeaders.hasNext()) {
            final SOAPHeaderElement nextHdr = allHeaders.next();
            headers.add(new SAAJHeader(nextHdr));
        }
        return headers;
    }
    
    private static class HeaderReadIterator implements Iterator<Header>
    {
        SOAPHeaderElement current;
        Iterator soapHeaders;
        String myNsUri;
        String myLocalName;
        
        public HeaderReadIterator(final Iterator allHeaders, final String nsUri, final String localName) {
            this.soapHeaders = allHeaders;
            this.myNsUri = nsUri;
            this.myLocalName = localName;
        }
        
        @Override
        public boolean hasNext() {
            if (this.current == null) {
                this.advance();
            }
            return this.current != null;
        }
        
        @Override
        public Header next() {
            if (!this.hasNext()) {
                return null;
            }
            if (this.current == null) {
                return null;
            }
            final SAAJHeader ret = new SAAJHeader(this.current);
            this.current = null;
            return ret;
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
        
        private void advance() {
            while (this.soapHeaders.hasNext()) {
                final SOAPHeaderElement nextHdr = this.soapHeaders.next();
                if (nextHdr != null && (this.myNsUri == null || nextHdr.getNamespaceURI().equals(this.myNsUri)) && (this.myLocalName == null || nextHdr.getLocalName().equals(this.myLocalName))) {
                    this.current = nextHdr;
                    return;
                }
            }
            this.current = null;
        }
    }
}
