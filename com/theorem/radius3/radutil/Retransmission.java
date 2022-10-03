package com.theorem.radius3.radutil;

import com.theorem.radius3.ClientReceiveException;
import com.theorem.radius3.AttributeList;
import com.theorem.radius3.RADIUSClient;
import java.net.InetAddress;
import java.util.Random;

public class Retransmission
{
    private Boolean a;
    Random b;
    private boolean c;
    private long d;
    private long e;
    private double f;
    private int g;
    private long h;
    
    public Retransmission(final int n, final int n2, int n3, final int g) {
        this.a = new Boolean(true);
        this.b = new Random();
        this.c = true;
        this.d = n * 1000L;
        this.e = n2 * 1000L;
        if (n3 <= 0) {
            n3 = 20;
        }
        this.f = n3 / 100.0;
        this.g = g;
    }
    
    public final void cancel() {
        synchronized (this.a) {
            this.c = false;
            this.a.notify();
        }
    }
    
    public final boolean pause() {
        if (this.g-- == 0) {
            return false;
        }
        this.getTimeout();
        synchronized (this.a) {
            this.c = true;
            try {
                this.a.wait(this.h);
            }
            catch (final InterruptedException ex) {
                return false;
            }
        }
        return this.c;
    }
    
    public final void reset() {
        this.h = 0L;
    }
    
    public final long getTimeout() {
        if (this.h <= 0L) {
            this.h = this.d;
        }
        else {
            this.h *= 2L;
            if (this.h > this.e) {
                this.h = this.d;
            }
        }
        this.h = this.a(this.h);
        if (this.h < 0L) {
            this.getTimeout();
        }
        return this.h;
    }
    
    private final long a(final long n) {
        double n2;
        if (this.h <= this.d) {
            n2 = 1.0;
        }
        else {
            n2 = ((this.b.nextInt(100) >= 50) ? -1.0 : 1.0);
        }
        return n + (long)(n * (this.b.nextDouble() * this.f * n2));
    }
    
    public static void main(final String[] array) {
        try {
            new Retransmission(5, 25, 20, 2).a();
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private final void a() throws Exception {
        final Retransmission retransmission = new Retransmission(6, 20, 60, 3);
        final RADIUSClient radiusClient = new RADIUSClient(InetAddress.getByName("127.0.0.1"), 0, Util.toUTF8("axltest"), 6);
        final AttributeList list = new AttributeList();
        list.addAttribute(4, InetAddress.getLocalHost());
        try {
            radiusClient.authenticate("chap", "test", list);
        }
        catch (final ClientReceiveException ex) {
            try {
                System.out.println("Sending retry.");
                radiusClient.retry();
            }
            catch (final ClientReceiveException ex2) {}
        }
    }
}
