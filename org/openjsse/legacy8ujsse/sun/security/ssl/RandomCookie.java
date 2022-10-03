package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.io.PrintStream;
import java.io.IOException;
import java.security.SecureRandom;

final class RandomCookie
{
    byte[] random_bytes;
    
    RandomCookie(final SecureRandom generator) {
        final long temp = System.currentTimeMillis() / 1000L;
        int gmt_unix_time;
        if (temp < 2147483647L) {
            gmt_unix_time = (int)temp;
        }
        else {
            gmt_unix_time = Integer.MAX_VALUE;
        }
        generator.nextBytes(this.random_bytes = new byte[32]);
        this.random_bytes[0] = (byte)(gmt_unix_time >> 24);
        this.random_bytes[1] = (byte)(gmt_unix_time >> 16);
        this.random_bytes[2] = (byte)(gmt_unix_time >> 8);
        this.random_bytes[3] = (byte)gmt_unix_time;
    }
    
    RandomCookie(final HandshakeInStream m) throws IOException {
        m.read(this.random_bytes = new byte[32], 0, 32);
    }
    
    void send(final HandshakeOutStream out) throws IOException {
        out.write(this.random_bytes, 0, 32);
    }
    
    void print(final PrintStream s) {
        int gmt_unix_time = this.random_bytes[0] << 24;
        gmt_unix_time += this.random_bytes[1] << 16;
        gmt_unix_time += this.random_bytes[2] << 8;
        gmt_unix_time += this.random_bytes[3];
        s.print("GMT: " + gmt_unix_time + " ");
        s.print("bytes = { ");
        for (int i = 4; i < 32; ++i) {
            if (i != 4) {
                s.print(", ");
            }
            s.print(this.random_bytes[i] & 0xFF);
        }
        s.println(" }");
    }
}
