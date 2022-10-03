package com.sun.java.util.jar.pack;

import java.util.List;
import java.util.Iterator;
import java.io.IOException;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

class ClassWriter
{
    int verbose;
    Package pkg;
    Package.Class cls;
    DataOutputStream out;
    ConstantPool.Index cpIndex;
    ConstantPool.Index bsmIndex;
    ByteArrayOutputStream buf;
    DataOutputStream bufOut;
    
    ClassWriter(final Package.Class cls, final OutputStream outputStream) throws IOException {
        this.buf = new ByteArrayOutputStream();
        this.bufOut = new DataOutputStream(this.buf);
        this.pkg = cls.getPackage();
        this.cls = cls;
        this.verbose = this.pkg.verbose;
        this.out = new DataOutputStream(new BufferedOutputStream(outputStream));
        this.cpIndex = ConstantPool.makeIndex(cls.toString(), cls.getCPMap());
        this.cpIndex.flattenSigs = true;
        if (cls.hasBootstrapMethods()) {
            this.bsmIndex = ConstantPool.makeIndex(this.cpIndex.debugName + ".BootstrapMethods", cls.getBootstrapMethodMap());
        }
        if (this.verbose > 1) {
            Utils.log.fine("local CP=" + ((this.verbose > 2) ? this.cpIndex.dumpString() : this.cpIndex.toString()));
        }
    }
    
    private void writeShort(final int n) throws IOException {
        this.out.writeShort(n);
    }
    
    private void writeInt(final int n) throws IOException {
        this.out.writeInt(n);
    }
    
    private void writeRef(final ConstantPool.Entry entry) throws IOException {
        this.writeRef(entry, this.cpIndex);
    }
    
    private void writeRef(final ConstantPool.Entry entry, final ConstantPool.Index index) throws IOException {
        this.writeShort((entry == null) ? 0 : index.indexOf(entry));
    }
    
    void write() throws IOException {
        boolean b = false;
        try {
            if (this.verbose > 1) {
                Utils.log.fine("...writing " + this.cls);
            }
            this.writeMagicNumbers();
            this.writeConstantPool();
            this.writeHeader();
            this.writeMembers(false);
            this.writeMembers(true);
            this.writeAttributes(0, this.cls);
            this.out.flush();
            b = true;
        }
        finally {
            if (!b) {
                Utils.log.warning("Error on output of " + this.cls);
            }
        }
    }
    
    void writeMagicNumbers() throws IOException {
        this.writeInt(this.cls.magic);
        this.writeShort(this.cls.version.minor);
        this.writeShort(this.cls.version.major);
    }
    
    void writeConstantPool() throws IOException {
        final ConstantPool.Entry[] cpMap = this.cls.cpMap;
        this.writeShort(cpMap.length);
        for (int i = 0; i < cpMap.length; ++i) {
            final ConstantPool.Entry entry = cpMap[i];
            assert entry == null == (i == 0 || (cpMap[i - 1] != null && cpMap[i - 1].isDoubleWord()));
            if (entry != null) {
                final byte tag = entry.getTag();
                if (this.verbose > 2) {
                    Utils.log.fine("   CP[" + i + "] = " + entry);
                }
                this.out.write(tag);
                switch (tag) {
                    case 13: {
                        throw new AssertionError((Object)"CP should have Signatures remapped to Utf8");
                    }
                    case 1: {
                        this.out.writeUTF(entry.stringValue());
                        break;
                    }
                    case 3: {
                        this.out.writeInt(((ConstantPool.NumberEntry)entry).numberValue().intValue());
                        break;
                    }
                    case 4: {
                        this.out.writeInt(Float.floatToRawIntBits(((ConstantPool.NumberEntry)entry).numberValue().floatValue()));
                        break;
                    }
                    case 5: {
                        this.out.writeLong(((ConstantPool.NumberEntry)entry).numberValue().longValue());
                        break;
                    }
                    case 6: {
                        this.out.writeLong(Double.doubleToRawLongBits(((ConstantPool.NumberEntry)entry).numberValue().doubleValue()));
                        break;
                    }
                    case 7:
                    case 8:
                    case 16: {
                        this.writeRef(entry.getRef(0));
                        break;
                    }
                    case 15: {
                        final ConstantPool.MethodHandleEntry methodHandleEntry = (ConstantPool.MethodHandleEntry)entry;
                        this.out.writeByte(methodHandleEntry.refKind);
                        this.writeRef(methodHandleEntry.getRef(0));
                        break;
                    }
                    case 9:
                    case 10:
                    case 11:
                    case 12: {
                        this.writeRef(entry.getRef(0));
                        this.writeRef(entry.getRef(1));
                        break;
                    }
                    case 18: {
                        this.writeRef(entry.getRef(0), this.bsmIndex);
                        this.writeRef(entry.getRef(1));
                        break;
                    }
                    case 17: {
                        throw new AssertionError((Object)"CP should have BootstrapMethods moved to side-table");
                    }
                    default: {
                        throw new IOException("Bad constant pool tag " + tag);
                    }
                }
            }
        }
    }
    
    void writeHeader() throws IOException {
        this.writeShort(this.cls.flags);
        this.writeRef(this.cls.thisClass);
        this.writeRef(this.cls.superClass);
        this.writeShort(this.cls.interfaces.length);
        for (int i = 0; i < this.cls.interfaces.length; ++i) {
            this.writeRef(this.cls.interfaces[i]);
        }
    }
    
    void writeMembers(final boolean b) throws IOException {
        Object o;
        if (!b) {
            o = this.cls.getFields();
        }
        else {
            o = this.cls.getMethods();
        }
        this.writeShort(((List)o).size());
        final Iterator iterator = ((List)o).iterator();
        while (iterator.hasNext()) {
            this.writeMember((Package.Class.Member)iterator.next(), b);
        }
    }
    
    void writeMember(final Package.Class.Member member, final boolean b) throws IOException {
        if (this.verbose > 2) {
            Utils.log.fine("writeMember " + member);
        }
        this.writeShort(member.flags);
        this.writeRef(member.getDescriptor().nameRef);
        this.writeRef(member.getDescriptor().typeRef);
        this.writeAttributes(b ? 2 : 1, member);
    }
    
    private void reorderBSMandICS(final Attribute.Holder holder) {
        final Attribute attribute = holder.getAttribute(Package.attrBootstrapMethodsEmpty);
        if (attribute == null) {
            return;
        }
        final Attribute attribute2 = holder.getAttribute(Package.attrInnerClassesEmpty);
        if (attribute2 == null) {
            return;
        }
        final int index = holder.attributes.indexOf(attribute);
        final int index2 = holder.attributes.indexOf(attribute2);
        if (index > index2) {
            holder.attributes.remove(attribute);
            holder.attributes.add(index2, attribute);
        }
    }
    
    void writeAttributes(final int n, final Attribute.Holder holder) throws IOException {
        if (holder.attributes == null) {
            this.writeShort(0);
            return;
        }
        if (holder instanceof Package.Class) {
            this.reorderBSMandICS(holder);
        }
        this.writeShort(holder.attributes.size());
        for (final Attribute attribute : holder.attributes) {
            attribute.finishRefs(this.cpIndex);
            this.writeRef(attribute.getNameRef());
            if (attribute.layout() == Package.attrCodeEmpty || attribute.layout() == Package.attrBootstrapMethodsEmpty || attribute.layout() == Package.attrInnerClassesEmpty) {
                final DataOutputStream out = this.out;
                assert this.out != this.bufOut;
                this.buf.reset();
                this.out = this.bufOut;
                if ("Code".equals(attribute.name())) {
                    this.writeCode(((Package.Class.Method)holder).code);
                }
                else if ("BootstrapMethods".equals(attribute.name())) {
                    assert holder == this.cls;
                    this.writeBootstrapMethods(this.cls);
                }
                else {
                    if (!"InnerClasses".equals(attribute.name())) {
                        throw new AssertionError();
                    }
                    assert holder == this.cls;
                    this.writeInnerClasses(this.cls);
                }
                this.out = out;
                if (this.verbose > 2) {
                    Utils.log.fine("Attribute " + attribute.name() + " [" + this.buf.size() + "]");
                }
                this.writeInt(this.buf.size());
                this.buf.writeTo(this.out);
            }
            else {
                if (this.verbose > 2) {
                    Utils.log.fine("Attribute " + attribute.name() + " [" + attribute.size() + "]");
                }
                this.writeInt(attribute.size());
                this.out.write(attribute.bytes());
            }
        }
    }
    
    void writeCode(final Code code) throws IOException {
        code.finishRefs(this.cpIndex);
        this.writeShort(code.max_stack);
        this.writeShort(code.max_locals);
        this.writeInt(code.bytes.length);
        this.out.write(code.bytes);
        final int handlerCount = code.getHandlerCount();
        this.writeShort(handlerCount);
        for (int i = 0; i < handlerCount; ++i) {
            this.writeShort(code.handler_start[i]);
            this.writeShort(code.handler_end[i]);
            this.writeShort(code.handler_catch[i]);
            this.writeRef(code.handler_class[i]);
        }
        this.writeAttributes(3, code);
    }
    
    void writeBootstrapMethods(final Package.Class class1) throws IOException {
        final List<ConstantPool.BootstrapMethodEntry> bootstrapMethods = class1.getBootstrapMethods();
        this.writeShort(bootstrapMethods.size());
        for (final ConstantPool.BootstrapMethodEntry bootstrapMethodEntry : bootstrapMethods) {
            this.writeRef(bootstrapMethodEntry.bsmRef);
            this.writeShort(bootstrapMethodEntry.argRefs.length);
            final ConstantPool.Entry[] argRefs = bootstrapMethodEntry.argRefs;
            for (int length = argRefs.length, i = 0; i < length; ++i) {
                this.writeRef(argRefs[i]);
            }
        }
    }
    
    void writeInnerClasses(final Package.Class class1) throws IOException {
        final List<Package.InnerClass> innerClasses = class1.getInnerClasses();
        this.writeShort(innerClasses.size());
        for (final Package.InnerClass innerClass : innerClasses) {
            this.writeRef(innerClass.thisClass);
            this.writeRef(innerClass.outerClass);
            this.writeRef(innerClass.name);
            this.writeShort(innerClass.flags);
        }
    }
}
