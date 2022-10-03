package org.omg.PortableInterceptor;

import org.omg.CORBA.PolicyError;
import org.omg.CORBA.Policy;
import org.omg.CORBA.Any;

public interface PolicyFactoryOperations
{
    Policy create_policy(final int p0, final Any p1) throws PolicyError;
}
