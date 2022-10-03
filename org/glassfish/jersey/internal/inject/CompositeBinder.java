package org.glassfish.jersey.internal.inject;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;

public class CompositeBinder extends AbstractBinder
{
    private Collection<Binder> installed;
    
    private CompositeBinder(final Collection<Binder> installed) {
        this.installed = new ArrayList<Binder>();
        this.installed = installed;
    }
    
    public static AbstractBinder wrap(final Collection<Binder> binders) {
        return new CompositeBinder(binders);
    }
    
    public static AbstractBinder wrap(final Binder... binders) {
        return new CompositeBinder(Arrays.asList(binders));
    }
    
    public void configure() {
        this.install((AbstractBinder[])this.installed.toArray(new AbstractBinder[0]));
    }
}
