package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

import java.util.Map;
import java.util.Iterator;
import java.util.HashMap;

public final class ParameterList
{
    private final HashMap list;
    
    public ParameterList() {
        this.list = new HashMap();
    }
    
    private ParameterList(final HashMap m) {
        this.list = m;
    }
    
    public ParameterList(final String s) throws ParseException {
        final HeaderTokenizer h = new HeaderTokenizer(s, "()<>@,;:\\\"\t []/?=");
        this.list = new HashMap();
        while (true) {
            HeaderTokenizer.Token tk = h.next();
            int type = tk.getType();
            if (type == -4) {
                return;
            }
            if ((char)type != ';') {
                throw new ParseException();
            }
            tk = h.next();
            if (tk.getType() == -4) {
                return;
            }
            if (tk.getType() != -1) {
                throw new ParseException();
            }
            final String name = tk.getValue().toLowerCase();
            tk = h.next();
            if ((char)tk.getType() != '=') {
                throw new ParseException();
            }
            tk = h.next();
            type = tk.getType();
            if (type != -1 && type != -2) {
                throw new ParseException();
            }
            this.list.put(name, tk.getValue());
        }
    }
    
    public int size() {
        return this.list.size();
    }
    
    public String get(final String name) {
        return this.list.get(name.trim().toLowerCase());
    }
    
    public void set(final String name, final String value) {
        this.list.put(name.trim().toLowerCase(), value);
    }
    
    public void remove(final String name) {
        this.list.remove(name.trim().toLowerCase());
    }
    
    public Iterator getNames() {
        return this.list.keySet().iterator();
    }
    
    @Override
    public String toString() {
        return this.toString(0);
    }
    
    public String toString(int used) {
        final StringBuffer sb = new StringBuffer();
        for (final Map.Entry e : this.list.entrySet()) {
            final String name = e.getKey();
            final String value = this.quote(e.getValue());
            sb.append("; ");
            used += 2;
            final int len = name.length() + value.length() + 1;
            if (used + len > 76) {
                sb.append("\r\n\t");
                used = 8;
            }
            sb.append(name).append('=');
            used += name.length() + 1;
            if (used + value.length() > 76) {
                final String s = MimeUtility.fold(used, value);
                sb.append(s);
                final int lastlf = s.lastIndexOf(10);
                if (lastlf >= 0) {
                    used += s.length() - lastlf - 1;
                }
                else {
                    used += s.length();
                }
            }
            else {
                sb.append(value);
                used += value.length();
            }
        }
        return sb.toString();
    }
    
    private String quote(final String value) {
        if ("".equals(value)) {
            return "\"\"";
        }
        return MimeUtility.quote(value, "()<>@,;:\\\"\t []/?=");
    }
    
    public ParameterList copy() {
        return new ParameterList((HashMap)this.list.clone());
    }
}
