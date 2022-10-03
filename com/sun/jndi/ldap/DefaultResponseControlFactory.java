package com.sun.jndi.ldap;

import java.io.IOException;
import javax.naming.NamingException;
import javax.naming.ldap.PagedResultsResponseControl;
import javax.naming.ldap.SortResponseControl;
import javax.naming.ldap.Control;
import javax.naming.ldap.ControlFactory;

public class DefaultResponseControlFactory extends ControlFactory
{
    @Override
    public Control getControlInstance(final Control control) throws NamingException {
        final String id = control.getID();
        try {
            if (id.equals("1.2.840.113556.1.4.474")) {
                return new SortResponseControl(id, control.isCritical(), control.getEncodedValue());
            }
            if (id.equals("1.2.840.113556.1.4.319")) {
                return new PagedResultsResponseControl(id, control.isCritical(), control.getEncodedValue());
            }
            if (id.equals("2.16.840.1.113730.3.4.7")) {
                return new EntryChangeResponseControl(id, control.isCritical(), control.getEncodedValue());
            }
        }
        catch (final IOException rootCause) {
            final NamingException ex = new NamingException();
            ex.setRootCause(rootCause);
            throw ex;
        }
        return null;
    }
}
