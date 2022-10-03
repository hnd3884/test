package org.jfree.base;

public class Library
{
    private String name;
    private String version;
    private String licenceName;
    private String info;
    
    protected Library() {
    }
    
    public Library(final String name, final String version, final String licence, final String info) {
        this.name = name;
        this.version = version;
        this.licenceName = licence;
        this.info = info;
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Library library = (Library)o;
        boolean b;
        if (this.name != null) {
            b = (this.name.equals(library.name) ^ true);
        }
        else {
            if (library.name != null) {
                return false;
            }
            b = false;
        }
        if (!b) {
            return true;
        }
        return false;
    }
    
    public String getInfo() {
        return this.info;
    }
    
    public String getLicenceName() {
        return this.licenceName;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getVersion() {
        return this.version;
    }
    
    public int hashCode() {
        return (this.name != null) ? this.name.hashCode() : 0;
    }
    
    protected void setInfo(final String info) {
        this.info = info;
    }
    
    protected void setLicenceName(final String licenceName) {
        this.licenceName = licenceName;
    }
    
    protected void setName(final String name) {
        this.name = name;
    }
    
    protected void setVersion(final String version) {
        this.version = version;
    }
}
