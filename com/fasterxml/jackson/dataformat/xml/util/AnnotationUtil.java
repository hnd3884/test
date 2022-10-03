package com.fasterxml.jackson.dataformat.xml.util;

import java.util.Iterator;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.dataformat.xml.XmlAnnotationIntrospector;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.AnnotationIntrospector;

public class AnnotationUtil
{
    public static String findNamespaceAnnotation(final AnnotationIntrospector ai, final AnnotatedMember prop) {
        for (final AnnotationIntrospector intr : ai.allIntrospectors()) {
            if (intr instanceof XmlAnnotationIntrospector) {
                final String ns = ((XmlAnnotationIntrospector)intr).findNamespace((Annotated)prop);
                if (ns != null) {
                    return ns;
                }
                continue;
            }
            else {
                if (!(intr instanceof JaxbAnnotationIntrospector)) {
                    continue;
                }
                final String ns = ((JaxbAnnotationIntrospector)intr).findNamespace((Annotated)prop);
                if (ns != null) {
                    return ns;
                }
                continue;
            }
        }
        return null;
    }
    
    public static Boolean findIsAttributeAnnotation(final AnnotationIntrospector ai, final AnnotatedMember prop) {
        for (final AnnotationIntrospector intr : ai.allIntrospectors()) {
            if (intr instanceof XmlAnnotationIntrospector) {
                final Boolean b = ((XmlAnnotationIntrospector)intr).isOutputAsAttribute((Annotated)prop);
                if (b != null) {
                    return b;
                }
                continue;
            }
            else {
                if (!(intr instanceof JaxbAnnotationIntrospector)) {
                    continue;
                }
                final Boolean b = ((JaxbAnnotationIntrospector)intr).isOutputAsAttribute((Annotated)prop);
                if (b != null) {
                    return b;
                }
                continue;
            }
        }
        return null;
    }
    
    public static Boolean findIsTextAnnotation(final AnnotationIntrospector ai, final AnnotatedMember prop) {
        for (final AnnotationIntrospector intr : ai.allIntrospectors()) {
            if (intr instanceof XmlAnnotationIntrospector) {
                final Boolean b = ((XmlAnnotationIntrospector)intr).isOutputAsText((Annotated)prop);
                if (b != null) {
                    return b;
                }
                continue;
            }
            else {
                if (!(intr instanceof JaxbAnnotationIntrospector)) {
                    continue;
                }
                final Boolean b = ((JaxbAnnotationIntrospector)intr).isOutputAsText((Annotated)prop);
                if (b != null) {
                    return b;
                }
                continue;
            }
        }
        return null;
    }
    
    public static Boolean findIsCDataAnnotation(final AnnotationIntrospector ai, final AnnotatedMember prop) {
        for (final AnnotationIntrospector intr : ai.allIntrospectors()) {
            if (intr instanceof XmlAnnotationIntrospector) {
                final Boolean b = ((XmlAnnotationIntrospector)intr).isOutputAsCData((Annotated)prop);
                if (b != null) {
                    return b;
                }
                continue;
            }
        }
        return null;
    }
}
