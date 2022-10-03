package com.sun.org.apache.bcel.internal.classfile;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public final class Signature extends Attribute
{
    private int signature_index;
    
    public Signature(final Signature c) {
        this(c.getNameIndex(), c.getLength(), c.getSignatureIndex(), c.getConstantPool());
    }
    
    Signature(final int name_index, final int length, final DataInputStream file, final ConstantPool constant_pool) throws IOException {
        this(name_index, length, file.readUnsignedShort(), constant_pool);
    }
    
    public Signature(final int name_index, final int length, final int signature_index, final ConstantPool constant_pool) {
        super((byte)10, name_index, length, constant_pool);
        this.signature_index = signature_index;
    }
    
    @Override
    public void accept(final Visitor v) {
        System.err.println("Visiting non-standard Signature object");
        v.visitSignature(this);
    }
    
    @Override
    public final void dump(final DataOutputStream file) throws IOException {
        super.dump(file);
        file.writeShort(this.signature_index);
    }
    
    public final int getSignatureIndex() {
        return this.signature_index;
    }
    
    public final void setSignatureIndex(final int signature_index) {
        this.signature_index = signature_index;
    }
    
    public final String getSignature() {
        final ConstantUtf8 c = (ConstantUtf8)this.constant_pool.getConstant(this.signature_index, (byte)1);
        return c.getBytes();
    }
    
    private static boolean identStart(final int ch) {
        return ch == 84 || ch == 76;
    }
    
    private static boolean identPart(final int ch) {
        return ch == 47 || ch == 59;
    }
    
    private static final void matchIdent(final MyByteArrayInputStream in, final StringBuffer buf) {
        int ch;
        if ((ch = in.read()) == -1) {
            throw new RuntimeException("Illegal signature: " + in.getData() + " no ident, reaching EOF");
        }
        if (!identStart(ch)) {
            final StringBuffer buf2 = new StringBuffer();
            int count = 1;
            while (Character.isJavaIdentifierPart((char)ch)) {
                buf2.append((char)ch);
                ++count;
                ch = in.read();
            }
            if (ch == 58) {
                in.skip("Ljava/lang/Object".length());
                buf.append(buf2);
                ch = in.read();
                in.unread();
            }
            else {
                for (int i = 0; i < count; ++i) {
                    in.unread();
                }
            }
            return;
        }
        final StringBuffer buf2 = new StringBuffer();
        ch = in.read();
        do {
            buf2.append((char)ch);
            ch = in.read();
        } while (ch != -1 && (Character.isJavaIdentifierPart((char)ch) || ch == 47));
        buf.append(buf2.toString().replace('/', '.'));
        if (ch != -1) {
            in.unread();
        }
    }
    
    private static final void matchGJIdent(final MyByteArrayInputStream in, final StringBuffer buf) {
        matchIdent(in, buf);
        int ch = in.read();
        if (ch == 60 || ch == 40) {
            buf.append((char)ch);
            matchGJIdent(in, buf);
            while ((ch = in.read()) != 62 && ch != 41) {
                if (ch == -1) {
                    throw new RuntimeException("Illegal signature: " + in.getData() + " reaching EOF");
                }
                buf.append(", ");
                in.unread();
                matchGJIdent(in, buf);
            }
            buf.append((char)ch);
        }
        else {
            in.unread();
        }
        ch = in.read();
        if (identStart(ch)) {
            in.unread();
            matchGJIdent(in, buf);
        }
        else {
            if (ch == 41) {
                in.unread();
                return;
            }
            if (ch != 59) {
                throw new RuntimeException("Illegal signature: " + in.getData() + " read " + (char)ch);
            }
        }
    }
    
    public static String translate(final String s) {
        final StringBuffer buf = new StringBuffer();
        matchGJIdent(new MyByteArrayInputStream(s), buf);
        return buf.toString();
    }
    
    public static final boolean isFormalParameterList(final String s) {
        return s.startsWith("<") && s.indexOf(58) > 0;
    }
    
    public static final boolean isActualParameterList(final String s) {
        return s.startsWith("L") && s.endsWith(">;");
    }
    
    @Override
    public final String toString() {
        final String s = this.getSignature();
        return "Signature(" + s + ")";
    }
    
    @Override
    public Attribute copy(final ConstantPool constant_pool) {
        return (Signature)this.clone();
    }
    
    private static final class MyByteArrayInputStream extends ByteArrayInputStream
    {
        MyByteArrayInputStream(final String data) {
            super(data.getBytes());
        }
        
        final int mark() {
            return this.pos;
        }
        
        final String getData() {
            return new String(this.buf);
        }
        
        final void reset(final int p) {
            this.pos = p;
        }
        
        final void unread() {
            if (this.pos > 0) {
                --this.pos;
            }
        }
    }
}
