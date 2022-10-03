package org.aopalliance.instrument;

import org.aopalliance.reflect.CodeLocator;
import org.aopalliance.reflect.Code;
import org.aopalliance.reflect.ClassLocator;

public interface Instrumentor
{
    ClassLocator createClass(final String p0) throws InstrumentationError;
    
    Instrumentation addInterface(final ClassLocator p0, final String p1) throws InstrumentationError;
    
    Instrumentation setSuperClass(final ClassLocator p0, final String p1) throws InstrumentationError;
    
    Instrumentation addClass(final ClassLocator p0, final String p1) throws InstrumentationError;
    
    Instrumentation addMethod(final ClassLocator p0, final String p1, final String[] p2, final String[] p3, final Code p4) throws InstrumentationError;
    
    Instrumentation addField(final ClassLocator p0, final String p1, final String p2, final Code p3) throws InstrumentationError;
    
    Instrumentation addBeforeCode(final CodeLocator p0, final Code p1, final Instrumentation p2, final Instrumentation p3) throws InstrumentationError;
    
    Instrumentation addAfterCode(final CodeLocator p0, final Code p1, final Instrumentation p2, final Instrumentation p3) throws InstrumentationError;
    
    Instrumentation addAroundCode(final CodeLocator p0, final Code p1, final String p2, final Instrumentation p3, final Instrumentation p4) throws InstrumentationError;
    
    void undo(final Instrumentation p0) throws UndoNotSupportedException;
}
