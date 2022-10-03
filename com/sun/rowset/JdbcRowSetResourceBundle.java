package com.sun.rowset;

import java.util.Enumeration;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.io.Serializable;

public class JdbcRowSetResourceBundle implements Serializable
{
    private static String fileName;
    private transient PropertyResourceBundle propResBundle;
    private static volatile JdbcRowSetResourceBundle jpResBundle;
    private static final String PROPERTIES = "properties";
    private static final String UNDERSCORE = "_";
    private static final String DOT = ".";
    private static final String SLASH = "/";
    private static final String PATH = "com/sun/rowset/RowSetResourceBundle";
    static final long serialVersionUID = 436199386225359954L;
    
    private JdbcRowSetResourceBundle() throws IOException {
        this.propResBundle = (PropertyResourceBundle)ResourceBundle.getBundle("com/sun/rowset/RowSetResourceBundle", Locale.getDefault(), Thread.currentThread().getContextClassLoader());
    }
    
    public static JdbcRowSetResourceBundle getJdbcRowSetResourceBundle() throws IOException {
        if (JdbcRowSetResourceBundle.jpResBundle == null) {
            synchronized (JdbcRowSetResourceBundle.class) {
                if (JdbcRowSetResourceBundle.jpResBundle == null) {
                    JdbcRowSetResourceBundle.jpResBundle = new JdbcRowSetResourceBundle();
                }
            }
        }
        return JdbcRowSetResourceBundle.jpResBundle;
    }
    
    public Enumeration getKeys() {
        return this.propResBundle.getKeys();
    }
    
    public Object handleGetObject(final String s) {
        return this.propResBundle.handleGetObject(s);
    }
}
