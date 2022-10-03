package com.unboundid.util.ssl.cert;

import java.util.Iterator;
import java.util.Collection;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import java.util.Collections;
import com.unboundid.asn1.ASN1IA5String;
import com.unboundid.asn1.ASN1ObjectIdentifier;
import com.unboundid.asn1.ASN1Sequence;
import java.util.ArrayList;
import com.unboundid.util.OID;
import com.unboundid.util.ObjectPair;
import java.net.InetAddress;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.asn1.ASN1Element;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class GeneralNames implements Serializable
{
    private static final byte NAME_TYPE_OTHER_NAME = -96;
    private static final byte NAME_TYPE_RFC_822_NAME = -127;
    private static final byte NAME_TYPE_DNS_NAME = -126;
    private static final byte NAME_TYPE_X400_ADDRESS = -93;
    private static final byte NAME_TYPE_DIRECTORY_NAME = -92;
    private static final byte NAME_TYPE_EDI_PARTY_NAME = -91;
    private static final byte NAME_TYPE_UNIFORM_RESOURCE_IDENTIFIER = -122;
    private static final byte NAME_TYPE_IP_ADDRESS = -121;
    private static final byte NAME_TYPE_REGISTERED_ID = -120;
    private static final byte NAME_TYPE_OTHER_NAME_VALUE = -96;
    private static final long serialVersionUID = -8789437423467093314L;
    private final List<ASN1Element> ediPartyNames;
    private final List<ASN1Element> x400Addresses;
    private final List<DN> directoryNames;
    private final List<InetAddress> ipAddresses;
    private final List<ObjectPair<OID, ASN1Element>> otherNames;
    private final List<OID> registeredIDs;
    private final List<String> dnsNames;
    private final List<String> rfc822Names;
    private final List<String> uniformResourceIdentifiers;
    
    GeneralNames(final List<ObjectPair<OID, ASN1Element>> otherNames, final List<String> rfc822Names, final List<String> dnsNames, final List<ASN1Element> x400Addresses, final List<DN> directoryNames, final List<ASN1Element> ediPartyNames, final List<String> uniformResourceIdentifiers, final List<InetAddress> ipAddresses, final List<OID> registeredIDs) {
        this.otherNames = otherNames;
        this.rfc822Names = rfc822Names;
        this.dnsNames = dnsNames;
        this.x400Addresses = x400Addresses;
        this.directoryNames = directoryNames;
        this.ediPartyNames = ediPartyNames;
        this.uniformResourceIdentifiers = uniformResourceIdentifiers;
        this.ipAddresses = ipAddresses;
        this.registeredIDs = registeredIDs;
    }
    
    GeneralNames(final ASN1Element element) throws CertException {
        try {
            final ASN1Element[] elements = element.decodeAsSequence().elements();
            final ArrayList<ASN1Element> ediPartyList = new ArrayList<ASN1Element>(elements.length);
            final ArrayList<ASN1Element> x400AddressList = new ArrayList<ASN1Element>(elements.length);
            final ArrayList<DN> directoryNameList = new ArrayList<DN>(elements.length);
            final ArrayList<InetAddress> ipAddressList = new ArrayList<InetAddress>(elements.length);
            final ArrayList<ObjectPair<OID, ASN1Element>> otherNameList = new ArrayList<ObjectPair<OID, ASN1Element>>(elements.length);
            final ArrayList<OID> registeredIDList = new ArrayList<OID>(elements.length);
            final ArrayList<String> dnsNameList = new ArrayList<String>(elements.length);
            final ArrayList<String> rfc822NameList = new ArrayList<String>(elements.length);
            final ArrayList<String> uriList = new ArrayList<String>(elements.length);
            for (final ASN1Element e : elements) {
                switch (e.getType()) {
                    case -96: {
                        final ASN1Element[] otherNameElements = ASN1Sequence.decodeAsSequence(e).elements();
                        final OID otherNameOID = ASN1ObjectIdentifier.decodeAsObjectIdentifier(otherNameElements[0]).getOID();
                        final ASN1Element otherNameValue = ASN1Element.decode(otherNameElements[1].getValue());
                        otherNameList.add(new ObjectPair<OID, ASN1Element>(otherNameOID, otherNameValue));
                        break;
                    }
                    case -127: {
                        rfc822NameList.add(ASN1IA5String.decodeAsIA5String(e).stringValue());
                        break;
                    }
                    case -126: {
                        dnsNameList.add(ASN1IA5String.decodeAsIA5String(e).stringValue());
                        break;
                    }
                    case -93: {
                        x400AddressList.add(e);
                        break;
                    }
                    case -92: {
                        directoryNameList.add(X509Certificate.decodeName(e));
                        break;
                    }
                    case -91: {
                        ediPartyList.add(e);
                        break;
                    }
                    case -122: {
                        uriList.add(ASN1IA5String.decodeAsIA5String(e).stringValue());
                        break;
                    }
                    case -121: {
                        ipAddressList.add(InetAddress.getByAddress(e.getValue()));
                        break;
                    }
                    case -120: {
                        registeredIDList.add(ASN1ObjectIdentifier.decodeAsObjectIdentifier(e).getOID());
                        break;
                    }
                }
            }
            this.ediPartyNames = Collections.unmodifiableList((List<? extends ASN1Element>)ediPartyList);
            this.otherNames = Collections.unmodifiableList((List<? extends ObjectPair<OID, ASN1Element>>)otherNameList);
            this.registeredIDs = Collections.unmodifiableList((List<? extends OID>)registeredIDList);
            this.x400Addresses = Collections.unmodifiableList((List<? extends ASN1Element>)x400AddressList);
            this.directoryNames = Collections.unmodifiableList((List<? extends DN>)directoryNameList);
            this.ipAddresses = Collections.unmodifiableList((List<? extends InetAddress>)ipAddressList);
            this.dnsNames = Collections.unmodifiableList((List<? extends String>)dnsNameList);
            this.rfc822Names = Collections.unmodifiableList((List<? extends String>)rfc822NameList);
            this.uniformResourceIdentifiers = Collections.unmodifiableList((List<? extends String>)uriList);
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new CertException(CertMessages.ERR_GENERAL_NAMES_CANNOT_PARSE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    ASN1Element encode() throws CertException {
        try {
            final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(10);
            for (final ObjectPair<OID, ASN1Element> otherName : this.otherNames) {
                elements.add(new ASN1Sequence((byte)(-96), new ASN1Element[] { new ASN1ObjectIdentifier(otherName.getFirst()), new ASN1Element((byte)(-96), otherName.getSecond().encode()) }));
            }
            for (final String rfc822Name : this.rfc822Names) {
                elements.add(new ASN1IA5String((byte)(-127), rfc822Name));
            }
            for (final String dnsName : this.dnsNames) {
                elements.add(new ASN1IA5String((byte)(-126), dnsName));
            }
            for (final ASN1Element x400Address : this.x400Addresses) {
                elements.add(new ASN1Element((byte)(-93), x400Address.getValue()));
            }
            for (final DN directoryName : this.directoryNames) {
                elements.add(new ASN1Element((byte)(-92), X509Certificate.encodeName(directoryName).getValue()));
            }
            for (final ASN1Element ediPartyName : this.ediPartyNames) {
                elements.add(new ASN1Element((byte)(-91), ediPartyName.getValue()));
            }
            for (final String uri : this.uniformResourceIdentifiers) {
                elements.add(new ASN1IA5String((byte)(-122), uri));
            }
            for (final InetAddress ipAddress : this.ipAddresses) {
                elements.add(new ASN1OctetString((byte)(-121), ipAddress.getAddress()));
            }
            for (final OID registeredID : this.registeredIDs) {
                elements.add(new ASN1ObjectIdentifier((byte)(-120), registeredID));
            }
            return new ASN1Sequence(elements);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new CertException(CertMessages.ERR_GENERAL_NAMES_CANNOT_ENCODE.get(this.toString(), StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    public List<ObjectPair<OID, ASN1Element>> getOtherNames() {
        return this.otherNames;
    }
    
    public List<String> getRFC822Names() {
        return this.rfc822Names;
    }
    
    public List<String> getDNSNames() {
        return this.dnsNames;
    }
    
    public List<ASN1Element> getX400Addresses() {
        return this.x400Addresses;
    }
    
    public List<DN> getDirectoryNames() {
        return this.directoryNames;
    }
    
    public List<ASN1Element> getEDIPartyNames() {
        return this.ediPartyNames;
    }
    
    public List<String> getUniformResourceIdentifiers() {
        return this.uniformResourceIdentifiers;
    }
    
    public List<InetAddress> getIPAddresses() {
        return this.ipAddresses;
    }
    
    public List<OID> getRegisteredIDs() {
        return this.registeredIDs;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("GeneralNames(");
        boolean appended = false;
        if (!this.dnsNames.isEmpty()) {
            buffer.append("dnsNames={");
            final Iterator<String> iterator = this.dnsNames.iterator();
            while (iterator.hasNext()) {
                buffer.append('\'');
                buffer.append(iterator.next());
                buffer.append('\'');
                if (iterator.hasNext()) {
                    buffer.append(',');
                }
            }
            buffer.append('}');
            appended = true;
        }
        if (!this.ipAddresses.isEmpty()) {
            if (appended) {
                buffer.append(", ");
            }
            buffer.append("ipAddresses={");
            final Iterator<InetAddress> iterator2 = this.ipAddresses.iterator();
            while (iterator2.hasNext()) {
                buffer.append('\'');
                buffer.append(iterator2.next().getHostAddress());
                buffer.append('\'');
                if (iterator2.hasNext()) {
                    buffer.append(',');
                }
            }
            buffer.append('}');
            appended = true;
        }
        if (!this.rfc822Names.isEmpty()) {
            if (appended) {
                buffer.append(", ");
            }
            buffer.append("rfc822Names={");
            final Iterator<String> iterator = this.rfc822Names.iterator();
            while (iterator.hasNext()) {
                buffer.append('\'');
                buffer.append(iterator.next());
                buffer.append('\'');
                if (iterator.hasNext()) {
                    buffer.append(',');
                }
            }
            buffer.append('}');
            appended = true;
        }
        if (!this.directoryNames.isEmpty()) {
            if (appended) {
                buffer.append(", ");
            }
            buffer.append("directoryNames={");
            final Iterator<DN> iterator3 = this.directoryNames.iterator();
            while (iterator3.hasNext()) {
                buffer.append('\'');
                buffer.append(iterator3.next());
                buffer.append('\'');
                if (iterator3.hasNext()) {
                    buffer.append(',');
                }
            }
            buffer.append('}');
            appended = true;
        }
        if (!this.uniformResourceIdentifiers.isEmpty()) {
            if (appended) {
                buffer.append(", ");
            }
            buffer.append("uniformResourceIdentifiers={");
            final Iterator<String> iterator = this.uniformResourceIdentifiers.iterator();
            while (iterator.hasNext()) {
                buffer.append('\'');
                buffer.append(iterator.next());
                buffer.append('\'');
                if (iterator.hasNext()) {
                    buffer.append(',');
                }
            }
            buffer.append('}');
            appended = true;
        }
        if (!this.registeredIDs.isEmpty()) {
            if (appended) {
                buffer.append(", ");
            }
            buffer.append("registeredIDs={");
            final Iterator<OID> iterator4 = this.registeredIDs.iterator();
            while (iterator4.hasNext()) {
                buffer.append('\'');
                buffer.append(iterator4.next());
                buffer.append('\'');
                if (iterator4.hasNext()) {
                    buffer.append(',');
                }
            }
            buffer.append('}');
            appended = true;
        }
        if (!this.otherNames.isEmpty()) {
            if (appended) {
                buffer.append(", ");
            }
            buffer.append("otherNameCount=");
            buffer.append(this.otherNames.size());
        }
        if (!this.x400Addresses.isEmpty()) {
            if (appended) {
                buffer.append(", ");
            }
            buffer.append("x400AddressCount=");
            buffer.append(this.x400Addresses.size());
        }
        if (!this.ediPartyNames.isEmpty()) {
            if (appended) {
                buffer.append(", ");
            }
            buffer.append("ediPartyNameCount=");
            buffer.append(this.ediPartyNames.size());
        }
        buffer.append(')');
    }
}
