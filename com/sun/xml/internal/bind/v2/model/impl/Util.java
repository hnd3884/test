package com.sun.xml.internal.bind.v2.model.impl;

import javax.activation.MimeTypeParseException;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import javax.xml.bind.annotation.XmlMimeType;
import javax.activation.MimeType;
import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlSchemaTypes;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.namespace.QName;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.annotation.AnnotationSource;
import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;

final class Util
{
    static <T, C, F, M> QName calcSchemaType(final AnnotationReader<T, C, F, M> reader, final AnnotationSource primarySource, final C enclosingClass, final T individualType, final Locatable src) {
        XmlSchemaType xst = primarySource.readAnnotation(XmlSchemaType.class);
        if (xst != null) {
            return new QName(xst.namespace(), xst.name());
        }
        final XmlSchemaTypes xsts = reader.getPackageAnnotation(XmlSchemaTypes.class, enclosingClass, src);
        XmlSchemaType[] values = null;
        if (xsts != null) {
            values = xsts.value();
        }
        else {
            xst = reader.getPackageAnnotation(XmlSchemaType.class, enclosingClass, src);
            if (xst != null) {
                values = new XmlSchemaType[] { xst };
            }
        }
        if (values != null) {
            for (final XmlSchemaType item : values) {
                if (reader.getClassValue(item, "type").equals(individualType)) {
                    return new QName(item.namespace(), item.name());
                }
            }
        }
        return null;
    }
    
    static MimeType calcExpectedMediaType(final AnnotationSource primarySource, final ModelBuilder builder) {
        final XmlMimeType xmt = primarySource.readAnnotation(XmlMimeType.class);
        if (xmt == null) {
            return null;
        }
        try {
            return new MimeType(xmt.value());
        }
        catch (final MimeTypeParseException e) {
            builder.reportError(new IllegalAnnotationException(Messages.ILLEGAL_MIME_TYPE.format(xmt.value(), e.getMessage()), xmt));
            return null;
        }
    }
}
