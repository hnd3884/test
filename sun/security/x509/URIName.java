package sun.security.x509;

import sun.security.util.DerOutputStream;
import java.net.URISyntaxException;
import java.io.IOException;
import sun.security.util.DerValue;
import java.net.URI;

public class URIName implements GeneralNameInterface
{
    private URI uri;
    private String host;
    private DNSName hostDNS;
    private IPAddressName hostIP;
    
    public URIName(final DerValue derValue) throws IOException {
        this(derValue.getIA5String());
    }
    
    public URIName(final String s) throws IOException {
        try {
            this.uri = new URI(s);
        }
        catch (final URISyntaxException ex) {
            throw new IOException("invalid URI name:" + s, ex);
        }
        if (this.uri.getScheme() == null) {
            throw new IOException("URI name must include scheme:" + s);
        }
        this.host = this.uri.getHost();
        if (this.host != null) {
            if (this.host.charAt(0) == '[') {
                final String substring = this.host.substring(1, this.host.length() - 1);
                try {
                    this.hostIP = new IPAddressName(substring);
                }
                catch (final IOException ex2) {
                    throw new IOException("invalid URI name (host portion is not a valid IPv6 address):" + s);
                }
            }
            else {
                try {
                    this.hostDNS = new DNSName(this.host);
                }
                catch (final IOException ex3) {
                    try {
                        this.hostIP = new IPAddressName(this.host);
                    }
                    catch (final Exception ex4) {
                        throw new IOException("invalid URI name (host portion is not a valid DNSName, IPv4 address, or IPv6 address):" + s);
                    }
                }
            }
        }
    }
    
    public static URIName nameConstraint(final DerValue derValue) throws IOException {
        final String ia5String = derValue.getIA5String();
        URI uri;
        try {
            uri = new URI(ia5String);
        }
        catch (final URISyntaxException ex) {
            throw new IOException("invalid URI name constraint:" + ia5String, ex);
        }
        if (uri.getScheme() == null) {
            final String schemeSpecificPart = uri.getSchemeSpecificPart();
            try {
                DNSName dnsName;
                if (schemeSpecificPart.startsWith(".")) {
                    dnsName = new DNSName(schemeSpecificPart.substring(1));
                }
                else {
                    dnsName = new DNSName(schemeSpecificPart);
                }
                return new URIName(uri, schemeSpecificPart, dnsName);
            }
            catch (final IOException ex2) {
                throw new IOException("invalid URI name constraint:" + ia5String, ex2);
            }
        }
        throw new IOException("invalid URI name constraint (should not include scheme):" + ia5String);
    }
    
    URIName(final URI uri, final String host, final DNSName hostDNS) {
        this.uri = uri;
        this.host = host;
        this.hostDNS = hostDNS;
    }
    
    @Override
    public int getType() {
        return 6;
    }
    
    @Override
    public void encode(final DerOutputStream derOutputStream) throws IOException {
        derOutputStream.putIA5String(this.uri.toASCIIString());
    }
    
    @Override
    public String toString() {
        return "URIName: " + this.uri.toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof URIName && this.uri.equals(((URIName)o).getURI()));
    }
    
    public URI getURI() {
        return this.uri;
    }
    
    public String getName() {
        return this.uri.toString();
    }
    
    public String getScheme() {
        return this.uri.getScheme();
    }
    
    public String getHost() {
        return this.host;
    }
    
    public Object getHostObject() {
        if (this.hostIP != null) {
            return this.hostIP;
        }
        return this.hostDNS;
    }
    
    @Override
    public int hashCode() {
        return this.uri.hashCode();
    }
    
    @Override
    public int constrains(final GeneralNameInterface generalNameInterface) throws UnsupportedOperationException {
        int constrains;
        if (generalNameInterface == null) {
            constrains = -1;
        }
        else if (generalNameInterface.getType() != 6) {
            constrains = -1;
        }
        else {
            final String host = ((URIName)generalNameInterface).getHost();
            if (host.equalsIgnoreCase(this.host)) {
                constrains = 0;
            }
            else {
                final Object hostObject = ((URIName)generalNameInterface).getHostObject();
                if (this.hostDNS == null || !(hostObject instanceof DNSName)) {
                    constrains = 3;
                }
                else {
                    final boolean b = this.host.charAt(0) == '.';
                    final boolean b2 = host.charAt(0) == '.';
                    constrains = this.hostDNS.constrains((GeneralNameInterface)hostObject);
                    if (!b && !b2 && (constrains == 2 || constrains == 1)) {
                        constrains = 3;
                    }
                    if (b != b2 && constrains == 0) {
                        if (b) {
                            constrains = 2;
                        }
                        else {
                            constrains = 1;
                        }
                    }
                }
            }
        }
        return constrains;
    }
    
    @Override
    public int subtreeDepth() throws UnsupportedOperationException {
        DNSName dnsName;
        try {
            dnsName = new DNSName(this.host);
        }
        catch (final IOException ex) {
            throw new UnsupportedOperationException(ex.getMessage());
        }
        return dnsName.subtreeDepth();
    }
}
