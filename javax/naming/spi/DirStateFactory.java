package javax.naming.spi;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;

public interface DirStateFactory extends StateFactory
{
    Result getStateToBind(final Object p0, final Name p1, final Context p2, final Hashtable<?, ?> p3, final Attributes p4) throws NamingException;
    
    public static class Result
    {
        private Object obj;
        private Attributes attrs;
        
        public Result(final Object obj, final Attributes attrs) {
            this.obj = obj;
            this.attrs = attrs;
        }
        
        public Object getObject() {
            return this.obj;
        }
        
        public Attributes getAttributes() {
            return this.attrs;
        }
    }
}
