package javax.management;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.Permission;

public class MBeanPermission extends Permission
{
    private static final long serialVersionUID = -2416928705275160661L;
    private static final int AddNotificationListener = 1;
    private static final int GetAttribute = 2;
    private static final int GetClassLoader = 4;
    private static final int GetClassLoaderFor = 8;
    private static final int GetClassLoaderRepository = 16;
    private static final int GetDomains = 32;
    private static final int GetMBeanInfo = 64;
    private static final int GetObjectInstance = 128;
    private static final int Instantiate = 256;
    private static final int Invoke = 512;
    private static final int IsInstanceOf = 1024;
    private static final int QueryMBeans = 2048;
    private static final int QueryNames = 4096;
    private static final int RegisterMBean = 8192;
    private static final int RemoveNotificationListener = 16384;
    private static final int SetAttribute = 32768;
    private static final int UnregisterMBean = 65536;
    private static final int NONE = 0;
    private static final int ALL = 131071;
    private String actions;
    private transient int mask;
    private transient String classNamePrefix;
    private transient boolean classNameExactMatch;
    private transient String member;
    private transient ObjectName objectName;
    
    private void parseActions() {
        if (this.actions == null) {
            throw new IllegalArgumentException("MBeanPermission: actions can't be null");
        }
        if (this.actions.equals("")) {
            throw new IllegalArgumentException("MBeanPermission: actions can't be empty");
        }
        final int mask = getMask(this.actions);
        if ((mask & 0x1FFFF) != mask) {
            throw new IllegalArgumentException("Invalid actions mask");
        }
        if (mask == 0) {
            throw new IllegalArgumentException("Invalid actions mask");
        }
        this.mask = mask;
    }
    
    private void parseName() {
        String className = this.getName();
        if (className == null) {
            throw new IllegalArgumentException("MBeanPermission name cannot be null");
        }
        if (className.equals("")) {
            throw new IllegalArgumentException("MBeanPermission name cannot be empty");
        }
        final int index = className.indexOf("[");
        if (index == -1) {
            this.objectName = ObjectName.WILDCARD;
        }
        else {
            if (!className.endsWith("]")) {
                throw new IllegalArgumentException("MBeanPermission: The ObjectName in the target name must be included in square brackets");
            }
            try {
                final String substring = className.substring(index + 1, className.length() - 1);
                if (substring.equals("")) {
                    this.objectName = ObjectName.WILDCARD;
                }
                else if (substring.equals("-")) {
                    this.objectName = null;
                }
                else {
                    this.objectName = new ObjectName(substring);
                }
            }
            catch (final MalformedObjectNameException ex) {
                throw new IllegalArgumentException("MBeanPermission: The target name does not specify a valid ObjectName", ex);
            }
            className = className.substring(0, index);
        }
        final int index2 = className.indexOf("#");
        if (index2 == -1) {
            this.setMember("*");
        }
        else {
            this.setMember(className.substring(index2 + 1));
            className = className.substring(0, index2);
        }
        this.setClassName(className);
    }
    
    private void initName(final String className, final String member, final ObjectName objectName) {
        this.setClassName(className);
        this.setMember(member);
        this.objectName = objectName;
    }
    
    private void setClassName(final String classNamePrefix) {
        if (classNamePrefix == null || classNamePrefix.equals("-")) {
            this.classNamePrefix = null;
            this.classNameExactMatch = false;
        }
        else if (classNamePrefix.equals("") || classNamePrefix.equals("*")) {
            this.classNamePrefix = "";
            this.classNameExactMatch = false;
        }
        else if (classNamePrefix.endsWith(".*")) {
            this.classNamePrefix = classNamePrefix.substring(0, classNamePrefix.length() - 1);
            this.classNameExactMatch = false;
        }
        else {
            this.classNamePrefix = classNamePrefix;
            this.classNameExactMatch = true;
        }
    }
    
    private void setMember(final String member) {
        if (member == null || member.equals("-")) {
            this.member = null;
        }
        else if (member.equals("")) {
            this.member = "*";
        }
        else {
            this.member = member;
        }
    }
    
    public MBeanPermission(final String s, final String actions) {
        super(s);
        this.parseName();
        this.actions = actions;
        this.parseActions();
    }
    
    public MBeanPermission(final String s, final String s2, final ObjectName objectName, final String actions) {
        super(makeName(s, s2, objectName));
        this.initName(s, s2, objectName);
        this.actions = actions;
        this.parseActions();
    }
    
    private static String makeName(String s, String s2, final ObjectName objectName) {
        final StringBuilder sb = new StringBuilder();
        if (s == null) {
            s = "-";
        }
        sb.append(s);
        if (s2 == null) {
            s2 = "-";
        }
        sb.append("#" + s2);
        if (objectName == null) {
            sb.append("[-]");
        }
        else {
            sb.append("[").append(objectName.getCanonicalName()).append("]");
        }
        if (sb.length() == 0) {
            return "*";
        }
        return sb.toString();
    }
    
    @Override
    public String getActions() {
        if (this.actions == null) {
            this.actions = getActions(this.mask);
        }
        return this.actions;
    }
    
    private static String getActions(final int n) {
        final StringBuilder sb = new StringBuilder();
        int n2 = 0;
        if ((n & 0x1) == 0x1) {
            n2 = 1;
            sb.append("addNotificationListener");
        }
        if ((n & 0x2) == 0x2) {
            if (n2 != 0) {
                sb.append(',');
            }
            else {
                n2 = 1;
            }
            sb.append("getAttribute");
        }
        if ((n & 0x4) == 0x4) {
            if (n2 != 0) {
                sb.append(',');
            }
            else {
                n2 = 1;
            }
            sb.append("getClassLoader");
        }
        if ((n & 0x8) == 0x8) {
            if (n2 != 0) {
                sb.append(',');
            }
            else {
                n2 = 1;
            }
            sb.append("getClassLoaderFor");
        }
        if ((n & 0x10) == 0x10) {
            if (n2 != 0) {
                sb.append(',');
            }
            else {
                n2 = 1;
            }
            sb.append("getClassLoaderRepository");
        }
        if ((n & 0x20) == 0x20) {
            if (n2 != 0) {
                sb.append(',');
            }
            else {
                n2 = 1;
            }
            sb.append("getDomains");
        }
        if ((n & 0x40) == 0x40) {
            if (n2 != 0) {
                sb.append(',');
            }
            else {
                n2 = 1;
            }
            sb.append("getMBeanInfo");
        }
        if ((n & 0x80) == 0x80) {
            if (n2 != 0) {
                sb.append(',');
            }
            else {
                n2 = 1;
            }
            sb.append("getObjectInstance");
        }
        if ((n & 0x100) == 0x100) {
            if (n2 != 0) {
                sb.append(',');
            }
            else {
                n2 = 1;
            }
            sb.append("instantiate");
        }
        if ((n & 0x200) == 0x200) {
            if (n2 != 0) {
                sb.append(',');
            }
            else {
                n2 = 1;
            }
            sb.append("invoke");
        }
        if ((n & 0x400) == 0x400) {
            if (n2 != 0) {
                sb.append(',');
            }
            else {
                n2 = 1;
            }
            sb.append("isInstanceOf");
        }
        if ((n & 0x800) == 0x800) {
            if (n2 != 0) {
                sb.append(',');
            }
            else {
                n2 = 1;
            }
            sb.append("queryMBeans");
        }
        if ((n & 0x1000) == 0x1000) {
            if (n2 != 0) {
                sb.append(',');
            }
            else {
                n2 = 1;
            }
            sb.append("queryNames");
        }
        if ((n & 0x2000) == 0x2000) {
            if (n2 != 0) {
                sb.append(',');
            }
            else {
                n2 = 1;
            }
            sb.append("registerMBean");
        }
        if ((n & 0x4000) == 0x4000) {
            if (n2 != 0) {
                sb.append(',');
            }
            else {
                n2 = 1;
            }
            sb.append("removeNotificationListener");
        }
        if ((n & 0x8000) == 0x8000) {
            if (n2 != 0) {
                sb.append(',');
            }
            else {
                n2 = 1;
            }
            sb.append("setAttribute");
        }
        if ((n & 0x10000) == 0x10000) {
            if (n2 != 0) {
                sb.append(',');
            }
            sb.append("unregisterMBean");
        }
        return sb.toString();
    }
    
    @Override
    public int hashCode() {
        return this.getName().hashCode() + this.getActions().hashCode();
    }
    
    private static int getMask(final String s) {
        int n = 0;
        if (s == null) {
            return n;
        }
        if (s.equals("*")) {
            return 131071;
        }
        final char[] charArray = s.toCharArray();
        int i = charArray.length - 1;
        if (i < 0) {
            return n;
        }
        while (i != -1) {
            char c;
            while (i != -1 && ((c = charArray[i]) == ' ' || c == '\r' || c == '\n' || c == '\f' || c == '\t')) {
                --i;
            }
            int n2;
            if (i >= 25 && charArray[i - 25] == 'r' && charArray[i - 24] == 'e' && charArray[i - 23] == 'm' && charArray[i - 22] == 'o' && charArray[i - 21] == 'v' && charArray[i - 20] == 'e' && charArray[i - 19] == 'N' && charArray[i - 18] == 'o' && charArray[i - 17] == 't' && charArray[i - 16] == 'i' && charArray[i - 15] == 'f' && charArray[i - 14] == 'i' && charArray[i - 13] == 'c' && charArray[i - 12] == 'a' && charArray[i - 11] == 't' && charArray[i - 10] == 'i' && charArray[i - 9] == 'o' && charArray[i - 8] == 'n' && charArray[i - 7] == 'L' && charArray[i - 6] == 'i' && charArray[i - 5] == 's' && charArray[i - 4] == 't' && charArray[i - 3] == 'e' && charArray[i - 2] == 'n' && charArray[i - 1] == 'e' && charArray[i] == 'r') {
                n2 = 26;
                n |= 0x4000;
            }
            else if (i >= 23 && charArray[i - 23] == 'g' && charArray[i - 22] == 'e' && charArray[i - 21] == 't' && charArray[i - 20] == 'C' && charArray[i - 19] == 'l' && charArray[i - 18] == 'a' && charArray[i - 17] == 's' && charArray[i - 16] == 's' && charArray[i - 15] == 'L' && charArray[i - 14] == 'o' && charArray[i - 13] == 'a' && charArray[i - 12] == 'd' && charArray[i - 11] == 'e' && charArray[i - 10] == 'r' && charArray[i - 9] == 'R' && charArray[i - 8] == 'e' && charArray[i - 7] == 'p' && charArray[i - 6] == 'o' && charArray[i - 5] == 's' && charArray[i - 4] == 'i' && charArray[i - 3] == 't' && charArray[i - 2] == 'o' && charArray[i - 1] == 'r' && charArray[i] == 'y') {
                n2 = 24;
                n |= 0x10;
            }
            else if (i >= 22 && charArray[i - 22] == 'a' && charArray[i - 21] == 'd' && charArray[i - 20] == 'd' && charArray[i - 19] == 'N' && charArray[i - 18] == 'o' && charArray[i - 17] == 't' && charArray[i - 16] == 'i' && charArray[i - 15] == 'f' && charArray[i - 14] == 'i' && charArray[i - 13] == 'c' && charArray[i - 12] == 'a' && charArray[i - 11] == 't' && charArray[i - 10] == 'i' && charArray[i - 9] == 'o' && charArray[i - 8] == 'n' && charArray[i - 7] == 'L' && charArray[i - 6] == 'i' && charArray[i - 5] == 's' && charArray[i - 4] == 't' && charArray[i - 3] == 'e' && charArray[i - 2] == 'n' && charArray[i - 1] == 'e' && charArray[i] == 'r') {
                n2 = 23;
                n |= 0x1;
            }
            else if (i >= 16 && charArray[i - 16] == 'g' && charArray[i - 15] == 'e' && charArray[i - 14] == 't' && charArray[i - 13] == 'C' && charArray[i - 12] == 'l' && charArray[i - 11] == 'a' && charArray[i - 10] == 's' && charArray[i - 9] == 's' && charArray[i - 8] == 'L' && charArray[i - 7] == 'o' && charArray[i - 6] == 'a' && charArray[i - 5] == 'd' && charArray[i - 4] == 'e' && charArray[i - 3] == 'r' && charArray[i - 2] == 'F' && charArray[i - 1] == 'o' && charArray[i] == 'r') {
                n2 = 17;
                n |= 0x8;
            }
            else if (i >= 16 && charArray[i - 16] == 'g' && charArray[i - 15] == 'e' && charArray[i - 14] == 't' && charArray[i - 13] == 'O' && charArray[i - 12] == 'b' && charArray[i - 11] == 'j' && charArray[i - 10] == 'e' && charArray[i - 9] == 'c' && charArray[i - 8] == 't' && charArray[i - 7] == 'I' && charArray[i - 6] == 'n' && charArray[i - 5] == 's' && charArray[i - 4] == 't' && charArray[i - 3] == 'a' && charArray[i - 2] == 'n' && charArray[i - 1] == 'c' && charArray[i] == 'e') {
                n2 = 17;
                n |= 0x80;
            }
            else if (i >= 14 && charArray[i - 14] == 'u' && charArray[i - 13] == 'n' && charArray[i - 12] == 'r' && charArray[i - 11] == 'e' && charArray[i - 10] == 'g' && charArray[i - 9] == 'i' && charArray[i - 8] == 's' && charArray[i - 7] == 't' && charArray[i - 6] == 'e' && charArray[i - 5] == 'r' && charArray[i - 4] == 'M' && charArray[i - 3] == 'B' && charArray[i - 2] == 'e' && charArray[i - 1] == 'a' && charArray[i] == 'n') {
                n2 = 15;
                n |= 0x10000;
            }
            else if (i >= 13 && charArray[i - 13] == 'g' && charArray[i - 12] == 'e' && charArray[i - 11] == 't' && charArray[i - 10] == 'C' && charArray[i - 9] == 'l' && charArray[i - 8] == 'a' && charArray[i - 7] == 's' && charArray[i - 6] == 's' && charArray[i - 5] == 'L' && charArray[i - 4] == 'o' && charArray[i - 3] == 'a' && charArray[i - 2] == 'd' && charArray[i - 1] == 'e' && charArray[i] == 'r') {
                n2 = 14;
                n |= 0x4;
            }
            else if (i >= 12 && charArray[i - 12] == 'r' && charArray[i - 11] == 'e' && charArray[i - 10] == 'g' && charArray[i - 9] == 'i' && charArray[i - 8] == 's' && charArray[i - 7] == 't' && charArray[i - 6] == 'e' && charArray[i - 5] == 'r' && charArray[i - 4] == 'M' && charArray[i - 3] == 'B' && charArray[i - 2] == 'e' && charArray[i - 1] == 'a' && charArray[i] == 'n') {
                n2 = 13;
                n |= 0x2000;
            }
            else if (i >= 11 && charArray[i - 11] == 'g' && charArray[i - 10] == 'e' && charArray[i - 9] == 't' && charArray[i - 8] == 'A' && charArray[i - 7] == 't' && charArray[i - 6] == 't' && charArray[i - 5] == 'r' && charArray[i - 4] == 'i' && charArray[i - 3] == 'b' && charArray[i - 2] == 'u' && charArray[i - 1] == 't' && charArray[i] == 'e') {
                n2 = 12;
                n |= 0x2;
            }
            else if (i >= 11 && charArray[i - 11] == 'g' && charArray[i - 10] == 'e' && charArray[i - 9] == 't' && charArray[i - 8] == 'M' && charArray[i - 7] == 'B' && charArray[i - 6] == 'e' && charArray[i - 5] == 'a' && charArray[i - 4] == 'n' && charArray[i - 3] == 'I' && charArray[i - 2] == 'n' && charArray[i - 1] == 'f' && charArray[i] == 'o') {
                n2 = 12;
                n |= 0x40;
            }
            else if (i >= 11 && charArray[i - 11] == 'i' && charArray[i - 10] == 's' && charArray[i - 9] == 'I' && charArray[i - 8] == 'n' && charArray[i - 7] == 's' && charArray[i - 6] == 't' && charArray[i - 5] == 'a' && charArray[i - 4] == 'n' && charArray[i - 3] == 'c' && charArray[i - 2] == 'e' && charArray[i - 1] == 'O' && charArray[i] == 'f') {
                n2 = 12;
                n |= 0x400;
            }
            else if (i >= 11 && charArray[i - 11] == 's' && charArray[i - 10] == 'e' && charArray[i - 9] == 't' && charArray[i - 8] == 'A' && charArray[i - 7] == 't' && charArray[i - 6] == 't' && charArray[i - 5] == 'r' && charArray[i - 4] == 'i' && charArray[i - 3] == 'b' && charArray[i - 2] == 'u' && charArray[i - 1] == 't' && charArray[i] == 'e') {
                n2 = 12;
                n |= 0x8000;
            }
            else if (i >= 10 && charArray[i - 10] == 'i' && charArray[i - 9] == 'n' && charArray[i - 8] == 's' && charArray[i - 7] == 't' && charArray[i - 6] == 'a' && charArray[i - 5] == 'n' && charArray[i - 4] == 't' && charArray[i - 3] == 'i' && charArray[i - 2] == 'a' && charArray[i - 1] == 't' && charArray[i] == 'e') {
                n2 = 11;
                n |= 0x100;
            }
            else if (i >= 10 && charArray[i - 10] == 'q' && charArray[i - 9] == 'u' && charArray[i - 8] == 'e' && charArray[i - 7] == 'r' && charArray[i - 6] == 'y' && charArray[i - 5] == 'M' && charArray[i - 4] == 'B' && charArray[i - 3] == 'e' && charArray[i - 2] == 'a' && charArray[i - 1] == 'n' && charArray[i] == 's') {
                n2 = 11;
                n |= 0x800;
            }
            else if (i >= 9 && charArray[i - 9] == 'g' && charArray[i - 8] == 'e' && charArray[i - 7] == 't' && charArray[i - 6] == 'D' && charArray[i - 5] == 'o' && charArray[i - 4] == 'm' && charArray[i - 3] == 'a' && charArray[i - 2] == 'i' && charArray[i - 1] == 'n' && charArray[i] == 's') {
                n2 = 10;
                n |= 0x20;
            }
            else if (i >= 9 && charArray[i - 9] == 'q' && charArray[i - 8] == 'u' && charArray[i - 7] == 'e' && charArray[i - 6] == 'r' && charArray[i - 5] == 'y' && charArray[i - 4] == 'N' && charArray[i - 3] == 'a' && charArray[i - 2] == 'm' && charArray[i - 1] == 'e' && charArray[i] == 's') {
                n2 = 10;
                n |= 0x1000;
            }
            else {
                if (i < 5 || charArray[i - 5] != 'i' || charArray[i - 4] != 'n' || charArray[i - 3] != 'v' || charArray[i - 2] != 'o' || charArray[i - 1] != 'k' || charArray[i] != 'e') {
                    throw new IllegalArgumentException("Invalid permission: " + s);
                }
                n2 = 6;
                n |= 0x200;
            }
            for (int n3 = 0; i >= n2 && n3 == 0; --i) {
                switch (charArray[i - n2]) {
                    case ',': {
                        n3 = 1;
                        break;
                    }
                    case '\t':
                    case '\n':
                    case '\f':
                    case '\r':
                    case ' ': {
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException("Invalid permission: " + s);
                    }
                }
            }
            i -= n2;
        }
        return n;
    }
    
    @Override
    public boolean implies(final Permission permission) {
        if (!(permission instanceof MBeanPermission)) {
            return false;
        }
        final MBeanPermission mBeanPermission = (MBeanPermission)permission;
        if ((this.mask & 0x800) == 0x800) {
            if (((this.mask | 0x1000) & mBeanPermission.mask) != mBeanPermission.mask) {
                return false;
            }
        }
        else if ((this.mask & mBeanPermission.mask) != mBeanPermission.mask) {
            return false;
        }
        if (mBeanPermission.classNamePrefix != null) {
            if (this.classNamePrefix == null) {
                return false;
            }
            if (this.classNameExactMatch) {
                if (!mBeanPermission.classNameExactMatch) {
                    return false;
                }
                if (!mBeanPermission.classNamePrefix.equals(this.classNamePrefix)) {
                    return false;
                }
            }
            else if (!mBeanPermission.classNamePrefix.startsWith(this.classNamePrefix)) {
                return false;
            }
        }
        if (mBeanPermission.member != null) {
            if (this.member == null) {
                return false;
            }
            if (!this.member.equals("*")) {
                if (!this.member.equals(mBeanPermission.member)) {
                    return false;
                }
            }
        }
        if (mBeanPermission.objectName != null) {
            if (this.objectName == null) {
                return false;
            }
            if (!this.objectName.apply(mBeanPermission.objectName) && !this.objectName.equals(mBeanPermission.objectName)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof MBeanPermission)) {
            return false;
        }
        final MBeanPermission mBeanPermission = (MBeanPermission)o;
        return this.mask == mBeanPermission.mask && this.getName().equals(mBeanPermission.getName());
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.parseName();
        this.parseActions();
    }
}
