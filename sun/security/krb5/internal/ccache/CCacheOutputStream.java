package sun.security.krb5.internal.ccache;

import sun.security.krb5.internal.TicketFlags;
import sun.security.krb5.internal.Ticket;
import sun.security.krb5.Asn1Exception;
import java.io.IOException;
import sun.security.krb5.PrincipalName;
import java.io.OutputStream;
import sun.security.krb5.internal.util.KrbDataOutputStream;

public class CCacheOutputStream extends KrbDataOutputStream implements FileCCacheConstants
{
    public CCacheOutputStream(final OutputStream outputStream) {
        super(outputStream);
    }
    
    public void writeHeader(final PrincipalName principalName, final int n) throws IOException {
        this.write((n & 0xFF00) >> 8);
        this.write(n & 0xFF);
        principalName.writePrincipal(this);
    }
    
    public void addCreds(final Credentials credentials) throws IOException, Asn1Exception {
        credentials.cname.writePrincipal(this);
        credentials.sname.writePrincipal(this);
        credentials.key.writeKey(this);
        this.write32((int)(credentials.authtime.getTime() / 1000L));
        if (credentials.starttime != null) {
            this.write32((int)(credentials.starttime.getTime() / 1000L));
        }
        else {
            this.write32(0);
        }
        this.write32((int)(credentials.endtime.getTime() / 1000L));
        if (credentials.renewTill != null) {
            this.write32((int)(credentials.renewTill.getTime() / 1000L));
        }
        else {
            this.write32(0);
        }
        if (credentials.isEncInSKey) {
            this.write8(1);
        }
        else {
            this.write8(0);
        }
        this.writeFlags(credentials.flags);
        if (credentials.caddr == null) {
            this.write32(0);
        }
        else {
            credentials.caddr.writeAddrs(this);
        }
        if (credentials.authorizationData == null) {
            this.write32(0);
        }
        else {
            credentials.authorizationData.writeAuth(this);
        }
        this.writeTicket(credentials.ticket);
        this.writeTicket(credentials.secondTicket);
    }
    
    public void addConfigEntry(final PrincipalName principalName, final CredentialsCache.ConfigEntry configEntry) throws IOException {
        principalName.writePrincipal(this);
        configEntry.getSName().writePrincipal(this);
        this.write16(0);
        this.write16(0);
        this.write32(0);
        this.write32(0);
        this.write32(0);
        this.write32(0);
        this.write32(0);
        this.write8(0);
        this.write32(0);
        this.write32(0);
        this.write32(0);
        this.write32(configEntry.getData().length);
        this.write(configEntry.getData());
        this.write32(0);
    }
    
    void writeTicket(final Ticket ticket) throws IOException, Asn1Exception {
        if (ticket == null) {
            this.write32(0);
        }
        else {
            final byte[] asn1Encode = ticket.asn1Encode();
            this.write32(asn1Encode.length);
            this.write(asn1Encode, 0, asn1Encode.length);
        }
    }
    
    void writeFlags(final TicketFlags ticketFlags) throws IOException {
        int n = 0;
        final boolean[] booleanArray = ticketFlags.toBooleanArray();
        if (booleanArray[1]) {
            n |= 0x40000000;
        }
        if (booleanArray[2]) {
            n |= 0x20000000;
        }
        if (booleanArray[3]) {
            n |= 0x10000000;
        }
        if (booleanArray[4]) {
            n |= 0x8000000;
        }
        if (booleanArray[5]) {
            n |= 0x4000000;
        }
        if (booleanArray[6]) {
            n |= 0x2000000;
        }
        if (booleanArray[7]) {
            n |= 0x1000000;
        }
        if (booleanArray[8]) {
            n |= 0x800000;
        }
        if (booleanArray[9]) {
            n |= 0x400000;
        }
        if (booleanArray[10]) {
            n |= 0x200000;
        }
        if (booleanArray[11]) {
            n |= 0x100000;
        }
        this.write32(n);
    }
}
