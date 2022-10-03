package org.apache.xerces.impl.xs.traversers;

import org.apache.xerces.impl.xs.opti.ElementImpl;
import org.w3c.dom.Element;

final class XSAnnotationInfo
{
    String fAnnotation;
    int fLine;
    int fColumn;
    int fCharOffset;
    XSAnnotationInfo next;
    
    XSAnnotationInfo(final String fAnnotation, final int fLine, final int fColumn, final int fCharOffset) {
        this.fAnnotation = fAnnotation;
        this.fLine = fLine;
        this.fColumn = fColumn;
        this.fCharOffset = fCharOffset;
    }
    
    XSAnnotationInfo(final String fAnnotation, final Element element) {
        this.fAnnotation = fAnnotation;
        if (element instanceof ElementImpl) {
            final ElementImpl elementImpl = (ElementImpl)element;
            this.fLine = elementImpl.getLineNumber();
            this.fColumn = elementImpl.getColumnNumber();
            this.fCharOffset = elementImpl.getCharacterOffset();
        }
        else {
            this.fLine = -1;
            this.fColumn = -1;
            this.fCharOffset = -1;
        }
    }
}
