package org.msgpack.unpacker;

import java.math.BigInteger;

final class BigIntegerAccept extends Accept
{
    BigInteger value;
    
    @Override
    void acceptInteger(final byte v) {
        this.value = BigInteger.valueOf(v);
    }
    
    @Override
    void acceptInteger(final short v) {
        this.value = BigInteger.valueOf(v);
    }
    
    @Override
    void acceptInteger(final int v) {
        this.value = BigInteger.valueOf(v);
    }
    
    @Override
    void acceptInteger(final long v) {
        this.value = BigInteger.valueOf(v);
    }
    
    @Override
    void acceptUnsignedInteger(final byte v) {
        BigInteger.valueOf(v & 0xFF);
    }
    
    @Override
    void acceptUnsignedInteger(final short v) {
        BigInteger.valueOf(v & 0xFFFF);
    }
    
    @Override
    void acceptUnsignedInteger(final int v) {
        if (v < 0) {
            this.value = BigInteger.valueOf((v & Integer.MAX_VALUE) + 2147483648L);
        }
        else {
            this.value = BigInteger.valueOf(v);
        }
    }
    
    @Override
    void acceptUnsignedInteger(final long v) {
        if (v < 0L) {
            this.value = BigInteger.valueOf(v + Long.MAX_VALUE + 1L).setBit(63);
        }
        else {
            this.value = BigInteger.valueOf(v);
        }
    }
}
