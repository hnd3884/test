package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import java.util.concurrent.Callable;
import org.xml.sax.SAXException;
import javax.xml.bind.ValidationEventHandler;
import java.util.HashMap;
import com.sun.xml.internal.bind.IDResolver;

final class DefaultIDResolver extends IDResolver
{
    private HashMap<String, Object> idmap;
    
    DefaultIDResolver() {
        this.idmap = null;
    }
    
    @Override
    public void startDocument(final ValidationEventHandler eventHandler) throws SAXException {
        if (this.idmap != null) {
            this.idmap.clear();
        }
    }
    
    @Override
    public void bind(final String id, final Object obj) {
        if (this.idmap == null) {
            this.idmap = new HashMap<String, Object>();
        }
        this.idmap.put(id, obj);
    }
    
    @Override
    public Callable resolve(final String id, final Class targetType) {
        return new Callable() {
            @Override
            public Object call() throws Exception {
                if (DefaultIDResolver.this.idmap == null) {
                    return null;
                }
                return DefaultIDResolver.this.idmap.get(id);
            }
        };
    }
}
