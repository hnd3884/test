package sun.misc;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.List;
import java.security.AccessController;
import java.security.Security;
import jdk.internal.util.StaticProperty;
import java.security.Permission;
import java.io.SerializablePermission;
import java.util.Objects;
import java.io.ObjectInputStream;
import sun.util.logging.PlatformLogger;

@FunctionalInterface
public interface ObjectInputFilter
{
    Status checkInput(final FilterInfo p0);
    
    public enum Status
    {
        UNDECIDED, 
        ALLOWED, 
        REJECTED;
    }
    
    public static final class Config
    {
        private static final Object serialFilterLock;
        private static final PlatformLogger configLog;
        private static final String SERIAL_FILTER_PROPNAME = "jdk.serialFilter";
        private static final ObjectInputFilter configuredFilter;
        private static ObjectInputFilter serialFilter;
        
        private Config() {
        }
        
        static void filterLog(final PlatformLogger.Level level, final String s, final Object... array) {
            if (Config.configLog != null) {
                if (PlatformLogger.Level.INFO.equals(level)) {
                    Config.configLog.info(s, array);
                }
                else if (PlatformLogger.Level.WARNING.equals(level)) {
                    Config.configLog.warning(s, array);
                }
                else {
                    Config.configLog.severe(s, array);
                }
            }
        }
        
        public static ObjectInputFilter getObjectInputFilter(final ObjectInputStream objectInputStream) {
            Objects.requireNonNull(objectInputStream, "inputStream");
            return SharedSecrets.getJavaOISAccess().getObjectInputFilter(objectInputStream);
        }
        
        public static void setObjectInputFilter(final ObjectInputStream objectInputStream, final ObjectInputFilter objectInputFilter) {
            Objects.requireNonNull(objectInputStream, "inputStream");
            SharedSecrets.getJavaOISAccess().setObjectInputFilter(objectInputStream, objectInputFilter);
        }
        
        public static ObjectInputFilter getSerialFilter() {
            synchronized (Config.serialFilterLock) {
                return Config.serialFilter;
            }
        }
        
        public static void setSerialFilter(final ObjectInputFilter serialFilter) {
            Objects.requireNonNull(serialFilter, "filter");
            final SecurityManager securityManager = System.getSecurityManager();
            if (securityManager != null) {
                securityManager.checkPermission(new SerializablePermission("serialFilter"));
            }
            synchronized (Config.serialFilterLock) {
                if (Config.serialFilter != null) {
                    throw new IllegalStateException("Serial filter can only be set once");
                }
                Config.serialFilter = serialFilter;
            }
        }
        
        public static ObjectInputFilter createFilter(final String s) {
            Objects.requireNonNull(s, "pattern");
            return Global.createFilter(s, true);
        }
        
        public static ObjectInputFilter createFilter2(final String s) {
            Objects.requireNonNull(s, "pattern");
            return Global.createFilter(s, false);
        }
        
        static {
            serialFilterLock = new Object();
            configuredFilter = AccessController.doPrivileged(() -> {
                StaticProperty.jdkSerialFilter();
                String property = null;
                if (property == null) {
                    property = Security.getProperty("jdk.serialFilter");
                }
                if (property != null) {
                    PlatformLogger.getLogger("java.io.serialization");
                    final PlatformLogger platformLogger;
                    platformLogger.info("Creating serialization filter from {0}", property);
                    try {
                        return createFilter(property);
                    }
                    catch (final RuntimeException ex) {
                        platformLogger.warning("Error configuring filter: {0}", ex);
                    }
                }
                return null;
            });
            configLog = ((Config.configuredFilter != null) ? PlatformLogger.getLogger("java.io.serialization") : null);
            Config.serialFilter = Config.configuredFilter;
        }
        
        static final class Global implements ObjectInputFilter
        {
            private final String pattern;
            private final List<Function<Class<?>, Status>> filters;
            private long maxStreamBytes;
            private long maxDepth;
            private long maxReferences;
            private long maxArrayLength;
            private final boolean checkComponentType;
            
            static ObjectInputFilter createFilter(final String s, final boolean b) {
                final Global global = new Global(s, b);
                return global.isEmpty() ? null : global;
            }
            
            private Global(final String pattern, final boolean checkComponentType) {
                this.pattern = pattern;
                this.checkComponentType = checkComponentType;
                this.maxArrayLength = Long.MAX_VALUE;
                this.maxDepth = Long.MAX_VALUE;
                this.maxReferences = Long.MAX_VALUE;
                this.maxStreamBytes = Long.MAX_VALUE;
                final String[] split = pattern.split(";");
                this.filters = new ArrayList<Function<Class<?>, Status>>(split.length);
                for (int i = 0; i < split.length; ++i) {
                    final String s = split[i];
                    final int length = s.length();
                    if (length != 0) {
                        if (!this.parseLimit(s)) {
                            final int n = (s.charAt(0) == '!') ? 1 : 0;
                            if (s.indexOf(47) >= 0) {
                                throw new IllegalArgumentException("invalid character \"/\" in: \"" + pattern + "\"");
                            }
                            if (s.endsWith("*")) {
                                if (s.endsWith(".*")) {
                                    if (s.substring(n, length - 1).length() < 2) {
                                        throw new IllegalArgumentException("package missing in: \"" + pattern + "\"");
                                    }
                                    if (n != 0) {
                                        this.filters.add(clazz2 -> matchesPackage(clazz2, s2) ? Status.REJECTED : Status.UNDECIDED);
                                    }
                                    else {
                                        this.filters.add(clazz4 -> matchesPackage(clazz4, s3) ? Status.ALLOWED : Status.UNDECIDED);
                                    }
                                }
                                else if (s.endsWith(".**")) {
                                    if (s.substring(n, length - 2).length() < 2) {
                                        throw new IllegalArgumentException("package missing in: \"" + pattern + "\"");
                                    }
                                    if (n != 0) {
                                        this.filters.add(clazz6 -> clazz6.getName().startsWith(s4) ? Status.REJECTED : Status.UNDECIDED);
                                    }
                                    else {
                                        this.filters.add(clazz8 -> clazz8.getName().startsWith(s5) ? Status.ALLOWED : Status.UNDECIDED);
                                    }
                                }
                                else {
                                    s.substring(n, length - 1);
                                    if (n != 0) {
                                        this.filters.add(clazz10 -> clazz10.getName().startsWith(s6) ? Status.REJECTED : Status.UNDECIDED);
                                    }
                                    else {
                                        this.filters.add(clazz12 -> clazz12.getName().startsWith(s7) ? Status.ALLOWED : Status.UNDECIDED);
                                    }
                                }
                            }
                            else {
                                if (s.substring(n).isEmpty()) {
                                    throw new IllegalArgumentException("class or package missing in: \"" + pattern + "\"");
                                }
                                if (n != 0) {
                                    this.filters.add(clazz14 -> clazz14.getName().equals(s8) ? Status.REJECTED : Status.UNDECIDED);
                                }
                                else {
                                    this.filters.add(clazz16 -> clazz16.getName().equals(s9) ? Status.ALLOWED : Status.UNDECIDED);
                                }
                            }
                        }
                    }
                }
            }
            
            private boolean isEmpty() {
                return this.filters.isEmpty() && this.maxArrayLength == Long.MAX_VALUE && this.maxDepth == Long.MAX_VALUE && this.maxReferences == Long.MAX_VALUE && this.maxStreamBytes == Long.MAX_VALUE;
            }
            
            private boolean parseLimit(final String s) {
                final int index = s.indexOf(61);
                if (index < 0) {
                    return false;
                }
                final String substring = s.substring(index + 1);
                if (s.startsWith("maxdepth=")) {
                    this.maxDepth = parseValue(substring);
                }
                else if (s.startsWith("maxarray=")) {
                    this.maxArrayLength = parseValue(substring);
                }
                else if (s.startsWith("maxrefs=")) {
                    this.maxReferences = parseValue(substring);
                }
                else {
                    if (!s.startsWith("maxbytes=")) {
                        throw new IllegalArgumentException("unknown limit: " + s.substring(0, index));
                    }
                    this.maxStreamBytes = parseValue(substring);
                }
                return true;
            }
            
            private static long parseValue(final String s) throws IllegalArgumentException {
                final long long1 = Long.parseLong(s);
                if (long1 < 0L) {
                    throw new IllegalArgumentException("negative limit: " + s);
                }
                return long1;
            }
            
            @Override
            public Status checkInput(final FilterInfo filterInfo) {
                if (filterInfo.references() < 0L || filterInfo.depth() < 0L || filterInfo.streamBytes() < 0L || filterInfo.references() > this.maxReferences || filterInfo.depth() > this.maxDepth || filterInfo.streamBytes() > this.maxStreamBytes) {
                    return Status.REJECTED;
                }
                Class<?> clazz = filterInfo.serialClass();
                if (clazz == null) {
                    return Status.UNDECIDED;
                }
                if (clazz.isArray()) {
                    if (filterInfo.arrayLength() >= 0L && filterInfo.arrayLength() > this.maxArrayLength) {
                        return Status.REJECTED;
                    }
                    if (!this.checkComponentType) {
                        return Status.UNDECIDED;
                    }
                    do {
                        clazz = clazz.getComponentType();
                    } while (clazz.isArray());
                }
                if (clazz.isPrimitive()) {
                    return Status.UNDECIDED;
                }
                return this.filters.stream().map(function -> function.apply(clazz2)).filter(status -> status != Status.UNDECIDED).findFirst().orElse(Status.UNDECIDED);
            }
            
            private static boolean matchesPackage(final Class<?> clazz, final String s) {
                final String name = clazz.getName();
                return name.startsWith(s) && name.lastIndexOf(46) == s.length() - 1;
            }
            
            @Override
            public String toString() {
                return this.pattern;
            }
        }
    }
    
    public interface FilterInfo
    {
        Class<?> serialClass();
        
        long arrayLength();
        
        long depth();
        
        long references();
        
        long streamBytes();
    }
}
