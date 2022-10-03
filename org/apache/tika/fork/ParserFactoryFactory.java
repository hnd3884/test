package org.apache.tika.fork;

import java.lang.reflect.Constructor;
import org.apache.tika.exception.TikaException;
import org.apache.tika.parser.ParserFactory;
import java.util.Map;
import java.io.Serializable;

public class ParserFactoryFactory implements Serializable
{
    private static final long serialVersionUID = 4710974869988895410L;
    private final String className;
    private final Map<String, String> args;
    
    public ParserFactoryFactory(final String className, final Map<String, String> args) {
        this.className = className;
        this.args = args;
    }
    
    public ParserFactory build() throws TikaException {
        try {
            final Class<?> clazz = Class.forName(this.className);
            final Constructor<?> con = clazz.getConstructor(Map.class);
            return (ParserFactory)con.newInstance(this.args);
        }
        catch (final ReflectiveOperationException | IllegalStateException e) {
            throw new TikaException("Couldn't create factory", e);
        }
    }
}
