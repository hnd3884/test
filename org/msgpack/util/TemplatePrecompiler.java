package org.msgpack.util;

import java.io.File;
import java.util.Properties;
import java.lang.reflect.Type;
import org.msgpack.template.builder.JavassistTemplateBuilder;
import java.util.regex.Matcher;
import javax.tools.JavaCompiler;
import java.util.Set;
import javax.tools.JavaFileManager;
import javax.tools.StandardLocation;
import java.util.HashSet;
import java.nio.charset.Charset;
import java.util.Locale;
import javax.tools.JavaFileObject;
import javax.tools.DiagnosticListener;
import javax.tools.DiagnosticCollector;
import javax.tools.ToolProvider;
import java.util.regex.Pattern;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.msgpack.template.TemplateRegistry;
import java.util.logging.Logger;

public class TemplatePrecompiler
{
    private static final Logger LOG;
    public static final String DEST = "msgpack.template.destdir";
    public static final String DEFAULT_DEST = ".";
    
    public static void saveTemplates(final String[] classNames) throws IOException, ClassNotFoundException {
        final TemplateRegistry registry = new TemplateRegistry(null);
        final List<String> ret = new ArrayList<String>();
        for (final String className : classNames) {
            matchClassNames(ret, className);
        }
        final List<Class<?>> ret2 = toClass(ret);
        for (final Class<?> c : ret2) {
            saveTemplateClass(registry, c);
        }
    }
    
    private static void matchClassNames(final List<String> ret, final String className) throws IOException {
        final String packageName = className.substring(0, className.lastIndexOf(46));
        final String relativedName = className.substring(className.lastIndexOf(46) + 1, className.length());
        final String patName = relativedName.replace("*", "(\\w+)");
        final Pattern pat = Pattern.compile(patName);
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        final JavaFileManager fm = compiler.getStandardFileManager(new DiagnosticCollector<Object>(), null, null);
        final HashSet<JavaFileObject.Kind> kind = new HashSet<JavaFileObject.Kind>() {
            {
                this.add(JavaFileObject.Kind.CLASS);
            }
        };
        for (final JavaFileObject f : fm.list(StandardLocation.PLATFORM_CLASS_PATH, packageName, kind, false)) {
            final String relatived0 = f.getName();
            final String name0 = relatived0.substring(0, relatived0.length() - ".class".length());
            final Matcher m = pat.matcher(name0);
            if (m.matches()) {
                final String name2 = packageName + '.' + name0;
                if (ret.contains(name2)) {
                    continue;
                }
                ret.add(name2);
            }
        }
    }
    
    private static List<Class<?>> toClass(final List<String> classNames) throws ClassNotFoundException {
        final List<Class<?>> ret = new ArrayList<Class<?>>(classNames.size());
        final ClassLoader cl = TemplatePrecompiler.class.getClassLoader();
        for (final String className : classNames) {
            final Class<?> c = cl.loadClass(className);
            ret.add(c);
        }
        return ret;
    }
    
    public static void saveTemplateClasses(final TemplateRegistry registry, final Class<?>[] targetClasses) throws IOException {
        for (final Class<?> c : targetClasses) {
            saveTemplateClass(registry, c);
        }
    }
    
    public static void saveTemplateClass(final TemplateRegistry registry, final Class<?> targetClass) throws IOException {
        TemplatePrecompiler.LOG.info("Saving template of " + targetClass.getName() + "...");
        final Properties props = System.getProperties();
        final String distDirName = getDirName(props, "msgpack.template.destdir", ".");
        if (targetClass.isEnum()) {
            throw new UnsupportedOperationException("Not supported enum type yet: " + targetClass.getName());
        }
        new JavassistTemplateBuilder(registry).writeTemplate(targetClass, distDirName);
        TemplatePrecompiler.LOG.info("Saved .class file of template class of " + targetClass.getName());
    }
    
    public static boolean deleteTemplateClass(final Class<?> targetClass) throws IOException {
        TemplatePrecompiler.LOG.info("Deleting template of " + targetClass.getName() + "...");
        final Properties props = System.getProperties();
        final String distDirName = getDirName(props, "msgpack.template.destdir", ".");
        final String targetClassName = targetClass.getName();
        final String targetClassFileName = targetClassName.replace('.', File.separatorChar) + "_$$_Template.class";
        final File targetFile = new File(distDirName + File.separatorChar + targetClassFileName);
        boolean deleted = false;
        if (!targetFile.isDirectory() && targetFile.exists()) {
            deleted = targetFile.delete();
        }
        TemplatePrecompiler.LOG.info("Deleted .class file of template class of " + targetClass.getName());
        return deleted;
    }
    
    private static String getDirName(final Properties props, final String dirName, final String defaultDirName) throws IOException {
        final String dName = props.getProperty(dirName, defaultDirName);
        final File d = new File(dName);
        if (!d.isDirectory() && !d.exists()) {
            throw new IOException("Directory not exists: " + dName);
        }
        return d.getAbsolutePath();
    }
    
    public static void main(final String[] args) throws Exception {
        saveTemplates(args);
    }
    
    static {
        LOG = Logger.getLogger(TemplatePrecompiler.class.getName());
    }
}
