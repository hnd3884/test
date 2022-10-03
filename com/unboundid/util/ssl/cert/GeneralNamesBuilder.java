package com.unboundid.util.ssl.cert;

import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.util.OID;
import com.unboundid.util.ObjectPair;
import java.net.InetAddress;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.asn1.ASN1Element;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;
import java.io.Serializable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
final class GeneralNamesBuilder implements Serializable
{
    private static final long serialVersionUID = -5005719526010439377L;
    private final List<ASN1Element> ediPartyNames;
    private final List<ASN1Element> x400Addresses;
    private final List<DN> directoryNames;
    private final List<InetAddress> ipAddresses;
    private final List<ObjectPair<OID, ASN1Element>> otherNames;
    private final List<OID> registeredIDs;
    private final List<String> dnsNames;
    private final List<String> rfc822Names;
    private final List<String> uniformResourceIdentifiers;
    
    GeneralNamesBuilder() {
        this.ediPartyNames = new ArrayList<ASN1Element>(5);
        this.x400Addresses = new ArrayList<ASN1Element>(5);
        this.directoryNames = new ArrayList<DN>(5);
        this.ipAddresses = new ArrayList<InetAddress>(5);
        this.otherNames = new ArrayList<ObjectPair<OID, ASN1Element>>(5);
        this.registeredIDs = new ArrayList<OID>(5);
        this.dnsNames = new ArrayList<String>(5);
        this.rfc822Names = new ArrayList<String>(5);
        this.uniformResourceIdentifiers = new ArrayList<String>(5);
    }
    
    List<ObjectPair<OID, ASN1Element>> getOtherNames() {
        return this.otherNames;
    }
    
    GeneralNamesBuilder addOtherName(final OID oid, final ASN1Element value) {
        this.otherNames.add(new ObjectPair<OID, ASN1Element>(oid, value));
        return this;
    }
    
    List<String> getRFC822Names() {
        return this.rfc822Names;
    }
    
    GeneralNamesBuilder addRFC822Name(final String emailAddress) {
        this.rfc822Names.add(emailAddress);
        return this;
    }
    
    List<String> getDNSNames() {
        return this.dnsNames;
    }
    
    GeneralNamesBuilder addDNSName(final String dnsName) {
        this.dnsNames.add(dnsName);
        return this;
    }
    
    List<ASN1Element> getX400Addresses() {
        return this.x400Addresses;
    }
    
    GeneralNamesBuilder addX400Address(final ASN1Element x400Address) {
        this.x400Addresses.add(x400Address);
        return this;
    }
    
    List<DN> getDirectoryNames() {
        return this.directoryNames;
    }
    
    GeneralNamesBuilder addDirectoryName(final DN dn) {
        this.directoryNames.add(dn);
        return this;
    }
    
    List<ASN1Element> getEDIPartyNames() {
        return this.ediPartyNames;
    }
    
    GeneralNamesBuilder addEDIPartyName(final ASN1Element value) {
        this.ediPartyNames.add(value);
        return this;
    }
    
    List<String> getUniformResourceIdentifiers() {
        return this.uniformResourceIdentifiers;
    }
    
    GeneralNamesBuilder addUniformResourceIdentifier(final String uri) {
        this.uniformResourceIdentifiers.add(uri);
        return this;
    }
    
    List<InetAddress> getIPAddresses() {
        return this.ipAddresses;
    }
    
    GeneralNamesBuilder addIPAddress(final InetAddress ipAddress) {
        this.ipAddresses.add(ipAddress);
        return this;
    }
    
    List<OID> getRegisteredIDs() {
        return this.registeredIDs;
    }
    
    GeneralNamesBuilder addRegisteredID(final OID id) {
        this.registeredIDs.add(id);
        return this;
    }
    
    GeneralNames build() {
        return new GeneralNames(Collections.unmodifiableList((List<? extends ObjectPair<OID, ASN1Element>>)new ArrayList<ObjectPair<OID, ASN1Element>>(this.otherNames)), Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(this.rfc822Names)), Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(this.dnsNames)), Collections.unmodifiableList((List<? extends ASN1Element>)new ArrayList<ASN1Element>(this.x400Addresses)), Collections.unmodifiableList((List<? extends DN>)new ArrayList<DN>(this.directoryNames)), Collections.unmodifiableList((List<? extends ASN1Element>)new ArrayList<ASN1Element>(this.ediPartyNames)), Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(this.uniformResourceIdentifiers)), Collections.unmodifiableList((List<? extends InetAddress>)new ArrayList<InetAddress>(this.ipAddresses)), Collections.unmodifiableList((List<? extends OID>)new ArrayList<OID>(this.registeredIDs)));
    }
}
