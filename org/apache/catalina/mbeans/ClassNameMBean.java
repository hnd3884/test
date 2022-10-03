package org.apache.catalina.mbeans;

public class ClassNameMBean<T> extends BaseCatalinaMBean<T>
{
    public String getClassName() {
        return this.resource.getClass().getName();
    }
}
