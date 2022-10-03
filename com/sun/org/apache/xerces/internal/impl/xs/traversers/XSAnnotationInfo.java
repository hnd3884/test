package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.impl.xs.opti.ElementImpl;
import org.w3c.dom.Element;

final class XSAnnotationInfo
{
    String fAnnotation;
    int fLine;
    int fColumn;
    int fCharOffset;
    XSAnnotationInfo next;
    
    XSAnnotationInfo(final String annotation, final int line, final int column, final int charOffset) {
        this.fAnnotation = annotation;
        this.fLine = line;
        this.fColumn = column;
        this.fCharOffset = charOffset;
    }
    
    XSAnnotationInfo(final String annotation, final Element annotationDecl) {
        this.fAnnotation = annotation;
        if (annotationDecl instanceof ElementImpl) {
            final ElementImpl annotationDeclImpl = (ElementImpl)annotationDecl;
            this.fLine = annotationDeclImpl.getLineNumber();
            this.fColumn = annotationDeclImpl.getColumnNumber();
            this.fCharOffset = annotationDeclImpl.getCharacterOffset();
        }
        else {
            this.fLine = -1;
            this.fColumn = -1;
            this.fCharOffset = -1;
        }
    }
}
