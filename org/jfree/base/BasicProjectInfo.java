package org.jfree.base;

import java.lang.reflect.Method;
import org.jfree.util.ObjectUtilities;
import java.util.ArrayList;
import java.util.List;

public class BasicProjectInfo extends Library
{
    private String copyright;
    private List libraries;
    private List optionalLibraries;
    
    public BasicProjectInfo() {
        this.libraries = new ArrayList();
        this.optionalLibraries = new ArrayList();
    }
    
    public BasicProjectInfo(final String name, final String version, final String licence, final String info) {
        this();
        this.setName(name);
        this.setVersion(version);
        this.setLicenceName(licence);
        this.setInfo(info);
    }
    
    public BasicProjectInfo(final String name, final String version, final String info, final String copyright, final String licenceName) {
        this(name, version, licenceName, info);
        this.setCopyright(copyright);
    }
    
    public void addLibrary(final Library library) {
        if (library == null) {
            throw new NullPointerException();
        }
        this.libraries.add(library);
    }
    
    public void addOptionalLibrary(final String libraryClass) {
        if (libraryClass == null) {
            throw new NullPointerException("Library classname must be given.");
        }
        this.optionalLibraries.add(new OptionalLibraryHolder(libraryClass));
    }
    
    public void addOptionalLibrary(final Library library) {
        if (library == null) {
            throw new NullPointerException("Library must be given.");
        }
        this.optionalLibraries.add(new OptionalLibraryHolder(library));
    }
    
    public String getCopyright() {
        return this.copyright;
    }
    
    public Library[] getLibraries() {
        return this.libraries.toArray(new Library[this.libraries.size()]);
    }
    
    public Library[] getOptionalLibraries() {
        final ArrayList libraries = new ArrayList();
        for (int i = 0; i < this.optionalLibraries.size(); ++i) {
            final OptionalLibraryHolder holder = this.optionalLibraries.get(i);
            final Library l = holder.getLibrary();
            if (l != null) {
                libraries.add(l);
            }
        }
        return libraries.toArray(new Library[libraries.size()]);
    }
    
    public void setCopyright(final String copyright) {
        this.copyright = copyright;
    }
    
    public void setInfo(final String info) {
        super.setInfo(info);
    }
    
    public void setLicenceName(final String licence) {
        super.setLicenceName(licence);
    }
    
    public void setName(final String name) {
        super.setName(name);
    }
    
    public void setVersion(final String version) {
        super.setVersion(version);
    }
    
    private static class OptionalLibraryHolder
    {
        private String libraryClass;
        private transient Library library;
        
        public OptionalLibraryHolder(final String libraryClass) {
            if (libraryClass == null) {
                throw new NullPointerException("LibraryClass must not be null.");
            }
            this.libraryClass = libraryClass;
        }
        
        public OptionalLibraryHolder(final Library library) {
            if (library == null) {
                throw new NullPointerException("Library must not be null.");
            }
            this.library = library;
            this.libraryClass = library.getClass().getName();
        }
        
        public Library getLibrary() {
            if (this.library == null) {
                this.library = this.loadLibrary(this.libraryClass);
            }
            return this.library;
        }
        
        public String getLibraryClass() {
            return this.libraryClass;
        }
        
        protected Library loadLibrary(final String classname) {
            if (classname == null) {
                return null;
            }
            try {
                final Class c = ObjectUtilities.getClassLoader(this.getClass()).loadClass(classname);
                try {
                    final Method m = c.getMethod("getInstance", (Class[])null);
                    return (Library)m.invoke(null, (Object[])null);
                }
                catch (final Exception ex) {
                    return c.newInstance();
                }
            }
            catch (final Exception ex2) {
                return null;
            }
        }
    }
}
