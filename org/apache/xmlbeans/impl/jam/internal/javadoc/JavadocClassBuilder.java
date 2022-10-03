package org.apache.xmlbeans.impl.jam.internal.javadoc;

import org.apache.xmlbeans.impl.jam.mutable.MSourcePosition;
import com.sun.javadoc.SourcePosition;
import org.apache.xmlbeans.impl.jam.internal.elements.PrimitiveClassImpl;
import java.io.StringWriter;
import com.sun.javadoc.Tag;
import java.util.StringTokenizer;
import org.apache.xmlbeans.impl.jam.mutable.MParameter;
import com.sun.javadoc.Parameter;
import org.apache.xmlbeans.impl.jam.mutable.MField;
import java.io.File;
import java.io.FileNotFoundException;
import org.apache.xmlbeans.impl.jam.internal.JamServiceContextImpl;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.IOException;
import org.apache.xmlbeans.impl.jam.mutable.MMethod;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.Doc;
import org.apache.xmlbeans.impl.jam.mutable.MElement;
import com.sun.javadoc.ProgramElementDoc;
import org.apache.xmlbeans.impl.jam.mutable.MAnnotatedElement;
import com.sun.javadoc.ExecutableMemberDoc;
import org.apache.xmlbeans.impl.jam.mutable.MInvokable;
import com.sun.javadoc.PackageDoc;
import java.util.List;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Type;
import java.util.ArrayList;
import org.apache.xmlbeans.impl.jam.mutable.MClass;
import org.apache.xmlbeans.impl.jam.provider.JamServiceContext;
import org.apache.xmlbeans.impl.jam.internal.elements.ElementContext;
import org.apache.xmlbeans.impl.jam.annotation.JavadocTagParser;
import com.sun.javadoc.RootDoc;
import org.apache.xmlbeans.impl.jam.provider.JamClassPopulator;
import org.apache.xmlbeans.impl.jam.provider.JamClassBuilder;

public class JavadocClassBuilder extends JamClassBuilder implements JamClassPopulator
{
    public static final String ARGS_PROPERTY = "javadoc.args";
    public static final String PARSETAGS_PROPERTY = "javadoc.parsetags";
    private RootDoc mRootDoc;
    private JavadocTigerDelegate mTigerDelegate;
    private JavadocTagParser mTagParser;
    private boolean mParseTags;
    
    public JavadocClassBuilder() {
        this.mRootDoc = null;
        this.mTigerDelegate = null;
        this.mTagParser = null;
        this.mParseTags = true;
    }
    
    @Override
    public void init(final ElementContext ctx) {
        if (ctx == null) {
            throw new IllegalArgumentException("null context");
        }
        super.init(ctx);
        this.getLogger().verbose("init()", this);
        this.initDelegate(ctx);
        this.initJavadoc((JamServiceContext)ctx);
    }
    
    @Override
    public MClass build(final String packageName, final String className) {
        this.assertInitialized();
        if (this.getLogger().isVerbose(this)) {
            this.getLogger().verbose("trying to build '" + packageName + "' '" + className + "'");
        }
        final String loadme = (packageName.trim().length() > 0) ? (packageName + '.' + className) : className;
        final ClassDoc cd = this.mRootDoc.classNamed(loadme);
        if (cd == null) {
            if (this.getLogger().isVerbose(this)) {
                this.getLogger().verbose("no ClassDoc for " + loadme);
            }
            return null;
        }
        List importSpecs = null;
        final ClassDoc[] imported = cd.importedClasses();
        if (imported != null) {
            importSpecs = new ArrayList();
            for (int i = 0; i < imported.length; ++i) {
                importSpecs.add(getFdFor((Type)imported[i]));
            }
        }
        final PackageDoc[] imported2 = cd.importedPackages();
        if (imported2 != null) {
            if (importSpecs == null) {
                importSpecs = new ArrayList();
            }
            for (int i = 0; i < imported2.length; ++i) {
                importSpecs.add(imported2[i].name() + ".*");
            }
        }
        String[] importSpecsArray = null;
        if (importSpecs != null) {
            importSpecsArray = new String[importSpecs.size()];
            importSpecs.toArray(importSpecsArray);
        }
        final MClass out = this.createClassToBuild(packageName, className, importSpecsArray, this);
        out.setArtifact(cd);
        return out;
    }
    
    @Override
    public void populate(final MClass dest) {
        if (dest == null) {
            throw new IllegalArgumentException("null dest");
        }
        this.assertInitialized();
        final ClassDoc src = (ClassDoc)dest.getArtifact();
        if (src == null) {
            throw new IllegalStateException("null artifact");
        }
        dest.setModifiers(src.modifierSpecifier());
        dest.setIsInterface(src.isInterface());
        if (this.mTigerDelegate != null) {
            dest.setIsEnumType(this.mTigerDelegate.isEnum(src));
        }
        final ClassDoc s = src.superclass();
        if (s != null) {
            dest.setSuperclass(getFdFor((Type)s));
        }
        final ClassDoc[] ints = src.interfaces();
        for (int i = 0; i < ints.length; ++i) {
            dest.addInterface(getFdFor((Type)ints[i]));
        }
        final FieldDoc[] fields = src.fields();
        for (int j = 0; j < fields.length; ++j) {
            this.populate(dest.addNewField(), fields[j]);
        }
        final ConstructorDoc[] ctors = src.constructors();
        for (int k = 0; k < ctors.length; ++k) {
            this.populate(dest.addNewConstructor(), (ExecutableMemberDoc)ctors[k]);
        }
        final MethodDoc[] methods = src.methods();
        for (int l = 0; l < methods.length; ++l) {
            this.populate(dest.addNewMethod(), methods[l]);
        }
        if (this.mTigerDelegate != null) {
            this.mTigerDelegate.populateAnnotationTypeIfNecessary(src, dest, this);
        }
        this.addAnnotations(dest, (ProgramElementDoc)src);
        addSourcePosition(dest, (Doc)src);
        final ClassDoc[] inners = src.innerClasses();
        if (inners != null) {
            for (int m = 0; m < inners.length; ++m) {
                final MClass inner = dest.addNewInnerClass(inners[m].typeName());
                inner.setArtifact(inners[m]);
                this.populate(inner);
            }
        }
    }
    
    public MMethod addMethod(final MClass dest, final MethodDoc doc) {
        final MMethod out = dest.addNewMethod();
        this.populate(out, doc);
        return out;
    }
    
    private void initDelegate(final ElementContext ctx) {
        this.mTigerDelegate = JavadocTigerDelegate.create(ctx);
    }
    
    private void initJavadoc(final JamServiceContext serviceContext) {
        this.mTagParser = serviceContext.getTagParser();
        final String pct = serviceContext.getProperty("javadoc.parsetags");
        if (pct != null) {
            this.mParseTags = Boolean.valueOf(pct);
            this.getLogger().verbose("mParseTags=" + this.mParseTags, this);
        }
        File[] files;
        try {
            files = serviceContext.getSourceFiles();
        }
        catch (final IOException ioe) {
            this.getLogger().error(ioe);
            return;
        }
        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("No source files in context.");
        }
        final String sourcePath = (serviceContext.getInputSourcepath() == null) ? null : serviceContext.getInputSourcepath().toString();
        final String classPath = (serviceContext.getInputClasspath() == null) ? null : serviceContext.getInputClasspath().toString();
        if (this.getLogger().isVerbose(this)) {
            this.getLogger().verbose("sourcePath =" + sourcePath);
            this.getLogger().verbose("classPath =" + classPath);
            for (int i = 0; i < files.length; ++i) {
                this.getLogger().verbose("including '" + files[i] + "'");
            }
        }
        final JavadocRunner jdr = JavadocRunner.newInstance();
        try {
            PrintWriter out = null;
            if (this.getLogger().isVerbose(this)) {
                out = new PrintWriter(System.out);
            }
            this.mRootDoc = jdr.run(files, out, sourcePath, classPath, this.getJavadocArgs(serviceContext), this.getLogger());
            if (this.mRootDoc == null) {
                this.getLogger().error("Javadoc returned a null root");
            }
            else {
                if (this.getLogger().isVerbose(this)) {
                    this.getLogger().verbose(" received " + this.mRootDoc.classes().length + " ClassDocs from javadoc: ");
                }
                final ClassDoc[] classes = this.mRootDoc.classes();
                for (int j = 0; j < classes.length; ++j) {
                    if (classes[j].containingClass() == null) {
                        if (this.getLogger().isVerbose(this)) {
                            this.getLogger().verbose("..." + classes[j].qualifiedName());
                        }
                        ((JamServiceContextImpl)serviceContext).includeClass(getFdFor((Type)classes[j]));
                    }
                }
            }
        }
        catch (final FileNotFoundException e) {
            this.getLogger().error(e);
        }
        catch (final IOException e2) {
            this.getLogger().error(e2);
        }
    }
    
    private void populate(final MField dest, final FieldDoc src) {
        dest.setArtifact(src);
        dest.setSimpleName(src.name());
        dest.setType(getFdFor(src.type()));
        dest.setModifiers(src.modifierSpecifier());
        this.addAnnotations(dest, (ProgramElementDoc)src);
        addSourcePosition(dest, (Doc)src);
    }
    
    private void populate(final MMethod dest, final MethodDoc src) {
        if (dest == null) {
            throw new IllegalArgumentException("null dest");
        }
        if (src == null) {
            throw new IllegalArgumentException("null src");
        }
        this.populate(dest, (ExecutableMemberDoc)src);
        dest.setReturnType(getFdFor(src.returnType()));
    }
    
    private void populate(final MInvokable dest, final ExecutableMemberDoc src) {
        if (dest == null) {
            throw new IllegalArgumentException("null dest");
        }
        if (src == null) {
            throw new IllegalArgumentException("null src");
        }
        dest.setArtifact(src);
        dest.setSimpleName(src.name());
        dest.setModifiers(src.modifierSpecifier());
        final ClassDoc[] exceptions = src.thrownExceptions();
        for (int i = 0; i < exceptions.length; ++i) {
            dest.addException(getFdFor((Type)exceptions[i]));
        }
        final Parameter[] params = src.parameters();
        for (int j = 0; j < params.length; ++j) {
            this.populate(dest.addNewParameter(), src, params[j]);
        }
        this.addAnnotations(dest, (ProgramElementDoc)src);
        addSourcePosition(dest, (Doc)src);
    }
    
    private void populate(final MParameter dest, final ExecutableMemberDoc method, final Parameter src) {
        dest.setArtifact(src);
        dest.setSimpleName(src.name());
        dest.setType(getFdFor(src.type()));
        if (this.mTigerDelegate != null) {
            this.mTigerDelegate.extractAnnotations(dest, method, src);
        }
    }
    
    private String[] getJavadocArgs(final JamServiceContext ctx) {
        final String prop = ctx.getProperty("javadoc.args");
        if (prop == null) {
            return null;
        }
        final StringTokenizer t = new StringTokenizer(prop);
        final String[] out = new String[t.countTokens()];
        int i = 0;
        while (t.hasMoreTokens()) {
            out[i++] = t.nextToken();
        }
        return out;
    }
    
    private void addAnnotations(final MAnnotatedElement dest, final ProgramElementDoc src) {
        final String comments = src.commentText();
        if (comments != null) {
            dest.createComment().setText(comments);
        }
        final Tag[] tags = src.tags();
        for (int i = 0; i < tags.length; ++i) {
            if (this.getLogger().isVerbose(this)) {
                this.getLogger().verbose("...'" + tags[i].name() + "' ' " + tags[i].text());
            }
            this.mTagParser.parse(dest, tags[i]);
        }
        if (this.mTigerDelegate != null) {
            this.mTigerDelegate.extractAnnotations(dest, src);
        }
    }
    
    public static String getFdFor(final Type t) {
        if (t == null) {
            throw new IllegalArgumentException("null type");
        }
        final String dim = t.dimension();
        if (dim != null && dim.length() != 0) {
            final StringWriter out = new StringWriter();
            for (int i = 0, iL = dim.length() / 2; i < iL; ++i) {
                out.write("[");
            }
            final String primFd = PrimitiveClassImpl.getPrimitiveClassForName(t.qualifiedTypeName());
            if (primFd != null) {
                out.write(primFd);
            }
            else {
                out.write("L");
                if (t.asClassDoc() != null) {
                    out.write(t.asClassDoc().qualifiedName());
                }
                else {
                    out.write(t.qualifiedTypeName());
                }
                out.write(";");
            }
            return out.toString();
        }
        final ClassDoc cd = t.asClassDoc();
        if (cd == null) {
            return t.qualifiedTypeName();
        }
        final ClassDoc outer = cd.containingClass();
        if (outer == null) {
            return cd.qualifiedName();
        }
        String simpleName = cd.name();
        simpleName = simpleName.substring(simpleName.lastIndexOf(46) + 1);
        return outer.qualifiedName() + '$' + simpleName;
    }
    
    public static void addSourcePosition(final MElement dest, final Doc src) {
        final SourcePosition pos = src.position();
        if (pos != null) {
            addSourcePosition(dest, pos);
        }
    }
    
    public static void addSourcePosition(final MElement dest, final SourcePosition pos) {
        final MSourcePosition sp = dest.createSourcePosition();
        sp.setColumn(pos.column());
        sp.setLine(pos.line());
        final File f = pos.file();
        if (f != null) {
            sp.setSourceURI(f.toURI());
        }
    }
}
