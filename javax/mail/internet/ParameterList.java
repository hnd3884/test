package javax.mail.internet;

import java.util.ArrayList;
import com.sun.mail.util.PropUtil;
import java.util.Enumeration;
import java.util.Iterator;
import java.io.IOException;
import com.sun.mail.util.ASCIIUtility;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Map;

public class ParameterList
{
    private Map<String, Object> list;
    private Set<String> multisegmentNames;
    private Map<String, Object> slist;
    private String lastName;
    private static final boolean encodeParameters;
    private static final boolean decodeParameters;
    private static final boolean decodeParametersStrict;
    private static final boolean applehack;
    private static final boolean windowshack;
    private static final boolean parametersStrict;
    private static final boolean splitLongParameters;
    private static final char[] hex;
    
    public ParameterList() {
        this.list = new LinkedHashMap<String, Object>();
        this.lastName = null;
        if (ParameterList.decodeParameters) {
            this.multisegmentNames = new HashSet<String>();
            this.slist = new HashMap<String, Object>();
        }
    }
    
    public ParameterList(final String s) throws ParseException {
        this();
        final HeaderTokenizer h = new HeaderTokenizer(s, "()<>@,;:\\\"\t []/?=");
        while (true) {
            HeaderTokenizer.Token tk = h.next();
            int type = tk.getType();
            if (type == -4) {
                break;
            }
            if ((char)type == ';') {
                tk = h.next();
                if (tk.getType() == -4) {
                    break;
                }
                if (tk.getType() != -1) {
                    throw new ParseException("In parameter list <" + s + ">, expected parameter name, got \"" + tk.getValue() + "\"");
                }
                final String name = tk.getValue().toLowerCase(Locale.ENGLISH);
                tk = h.next();
                if ((char)tk.getType() != '=') {
                    throw new ParseException("In parameter list <" + s + ">, expected '=', got \"" + tk.getValue() + "\"");
                }
                if (ParameterList.windowshack && (name.equals("name") || name.equals("filename"))) {
                    tk = h.next(';', true);
                }
                else if (ParameterList.parametersStrict) {
                    tk = h.next();
                }
                else {
                    tk = h.next(';');
                }
                type = tk.getType();
                if (type != -1 && type != -2) {
                    throw new ParseException("In parameter list <" + s + ">, expected parameter value, got \"" + tk.getValue() + "\"");
                }
                final String value = tk.getValue();
                this.lastName = name;
                if (ParameterList.decodeParameters) {
                    this.putEncodedName(name, value);
                }
                else {
                    this.list.put(name, value);
                }
            }
            else {
                if (type != -1 || this.lastName == null || ((!ParameterList.applehack || (!this.lastName.equals("name") && !this.lastName.equals("filename"))) && ParameterList.parametersStrict)) {
                    throw new ParseException("In parameter list <" + s + ">, expected ';', got \"" + tk.getValue() + "\"");
                }
                final String lastValue = this.list.get(this.lastName);
                final String value = lastValue + " " + tk.getValue();
                this.list.put(this.lastName, value);
            }
        }
        if (ParameterList.decodeParameters) {
            this.combineMultisegmentNames(false);
        }
    }
    
    public void combineSegments() {
        if (ParameterList.decodeParameters && this.multisegmentNames.size() > 0) {
            try {
                this.combineMultisegmentNames(true);
            }
            catch (final ParseException ex) {}
        }
    }
    
    private void putEncodedName(String name, final String value) throws ParseException {
        final int star = name.indexOf(42);
        if (star < 0) {
            this.list.put(name, value);
        }
        else if (star == name.length() - 1) {
            name = name.substring(0, star);
            final Value v = extractCharset(value);
            try {
                v.value = decodeBytes(v.value, v.charset);
            }
            catch (final UnsupportedEncodingException ex) {
                if (ParameterList.decodeParametersStrict) {
                    throw new ParseException(ex.toString());
                }
            }
            this.list.put(name, v);
        }
        else {
            final String rname = name.substring(0, star);
            this.multisegmentNames.add(rname);
            this.list.put(rname, "");
            Object v2;
            if (name.endsWith("*")) {
                if (name.endsWith("*0*")) {
                    v2 = extractCharset(value);
                }
                else {
                    v2 = new Value();
                    ((Value)v2).encodedValue = value;
                    ((Value)v2).value = value;
                }
                name = name.substring(0, name.length() - 1);
            }
            else {
                v2 = value;
            }
            this.slist.put(name, v2);
        }
    }
    
    private void combineMultisegmentNames(final boolean keepConsistentOnFailure) throws ParseException {
        boolean success = false;
        try {
            for (final String name : this.multisegmentNames) {
                final MultiValue mv = new MultiValue();
                String charset = null;
                final ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int segment = 0;
                while (true) {
                    final String sname = name + "*" + segment;
                    final Object v = this.slist.get(sname);
                    if (v == null) {
                        break;
                    }
                    mv.add(v);
                    try {
                        if (v instanceof Value) {
                            final Value vv = (Value)v;
                            if (segment == 0) {
                                charset = vv.charset;
                            }
                            else if (charset == null) {
                                this.multisegmentNames.remove(name);
                                break;
                            }
                            decodeBytes(vv.value, bos);
                        }
                        else {
                            bos.write(ASCIIUtility.getBytes((String)v));
                        }
                    }
                    catch (final IOException ex2) {}
                    this.slist.remove(sname);
                    ++segment;
                }
                if (segment == 0) {
                    this.list.remove(name);
                }
                else {
                    try {
                        if (charset != null) {
                            charset = MimeUtility.javaCharset(charset);
                        }
                        if (charset == null || charset.length() == 0) {
                            charset = MimeUtility.getDefaultJavaCharset();
                        }
                        if (charset != null) {
                            mv.value = bos.toString(charset);
                        }
                        else {
                            mv.value = bos.toString();
                        }
                    }
                    catch (final UnsupportedEncodingException uex) {
                        if (ParameterList.decodeParametersStrict) {
                            throw new ParseException(uex.toString());
                        }
                        try {
                            mv.value = bos.toString("iso-8859-1");
                        }
                        catch (final UnsupportedEncodingException ex3) {}
                    }
                    this.list.put(name, mv);
                }
            }
            success = true;
        }
        finally {
            if (keepConsistentOnFailure || success) {
                if (this.slist.size() > 0) {
                    for (final Object v2 : this.slist.values()) {
                        if (v2 instanceof Value) {
                            final Value vv2 = (Value)v2;
                            try {
                                vv2.value = decodeBytes(vv2.value, vv2.charset);
                            }
                            catch (final UnsupportedEncodingException ex) {
                                if (ParameterList.decodeParametersStrict) {
                                    throw new ParseException(ex.toString());
                                }
                                continue;
                            }
                        }
                    }
                    this.list.putAll(this.slist);
                }
                this.multisegmentNames.clear();
                this.slist.clear();
            }
        }
    }
    
    public int size() {
        return this.list.size();
    }
    
    public String get(final String name) {
        final Object v = this.list.get(name.trim().toLowerCase(Locale.ENGLISH));
        String value;
        if (v instanceof MultiValue) {
            value = ((MultiValue)v).value;
        }
        else if (v instanceof LiteralValue) {
            value = ((LiteralValue)v).value;
        }
        else if (v instanceof Value) {
            value = ((Value)v).value;
        }
        else {
            value = (String)v;
        }
        return value;
    }
    
    public void set(String name, final String value) {
        name = name.trim().toLowerCase(Locale.ENGLISH);
        if (ParameterList.decodeParameters) {
            try {
                this.putEncodedName(name, value);
            }
            catch (final ParseException pex) {
                this.list.put(name, value);
            }
        }
        else {
            this.list.put(name, value);
        }
    }
    
    public void set(final String name, final String value, final String charset) {
        if (ParameterList.encodeParameters) {
            final Value ev = encodeValue(value, charset);
            if (ev != null) {
                this.list.put(name.trim().toLowerCase(Locale.ENGLISH), ev);
            }
            else {
                this.set(name, value);
            }
        }
        else {
            this.set(name, value);
        }
    }
    
    void setLiteral(final String name, final String value) {
        final LiteralValue lv = new LiteralValue();
        lv.value = value;
        this.list.put(name, lv);
    }
    
    public void remove(final String name) {
        this.list.remove(name.trim().toLowerCase(Locale.ENGLISH));
    }
    
    public Enumeration<String> getNames() {
        return new ParamEnum(this.list.keySet().iterator());
    }
    
    @Override
    public String toString() {
        return this.toString(0);
    }
    
    public String toString(final int used) {
        final ToStringBuffer sb = new ToStringBuffer(used);
        for (final Map.Entry<String, Object> ent : this.list.entrySet()) {
            String name = ent.getKey();
            final Object v = ent.getValue();
            if (v instanceof MultiValue) {
                final MultiValue vv = (MultiValue)v;
                name += "*";
                for (int i = 0; i < vv.size(); ++i) {
                    final Object va = vv.get(i);
                    String ns;
                    String value;
                    if (va instanceof Value) {
                        ns = name + i + "*";
                        value = ((Value)va).encodedValue;
                    }
                    else {
                        ns = name + i;
                        value = (String)va;
                    }
                    sb.addNV(ns, quote(value));
                }
            }
            else if (v instanceof LiteralValue) {
                final String value = ((LiteralValue)v).value;
                sb.addNV(name, quote(value));
            }
            else if (v instanceof Value) {
                name += "*";
                final String value = ((Value)v).encodedValue;
                sb.addNV(name, quote(value));
            }
            else {
                String value = (String)v;
                if (value.length() > 60 && ParameterList.splitLongParameters && ParameterList.encodeParameters) {
                    int seg = 0;
                    name += "*";
                    while (value.length() > 60) {
                        sb.addNV(name + seg, quote(value.substring(0, 60)));
                        value = value.substring(60);
                        ++seg;
                    }
                    if (value.length() <= 0) {
                        continue;
                    }
                    sb.addNV(name + seg, quote(value));
                }
                else {
                    sb.addNV(name, quote(value));
                }
            }
        }
        return sb.toString();
    }
    
    private static String quote(final String value) {
        return MimeUtility.quote(value, "()<>@,;:\\\"\t []/?=");
    }
    
    private static Value encodeValue(final String value, final String charset) {
        if (MimeUtility.checkAscii(value) == 1) {
            return null;
        }
        byte[] b;
        try {
            b = value.getBytes(MimeUtility.javaCharset(charset));
        }
        catch (final UnsupportedEncodingException ex) {
            return null;
        }
        final StringBuffer sb = new StringBuffer(b.length + charset.length() + 2);
        sb.append(charset).append("''");
        for (int i = 0; i < b.length; ++i) {
            final char c = (char)(b[i] & 0xFF);
            if (c <= ' ' || c >= '\u007f' || c == '*' || c == '\'' || c == '%' || "()<>@,;:\\\"\t []/?=".indexOf(c) >= 0) {
                sb.append('%').append(ParameterList.hex[c >> 4]).append(ParameterList.hex[c & '\u000f']);
            }
            else {
                sb.append(c);
            }
        }
        final Value v = new Value();
        v.charset = charset;
        v.value = value;
        v.encodedValue = sb.toString();
        return v;
    }
    
    private static Value extractCharset(final String value) throws ParseException {
        final Value value2;
        final Value v = value2 = new Value();
        v.encodedValue = value;
        value2.value = value;
        try {
            final int i = value.indexOf(39);
            if (i < 0) {
                if (ParameterList.decodeParametersStrict) {
                    throw new ParseException("Missing charset in encoded value: " + value);
                }
                return v;
            }
            else {
                final String charset = value.substring(0, i);
                final int li = value.indexOf(39, i + 1);
                if (li < 0) {
                    if (ParameterList.decodeParametersStrict) {
                        throw new ParseException("Missing language in encoded value: " + value);
                    }
                    return v;
                }
                else {
                    v.value = value.substring(li + 1);
                    v.charset = charset;
                }
            }
        }
        catch (final NumberFormatException nex) {
            if (ParameterList.decodeParametersStrict) {
                throw new ParseException(nex.toString());
            }
        }
        catch (final StringIndexOutOfBoundsException ex) {
            if (ParameterList.decodeParametersStrict) {
                throw new ParseException(ex.toString());
            }
        }
        return v;
    }
    
    private static String decodeBytes(final String value, String charset) throws ParseException, UnsupportedEncodingException {
        final byte[] b = new byte[value.length()];
        int i = 0;
        int bi = 0;
        while (i < value.length()) {
            char c = value.charAt(i);
            if (c == '%') {
                try {
                    final String hex = value.substring(i + 1, i + 3);
                    c = (char)Integer.parseInt(hex, 16);
                    i += 2;
                }
                catch (final NumberFormatException ex) {
                    if (ParameterList.decodeParametersStrict) {
                        throw new ParseException(ex.toString());
                    }
                }
                catch (final StringIndexOutOfBoundsException ex2) {
                    if (ParameterList.decodeParametersStrict) {
                        throw new ParseException(ex2.toString());
                    }
                }
            }
            b[bi++] = (byte)c;
            ++i;
        }
        if (charset != null) {
            charset = MimeUtility.javaCharset(charset);
        }
        if (charset == null || charset.length() == 0) {
            charset = MimeUtility.getDefaultJavaCharset();
        }
        return new String(b, 0, bi, charset);
    }
    
    private static void decodeBytes(final String value, final OutputStream os) throws ParseException, IOException {
        for (int i = 0; i < value.length(); ++i) {
            char c = value.charAt(i);
            if (c == '%') {
                try {
                    final String hex = value.substring(i + 1, i + 3);
                    c = (char)Integer.parseInt(hex, 16);
                    i += 2;
                }
                catch (final NumberFormatException ex) {
                    if (ParameterList.decodeParametersStrict) {
                        throw new ParseException(ex.toString());
                    }
                }
                catch (final StringIndexOutOfBoundsException ex2) {
                    if (ParameterList.decodeParametersStrict) {
                        throw new ParseException(ex2.toString());
                    }
                }
            }
            os.write((byte)c);
        }
    }
    
    static {
        encodeParameters = PropUtil.getBooleanSystemProperty("mail.mime.encodeparameters", true);
        decodeParameters = PropUtil.getBooleanSystemProperty("mail.mime.decodeparameters", true);
        decodeParametersStrict = PropUtil.getBooleanSystemProperty("mail.mime.decodeparameters.strict", false);
        applehack = PropUtil.getBooleanSystemProperty("mail.mime.applefilenames", false);
        windowshack = PropUtil.getBooleanSystemProperty("mail.mime.windowsfilenames", false);
        parametersStrict = PropUtil.getBooleanSystemProperty("mail.mime.parameters.strict", true);
        splitLongParameters = PropUtil.getBooleanSystemProperty("mail.mime.splitlongparameters", true);
        hex = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    }
    
    private static class Value
    {
        String value;
        String charset;
        String encodedValue;
    }
    
    private static class LiteralValue
    {
        String value;
    }
    
    private static class MultiValue extends ArrayList<Object>
    {
        private static final long serialVersionUID = 699561094618751023L;
        String value;
    }
    
    private static class ParamEnum implements Enumeration<String>
    {
        private Iterator<String> it;
        
        ParamEnum(final Iterator<String> it) {
            this.it = it;
        }
        
        @Override
        public boolean hasMoreElements() {
            return this.it.hasNext();
        }
        
        @Override
        public String nextElement() {
            return this.it.next();
        }
    }
    
    private static class ToStringBuffer
    {
        private int used;
        private StringBuilder sb;
        
        public ToStringBuffer(final int used) {
            this.sb = new StringBuilder();
            this.used = used;
        }
        
        public void addNV(final String name, final String value) {
            this.sb.append("; ");
            this.used += 2;
            final int len = name.length() + value.length() + 1;
            if (this.used + len > 76) {
                this.sb.append("\r\n\t");
                this.used = 8;
            }
            this.sb.append(name).append('=');
            this.used += name.length() + 1;
            if (this.used + value.length() > 76) {
                final String s = MimeUtility.fold(this.used, value);
                this.sb.append(s);
                final int lastlf = s.lastIndexOf(10);
                if (lastlf >= 0) {
                    this.used += s.length() - lastlf - 1;
                }
                else {
                    this.used += s.length();
                }
            }
            else {
                this.sb.append(value);
                this.used += value.length();
            }
        }
        
        @Override
        public String toString() {
            return this.sb.toString();
        }
    }
}
