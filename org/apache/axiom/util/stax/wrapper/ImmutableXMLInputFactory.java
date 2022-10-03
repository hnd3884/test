package org.apache.axiom.util.stax.wrapper;

import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.util.XMLEventAllocator;
import javax.xml.stream.XMLInputFactory;

public class ImmutableXMLInputFactory extends XMLInputFactoryWrapper
{
    public ImmutableXMLInputFactory(final XMLInputFactory parent) {
        super(parent);
    }
    
    @Override
    public void setEventAllocator(final XMLEventAllocator allocator) {
        throw new IllegalStateException("This factory is immutable");
    }
    
    @Override
    public void setProperty(final String name, final Object value) throws IllegalArgumentException {
        throw new IllegalStateException("This factory is immutable");
    }
    
    @Override
    public void setXMLReporter(final XMLReporter reporter) {
        throw new IllegalStateException("This factory is immutable");
    }
    
    @Override
    public void setXMLResolver(final XMLResolver resolver) {
        throw new IllegalStateException("This factory is immutable");
    }
}
