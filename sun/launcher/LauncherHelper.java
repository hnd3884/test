package sun.launcher;

import java.math.RoundingMode;
import java.math.BigDecimal;
import java.util.ResourceBundle;
import sun.misc.VM;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.Normalizer;
import java.util.jar.Manifest;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.io.File;
import java.text.MessageFormat;
import jdk.internal.platform.Metrics;
import jdk.internal.platform.Container;
import java.util.TreeSet;
import java.util.Locale;
import java.util.Iterator;
import java.util.Properties;
import java.util.List;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.io.PrintStream;

public enum LauncherHelper
{
    INSTANCE;
    
    private static final String MAIN_CLASS = "Main-Class";
    private static StringBuilder outBuf;
    private static final String INDENT = "    ";
    private static final String VM_SETTINGS = "VM settings:";
    private static final String PROP_SETTINGS = "Property settings:";
    private static final String LOCALE_SETTINGS = "Locale settings:";
    private static final String diagprop = "sun.java.launcher.diag";
    static final boolean trace;
    private static final String defaultBundleName = "sun.launcher.resources.launcher";
    private static PrintStream ostream;
    private static final ClassLoader scloader;
    private static Class<?> appClass;
    private static final int LM_UNKNOWN = 0;
    private static final int LM_CLASS = 1;
    private static final int LM_JAR = 2;
    private static final String encprop = "sun.jnu.encoding";
    private static String encoding;
    private static boolean isCharsetSupported;
    
    static void showSettings(final boolean b, final String s, final long n, final long n2, final long n3, final boolean b2) {
        initOutput(b);
        final String[] split = s.split(":");
        final String s2 = (split.length > 1 && split[1] != null) ? split[1].trim() : "all";
        switch (s2) {
            case "vm": {
                printVmSettings(n, n2, n3, b2);
                return;
            }
            case "properties": {
                printProperties();
                return;
            }
            case "locale": {
                printLocale();
                return;
            }
            case "system": {
                if (System.getProperty("os.name").contains("Linux")) {
                    printSystemMetrics();
                    return;
                }
                break;
            }
        }
        printVmSettings(n, n2, n3, b2);
        printProperties();
        printLocale();
        if (System.getProperty("os.name").contains("Linux")) {
            printSystemMetrics();
        }
    }
    
    private static void printVmSettings(final long n, final long n2, final long n3, final boolean b) {
        LauncherHelper.ostream.println("VM settings:");
        if (n3 != 0L) {
            LauncherHelper.ostream.println("    Stack Size: " + SizePrefix.scaleValue(n3));
        }
        if (n != 0L) {
            LauncherHelper.ostream.println("    Min. Heap Size: " + SizePrefix.scaleValue(n));
        }
        if (n2 != 0L) {
            LauncherHelper.ostream.println("    Max. Heap Size: " + SizePrefix.scaleValue(n2));
        }
        else {
            LauncherHelper.ostream.println("    Max. Heap Size (Estimated): " + SizePrefix.scaleValue(Runtime.getRuntime().maxMemory()));
        }
        LauncherHelper.ostream.println("    Ergonomics Machine Class: " + (b ? "server" : "client"));
        LauncherHelper.ostream.println("    Using VM: " + System.getProperty("java.vm.name"));
        LauncherHelper.ostream.println();
    }
    
    private static void printProperties() {
        final Properties properties = System.getProperties();
        LauncherHelper.ostream.println("Property settings:");
        final ArrayList list = new ArrayList();
        list.addAll(properties.stringPropertyNames());
        Collections.sort((List<Comparable>)list);
        for (final String s : list) {
            printPropertyValue(s, properties.getProperty(s));
        }
        LauncherHelper.ostream.println();
    }
    
    private static boolean isPath(final String s) {
        return s.endsWith(".dirs") || s.endsWith(".path");
    }
    
    private static void printPropertyValue(final String s, final String s2) {
        LauncherHelper.ostream.print("    " + s + " = ");
        if (s.equals("line.separator")) {
            for (final byte b : s2.getBytes()) {
                switch (b) {
                    case 13: {
                        LauncherHelper.ostream.print("\\r ");
                        break;
                    }
                    case 10: {
                        LauncherHelper.ostream.print("\\n ");
                        break;
                    }
                    default: {
                        LauncherHelper.ostream.printf("0x%02X", b & 0xFF);
                        break;
                    }
                }
            }
            LauncherHelper.ostream.println();
            return;
        }
        if (!isPath(s)) {
            LauncherHelper.ostream.println(s2);
            return;
        }
        final String[] split = s2.split(System.getProperty("path.separator"));
        int n = 1;
        for (final String s3 : split) {
            if (n != 0) {
                LauncherHelper.ostream.println(s3);
                n = 0;
            }
            else {
                LauncherHelper.ostream.println("        " + s3);
            }
        }
    }
    
    private static void printLocale() {
        final Locale default1 = Locale.getDefault();
        LauncherHelper.ostream.println("Locale settings:");
        LauncherHelper.ostream.println("    default locale = " + default1.getDisplayLanguage());
        LauncherHelper.ostream.println("    default display locale = " + Locale.getDefault(Locale.Category.DISPLAY).getDisplayName());
        LauncherHelper.ostream.println("    default format locale = " + Locale.getDefault(Locale.Category.FORMAT).getDisplayName());
        printLocales();
        LauncherHelper.ostream.println();
    }
    
    private static void printLocales() {
        final Locale[] availableLocales = Locale.getAvailableLocales();
        final int n = (availableLocales == null) ? 0 : availableLocales.length;
        if (n < 1) {
            return;
        }
        final TreeSet set = new TreeSet();
        final Locale[] array = availableLocales;
        for (int length = array.length, i = 0; i < length; ++i) {
            set.add(array[i].toString());
        }
        LauncherHelper.ostream.print("    available locales = ");
        final Iterator iterator = set.iterator();
        final int n2 = n - 1;
        int n3 = 0;
        while (iterator.hasNext()) {
            LauncherHelper.ostream.print((String)iterator.next());
            if (n3 != n2) {
                LauncherHelper.ostream.print(", ");
            }
            if ((n3 + 1) % 8 == 0) {
                LauncherHelper.ostream.println();
                LauncherHelper.ostream.print("        ");
            }
            ++n3;
        }
    }
    
    public static void printSystemMetrics() {
        final Metrics metrics = Container.metrics();
        LauncherHelper.ostream.println("Operating System Metrics:");
        if (metrics == null) {
            LauncherHelper.ostream.println("    No metrics available for this platform");
            return;
        }
        LauncherHelper.ostream.println("    Provider: " + metrics.getProvider());
        LauncherHelper.ostream.println("    Effective CPU Count: " + metrics.getEffectiveCpuCount());
        LauncherHelper.ostream.println("    CPU Period: " + metrics.getCpuPeriod() + ((metrics.getCpuPeriod() == -1L) ? "" : "us"));
        LauncherHelper.ostream.println("    CPU Quota: " + metrics.getCpuQuota() + ((metrics.getCpuQuota() == -1L) ? "" : "us"));
        LauncherHelper.ostream.println("    CPU Shares: " + metrics.getCpuShares());
        final int[] cpuSetCpus = metrics.getCpuSetCpus();
        LauncherHelper.ostream.println("    List of Processors, " + cpuSetCpus.length + " total: ");
        LauncherHelper.ostream.print("    ");
        for (int i = 0; i < cpuSetCpus.length; ++i) {
            LauncherHelper.ostream.print(cpuSetCpus[i] + " ");
        }
        if (cpuSetCpus.length > 0) {
            LauncherHelper.ostream.println("");
        }
        final int[] effectiveCpuSetCpus = metrics.getEffectiveCpuSetCpus();
        LauncherHelper.ostream.println("    List of Effective Processors, " + effectiveCpuSetCpus.length + " total: ");
        LauncherHelper.ostream.print("    ");
        for (int j = 0; j < effectiveCpuSetCpus.length; ++j) {
            LauncherHelper.ostream.print(effectiveCpuSetCpus[j] + " ");
        }
        if (effectiveCpuSetCpus.length > 0) {
            LauncherHelper.ostream.println("");
        }
        final int[] cpuSetMems = metrics.getCpuSetMems();
        LauncherHelper.ostream.println("    List of Memory Nodes, " + cpuSetMems.length + " total: ");
        LauncherHelper.ostream.print("    ");
        for (int k = 0; k < cpuSetMems.length; ++k) {
            LauncherHelper.ostream.print(cpuSetMems[k] + " ");
        }
        if (cpuSetMems.length > 0) {
            LauncherHelper.ostream.println("");
        }
        final int[] effectiveCpuSetMems = metrics.getEffectiveCpuSetMems();
        LauncherHelper.ostream.println("    List of Available Memory Nodes, " + effectiveCpuSetMems.length + " total: ");
        LauncherHelper.ostream.print("    ");
        for (int l = 0; l < effectiveCpuSetMems.length; ++l) {
            LauncherHelper.ostream.print(effectiveCpuSetMems[l] + " ");
        }
        if (effectiveCpuSetMems.length > 0) {
            LauncherHelper.ostream.println("");
        }
        LauncherHelper.ostream.println("    CPUSet Memory Pressure Enabled: " + metrics.isCpuSetMemoryPressureEnabled());
        final long memoryLimit = metrics.getMemoryLimit();
        LauncherHelper.ostream.println("    Memory Limit: " + ((memoryLimit >= 0L) ? SizePrefix.scaleValue(memoryLimit) : "Unlimited"));
        final long memorySoftLimit = metrics.getMemorySoftLimit();
        LauncherHelper.ostream.println("    Memory Soft Limit: " + ((memorySoftLimit >= 0L) ? SizePrefix.scaleValue(memorySoftLimit) : "Unlimited"));
        final long memoryAndSwapLimit = metrics.getMemoryAndSwapLimit();
        LauncherHelper.ostream.println("    Memory & Swap Limit: " + ((memoryAndSwapLimit >= 0L) ? SizePrefix.scaleValue(memoryAndSwapLimit) : "Unlimited"));
        final long kernelMemoryLimit = metrics.getKernelMemoryLimit();
        LauncherHelper.ostream.println("    Kernel Memory Limit: " + ((kernelMemoryLimit >= 0L) ? SizePrefix.scaleValue(kernelMemoryLimit) : "Unlimited"));
        final long tcpMemoryLimit = metrics.getTcpMemoryLimit();
        LauncherHelper.ostream.println("    TCP Memory Limit: " + ((tcpMemoryLimit >= 0L) ? SizePrefix.scaleValue(tcpMemoryLimit) : "Unlimited"));
        LauncherHelper.ostream.println("    Out Of Memory Killer Enabled: " + metrics.isMemoryOOMKillEnabled());
        LauncherHelper.ostream.println("");
    }
    
    private static String getLocalizedMessage(final String s, final Object... array) {
        final String string = ResourceBundleHolder.RB.getString(s);
        return (array != null) ? MessageFormat.format(string, array) : string;
    }
    
    static void initHelpMessage(final String s) {
        LauncherHelper.outBuf = LauncherHelper.outBuf.append(getLocalizedMessage("java.launcher.opt.header", (s == null) ? "java" : s));
        LauncherHelper.outBuf = LauncherHelper.outBuf.append(getLocalizedMessage("java.launcher.opt.datamodel", 32));
        LauncherHelper.outBuf = LauncherHelper.outBuf.append(getLocalizedMessage("java.launcher.opt.datamodel", 64));
    }
    
    static void appendVmSelectMessage(final String s, final String s2) {
        LauncherHelper.outBuf = LauncherHelper.outBuf.append(getLocalizedMessage("java.launcher.opt.vmselect", s, s2));
    }
    
    static void appendVmSynonymMessage(final String s, final String s2) {
        LauncherHelper.outBuf = LauncherHelper.outBuf.append(getLocalizedMessage("java.launcher.opt.hotspot", s, s2));
    }
    
    static void appendVmErgoMessage(final boolean b, final String s) {
        LauncherHelper.outBuf = LauncherHelper.outBuf.append(getLocalizedMessage("java.launcher.ergo.message1", s));
        LauncherHelper.outBuf = (b ? LauncherHelper.outBuf.append(",\n" + getLocalizedMessage("java.launcher.ergo.message2", new Object[0]) + "\n\n") : LauncherHelper.outBuf.append(".\n\n"));
    }
    
    static void printHelpMessage(final boolean b) {
        initOutput(b);
        LauncherHelper.outBuf = LauncherHelper.outBuf.append(getLocalizedMessage("java.launcher.opt.footer", File.pathSeparator));
        LauncherHelper.ostream.println(LauncherHelper.outBuf.toString());
    }
    
    static void printXUsageMessage(final boolean b) {
        initOutput(b);
        LauncherHelper.ostream.println(getLocalizedMessage("java.launcher.X.usage", File.pathSeparator));
        if (System.getProperty("os.name").contains("OS X")) {
            LauncherHelper.ostream.println(getLocalizedMessage("java.launcher.X.macosx.usage", File.pathSeparator));
        }
    }
    
    static void initOutput(final boolean b) {
        LauncherHelper.ostream = (b ? System.err : System.out);
    }
    
    static String getMainClassFromJar(final String s) {
        try (final JarFile jarFile = new JarFile(s)) {
            final Manifest manifest = jarFile.getManifest();
            if (manifest == null) {
                abort(null, "java.launcher.jar.error2", s);
            }
            final Attributes mainAttributes = manifest.getMainAttributes();
            if (mainAttributes == null) {
                abort(null, "java.launcher.jar.error3", s);
            }
            final String value = mainAttributes.getValue("Main-Class");
            if (value == null) {
                abort(null, "java.launcher.jar.error3", s);
            }
            if (mainAttributes.containsKey(new Attributes.Name("JavaFX-Application-Class"))) {
                return FXHelper.class.getName();
            }
            return value.trim();
        }
        catch (final IOException ex) {
            abort(ex, "java.launcher.jar.error1", s);
            return null;
        }
    }
    
    static void abort(final Throwable t, final String s, final Object... array) {
        if (s != null) {
            LauncherHelper.ostream.println(getLocalizedMessage(s, array));
        }
        if (LauncherHelper.trace) {
            if (t != null) {
                t.printStackTrace();
            }
            else {
                Thread.dumpStack();
            }
        }
        System.exit(1);
    }
    
    public static Class<?> checkAndLoadMain(final boolean b, final int n, final String s) {
        initOutput(b);
        String mainClassFromJar = null;
        switch (n) {
            case 1: {
                mainClassFromJar = s;
                break;
            }
            case 2: {
                mainClassFromJar = getMainClassFromJar(s);
                break;
            }
            default: {
                throw new InternalError("" + n + ": Unknown launch mode");
            }
        }
        final String replace = mainClassFromJar.replace('/', '.');
        Class<?> appClass = null;
        try {
            appClass = LauncherHelper.scloader.loadClass(replace);
        }
        catch (final NoClassDefFoundError | ClassNotFoundException ex) {
            if (System.getProperty("os.name", "").contains("OS X") && Normalizer.isNormalized(replace, Normalizer.Form.NFD)) {
                try {
                    appClass = LauncherHelper.scloader.loadClass(Normalizer.normalize(replace, Normalizer.Form.NFC));
                }
                catch (final NoClassDefFoundError | ClassNotFoundException ex2) {
                    abort((Throwable)ex, "java.launcher.cls.error1", replace);
                }
            }
            else {
                abort((Throwable)ex, "java.launcher.cls.error1", replace);
            }
        }
        LauncherHelper.appClass = appClass;
        if (appClass.equals(FXHelper.class) || doesExtendFXApplication(appClass)) {
            setFXLaunchParameters(s, n);
            return FXHelper.class;
        }
        validateMainClass(appClass);
        return appClass;
    }
    
    public static Class<?> getApplicationClass() {
        return LauncherHelper.appClass;
    }
    
    static void validateMainClass(final Class<?> clazz) {
        Method method;
        try {
            method = clazz.getMethod("main", String[].class);
        }
        catch (final NoSuchMethodException ex) {
            abort(null, "java.launcher.cls.error4", clazz.getName(), "javafx.application.Application");
            return;
        }
        if (!Modifier.isStatic(method.getModifiers())) {
            abort(null, "java.launcher.cls.error2", "static", method.getDeclaringClass().getName());
        }
        if (method.getReturnType() != Void.TYPE) {
            abort(null, "java.launcher.cls.error3", method.getDeclaringClass().getName());
        }
    }
    
    static String makePlatformString(final boolean b, final byte[] array) {
        initOutput(b);
        if (LauncherHelper.encoding == null) {
            LauncherHelper.encoding = System.getProperty("sun.jnu.encoding");
            LauncherHelper.isCharsetSupported = Charset.isSupported(LauncherHelper.encoding);
        }
        try {
            return LauncherHelper.isCharsetSupported ? new String(array, LauncherHelper.encoding) : new String(array);
        }
        catch (final UnsupportedEncodingException ex) {
            abort(ex, null, new Object[0]);
            return null;
        }
    }
    
    static String[] expandArgs(final String[] array) {
        final ArrayList list = new ArrayList();
        for (int length = array.length, i = 0; i < length; ++i) {
            list.add(new StdArg(array[i]));
        }
        return expandArgs(list);
    }
    
    static String[] expandArgs(final List<StdArg> list) {
        final ArrayList list2 = new ArrayList();
        if (LauncherHelper.trace) {
            System.err.println("Incoming arguments:");
        }
        for (final StdArg stdArg : list) {
            if (LauncherHelper.trace) {
                System.err.println(stdArg);
            }
            if (stdArg.needsExpansion) {
                final File file = new File(stdArg.arg);
                File parentFile = file.getParentFile();
                final String name = file.getName();
                if (parentFile == null) {
                    parentFile = new File(".");
                }
                try (final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(parentFile.toPath(), name)) {
                    int n = 0;
                    final Iterator<Path> iterator2 = directoryStream.iterator();
                    while (iterator2.hasNext()) {
                        list2.add(iterator2.next().normalize().toString());
                        ++n;
                    }
                    if (n == 0) {
                        list2.add(stdArg.arg);
                    }
                }
                catch (final Exception ex) {
                    list2.add(stdArg.arg);
                    if (!LauncherHelper.trace) {
                        continue;
                    }
                    System.err.println("Warning: passing argument as-is " + stdArg);
                    System.err.print(ex);
                }
            }
            else {
                list2.add(stdArg.arg);
            }
        }
        final String[] array = new String[list2.size()];
        list2.toArray(array);
        if (LauncherHelper.trace) {
            System.err.println("Expanded arguments:");
            final String[] array2 = array;
            for (int length = array2.length, i = 0; i < length; ++i) {
                System.err.println(array2[i]);
            }
        }
        return array;
    }
    
    static {
        LauncherHelper.outBuf = new StringBuilder();
        trace = (VM.getSavedProperty("sun.java.launcher.diag") != null);
        scloader = ClassLoader.getSystemClassLoader();
        LauncherHelper.encoding = null;
        LauncherHelper.isCharsetSupported = false;
    }
    
    private static class ResourceBundleHolder
    {
        private static final ResourceBundle RB;
        
        static {
            RB = ResourceBundle.getBundle("sun.launcher.resources.launcher");
        }
    }
    
    private enum SizePrefix
    {
        KILO(1024L, "K"), 
        MEGA(1048576L, "M"), 
        GIGA(1073741824L, "G"), 
        TERA(1099511627776L, "T");
        
        long size;
        String abbrev;
        
        private SizePrefix(final long size, final String abbrev) {
            this.size = size;
            this.abbrev = abbrev;
        }
        
        private static String scale(final long n, final SizePrefix sizePrefix) {
            return BigDecimal.valueOf(n).divide(BigDecimal.valueOf(sizePrefix.size), 2, RoundingMode.HALF_EVEN).toPlainString() + sizePrefix.abbrev;
        }
        
        static String scaleValue(final long n) {
            if (n < SizePrefix.MEGA.size) {
                return scale(n, SizePrefix.KILO);
            }
            if (n < SizePrefix.GIGA.size) {
                return scale(n, SizePrefix.MEGA);
            }
            if (n < SizePrefix.TERA.size) {
                return scale(n, SizePrefix.GIGA);
            }
            return scale(n, SizePrefix.TERA);
        }
    }
    
    private static class StdArg
    {
        final String arg;
        final boolean needsExpansion;
        
        StdArg(final String arg, final boolean needsExpansion) {
            this.arg = arg;
            this.needsExpansion = needsExpansion;
        }
        
        StdArg(final String s) {
            this.arg = s.substring(1);
            this.needsExpansion = (s.charAt(0) == 'T');
        }
        
        @Override
        public String toString() {
            return "StdArg{arg=" + this.arg + ", needsExpansion=" + this.needsExpansion + '}';
        }
    }
    
    static final class FXHelper
    {
        private static final String JAVAFX_APPLICATION_MARKER = "JavaFX-Application-Class";
        private static final String JAVAFX_APPLICATION_CLASS_NAME = "javafx.application.Application";
        private static final String JAVAFX_LAUNCHER_CLASS_NAME = "com.sun.javafx.application.LauncherImpl";
        private static final String JAVAFX_LAUNCH_MODE_CLASS = "LM_CLASS";
        private static final String JAVAFX_LAUNCH_MODE_JAR = "LM_JAR";
        private static String fxLaunchName;
        private static String fxLaunchMode;
        private static Class<?> fxLauncherClass;
        private static Method fxLauncherMethod;
        
        private static void setFXLaunchParameters(final String fxLaunchName, final int n) {
            try {
                FXHelper.fxLauncherClass = LauncherHelper.scloader.loadClass("com.sun.javafx.application.LauncherImpl");
                FXHelper.fxLauncherMethod = FXHelper.fxLauncherClass.getMethod("launchApplication", String.class, String.class, String[].class);
                if (!Modifier.isStatic(FXHelper.fxLauncherMethod.getModifiers())) {
                    LauncherHelper.abort(null, "java.launcher.javafx.error1", new Object[0]);
                }
                if (FXHelper.fxLauncherMethod.getReturnType() != Void.TYPE) {
                    LauncherHelper.abort(null, "java.launcher.javafx.error1", new Object[0]);
                }
            }
            catch (final ClassNotFoundException | NoSuchMethodException ex) {
                LauncherHelper.abort((Throwable)ex, "java.launcher.cls.error5", ex);
            }
            FXHelper.fxLaunchName = fxLaunchName;
            switch (n) {
                case 1: {
                    FXHelper.fxLaunchMode = "LM_CLASS";
                    break;
                }
                case 2: {
                    FXHelper.fxLaunchMode = "LM_JAR";
                    break;
                }
                default: {
                    throw new InternalError(n + ": Unknown launch mode");
                }
            }
        }
        
        private static boolean doesExtendFXApplication(final Class<?> clazz) {
            for (Class<?> clazz2 = clazz.getSuperclass(); clazz2 != null; clazz2 = clazz2.getSuperclass()) {
                if (clazz2.getName().equals("javafx.application.Application")) {
                    return true;
                }
            }
            return false;
        }
        
        public static void main(final String... array) throws Exception {
            if (FXHelper.fxLauncherMethod == null || FXHelper.fxLaunchMode == null || FXHelper.fxLaunchName == null) {
                throw new RuntimeException("Invalid JavaFX launch parameters");
            }
            FXHelper.fxLauncherMethod.invoke(null, FXHelper.fxLaunchName, FXHelper.fxLaunchMode, array);
        }
        
        static {
            FXHelper.fxLaunchName = null;
            FXHelper.fxLaunchMode = null;
            FXHelper.fxLauncherClass = null;
            FXHelper.fxLauncherMethod = null;
        }
    }
}
