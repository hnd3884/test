package com.sun.corba.se.impl.orbutil;

import java.net.MalformedURLException;
import com.sun.corba.se.impl.io.TypeMismatchException;
import java.io.Serializable;
import com.sun.corba.se.impl.util.RepositoryId;

public final class RepIdDelegator implements RepositoryIdStrings, RepositoryIdUtility, RepositoryIdInterface
{
    private final RepositoryId delegate;
    
    @Override
    public String createForAnyType(final Class clazz) {
        return RepositoryId.createForAnyType(clazz);
    }
    
    @Override
    public String createForJavaType(final Serializable s) throws TypeMismatchException {
        return RepositoryId.createForJavaType(s);
    }
    
    @Override
    public String createForJavaType(final Class clazz) throws TypeMismatchException {
        return RepositoryId.createForJavaType(clazz);
    }
    
    @Override
    public String createSequenceRepID(final Object o) {
        return RepositoryId.createSequenceRepID(o);
    }
    
    @Override
    public String createSequenceRepID(final Class clazz) {
        return RepositoryId.createSequenceRepID(clazz);
    }
    
    @Override
    public RepositoryIdInterface getFromString(final String s) {
        return new RepIdDelegator(RepositoryId.cache.getId(s));
    }
    
    @Override
    public boolean isChunkedEncoding(final int n) {
        return RepositoryId.isChunkedEncoding(n);
    }
    
    @Override
    public boolean isCodeBasePresent(final int n) {
        return RepositoryId.isCodeBasePresent(n);
    }
    
    @Override
    public String getClassDescValueRepId() {
        return RepositoryId.kClassDescValueRepID;
    }
    
    @Override
    public String getWStringValueRepId() {
        return "IDL:omg.org/CORBA/WStringValue:1.0";
    }
    
    @Override
    public int getTypeInfo(final int n) {
        return RepositoryId.getTypeInfo(n);
    }
    
    @Override
    public int getStandardRMIChunkedNoRepStrId() {
        return RepositoryId.kPreComputed_StandardRMIChunked_NoRep;
    }
    
    @Override
    public int getCodeBaseRMIChunkedNoRepStrId() {
        return RepositoryId.kPreComputed_CodeBaseRMIChunked_NoRep;
    }
    
    @Override
    public int getStandardRMIChunkedId() {
        return RepositoryId.kPreComputed_StandardRMIChunked;
    }
    
    @Override
    public int getCodeBaseRMIChunkedId() {
        return RepositoryId.kPreComputed_CodeBaseRMIChunked;
    }
    
    @Override
    public int getStandardRMIUnchunkedId() {
        return RepositoryId.kPreComputed_StandardRMIUnchunked;
    }
    
    @Override
    public int getCodeBaseRMIUnchunkedId() {
        return RepositoryId.kPreComputed_CodeBaseRMIUnchunked;
    }
    
    @Override
    public int getStandardRMIUnchunkedNoRepStrId() {
        return RepositoryId.kPreComputed_StandardRMIUnchunked_NoRep;
    }
    
    @Override
    public int getCodeBaseRMIUnchunkedNoRepStrId() {
        return RepositoryId.kPreComputed_CodeBaseRMIUnchunked_NoRep;
    }
    
    @Override
    public Class getClassFromType() throws ClassNotFoundException {
        return this.delegate.getClassFromType();
    }
    
    @Override
    public Class getClassFromType(final String s) throws ClassNotFoundException, MalformedURLException {
        return this.delegate.getClassFromType(s);
    }
    
    @Override
    public Class getClassFromType(final Class clazz, final String s) throws ClassNotFoundException, MalformedURLException {
        return this.delegate.getClassFromType(clazz, s);
    }
    
    @Override
    public String getClassName() {
        return this.delegate.getClassName();
    }
    
    public RepIdDelegator() {
        this(null);
    }
    
    private RepIdDelegator(final RepositoryId delegate) {
        this.delegate = delegate;
    }
    
    @Override
    public String toString() {
        if (this.delegate != null) {
            return this.delegate.toString();
        }
        return this.getClass().getName();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this.delegate != null) {
            return this.delegate.equals(o);
        }
        return super.equals(o);
    }
    
    @Override
    public int hashCode() {
        if (this.delegate != null) {
            return this.delegate.hashCode();
        }
        return super.hashCode();
    }
}
