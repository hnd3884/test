package org.apache.xmlbeans.impl.jam.internal.classrefs;

import java.io.StringWriter;
import org.apache.xmlbeans.impl.jam.JClass;

public class UnqualifiedJClassRef implements JClassRef
{
    private static final boolean VERBOSE = false;
    private static final String PREFIX = "[UnqualifiedJClassRef]";
    private String mUnqualifiedClassname;
    private String mQualifiedClassname;
    private JClassRefContext mContext;
    
    public static JClassRef create(final String qualifiedClassname, final JClassRefContext ctx) {
        throw new IllegalStateException("Unqualified names currently disabled.");
    }
    
    private UnqualifiedJClassRef(final String ucname, final JClassRefContext ctx) {
        this.mQualifiedClassname = null;
        if (ctx == null) {
            throw new IllegalArgumentException("null ctx");
        }
        if (ucname == null) {
            throw new IllegalArgumentException("null ucname");
        }
        this.mContext = ctx;
        this.mUnqualifiedClassname = ucname;
    }
    
    @Override
    public JClass getRefClass() {
        return this.mContext.getClassLoader().loadClass(this.getQualifiedName());
    }
    
    @Override
    public String getQualifiedName() {
        if (this.mQualifiedClassname != null) {
            return this.mQualifiedClassname;
        }
        int arrayDimensions = 0;
        int bracket = this.mUnqualifiedClassname.indexOf(91);
        String candidateName;
        if (bracket != -1) {
            candidateName = this.mUnqualifiedClassname.substring(0, bracket);
            do {
                ++arrayDimensions;
                bracket = this.mUnqualifiedClassname.indexOf(91, bracket + 1);
            } while (bracket != -1);
        }
        else {
            candidateName = this.mUnqualifiedClassname;
        }
        final String name = this.qualifyName(candidateName);
        if (name == null) {
            throw new IllegalStateException("unable to handle unqualified java type reference '" + candidateName + " [" + this.mUnqualifiedClassname + "]'. " + "This is still partially NYI.");
        }
        if (arrayDimensions > 0) {
            final StringWriter out = new StringWriter();
            for (int i = 0; i < arrayDimensions; ++i) {
                out.write(91);
            }
            out.write(76);
            out.write(name);
            out.write(59);
            this.mQualifiedClassname = out.toString();
        }
        else {
            this.mQualifiedClassname = name;
        }
        return this.mQualifiedClassname;
    }
    
    private String qualifyName(final String ucname) {
        String out = null;
        if ((out = this.checkExplicitImport(ucname)) != null) {
            return out;
        }
        if ((out = this.checkJavaLang(ucname)) != null) {
            return out;
        }
        if ((out = this.checkSamePackage(ucname)) != null) {
            return out;
        }
        if ((out = this.checkAlreadyQualified(ucname)) != null) {
            return out;
        }
        return null;
    }
    
    private String checkSamePackage(final String ucname) {
        final String name = this.mContext.getPackageName() + "." + ucname;
        final JClass clazz = this.mContext.getClassLoader().loadClass(name);
        return clazz.isUnresolvedType() ? null : clazz.getQualifiedName();
    }
    
    private String checkJavaLang(final String ucname) {
        final String name = "java.lang." + ucname;
        final JClass clazz = this.mContext.getClassLoader().loadClass(name);
        return clazz.isUnresolvedType() ? null : clazz.getQualifiedName();
    }
    
    private String checkAlreadyQualified(final String ucname) {
        final JClass clazz = this.mContext.getClassLoader().loadClass(ucname);
        return clazz.isUnresolvedType() ? null : clazz.getQualifiedName();
    }
    
    private String checkExplicitImport(final String ucname) {
        final String[] imports = this.mContext.getImportSpecs();
        for (int i = 0; i < imports.length; ++i) {
            final String last = lastSegment(imports[i]);
            if (last.equals(ucname)) {
                return imports[i];
            }
        }
        return null;
    }
    
    private static String lastSegment(final String s) {
        final int lastDot = s.lastIndexOf(".");
        if (lastDot == -1) {
            return s;
        }
        return s.substring(lastDot + 1);
    }
    
    private static String firstSegment(final String s) {
        final int lastDot = s.indexOf(".");
        if (lastDot == -1) {
            return s;
        }
        return s.substring(0, lastDot);
    }
}
