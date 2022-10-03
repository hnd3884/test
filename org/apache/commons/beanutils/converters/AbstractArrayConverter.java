package org.apache.commons.beanutils.converters;

import java.io.IOException;
import org.apache.commons.beanutils.ConversionException;
import java.util.ArrayList;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.List;
import org.apache.commons.beanutils.Converter;

@Deprecated
public abstract class AbstractArrayConverter implements Converter
{
    public static final Object NO_DEFAULT;
    protected static String[] strings;
    protected Object defaultValue;
    protected boolean useDefault;
    
    public AbstractArrayConverter() {
        this.defaultValue = null;
        this.useDefault = true;
        this.defaultValue = null;
        this.useDefault = false;
    }
    
    public AbstractArrayConverter(final Object defaultValue) {
        this.defaultValue = null;
        this.useDefault = true;
        if (defaultValue == AbstractArrayConverter.NO_DEFAULT) {
            this.useDefault = false;
        }
        else {
            this.defaultValue = defaultValue;
            this.useDefault = true;
        }
    }
    
    @Override
    public abstract Object convert(final Class p0, final Object p1);
    
    protected List parseElements(String svalue) {
        if (svalue == null) {
            throw new NullPointerException();
        }
        svalue = svalue.trim();
        if (svalue.startsWith("{") && svalue.endsWith("}")) {
            svalue = svalue.substring(1, svalue.length() - 1);
        }
        try {
            final StreamTokenizer st = new StreamTokenizer(new StringReader(svalue));
            st.whitespaceChars(44, 44);
            st.ordinaryChars(48, 57);
            st.ordinaryChars(46, 46);
            st.ordinaryChars(45, 45);
            st.wordChars(48, 57);
            st.wordChars(46, 46);
            st.wordChars(45, 45);
            final ArrayList list = new ArrayList();
            int ttype;
            while (true) {
                ttype = st.nextToken();
                if (ttype != -3 && ttype <= 0) {
                    break;
                }
                list.add(st.sval);
            }
            if (ttype == -1) {
                return list;
            }
            throw new ConversionException("Encountered token of type " + ttype);
        }
        catch (final IOException e) {
            throw new ConversionException(e);
        }
    }
    
    static {
        NO_DEFAULT = new Object();
        AbstractArrayConverter.strings = new String[0];
    }
}
