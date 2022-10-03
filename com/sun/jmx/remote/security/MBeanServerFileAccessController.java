package com.sun.jmx.remote.security;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.StringTokenizer;
import java.util.Iterator;
import java.util.Set;
import java.security.Principal;
import java.security.AccessControlContext;
import java.security.PrivilegedAction;
import java.security.AccessController;
import javax.security.auth.Subject;
import java.io.InputStream;
import java.io.FileInputStream;
import javax.management.ObjectName;
import javax.management.MBeanServer;
import java.io.IOException;
import java.util.Properties;
import java.util.Map;

public class MBeanServerFileAccessController extends MBeanServerAccessController
{
    static final String READONLY = "readonly";
    static final String READWRITE = "readwrite";
    static final String CREATE = "create";
    static final String UNREGISTER = "unregister";
    private Map<String, Access> accessMap;
    private Properties originalProps;
    private String accessFileName;
    
    public MBeanServerFileAccessController(final String accessFileName) throws IOException {
        this.accessFileName = accessFileName;
        this.parseProperties(propertiesFromFile(accessFileName));
    }
    
    public MBeanServerFileAccessController(final String s, final MBeanServer mBeanServer) throws IOException {
        this(s);
        this.setMBeanServer(mBeanServer);
    }
    
    public MBeanServerFileAccessController(final Properties originalProps) throws IOException {
        if (originalProps == null) {
            throw new IllegalArgumentException("Null properties");
        }
        this.parseProperties(this.originalProps = originalProps);
    }
    
    public MBeanServerFileAccessController(final Properties properties, final MBeanServer mBeanServer) throws IOException {
        this(properties);
        this.setMBeanServer(mBeanServer);
    }
    
    public void checkRead() {
        this.checkAccess(AccessType.READ, null);
    }
    
    public void checkWrite() {
        this.checkAccess(AccessType.WRITE, null);
    }
    
    public void checkCreate(final String s) {
        this.checkAccess(AccessType.CREATE, s);
    }
    
    public void checkUnregister(final ObjectName objectName) {
        this.checkAccess(AccessType.UNREGISTER, null);
    }
    
    public synchronized void refresh() throws IOException {
        Properties properties;
        if (this.accessFileName == null) {
            properties = this.originalProps;
        }
        else {
            properties = propertiesFromFile(this.accessFileName);
        }
        this.parseProperties(properties);
    }
    
    private static Properties propertiesFromFile(final String s) throws IOException {
        final FileInputStream fileInputStream = new FileInputStream(s);
        try {
            final Properties properties = new Properties();
            properties.load(fileInputStream);
            return properties;
        }
        finally {
            fileInputStream.close();
        }
    }
    
    private synchronized void checkAccess(final AccessType accessType, final String s) {
        final Subject subject = AccessController.doPrivileged((PrivilegedAction<Subject>)new PrivilegedAction<Subject>() {
            final /* synthetic */ AccessControlContext val$acc = AccessController.getContext();
            
            @Override
            public Subject run() {
                return Subject.getSubject(this.val$acc);
            }
        });
        if (subject == null) {
            return;
        }
        final Set<Principal> principals = subject.getPrincipals();
        String string = null;
        final Iterator<Principal> iterator = principals.iterator();
        while (iterator.hasNext()) {
            final Access access = this.accessMap.get(iterator.next().getName());
            if (access != null) {
                boolean b = false;
                switch (accessType) {
                    case READ: {
                        b = true;
                        break;
                    }
                    case WRITE: {
                        b = access.write;
                        break;
                    }
                    case UNREGISTER: {
                        b = access.unregister;
                        if (!b && access.write) {
                            string = "unregister";
                            break;
                        }
                        break;
                    }
                    case CREATE: {
                        b = checkCreateAccess(access, s);
                        if (!b && access.write) {
                            string = "create " + s;
                            break;
                        }
                        break;
                    }
                    default: {
                        throw new AssertionError();
                    }
                }
                if (b) {
                    return;
                }
                continue;
            }
        }
        final SecurityException ex = new SecurityException("Access denied! Invalid access level for requested MBeanServer operation.");
        if (string != null) {
            ex.initCause(new SecurityException("Access property for this identity should be similar to: readwrite " + string));
        }
        throw ex;
    }
    
    private static boolean checkCreateAccess(final Access access, final String s) {
        final String[] createPatterns = access.createPatterns;
        for (int length = createPatterns.length, i = 0; i < length; ++i) {
            if (classNameMatch(createPatterns[i], s)) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean classNameMatch(final String s, final String s2) {
        final StringBuilder sb = new StringBuilder();
        final StringTokenizer stringTokenizer = new StringTokenizer(s, "*", true);
        while (stringTokenizer.hasMoreTokens()) {
            final String nextToken = stringTokenizer.nextToken();
            if (nextToken.equals("*")) {
                sb.append("[^.]*");
            }
            else {
                sb.append(Pattern.quote(nextToken));
            }
        }
        return s2.matches(sb.toString());
    }
    
    private void parseProperties(final Properties properties) {
        this.accessMap = new HashMap<String, Access>();
        for (final Map.Entry entry : properties.entrySet()) {
            final String s = (String)entry.getKey();
            this.accessMap.put(s, Parser.parseAccess(s, (String)entry.getValue()));
        }
    }
    
    private enum AccessType
    {
        READ, 
        WRITE, 
        CREATE, 
        UNREGISTER;
    }
    
    private static class Access
    {
        final boolean write;
        final String[] createPatterns;
        private boolean unregister;
        private final String[] NO_STRINGS;
        
        Access(final boolean write, final boolean unregister, final List<String> list) {
            this.NO_STRINGS = new String[0];
            this.write = write;
            final int n = (list == null) ? 0 : list.size();
            if (n == 0) {
                this.createPatterns = this.NO_STRINGS;
            }
            else {
                this.createPatterns = list.toArray(new String[n]);
            }
            this.unregister = unregister;
        }
    }
    
    private static class Parser
    {
        private static final int EOS = -1;
        private final String identity;
        private final String s;
        private final int len;
        private int i;
        private int c;
        
        private Parser(final String identity, final String s) {
            this.identity = identity;
            this.s = s;
            this.len = s.length();
            this.i = 0;
            if (this.i < this.len) {
                this.c = s.codePointAt(this.i);
            }
            else {
                this.c = -1;
            }
        }
        
        static Access parseAccess(final String s, final String s2) {
            return new Parser(s, s2).parseAccess();
        }
        
        private Access parseAccess() {
            this.skipSpace();
            final String word = this.parseWord();
            Access readWrite;
            if (word.equals("readonly")) {
                readWrite = new Access(false, false, null);
            }
            else {
                if (!word.equals("readwrite")) {
                    throw this.syntax("Expected readonly or readwrite: " + word);
                }
                readWrite = this.parseReadWrite();
            }
            if (this.c != -1) {
                throw this.syntax("Extra text at end of line");
            }
            return readWrite;
        }
        
        private Access parseReadWrite() {
            final ArrayList list = new ArrayList();
            boolean b = false;
            while (true) {
                this.skipSpace();
                if (this.c == -1) {
                    return new Access(true, b, list);
                }
                final String word = this.parseWord();
                if (word.equals("unregister")) {
                    b = true;
                }
                else {
                    if (!word.equals("create")) {
                        throw this.syntax("Unrecognized keyword " + word);
                    }
                    this.parseCreate(list);
                }
            }
        }
        
        private void parseCreate(final List<String> list) {
            while (true) {
                this.skipSpace();
                list.add(this.parseClassName());
                this.skipSpace();
                if (this.c != 44) {
                    break;
                }
                this.next();
            }
        }
        
        private String parseClassName() {
            final int i = this.i;
            int n = 0;
            while (true) {
                if (this.c == 46) {
                    if (n == 0) {
                        throw this.syntax("Bad . in class name");
                    }
                    n = 0;
                }
                else if (this.c == 42 || Character.isJavaIdentifierPart(this.c)) {
                    n = 1;
                }
                else {
                    final String substring = this.s.substring(i, this.i);
                    if (n == 0) {
                        throw this.syntax("Bad class name " + substring);
                    }
                    return substring;
                }
                this.next();
            }
        }
        
        private void next() {
            if (this.c != -1) {
                this.i += Character.charCount(this.c);
                if (this.i < this.len) {
                    this.c = this.s.codePointAt(this.i);
                }
                else {
                    this.c = -1;
                }
            }
        }
        
        private void skipSpace() {
            while (Character.isWhitespace(this.c)) {
                this.next();
            }
        }
        
        private String parseWord() {
            this.skipSpace();
            if (this.c == -1) {
                throw this.syntax("Expected word at end of line");
            }
            final int i = this.i;
            while (this.c != -1 && !Character.isWhitespace(this.c)) {
                this.next();
            }
            final String substring = this.s.substring(i, this.i);
            this.skipSpace();
            return substring;
        }
        
        private IllegalArgumentException syntax(final String s) {
            return new IllegalArgumentException(s + " [" + this.identity + " " + this.s + "]");
        }
        
        static {
            assert !Character.isWhitespace(-1);
        }
    }
}
