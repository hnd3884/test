package org.apache.axiom.util.stax.wrapper;

import javax.xml.stream.XMLOutputFactory;

public class ImmutableXMLOutputFactory extends XMLOutputFactoryWrapper
{
    public ImmutableXMLOutputFactory(final XMLOutputFactory parent) {
        super(parent);
    }
    
    @Override
    public void setProperty(final String name, final Object value) throws IllegalArgumentException {
        throw new IllegalStateException("This factory is immutable");
    }
}
