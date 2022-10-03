package org.apache.lucene.queryparser.flexible.messages;

import java.util.Locale;

public class MessageImpl implements Message
{
    private String key;
    private Object[] arguments;
    
    public MessageImpl(final String key) {
        this.arguments = new Object[0];
        this.key = key;
    }
    
    public MessageImpl(final String key, final Object... args) {
        this(key);
        this.arguments = args;
    }
    
    @Override
    public Object[] getArguments() {
        return this.arguments;
    }
    
    @Override
    public String getKey() {
        return this.key;
    }
    
    @Override
    public String getLocalizedMessage() {
        return this.getLocalizedMessage(Locale.getDefault());
    }
    
    @Override
    public String getLocalizedMessage(final Locale locale) {
        return NLS.getLocalizedMessage(this.getKey(), locale, this.getArguments());
    }
    
    @Override
    public String toString() {
        final Object[] args = this.getArguments();
        final StringBuilder sb = new StringBuilder(this.getKey());
        if (args != null) {
            for (int i = 0; i < args.length; ++i) {
                sb.append((i == 0) ? " " : ", ").append(args[i]);
            }
        }
        return sb.toString();
    }
}
