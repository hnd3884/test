package com.maverick.ssh;

import java.io.EOFException;
import java.io.IOException;
import java.util.Vector;
import java.io.DataOutputStream;
import java.io.DataInputStream;

public class SubsystemChannel
{
    DataInputStream c;
    DataOutputStream d;
    Vector f;
    int g;
    protected SshChannel channel;
    _c b;
    _b e;
    
    public SubsystemChannel(final SshChannel channel) throws SshException {
        this.f = new Vector();
        this.g = Integer.parseInt(System.getProperty("maverick.sftp.maxPacketSize", "1024000"));
        this.b = new _c();
        this.e = new _b();
        this.channel = channel;
        try {
            this.c = new DataInputStream(channel.getInputStream());
            this.d = new DataOutputStream(channel.getOutputStream());
        }
        catch (final SshIOException ex) {
            throw ex.getRealException();
        }
        catch (final IOException ex2) {
            throw new SshException(ex2.getMessage(), 6);
        }
    }
    
    public boolean isClosed() {
        return this.channel.isClosed();
    }
    
    public void close() throws IOException {
        this.f.removeAllElements();
        this.channel.close();
    }
    
    public byte[] nextMessage() throws SshException {
        return this.b.b(this.c);
    }
    
    protected void sendMessage(final Packet packet) throws SshException {
        this.e.b(packet);
    }
    
    protected void sendMessage(final byte[] array) throws SshException {
        try {
            final Packet packet = this.createPacket();
            packet.write(array);
            this.sendMessage(packet);
        }
        catch (final IOException ex) {
            throw new SshException(1, ex);
        }
    }
    
    protected Packet createPacket() throws IOException {
        synchronized (this.f) {
            if (this.f.size() == 0) {
                return new Packet();
            }
            final Packet packet = this.f.elementAt(0);
            this.f.removeElementAt(0);
            return packet;
        }
    }
    
    class _b
    {
        synchronized void b(final Packet packet) throws SshException {
            try {
                packet.finish();
                SubsystemChannel.this.d.write(packet.array(), 0, packet.size());
            }
            catch (final SshIOException ex) {
                throw ex.getRealException();
            }
            catch (final EOFException ex2) {
                try {
                    SubsystemChannel.this.close();
                }
                catch (final SshIOException ex3) {
                    throw ex3.getRealException();
                }
                catch (final IOException ex4) {
                    throw new SshException(ex4.getMessage(), 6);
                }
                throw new SshException("The channel unexpectedly terminated", 6);
            }
            catch (final IOException ex5) {
                try {
                    SubsystemChannel.this.close();
                }
                catch (final SshIOException ex6) {
                    throw ex6.getRealException();
                }
                catch (final IOException ex7) {
                    throw new SshException(ex7.getMessage(), 6);
                }
                throw new SshException("Unknown channel IO failure: " + ex5.getMessage(), 6);
            }
            finally {
                packet.reset();
                synchronized (SubsystemChannel.this.f) {
                    SubsystemChannel.this.f.addElement(packet);
                }
            }
        }
    }
    
    class _c
    {
        synchronized byte[] b(final DataInputStream dataInputStream) throws SshException {
            int int1 = -1;
            try {
                int1 = dataInputStream.readInt();
                if (int1 < 0) {
                    throw new SshException("Negative message length in SFTP protocol.", 3);
                }
                if (int1 > SubsystemChannel.this.g) {
                    throw new SshException("Invalid message length in SFTP protocol [" + int1 + "]", 3);
                }
                final byte[] array = new byte[int1];
                dataInputStream.readFully(array);
                return array;
            }
            catch (final OutOfMemoryError outOfMemoryError) {
                throw new SshException("Invalid message length in SFTP protocol [" + int1 + "]", 3);
            }
            catch (final EOFException ex) {
                try {
                    SubsystemChannel.this.close();
                }
                catch (final SshIOException ex2) {
                    throw ex2.getRealException();
                }
                catch (final IOException ex3) {
                    throw new SshException(ex3.getMessage(), 6);
                }
                throw new SshException("The channel unexpectedly terminated", 6);
            }
            catch (final IOException ex4) {
                if (ex4 instanceof SshIOException) {
                    throw ((SshIOException)ex4).getRealException();
                }
                try {
                    SubsystemChannel.this.close();
                }
                catch (final SshIOException ex5) {
                    throw ex5.getRealException();
                }
                catch (final IOException ex6) {
                    throw new SshException(ex6.getMessage(), 6);
                }
                throw new SshException(6, ex4);
            }
        }
    }
}
