package schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443;

import org.apache.xmlbeans.SchemaTypeSystem;

public class TypeSystemHolder
{
    public static final SchemaTypeSystem typeSystem;
    
    private TypeSystemHolder() {
    }
    
    private static final SchemaTypeSystem loadTypeSystem() {
        try {
            return (SchemaTypeSystem)Class.forName("org.apache.xmlbeans.impl.schema.SchemaTypeSystemImpl", true, TypeSystemHolder.class.getClassLoader()).getConstructor(Class.class).newInstance(TypeSystemHolder.class);
        }
        catch (final ClassNotFoundException ex) {
            throw new RuntimeException("Cannot load org.apache.xmlbeans.impl.SchemaTypeSystemImpl: make sure xbean.jar is on the classpath.", ex);
        }
        catch (final Exception ex2) {
            throw new RuntimeException("Could not instantiate SchemaTypeSystemImpl (" + ex2.toString() + "): is the version of xbean.jar correct?", ex2);
        }
    }
    
    static {
        typeSystem = loadTypeSystem();
    }
}
