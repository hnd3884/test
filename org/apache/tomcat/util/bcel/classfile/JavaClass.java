package org.apache.tomcat.util.bcel.classfile;

import java.util.HashMap;
import java.util.List;

public class JavaClass
{
    private final int accessFlags;
    private final String className;
    private final String superclassName;
    private final String[] interfaceNames;
    private final Annotations runtimeVisibleAnnotations;
    private final List<Annotations> runtimeVisibleFieldOrMethodAnnotations;
    
    JavaClass(final String className, final String superclassName, final int accessFlags, final ConstantPool constant_pool, final String[] interfaceNames, final Annotations runtimeVisibleAnnotations, final List<Annotations> runtimeVisibleFieldOrMethodAnnotations) {
        this.accessFlags = accessFlags;
        this.runtimeVisibleAnnotations = runtimeVisibleAnnotations;
        this.runtimeVisibleFieldOrMethodAnnotations = runtimeVisibleFieldOrMethodAnnotations;
        this.className = className;
        this.superclassName = superclassName;
        this.interfaceNames = interfaceNames;
    }
    
    public final int getAccessFlags() {
        return this.accessFlags;
    }
    
    public AnnotationEntry[] getAnnotationEntries() {
        if (this.runtimeVisibleAnnotations != null) {
            return this.runtimeVisibleAnnotations.getAnnotationEntries();
        }
        return null;
    }
    
    public AnnotationEntry[] getAllAnnotationEntries() {
        final HashMap<String, AnnotationEntry> annotationEntries = new HashMap<String, AnnotationEntry>();
        if (this.runtimeVisibleAnnotations != null) {
            for (final AnnotationEntry annotationEntry : this.runtimeVisibleAnnotations.getAnnotationEntries()) {
                annotationEntries.put(annotationEntry.getAnnotationType(), annotationEntry);
            }
        }
        if (this.runtimeVisibleFieldOrMethodAnnotations != null) {
            for (final Annotations annotations : this.runtimeVisibleFieldOrMethodAnnotations.toArray(new Annotations[0])) {
                for (final AnnotationEntry annotationEntry2 : annotations.getAnnotationEntries()) {
                    if (!annotationEntries.containsKey(annotationEntry2.getAnnotationType())) {
                        annotationEntries.put(annotationEntry2.getAnnotationType(), annotationEntry2);
                    }
                }
            }
        }
        if (annotationEntries.isEmpty()) {
            return null;
        }
        return annotationEntries.values().toArray(new AnnotationEntry[0]);
    }
    
    public String getClassName() {
        return this.className;
    }
    
    public String[] getInterfaceNames() {
        return this.interfaceNames;
    }
    
    public String getSuperclassName() {
        return this.superclassName;
    }
}
