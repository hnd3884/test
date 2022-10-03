package org.bouncycastle.i18n;

import org.bouncycastle.i18n.filter.UntrustedUrlInput;
import org.bouncycastle.i18n.filter.UntrustedInput;
import org.bouncycastle.i18n.filter.TrustedInput;
import java.text.Format;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.Locale;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import org.bouncycastle.i18n.filter.Filter;

public class LocalizedMessage
{
    protected final String id;
    protected final String resource;
    public static final String DEFAULT_ENCODING = "ISO-8859-1";
    protected String encoding;
    protected FilteredArguments arguments;
    protected FilteredArguments extraArgs;
    protected Filter filter;
    protected ClassLoader loader;
    
    public LocalizedMessage(final String resource, final String id) throws NullPointerException {
        this.encoding = "ISO-8859-1";
        this.extraArgs = null;
        this.filter = null;
        this.loader = null;
        if (resource == null || id == null) {
            throw new NullPointerException();
        }
        this.id = id;
        this.resource = resource;
        this.arguments = new FilteredArguments();
    }
    
    public LocalizedMessage(final String resource, final String id, final String encoding) throws NullPointerException, UnsupportedEncodingException {
        this.encoding = "ISO-8859-1";
        this.extraArgs = null;
        this.filter = null;
        this.loader = null;
        if (resource == null || id == null) {
            throw new NullPointerException();
        }
        this.id = id;
        this.resource = resource;
        this.arguments = new FilteredArguments();
        if (!Charset.isSupported(encoding)) {
            throw new UnsupportedEncodingException("The encoding \"" + encoding + "\" is not supported.");
        }
        this.encoding = encoding;
    }
    
    public LocalizedMessage(final String resource, final String id, final Object[] array) throws NullPointerException {
        this.encoding = "ISO-8859-1";
        this.extraArgs = null;
        this.filter = null;
        this.loader = null;
        if (resource == null || id == null || array == null) {
            throw new NullPointerException();
        }
        this.id = id;
        this.resource = resource;
        this.arguments = new FilteredArguments(array);
    }
    
    public LocalizedMessage(final String resource, final String id, final String encoding, final Object[] array) throws NullPointerException, UnsupportedEncodingException {
        this.encoding = "ISO-8859-1";
        this.extraArgs = null;
        this.filter = null;
        this.loader = null;
        if (resource == null || id == null || array == null) {
            throw new NullPointerException();
        }
        this.id = id;
        this.resource = resource;
        this.arguments = new FilteredArguments(array);
        if (!Charset.isSupported(encoding)) {
            throw new UnsupportedEncodingException("The encoding \"" + encoding + "\" is not supported.");
        }
        this.encoding = encoding;
    }
    
    public String getEntry(final String s, final Locale locale, final TimeZone timeZone) throws MissingEntryException {
        String s2 = this.id;
        if (s != null) {
            s2 = s2 + "." + s;
        }
        try {
            ResourceBundle resourceBundle;
            if (this.loader == null) {
                resourceBundle = ResourceBundle.getBundle(this.resource, locale);
            }
            else {
                resourceBundle = ResourceBundle.getBundle(this.resource, locale, this.loader);
            }
            String s3 = resourceBundle.getString(s2);
            if (!this.encoding.equals("ISO-8859-1")) {
                s3 = new String(s3.getBytes("ISO-8859-1"), this.encoding);
            }
            if (!this.arguments.isEmpty()) {
                s3 = this.formatWithTimeZone(s3, this.arguments.getFilteredArgs(locale), locale, timeZone);
            }
            return this.addExtraArgs(s3, locale);
        }
        catch (final MissingResourceException ex) {
            throw new MissingEntryException("Can't find entry " + s2 + " in resource file " + this.resource + ".", this.resource, s2, locale, (this.loader != null) ? this.loader : this.getClassLoader());
        }
        catch (final UnsupportedEncodingException ex2) {
            throw new RuntimeException(ex2);
        }
    }
    
    protected String formatWithTimeZone(final String s, final Object[] array, final Locale locale, final TimeZone timeZone) {
        final MessageFormat messageFormat = new MessageFormat(" ");
        messageFormat.setLocale(locale);
        messageFormat.applyPattern(s);
        if (!timeZone.equals(TimeZone.getDefault())) {
            final Format[] formats = messageFormat.getFormats();
            for (int i = 0; i < formats.length; ++i) {
                if (formats[i] instanceof DateFormat) {
                    final DateFormat dateFormat = (DateFormat)formats[i];
                    dateFormat.setTimeZone(timeZone);
                    messageFormat.setFormat(i, dateFormat);
                }
            }
        }
        return messageFormat.format(array);
    }
    
    protected String addExtraArgs(String string, final Locale locale) {
        if (this.extraArgs != null) {
            final StringBuffer sb = new StringBuffer(string);
            final Object[] filteredArgs = this.extraArgs.getFilteredArgs(locale);
            for (int i = 0; i < filteredArgs.length; ++i) {
                sb.append(filteredArgs[i]);
            }
            string = sb.toString();
        }
        return string;
    }
    
    public void setFilter(final Filter filter) {
        this.arguments.setFilter(filter);
        if (this.extraArgs != null) {
            this.extraArgs.setFilter(filter);
        }
        this.filter = filter;
    }
    
    public Filter getFilter() {
        return this.filter;
    }
    
    public void setClassLoader(final ClassLoader loader) {
        this.loader = loader;
    }
    
    public ClassLoader getClassLoader() {
        return this.loader;
    }
    
    public String getId() {
        return this.id;
    }
    
    public String getResource() {
        return this.resource;
    }
    
    public Object[] getArguments() {
        return this.arguments.getArguments();
    }
    
    public void setExtraArgument(final Object o) {
        this.setExtraArguments(new Object[] { o });
    }
    
    public void setExtraArguments(final Object[] array) {
        if (array != null) {
            (this.extraArgs = new FilteredArguments(array)).setFilter(this.filter);
        }
        else {
            this.extraArgs = null;
        }
    }
    
    public Object[] getExtraArgs() {
        return (Object[])((this.extraArgs == null) ? null : this.extraArgs.getArguments());
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("Resource: \"").append(this.resource);
        sb.append("\" Id: \"").append(this.id).append("\"");
        sb.append(" Arguments: ").append(this.arguments.getArguments().length).append(" normal");
        if (this.extraArgs != null && this.extraArgs.getArguments().length > 0) {
            sb.append(", ").append(this.extraArgs.getArguments().length).append(" extra");
        }
        sb.append(" Encoding: ").append(this.encoding);
        sb.append(" ClassLoader: ").append(this.loader);
        return sb.toString();
    }
    
    protected class FilteredArguments
    {
        protected static final int NO_FILTER = 0;
        protected static final int FILTER = 1;
        protected static final int FILTER_URL = 2;
        protected Filter filter;
        protected boolean[] isLocaleSpecific;
        protected int[] argFilterType;
        protected Object[] arguments;
        protected Object[] unpackedArgs;
        protected Object[] filteredArgs;
        
        FilteredArguments(final LocalizedMessage localizedMessage) {
            this(localizedMessage, new Object[0]);
        }
        
        FilteredArguments(final Object[] arguments) {
            this.filter = null;
            this.arguments = arguments;
            this.unpackedArgs = new Object[arguments.length];
            this.filteredArgs = new Object[arguments.length];
            this.isLocaleSpecific = new boolean[arguments.length];
            this.argFilterType = new int[arguments.length];
            for (int i = 0; i < arguments.length; ++i) {
                if (arguments[i] instanceof TrustedInput) {
                    this.unpackedArgs[i] = ((TrustedInput)arguments[i]).getInput();
                    this.argFilterType[i] = 0;
                }
                else if (arguments[i] instanceof UntrustedInput) {
                    this.unpackedArgs[i] = ((UntrustedInput)arguments[i]).getInput();
                    if (arguments[i] instanceof UntrustedUrlInput) {
                        this.argFilterType[i] = 2;
                    }
                    else {
                        this.argFilterType[i] = 1;
                    }
                }
                else {
                    this.unpackedArgs[i] = arguments[i];
                    this.argFilterType[i] = 1;
                }
                this.isLocaleSpecific[i] = (this.unpackedArgs[i] instanceof LocaleString);
            }
        }
        
        public boolean isEmpty() {
            return this.unpackedArgs.length == 0;
        }
        
        public Object[] getArguments() {
            return this.arguments;
        }
        
        public Object[] getFilteredArgs(final Locale locale) {
            final Object[] array = new Object[this.unpackedArgs.length];
            for (int i = 0; i < this.unpackedArgs.length; ++i) {
                Object o;
                if (this.filteredArgs[i] != null) {
                    o = this.filteredArgs[i];
                }
                else {
                    final Object o2 = this.unpackedArgs[i];
                    if (this.isLocaleSpecific[i]) {
                        o = this.filter(this.argFilterType[i], ((LocaleString)o2).getLocaleString(locale));
                    }
                    else {
                        o = this.filter(this.argFilterType[i], o2);
                        this.filteredArgs[i] = o;
                    }
                }
                array[i] = o;
            }
            return array;
        }
        
        private Object filter(final int n, final Object o) {
            if (this.filter == null) {
                return o;
            }
            final Object o2 = (null == o) ? "null" : o;
            switch (n) {
                case 0: {
                    return o2;
                }
                case 1: {
                    return this.filter.doFilter(o2.toString());
                }
                case 2: {
                    return this.filter.doFilterUrl(o2.toString());
                }
                default: {
                    return null;
                }
            }
        }
        
        public Filter getFilter() {
            return this.filter;
        }
        
        public void setFilter(final Filter filter) {
            if (filter != this.filter) {
                for (int i = 0; i < this.unpackedArgs.length; ++i) {
                    this.filteredArgs[i] = null;
                }
            }
            this.filter = filter;
        }
    }
}
