package com.sun.corba.se.impl.encoding;

import org.omg.CORBA.CompletionStatus;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;

public class CDROutputStream_1_2 extends CDROutputStream_1_1
{
    protected boolean primitiveAcrossFragmentedChunk;
    protected boolean specialChunk;
    private boolean headerPadding;
    
    public CDROutputStream_1_2() {
        this.primitiveAcrossFragmentedChunk = false;
        this.specialChunk = false;
    }
    
    @Override
    protected void handleSpecialChunkBegin(final int n) {
        if (this.inBlock && n + this.bbwi.position() > this.bbwi.buflen) {
            final int position = this.bbwi.position();
            this.bbwi.position(this.blockSizeIndex - 4);
            this.writeLongWithoutAlign(position - this.blockSizeIndex + n);
            this.bbwi.position(position);
            this.specialChunk = true;
        }
    }
    
    @Override
    protected void handleSpecialChunkEnd() {
        if (this.inBlock && this.specialChunk) {
            this.inBlock = false;
            this.blockSizeIndex = -1;
            this.blockSizePosition = -1;
            this.start_block();
            this.specialChunk = false;
        }
    }
    
    private void checkPrimitiveAcrossFragmentedChunk() {
        if (this.primitiveAcrossFragmentedChunk) {
            this.primitiveAcrossFragmentedChunk = false;
            this.inBlock = false;
            this.blockSizeIndex = -1;
            this.blockSizePosition = -1;
            this.start_block();
        }
    }
    
    @Override
    public void write_octet(final byte b) {
        super.write_octet(b);
        this.checkPrimitiveAcrossFragmentedChunk();
    }
    
    @Override
    public void write_short(final short n) {
        super.write_short(n);
        this.checkPrimitiveAcrossFragmentedChunk();
    }
    
    @Override
    public void write_long(final int n) {
        super.write_long(n);
        this.checkPrimitiveAcrossFragmentedChunk();
    }
    
    @Override
    public void write_longlong(final long n) {
        super.write_longlong(n);
        this.checkPrimitiveAcrossFragmentedChunk();
    }
    
    @Override
    void setHeaderPadding(final boolean headerPadding) {
        this.headerPadding = headerPadding;
    }
    
    @Override
    protected void alignAndReserve(final int n, final int n2) {
        if (this.headerPadding) {
            this.headerPadding = false;
            this.alignOnBoundary(8);
        }
        this.bbwi.position(this.bbwi.position() + this.computeAlignment(n));
        if (this.bbwi.position() + n2 > this.bbwi.buflen) {
            this.grow(n, n2);
        }
    }
    
    @Override
    protected void grow(final int n, final int needed) {
        final int position = this.bbwi.position();
        final boolean b = this.inBlock && !this.specialChunk;
        if (b) {
            final int position2 = this.bbwi.position();
            this.bbwi.position(this.blockSizeIndex - 4);
            this.writeLongWithoutAlign(position2 - this.blockSizeIndex + needed);
            this.bbwi.position(position2);
        }
        this.bbwi.needed = needed;
        this.bufferManagerWrite.overflow(this.bbwi);
        if (this.bbwi.fragmented) {
            this.bbwi.fragmented = false;
            this.fragmentOffset += position - this.bbwi.position();
            if (b) {
                this.primitiveAcrossFragmentedChunk = true;
            }
        }
    }
    
    @Override
    public GIOPVersion getGIOPVersion() {
        return GIOPVersion.V1_2;
    }
    
    @Override
    public void write_wchar(final char c) {
        final CodeSetConversion.CTBConverter wCharConverter = this.getWCharConverter();
        wCharConverter.convert(c);
        this.handleSpecialChunkBegin(1 + wCharConverter.getNumBytes());
        this.write_octet((byte)wCharConverter.getNumBytes());
        this.internalWriteOctetArray(wCharConverter.getBytes(), 0, wCharConverter.getNumBytes());
        this.handleSpecialChunkEnd();
    }
    
    @Override
    public void write_wchar_array(final char[] array, final int n, final int n2) {
        if (array == null) {
            throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
        }
        final CodeSetConversion.CTBConverter wCharConverter = this.getWCharConverter();
        int n3 = 0;
        final byte[] array2 = new byte[(int)Math.ceil(wCharConverter.getMaxBytesPerChar() * n2) + n2];
        for (int i = 0; i < n2; ++i) {
            wCharConverter.convert(array[n + i]);
            array2[n3++] = (byte)wCharConverter.getNumBytes();
            System.arraycopy(wCharConverter.getBytes(), 0, array2, n3, wCharConverter.getNumBytes());
            n3 += wCharConverter.getNumBytes();
        }
        this.handleSpecialChunkBegin(n3);
        this.internalWriteOctetArray(array2, 0, n3);
        this.handleSpecialChunkEnd();
    }
    
    @Override
    public void write_wstring(final String s) {
        if (s == null) {
            throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
        }
        if (s.length() == 0) {
            this.write_long(0);
            return;
        }
        final CodeSetConversion.CTBConverter wCharConverter = this.getWCharConverter();
        wCharConverter.convert(s);
        this.handleSpecialChunkBegin(this.computeAlignment(4) + 4 + wCharConverter.getNumBytes());
        this.write_long(wCharConverter.getNumBytes());
        this.internalWriteOctetArray(wCharConverter.getBytes(), 0, wCharConverter.getNumBytes());
        this.handleSpecialChunkEnd();
    }
}
