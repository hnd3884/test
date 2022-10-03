package com.sun.jndi.toolkit.dir;

import javax.naming.NamingException;
import javax.naming.Binding;
import javax.naming.directory.SearchControls;
import javax.naming.Context;
import javax.naming.directory.SearchResult;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;

public class DirSearch
{
    public static NamingEnumeration<SearchResult> search(final DirContext dirContext, final Attributes attributes, final String[] array) throws NamingException {
        return new LazySearchEnumerationImpl(new ContextEnumerator(dirContext, 1), new ContainmentFilter(attributes), new SearchControls(1, 0L, 0, array, false, false));
    }
    
    public static NamingEnumeration<SearchResult> search(final DirContext dirContext, final String s, SearchControls searchControls) throws NamingException {
        if (searchControls == null) {
            searchControls = new SearchControls();
        }
        return new LazySearchEnumerationImpl(new ContextEnumerator(dirContext, searchControls.getSearchScope()), new SearchFilter(s), searchControls);
    }
    
    public static NamingEnumeration<SearchResult> search(final DirContext dirContext, final String s, final Object[] array, final SearchControls searchControls) throws NamingException {
        return search(dirContext, SearchFilter.format(s, array), searchControls);
    }
}
