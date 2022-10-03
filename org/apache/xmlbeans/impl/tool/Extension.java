package org.apache.xmlbeans.impl.tool;

import java.util.ArrayList;
import java.util.List;

public class Extension
{
    private Class className;
    private List params;
    
    public Extension() {
        this.params = new ArrayList();
    }
    
    public Class getClassName() {
        return this.className;
    }
    
    public void setClassName(final Class className) {
        this.className = className;
    }
    
    public List getParams() {
        return this.params;
    }
    
    public Param createParam() {
        final Param p = new Param();
        this.params.add(p);
        return p;
    }
    
    public class Param
    {
        private String name;
        private String value;
        
        public String getName() {
            return this.name;
        }
        
        public void setName(final String name) {
            this.name = name;
        }
        
        public String getValue() {
            return this.value;
        }
        
        public void setValue(final String value) {
            this.value = value;
        }
    }
}
