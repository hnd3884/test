package com.sun.java.util.jar.pack;

import java.util.SortedMap;
import java.util.HashMap;
import java.util.Map;

class TLGlobals
{
    final PropMap props;
    private final Map<String, ConstantPool.Utf8Entry> utf8Entries;
    private final Map<String, ConstantPool.ClassEntry> classEntries;
    private final Map<Object, ConstantPool.LiteralEntry> literalEntries;
    private final Map<String, ConstantPool.SignatureEntry> signatureEntries;
    private final Map<String, ConstantPool.DescriptorEntry> descriptorEntries;
    private final Map<String, ConstantPool.MemberEntry> memberEntries;
    private final Map<String, ConstantPool.MethodHandleEntry> methodHandleEntries;
    private final Map<String, ConstantPool.MethodTypeEntry> methodTypeEntries;
    private final Map<String, ConstantPool.InvokeDynamicEntry> invokeDynamicEntries;
    private final Map<String, ConstantPool.BootstrapMethodEntry> bootstrapMethodEntries;
    
    TLGlobals() {
        this.utf8Entries = new HashMap<String, ConstantPool.Utf8Entry>();
        this.classEntries = new HashMap<String, ConstantPool.ClassEntry>();
        this.literalEntries = new HashMap<Object, ConstantPool.LiteralEntry>();
        this.signatureEntries = new HashMap<String, ConstantPool.SignatureEntry>();
        this.descriptorEntries = new HashMap<String, ConstantPool.DescriptorEntry>();
        this.memberEntries = new HashMap<String, ConstantPool.MemberEntry>();
        this.methodHandleEntries = new HashMap<String, ConstantPool.MethodHandleEntry>();
        this.methodTypeEntries = new HashMap<String, ConstantPool.MethodTypeEntry>();
        this.invokeDynamicEntries = new HashMap<String, ConstantPool.InvokeDynamicEntry>();
        this.bootstrapMethodEntries = new HashMap<String, ConstantPool.BootstrapMethodEntry>();
        this.props = new PropMap();
    }
    
    SortedMap<String, String> getPropMap() {
        return this.props;
    }
    
    Map<String, ConstantPool.Utf8Entry> getUtf8Entries() {
        return this.utf8Entries;
    }
    
    Map<String, ConstantPool.ClassEntry> getClassEntries() {
        return this.classEntries;
    }
    
    Map<Object, ConstantPool.LiteralEntry> getLiteralEntries() {
        return this.literalEntries;
    }
    
    Map<String, ConstantPool.DescriptorEntry> getDescriptorEntries() {
        return this.descriptorEntries;
    }
    
    Map<String, ConstantPool.SignatureEntry> getSignatureEntries() {
        return this.signatureEntries;
    }
    
    Map<String, ConstantPool.MemberEntry> getMemberEntries() {
        return this.memberEntries;
    }
    
    Map<String, ConstantPool.MethodHandleEntry> getMethodHandleEntries() {
        return this.methodHandleEntries;
    }
    
    Map<String, ConstantPool.MethodTypeEntry> getMethodTypeEntries() {
        return this.methodTypeEntries;
    }
    
    Map<String, ConstantPool.InvokeDynamicEntry> getInvokeDynamicEntries() {
        return this.invokeDynamicEntries;
    }
    
    Map<String, ConstantPool.BootstrapMethodEntry> getBootstrapMethodEntries() {
        return this.bootstrapMethodEntries;
    }
}
