package com.sun.jndi.toolkit.dir;

import java.util.Hashtable;
import javax.naming.NamingException;
import javax.naming.Name;
import java.util.Properties;
import javax.naming.NameParser;

final class HierarchicalNameParser implements NameParser
{
    static final Properties mySyntax;
    
    @Override
    public Name parse(final String s) throws NamingException {
        return new HierarchicalName(s, HierarchicalNameParser.mySyntax);
    }
    
    static {
        ((Hashtable<String, String>)(mySyntax = new Properties())).put("jndi.syntax.direction", "left_to_right");
        ((Hashtable<String, String>)HierarchicalNameParser.mySyntax).put("jndi.syntax.separator", "/");
        ((Hashtable<String, String>)HierarchicalNameParser.mySyntax).put("jndi.syntax.ignorecase", "true");
        ((Hashtable<String, String>)HierarchicalNameParser.mySyntax).put("jndi.syntax.escape", "\\");
        ((Hashtable<String, String>)HierarchicalNameParser.mySyntax).put("jndi.syntax.beginquote", "\"");
        ((Hashtable<String, String>)HierarchicalNameParser.mySyntax).put("jndi.syntax.trimblanks", "false");
    }
}
