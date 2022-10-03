package javax.xml.validation;

public abstract class SchemaFactoryLoader
{
    protected SchemaFactoryLoader() {
    }
    
    public abstract SchemaFactory newFactory(final String p0);
}
