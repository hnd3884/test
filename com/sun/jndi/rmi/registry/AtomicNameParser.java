package com.sun.jndi.rmi.registry;

import javax.naming.NamingException;
import javax.naming.CompoundName;
import javax.naming.Name;
import java.util.Properties;
import javax.naming.NameParser;

class AtomicNameParser implements NameParser
{
    private static final Properties syntax;
    
    @Override
    public Name parse(final String s) throws NamingException {
        return new CompoundName(s, AtomicNameParser.syntax);
    }
    
    static {
        syntax = new Properties();
    }
}
