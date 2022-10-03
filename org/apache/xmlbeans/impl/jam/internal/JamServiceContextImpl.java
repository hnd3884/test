package org.apache.xmlbeans.impl.jam.internal;

import java.util.HashMap;
import org.apache.xmlbeans.impl.jam.annotation.DefaultAnnotationProxy;
import org.apache.xmlbeans.impl.jam.annotation.AnnotationProxy;
import java.io.PrintWriter;
import org.apache.xmlbeans.impl.jam.visitor.CompositeMVisitor;
import java.util.ArrayList;
import org.apache.xmlbeans.impl.jam.provider.ResourcePath;
import java.io.File;
import org.apache.xmlbeans.impl.jam.provider.JamLogger;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.Collection;
import java.util.HashSet;
import org.apache.xmlbeans.impl.jam.visitor.PropertyInitializer;
import org.apache.xmlbeans.impl.jam.annotation.WhitespaceDelimitedTagParser;
import org.apache.xmlbeans.impl.jam.provider.CompositeJamClassBuilder;
import org.apache.xmlbeans.impl.jam.provider.JamClassBuilder;
import org.apache.xmlbeans.impl.jam.JamClassLoader;
import org.apache.xmlbeans.impl.jam.visitor.MVisitor;
import org.apache.xmlbeans.impl.jam.annotation.JavadocTagParser;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.xmlbeans.impl.jam.internal.elements.ElementContext;
import org.apache.xmlbeans.impl.jam.JamServiceParams;
import org.apache.xmlbeans.impl.jam.provider.JamServiceContext;

public class JamServiceContextImpl extends JamLoggerImpl implements JamServiceContext, JamServiceParams, ElementContext
{
    private static final char INNER_CLASS_SEPARATOR = '$';
    private boolean m14WarningsEnabled;
    private Properties mProperties;
    private Map mSourceRoot2Scanner;
    private Map mClassRoot2Scanner;
    private List mClasspath;
    private List mSourcepath;
    private List mToolClasspath;
    private List mIncludeClasses;
    private List mExcludeClasses;
    private boolean mUseSystemClasspath;
    private JavadocTagParser mTagParser;
    private MVisitor mCommentInitializer;
    private MVisitor mPropertyInitializer;
    private List mOtherInitializers;
    private List mUnstructuredSourceFiles;
    private List mClassLoaders;
    private List mBaseBuilders;
    private JamClassLoader mLoader;
    private static final String PREFIX = "[JamServiceContextImpl] ";
    
    public void setClassLoader(final JamClassLoader loader) {
        this.mLoader = loader;
    }
    
    @Override
    public JamClassBuilder getBaseBuilder() {
        if (this.mBaseBuilders == null || this.mBaseBuilders.size() == 0) {
            return null;
        }
        if (this.mBaseBuilders.size() == 1) {
            return this.mBaseBuilders.get(0);
        }
        final JamClassBuilder[] comp = new JamClassBuilder[this.mBaseBuilders.size()];
        this.mBaseBuilders.toArray(comp);
        return new CompositeJamClassBuilder(comp);
    }
    
    @Override
    public JavadocTagParser getTagParser() {
        if (this.mTagParser == null) {
            (this.mTagParser = new WhitespaceDelimitedTagParser()).init(this);
        }
        return this.mTagParser;
    }
    
    public JamServiceContextImpl() {
        this.m14WarningsEnabled = false;
        this.mProperties = null;
        this.mSourceRoot2Scanner = null;
        this.mClassRoot2Scanner = null;
        this.mClasspath = null;
        this.mSourcepath = null;
        this.mToolClasspath = null;
        this.mIncludeClasses = null;
        this.mExcludeClasses = null;
        this.mUseSystemClasspath = true;
        this.mTagParser = null;
        this.mCommentInitializer = null;
        this.mPropertyInitializer = new PropertyInitializer();
        this.mOtherInitializers = null;
        this.mUnstructuredSourceFiles = null;
        this.mClassLoaders = null;
        this.mBaseBuilders = null;
        this.mLoader = null;
    }
    
    @Override
    public String[] getAllClassnames() throws IOException {
        final Set all = new HashSet();
        if (this.mIncludeClasses != null) {
            all.addAll(this.mIncludeClasses);
        }
        final Iterator i = this.getAllDirectoryScanners();
        while (i.hasNext()) {
            final DirectoryScanner ds = i.next();
            final String[] files = ds.getIncludedFiles();
            for (int j = 0; j < files.length; ++j) {
                if (files[j].indexOf(36) == -1) {
                    all.add(filename2classname(files[j]));
                }
            }
        }
        if (this.mExcludeClasses != null) {
            all.removeAll(this.mExcludeClasses);
        }
        final String[] out = new String[all.size()];
        all.toArray(out);
        return out;
    }
    
    @Override
    public JamLogger getLogger() {
        return this;
    }
    
    @Override
    public File[] getSourceFiles() throws IOException {
        final Set set = new HashSet();
        if (this.mSourceRoot2Scanner != null) {
            for (final DirectoryScanner ds : this.mSourceRoot2Scanner.values()) {
                if (this.isVerbose(this)) {
                    this.verbose("[JamServiceContextImpl]  checking scanner for dir" + ds.getRoot());
                }
                final String[] files = ds.getIncludedFiles();
                for (int j = 0; j < files.length; ++j) {
                    if (this.isVerbose(this)) {
                        this.verbose("[JamServiceContextImpl]  ...including a source file " + files[j]);
                    }
                    set.add(new File(ds.getRoot(), files[j]));
                }
            }
        }
        if (this.mUnstructuredSourceFiles != null) {
            if (this.isVerbose(this)) {
                this.verbose("[JamServiceContextImpl] adding " + this.mUnstructuredSourceFiles.size() + " other source files");
            }
            set.addAll(this.mUnstructuredSourceFiles);
        }
        final File[] out = new File[set.size()];
        set.toArray(out);
        return out;
    }
    
    public File[] getUnstructuredSourceFiles() {
        if (this.mUnstructuredSourceFiles == null) {
            return null;
        }
        final File[] out = new File[this.mUnstructuredSourceFiles.size()];
        this.mUnstructuredSourceFiles.toArray(out);
        return out;
    }
    
    @Override
    public ResourcePath getInputClasspath() {
        return createJPath(this.mClasspath);
    }
    
    @Override
    public ResourcePath getInputSourcepath() {
        return createJPath(this.mSourcepath);
    }
    
    @Override
    public ResourcePath getToolClasspath() {
        return createJPath(this.mToolClasspath);
    }
    
    @Override
    public String getProperty(final String name) {
        return (this.mProperties == null) ? null : this.mProperties.getProperty(name);
    }
    
    @Override
    public MVisitor getInitializer() {
        final List initers = new ArrayList();
        if (this.mCommentInitializer != null) {
            initers.add(this.mCommentInitializer);
        }
        if (this.mPropertyInitializer != null) {
            initers.add(this.mPropertyInitializer);
        }
        if (this.mOtherInitializers != null) {
            initers.addAll(this.mOtherInitializers);
        }
        final MVisitor[] inits = new MVisitor[initers.size()];
        initers.toArray(inits);
        return new CompositeMVisitor(inits);
    }
    
    @Override
    public void addClassBuilder(final JamClassBuilder builder) {
        if (this.mBaseBuilders == null) {
            this.mBaseBuilders = new ArrayList();
        }
        this.mBaseBuilders.add(builder);
    }
    
    public void setCommentInitializer(final MVisitor initializer) {
        this.mCommentInitializer = initializer;
    }
    
    @Override
    public void setPropertyInitializer(final MVisitor initializer) {
        this.mPropertyInitializer = initializer;
    }
    
    @Override
    public void addInitializer(final MVisitor initializer) {
        if (this.mOtherInitializers == null) {
            this.mOtherInitializers = new ArrayList();
        }
        this.mOtherInitializers.add(initializer);
    }
    
    @Override
    public void setJavadocTagParser(final JavadocTagParser tp) {
        (this.mTagParser = tp).init(this);
    }
    
    @Override
    public void includeSourceFile(File file) {
        if (file == null) {
            throw new IllegalArgumentException("null file");
        }
        file = file.getAbsoluteFile();
        if (this.isVerbose(this)) {
            this.verbose("[JamServiceContextImpl] adding source ");
        }
        if (!file.exists()) {
            throw new IllegalArgumentException(file + " does not exist");
        }
        if (file.isDirectory()) {
            throw new IllegalArgumentException(file + " cannot be included as a source file because it is a directory.");
        }
        if (this.mUnstructuredSourceFiles == null) {
            this.mUnstructuredSourceFiles = new ArrayList();
        }
        this.mUnstructuredSourceFiles.add(file.getAbsoluteFile());
    }
    
    @Override
    public void includeSourcePattern(final File[] sourcepath, String pattern) {
        if (sourcepath == null) {
            throw new IllegalArgumentException("null sourcepath");
        }
        if (sourcepath.length == 0) {
            throw new IllegalArgumentException("empty sourcepath");
        }
        if (pattern == null) {
            throw new IllegalArgumentException("null pattern");
        }
        pattern = pattern.trim();
        if (pattern.length() == 0) {
            throw new IllegalArgumentException("empty pattern");
        }
        for (int i = 0; i < sourcepath.length; ++i) {
            if (this.isVerbose(this)) {
                this.verbose("[JamServiceContextImpl] including '" + pattern + "' under " + sourcepath[i]);
            }
            this.addSourcepath(sourcepath[i]);
            this.getSourceScanner(sourcepath[i]).include(pattern);
        }
    }
    
    @Override
    public void includeClassPattern(final File[] classpath, String pattern) {
        if (classpath == null) {
            throw new IllegalArgumentException("null classpath");
        }
        if (classpath.length == 0) {
            throw new IllegalArgumentException("empty classpath");
        }
        if (pattern == null) {
            throw new IllegalArgumentException("null pattern");
        }
        pattern = pattern.trim();
        if (pattern.length() == 0) {
            throw new IllegalArgumentException("empty pattern");
        }
        for (int i = 0; i < classpath.length; ++i) {
            if (this.isVerbose(this)) {
                this.verbose("[JamServiceContextImpl] including '" + pattern + "' under " + classpath[i]);
            }
            this.addClasspath(classpath[i]);
            this.getClassScanner(classpath[i]).include(pattern);
        }
    }
    
    @Override
    public void excludeSourcePattern(final File[] sourcepath, String pattern) {
        if (sourcepath == null) {
            throw new IllegalArgumentException("null sourcepath");
        }
        if (sourcepath.length == 0) {
            throw new IllegalArgumentException("empty sourcepath");
        }
        if (pattern == null) {
            throw new IllegalArgumentException("null pattern");
        }
        pattern = pattern.trim();
        if (pattern.length() == 0) {
            throw new IllegalArgumentException("empty pattern");
        }
        for (int i = 0; i < sourcepath.length; ++i) {
            if (this.isVerbose(this)) {
                this.verbose("[JamServiceContextImpl] EXCLUDING '" + pattern + "' under " + sourcepath[i]);
            }
            this.addSourcepath(sourcepath[i]);
            this.getSourceScanner(sourcepath[i]).exclude(pattern);
        }
    }
    
    @Override
    public void excludeClassPattern(final File[] classpath, String pattern) {
        if (classpath == null) {
            throw new IllegalArgumentException("null classpath");
        }
        if (classpath.length == 0) {
            throw new IllegalArgumentException("empty classpath");
        }
        if (pattern == null) {
            throw new IllegalArgumentException("null pattern");
        }
        pattern = pattern.trim();
        if (pattern.length() == 0) {
            throw new IllegalArgumentException("empty pattern");
        }
        for (int i = 0; i < classpath.length; ++i) {
            if (this.isVerbose(this)) {
                this.verbose("[JamServiceContextImpl] EXCLUDING '" + pattern + "' under " + classpath[i]);
            }
            this.addClasspath(classpath[i]);
            this.getClassScanner(classpath[i]).exclude(pattern);
        }
    }
    
    @Override
    public void includeSourceFile(final File[] sourcepath, final File sourceFile) {
        final File root = this.getPathRootForFile(sourcepath, sourceFile);
        this.includeSourcePattern(new File[] { root }, this.source2pattern(root, sourceFile));
    }
    
    @Override
    public void excludeSourceFile(final File[] sourcepath, final File sourceFile) {
        final File root = this.getPathRootForFile(sourcepath, sourceFile);
        this.excludeSourcePattern(new File[] { root }, this.source2pattern(root, sourceFile));
    }
    
    @Override
    public void includeClassFile(final File[] classpath, final File classFile) {
        final File root = this.getPathRootForFile(classpath, classFile);
        this.includeClassPattern(new File[] { root }, this.source2pattern(root, classFile));
    }
    
    @Override
    public void excludeClassFile(final File[] classpath, final File classFile) {
        final File root = this.getPathRootForFile(classpath, classFile);
        this.excludeClassPattern(new File[] { root }, this.source2pattern(root, classFile));
    }
    
    @Override
    public void includeClass(final String qualifiedClassname) {
        if (this.mIncludeClasses == null) {
            this.mIncludeClasses = new ArrayList();
        }
        this.mIncludeClasses.add(qualifiedClassname);
    }
    
    @Override
    public void excludeClass(final String qualifiedClassname) {
        if (this.mExcludeClasses == null) {
            this.mExcludeClasses = new ArrayList();
        }
        this.mExcludeClasses.add(qualifiedClassname);
    }
    
    @Override
    public void addClasspath(final File classpathElement) {
        if (this.mClasspath == null) {
            this.mClasspath = new ArrayList();
        }
        else if (this.mClasspath.contains(classpathElement)) {
            return;
        }
        this.mClasspath.add(classpathElement);
    }
    
    @Override
    public void setLoggerWriter(final PrintWriter out) {
        super.setOut(out);
    }
    
    @Override
    public void setJamLogger(final JamLogger logger) {
        throw new IllegalStateException("NYI");
    }
    
    @Override
    public void addSourcepath(final File sourcepathElement) {
        if (this.mSourcepath == null) {
            this.mSourcepath = new ArrayList();
        }
        else if (this.mSourcepath.contains(sourcepathElement)) {
            return;
        }
        this.mSourcepath.add(sourcepathElement);
    }
    
    @Override
    public void addToolClasspath(final File classpathElement) {
        if (this.mToolClasspath == null) {
            this.mToolClasspath = new ArrayList();
        }
        else if (this.mToolClasspath.contains(classpathElement)) {
            return;
        }
        this.mToolClasspath.add(classpathElement);
    }
    
    @Override
    public void setProperty(final String name, final String value) {
        if (this.mProperties == null) {
            this.mProperties = new Properties();
        }
        this.mProperties.setProperty(name, value);
    }
    
    @Override
    public void set14WarningsEnabled(final boolean b) {
        this.m14WarningsEnabled = b;
    }
    
    @Override
    public void setParentClassLoader(final JamClassLoader loader) {
        throw new IllegalStateException("NYI");
    }
    
    @Override
    public void setUseSystemClasspath(final boolean use) {
        this.mUseSystemClasspath = use;
    }
    
    @Override
    public void addClassLoader(final ClassLoader cl) {
        if (this.mClassLoaders == null) {
            this.mClassLoaders = new ArrayList();
        }
        this.mClassLoaders.add(cl);
    }
    
    @Override
    public ClassLoader[] getReflectionClassLoaders() {
        if (this.mClassLoaders != null) {
            final ClassLoader[] out = new ClassLoader[this.mClassLoaders.size() + (this.mUseSystemClasspath ? 1 : 0)];
            for (int i = 0; i < this.mClassLoaders.size(); ++i) {
                out[i] = this.mClassLoaders.get(i);
            }
            if (this.mUseSystemClasspath) {
                out[out.length - 1] = ClassLoader.getSystemClassLoader();
            }
            return out;
        }
        if (this.mUseSystemClasspath) {
            return new ClassLoader[] { ClassLoader.getSystemClassLoader() };
        }
        return new ClassLoader[0];
    }
    
    @Override
    public boolean is14WarningsEnabled() {
        return this.m14WarningsEnabled;
    }
    
    @Override
    public JamClassLoader getClassLoader() {
        return this.mLoader;
    }
    
    @Override
    public AnnotationProxy createAnnotationProxy(final String jsr175typename) {
        final AnnotationProxy out = new DefaultAnnotationProxy();
        out.init(this);
        return out;
    }
    
    private File getPathRootForFile(final File[] sourcepath, File sourceFile) {
        if (sourcepath == null) {
            throw new IllegalArgumentException("null sourcepath");
        }
        if (sourcepath.length == 0) {
            throw new IllegalArgumentException("empty sourcepath");
        }
        if (sourceFile == null) {
            throw new IllegalArgumentException("null sourceFile");
        }
        sourceFile = sourceFile.getAbsoluteFile();
        if (this.isVerbose(this)) {
            this.verbose("[JamServiceContextImpl] Getting root for " + sourceFile + "...");
        }
        for (int i = 0; i < sourcepath.length; ++i) {
            if (this.isVerbose(this)) {
                this.verbose("[JamServiceContextImpl] ...looking in " + sourcepath[i]);
            }
            if (this.isContainingDir(sourcepath[i].getAbsoluteFile(), sourceFile)) {
                if (this.isVerbose(this)) {
                    this.verbose("[JamServiceContextImpl] ...found it!");
                }
                return sourcepath[i].getAbsoluteFile();
            }
        }
        throw new IllegalArgumentException(sourceFile + " is not in the given path.");
    }
    
    private boolean isContainingDir(final File dir, final File file) {
        if (this.isVerbose(this)) {
            this.verbose("[JamServiceContextImpl] ... ...isContainingDir " + dir + "  " + file);
        }
        if (file == null) {
            return false;
        }
        if (dir.equals(file)) {
            if (this.isVerbose(this)) {
                this.verbose("[JamServiceContextImpl] ... ...yes!");
            }
            return true;
        }
        return this.isContainingDir(dir, file.getParentFile());
    }
    
    private String source2pattern(final File root, final File sourceFile) {
        if (this.isVerbose(this)) {
            this.verbose("[JamServiceContextImpl] source2pattern " + root + "  " + sourceFile);
        }
        final String r = root.getAbsolutePath();
        final String s = sourceFile.getAbsolutePath();
        if (this.isVerbose(this)) {
            this.verbose("[JamServiceContextImpl] source2pattern returning " + s.substring(r.length() + 1));
        }
        return s.substring(r.length() + 1);
    }
    
    private static String filename2classname(String filename) {
        final int extDot = filename.lastIndexOf(46);
        if (extDot != -1) {
            filename = filename.substring(0, extDot);
        }
        filename = filename.replace('/', '.');
        filename = filename.replace('\\', '.');
        return filename;
    }
    
    private Iterator getAllDirectoryScanners() {
        final Collection out = new ArrayList();
        if (this.mSourceRoot2Scanner != null) {
            out.addAll(this.mSourceRoot2Scanner.values());
        }
        if (this.mClassRoot2Scanner != null) {
            out.addAll(this.mClassRoot2Scanner.values());
        }
        return out.iterator();
    }
    
    private static ResourcePath createJPath(final Collection filelist) {
        if (filelist == null || filelist.size() == 0) {
            return null;
        }
        final File[] files = new File[filelist.size()];
        filelist.toArray(files);
        return ResourcePath.forFiles(files);
    }
    
    private DirectoryScanner getSourceScanner(final File srcRoot) {
        if (this.mSourceRoot2Scanner == null) {
            this.mSourceRoot2Scanner = new HashMap();
        }
        DirectoryScanner out = this.mSourceRoot2Scanner.get(srcRoot);
        if (out == null) {
            this.mSourceRoot2Scanner.put(srcRoot, out = new DirectoryScanner(srcRoot, this));
        }
        return out;
    }
    
    private DirectoryScanner getClassScanner(final File clsRoot) {
        if (this.mClassRoot2Scanner == null) {
            this.mClassRoot2Scanner = new HashMap();
        }
        DirectoryScanner out = this.mClassRoot2Scanner.get(clsRoot);
        if (out == null) {
            this.mClassRoot2Scanner.put(clsRoot, out = new DirectoryScanner(clsRoot, this));
        }
        return out;
    }
}
