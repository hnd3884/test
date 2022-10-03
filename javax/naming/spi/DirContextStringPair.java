package javax.naming.spi;

import javax.naming.directory.DirContext;

class DirContextStringPair
{
    DirContext ctx;
    String str;
    
    DirContextStringPair(final DirContext ctx, final String str) {
        this.ctx = ctx;
        this.str = str;
    }
    
    DirContext getDirContext() {
        return this.ctx;
    }
    
    String getString() {
        return this.str;
    }
}
