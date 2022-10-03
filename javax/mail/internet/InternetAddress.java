package javax.mail.internet;

import com.sun.mail.util.PropUtil;
import java.util.List;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.mail.Session;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.io.UnsupportedEncodingException;
import javax.mail.Address;

public class InternetAddress extends Address implements Cloneable
{
    protected String address;
    protected String personal;
    protected String encodedPersonal;
    private static final long serialVersionUID = -7507595530758302903L;
    private static final boolean ignoreBogusGroupName;
    private static final boolean useCanonicalHostName;
    private static final boolean allowUtf8;
    private static final String rfc822phrase;
    private static final String specialsNoDotNoAt = "()<>,;:\\\"[]";
    private static final String specialsNoDot = "()<>,;:\\\"[]@";
    
    public InternetAddress() {
    }
    
    public InternetAddress(final String address) throws AddressException {
        final InternetAddress[] a = parse(address, true);
        if (a.length != 1) {
            throw new AddressException("Illegal address", address);
        }
        this.address = a[0].address;
        this.personal = a[0].personal;
        this.encodedPersonal = a[0].encodedPersonal;
    }
    
    public InternetAddress(final String address, final boolean strict) throws AddressException {
        this(address);
        if (strict) {
            if (this.isGroup()) {
                this.getGroup(true);
            }
            else {
                checkAddress(this.address, true, true);
            }
        }
    }
    
    public InternetAddress(final String address, final String personal) throws UnsupportedEncodingException {
        this(address, personal, null);
    }
    
    public InternetAddress(final String address, final String personal, final String charset) throws UnsupportedEncodingException {
        this.address = address;
        this.setPersonal(personal, charset);
    }
    
    public Object clone() {
        InternetAddress a = null;
        try {
            a = (InternetAddress)super.clone();
        }
        catch (final CloneNotSupportedException ex) {}
        return a;
    }
    
    @Override
    public String getType() {
        return "rfc822";
    }
    
    public void setAddress(final String address) {
        this.address = address;
    }
    
    public void setPersonal(final String name, final String charset) throws UnsupportedEncodingException {
        this.personal = name;
        if (name != null) {
            this.encodedPersonal = MimeUtility.encodeWord(name, charset, null);
        }
        else {
            this.encodedPersonal = null;
        }
    }
    
    public void setPersonal(final String name) throws UnsupportedEncodingException {
        this.personal = name;
        if (name != null) {
            this.encodedPersonal = MimeUtility.encodeWord(name);
        }
        else {
            this.encodedPersonal = null;
        }
    }
    
    public String getAddress() {
        return this.address;
    }
    
    public String getPersonal() {
        if (this.personal != null) {
            return this.personal;
        }
        if (this.encodedPersonal != null) {
            try {
                return this.personal = MimeUtility.decodeText(this.encodedPersonal);
            }
            catch (final Exception ex) {
                return this.encodedPersonal;
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        final String a = (this.address == null) ? "" : this.address;
        if (this.encodedPersonal == null && this.personal != null) {
            try {
                this.encodedPersonal = MimeUtility.encodeWord(this.personal);
            }
            catch (final UnsupportedEncodingException ex) {}
        }
        if (this.encodedPersonal != null) {
            return quotePhrase(this.encodedPersonal) + " <" + a + ">";
        }
        if (this.isGroup() || this.isSimple()) {
            return a;
        }
        return "<" + a + ">";
    }
    
    public String toUnicodeString() {
        final String p = this.getPersonal();
        if (p != null) {
            return quotePhrase(p) + " <" + this.address + ">";
        }
        if (this.isGroup() || this.isSimple()) {
            return this.address;
        }
        return "<" + this.address + ">";
    }
    
    private static String quotePhrase(final String phrase) {
        final int len = phrase.length();
        boolean needQuoting = false;
        for (int i = 0; i < len; ++i) {
            final char c = phrase.charAt(i);
            if (c == '\"' || c == '\\') {
                final StringBuilder sb = new StringBuilder(len + 3);
                sb.append('\"');
                for (int j = 0; j < len; ++j) {
                    final char cc = phrase.charAt(j);
                    if (cc == '\"' || cc == '\\') {
                        sb.append('\\');
                    }
                    sb.append(cc);
                }
                sb.append('\"');
                return sb.toString();
            }
            if ((c < ' ' && c != '\r' && c != '\n' && c != '\t') || (c >= '\u007f' && !InternetAddress.allowUtf8) || InternetAddress.rfc822phrase.indexOf(c) >= 0) {
                needQuoting = true;
            }
        }
        if (needQuoting) {
            final StringBuilder sb2 = new StringBuilder(len + 2);
            sb2.append('\"').append(phrase).append('\"');
            return sb2.toString();
        }
        return phrase;
    }
    
    private static String unquote(String s) {
        if (s.startsWith("\"") && s.endsWith("\"") && s.length() > 1) {
            s = s.substring(1, s.length() - 1);
            if (s.indexOf(92) >= 0) {
                final StringBuilder sb = new StringBuilder(s.length());
                for (int i = 0; i < s.length(); ++i) {
                    char c = s.charAt(i);
                    if (c == '\\' && i < s.length() - 1) {
                        c = s.charAt(++i);
                    }
                    sb.append(c);
                }
                s = sb.toString();
            }
        }
        return s;
    }
    
    @Override
    public boolean equals(final Object a) {
        if (!(a instanceof InternetAddress)) {
            return false;
        }
        final String s = ((InternetAddress)a).getAddress();
        return s == this.address || (this.address != null && this.address.equalsIgnoreCase(s));
    }
    
    @Override
    public int hashCode() {
        if (this.address == null) {
            return 0;
        }
        return this.address.toLowerCase(Locale.ENGLISH).hashCode();
    }
    
    public static String toString(final Address[] addresses) {
        return toString(addresses, 0);
    }
    
    public static String toUnicodeString(final Address[] addresses) {
        return toUnicodeString(addresses, 0);
    }
    
    public static String toString(final Address[] addresses, int used) {
        if (addresses == null || addresses.length == 0) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < addresses.length; ++i) {
            if (i != 0) {
                sb.append(", ");
                used += 2;
            }
            final String s = MimeUtility.fold(0, addresses[i].toString());
            final int len = lengthOfFirstSegment(s);
            if (used + len > 76) {
                final int curlen = sb.length();
                if (curlen > 0 && sb.charAt(curlen - 1) == ' ') {
                    sb.setLength(curlen - 1);
                }
                sb.append("\r\n\t");
                used = 8;
            }
            sb.append(s);
            used = lengthOfLastSegment(s, used);
        }
        return sb.toString();
    }
    
    public static String toUnicodeString(final Address[] addresses, int used) {
        if (addresses == null || addresses.length == 0) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        boolean sawNonAscii = false;
        for (int i = 0; i < addresses.length; ++i) {
            if (i != 0) {
                sb.append(", ");
                used += 2;
            }
            String as = ((InternetAddress)addresses[i]).toUnicodeString();
            if (MimeUtility.checkAscii(as) != 1) {
                sawNonAscii = true;
                as = new String(as.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
            }
            final String s = MimeUtility.fold(0, as);
            final int len = lengthOfFirstSegment(s);
            if (used + len > 76) {
                final int curlen = sb.length();
                if (curlen > 0 && sb.charAt(curlen - 1) == ' ') {
                    sb.setLength(curlen - 1);
                }
                sb.append("\r\n\t");
                used = 8;
            }
            sb.append(s);
            used = lengthOfLastSegment(s, used);
        }
        String ret = sb.toString();
        if (sawNonAscii) {
            ret = new String(ret.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        }
        return ret;
    }
    
    private static int lengthOfFirstSegment(final String s) {
        final int pos;
        if ((pos = s.indexOf("\r\n")) != -1) {
            return pos;
        }
        return s.length();
    }
    
    private static int lengthOfLastSegment(final String s, final int used) {
        final int pos;
        if ((pos = s.lastIndexOf("\r\n")) != -1) {
            return s.length() - pos - 2;
        }
        return s.length() + used;
    }
    
    public static InternetAddress getLocalAddress(final Session session) {
        try {
            return _getLocalAddress(session);
        }
        catch (final SecurityException ex) {}
        catch (final AddressException ex2) {}
        catch (final UnknownHostException ex3) {}
        return null;
    }
    
    static InternetAddress _getLocalAddress(final Session session) throws SecurityException, AddressException, UnknownHostException {
        String user = null;
        String host = null;
        String address = null;
        if (session == null) {
            user = System.getProperty("user.name");
            host = getLocalHostName();
        }
        else {
            address = session.getProperty("mail.from");
            if (address == null) {
                user = session.getProperty("mail.user");
                if (user == null || user.length() == 0) {
                    user = session.getProperty("user.name");
                }
                if (user == null || user.length() == 0) {
                    user = System.getProperty("user.name");
                }
                host = session.getProperty("mail.host");
                if (host == null || host.length() == 0) {
                    host = getLocalHostName();
                }
            }
        }
        if (address == null && user != null && user.length() != 0 && host != null && host.length() != 0) {
            address = MimeUtility.quote(user.trim(), "()<>,;:\\\"[]@\t ") + "@" + host;
        }
        if (address == null) {
            return null;
        }
        return new InternetAddress(address);
    }
    
    private static String getLocalHostName() throws UnknownHostException {
        String host = null;
        final InetAddress me = InetAddress.getLocalHost();
        if (me != null) {
            if (InternetAddress.useCanonicalHostName) {
                host = me.getCanonicalHostName();
            }
            if (host == null) {
                host = me.getHostName();
            }
            if (host == null) {
                host = me.getHostAddress();
            }
            if (host != null && host.length() > 0 && isInetAddressLiteral(host)) {
                host = '[' + host + ']';
            }
        }
        return host;
    }
    
    private static boolean isInetAddressLiteral(final String addr) {
        boolean sawHex = false;
        boolean sawColon = false;
        for (int i = 0; i < addr.length(); ++i) {
            final char c = addr.charAt(i);
            if (c < '0' || c > '9') {
                if (c != '.') {
                    if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                        sawHex = true;
                    }
                    else {
                        if (c != ':') {
                            return false;
                        }
                        sawColon = true;
                    }
                }
            }
        }
        return !sawHex || sawColon;
    }
    
    public static InternetAddress[] parse(final String addresslist) throws AddressException {
        return parse(addresslist, true);
    }
    
    public static InternetAddress[] parse(final String addresslist, final boolean strict) throws AddressException {
        return parse(addresslist, strict, false);
    }
    
    public static InternetAddress[] parseHeader(final String addresslist, final boolean strict) throws AddressException {
        return parse(MimeUtility.unfold(addresslist), strict, true);
    }
    
    private static InternetAddress[] parse(final String s, final boolean strict, final boolean parseHdr) throws AddressException {
        int start_personal = -1;
        int end_personal = -1;
        final int length = s.length();
        final boolean ignoreErrors = parseHdr && !strict;
        boolean in_group = false;
        boolean route_addr = false;
        boolean rfc822 = false;
        final List<InternetAddress> v = new ArrayList<InternetAddress>();
        int start;
        int end = start = -1;
        for (int index = 0; index < length; ++index) {
            char c = s.charAt(index);
            switch (c) {
                case '(': {
                    rfc822 = true;
                    if (start >= 0 && end == -1) {
                        end = index;
                    }
                    final int pindex = index;
                    ++index;
                    int nesting;
                    for (nesting = 1; index < length && nesting > 0; ++index) {
                        c = s.charAt(index);
                        switch (c) {
                            case '\\': {
                                ++index;
                                break;
                            }
                            case '(': {
                                ++nesting;
                                break;
                            }
                            case ')': {
                                --nesting;
                                break;
                            }
                        }
                    }
                    if (nesting > 0) {
                        if (!ignoreErrors) {
                            throw new AddressException("Missing ')'", s, index);
                        }
                        index = pindex + 1;
                        break;
                    }
                    else {
                        --index;
                        if (start_personal == -1) {
                            start_personal = pindex + 1;
                        }
                        if (end_personal == -1) {
                            end_personal = index;
                            break;
                        }
                        break;
                    }
                    break;
                }
                case ')': {
                    if (!ignoreErrors) {
                        throw new AddressException("Missing '('", s, index);
                    }
                    if (start == -1) {
                        start = index;
                        break;
                    }
                    break;
                }
                case '<': {
                    rfc822 = true;
                    if (route_addr) {
                        if (!ignoreErrors) {
                            throw new AddressException("Extra route-addr", s, index);
                        }
                        if (start == -1) {
                            route_addr = false;
                            rfc822 = false;
                            end = (start = -1);
                            break;
                        }
                        if (!in_group) {
                            if (end == -1) {
                                end = index;
                            }
                            final String addr = s.substring(start, end).trim();
                            final InternetAddress ma = new InternetAddress();
                            ma.setAddress(addr);
                            if (start_personal >= 0) {
                                ma.encodedPersonal = unquote(s.substring(start_personal, end_personal).trim());
                            }
                            v.add(ma);
                            route_addr = false;
                            rfc822 = false;
                            end = (start = -1);
                            end_personal = (start_personal = -1);
                        }
                    }
                    final int rindex = index;
                    boolean inquote = false;
                    ++index;
                Label_0615:
                    while (index < length) {
                        c = s.charAt(index);
                        switch (c) {
                            case '\\': {
                                ++index;
                                break;
                            }
                            case '\"': {
                                inquote = !inquote;
                                break;
                            }
                            case '>': {
                                if (inquote) {
                                    break;
                                }
                                break Label_0615;
                            }
                        }
                        ++index;
                    }
                    if (inquote) {
                        if (!ignoreErrors) {
                            throw new AddressException("Missing '\"'", s, index);
                        }
                        for (index = rindex + 1; index < length; ++index) {
                            c = s.charAt(index);
                            if (c == '\\') {
                                ++index;
                            }
                            else if (c == '>') {
                                break;
                            }
                        }
                    }
                    if (index < length) {
                        if (!in_group) {
                            if (start >= 0) {
                                start_personal = start;
                                end_personal = rindex;
                            }
                            start = rindex + 1;
                        }
                        route_addr = true;
                        end = index;
                        break;
                    }
                    if (!ignoreErrors) {
                        throw new AddressException("Missing '>'", s, index);
                    }
                    index = rindex + 1;
                    if (start == -1) {
                        start = rindex;
                        break;
                    }
                    break;
                }
                case '>': {
                    if (!ignoreErrors) {
                        throw new AddressException("Missing '<'", s, index);
                    }
                    if (start == -1) {
                        start = index;
                        break;
                    }
                    break;
                }
                case '\"': {
                    final int qindex = index;
                    rfc822 = true;
                    if (start == -1) {
                        start = index;
                    }
                    ++index;
                Label_0867:
                    while (index < length) {
                        c = s.charAt(index);
                        switch (c) {
                            case '\\': {
                                ++index;
                                break;
                            }
                            case '\"': {
                                break Label_0867;
                            }
                        }
                        ++index;
                    }
                    if (index < length) {
                        break;
                    }
                    if (!ignoreErrors) {
                        throw new AddressException("Missing '\"'", s, index);
                    }
                    index = qindex + 1;
                    break;
                }
                case '[': {
                    final int lindex = index;
                    rfc822 = true;
                    if (start == -1) {
                        start = index;
                    }
                    ++index;
                Label_0979:
                    while (index < length) {
                        c = s.charAt(index);
                        switch (c) {
                            case '\\': {
                                ++index;
                                break;
                            }
                            case ']': {
                                break Label_0979;
                            }
                        }
                        ++index;
                    }
                    if (index < length) {
                        break;
                    }
                    if (!ignoreErrors) {
                        throw new AddressException("Missing ']'", s, index);
                    }
                    index = lindex + 1;
                    break;
                }
                case ';': {
                    if (start == -1) {
                        route_addr = false;
                        rfc822 = false;
                        end = (start = -1);
                        break;
                    }
                    if (in_group) {
                        in_group = false;
                        if (parseHdr && !strict && index + 1 < length && s.charAt(index + 1) == '@') {
                            break;
                        }
                        final InternetAddress ma = new InternetAddress();
                        end = index + 1;
                        ma.setAddress(s.substring(start, end).trim());
                        v.add(ma);
                        route_addr = false;
                        rfc822 = false;
                        end = (start = -1);
                        end_personal = (start_personal = -1);
                        break;
                    }
                    else if (!ignoreErrors) {
                        throw new AddressException("Illegal semicolon, not in group", s, index);
                    }
                    break;
                }
                case ',': {
                    if (start == -1) {
                        route_addr = false;
                        rfc822 = false;
                        end = (start = -1);
                        break;
                    }
                    if (in_group) {
                        route_addr = false;
                        break;
                    }
                    if (end == -1) {
                        end = index;
                    }
                    String addr2 = s.substring(start, end).trim();
                    String pers = null;
                    if (rfc822 && start_personal >= 0) {
                        pers = unquote(s.substring(start_personal, end_personal).trim());
                        if (pers.trim().length() == 0) {
                            pers = null;
                        }
                    }
                    if (parseHdr && !strict && pers != null && pers.indexOf(64) >= 0 && addr2.indexOf(64) < 0 && addr2.indexOf(33) < 0) {
                        final String tmp = addr2;
                        addr2 = pers;
                        pers = tmp;
                    }
                    if (rfc822 || strict || parseHdr) {
                        if (!ignoreErrors) {
                            checkAddress(addr2, route_addr, false);
                        }
                        final InternetAddress ma = new InternetAddress();
                        ma.setAddress(addr2);
                        if (pers != null) {
                            ma.encodedPersonal = pers;
                        }
                        v.add(ma);
                    }
                    else {
                        final StringTokenizer st = new StringTokenizer(addr2);
                        while (st.hasMoreTokens()) {
                            final String a = st.nextToken();
                            checkAddress(a, false, false);
                            final InternetAddress ma = new InternetAddress();
                            ma.setAddress(a);
                            v.add(ma);
                        }
                    }
                    route_addr = false;
                    rfc822 = false;
                    end = (start = -1);
                    end_personal = (start_personal = -1);
                    break;
                }
                case ':': {
                    rfc822 = true;
                    if (in_group && !ignoreErrors) {
                        throw new AddressException("Nested group", s, index);
                    }
                    if (start == -1) {
                        start = index;
                    }
                    if (parseHdr && !strict) {
                        if (index + 1 < length) {
                            final String addressSpecials = ")>[]:@\\,.";
                            char nc = s.charAt(index + 1);
                            if (addressSpecials.indexOf(nc) >= 0) {
                                if (nc != '@') {
                                    break;
                                }
                                for (int i = index + 2; i < length; ++i) {
                                    nc = s.charAt(i);
                                    if (nc == ';') {
                                        break;
                                    }
                                    if (addressSpecials.indexOf(nc) >= 0) {
                                        break;
                                    }
                                }
                                if (nc == ';') {
                                    break;
                                }
                            }
                        }
                        final String gname = s.substring(start, index);
                        if (InternetAddress.ignoreBogusGroupName && (gname.equalsIgnoreCase("mailto") || gname.equalsIgnoreCase("From") || gname.equalsIgnoreCase("To") || gname.equalsIgnoreCase("Cc") || gname.equalsIgnoreCase("Subject") || gname.equalsIgnoreCase("Re"))) {
                            start = -1;
                        }
                        else {
                            in_group = true;
                        }
                        break;
                    }
                    in_group = true;
                    break;
                }
                case '\t':
                case '\n':
                case '\r':
                case ' ': {
                    break;
                }
                default: {
                    if (start == -1) {
                        start = index;
                        break;
                    }
                    break;
                }
            }
        }
        if (start >= 0) {
            if (end == -1) {
                end = length;
            }
            String addr3 = s.substring(start, end).trim();
            String pers2 = null;
            if (rfc822 && start_personal >= 0) {
                pers2 = unquote(s.substring(start_personal, end_personal).trim());
                if (pers2.trim().length() == 0) {
                    pers2 = null;
                }
            }
            if (parseHdr && !strict && pers2 != null && pers2.indexOf(64) >= 0 && addr3.indexOf(64) < 0 && addr3.indexOf(33) < 0) {
                final String tmp2 = addr3;
                addr3 = pers2;
                pers2 = tmp2;
            }
            if (rfc822 || strict || parseHdr) {
                if (!ignoreErrors) {
                    checkAddress(addr3, route_addr, false);
                }
                final InternetAddress ma = new InternetAddress();
                ma.setAddress(addr3);
                if (pers2 != null) {
                    ma.encodedPersonal = pers2;
                }
                v.add(ma);
            }
            else {
                final StringTokenizer st2 = new StringTokenizer(addr3);
                while (st2.hasMoreTokens()) {
                    final String a2 = st2.nextToken();
                    checkAddress(a2, false, false);
                    final InternetAddress ma = new InternetAddress();
                    ma.setAddress(a2);
                    v.add(ma);
                }
            }
        }
        final InternetAddress[] a3 = new InternetAddress[v.size()];
        v.toArray(a3);
        return a3;
    }
    
    public void validate() throws AddressException {
        if (this.isGroup()) {
            this.getGroup(true);
        }
        else {
            checkAddress(this.getAddress(), true, true);
        }
    }
    
    private static void checkAddress(final String addr, final boolean routeAddr, final boolean validate) throws AddressException {
        int start = 0;
        if (addr == null) {
            throw new AddressException("Address is null");
        }
        final int len = addr.length();
        if (len == 0) {
            throw new AddressException("Empty address", addr);
        }
        if (routeAddr && addr.charAt(0) == '@') {
            int i;
            for (start = 0; (i = indexOfAny(addr, ",:", start)) >= 0; start = i + 1) {
                if (addr.charAt(start) != '@') {
                    throw new AddressException("Illegal route-addr", addr);
                }
                if (addr.charAt(i) == ':') {
                    start = i + 1;
                    break;
                }
            }
        }
        char c = '\uffff';
        char lastc = '\uffff';
        boolean inquote = false;
        int i;
        for (i = start; i < len; ++i) {
            lastc = c;
            c = addr.charAt(i);
            if (c != '\\') {
                if (lastc != '\\') {
                    if (c == '\"') {
                        if (inquote) {
                            if (validate && i + 1 < len && addr.charAt(i + 1) != '@') {
                                throw new AddressException("Quote not at end of local address", addr);
                            }
                            inquote = false;
                        }
                        else {
                            if (validate && i != 0) {
                                throw new AddressException("Quote not at start of local address", addr);
                            }
                            inquote = true;
                        }
                    }
                    else {
                        if (c == '\r') {
                            if (i + 1 < len && addr.charAt(i + 1) != '\n') {
                                throw new AddressException("Quoted local address contains CR without LF", addr);
                            }
                        }
                        else if (c == '\n') {
                            if (i + 1 < len && addr.charAt(i + 1) != ' ' && addr.charAt(i + 1) != '\t') {
                                throw new AddressException("Quoted local address contains newline without whitespace", addr);
                            }
                        }
                        else if (c == '.') {
                            if (i == start) {
                                throw new AddressException("Local address starts with dot", addr);
                            }
                            if (lastc == '.') {
                                throw new AddressException("Local address contains dot-dot", addr);
                            }
                        }
                        if (!inquote) {
                            if (c == '@') {
                                if (i == 0) {
                                    throw new AddressException("Missing local name", addr);
                                }
                                if (lastc == '.') {
                                    throw new AddressException("Local address ends with dot", addr);
                                }
                                break;
                            }
                            else {
                                if (c <= ' ' || c == '\u007f') {
                                    throw new AddressException("Local address contains control or whitespace", addr);
                                }
                                if ("()<>,;:\\\"[]@".indexOf(c) >= 0) {
                                    throw new AddressException("Local address contains illegal character", addr);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (inquote) {
            throw new AddressException("Unterminated quote", addr);
        }
        if (c != '@') {
            if (validate) {
                throw new AddressException("Missing final '@domain'", addr);
            }
        }
        else {
            start = i + 1;
            if (start >= len) {
                throw new AddressException("Missing domain", addr);
            }
            if (addr.charAt(start) == '.') {
                throw new AddressException("Domain starts with dot", addr);
            }
            boolean inliteral = false;
            for (i = start; i < len; ++i) {
                c = addr.charAt(i);
                if (c == '[') {
                    if (i != start) {
                        throw new AddressException("Domain literal not at start of domain", addr);
                    }
                    inliteral = true;
                }
                else if (c == ']') {
                    if (i != len - 1) {
                        throw new AddressException("Domain literal end not at end of domain", addr);
                    }
                    inliteral = false;
                }
                else {
                    if (c <= ' ' || c == '\u007f') {
                        throw new AddressException("Domain contains control or whitespace", addr);
                    }
                    if (!inliteral) {
                        if (!Character.isLetterOrDigit(c) && c != '-' && c != '.') {
                            throw new AddressException("Domain contains illegal character", addr);
                        }
                        if (c == '.' && lastc == '.') {
                            throw new AddressException("Domain contains dot-dot", addr);
                        }
                    }
                }
                lastc = c;
            }
            if (lastc == '.') {
                throw new AddressException("Domain ends with dot", addr);
            }
        }
    }
    
    private boolean isSimple() {
        return this.address == null || indexOfAny(this.address, "()<>,;:\\\"[]") < 0;
    }
    
    public boolean isGroup() {
        return this.address != null && this.address.endsWith(";") && this.address.indexOf(58) > 0;
    }
    
    public InternetAddress[] getGroup(final boolean strict) throws AddressException {
        final String addr = this.getAddress();
        if (addr == null) {
            return null;
        }
        if (!addr.endsWith(";")) {
            return null;
        }
        final int ix = addr.indexOf(58);
        if (ix < 0) {
            return null;
        }
        final String list = addr.substring(ix + 1, addr.length() - 1);
        return parseHeader(list, strict);
    }
    
    private static int indexOfAny(final String s, final String any) {
        return indexOfAny(s, any, 0);
    }
    
    private static int indexOfAny(final String s, final String any, final int start) {
        try {
            for (int len = s.length(), i = start; i < len; ++i) {
                if (any.indexOf(s.charAt(i)) >= 0) {
                    return i;
                }
            }
            return -1;
        }
        catch (final StringIndexOutOfBoundsException e) {
            return -1;
        }
    }
    
    static {
        ignoreBogusGroupName = PropUtil.getBooleanSystemProperty("mail.mime.address.ignorebogusgroupname", true);
        useCanonicalHostName = PropUtil.getBooleanSystemProperty("mail.mime.address.usecanonicalhostname", true);
        allowUtf8 = PropUtil.getBooleanSystemProperty("mail.mime.allowutf8", false);
        rfc822phrase = "()<>@,;:\\\"\t .[]".replace(' ', '\0').replace('\t', '\0');
    }
}
