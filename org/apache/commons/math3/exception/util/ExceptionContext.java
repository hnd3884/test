package org.apache.commons.math3.exception.util;

import java.util.Iterator;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.io.Serializable;

public class ExceptionContext implements Serializable
{
    private static final long serialVersionUID = -6024911025449780478L;
    private Throwable throwable;
    private List<Localizable> msgPatterns;
    private List<Object[]> msgArguments;
    private Map<String, Object> context;
    
    public ExceptionContext(final Throwable throwable) {
        this.throwable = throwable;
        this.msgPatterns = new ArrayList<Localizable>();
        this.msgArguments = new ArrayList<Object[]>();
        this.context = new HashMap<String, Object>();
    }
    
    public Throwable getThrowable() {
        return this.throwable;
    }
    
    public void addMessage(final Localizable pattern, final Object... arguments) {
        this.msgPatterns.add(pattern);
        this.msgArguments.add(ArgUtils.flatten(arguments));
    }
    
    public void setValue(final String key, final Object value) {
        this.context.put(key, value);
    }
    
    public Object getValue(final String key) {
        return this.context.get(key);
    }
    
    public Set<String> getKeys() {
        return this.context.keySet();
    }
    
    public String getMessage() {
        return this.getMessage(Locale.US);
    }
    
    public String getLocalizedMessage() {
        return this.getMessage(Locale.getDefault());
    }
    
    public String getMessage(final Locale locale) {
        return this.buildMessage(locale, ": ");
    }
    
    public String getMessage(final Locale locale, final String separator) {
        return this.buildMessage(locale, separator);
    }
    
    private String buildMessage(final Locale locale, final String separator) {
        final StringBuilder sb = new StringBuilder();
        int count = 0;
        for (int len = this.msgPatterns.size(), i = 0; i < len; ++i) {
            final Localizable pat = this.msgPatterns.get(i);
            final Object[] args = this.msgArguments.get(i);
            final MessageFormat fmt = new MessageFormat(pat.getLocalizedString(locale), locale);
            sb.append(fmt.format(args));
            if (++count < len) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.writeObject(this.throwable);
        this.serializeMessages(out);
        this.serializeContext(out);
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.throwable = (Throwable)in.readObject();
        this.deSerializeMessages(in);
        this.deSerializeContext(in);
    }
    
    private void serializeMessages(final ObjectOutputStream out) throws IOException {
        final int len = this.msgPatterns.size();
        out.writeInt(len);
        for (int i = 0; i < len; ++i) {
            final Localizable pat = this.msgPatterns.get(i);
            out.writeObject(pat);
            final Object[] args = this.msgArguments.get(i);
            final int aLen = args.length;
            out.writeInt(aLen);
            for (int j = 0; j < aLen; ++j) {
                if (args[j] instanceof Serializable) {
                    out.writeObject(args[j]);
                }
                else {
                    out.writeObject(this.nonSerializableReplacement(args[j]));
                }
            }
        }
    }
    
    private void deSerializeMessages(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        final int len = in.readInt();
        this.msgPatterns = new ArrayList<Localizable>(len);
        this.msgArguments = new ArrayList<Object[]>(len);
        for (int i = 0; i < len; ++i) {
            final Localizable pat = (Localizable)in.readObject();
            this.msgPatterns.add(pat);
            final int aLen = in.readInt();
            final Object[] args = new Object[aLen];
            for (int j = 0; j < aLen; ++j) {
                args[j] = in.readObject();
            }
            this.msgArguments.add(args);
        }
    }
    
    private void serializeContext(final ObjectOutputStream out) throws IOException {
        final int len = this.context.size();
        out.writeInt(len);
        for (final Map.Entry<String, Object> entry : this.context.entrySet()) {
            out.writeObject(entry.getKey());
            final Object value = entry.getValue();
            if (value instanceof Serializable) {
                out.writeObject(value);
            }
            else {
                out.writeObject(this.nonSerializableReplacement(value));
            }
        }
    }
    
    private void deSerializeContext(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        final int len = in.readInt();
        this.context = new HashMap<String, Object>();
        for (int i = 0; i < len; ++i) {
            final String key = (String)in.readObject();
            final Object value = in.readObject();
            this.context.put(key, value);
        }
    }
    
    private String nonSerializableReplacement(final Object obj) {
        return "[Object could not be serialized: " + obj.getClass().getName() + "]";
    }
}
