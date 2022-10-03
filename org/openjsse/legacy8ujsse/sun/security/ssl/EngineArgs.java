package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.nio.ReadOnlyBufferException;
import java.nio.ByteBuffer;

class EngineArgs
{
    ByteBuffer netData;
    ByteBuffer[] appData;
    private int offset;
    private int len;
    private int netPos;
    private int netLim;
    private int[] appPoss;
    private int[] appLims;
    private int appRemaining;
    private boolean wrapMethod;
    
    EngineArgs(final ByteBuffer[] appData, final int offset, final int len, final ByteBuffer netData) {
        this.appRemaining = 0;
        this.wrapMethod = true;
        this.init(netData, appData, offset, len);
    }
    
    EngineArgs(final ByteBuffer netData, final ByteBuffer[] appData, final int offset, final int len) {
        this.appRemaining = 0;
        this.wrapMethod = false;
        this.init(netData, appData, offset, len);
    }
    
    private void init(final ByteBuffer netData, final ByteBuffer[] appData, final int offset, final int len) {
        if (netData == null || appData == null) {
            throw new IllegalArgumentException("src/dst is null");
        }
        if (offset < 0 || len < 0 || offset > appData.length - len) {
            throw new IndexOutOfBoundsException();
        }
        if (this.wrapMethod && netData.isReadOnly()) {
            throw new ReadOnlyBufferException();
        }
        this.netPos = netData.position();
        this.netLim = netData.limit();
        this.appPoss = new int[appData.length];
        this.appLims = new int[appData.length];
        for (int i = offset; i < offset + len; ++i) {
            if (appData[i] == null) {
                throw new IllegalArgumentException("appData[" + i + "] == null");
            }
            if (!this.wrapMethod && appData[i].isReadOnly()) {
                throw new ReadOnlyBufferException();
            }
            this.appRemaining += appData[i].remaining();
            this.appPoss[i] = appData[i].position();
            this.appLims[i] = appData[i].limit();
        }
        this.netData = netData;
        this.appData = appData;
        this.offset = offset;
        this.len = len;
    }
    
    void gather(int spaceLeft) {
        int amount;
        for (int i = this.offset; i < this.offset + this.len && spaceLeft > 0; spaceLeft -= amount, ++i) {
            amount = Math.min(this.appData[i].remaining(), spaceLeft);
            this.appData[i].limit(this.appData[i].position() + amount);
            this.netData.put(this.appData[i]);
            this.appRemaining -= amount;
        }
    }
    
    void scatter(final ByteBuffer readyData) {
        int amount;
        for (int amountLeft = readyData.remaining(), i = this.offset; i < this.offset + this.len && amountLeft > 0; amountLeft -= amount, ++i) {
            amount = Math.min(this.appData[i].remaining(), amountLeft);
            readyData.limit(readyData.position() + amount);
            this.appData[i].put(readyData);
        }
        assert readyData.remaining() == 0;
    }
    
    int getAppRemaining() {
        return this.appRemaining;
    }
    
    int deltaNet() {
        return this.netData.position() - this.netPos;
    }
    
    int deltaApp() {
        int sum = 0;
        for (int i = this.offset; i < this.offset + this.len; ++i) {
            sum += this.appData[i].position() - this.appPoss[i];
        }
        return sum;
    }
    
    void resetPos() {
        this.netData.position(this.netPos);
        for (int i = this.offset; i < this.offset + this.len; ++i) {
            this.appData[i].position(this.appPoss[i]);
        }
    }
    
    void resetLim() {
        this.netData.limit(this.netLim);
        for (int i = this.offset; i < this.offset + this.len; ++i) {
            this.appData[i].limit(this.appLims[i]);
        }
    }
}
