package com.sun.xml.internal.ws.api.message;

import java.util.List;
import com.sun.xml.internal.ws.binding.SOAPBindingImpl;
import java.util.HashSet;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import java.util.NoSuchElementException;
import com.sun.istack.internal.Nullable;
import com.sun.istack.internal.NotNull;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import javax.xml.namespace.QName;
import java.util.Collection;
import com.sun.xml.internal.ws.api.SOAPVersion;
import java.util.BitSet;
import java.util.ArrayList;

public class HeaderList extends ArrayList<Header> implements MessageHeaders
{
    private static final long serialVersionUID = -6358045781349627237L;
    private int understoodBits;
    private BitSet moreUnderstoodBits;
    private SOAPVersion soapVersion;
    
    @Deprecated
    public HeaderList() {
        this.moreUnderstoodBits = null;
    }
    
    public HeaderList(final SOAPVersion soapVersion) {
        this.moreUnderstoodBits = null;
        this.soapVersion = soapVersion;
    }
    
    public HeaderList(final HeaderList that) {
        super(that);
        this.moreUnderstoodBits = null;
        this.understoodBits = that.understoodBits;
        if (that.moreUnderstoodBits != null) {
            this.moreUnderstoodBits = (BitSet)that.moreUnderstoodBits.clone();
        }
    }
    
    public HeaderList(final MessageHeaders that) {
        super(that.asList());
        this.moreUnderstoodBits = null;
        if (that instanceof HeaderList) {
            final HeaderList hThat = (HeaderList)that;
            this.understoodBits = hThat.understoodBits;
            if (hThat.moreUnderstoodBits != null) {
                this.moreUnderstoodBits = (BitSet)hThat.moreUnderstoodBits.clone();
            }
        }
        else {
            final Set<QName> understood = that.getUnderstoodHeaders();
            if (understood != null) {
                for (final QName qname : understood) {
                    this.understood(qname);
                }
            }
        }
    }
    
    @Override
    public int size() {
        return super.size();
    }
    
    @Override
    public boolean hasHeaders() {
        return !this.isEmpty();
    }
    
    @Deprecated
    public void addAll(final Header... headers) {
        this.addAll(Arrays.asList(headers));
    }
    
    @Override
    public Header get(final int index) {
        return super.get(index);
    }
    
    public void understood(final int index) {
        if (index >= this.size()) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        if (index < 32) {
            this.understoodBits |= 1 << index;
        }
        else {
            if (this.moreUnderstoodBits == null) {
                this.moreUnderstoodBits = new BitSet();
            }
            this.moreUnderstoodBits.set(index - 32);
        }
    }
    
    public boolean isUnderstood(final int index) {
        if (index >= this.size()) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        if (index < 32) {
            return this.understoodBits == (this.understoodBits | 1 << index);
        }
        return this.moreUnderstoodBits != null && this.moreUnderstoodBits.get(index - 32);
    }
    
    @Override
    @Deprecated
    public void understood(@NotNull final Header header) {
        for (int sz = this.size(), i = 0; i < sz; ++i) {
            if (this.get(i) == header) {
                this.understood(i);
                return;
            }
        }
        throw new IllegalArgumentException();
    }
    
    @Nullable
    @Override
    public Header get(@NotNull final String nsUri, @NotNull final String localName, final boolean markAsUnderstood) {
        for (int len = this.size(), i = 0; i < len; ++i) {
            final Header h = this.get(i);
            if (h.getLocalPart().equals(localName) && h.getNamespaceURI().equals(nsUri)) {
                if (markAsUnderstood) {
                    this.understood(i);
                }
                return h;
            }
        }
        return null;
    }
    
    @Deprecated
    public Header get(final String nsUri, final String localName) {
        return this.get(nsUri, localName, true);
    }
    
    @Nullable
    @Override
    public Header get(@NotNull final QName name, final boolean markAsUnderstood) {
        return this.get(name.getNamespaceURI(), name.getLocalPart(), markAsUnderstood);
    }
    
    @Nullable
    @Deprecated
    public Header get(@NotNull final QName name) {
        return this.get(name, true);
    }
    
    @Deprecated
    public Iterator<Header> getHeaders(final String nsUri, final String localName) {
        return this.getHeaders(nsUri, localName, true);
    }
    
    @NotNull
    @Override
    public Iterator<Header> getHeaders(@NotNull final String nsUri, @NotNull final String localName, final boolean markAsUnderstood) {
        return new Iterator<Header>() {
            int idx = 0;
            Header next;
            
            @Override
            public boolean hasNext() {
                if (this.next == null) {
                    this.fetch();
                }
                return this.next != null;
            }
            
            @Override
            public Header next() {
                if (this.next == null) {
                    this.fetch();
                    if (this.next == null) {
                        throw new NoSuchElementException();
                    }
                }
                if (markAsUnderstood) {
                    assert HeaderList.this.get(this.idx - 1) == this.next;
                    HeaderList.this.understood(this.idx - 1);
                }
                final Header r = this.next;
                this.next = null;
                return r;
            }
            
            private void fetch() {
                while (this.idx < HeaderList.this.size()) {
                    final Header h = HeaderList.this.get(this.idx++);
                    if (h.getLocalPart().equals(localName) && h.getNamespaceURI().equals(nsUri)) {
                        this.next = h;
                        break;
                    }
                }
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    @NotNull
    @Override
    public Iterator<Header> getHeaders(@NotNull final QName headerName, final boolean markAsUnderstood) {
        return this.getHeaders(headerName.getNamespaceURI(), headerName.getLocalPart(), markAsUnderstood);
    }
    
    @NotNull
    @Deprecated
    public Iterator<Header> getHeaders(@NotNull final String nsUri) {
        return this.getHeaders(nsUri, true);
    }
    
    @NotNull
    @Override
    public Iterator<Header> getHeaders(@NotNull final String nsUri, final boolean markAsUnderstood) {
        return new Iterator<Header>() {
            int idx = 0;
            Header next;
            
            @Override
            public boolean hasNext() {
                if (this.next == null) {
                    this.fetch();
                }
                return this.next != null;
            }
            
            @Override
            public Header next() {
                if (this.next == null) {
                    this.fetch();
                    if (this.next == null) {
                        throw new NoSuchElementException();
                    }
                }
                if (markAsUnderstood) {
                    assert HeaderList.this.get(this.idx - 1) == this.next;
                    HeaderList.this.understood(this.idx - 1);
                }
                final Header r = this.next;
                this.next = null;
                return r;
            }
            
            private void fetch() {
                while (this.idx < HeaderList.this.size()) {
                    final Header h = HeaderList.this.get(this.idx++);
                    if (h.getNamespaceURI().equals(nsUri)) {
                        this.next = h;
                        break;
                    }
                }
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    public String getTo(final AddressingVersion av, final SOAPVersion sv) {
        return AddressingUtils.getTo(this, av, sv);
    }
    
    public String getAction(@NotNull final AddressingVersion av, @NotNull final SOAPVersion sv) {
        return AddressingUtils.getAction(this, av, sv);
    }
    
    public WSEndpointReference getReplyTo(@NotNull final AddressingVersion av, @NotNull final SOAPVersion sv) {
        return AddressingUtils.getReplyTo(this, av, sv);
    }
    
    public WSEndpointReference getFaultTo(@NotNull final AddressingVersion av, @NotNull final SOAPVersion sv) {
        return AddressingUtils.getFaultTo(this, av, sv);
    }
    
    public String getMessageID(@NotNull final AddressingVersion av, @NotNull final SOAPVersion sv) {
        return AddressingUtils.getMessageID(this, av, sv);
    }
    
    public String getRelatesTo(@NotNull final AddressingVersion av, @NotNull final SOAPVersion sv) {
        return AddressingUtils.getRelatesTo(this, av, sv);
    }
    
    public void fillRequestAddressingHeaders(final Packet packet, final AddressingVersion av, final SOAPVersion sv, final boolean oneway, final String action, final boolean mustUnderstand) {
        AddressingUtils.fillRequestAddressingHeaders(this, packet, av, sv, oneway, action, mustUnderstand);
    }
    
    public void fillRequestAddressingHeaders(final Packet packet, final AddressingVersion av, final SOAPVersion sv, final boolean oneway, final String action) {
        AddressingUtils.fillRequestAddressingHeaders(this, packet, av, sv, oneway, action);
    }
    
    public void fillRequestAddressingHeaders(final WSDLPort wsdlPort, @NotNull final WSBinding binding, final Packet packet) {
        AddressingUtils.fillRequestAddressingHeaders(this, wsdlPort, binding, packet);
    }
    
    @Override
    public boolean add(final Header header) {
        return super.add(header);
    }
    
    @Nullable
    @Override
    public Header remove(@NotNull final String nsUri, @NotNull final String localName) {
        for (int len = this.size(), i = 0; i < len; ++i) {
            final Header h = this.get(i);
            if (h.getLocalPart().equals(localName) && h.getNamespaceURI().equals(nsUri)) {
                return this.remove(i);
            }
        }
        return null;
    }
    
    @Override
    public boolean addOrReplace(final Header header) {
        for (int i = 0; i < this.size(); ++i) {
            final Header hdr = this.get(i);
            if (hdr.getNamespaceURI().equals(header.getNamespaceURI()) && hdr.getLocalPart().equals(header.getLocalPart())) {
                this.removeInternal(i);
                this.addInternal(i, header);
                return true;
            }
        }
        return this.add(header);
    }
    
    @Override
    public void replace(final Header old, final Header header) {
        for (int i = 0; i < this.size(); ++i) {
            final Header hdr = this.get(i);
            if (hdr.getNamespaceURI().equals(header.getNamespaceURI()) && hdr.getLocalPart().equals(header.getLocalPart())) {
                this.removeInternal(i);
                this.addInternal(i, header);
                return;
            }
        }
        throw new IllegalArgumentException();
    }
    
    protected void addInternal(final int index, final Header header) {
        super.add(index, header);
    }
    
    protected Header removeInternal(final int index) {
        return super.remove(index);
    }
    
    @Nullable
    @Override
    public Header remove(@NotNull final QName name) {
        return this.remove(name.getNamespaceURI(), name.getLocalPart());
    }
    
    @Override
    public Header remove(final int index) {
        this.removeUnderstoodBit(index);
        return super.remove(index);
    }
    
    private void removeUnderstoodBit(int index) {
        assert index < this.size();
        if (index < 32) {
            final int shiftedUpperBits = this.understoodBits >>> -31 + index << index;
            final int lowerBits = this.understoodBits << -index >>> 31 - index >>> 1;
            this.understoodBits = (shiftedUpperBits | lowerBits);
            if (this.moreUnderstoodBits != null && this.moreUnderstoodBits.cardinality() > 0) {
                if (this.moreUnderstoodBits.get(0)) {
                    this.understoodBits |= Integer.MIN_VALUE;
                }
                this.moreUnderstoodBits.clear(0);
                for (int i = this.moreUnderstoodBits.nextSetBit(1); i > 0; i = this.moreUnderstoodBits.nextSetBit(i + 1)) {
                    this.moreUnderstoodBits.set(i - 1);
                    this.moreUnderstoodBits.clear(i);
                }
            }
        }
        else if (this.moreUnderstoodBits != null && this.moreUnderstoodBits.cardinality() > 0) {
            index -= 32;
            this.moreUnderstoodBits.clear(index);
            for (int j = this.moreUnderstoodBits.nextSetBit(index); j >= 1; j = this.moreUnderstoodBits.nextSetBit(j + 1)) {
                this.moreUnderstoodBits.set(j - 1);
                this.moreUnderstoodBits.clear(j);
            }
        }
        if (this.size() - 1 <= 33 && this.moreUnderstoodBits != null) {
            this.moreUnderstoodBits = null;
        }
    }
    
    @Override
    public boolean remove(final Object o) {
        if (o != null) {
            for (int index = 0; index < this.size(); ++index) {
                if (o.equals(this.get(index))) {
                    this.remove(index);
                    return true;
                }
            }
        }
        return false;
    }
    
    public Header remove(final Header h) {
        if (this.remove((Object)h)) {
            return h;
        }
        return null;
    }
    
    public static HeaderList copy(final MessageHeaders original) {
        if (original == null) {
            return null;
        }
        return new HeaderList(original);
    }
    
    public static HeaderList copy(final HeaderList original) {
        return copy((MessageHeaders)original);
    }
    
    public void readResponseAddressingHeaders(final WSDLPort wsdlPort, final WSBinding binding) {
    }
    
    @Override
    public void understood(final QName name) {
        this.get(name, true);
    }
    
    @Override
    public void understood(final String nsUri, final String localName) {
        this.get(nsUri, localName, true);
    }
    
    @Override
    public Set<QName> getUnderstoodHeaders() {
        final Set<QName> understoodHdrs = new HashSet<QName>();
        for (int i = 0; i < this.size(); ++i) {
            if (this.isUnderstood(i)) {
                final Header header = this.get(i);
                understoodHdrs.add(new QName(header.getNamespaceURI(), header.getLocalPart()));
            }
        }
        return understoodHdrs;
    }
    
    @Override
    public boolean isUnderstood(final Header header) {
        return this.isUnderstood(header.getNamespaceURI(), header.getLocalPart());
    }
    
    @Override
    public boolean isUnderstood(final String nsUri, final String localName) {
        for (int i = 0; i < this.size(); ++i) {
            final Header h = this.get(i);
            if (h.getLocalPart().equals(localName) && h.getNamespaceURI().equals(nsUri)) {
                return this.isUnderstood(i);
            }
        }
        return false;
    }
    
    @Override
    public boolean isUnderstood(final QName name) {
        return this.isUnderstood(name.getNamespaceURI(), name.getLocalPart());
    }
    
    @Override
    public Set<QName> getNotUnderstoodHeaders(Set<String> roles, final Set<QName> knownHeaders, final WSBinding binding) {
        Set<QName> notUnderstoodHeaders = null;
        if (roles == null) {
            roles = new HashSet<String>();
        }
        final SOAPVersion effectiveSoapVersion = this.getEffectiveSOAPVersion(binding);
        roles.add(effectiveSoapVersion.implicitRole);
        for (int i = 0; i < this.size(); ++i) {
            if (!this.isUnderstood(i)) {
                final Header header = this.get(i);
                if (!header.isIgnorable(effectiveSoapVersion, roles)) {
                    final QName qName = new QName(header.getNamespaceURI(), header.getLocalPart());
                    if (binding == null) {
                        if (notUnderstoodHeaders == null) {
                            notUnderstoodHeaders = new HashSet<QName>();
                        }
                        notUnderstoodHeaders.add(qName);
                    }
                    else if (binding instanceof SOAPBindingImpl && !((SOAPBindingImpl)binding).understandsHeader(qName) && !knownHeaders.contains(qName)) {
                        if (notUnderstoodHeaders == null) {
                            notUnderstoodHeaders = new HashSet<QName>();
                        }
                        notUnderstoodHeaders.add(qName);
                    }
                }
            }
        }
        return notUnderstoodHeaders;
    }
    
    private SOAPVersion getEffectiveSOAPVersion(final WSBinding binding) {
        SOAPVersion mySOAPVersion = (this.soapVersion != null) ? this.soapVersion : binding.getSOAPVersion();
        if (mySOAPVersion == null) {
            mySOAPVersion = SOAPVersion.SOAP_11;
        }
        return mySOAPVersion;
    }
    
    public void setSoapVersion(final SOAPVersion soapVersion) {
        this.soapVersion = soapVersion;
    }
    
    @Override
    public Iterator<Header> getHeaders() {
        return this.iterator();
    }
    
    @Override
    public List<Header> asList() {
        return this;
    }
}
