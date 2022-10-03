package org.bouncycastle.crypto.tls;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

public class NewSessionTicket
{
    protected long ticketLifetimeHint;
    protected byte[] ticket;
    
    public NewSessionTicket(final long ticketLifetimeHint, final byte[] ticket) {
        this.ticketLifetimeHint = ticketLifetimeHint;
        this.ticket = ticket;
    }
    
    public long getTicketLifetimeHint() {
        return this.ticketLifetimeHint;
    }
    
    public byte[] getTicket() {
        return this.ticket;
    }
    
    public void encode(final OutputStream outputStream) throws IOException {
        TlsUtils.writeUint32(this.ticketLifetimeHint, outputStream);
        TlsUtils.writeOpaque16(this.ticket, outputStream);
    }
    
    public static NewSessionTicket parse(final InputStream inputStream) throws IOException {
        return new NewSessionTicket(TlsUtils.readUint32(inputStream), TlsUtils.readOpaque16(inputStream));
    }
}
