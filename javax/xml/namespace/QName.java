package javax.xml.namespace;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class QName implements Serializable
{
    private static final long serialVersionUID;
    private static final long defaultSerialVersionUID = -9120448754896609940L;
    private static final long compatabilitySerialVersionUID = 4418622981026545151L;
    private final String namespaceURI;
    private final String localPart;
    private String prefix;
    private transient String qNameAsString;
    
    public QName(final String s, final String s2) {
        this(s, s2, "");
    }
    
    public QName(final String namespaceURI, final String localPart, final String prefix) {
        if (namespaceURI == null) {
            this.namespaceURI = "";
        }
        else {
            this.namespaceURI = namespaceURI;
        }
        if (localPart == null) {
            throw new IllegalArgumentException("local part cannot be \"null\" when creating a QName");
        }
        this.localPart = localPart;
        if (prefix == null) {
            throw new IllegalArgumentException("prefix cannot be \"null\" when creating a QName");
        }
        this.prefix = prefix;
    }
    
    public QName(final String s) {
        this("", s, "");
    }
    
    public String getNamespaceURI() {
        return this.namespaceURI;
    }
    
    public String getLocalPart() {
        return this.localPart;
    }
    
    public String getPrefix() {
        return this.prefix;
    }
    
    public final boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof QName) {
            final QName qName = (QName)o;
            return this.localPart.equals(qName.localPart) && this.namespaceURI.equals(qName.namespaceURI);
        }
        return false;
    }
    
    public final int hashCode() {
        return this.namespaceURI.hashCode() ^ this.localPart.hashCode();
    }
    
    public String toString() {
        String qNameAsString = this.qNameAsString;
        if (qNameAsString == null) {
            final int length = this.namespaceURI.length();
            if (length == 0) {
                qNameAsString = this.localPart;
            }
            else {
                final StringBuffer sb = new StringBuffer(length + this.localPart.length() + 2);
                sb.append('{');
                sb.append(this.namespaceURI);
                sb.append('}');
                sb.append(this.localPart);
                qNameAsString = sb.toString();
            }
            this.qNameAsString = qNameAsString;
        }
        return qNameAsString;
    }
    
    public static QName valueOf(final String s) {
        if (s == null) {
            throw new IllegalArgumentException("cannot create QName from \"null\" or \"\" String");
        }
        if (s.length() == 0) {
            return new QName("", s, "");
        }
        if (s.charAt(0) != '{') {
            return new QName("", s, "");
        }
        if (s.startsWith("{}")) {
            throw new IllegalArgumentException("Namespace URI .equals(XMLConstants.NULL_NS_URI), .equals(\"\"), only the local part, \"" + s.substring(2 + "".length()) + "\", " + "should be provided.");
        }
        final int index = s.indexOf(125);
        if (index == -1) {
            throw new IllegalArgumentException("cannot create QName from \"" + s + "\", missing closing \"}\"");
        }
        return new QName(s.substring(1, index), s.substring(index + 1), "");
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        if (this.prefix == null) {
            this.prefix = "";
        }
    }
    
    static {
        Object o = null;
        try {
            o = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction() {
                public Object run() {
                    return System.getProperty("org.apache.xml.namespace.QName.useCompatibleSerialVersionUID");
                }
            });
        }
        catch (final Exception ex) {}
        serialVersionUID = ("1.0".equals(o) ? 4418622981026545151L : -9120448754896609940L);
    }
}
