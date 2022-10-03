package com.sun.jndi.toolkit.dir;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

public interface AttrFilter
{
    boolean check(final Attributes p0) throws NamingException;
}
