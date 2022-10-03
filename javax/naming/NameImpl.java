package javax.naming;

import java.util.NoSuchElementException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.Vector;

class NameImpl
{
    private static final byte LEFT_TO_RIGHT = 1;
    private static final byte RIGHT_TO_LEFT = 2;
    private static final byte FLAT = 0;
    private Vector<String> components;
    private byte syntaxDirection;
    private String syntaxSeparator;
    private String syntaxSeparator2;
    private boolean syntaxCaseInsensitive;
    private boolean syntaxTrimBlanks;
    private String syntaxEscape;
    private String syntaxBeginQuote1;
    private String syntaxEndQuote1;
    private String syntaxBeginQuote2;
    private String syntaxEndQuote2;
    private String syntaxAvaSeparator;
    private String syntaxTypevalSeparator;
    private static final int STYLE_NONE = 0;
    private static final int STYLE_QUOTE1 = 1;
    private static final int STYLE_QUOTE2 = 2;
    private static final int STYLE_ESCAPE = 3;
    private int escapingStyle;
    
    private final boolean isA(final String s, final int n, final String s2) {
        return s2 != null && s.startsWith(s2, n);
    }
    
    private final boolean isMeta(final String s, final int n) {
        return this.isA(s, n, this.syntaxEscape) || this.isA(s, n, this.syntaxBeginQuote1) || this.isA(s, n, this.syntaxBeginQuote2) || this.isSeparator(s, n);
    }
    
    private final boolean isSeparator(final String s, final int n) {
        return this.isA(s, n, this.syntaxSeparator) || this.isA(s, n, this.syntaxSeparator2);
    }
    
    private final int skipSeparator(final String s, int n) {
        if (this.isA(s, n, this.syntaxSeparator)) {
            n += this.syntaxSeparator.length();
        }
        else if (this.isA(s, n, this.syntaxSeparator2)) {
            n += this.syntaxSeparator2.length();
        }
        return n;
    }
    
    private final int extractComp(final String s, int i, final int n, final Vector<String> vector) throws InvalidNameException {
        int n2 = 1;
        final StringBuffer sb = new StringBuffer(n);
        while (i < n) {
            final boolean a;
            if (n2 != 0 && ((a = this.isA(s, i, this.syntaxBeginQuote1)) || this.isA(s, i, this.syntaxBeginQuote2))) {
                final String s2 = a ? this.syntaxBeginQuote1 : this.syntaxBeginQuote2;
                final String s3 = a ? this.syntaxEndQuote1 : this.syntaxEndQuote2;
                if (this.escapingStyle == 0) {
                    this.escapingStyle = (a ? 1 : 2);
                }
                for (i += s2.length(); i < n && !s.startsWith(s3, i); ++i) {
                    if (this.isA(s, i, this.syntaxEscape) && this.isA(s, i + this.syntaxEscape.length(), s3)) {
                        i += this.syntaxEscape.length();
                    }
                    sb.append(s.charAt(i));
                }
                if (i >= n) {
                    throw new InvalidNameException(s + ": no close quote");
                }
                i += s3.length();
                if (i == n) {
                    break;
                }
                if (this.isSeparator(s, i)) {
                    break;
                }
                throw new InvalidNameException(s + ": close quote appears before end of component");
            }
            else {
                if (this.isSeparator(s, i)) {
                    break;
                }
                if (this.isA(s, i, this.syntaxEscape)) {
                    if (this.isMeta(s, i + this.syntaxEscape.length())) {
                        i += this.syntaxEscape.length();
                        if (this.escapingStyle == 0) {
                            this.escapingStyle = 3;
                        }
                    }
                    else if (i + this.syntaxEscape.length() >= n) {
                        throw new InvalidNameException(s + ": unescaped " + this.syntaxEscape + " at end of component");
                    }
                }
                else {
                    final boolean a2;
                    if (this.isA(s, i, this.syntaxTypevalSeparator) && ((a2 = this.isA(s, i + this.syntaxTypevalSeparator.length(), this.syntaxBeginQuote1)) || this.isA(s, i + this.syntaxTypevalSeparator.length(), this.syntaxBeginQuote2))) {
                        final String s4 = a2 ? this.syntaxBeginQuote1 : this.syntaxBeginQuote2;
                        final String s5 = a2 ? this.syntaxEndQuote1 : this.syntaxEndQuote2;
                        i += this.syntaxTypevalSeparator.length();
                        sb.append(this.syntaxTypevalSeparator + s4);
                        for (i += s4.length(); i < n && !s.startsWith(s5, i); ++i) {
                            if (this.isA(s, i, this.syntaxEscape) && this.isA(s, i + this.syntaxEscape.length(), s5)) {
                                i += this.syntaxEscape.length();
                            }
                            sb.append(s.charAt(i));
                        }
                        if (i >= n) {
                            throw new InvalidNameException(s + ": typeval no close quote");
                        }
                        i += s5.length();
                        sb.append(s5);
                        if (i == n) {
                            break;
                        }
                        if (this.isSeparator(s, i)) {
                            break;
                        }
                        throw new InvalidNameException(s.substring(i) + ": typeval close quote appears before end of component");
                    }
                }
                sb.append(s.charAt(i++));
                n2 = 0;
            }
        }
        if (this.syntaxDirection == 2) {
            vector.insertElementAt(sb.toString(), 0);
        }
        else {
            vector.addElement(sb.toString());
        }
        return i;
    }
    
    private static boolean getBoolean(final Properties properties, final String s) {
        return toBoolean(properties.getProperty(s));
    }
    
    private static boolean toBoolean(final String s) {
        return s != null && s.toLowerCase(Locale.ENGLISH).equals("true");
    }
    
    private final void recordNamingConvention(final Properties properties) {
        final String property = properties.getProperty("jndi.syntax.direction", "flat");
        if (property.equals("left_to_right")) {
            this.syntaxDirection = 1;
        }
        else if (property.equals("right_to_left")) {
            this.syntaxDirection = 2;
        }
        else {
            if (!property.equals("flat")) {
                throw new IllegalArgumentException(property + "is not a valid value for the jndi.syntax.direction property");
            }
            this.syntaxDirection = 0;
        }
        if (this.syntaxDirection != 0) {
            this.syntaxSeparator = properties.getProperty("jndi.syntax.separator");
            this.syntaxSeparator2 = properties.getProperty("jndi.syntax.separator2");
            if (this.syntaxSeparator == null) {
                throw new IllegalArgumentException("jndi.syntax.separator property required for non-flat syntax");
            }
        }
        else {
            this.syntaxSeparator = null;
        }
        this.syntaxEscape = properties.getProperty("jndi.syntax.escape");
        this.syntaxCaseInsensitive = getBoolean(properties, "jndi.syntax.ignorecase");
        this.syntaxTrimBlanks = getBoolean(properties, "jndi.syntax.trimblanks");
        this.syntaxBeginQuote1 = properties.getProperty("jndi.syntax.beginquote");
        this.syntaxEndQuote1 = properties.getProperty("jndi.syntax.endquote");
        if (this.syntaxEndQuote1 == null && this.syntaxBeginQuote1 != null) {
            this.syntaxEndQuote1 = this.syntaxBeginQuote1;
        }
        else if (this.syntaxBeginQuote1 == null && this.syntaxEndQuote1 != null) {
            this.syntaxBeginQuote1 = this.syntaxEndQuote1;
        }
        this.syntaxBeginQuote2 = properties.getProperty("jndi.syntax.beginquote2");
        this.syntaxEndQuote2 = properties.getProperty("jndi.syntax.endquote2");
        if (this.syntaxEndQuote2 == null && this.syntaxBeginQuote2 != null) {
            this.syntaxEndQuote2 = this.syntaxBeginQuote2;
        }
        else if (this.syntaxBeginQuote2 == null && this.syntaxEndQuote2 != null) {
            this.syntaxBeginQuote2 = this.syntaxEndQuote2;
        }
        this.syntaxAvaSeparator = properties.getProperty("jndi.syntax.separator.ava");
        this.syntaxTypevalSeparator = properties.getProperty("jndi.syntax.separator.typeval");
    }
    
    NameImpl(final Properties properties) {
        this.syntaxDirection = 1;
        this.syntaxSeparator = "/";
        this.syntaxSeparator2 = null;
        this.syntaxCaseInsensitive = false;
        this.syntaxTrimBlanks = false;
        this.syntaxEscape = "\\";
        this.syntaxBeginQuote1 = "\"";
        this.syntaxEndQuote1 = "\"";
        this.syntaxBeginQuote2 = "'";
        this.syntaxEndQuote2 = "'";
        this.syntaxAvaSeparator = null;
        this.syntaxTypevalSeparator = null;
        this.escapingStyle = 0;
        if (properties != null) {
            this.recordNamingConvention(properties);
        }
        this.components = new Vector<String>();
    }
    
    NameImpl(final Properties properties, final String s) throws InvalidNameException {
        this(properties);
        final boolean b = this.syntaxDirection == 2;
        boolean b2 = true;
        final int length = s.length();
        int i = 0;
        while (i < length) {
            i = this.extractComp(s, i, length, this.components);
            if ((b ? this.components.firstElement() : this.components.lastElement()).length() >= 1) {
                b2 = false;
            }
            if (i < length) {
                i = this.skipSeparator(s, i);
                if (i != length || b2) {
                    continue;
                }
                if (b) {
                    this.components.insertElementAt("", 0);
                }
                else {
                    this.components.addElement("");
                }
            }
        }
    }
    
    NameImpl(final Properties properties, final Enumeration<String> enumeration) {
        this(properties);
        while (enumeration.hasMoreElements()) {
            this.components.addElement(enumeration.nextElement());
        }
    }
    
    private final String stringifyComp(final String s) {
        final int length = s.length();
        boolean b = false;
        boolean b2 = false;
        String s2 = null;
        String s3 = null;
        StringBuffer append = new StringBuffer(length);
        if (this.syntaxSeparator != null && s.indexOf(this.syntaxSeparator) >= 0) {
            if (this.syntaxBeginQuote1 != null) {
                s2 = this.syntaxBeginQuote1;
                s3 = this.syntaxEndQuote1;
            }
            else if (this.syntaxBeginQuote2 != null) {
                s2 = this.syntaxBeginQuote2;
                s3 = this.syntaxEndQuote2;
            }
            else if (this.syntaxEscape != null) {
                b = true;
            }
        }
        if (this.syntaxSeparator2 != null && s.indexOf(this.syntaxSeparator2) >= 0) {
            if (this.syntaxBeginQuote1 != null) {
                if (s2 == null) {
                    s2 = this.syntaxBeginQuote1;
                    s3 = this.syntaxEndQuote1;
                }
            }
            else if (this.syntaxBeginQuote2 != null) {
                if (s2 == null) {
                    s2 = this.syntaxBeginQuote2;
                    s3 = this.syntaxEndQuote2;
                }
            }
            else if (this.syntaxEscape != null) {
                b2 = true;
            }
        }
        if (s2 != null) {
            append = append.append(s2);
            int i = 0;
            while (i < length) {
                if (s.startsWith(s3, i)) {
                    append.append(this.syntaxEscape).append(s3);
                    i += s3.length();
                }
                else {
                    append.append(s.charAt(i++));
                }
            }
            append.append(s3);
        }
        else {
            int n = 1;
            int j = 0;
            while (j < length) {
                if (n != 0 && this.isA(s, j, this.syntaxBeginQuote1)) {
                    append.append(this.syntaxEscape).append(this.syntaxBeginQuote1);
                    j += this.syntaxBeginQuote1.length();
                }
                else if (n != 0 && this.isA(s, j, this.syntaxBeginQuote2)) {
                    append.append(this.syntaxEscape).append(this.syntaxBeginQuote2);
                    j += this.syntaxBeginQuote2.length();
                }
                else if (this.isA(s, j, this.syntaxEscape)) {
                    if (j + this.syntaxEscape.length() >= length) {
                        append.append(this.syntaxEscape);
                    }
                    else if (this.isMeta(s, j + this.syntaxEscape.length())) {
                        append.append(this.syntaxEscape);
                    }
                    append.append(this.syntaxEscape);
                    j += this.syntaxEscape.length();
                }
                else if (b && s.startsWith(this.syntaxSeparator, j)) {
                    append.append(this.syntaxEscape).append(this.syntaxSeparator);
                    j += this.syntaxSeparator.length();
                }
                else if (b2 && s.startsWith(this.syntaxSeparator2, j)) {
                    append.append(this.syntaxEscape).append(this.syntaxSeparator2);
                    j += this.syntaxSeparator2.length();
                }
                else {
                    append.append(s.charAt(j++));
                }
                n = 0;
            }
        }
        return append.toString();
    }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        boolean b = true;
        final int size = this.components.size();
        for (int i = 0; i < size; ++i) {
            String s;
            if (this.syntaxDirection == 2) {
                s = this.stringifyComp(this.components.elementAt(size - 1 - i));
            }
            else {
                s = this.stringifyComp(this.components.elementAt(i));
            }
            if (i != 0 && this.syntaxSeparator != null) {
                sb.append(this.syntaxSeparator);
            }
            if (s.length() >= 1) {
                b = false;
            }
            sb = sb.append(s);
        }
        if (b && size >= 1 && this.syntaxSeparator != null) {
            sb = sb.append(this.syntaxSeparator);
        }
        return sb.toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o != null && o instanceof NameImpl) {
            final NameImpl nameImpl = (NameImpl)o;
            if (nameImpl.size() == this.size()) {
                final Enumeration<String> all = this.getAll();
                final Enumeration<String> all2 = nameImpl.getAll();
                while (all.hasMoreElements()) {
                    String trim = all.nextElement();
                    String trim2 = all2.nextElement();
                    if (this.syntaxTrimBlanks) {
                        trim = trim.trim();
                        trim2 = trim2.trim();
                    }
                    if (this.syntaxCaseInsensitive) {
                        if (!trim.equalsIgnoreCase(trim2)) {
                            return false;
                        }
                        continue;
                    }
                    else {
                        if (!trim.equals(trim2)) {
                            return false;
                        }
                        continue;
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    public int compareTo(final NameImpl nameImpl) {
        if (this == nameImpl) {
            return 0;
        }
        final int size = this.size();
        final int size2 = nameImpl.size();
        int min = Math.min(size, size2);
        int n = 0;
        int n2 = 0;
        while (min-- != 0) {
            String s = this.get(n++);
            String s2 = nameImpl.get(n2++);
            if (this.syntaxTrimBlanks) {
                s = s.trim();
                s2 = s2.trim();
            }
            int n3;
            if (this.syntaxCaseInsensitive) {
                n3 = s.compareToIgnoreCase(s2);
            }
            else {
                n3 = s.compareTo(s2);
            }
            if (n3 != 0) {
                return n3;
            }
        }
        return size - size2;
    }
    
    public int size() {
        return this.components.size();
    }
    
    public Enumeration<String> getAll() {
        return this.components.elements();
    }
    
    public String get(final int n) {
        return this.components.elementAt(n);
    }
    
    public Enumeration<String> getPrefix(final int n) {
        if (n < 0 || n > this.size()) {
            throw new ArrayIndexOutOfBoundsException(n);
        }
        return new NameImplEnumerator(this.components, 0, n);
    }
    
    public Enumeration<String> getSuffix(final int n) {
        final int size = this.size();
        if (n < 0 || n > size) {
            throw new ArrayIndexOutOfBoundsException(n);
        }
        return new NameImplEnumerator(this.components, n, size);
    }
    
    public boolean isEmpty() {
        return this.components.isEmpty();
    }
    
    public boolean startsWith(final int n, final Enumeration<String> enumeration) {
        if (n < 0 || n > this.size()) {
            return false;
        }
        try {
            final Enumeration<String> prefix = this.getPrefix(n);
            while (prefix.hasMoreElements()) {
                String trim = prefix.nextElement();
                String trim2 = enumeration.nextElement();
                if (this.syntaxTrimBlanks) {
                    trim = trim.trim();
                    trim2 = trim2.trim();
                }
                if (this.syntaxCaseInsensitive) {
                    if (!trim.equalsIgnoreCase(trim2)) {
                        return false;
                    }
                    continue;
                }
                else {
                    if (!trim.equals(trim2)) {
                        return false;
                    }
                    continue;
                }
            }
        }
        catch (final NoSuchElementException ex) {
            return false;
        }
        return true;
    }
    
    public boolean endsWith(final int n, final Enumeration<String> enumeration) {
        final int n2 = this.size() - n;
        if (n2 < 0 || n2 > this.size()) {
            return false;
        }
        try {
            final Enumeration<String> suffix = this.getSuffix(n2);
            while (suffix.hasMoreElements()) {
                String trim = suffix.nextElement();
                String trim2 = enumeration.nextElement();
                if (this.syntaxTrimBlanks) {
                    trim = trim.trim();
                    trim2 = trim2.trim();
                }
                if (this.syntaxCaseInsensitive) {
                    if (!trim.equalsIgnoreCase(trim2)) {
                        return false;
                    }
                    continue;
                }
                else {
                    if (!trim.equals(trim2)) {
                        return false;
                    }
                    continue;
                }
            }
        }
        catch (final NoSuchElementException ex) {
            return false;
        }
        return true;
    }
    
    public boolean addAll(final Enumeration<String> enumeration) throws InvalidNameException {
        boolean b = false;
        while (enumeration.hasMoreElements()) {
            try {
                final String s = enumeration.nextElement();
                if (this.size() > 0 && this.syntaxDirection == 0) {
                    throw new InvalidNameException("A flat name can only have a single component");
                }
                this.components.addElement(s);
                b = true;
                continue;
            }
            catch (final NoSuchElementException ex) {}
            break;
        }
        return b;
    }
    
    public boolean addAll(final int n, final Enumeration<String> enumeration) throws InvalidNameException {
        boolean b = false;
        int n2 = n;
        while (enumeration.hasMoreElements()) {
            try {
                final String s = enumeration.nextElement();
                if (this.size() > 0 && this.syntaxDirection == 0) {
                    throw new InvalidNameException("A flat name can only have a single component");
                }
                this.components.insertElementAt(s, n2);
                b = true;
            }
            catch (final NoSuchElementException ex) {
                break;
            }
            ++n2;
        }
        return b;
    }
    
    public void add(final String s) throws InvalidNameException {
        if (this.size() > 0 && this.syntaxDirection == 0) {
            throw new InvalidNameException("A flat name can only have a single component");
        }
        this.components.addElement(s);
    }
    
    public void add(final int n, final String s) throws InvalidNameException {
        if (this.size() > 0 && this.syntaxDirection == 0) {
            throw new InvalidNameException("A flat name can only zero or one component");
        }
        this.components.insertElementAt(s, n);
    }
    
    public Object remove(final int n) {
        final String element = this.components.elementAt(n);
        this.components.removeElementAt(n);
        return element;
    }
    
    @Override
    public int hashCode() {
        int n = 0;
        final Enumeration<String> all = this.getAll();
        while (all.hasMoreElements()) {
            String s = all.nextElement();
            if (this.syntaxTrimBlanks) {
                s = s.trim();
            }
            if (this.syntaxCaseInsensitive) {
                s = s.toLowerCase(Locale.ENGLISH);
            }
            n += s.hashCode();
        }
        return n;
    }
}
