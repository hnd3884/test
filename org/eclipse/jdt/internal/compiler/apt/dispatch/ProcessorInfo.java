package org.eclipse.jdt.internal.compiler.apt.dispatch;

import java.util.regex.Matcher;
import javax.lang.model.element.TypeElement;
import java.util.Iterator;
import java.util.regex.Pattern;
import javax.lang.model.SourceVersion;
import java.util.Set;
import javax.annotation.processing.Processor;

public class ProcessorInfo
{
    final Processor _processor;
    final Set<String> _supportedOptions;
    final SourceVersion _supportedSourceVersion;
    private final Pattern _supportedAnnotationTypesPattern;
    private final boolean _supportsStar;
    private boolean _hasBeenCalled;
    
    public ProcessorInfo(final Processor p) {
        this._processor = p;
        this._hasBeenCalled = false;
        this._supportedSourceVersion = p.getSupportedSourceVersion();
        this._supportedOptions = p.getSupportedOptions();
        final Set<String> supportedAnnotationTypes = p.getSupportedAnnotationTypes();
        boolean supportsStar = false;
        if (supportedAnnotationTypes != null && !supportedAnnotationTypes.isEmpty()) {
            final StringBuilder regex = new StringBuilder();
            final Iterator<String> iName = supportedAnnotationTypes.iterator();
            while (true) {
                final String name = iName.next();
                supportsStar |= "*".equals(name);
                final String escapedName1 = name.replace(".", "\\.");
                final String escapedName2 = escapedName1.replace("*", ".*");
                regex.append(escapedName2);
                if (!iName.hasNext()) {
                    break;
                }
                regex.append('|');
            }
            this._supportedAnnotationTypesPattern = Pattern.compile(regex.toString());
        }
        else {
            this._supportedAnnotationTypesPattern = null;
        }
        this._supportsStar = supportsStar;
    }
    
    public boolean computeSupportedAnnotations(final Set<TypeElement> annotations, final Set<TypeElement> result) {
        if (annotations != null && !annotations.isEmpty() && this._supportedAnnotationTypesPattern != null) {
            for (final TypeElement annotation : annotations) {
                final Matcher matcher = this._supportedAnnotationTypesPattern.matcher(annotation.getQualifiedName().toString());
                if (matcher.matches()) {
                    result.add(annotation);
                }
            }
        }
        final boolean call = this._hasBeenCalled || this._supportsStar || !result.isEmpty();
        this._hasBeenCalled |= call;
        return call;
    }
    
    public boolean supportsStar() {
        return this._supportsStar;
    }
    
    public void reset() {
        this._hasBeenCalled = false;
    }
    
    @Override
    public int hashCode() {
        return this._processor.getClass().hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final ProcessorInfo other = (ProcessorInfo)obj;
        return this._processor.getClass().equals(other._processor.getClass());
    }
    
    @Override
    public String toString() {
        return this._processor.getClass().getName();
    }
    
    public String getSupportedAnnotationTypesAsString() {
        final StringBuilder sb = new StringBuilder();
        sb.append('[');
        final Iterator<String> iAnnots = this._processor.getSupportedAnnotationTypes().iterator();
        boolean hasNext = iAnnots.hasNext();
        while (hasNext) {
            sb.append(iAnnots.next());
            hasNext = iAnnots.hasNext();
            if (hasNext) {
                sb.append(',');
            }
        }
        sb.append(']');
        return sb.toString();
    }
}
