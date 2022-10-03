package com.sun.xml.internal.stream;

import java.io.IOException;
import com.sun.xml.internal.stream.util.BufferAllocator;
import com.sun.xml.internal.stream.util.ThreadLocalBufferAllocator;
import java.io.Reader;
import java.io.InputStream;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;

public abstract class Entity
{
    public String name;
    public boolean inExternalSubset;
    
    public Entity() {
        this.clear();
    }
    
    public Entity(final String name, final boolean inExternalSubset) {
        this.name = name;
        this.inExternalSubset = inExternalSubset;
    }
    
    public boolean isEntityDeclInExternalSubset() {
        return this.inExternalSubset;
    }
    
    public abstract boolean isExternal();
    
    public abstract boolean isUnparsed();
    
    public void clear() {
        this.name = null;
        this.inExternalSubset = false;
    }
    
    public void setValues(final Entity entity) {
        this.name = entity.name;
        this.inExternalSubset = entity.inExternalSubset;
    }
    
    public static class InternalEntity extends Entity
    {
        public String text;
        
        public InternalEntity() {
            this.clear();
        }
        
        public InternalEntity(final String name, final String text, final boolean inExternalSubset) {
            super(name, inExternalSubset);
            this.text = text;
        }
        
        @Override
        public final boolean isExternal() {
            return false;
        }
        
        @Override
        public final boolean isUnparsed() {
            return false;
        }
        
        @Override
        public void clear() {
            super.clear();
            this.text = null;
        }
        
        @Override
        public void setValues(final Entity entity) {
            super.setValues(entity);
            this.text = null;
        }
        
        public void setValues(final InternalEntity entity) {
            super.setValues(entity);
            this.text = entity.text;
        }
    }
    
    public static class ExternalEntity extends Entity
    {
        public XMLResourceIdentifier entityLocation;
        public String notation;
        
        public ExternalEntity() {
            this.clear();
        }
        
        public ExternalEntity(final String name, final XMLResourceIdentifier entityLocation, final String notation, final boolean inExternalSubset) {
            super(name, inExternalSubset);
            this.entityLocation = entityLocation;
            this.notation = notation;
        }
        
        @Override
        public final boolean isExternal() {
            return true;
        }
        
        @Override
        public final boolean isUnparsed() {
            return this.notation != null;
        }
        
        @Override
        public void clear() {
            super.clear();
            this.entityLocation = null;
            this.notation = null;
        }
        
        @Override
        public void setValues(final Entity entity) {
            super.setValues(entity);
            this.entityLocation = null;
            this.notation = null;
        }
        
        public void setValues(final ExternalEntity entity) {
            super.setValues(entity);
            this.entityLocation = entity.entityLocation;
            this.notation = entity.notation;
        }
    }
    
    public static class ScannedEntity extends Entity
    {
        public static final int DEFAULT_BUFFER_SIZE = 8192;
        public int fBufferSize;
        public static final int DEFAULT_XMLDECL_BUFFER_SIZE = 28;
        public static final int DEFAULT_INTERNAL_BUFFER_SIZE = 1024;
        public InputStream stream;
        public Reader reader;
        public XMLResourceIdentifier entityLocation;
        public String encoding;
        public boolean literal;
        public boolean isExternal;
        public String version;
        public char[] ch;
        public int position;
        public int count;
        public int lineNumber;
        public int columnNumber;
        boolean declaredEncoding;
        boolean externallySpecifiedEncoding;
        public String xmlVersion;
        public int fTotalCountTillLastLoad;
        public int fLastCount;
        public int baseCharOffset;
        public int startPosition;
        public boolean mayReadChunks;
        public boolean xmlDeclChunkRead;
        public boolean isGE;
        
        public String getEncodingName() {
            return this.encoding;
        }
        
        public String getEntityVersion() {
            return this.version;
        }
        
        public void setEntityVersion(final String version) {
            this.version = version;
        }
        
        public Reader getEntityReader() {
            return this.reader;
        }
        
        public InputStream getEntityInputStream() {
            return this.stream;
        }
        
        public ScannedEntity(final boolean isGE, final String name, final XMLResourceIdentifier entityLocation, final InputStream stream, final Reader reader, final String encoding, final boolean literal, final boolean mayReadChunks, final boolean isExternal) {
            this.fBufferSize = 8192;
            this.ch = null;
            this.lineNumber = 1;
            this.columnNumber = 1;
            this.declaredEncoding = false;
            this.externallySpecifiedEncoding = false;
            this.xmlVersion = "1.0";
            this.xmlDeclChunkRead = false;
            this.isGE = false;
            this.isGE = isGE;
            this.name = name;
            this.entityLocation = entityLocation;
            this.stream = stream;
            this.reader = reader;
            this.encoding = encoding;
            this.literal = literal;
            this.mayReadChunks = mayReadChunks;
            this.isExternal = isExternal;
            final int size = isExternal ? this.fBufferSize : 1024;
            final BufferAllocator ba = ThreadLocalBufferAllocator.getBufferAllocator();
            this.ch = ba.getCharBuffer(size);
            if (this.ch == null) {
                this.ch = new char[size];
            }
        }
        
        public void close() throws IOException {
            final BufferAllocator ba = ThreadLocalBufferAllocator.getBufferAllocator();
            ba.returnCharBuffer(this.ch);
            this.ch = null;
            this.reader.close();
        }
        
        public boolean isEncodingExternallySpecified() {
            return this.externallySpecifiedEncoding;
        }
        
        public void setEncodingExternallySpecified(final boolean value) {
            this.externallySpecifiedEncoding = value;
        }
        
        public boolean isDeclaredEncoding() {
            return this.declaredEncoding;
        }
        
        public void setDeclaredEncoding(final boolean value) {
            this.declaredEncoding = value;
        }
        
        @Override
        public final boolean isExternal() {
            return this.isExternal;
        }
        
        @Override
        public final boolean isUnparsed() {
            return false;
        }
        
        @Override
        public String toString() {
            final StringBuffer str = new StringBuffer();
            str.append("name=\"" + this.name + '\"');
            str.append(",ch=" + new String(this.ch));
            str.append(",position=" + this.position);
            str.append(",count=" + this.count);
            return str.toString();
        }
    }
}
