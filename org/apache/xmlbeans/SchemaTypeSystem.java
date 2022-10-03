package org.apache.xmlbeans;

import java.io.File;

public interface SchemaTypeSystem extends SchemaTypeLoader
{
    String getName();
    
    SchemaType[] globalTypes();
    
    SchemaType[] documentTypes();
    
    SchemaType[] attributeTypes();
    
    SchemaGlobalElement[] globalElements();
    
    SchemaGlobalAttribute[] globalAttributes();
    
    SchemaModelGroup[] modelGroups();
    
    SchemaAttributeGroup[] attributeGroups();
    
    SchemaAnnotation[] annotations();
    
    void resolve();
    
    SchemaComponent resolveHandle(final String p0);
    
    SchemaType typeForHandle(final String p0);
    
    ClassLoader getClassLoader();
    
    void saveToDirectory(final File p0);
    
    void save(final Filer p0);
}
