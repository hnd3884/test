package org.apache.xmlbeans.impl.jam.internal;

import java.util.HashMap;
import org.apache.xmlbeans.impl.jam.mutable.MClass;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.xmlbeans.impl.jam.provider.JamClassBuilder;

public class CachedClassBuilder extends JamClassBuilder
{
    private Map mQcname2jclass;
    private List mClassNames;
    
    public CachedClassBuilder() {
        this.mQcname2jclass = null;
        this.mClassNames = new ArrayList();
    }
    
    @Override
    public MClass build(final String packageName, String className) {
        if (this.mQcname2jclass == null) {
            return null;
        }
        if (packageName.trim().length() > 0) {
            className = packageName + '.' + className;
        }
        return this.mQcname2jclass.get(className);
    }
    
    public MClass createClassToBuild(final String packageName, final String className, final String[] importSpecs) {
        String qualifiedName;
        if (packageName.trim().length() > 0) {
            qualifiedName = packageName + '.' + className;
        }
        else {
            qualifiedName = className;
        }
        if (this.mQcname2jclass != null) {
            final MClass out = this.mQcname2jclass.get(qualifiedName);
            if (out != null) {
                return out;
            }
        }
        else {
            this.mQcname2jclass = new HashMap();
        }
        final MClass out = super.createClassToBuild(packageName, className, importSpecs);
        this.mQcname2jclass.put(qualifiedName, out);
        this.mClassNames.add(qualifiedName);
        return out;
    }
    
    public String[] getClassNames() {
        final String[] out = new String[this.mClassNames.size()];
        this.mClassNames.toArray(out);
        return out;
    }
}
