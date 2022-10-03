package java.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.security.Permission;

public final class URLPermission extends Permission
{
    private static final long serialVersionUID = -2702463814894478682L;
    private transient String scheme;
    private transient String ssp;
    private transient String path;
    private transient List<String> methods;
    private transient List<String> requestHeaders;
    private transient Authority authority;
    private String actions;
    
    public URLPermission(final String s, final String s2) {
        super(s);
        this.init(s2);
    }
    
    private void init(final String s) {
        this.parseURI(this.getName());
        final int index = s.indexOf(58);
        if (s.lastIndexOf(58) != index) {
            throw new IllegalArgumentException("Invalid actions string: \"" + s + "\"");
        }
        String substring;
        String substring2;
        if (index == -1) {
            substring = s;
            substring2 = "";
        }
        else {
            substring = s.substring(0, index);
            substring2 = s.substring(index + 1);
        }
        final List<String> normalizeMethods = this.normalizeMethods(substring);
        Collections.sort((List<Comparable>)normalizeMethods);
        this.methods = (List<String>)Collections.unmodifiableList((List<?>)normalizeMethods);
        final List<String> normalizeHeaders = this.normalizeHeaders(substring2);
        Collections.sort((List<Comparable>)normalizeHeaders);
        this.requestHeaders = (List<String>)Collections.unmodifiableList((List<?>)normalizeHeaders);
        this.actions = this.actions();
    }
    
    public URLPermission(final String s) {
        this(s, "*:*");
    }
    
    @Override
    public String getActions() {
        return this.actions;
    }
    
    @Override
    public boolean implies(final Permission permission) {
        if (!(permission instanceof URLPermission)) {
            return false;
        }
        final URLPermission urlPermission = (URLPermission)permission;
        if (!this.methods.get(0).equals("*") && Collections.indexOfSubList(this.methods, urlPermission.methods) == -1) {
            return false;
        }
        if (this.requestHeaders.isEmpty() && !urlPermission.requestHeaders.isEmpty()) {
            return false;
        }
        if (!this.requestHeaders.isEmpty() && !this.requestHeaders.get(0).equals("*") && Collections.indexOfSubList(this.requestHeaders, urlPermission.requestHeaders) == -1) {
            return false;
        }
        if (!this.scheme.equals(urlPermission.scheme)) {
            return false;
        }
        if (this.ssp.equals("*")) {
            return true;
        }
        if (!this.authority.implies(urlPermission.authority)) {
            return false;
        }
        if (this.path == null) {
            return urlPermission.path == null;
        }
        if (urlPermission.path == null) {
            return false;
        }
        if (this.path.endsWith("/-")) {
            return urlPermission.path.startsWith(this.path.substring(0, this.path.length() - 1));
        }
        if (!this.path.endsWith("/*")) {
            return this.path.equals(urlPermission.path);
        }
        final String substring = this.path.substring(0, this.path.length() - 1);
        if (!urlPermission.path.startsWith(substring)) {
            return false;
        }
        final String substring2 = urlPermission.path.substring(substring.length());
        return substring2.indexOf(47) == -1 && !substring2.equals("-");
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof URLPermission)) {
            return false;
        }
        final URLPermission urlPermission = (URLPermission)o;
        if (!this.scheme.equals(urlPermission.scheme)) {
            return false;
        }
        if (!this.getActions().equals(urlPermission.getActions())) {
            return false;
        }
        if (!this.authority.equals(urlPermission.authority)) {
            return false;
        }
        if (this.path != null) {
            return this.path.equals(urlPermission.path);
        }
        return urlPermission.path == null;
    }
    
    @Override
    public int hashCode() {
        return this.getActions().hashCode() + this.scheme.hashCode() + this.authority.hashCode() + ((this.path == null) ? 0 : this.path.hashCode());
    }
    
    private List<String> normalizeMethods(final String s) {
        final ArrayList list = new ArrayList();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); ++i) {
            char char1 = s.charAt(i);
            if (char1 == ',') {
                final String string = sb.toString();
                if (string.length() > 0) {
                    list.add(string);
                }
                sb = new StringBuilder();
            }
            else {
                if (char1 == ' ' || char1 == '\t') {
                    throw new IllegalArgumentException("White space not allowed in methods: \"" + s + "\"");
                }
                if (char1 >= 'a' && char1 <= 'z') {
                    char1 -= ' ';
                }
                sb.append(char1);
            }
        }
        final String string2 = sb.toString();
        if (string2.length() > 0) {
            list.add(string2);
        }
        return list;
    }
    
    private List<String> normalizeHeaders(final String s) {
        final ArrayList list = new ArrayList();
        StringBuilder sb = new StringBuilder();
        int n = 1;
        for (int i = 0; i < s.length(); ++i) {
            char char1 = s.charAt(i);
            if (char1 >= 'a' && char1 <= 'z') {
                if (n != 0) {
                    char1 -= ' ';
                    n = 0;
                }
                sb.append(char1);
            }
            else {
                if (char1 == ' ' || char1 == '\t') {
                    throw new IllegalArgumentException("White space not allowed in headers: \"" + s + "\"");
                }
                if (char1 == '-') {
                    n = 1;
                    sb.append(char1);
                }
                else if (char1 == ',') {
                    final String string = sb.toString();
                    if (string.length() > 0) {
                        list.add(string);
                    }
                    sb = new StringBuilder();
                    n = 1;
                }
                else {
                    n = 0;
                    sb.append(char1);
                }
            }
        }
        final String string2 = sb.toString();
        if (string2.length() > 0) {
            list.add(string2);
        }
        return list;
    }
    
    private void parseURI(final String s) {
        final int length = s.length();
        final int index = s.indexOf(58);
        if (index == -1 || index + 1 == length) {
            throw new IllegalArgumentException("Invalid URL string: \"" + s + "\"");
        }
        this.scheme = s.substring(0, index).toLowerCase();
        this.ssp = s.substring(index + 1);
        if (this.ssp.startsWith("//")) {
            final String substring = this.ssp.substring(2);
            final int index2 = substring.indexOf(47);
            String substring2;
            if (index2 == -1) {
                this.path = "";
                substring2 = substring;
            }
            else {
                substring2 = substring.substring(0, index2);
                this.path = substring.substring(index2);
            }
            this.authority = new Authority(this.scheme, substring2.toLowerCase());
            return;
        }
        if (!this.ssp.equals("*")) {
            throw new IllegalArgumentException("Invalid URL string: \"" + s + "\"");
        }
        this.authority = new Authority(this.scheme, "*");
    }
    
    private String actions() {
        final StringBuilder sb = new StringBuilder();
        final Iterator<String> iterator = this.methods.iterator();
        while (iterator.hasNext()) {
            sb.append(iterator.next());
        }
        sb.append(":");
        final Iterator<String> iterator2 = this.requestHeaders.iterator();
        while (iterator2.hasNext()) {
            sb.append(iterator2.next());
        }
        return sb.toString();
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        this.init((String)objectInputStream.readFields().get("actions", null));
    }
    
    static class Authority
    {
        HostPortrange p;
        
        Authority(final String s, final String s2) {
            final int index = s2.indexOf(64);
            if (index == -1) {
                this.p = new HostPortrange(s, s2);
            }
            else {
                this.p = new HostPortrange(s, s2.substring(index + 1));
            }
        }
        
        boolean implies(final Authority authority) {
            return this.impliesHostrange(authority) && this.impliesPortrange(authority);
        }
        
        private boolean impliesHostrange(final Authority authority) {
            final String hostname = this.p.hostname();
            final String hostname2 = authority.p.hostname();
            return (this.p.wildcard() && hostname.equals("")) || ((!authority.p.wildcard() || !hostname2.equals("")) && (hostname.equals(hostname2) || (this.p.wildcard() && hostname2.endsWith(hostname))));
        }
        
        private boolean impliesPortrange(final Authority authority) {
            final int[] portrange = this.p.portrange();
            final int[] portrange2 = authority.p.portrange();
            return portrange[0] == -1 || (portrange[0] <= portrange2[0] && portrange[1] >= portrange2[1]);
        }
        
        boolean equals(final Authority authority) {
            return this.p.equals(authority.p);
        }
        
        @Override
        public int hashCode() {
            return this.p.hashCode();
        }
    }
}
