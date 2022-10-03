package org.apache.commons.text.lookup;

import org.apache.commons.lang3.StringUtils;
import java.util.Locale;

final class JavaPlatformStringLookup extends AbstractStringLookup
{
    private static final String KEY_LOCALE = "locale";
    private static final String KEY_HARDWARE = "hardware";
    private static final String KEY_OS = "os";
    private static final String KEY_VM = "vm";
    private static final String KEY_RUNTIME = "runtime";
    private static final String KEY_VERSION = "version";
    static final JavaPlatformStringLookup INSTANCE;
    
    public static void main(final String[] args) {
        System.out.println(JavaPlatformStringLookup.class);
        System.out.printf("%s = %s%n", "version", JavaPlatformStringLookup.INSTANCE.lookup("version"));
        System.out.printf("%s = %s%n", "runtime", JavaPlatformStringLookup.INSTANCE.lookup("runtime"));
        System.out.printf("%s = %s%n", "vm", JavaPlatformStringLookup.INSTANCE.lookup("vm"));
        System.out.printf("%s = %s%n", "os", JavaPlatformStringLookup.INSTANCE.lookup("os"));
        System.out.printf("%s = %s%n", "hardware", JavaPlatformStringLookup.INSTANCE.lookup("hardware"));
        System.out.printf("%s = %s%n", "locale", JavaPlatformStringLookup.INSTANCE.lookup("locale"));
    }
    
    private JavaPlatformStringLookup() {
    }
    
    String getHardware() {
        return "processors: " + Runtime.getRuntime().availableProcessors() + ", architecture: " + this.getSystemProperty("os.arch") + this.getSystemProperty("-", "sun.arch.data.model") + this.getSystemProperty(", instruction sets: ", "sun.cpu.isalist");
    }
    
    String getLocale() {
        return "default locale: " + Locale.getDefault() + ", platform encoding: " + this.getSystemProperty("file.encoding");
    }
    
    String getOperatingSystem() {
        return this.getSystemProperty("os.name") + " " + this.getSystemProperty("os.version") + this.getSystemProperty(" ", "sun.os.patch.level") + ", architecture: " + this.getSystemProperty("os.arch") + this.getSystemProperty("-", "sun.arch.data.model");
    }
    
    String getRuntime() {
        return this.getSystemProperty("java.runtime.name") + " (build " + this.getSystemProperty("java.runtime.version") + ") from " + this.getSystemProperty("java.vendor");
    }
    
    private String getSystemProperty(final String name) {
        return SystemPropertyStringLookup.INSTANCE.lookup(name);
    }
    
    private String getSystemProperty(final String prefix, final String name) {
        final String value = this.getSystemProperty(name);
        if (StringUtils.isEmpty((CharSequence)value)) {
            return "";
        }
        return prefix + value;
    }
    
    String getVirtualMachine() {
        return this.getSystemProperty("java.vm.name") + " (build " + this.getSystemProperty("java.vm.version") + ", " + this.getSystemProperty("java.vm.info") + ")";
    }
    
    @Override
    public String lookup(final String key) {
        if (key == null) {
            return null;
        }
        switch (key) {
            case "version": {
                return "Java version " + this.getSystemProperty("java.version");
            }
            case "runtime": {
                return this.getRuntime();
            }
            case "vm": {
                return this.getVirtualMachine();
            }
            case "os": {
                return this.getOperatingSystem();
            }
            case "hardware": {
                return this.getHardware();
            }
            case "locale": {
                return this.getLocale();
            }
            default: {
                throw new IllegalArgumentException(key);
            }
        }
    }
    
    static {
        INSTANCE = new JavaPlatformStringLookup();
    }
}
