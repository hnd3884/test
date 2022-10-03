package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.Node;
import org.w3c.dom.DOMLocator;

public class DOMLocatorImpl implements DOMLocator
{
    public int fColumnNumber;
    public int fLineNumber;
    public Node fRelatedNode;
    public String fUri;
    public int fByteOffset;
    public int fUtf16Offset;
    
    public DOMLocatorImpl() {
        this.fColumnNumber = -1;
        this.fLineNumber = -1;
        this.fRelatedNode = null;
        this.fUri = null;
        this.fByteOffset = -1;
        this.fUtf16Offset = -1;
    }
    
    public DOMLocatorImpl(final int lineNumber, final int columnNumber, final String uri) {
        this.fColumnNumber = -1;
        this.fLineNumber = -1;
        this.fRelatedNode = null;
        this.fUri = null;
        this.fByteOffset = -1;
        this.fUtf16Offset = -1;
        this.fLineNumber = lineNumber;
        this.fColumnNumber = columnNumber;
        this.fUri = uri;
    }
    
    public DOMLocatorImpl(final int lineNumber, final int columnNumber, final int utf16Offset, final String uri) {
        this.fColumnNumber = -1;
        this.fLineNumber = -1;
        this.fRelatedNode = null;
        this.fUri = null;
        this.fByteOffset = -1;
        this.fUtf16Offset = -1;
        this.fLineNumber = lineNumber;
        this.fColumnNumber = columnNumber;
        this.fUri = uri;
        this.fUtf16Offset = utf16Offset;
    }
    
    public DOMLocatorImpl(final int lineNumber, final int columnNumber, final int byteoffset, final Node relatedData, final String uri) {
        this.fColumnNumber = -1;
        this.fLineNumber = -1;
        this.fRelatedNode = null;
        this.fUri = null;
        this.fByteOffset = -1;
        this.fUtf16Offset = -1;
        this.fLineNumber = lineNumber;
        this.fColumnNumber = columnNumber;
        this.fByteOffset = byteoffset;
        this.fRelatedNode = relatedData;
        this.fUri = uri;
    }
    
    public DOMLocatorImpl(final int lineNumber, final int columnNumber, final int byteoffset, final Node relatedData, final String uri, final int utf16Offset) {
        this.fColumnNumber = -1;
        this.fLineNumber = -1;
        this.fRelatedNode = null;
        this.fUri = null;
        this.fByteOffset = -1;
        this.fUtf16Offset = -1;
        this.fLineNumber = lineNumber;
        this.fColumnNumber = columnNumber;
        this.fByteOffset = byteoffset;
        this.fRelatedNode = relatedData;
        this.fUri = uri;
        this.fUtf16Offset = utf16Offset;
    }
    
    @Override
    public int getLineNumber() {
        return this.fLineNumber;
    }
    
    @Override
    public int getColumnNumber() {
        return this.fColumnNumber;
    }
    
    @Override
    public String getUri() {
        return this.fUri;
    }
    
    @Override
    public Node getRelatedNode() {
        return this.fRelatedNode;
    }
    
    @Override
    public int getByteOffset() {
        return this.fByteOffset;
    }
    
    @Override
    public int getUtf16Offset() {
        return this.fUtf16Offset;
    }
}
