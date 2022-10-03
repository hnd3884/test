package com.sun.jndi.dns;

class CT
{
    int rrclass;
    int rrtype;
    
    CT(final int rrclass, final int rrtype) {
        this.rrclass = rrclass;
        this.rrtype = rrtype;
    }
}
