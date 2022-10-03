package org.apache.xerces.dom;

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
    
    public DOMLocatorImpl(final int fLineNumber, final int fColumnNumber, final String fUri) {
        this.fColumnNumber = -1;
        this.fLineNumber = -1;
        this.fRelatedNode = null;
        this.fUri = null;
        this.fByteOffset = -1;
        this.fUtf16Offset = -1;
        this.fLineNumber = fLineNumber;
        this.fColumnNumber = fColumnNumber;
        this.fUri = fUri;
    }
    
    public DOMLocatorImpl(final int fLineNumber, final int fColumnNumber, final int fUtf16Offset, final String fUri) {
        this.fColumnNumber = -1;
        this.fLineNumber = -1;
        this.fRelatedNode = null;
        this.fUri = null;
        this.fByteOffset = -1;
        this.fUtf16Offset = -1;
        this.fLineNumber = fLineNumber;
        this.fColumnNumber = fColumnNumber;
        this.fUri = fUri;
        this.fUtf16Offset = fUtf16Offset;
    }
    
    public DOMLocatorImpl(final int fLineNumber, final int fColumnNumber, final int fByteOffset, final Node fRelatedNode, final String fUri) {
        this.fColumnNumber = -1;
        this.fLineNumber = -1;
        this.fRelatedNode = null;
        this.fUri = null;
        this.fByteOffset = -1;
        this.fUtf16Offset = -1;
        this.fLineNumber = fLineNumber;
        this.fColumnNumber = fColumnNumber;
        this.fByteOffset = fByteOffset;
        this.fRelatedNode = fRelatedNode;
        this.fUri = fUri;
    }
    
    public DOMLocatorImpl(final int fLineNumber, final int fColumnNumber, final int fByteOffset, final Node fRelatedNode, final String fUri, final int fUtf16Offset) {
        this.fColumnNumber = -1;
        this.fLineNumber = -1;
        this.fRelatedNode = null;
        this.fUri = null;
        this.fByteOffset = -1;
        this.fUtf16Offset = -1;
        this.fLineNumber = fLineNumber;
        this.fColumnNumber = fColumnNumber;
        this.fByteOffset = fByteOffset;
        this.fRelatedNode = fRelatedNode;
        this.fUri = fUri;
        this.fUtf16Offset = fUtf16Offset;
    }
    
    public int getLineNumber() {
        return this.fLineNumber;
    }
    
    public int getColumnNumber() {
        return this.fColumnNumber;
    }
    
    public String getUri() {
        return this.fUri;
    }
    
    public Node getRelatedNode() {
        return this.fRelatedNode;
    }
    
    public int getByteOffset() {
        return this.fByteOffset;
    }
    
    public int getUtf16Offset() {
        return this.fUtf16Offset;
    }
}
