package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.util.Iterator;
import java.util.Collection;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

final class ProtocolList
{
    private final ArrayList<ProtocolVersion> protocols;
    private String[] protocolNames;
    final ProtocolVersion min;
    final ProtocolVersion max;
    final ProtocolVersion helloVersion;
    
    ProtocolList(final String[] names) {
        this(convert(names));
    }
    
    ProtocolList(final ArrayList<ProtocolVersion> versions) {
        this.protocols = versions;
        if (this.protocols.size() == 1 && this.protocols.contains(ProtocolVersion.SSL20Hello)) {
            throw new IllegalArgumentException("SSLv2Hello cannot be enabled unless at least one other supported version is also enabled.");
        }
        if (this.protocols.size() != 0) {
            Collections.sort(this.protocols);
            this.min = this.protocols.get(0);
            this.max = this.protocols.get(this.protocols.size() - 1);
            this.helloVersion = this.protocols.get(0);
        }
        else {
            this.min = ProtocolVersion.NONE;
            this.max = ProtocolVersion.NONE;
            this.helloVersion = ProtocolVersion.NONE;
        }
    }
    
    private static ArrayList<ProtocolVersion> convert(final String[] names) {
        if (names == null) {
            throw new IllegalArgumentException("Protocols may not be null");
        }
        final ArrayList<ProtocolVersion> versions = new ArrayList<ProtocolVersion>(names.length);
        for (int i = 0; i < names.length; ++i) {
            final ProtocolVersion version = ProtocolVersion.valueOf(names[i]);
            if (!versions.contains(version)) {
                versions.add(version);
            }
        }
        return versions;
    }
    
    boolean contains(final ProtocolVersion protocolVersion) {
        return protocolVersion != ProtocolVersion.SSL20Hello && this.protocols.contains(protocolVersion);
    }
    
    Collection<ProtocolVersion> collection() {
        return this.protocols;
    }
    
    ProtocolVersion selectProtocolVersion(final ProtocolVersion protocolVersion) {
        ProtocolVersion selectedVersion = null;
        for (final ProtocolVersion pv : this.protocols) {
            if (pv.v > protocolVersion.v) {
                break;
            }
            selectedVersion = pv;
        }
        return selectedVersion;
    }
    
    synchronized String[] toStringArray() {
        if (this.protocolNames == null) {
            this.protocolNames = new String[this.protocols.size()];
            int i = 0;
            for (final ProtocolVersion version : this.protocols) {
                this.protocolNames[i++] = version.name;
            }
        }
        return this.protocolNames.clone();
    }
    
    @Override
    public String toString() {
        return this.protocols.toString();
    }
}
