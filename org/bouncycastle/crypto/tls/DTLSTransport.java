package org.bouncycastle.crypto.tls;

import java.io.IOException;

public class DTLSTransport implements DatagramTransport
{
    private final DTLSRecordLayer recordLayer;
    
    DTLSTransport(final DTLSRecordLayer recordLayer) {
        this.recordLayer = recordLayer;
    }
    
    public int getReceiveLimit() throws IOException {
        return this.recordLayer.getReceiveLimit();
    }
    
    public int getSendLimit() throws IOException {
        return this.recordLayer.getSendLimit();
    }
    
    public int receive(final byte[] array, final int n, final int n2, final int n3) throws IOException {
        try {
            return this.recordLayer.receive(array, n, n2, n3);
        }
        catch (final TlsFatalAlert tlsFatalAlert) {
            this.recordLayer.fail(tlsFatalAlert.getAlertDescription());
            throw tlsFatalAlert;
        }
        catch (final IOException ex) {
            this.recordLayer.fail((short)80);
            throw ex;
        }
        catch (final RuntimeException ex2) {
            this.recordLayer.fail((short)80);
            throw new TlsFatalAlert((short)80, ex2);
        }
    }
    
    public void send(final byte[] array, final int n, final int n2) throws IOException {
        try {
            this.recordLayer.send(array, n, n2);
        }
        catch (final TlsFatalAlert tlsFatalAlert) {
            this.recordLayer.fail(tlsFatalAlert.getAlertDescription());
            throw tlsFatalAlert;
        }
        catch (final IOException ex) {
            this.recordLayer.fail((short)80);
            throw ex;
        }
        catch (final RuntimeException ex2) {
            this.recordLayer.fail((short)80);
            throw new TlsFatalAlert((short)80, ex2);
        }
    }
    
    public void close() throws IOException {
        this.recordLayer.close();
    }
}
