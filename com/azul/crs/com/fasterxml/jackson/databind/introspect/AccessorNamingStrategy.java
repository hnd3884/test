package com.azul.crs.com.fasterxml.jackson.databind.introspect;

import com.azul.crs.com.fasterxml.jackson.databind.BeanDescription;
import com.azul.crs.com.fasterxml.jackson.databind.cfg.MapperConfig;
import java.io.Serializable;

public abstract class AccessorNamingStrategy
{
    public abstract String findNameForIsGetter(final AnnotatedMethod p0, final String p1);
    
    public abstract String findNameForRegularGetter(final AnnotatedMethod p0, final String p1);
    
    public abstract String findNameForMutator(final AnnotatedMethod p0, final String p1);
    
    public abstract String modifyFieldName(final AnnotatedField p0, final String p1);
    
    public static class Base extends AccessorNamingStrategy implements Serializable
    {
        private static final long serialVersionUID = 1L;
        
        @Override
        public String findNameForIsGetter(final AnnotatedMethod method, final String name) {
            return null;
        }
        
        @Override
        public String findNameForRegularGetter(final AnnotatedMethod method, final String name) {
            return null;
        }
        
        @Override
        public String findNameForMutator(final AnnotatedMethod method, final String name) {
            return null;
        }
        
        @Override
        public String modifyFieldName(final AnnotatedField field, final String name) {
            return name;
        }
    }
    
    public abstract static class Provider implements Serializable
    {
        private static final long serialVersionUID = 1L;
        
        public abstract AccessorNamingStrategy forPOJO(final MapperConfig<?> p0, final AnnotatedClass p1);
        
        public abstract AccessorNamingStrategy forBuilder(final MapperConfig<?> p0, final AnnotatedClass p1, final BeanDescription p2);
        
        public abstract AccessorNamingStrategy forRecord(final MapperConfig<?> p0, final AnnotatedClass p1);
    }
}
