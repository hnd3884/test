package com.maverick.ssh1;

import java.io.InterruptedIOException;
import com.maverick.util.ByteArrayReader;
import com.maverick.ssh.components.SshPublicKey;
import com.maverick.ssh.components.Digest;
import com.maverick.events.EventLog;
import java.math.BigInteger;
import com.maverick.ssh.message.SshMessage;
import com.maverick.ssh.components.ComponentManager;
import com.maverick.util.ByteArrayWriter;
import java.io.IOException;
import com.maverick.ssh.SshException;
import com.maverick.ssh.SshContext;
import com.maverick.ssh.components.SshRsaPublicKey;
import com.maverick.ssh.compression.SshCompression;
import com.maverick.ssh.components.SshCipher;
import com.maverick.ssh.SshTransport;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import com.maverick.ssh.message.SshMessageReader;

class f implements SshMessageReader
{
    DataInputStream g;
    DataOutputStream o;
    SshTransport q;
    SshCipher s;
    SshCipher j;
    SshCompression n;
    SshCompression m;
    SshRsaPublicKey u;
    SshRsaPublicKey h;
    int k;
    int r;
    int f;
    int l;
    byte[] c;
    byte[] p;
    byte[] t;
    long i;
    Ssh1Context b;
    int d;
    boolean e;
    
    f(final SshTransport q, final SshContext sshContext) throws SshException {
        this.r = 2;
        this.i = 0L;
        this.d = 1;
        this.e = false;
        try {
            if (!(sshContext instanceof Ssh1Context)) {
                throw new SshException("Invalid SshContext!", 4);
            }
            this.g = new DataInputStream(q.getInputStream());
            this.o = new DataOutputStream(q.getOutputStream());
            this.q = q;
            this.b = (Ssh1Context)sshContext;
        }
        catch (final IOException ex) {
            this.c();
            throw new SshException(ex, 10);
        }
    }
    
    public boolean isConnected() {
        return this.d == 2;
    }
    
    public byte[] nextMessage() throws SshException {
        byte[] h;
        do {
            h = this.h();
        } while (this.b(h) && this.d == 2);
        if (this.d == 3) {
            throw new SshException("The remote host disconnected", 2);
        }
        return h;
    }
    
    void c() {
        try {
            this.q.close();
        }
        catch (final IOException ex) {}
        this.d = 3;
    }
    
    void c(final String s) {
        try {
            this.e = true;
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter(s.length() + 5);
            byteArrayWriter.write(1);
            byteArrayWriter.writeString(s);
            this.d(byteArrayWriter.toByteArray());
        }
        catch (final Throwable t) {}
        finally {
            this.c();
        }
    }
    
    int e() {
        return this.d;
    }
    
    boolean b(final byte[] array) {
        switch (array[0]) {
            case 1: {
                this.c();
                return true;
            }
            case 32: {
                return true;
            }
            case 36: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    boolean b(final String s) throws SshException {
        try {
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
            byteArrayWriter.write(4);
            byteArrayWriter.writeString(s);
            this.d(byteArrayWriter.toByteArray());
            final boolean f = this.f();
            this.d = 2;
            return f;
        }
        catch (final IOException ex) {
            this.c();
            throw new SshException(ex, 1);
        }
    }
    
    void g() throws SshException {
        try {
            final int cipherType = this.b.getCipherType(this.f);
            final SshCipher cipher = this.b.createCipher(cipherType);
            final SshCipher cipher2 = this.b.createCipher(cipherType);
            this.d();
            this.c = new byte[32];
            ComponentManager.getInstance().getRND().nextBytes(this.c);
            this.b(cipherType);
            final byte[] array = new byte[cipher.getBlockSize()];
            for (int i = 0; i < array.length; ++i) {
                array[i] = 0;
            }
            cipher.init(1, array, this.c);
            final byte[] array2 = new byte[cipher2.getBlockSize()];
            for (int j = 0; j < array2.length; ++j) {
                array2[j] = 0;
            }
            cipher2.init(0, array2, this.c);
            this.s = cipher;
            this.j = cipher2;
            if (!this.f()) {
                try {
                    this.q.close();
                }
                catch (final IOException ex) {}
                throw new SshException("The session failed to initialize!", 9);
            }
        }
        catch (final IOException ex2) {
            this.c();
            throw new SshException(ex2, 9);
        }
    }
    
    boolean f() throws SshException {
        try {
            while (true) {
                final SshMessage sshMessage = new SshMessage(this.nextMessage());
                switch (sshMessage.getMessageId()) {
                    case 14: {
                        return true;
                    }
                    case 15: {
                        return false;
                    }
                    case 1: {
                        throw new SshException("The server disconnected! " + sshMessage.readString(), 2);
                    }
                    case 36: {
                        continue;
                    }
                    case 32: {
                        continue;
                    }
                    default: {
                        throw new SshException("Invalid message type " + String.valueOf(sshMessage.getMessageId()) + " received", 3);
                    }
                }
            }
        }
        catch (final IOException ex) {
            this.c();
            throw new SshException(ex, 1);
        }
    }
    
    private String c(final byte[] array) {
        String string = "";
        for (int i = 0; i < array.length; ++i) {
            string = string + " " + Integer.toHexString(array[i] & 0xFF);
        }
        return string.trim();
    }
    
    void b(final int n) throws SshException {
        try {
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
            final byte[] array = new byte[this.c.length + 1];
            array[0] = 0;
            System.arraycopy(this.c, 0, array, 1, this.c.length);
            for (int i = 0; i < this.p.length; ++i) {
                final byte[] array2 = array;
                final int n2 = i + 1;
                array2[n2] ^= this.p[i];
            }
            final BigInteger bigInteger = new BigInteger(array);
            if (this.h.getModulus().bitLength() > this.u.getModulus().bitLength() - 16 && this.u.getModulus().bitLength() > this.h.getModulus().bitLength() - 16) {
                throw new SshException("SSH 1.5 protocol violation: Server key and host key lengths do not match protocol requirements. serverbits=" + String.valueOf(this.h.getModulus().bitLength()) + " hostbits=" + String.valueOf(this.u.getModulus().bitLength()), 3);
            }
            BigInteger bigInteger2;
            if ((this.u.getModulus().bitLength() + 7) / 8 < (this.h.getModulus().bitLength() + 7) / 8) {
                bigInteger2 = this.h.doPublic(this.u.doPublic(bigInteger));
            }
            else {
                bigInteger2 = this.u.doPublic(this.h.doPublic(bigInteger));
            }
            EventLog.LogEvent(this, "Encoded key is : " + this.c(bigInteger2.toByteArray()));
            byteArrayWriter.write(3);
            byteArrayWriter.write(n);
            byteArrayWriter.write(this.t);
            byteArrayWriter.writeMPINT(bigInteger2);
            byteArrayWriter.writeInt(this.r);
            this.d(byteArrayWriter.toByteArray());
        }
        catch (final IOException ex) {
            this.c();
            throw new SshException("", 1);
        }
    }
    
    void d() throws SshException {
        final byte[] byteArray = this.u.getModulus().toByteArray();
        final byte[] byteArray2 = this.h.getModulus().toByteArray();
        int n = byteArray.length + byteArray2.length + this.t.length;
        if (byteArray[0] == 0) {
            --n;
        }
        if (byteArray2[0] == 0) {
            --n;
        }
        final byte[] array = new byte[n];
        int length;
        if (byteArray[0] == 0) {
            System.arraycopy(byteArray, (byteArray[0] == 0) ? 1 : 0, array, 0, byteArray.length - 1);
            length = byteArray.length - 1;
        }
        else {
            System.arraycopy(byteArray, (byteArray[0] == 0) ? 1 : 0, array, 0, byteArray.length);
            length = byteArray.length;
        }
        int n2;
        if (byteArray2[0] == 0) {
            System.arraycopy(byteArray2, (byteArray2[0] == 0) ? 1 : 0, array, length, byteArray2.length - 1);
            n2 = length + (byteArray2.length - 1);
        }
        else {
            System.arraycopy(byteArray2, (byteArray2[0] == 0) ? 1 : 0, array, length, byteArray2.length);
            n2 = length + byteArray2.length;
        }
        System.arraycopy(this.t, 0, array, n2, this.t.length);
        final Digest digest = (Digest)ComponentManager.getInstance().supportedDigests().getInstance("MD5");
        digest.putBytes(array);
        this.p = digest.doFinal();
    }
    
    void b() throws SshException {
        try {
            final SshMessage sshMessage = new SshMessage(this.nextMessage());
            if (sshMessage.getMessageId() != 2) {
                throw new SshException("SSH_SMSG_PUBLIC_KEY message expected but received type " + String.valueOf(sshMessage.getMessageId()) + " instead!", 3);
            }
            sshMessage.read(this.t = new byte[8]);
            final int n = (int)sshMessage.readInt();
            this.h = ComponentManager.getInstance().createRsaPublicKey(sshMessage.readMPINT(), sshMessage.readMPINT(), 1);
            final int n2 = (int)sshMessage.readInt();
            this.u = ComponentManager.getInstance().createRsaPublicKey(sshMessage.readMPINT(), sshMessage.readMPINT(), 1);
            this.k = (int)sshMessage.readInt();
            this.f = (int)sshMessage.readInt();
            this.l = (int)sshMessage.readInt();
            if (this.b.getHostKeyVerification() != null && !this.b.getHostKeyVerification().verifyHost(this.q.getHost(), this.u)) {
                throw new SshException("The host key was not accepted.", 9);
            }
        }
        catch (final IOException ex) {
            throw new SshException(ex, 5);
        }
    }
    
    byte[] h() throws SshException {
        try {
            synchronized (this.g) {
                final int int1 = this.g.readInt();
                final int n = int1 + 8 & 0xFFFFFFF8;
                final byte[] array = new byte[n];
                this.g.readFully(array);
                if (this.s != null) {
                    this.s.transform(array);
                }
                final int n2 = (int)ByteArrayReader.readInt(array, array.length - 4);
                final int n3 = (int)com.maverick.ssh1.c.b(array, 0, n - 4);
                if (n2 != n3) {
                    throw new SshException("Invalid checksum detected! Received:" + n2 + " Expected:" + n3, 3);
                }
                if (this.n != null) {
                    throw new SshException("Compression not yet supported", 4);
                }
                final byte[] array2 = new byte[int1 - 4];
                System.arraycopy(array, 8 - int1 % 8, array2, 0, array2.length);
                return array2;
            }
        }
        catch (final InterruptedIOException ex) {
            throw new SshException("Interrupted IO; possible socket timeout detected?", 19);
        }
        catch (final IOException ex2) {
            this.c();
            throw new SshException(ex2, 1);
        }
    }
    
    void d(byte[] byteArray) throws SshException {
        try {
            synchronized (this.o) {
                if (this.m != null) {
                    throw new SshException("Compression not supported yet!", 4);
                }
                final int n = 8 - (byteArray.length + 4) % 8;
                final byte[] array = new byte[n];
                final ByteArrayWriter byteArrayWriter = new ByteArrayWriter(byteArray.length + 4 + n);
                if (this.j != null) {
                    ComponentManager.getInstance().getRND().nextBytes(array);
                }
                byteArrayWriter.write(array);
                byteArrayWriter.write(byteArray);
                final byte[] byteArray2 = byteArrayWriter.toByteArray();
                byteArrayWriter.writeInt((int)com.maverick.ssh1.c.b(byteArray2, 0, byteArray2.length));
                byteArray = byteArrayWriter.toByteArray();
                if (this.j != null) {
                    this.j.transform(byteArray);
                }
                byteArrayWriter.reset();
                byteArrayWriter.writeInt(byteArray.length - n);
                byteArrayWriter.write(byteArray);
                this.o.write(byteArrayWriter.toByteArray());
            }
        }
        catch (final IOException ex) {
            this.c();
            throw new SshException(ex, 1);
        }
    }
}
