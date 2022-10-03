package org.apache.commons.math3.ode;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;

public abstract class AbstractParameterizable implements Parameterizable
{
    private final Collection<String> parametersNames;
    
    protected AbstractParameterizable(final String... names) {
        this.parametersNames = new ArrayList<String>();
        for (final String name : names) {
            this.parametersNames.add(name);
        }
    }
    
    protected AbstractParameterizable(final Collection<String> names) {
        (this.parametersNames = new ArrayList<String>()).addAll(names);
    }
    
    public Collection<String> getParametersNames() {
        return this.parametersNames;
    }
    
    public boolean isSupported(final String name) {
        for (final String supportedName : this.parametersNames) {
            if (supportedName.equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    public void complainIfNotSupported(final String name) throws UnknownParameterException {
        if (!this.isSupported(name)) {
            throw new UnknownParameterException(name);
        }
    }
}
