package org.apache.catalina.startup;

import org.apache.juli.logging.LogFactory;
import java.lang.reflect.Method;
import java.io.File;
import java.util.ArrayList;
import org.apache.juli.logging.Log;

public final class Tool
{
    private static final Log log;
    private static boolean ant;
    private static final String catalinaHome;
    private static boolean common;
    private static boolean server;
    private static boolean shared;
    
    public static void main(final String[] args) {
        if (Tool.catalinaHome == null) {
            Tool.log.error((Object)"Must set 'catalina.home' system property");
            System.exit(1);
        }
        int index = 0;
        while (true) {
            if (index == args.length) {
                usage();
                System.exit(1);
            }
            if ("-ant".equals(args[index])) {
                Tool.ant = true;
            }
            else if ("-common".equals(args[index])) {
                Tool.common = true;
            }
            else if ("-server".equals(args[index])) {
                Tool.server = true;
            }
            else {
                if (!"-shared".equals(args[index])) {
                    break;
                }
                Tool.shared = true;
            }
            ++index;
        }
        if (index > args.length) {
            usage();
            System.exit(1);
        }
        if (Tool.ant) {
            System.setProperty("ant.home", Tool.catalinaHome);
        }
        ClassLoader classLoader = null;
        try {
            final ArrayList<File> packed = new ArrayList<File>();
            final ArrayList<File> unpacked = new ArrayList<File>();
            unpacked.add(new File(Tool.catalinaHome, "classes"));
            packed.add(new File(Tool.catalinaHome, "lib"));
            if (Tool.common) {
                unpacked.add(new File(Tool.catalinaHome, "common" + File.separator + "classes"));
                packed.add(new File(Tool.catalinaHome, "common" + File.separator + "lib"));
            }
            if (Tool.server) {
                unpacked.add(new File(Tool.catalinaHome, "server" + File.separator + "classes"));
                packed.add(new File(Tool.catalinaHome, "server" + File.separator + "lib"));
            }
            if (Tool.shared) {
                unpacked.add(new File(Tool.catalinaHome, "shared" + File.separator + "classes"));
                packed.add(new File(Tool.catalinaHome, "shared" + File.separator + "lib"));
            }
            classLoader = ClassLoaderFactory.createClassLoader(unpacked.toArray(new File[0]), packed.toArray(new File[0]), null);
        }
        catch (final Throwable t) {
            Bootstrap.handleThrowable(t);
            Tool.log.error((Object)"Class loader creation threw exception", t);
            System.exit(1);
        }
        Thread.currentThread().setContextClassLoader(classLoader);
        Class<?> clazz = null;
        final String className = args[index++];
        try {
            if (Tool.log.isDebugEnabled()) {
                Tool.log.debug((Object)("Loading application class " + className));
            }
            clazz = classLoader.loadClass(className);
        }
        catch (final Throwable t2) {
            Bootstrap.handleThrowable(t2);
            Tool.log.error((Object)("Exception creating instance of " + className), t2);
            System.exit(1);
        }
        Method method = null;
        final String[] params = new String[args.length - index];
        System.arraycopy(args, index, params, 0, params.length);
        try {
            if (Tool.log.isDebugEnabled()) {
                Tool.log.debug((Object)"Identifying main() method");
            }
            final String methodName = "main";
            final Class<?>[] paramTypes = { params.getClass() };
            method = clazz.getMethod(methodName, paramTypes);
        }
        catch (final Throwable t3) {
            Bootstrap.handleThrowable(t3);
            Tool.log.error((Object)"Exception locating main() method", t3);
            System.exit(1);
        }
        try {
            if (Tool.log.isDebugEnabled()) {
                Tool.log.debug((Object)"Calling main() method");
            }
            final Object[] paramValues = { params };
            method.invoke(null, paramValues);
        }
        catch (Throwable t3) {
            t3 = Bootstrap.unwrapInvocationTargetException(t3);
            Bootstrap.handleThrowable(t3);
            Tool.log.error((Object)"Exception calling main() method", t3);
            System.exit(1);
        }
    }
    
    private static void usage() {
        Tool.log.info((Object)"Usage:  java org.apache.catalina.startup.Tool [<options>] <class> [<arguments>]");
    }
    
    static {
        log = LogFactory.getLog((Class)Tool.class);
        Tool.ant = false;
        catalinaHome = System.getProperty("catalina.home");
        Tool.common = false;
        Tool.server = false;
        Tool.shared = false;
    }
}
