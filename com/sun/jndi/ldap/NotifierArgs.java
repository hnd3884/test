package com.sun.jndi.ldap;

import javax.naming.event.ObjectChangeListener;
import javax.naming.event.NamespaceChangeListener;
import javax.naming.event.NamingListener;
import javax.naming.directory.SearchControls;

final class NotifierArgs
{
    static final int ADDED_MASK = 1;
    static final int REMOVED_MASK = 2;
    static final int CHANGED_MASK = 4;
    static final int RENAMED_MASK = 8;
    String name;
    String filter;
    SearchControls controls;
    int mask;
    private int sum;
    
    NotifierArgs(final String s, final int searchScope, final NamingListener namingListener) {
        this(s, "(objectclass=*)", null, namingListener);
        if (searchScope != 1) {
            (this.controls = new SearchControls()).setSearchScope(searchScope);
        }
    }
    
    NotifierArgs(final String name, final String filter, final SearchControls controls, final NamingListener namingListener) {
        this.sum = -1;
        this.name = name;
        this.filter = filter;
        this.controls = controls;
        if (namingListener instanceof NamespaceChangeListener) {
            this.mask |= 0xB;
        }
        if (namingListener instanceof ObjectChangeListener) {
            this.mask |= 0x4;
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof NotifierArgs) {
            final NotifierArgs notifierArgs = (NotifierArgs)o;
            return this.mask == notifierArgs.mask && this.name.equals(notifierArgs.name) && this.filter.equals(notifierArgs.filter) && this.checkControls(notifierArgs.controls);
        }
        return false;
    }
    
    private boolean checkControls(final SearchControls searchControls) {
        if (this.controls == null || searchControls == null) {
            return searchControls == this.controls;
        }
        return this.controls.getSearchScope() == searchControls.getSearchScope() && this.controls.getTimeLimit() == searchControls.getTimeLimit() && this.controls.getDerefLinkFlag() == searchControls.getDerefLinkFlag() && this.controls.getReturningObjFlag() == searchControls.getReturningObjFlag() && this.controls.getCountLimit() == searchControls.getCountLimit() && checkStringArrays(this.controls.getReturningAttributes(), searchControls.getReturningAttributes());
    }
    
    private static boolean checkStringArrays(final String[] array, final String[] array2) {
        if (array == null || array2 == null) {
            return array == array2;
        }
        if (array.length != array2.length) {
            return false;
        }
        for (int i = 0; i < array.length; ++i) {
            if (!array[i].equals(array2[i])) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        if (this.sum == -1) {
            this.sum = this.mask + this.name.hashCode() + this.filter.hashCode() + this.controlsCode();
        }
        return this.sum;
    }
    
    private int controlsCode() {
        if (this.controls == null) {
            return 0;
        }
        int n = this.controls.getTimeLimit() + (int)this.controls.getCountLimit() + (this.controls.getDerefLinkFlag() ? 1 : 0) + (this.controls.getReturningObjFlag() ? 1 : 0);
        final String[] returningAttributes = this.controls.getReturningAttributes();
        if (returningAttributes != null) {
            for (int i = 0; i < returningAttributes.length; ++i) {
                n += returningAttributes[i].hashCode();
            }
        }
        return n;
    }
}
