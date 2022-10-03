package com.sun.corba.se.impl.encoding;

import java.util.Iterator;
import sun.corba.OutputStreamFactory;
import java.nio.ByteBuffer;
import org.omg.CORBA.CompletionStatus;
import java.util.HashMap;
import sun.corba.EncapsInputStreamFactory;
import org.omg.CORBA.portable.InputStream;
import com.sun.corba.se.spi.orb.ORB;
import java.util.Map;
import org.omg.CORBA_2_3.portable.OutputStream;

public final class TypeCodeOutputStream extends EncapsOutputStream
{
    private OutputStream enclosure;
    private Map typeMap;
    private boolean isEncapsulation;
    
    public TypeCodeOutputStream(final ORB orb) {
        super(orb, false);
        this.enclosure = null;
        this.typeMap = null;
        this.isEncapsulation = false;
    }
    
    public TypeCodeOutputStream(final ORB orb, final boolean b) {
        super(orb, b);
        this.enclosure = null;
        this.typeMap = null;
        this.isEncapsulation = false;
    }
    
    @Override
    public InputStream create_input_stream() {
        return EncapsInputStreamFactory.newTypeCodeInputStream(this.orb(), this.getByteBuffer(), this.getIndex(), this.isLittleEndian(), this.getGIOPVersion());
    }
    
    public void setEnclosingOutputStream(final OutputStream enclosure) {
        this.enclosure = enclosure;
    }
    
    public TypeCodeOutputStream getTopLevelStream() {
        if (this.enclosure == null) {
            return this;
        }
        if (this.enclosure instanceof TypeCodeOutputStream) {
            return ((TypeCodeOutputStream)this.enclosure).getTopLevelStream();
        }
        return this;
    }
    
    public int getTopLevelPosition() {
        if (this.enclosure != null && this.enclosure instanceof TypeCodeOutputStream) {
            int n = ((TypeCodeOutputStream)this.enclosure).getTopLevelPosition() + this.getPosition();
            if (this.isEncapsulation) {
                n += 4;
            }
            return n;
        }
        return this.getPosition();
    }
    
    public void addIDAtPosition(final String s, final int n) {
        if (this.typeMap == null) {
            this.typeMap = new HashMap(16);
        }
        this.typeMap.put(s, new Integer(n));
    }
    
    public int getPositionForID(final String s) {
        if (this.typeMap == null) {
            throw this.wrapper.refTypeIndirType(CompletionStatus.COMPLETED_NO);
        }
        return this.typeMap.get(s);
    }
    
    public void writeRawBuffer(final org.omg.CORBA.portable.OutputStream outputStream, final int n) {
        outputStream.write_long(n);
        final ByteBuffer byteBuffer = this.getByteBuffer();
        if (byteBuffer.hasArray()) {
            outputStream.write_octet_array(byteBuffer.array(), 4, this.getIndex() - 4);
        }
        else {
            final byte[] array = new byte[byteBuffer.limit()];
            for (int i = 0; i < array.length; ++i) {
                array[i] = byteBuffer.get(i);
            }
            outputStream.write_octet_array(array, 4, this.getIndex() - 4);
        }
    }
    
    public TypeCodeOutputStream createEncapsulation(final org.omg.CORBA.ORB orb) {
        final TypeCodeOutputStream typeCodeOutputStream = sun.corba.OutputStreamFactory.newTypeCodeOutputStream((ORB)orb, this.isLittleEndian());
        typeCodeOutputStream.setEnclosingOutputStream(this);
        typeCodeOutputStream.makeEncapsulation();
        return typeCodeOutputStream;
    }
    
    protected void makeEncapsulation() {
        this.putEndian();
        this.isEncapsulation = true;
    }
    
    public static TypeCodeOutputStream wrapOutputStream(final OutputStream enclosingOutputStream) {
        final TypeCodeOutputStream typeCodeOutputStream = sun.corba.OutputStreamFactory.newTypeCodeOutputStream((ORB)enclosingOutputStream.orb(), enclosingOutputStream instanceof CDROutputStream && ((CDROutputStream)enclosingOutputStream).isLittleEndian());
        typeCodeOutputStream.setEnclosingOutputStream(enclosingOutputStream);
        return typeCodeOutputStream;
    }
    
    public int getPosition() {
        return this.getIndex();
    }
    
    public int getRealIndex(final int n) {
        return this.getTopLevelPosition();
    }
    
    public byte[] getTypeCodeBuffer() {
        final ByteBuffer byteBuffer = this.getByteBuffer();
        final byte[] array = new byte[this.getIndex() - 4];
        for (int i = 0; i < array.length; ++i) {
            array[i] = byteBuffer.get(i + 4);
        }
        return array;
    }
    
    public void printTypeMap() {
        System.out.println("typeMap = {");
        for (final String s : this.typeMap.keySet()) {
            System.out.println("  key = " + s + ", value = " + this.typeMap.get(s));
        }
        System.out.println("}");
    }
}
