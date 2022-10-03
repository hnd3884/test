package org.jfree.base;

import java.util.ArrayList;

public class BootableProjectInfo extends BasicProjectInfo
{
    private String bootClass;
    private boolean autoBoot;
    
    public BootableProjectInfo() {
        this.autoBoot = true;
    }
    
    public BootableProjectInfo(final String name, final String version, final String licence, final String info) {
        this();
        this.setName(name);
        this.setVersion(version);
        this.setLicenceName(licence);
        this.setInfo(info);
    }
    
    public BootableProjectInfo(final String name, final String version, final String info, final String copyright, final String licenceName) {
        this();
        this.setName(name);
        this.setVersion(version);
        this.setLicenceName(licenceName);
        this.setInfo(info);
        this.setCopyright(copyright);
    }
    
    public void addDependency(final BootableProjectInfo projectInfo) {
        if (projectInfo == null) {
            throw new NullPointerException();
        }
        this.addLibrary(projectInfo);
    }
    
    public String getBootClass() {
        return this.bootClass;
    }
    
    public BootableProjectInfo[] getDependencies() {
        final ArrayList dependencies = new ArrayList();
        final Library[] libraries = this.getLibraries();
        for (int i = 0; i < libraries.length; ++i) {
            final Library lib = libraries[i];
            if (lib instanceof BootableProjectInfo) {
                dependencies.add(lib);
            }
        }
        final Library[] optionalLibraries = this.getOptionalLibraries();
        for (int j = 0; j < optionalLibraries.length; ++j) {
            final Library lib2 = optionalLibraries[j];
            if (lib2 instanceof BootableProjectInfo) {
                dependencies.add(lib2);
            }
        }
        return dependencies.toArray(new BootableProjectInfo[dependencies.size()]);
    }
    
    public boolean isAutoBoot() {
        return this.autoBoot;
    }
    
    public void setAutoBoot(final boolean autoBoot) {
        this.autoBoot = autoBoot;
    }
    
    public void setBootClass(final String bootClass) {
        this.bootClass = bootClass;
    }
}
