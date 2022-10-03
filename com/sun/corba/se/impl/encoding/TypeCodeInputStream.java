package com.sun.corba.se.impl.encoding;

import java.util.Iterator;
import sun.corba.EncapsInputStreamFactory;
import java.util.HashMap;
import com.sun.corba.se.impl.corba.TypeCodeImpl;
import java.nio.ByteBuffer;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import org.omg.CORBA.ORB;
import org.omg.CORBA_2_3.portable.InputStream;
import java.util.Map;

public class TypeCodeInputStream extends EncapsInputStream implements TypeCodeReader
{
    private Map typeMap;
    private InputStream enclosure;
    private boolean isEncapsulation;
    
    public TypeCodeInputStream(final ORB orb, final byte[] array, final int n) {
        super(orb, array, n);
        this.typeMap = null;
        this.enclosure = null;
        this.isEncapsulation = false;
    }
    
    public TypeCodeInputStream(final ORB orb, final byte[] array, final int n, final boolean b, final GIOPVersion giopVersion) {
        super(orb, array, n, b, giopVersion);
        this.typeMap = null;
        this.enclosure = null;
        this.isEncapsulation = false;
    }
    
    public TypeCodeInputStream(final ORB orb, final ByteBuffer byteBuffer, final int n, final boolean b, final GIOPVersion giopVersion) {
        super(orb, byteBuffer, n, b, giopVersion);
        this.typeMap = null;
        this.enclosure = null;
        this.isEncapsulation = false;
    }
    
    @Override
    public void addTypeCodeAtPosition(final TypeCodeImpl typeCodeImpl, final int n) {
        if (this.typeMap == null) {
            this.typeMap = new HashMap(16);
        }
        this.typeMap.put(new Integer(n), typeCodeImpl);
    }
    
    @Override
    public TypeCodeImpl getTypeCodeAtPosition(final int n) {
        if (this.typeMap == null) {
            return null;
        }
        return this.typeMap.get(new Integer(n));
    }
    
    @Override
    public void setEnclosingInputStream(final InputStream enclosure) {
        this.enclosure = enclosure;
    }
    
    @Override
    public TypeCodeReader getTopLevelStream() {
        if (this.enclosure == null) {
            return this;
        }
        if (this.enclosure instanceof TypeCodeReader) {
            return ((TypeCodeReader)this.enclosure).getTopLevelStream();
        }
        return this;
    }
    
    @Override
    public int getTopLevelPosition() {
        if (this.enclosure != null && this.enclosure instanceof TypeCodeReader) {
            return ((TypeCodeReader)this.enclosure).getTopLevelPosition() - this.getBufferLength() + this.getPosition();
        }
        return this.getPosition();
    }
    
    public static TypeCodeInputStream readEncapsulation(final InputStream enclosingInputStream, final ORB orb) {
        final byte[] array = new byte[enclosingInputStream.read_long()];
        enclosingInputStream.read_octet_array(array, 0, array.length);
        TypeCodeInputStream typeCodeInputStream;
        if (enclosingInputStream instanceof CDRInputStream) {
            typeCodeInputStream = EncapsInputStreamFactory.newTypeCodeInputStream(orb, array, array.length, ((CDRInputStream)enclosingInputStream).isLittleEndian(), ((CDRInputStream)enclosingInputStream).getGIOPVersion());
        }
        else {
            typeCodeInputStream = EncapsInputStreamFactory.newTypeCodeInputStream(orb, array, array.length);
        }
        typeCodeInputStream.setEnclosingInputStream(enclosingInputStream);
        typeCodeInputStream.makeEncapsulation();
        return typeCodeInputStream;
    }
    
    protected void makeEncapsulation() {
        this.consumeEndian();
        this.isEncapsulation = true;
    }
    
    @Override
    public void printTypeMap() {
        System.out.println("typeMap = {");
        for (final Integer n : this.typeMap.keySet()) {
            System.out.println("  key = " + (int)n + ", value = " + ((TypeCodeImpl)this.typeMap.get(n)).description());
        }
        System.out.println("}");
    }
}
