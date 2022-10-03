package javax.naming.spi;

import javax.naming.Name;
import javax.naming.directory.DirContext;

class DirContextNamePair
{
    DirContext ctx;
    Name name;
    
    DirContextNamePair(final DirContext ctx, final Name name) {
        this.ctx = ctx;
        this.name = name;
    }
    
    DirContext getDirContext() {
        return this.ctx;
    }
    
    Name getName() {
        return this.name;
    }
}
