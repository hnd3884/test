package com.theorem.radius3.radutil;

import com.theorem.radius3.ClientReceiveException;
import com.theorem.radius3.ClientSendException;
import java.net.SocketException;
import java.net.InetAddress;
import com.theorem.radius3.PacketType;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import com.theorem.radius3.RADIUSClient;

public class Replay
{
    RADIUSClient a;
    
    public static void main(final String[] array) {
        if (array.length < 3 || array.length > 4) {
            System.err.println("Usage: Replay <filename> <server> [port] <secret> ");
            System.exit(1);
        }
        try {
            final byte[] byteArray = new DumpPacket().toByteArray(new BufferedReader(new FileReader(array[0])));
            final String s = array[1];
            int int1 = 0;
            String s2;
            if (array.length == 4) {
                s2 = array[3];
                try {
                    int1 = Integer.parseInt(array[2]);
                }
                catch (final NumberFormatException ex) {
                    System.err.println("Port number format problem. Port is [" + array[2] + "]");
                    System.exit(1);
                }
            }
            else {
                s2 = array[2];
            }
            final RADIUSClient radiusClient = new RADIUSClient(s, int1, s2, 1000);
            radiusClient.setDebug(true);
            System.out.println("Authentication result: " + new PacketType().getName(radiusClient.authenticate(byteArray)) + " Error: " + radiusClient.getErrorString());
        }
        catch (final Exception ex2) {
            ex2.printStackTrace();
            System.exit(1);
        }
    }
    
    public Replay(final InetAddress inetAddress, final int n, final String s, final int n2) throws SocketException {
        this.a = new RADIUSClient(inetAddress, n, s, n2);
    }
    
    public final int send(final byte[] array) throws ClientSendException, ClientReceiveException {
        return this.a.sendRawPacket(array);
    }
    
    public final void reset() throws SocketException {
        this.a.reset();
    }
    
    public final int getError() throws SocketException {
        return this.a.getError();
    }
    
    public final String getErrorString() throws SocketException {
        return this.a.getErrorString();
    }
    
    public final void setDebug(final boolean debug) {
        this.a.setDebug(debug);
    }
}
