package org.jfree.ui.about;

public class Library extends org.jfree.base.Library
{
    public Library(final String name, final String version, final String licence, final String info) {
        super(name, version, licence, info);
    }
    
    public Library(final ProjectInfo project) {
        this(project.getName(), project.getVersion(), project.getLicenceName(), project.getInfo());
    }
}
