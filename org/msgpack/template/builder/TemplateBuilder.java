package org.msgpack.template.builder;

import org.msgpack.template.FieldList;
import org.msgpack.template.Template;
import java.lang.reflect.Type;

public interface TemplateBuilder
{
    boolean matchType(final Type p0, final boolean p1);
    
     <T> Template<T> buildTemplate(final Type p0) throws TemplateBuildException;
    
     <T> Template<T> buildTemplate(final Class<T> p0, final FieldList p1) throws TemplateBuildException;
    
    void writeTemplate(final Type p0, final String p1);
    
     <T> Template<T> loadTemplate(final Type p0);
}
