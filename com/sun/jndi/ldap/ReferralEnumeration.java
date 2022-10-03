package com.sun.jndi.ldap;

import javax.naming.NamingEnumeration;

interface ReferralEnumeration<T> extends NamingEnumeration<T>
{
    void appendUnprocessedReferrals(final LdapReferralException p0);
}
