package org.glassfish.jersey.server.wadl.internal;

import javax.ws.rs.core.MediaType;
import java.util.Set;
import javax.xml.namespace.QName;
import org.glassfish.jersey.server.wadl.WadlGenerator;
import com.sun.research.ws.wadl.Application;

public class ApplicationDescription
{
    private Application _application;
    private WadlGenerator.ExternalGrammarDefinition _externalGrammarDefiniton;
    
    ApplicationDescription(final Application application, final WadlGenerator.ExternalGrammarDefinition externalGrammarDefiniton) {
        this._application = application;
        this._externalGrammarDefiniton = externalGrammarDefiniton;
    }
    
    public Application getApplication() {
        return this._application;
    }
    
    public QName resolve(final Class type) {
        return this._externalGrammarDefiniton.resolve(type);
    }
    
    public ExternalGrammar getExternalGrammar(final String path) {
        return this._externalGrammarDefiniton.map.get(path);
    }
    
    public Set<String> getExternalMetadataKeys() {
        return this._externalGrammarDefiniton.map.keySet();
    }
    
    public static class ExternalGrammar
    {
        private MediaType _type;
        private byte[] _content;
        
        public ExternalGrammar(final MediaType type, final byte[] content) {
            this._type = type;
            this._content = content.clone();
        }
        
        public MediaType getType() {
            return this._type;
        }
        
        public byte[] getContent() {
            return this._content.clone();
        }
    }
}
