package sun.security.krb5.internal.ccache;

import sun.security.krb5.RealmException;
import sun.security.krb5.internal.KDCRep;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.Ticket;
import sun.security.krb5.internal.TicketFlags;
import sun.security.krb5.internal.AuthorizationData;
import sun.security.krb5.internal.HostAddresses;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.PrincipalName;

public class Credentials
{
    PrincipalName cname;
    PrincipalName sname;
    EncryptionKey key;
    KerberosTime authtime;
    KerberosTime starttime;
    KerberosTime endtime;
    KerberosTime renewTill;
    HostAddresses caddr;
    AuthorizationData authorizationData;
    public boolean isEncInSKey;
    TicketFlags flags;
    Ticket ticket;
    Ticket secondTicket;
    private boolean DEBUG;
    
    public Credentials(final PrincipalName principalName, final PrincipalName principalName2, final EncryptionKey encryptionKey, final KerberosTime authtime, final KerberosTime starttime, final KerberosTime endtime, final KerberosTime renewTill, final boolean isEncInSKey, final TicketFlags ticketFlags, final HostAddresses hostAddresses, final AuthorizationData authorizationData, final Ticket ticket, final Ticket ticket2) {
        this.DEBUG = Krb5.DEBUG;
        this.cname = (PrincipalName)principalName.clone();
        this.sname = (PrincipalName)principalName2.clone();
        this.key = (EncryptionKey)encryptionKey.clone();
        this.authtime = authtime;
        this.starttime = starttime;
        this.endtime = endtime;
        this.renewTill = renewTill;
        if (hostAddresses != null) {
            this.caddr = (HostAddresses)hostAddresses.clone();
        }
        if (authorizationData != null) {
            this.authorizationData = (AuthorizationData)authorizationData.clone();
        }
        this.isEncInSKey = isEncInSKey;
        this.flags = (TicketFlags)ticketFlags.clone();
        this.ticket = (Ticket)ticket.clone();
        if (ticket2 != null) {
            this.secondTicket = (Ticket)ticket2.clone();
        }
    }
    
    public Credentials(final KDCRep kdcRep, final Ticket ticket, final AuthorizationData authorizationData, final boolean isEncInSKey) {
        this.DEBUG = Krb5.DEBUG;
        if (kdcRep.encKDCRepPart == null) {
            return;
        }
        this.cname = (PrincipalName)kdcRep.cname.clone();
        this.ticket = (Ticket)kdcRep.ticket.clone();
        this.key = (EncryptionKey)kdcRep.encKDCRepPart.key.clone();
        this.flags = (TicketFlags)kdcRep.encKDCRepPart.flags.clone();
        this.authtime = kdcRep.encKDCRepPart.authtime;
        this.starttime = kdcRep.encKDCRepPart.starttime;
        this.endtime = kdcRep.encKDCRepPart.endtime;
        this.renewTill = kdcRep.encKDCRepPart.renewTill;
        this.sname = (PrincipalName)kdcRep.encKDCRepPart.sname.clone();
        this.caddr = (HostAddresses)kdcRep.encKDCRepPart.caddr.clone();
        this.secondTicket = (Ticket)ticket.clone();
        this.authorizationData = (AuthorizationData)authorizationData.clone();
        this.isEncInSKey = isEncInSKey;
    }
    
    public Credentials(final KDCRep kdcRep) {
        this(kdcRep, null);
    }
    
    public Credentials(final KDCRep kdcRep, final Ticket ticket) {
        this.DEBUG = Krb5.DEBUG;
        this.sname = (PrincipalName)kdcRep.encKDCRepPart.sname.clone();
        this.cname = (PrincipalName)kdcRep.cname.clone();
        this.key = (EncryptionKey)kdcRep.encKDCRepPart.key.clone();
        this.authtime = kdcRep.encKDCRepPart.authtime;
        this.starttime = kdcRep.encKDCRepPart.starttime;
        this.endtime = kdcRep.encKDCRepPart.endtime;
        this.renewTill = kdcRep.encKDCRepPart.renewTill;
        this.flags = kdcRep.encKDCRepPart.flags;
        if (kdcRep.encKDCRepPart.caddr != null) {
            this.caddr = (HostAddresses)kdcRep.encKDCRepPart.caddr.clone();
        }
        else {
            this.caddr = null;
        }
        this.ticket = (Ticket)kdcRep.ticket.clone();
        if (ticket != null) {
            this.secondTicket = (Ticket)ticket.clone();
            this.isEncInSKey = true;
        }
        else {
            this.secondTicket = null;
            this.isEncInSKey = false;
        }
    }
    
    public boolean isValid() {
        boolean b = true;
        if (this.endtime.getTime() < System.currentTimeMillis()) {
            b = false;
        }
        else if (this.starttime != null) {
            if (this.starttime.getTime() > System.currentTimeMillis()) {
                b = false;
            }
        }
        else if (this.authtime.getTime() > System.currentTimeMillis()) {
            b = false;
        }
        return b;
    }
    
    public PrincipalName getServicePrincipal() throws RealmException {
        return this.sname;
    }
    
    public Ticket getTicket() throws RealmException {
        return this.ticket;
    }
    
    public PrincipalName getServicePrincipal2() throws RealmException {
        return (this.secondTicket == null) ? null : this.secondTicket.sname;
    }
    
    public PrincipalName getClientPrincipal() throws RealmException {
        return this.cname;
    }
    
    public sun.security.krb5.Credentials setKrbCreds() {
        return new sun.security.krb5.Credentials(this.ticket, this.cname, null, this.sname, null, this.key, this.flags, this.authtime, this.starttime, this.endtime, this.renewTill, this.caddr);
    }
    
    public KerberosTime getStartTime() {
        return this.starttime;
    }
    
    public KerberosTime getAuthTime() {
        return this.authtime;
    }
    
    public KerberosTime getEndTime() {
        return this.endtime;
    }
    
    public KerberosTime getRenewTill() {
        return this.renewTill;
    }
    
    public TicketFlags getTicketFlags() {
        return this.flags;
    }
    
    public int getEType() {
        return this.key.getEType();
    }
    
    public EncryptionKey getKey() {
        return this.key;
    }
    
    public int getTktEType() {
        return this.ticket.encPart.getEType();
    }
    
    public int getTktEType2() {
        return (this.secondTicket == null) ? 0 : this.secondTicket.encPart.getEType();
    }
}
