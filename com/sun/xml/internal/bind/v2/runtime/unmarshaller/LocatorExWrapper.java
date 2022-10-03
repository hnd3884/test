package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import javax.xml.bind.helpers.ValidationEventLocatorImpl;
import javax.xml.bind.ValidationEventLocator;
import org.xml.sax.Locator;

class LocatorExWrapper implements LocatorEx
{
    private final Locator locator;
    
    public LocatorExWrapper(final Locator locator) {
        this.locator = locator;
    }
    
    @Override
    public ValidationEventLocator getLocation() {
        return new ValidationEventLocatorImpl(this.locator);
    }
    
    @Override
    public String getPublicId() {
        return this.locator.getPublicId();
    }
    
    @Override
    public String getSystemId() {
        return this.locator.getSystemId();
    }
    
    @Override
    public int getLineNumber() {
        return this.locator.getLineNumber();
    }
    
    @Override
    public int getColumnNumber() {
        return this.locator.getColumnNumber();
    }
}
