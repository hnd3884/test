package jdk.internal.org.objectweb.asm.commons;

import java.security.MessageDigest;
import java.io.IOException;
import java.io.DataOutput;
import java.util.Arrays;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import jdk.internal.org.objectweb.asm.FieldVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import java.util.ArrayList;
import java.util.Collection;
import jdk.internal.org.objectweb.asm.ClassVisitor;

public class SerialVersionUIDAdder extends ClassVisitor
{
    private boolean computeSVUID;
    private boolean hasSVUID;
    private int access;
    private String name;
    private String[] interfaces;
    private Collection<Item> svuidFields;
    private boolean hasStaticInitializer;
    private Collection<Item> svuidConstructors;
    private Collection<Item> svuidMethods;
    
    public SerialVersionUIDAdder(final ClassVisitor classVisitor) {
        this(327680, classVisitor);
        if (this.getClass() != SerialVersionUIDAdder.class) {
            throw new IllegalStateException();
        }
    }
    
    protected SerialVersionUIDAdder(final int n, final ClassVisitor classVisitor) {
        super(n, classVisitor);
        this.svuidFields = new ArrayList<Item>();
        this.svuidConstructors = new ArrayList<Item>();
        this.svuidMethods = new ArrayList<Item>();
    }
    
    @Override
    public void visit(final int n, final int access, final String name, final String s, final String s2, final String[] array) {
        this.computeSVUID = ((access & 0x200) == 0x0);
        if (this.computeSVUID) {
            this.name = name;
            this.access = access;
            System.arraycopy(array, 0, this.interfaces = new String[array.length], 0, array.length);
        }
        super.visit(n, access, name, s, s2, array);
    }
    
    @Override
    public MethodVisitor visitMethod(final int n, final String s, final String s2, final String s3, final String[] array) {
        if (this.computeSVUID) {
            if ("<clinit>".equals(s)) {
                this.hasStaticInitializer = true;
            }
            final int n2 = n & 0xD3F;
            if ((n & 0x2) == 0x0) {
                if ("<init>".equals(s)) {
                    this.svuidConstructors.add(new Item(s, n2, s2));
                }
                else if (!"<clinit>".equals(s)) {
                    this.svuidMethods.add(new Item(s, n2, s2));
                }
            }
        }
        return super.visitMethod(n, s, s2, s3, array);
    }
    
    @Override
    public FieldVisitor visitField(final int n, final String s, final String s2, final String s3, final Object o) {
        if (this.computeSVUID) {
            if ("serialVersionUID".equals(s)) {
                this.computeSVUID = false;
                this.hasSVUID = true;
            }
            if ((n & 0x2) == 0x0 || (n & 0x88) == 0x0) {
                this.svuidFields.add(new Item(s, n & 0xDF, s2));
            }
        }
        return super.visitField(n, s, s2, s3, o);
    }
    
    @Override
    public void visitInnerClass(final String s, final String s2, final String s3, final int access) {
        if (this.name != null && this.name.equals(s)) {
            this.access = access;
        }
        super.visitInnerClass(s, s2, s3, access);
    }
    
    @Override
    public void visitEnd() {
        if (this.computeSVUID && !this.hasSVUID) {
            try {
                this.addSVUID(this.computeSVUID());
            }
            catch (final Throwable t) {
                throw new RuntimeException("Error while computing SVUID for " + this.name, t);
            }
        }
        super.visitEnd();
    }
    
    public boolean hasSVUID() {
        return this.hasSVUID;
    }
    
    protected void addSVUID(final long n) {
        final FieldVisitor visitField = super.visitField(24, "serialVersionUID", "J", null, n);
        if (visitField != null) {
            visitField.visitEnd();
        }
    }
    
    protected long computeSVUID() throws IOException {
        DataOutputStream dataOutputStream = null;
        long n = 0L;
        try {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            dataOutputStream.writeUTF(this.name.replace('/', '.'));
            dataOutputStream.writeInt(this.access & 0x611);
            Arrays.sort(this.interfaces);
            for (int i = 0; i < this.interfaces.length; ++i) {
                dataOutputStream.writeUTF(this.interfaces[i].replace('/', '.'));
            }
            writeItems(this.svuidFields, dataOutputStream, false);
            if (this.hasStaticInitializer) {
                dataOutputStream.writeUTF("<clinit>");
                dataOutputStream.writeInt(8);
                dataOutputStream.writeUTF("()V");
            }
            writeItems(this.svuidConstructors, dataOutputStream, true);
            writeItems(this.svuidMethods, dataOutputStream, true);
            dataOutputStream.flush();
            final byte[] computeSHAdigest = this.computeSHAdigest(byteArrayOutputStream.toByteArray());
            for (int j = Math.min(computeSHAdigest.length, 8) - 1; j >= 0; --j) {
                n = (n << 8 | (long)(computeSHAdigest[j] & 0xFF));
            }
        }
        finally {
            if (dataOutputStream != null) {
                dataOutputStream.close();
            }
        }
        return n;
    }
    
    protected byte[] computeSHAdigest(final byte[] array) {
        try {
            return MessageDigest.getInstance("SHA").digest(array);
        }
        catch (final Exception ex) {
            throw new UnsupportedOperationException(ex.toString());
        }
    }
    
    private static void writeItems(final Collection<Item> collection, final DataOutput dataOutput, final boolean b) throws IOException {
        final int size = collection.size();
        final Item[] array = collection.toArray(new Item[size]);
        Arrays.sort(array);
        for (int i = 0; i < size; ++i) {
            dataOutput.writeUTF(array[i].name);
            dataOutput.writeInt(array[i].access);
            dataOutput.writeUTF(b ? array[i].desc.replace('/', '.') : array[i].desc);
        }
    }
    
    private static class Item implements Comparable<Item>
    {
        final String name;
        final int access;
        final String desc;
        
        Item(final String name, final int access, final String desc) {
            this.name = name;
            this.access = access;
            this.desc = desc;
        }
        
        @Override
        public int compareTo(final Item item) {
            int n = this.name.compareTo(item.name);
            if (n == 0) {
                n = this.desc.compareTo(item.desc);
            }
            return n;
        }
        
        @Override
        public boolean equals(final Object o) {
            return o instanceof Item && this.compareTo((Item)o) == 0;
        }
        
        @Override
        public int hashCode() {
            return (this.name + this.desc).hashCode();
        }
    }
}
