package com.sshtools.publickey;

import java.util.Vector;
import com.maverick.ssh.components.SshPublicKey;
import com.maverick.ssh.Packet;
import java.io.IOException;
import com.maverick.util.ByteArrayReader;
import com.maverick.ssh.SshException;
import com.maverick.ssh.SshChannel;
import com.maverick.ssh2.Ssh2Session;
import com.maverick.ssh.SubsystemChannel;

public class PublicKeySubsystem extends SubsystemChannel
{
    int q;
    
    public PublicKeySubsystem(final Ssh2Session ssh2Session) throws SshException {
        super(ssh2Session);
        try {
            if (!ssh2Session.startSubsystem("publickey@vandyke.com")) {
                throw new SshException("The remote side failed to start the publickey subsystem", 6);
            }
            final Packet packet = this.createPacket();
            packet.writeString("version");
            packet.writeInt(1);
            this.sendMessage(packet);
            final ByteArrayReader byteArrayReader = new ByteArrayReader(this.nextMessage());
            byteArrayReader.readString();
            this.q = Math.min((int)byteArrayReader.readInt(), 1);
        }
        catch (final IOException ex) {
            throw new SshException(5, ex);
        }
    }
    
    public void add(final SshPublicKey sshPublicKey, final String s) throws SshException, PublicKeySubsystemException {
        try {
            final Packet packet = this.createPacket();
            packet.writeString("add");
            packet.writeString(s);
            packet.writeString(sshPublicKey.getAlgorithm());
            packet.writeBinaryString(sshPublicKey.getEncoded());
            this.sendMessage(packet);
            this.c();
        }
        catch (final IOException ex) {
            throw new SshException(ex);
        }
    }
    
    public void remove(final SshPublicKey sshPublicKey) throws SshException, PublicKeySubsystemException {
        try {
            final Packet packet = this.createPacket();
            packet.writeString("remove");
            packet.writeString(sshPublicKey.getAlgorithm());
            packet.writeBinaryString(sshPublicKey.getEncoded());
            this.sendMessage(packet);
            this.c();
        }
        catch (final IOException ex) {
            throw new SshException(ex);
        }
    }
    
    public SshPublicKey[] list() throws SshException, PublicKeySubsystemException {
        try {
            final Packet packet = this.createPacket();
            packet.writeString("list");
            this.sendMessage(packet);
            final Vector vector = new Vector();
            ByteArrayReader byteArrayReader;
            String string;
            while (true) {
                byteArrayReader = new ByteArrayReader(this.nextMessage());
                string = byteArrayReader.readString();
                if (!string.equals("publickey")) {
                    break;
                }
                byteArrayReader.readString();
                vector.addElement(SshPublicKeyFileFactory.decodeSSH2PublicKey(byteArrayReader.readString(), byteArrayReader.readBinaryString()));
            }
            if (!string.equals("status")) {
                throw new SshException("The server sent an invalid response to a list command", 3);
            }
            final int n = (int)byteArrayReader.readInt();
            final String string2 = byteArrayReader.readString();
            if (n != 0) {
                throw new PublicKeySubsystemException(n, string2);
            }
            final SshPublicKey[] array = new SshPublicKey[vector.size()];
            vector.copyInto(array);
            return array;
        }
        catch (final IOException ex) {
            throw new SshException(ex);
        }
    }
    
    public void associateCommand(final SshPublicKey sshPublicKey, final String s) throws SshException, PublicKeySubsystemException {
        try {
            final Packet packet = this.createPacket();
            packet.writeString("command");
            packet.writeString(sshPublicKey.getAlgorithm());
            packet.writeBinaryString(sshPublicKey.getEncoded());
            packet.writeString(s);
            this.sendMessage(packet);
            this.c();
        }
        catch (final IOException ex) {
            throw new SshException(ex);
        }
    }
    
    void c() throws SshException, PublicKeySubsystemException {
        try {
            final ByteArrayReader byteArrayReader = new ByteArrayReader(this.nextMessage());
            byteArrayReader.readString();
            final int n = (int)byteArrayReader.readInt();
            final String string = byteArrayReader.readString();
            if (n != 0) {
                throw new PublicKeySubsystemException(n, string);
            }
        }
        catch (final IOException ex) {
            throw new SshException(ex);
        }
    }
}
