package javax.security.auth.kerberos;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.Objects;
import sun.misc.HexDumpEncoder;
import java.util.Arrays;
import javax.security.auth.DestroyFailedException;
import java.io.IOException;
import sun.security.krb5.KrbException;
import sun.security.krb5.Credentials;
import javax.security.auth.RefreshFailedException;
import javax.crypto.SecretKey;
import java.net.InetAddress;
import java.util.Date;
import java.io.Serializable;
import javax.security.auth.Refreshable;
import javax.security.auth.Destroyable;

public class KerberosTicket implements Destroyable, Refreshable, Serializable
{
    private static final long serialVersionUID = 7395334370157380539L;
    private static final int FORWARDABLE_TICKET_FLAG = 1;
    private static final int FORWARDED_TICKET_FLAG = 2;
    private static final int PROXIABLE_TICKET_FLAG = 3;
    private static final int PROXY_TICKET_FLAG = 4;
    private static final int POSTDATED_TICKET_FLAG = 6;
    private static final int RENEWABLE_TICKET_FLAG = 8;
    private static final int INITIAL_TICKET_FLAG = 9;
    private static final int NUM_FLAGS = 32;
    private byte[] asn1Encoding;
    private KeyImpl sessionKey;
    private boolean[] flags;
    private Date authTime;
    private Date startTime;
    private Date endTime;
    private Date renewTill;
    private KerberosPrincipal client;
    private KerberosPrincipal server;
    private InetAddress[] clientAddresses;
    transient KerberosPrincipal clientAlias;
    transient KerberosPrincipal serverAlias;
    KerberosTicket proxy;
    private transient boolean destroyed;
    
    public KerberosTicket(final byte[] array, final KerberosPrincipal kerberosPrincipal, final KerberosPrincipal kerberosPrincipal2, final byte[] array2, final int n, final boolean[] array3, final Date date, final Date date2, final Date date3, final Date date4, final InetAddress[] array4) {
        this.clientAlias = null;
        this.serverAlias = null;
        this.proxy = null;
        this.destroyed = false;
        this.init(array, kerberosPrincipal, kerberosPrincipal2, array2, n, array3, date, date2, date3, date4, array4);
    }
    
    private void init(final byte[] array, final KerberosPrincipal kerberosPrincipal, final KerberosPrincipal kerberosPrincipal2, final byte[] array2, final int n, final boolean[] array3, final Date date, final Date date2, final Date date3, final Date date4, final InetAddress[] array4) {
        if (array2 == null) {
            throw new IllegalArgumentException("Session key for ticket cannot be null");
        }
        this.init(array, kerberosPrincipal, kerberosPrincipal2, new KeyImpl(array2, n), array3, date, date2, date3, date4, array4);
    }
    
    private void init(final byte[] array, final KerberosPrincipal client, final KerberosPrincipal server, final KeyImpl sessionKey, final boolean[] array2, final Date date, final Date date2, final Date date3, final Date date4, final InetAddress[] array3) {
        if (array == null) {
            throw new IllegalArgumentException("ASN.1 encoding of ticket cannot be null");
        }
        this.asn1Encoding = array.clone();
        if (client == null) {
            throw new IllegalArgumentException("Client name in ticket cannot be null");
        }
        this.client = client;
        if (server == null) {
            throw new IllegalArgumentException("Server name in ticket cannot be null");
        }
        this.server = server;
        this.sessionKey = sessionKey;
        if (array2 != null) {
            if (array2.length >= 32) {
                this.flags = array2.clone();
            }
            else {
                this.flags = new boolean[32];
                for (int i = 0; i < array2.length; ++i) {
                    this.flags[i] = array2[i];
                }
            }
        }
        else {
            this.flags = new boolean[32];
        }
        if (this.flags[8] && date4 != null) {
            this.renewTill = new Date(date4.getTime());
        }
        if (date != null) {
            this.authTime = new Date(date.getTime());
        }
        if (date2 != null) {
            this.startTime = new Date(date2.getTime());
        }
        else {
            this.startTime = this.authTime;
        }
        if (date3 == null) {
            throw new IllegalArgumentException("End time for ticket validity cannot be null");
        }
        this.endTime = new Date(date3.getTime());
        if (array3 != null) {
            this.clientAddresses = array3.clone();
        }
    }
    
    public final KerberosPrincipal getClient() {
        return this.client;
    }
    
    public final KerberosPrincipal getServer() {
        return this.server;
    }
    
    public final SecretKey getSessionKey() {
        if (this.destroyed) {
            throw new IllegalStateException("This ticket is no longer valid");
        }
        return this.sessionKey;
    }
    
    public final int getSessionKeyType() {
        if (this.destroyed) {
            throw new IllegalStateException("This ticket is no longer valid");
        }
        return this.sessionKey.getKeyType();
    }
    
    public final boolean isForwardable() {
        return this.flags != null && this.flags[1];
    }
    
    public final boolean isForwarded() {
        return this.flags != null && this.flags[2];
    }
    
    public final boolean isProxiable() {
        return this.flags != null && this.flags[3];
    }
    
    public final boolean isProxy() {
        return this.flags != null && this.flags[4];
    }
    
    public final boolean isPostdated() {
        return this.flags != null && this.flags[6];
    }
    
    public final boolean isRenewable() {
        return this.flags != null && this.flags[8];
    }
    
    public final boolean isInitial() {
        return this.flags != null && this.flags[9];
    }
    
    public final boolean[] getFlags() {
        return (boolean[])((this.flags == null) ? null : ((boolean[])this.flags.clone()));
    }
    
    public final Date getAuthTime() {
        return (this.authTime == null) ? null : ((Date)this.authTime.clone());
    }
    
    public final Date getStartTime() {
        return (this.startTime == null) ? null : ((Date)this.startTime.clone());
    }
    
    public final Date getEndTime() {
        return (this.endTime == null) ? null : ((Date)this.endTime.clone());
    }
    
    public final Date getRenewTill() {
        return (this.renewTill == null) ? null : ((Date)this.renewTill.clone());
    }
    
    public final InetAddress[] getClientAddresses() {
        return (InetAddress[])((this.clientAddresses == null) ? null : ((InetAddress[])this.clientAddresses.clone()));
    }
    
    public final byte[] getEncoded() {
        if (this.destroyed) {
            throw new IllegalStateException("This ticket is no longer valid");
        }
        return this.asn1Encoding.clone();
    }
    
    @Override
    public boolean isCurrent() {
        return this.endTime != null && System.currentTimeMillis() <= this.endTime.getTime();
    }
    
    @Override
    public void refresh() throws RefreshFailedException {
        if (this.destroyed) {
            throw new RefreshFailedException("A destroyed ticket cannot be renewd.");
        }
        if (!this.isRenewable()) {
            throw new RefreshFailedException("This ticket is not renewable");
        }
        if (this.getRenewTill() == null) {
            return;
        }
        if (System.currentTimeMillis() > this.getRenewTill().getTime()) {
            throw new RefreshFailedException("This ticket is past its last renewal time.");
        }
        Throwable t = null;
        Credentials renew = null;
        try {
            renew = new Credentials(this.asn1Encoding, this.client.toString(), (this.clientAlias != null) ? this.clientAlias.getName() : null, this.server.toString(), (this.serverAlias != null) ? this.serverAlias.getName() : null, this.sessionKey.getEncoded(), this.sessionKey.getKeyType(), this.flags, this.authTime, this.startTime, this.endTime, this.renewTill, this.clientAddresses);
            renew = renew.renew();
        }
        catch (final KrbException ex) {
            t = ex;
        }
        catch (final IOException ex2) {
            t = ex2;
        }
        if (t != null) {
            final RefreshFailedException ex3 = new RefreshFailedException("Failed to renew Kerberos Ticket for client " + this.client + " and server " + this.server + " - " + t.getMessage());
            ex3.initCause(t);
            throw ex3;
        }
        synchronized (this) {
            try {
                this.destroy();
            }
            catch (final DestroyFailedException ex4) {}
            this.init(renew.getEncoded(), new KerberosPrincipal(renew.getClient().getName()), new KerberosPrincipal(renew.getServer().getName(), 2), renew.getSessionKey().getBytes(), renew.getSessionKey().getEType(), renew.getFlags(), renew.getAuthTime(), renew.getStartTime(), renew.getEndTime(), renew.getRenewTill(), renew.getClientAddresses());
            this.destroyed = false;
        }
    }
    
    @Override
    public void destroy() throws DestroyFailedException {
        if (!this.destroyed) {
            Arrays.fill(this.asn1Encoding, (byte)0);
            this.client = null;
            this.server = null;
            this.sessionKey.destroy();
            this.flags = null;
            this.authTime = null;
            this.startTime = null;
            this.endTime = null;
            this.renewTill = null;
            this.clientAddresses = null;
            this.destroyed = true;
        }
    }
    
    @Override
    public boolean isDestroyed() {
        return this.destroyed;
    }
    
    @Override
    public String toString() {
        if (this.destroyed) {
            return "Destroyed KerberosTicket";
        }
        final StringBuffer sb = new StringBuffer();
        if (this.clientAddresses != null) {
            for (int i = 0; i < this.clientAddresses.length; ++i) {
                sb.append("clientAddresses[" + i + "] = " + this.clientAddresses[i].toString());
            }
        }
        return "Ticket (hex) = \n" + new HexDumpEncoder().encodeBuffer(this.asn1Encoding) + "\nClient Principal = " + this.client.toString() + "\nServer Principal = " + this.server.toString() + "\nSession Key = " + this.sessionKey.toString() + "\nForwardable Ticket " + this.flags[1] + "\nForwarded Ticket " + this.flags[2] + "\nProxiable Ticket " + this.flags[3] + "\nProxy Ticket " + this.flags[4] + "\nPostdated Ticket " + this.flags[6] + "\nRenewable Ticket " + this.flags[8] + "\nInitial Ticket " + this.flags[8] + "\nAuth Time = " + String.valueOf(this.authTime) + "\nStart Time = " + String.valueOf(this.startTime) + "\nEnd Time = " + this.endTime.toString() + "\nRenew Till = " + String.valueOf(this.renewTill) + "\nClient Addresses " + ((this.clientAddresses == null) ? " Null " : (sb.toString() + ((this.proxy == null) ? "" : "\nwith a proxy ticket") + "\n"));
    }
    
    @Override
    public int hashCode() {
        final int n = 17;
        if (this.isDestroyed()) {
            return n;
        }
        int n2 = ((((n * 37 + Arrays.hashCode(this.getEncoded())) * 37 + this.endTime.hashCode()) * 37 + this.client.hashCode()) * 37 + this.server.hashCode()) * 37 + this.sessionKey.hashCode();
        if (this.authTime != null) {
            n2 = n2 * 37 + this.authTime.hashCode();
        }
        if (this.startTime != null) {
            n2 = n2 * 37 + this.startTime.hashCode();
        }
        if (this.renewTill != null) {
            n2 = n2 * 37 + this.renewTill.hashCode();
        }
        int n3 = n2 * 37 + Arrays.hashCode(this.clientAddresses);
        if (this.proxy != null) {
            n3 = n3 * 37 + this.proxy.hashCode();
        }
        return n3 * 37 + Arrays.hashCode(this.flags);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof KerberosTicket)) {
            return false;
        }
        final KerberosTicket kerberosTicket = (KerberosTicket)o;
        if (this.isDestroyed() || kerberosTicket.isDestroyed()) {
            return false;
        }
        if (!Arrays.equals(this.getEncoded(), kerberosTicket.getEncoded()) || !this.endTime.equals(kerberosTicket.getEndTime()) || !this.server.equals(kerberosTicket.getServer()) || !this.client.equals(kerberosTicket.getClient()) || !this.sessionKey.equals(kerberosTicket.getSessionKey()) || !Arrays.equals(this.clientAddresses, kerberosTicket.getClientAddresses()) || !Arrays.equals(this.flags, kerberosTicket.getFlags())) {
            return false;
        }
        if (this.authTime == null) {
            if (kerberosTicket.getAuthTime() != null) {
                return false;
            }
        }
        else if (!this.authTime.equals(kerberosTicket.getAuthTime())) {
            return false;
        }
        if (this.startTime == null) {
            if (kerberosTicket.getStartTime() != null) {
                return false;
            }
        }
        else if (!this.startTime.equals(kerberosTicket.getStartTime())) {
            return false;
        }
        if (this.renewTill == null) {
            if (kerberosTicket.getRenewTill() != null) {
                return false;
            }
        }
        else if (!this.renewTill.equals(kerberosTicket.getRenewTill())) {
            return false;
        }
        return Objects.equals(this.proxy, kerberosTicket.proxy);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        if (this.sessionKey == null) {
            throw new InvalidObjectException("Session key cannot be null");
        }
        try {
            this.init(this.asn1Encoding, this.client, this.server, this.sessionKey, this.flags, this.authTime, this.startTime, this.endTime, this.renewTill, this.clientAddresses);
        }
        catch (final IllegalArgumentException ex) {
            throw (InvalidObjectException)new InvalidObjectException(ex.getMessage()).initCause(ex);
        }
    }
}
