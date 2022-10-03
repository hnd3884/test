package com.sun.corba.se.spi.orb;

import java.util.Iterator;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.Map;
import java.lang.reflect.Field;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;

public abstract class ParserImplBase
{
    private ORBUtilSystemException wrapper;
    
    protected abstract PropertyParser makeParser();
    
    protected void complete() {
    }
    
    public ParserImplBase() {
        this.wrapper = ORBUtilSystemException.get("orb.lifecycle");
    }
    
    public void init(final DataCollector dataCollector) {
        final PropertyParser parser = this.makeParser();
        dataCollector.setParser(parser);
        this.setFields(parser.parse(dataCollector.getProperties()));
    }
    
    private Field getAnyField(final String s) {
        Field field;
        try {
            Class<? extends ParserImplBase> clazz;
            for (clazz = this.getClass(), field = clazz.getDeclaredField(s); field == null; field = clazz.getDeclaredField(s)) {
                clazz = (Class<? extends ParserImplBase>)clazz.getSuperclass();
                if (clazz == null) {
                    break;
                }
            }
        }
        catch (final Exception ex) {
            throw this.wrapper.fieldNotFound(ex, s);
        }
        if (field == null) {
            throw this.wrapper.fieldNotFound(s);
        }
        return field;
    }
    
    protected void setFields(final Map map) {
        for (final Map.Entry entry : map.entrySet()) {
            final String s = (String)entry.getKey();
            final Object value = entry.getValue();
            try {
                AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction() {
                    @Override
                    public Object run() throws IllegalAccessException, IllegalArgumentException {
                        final Field access$000 = ParserImplBase.this.getAnyField(s);
                        access$000.setAccessible(true);
                        access$000.set(ParserImplBase.this, value);
                        return null;
                    }
                });
            }
            catch (final PrivilegedActionException ex) {
                throw this.wrapper.errorSettingField(ex.getCause(), s, value.toString());
            }
        }
        this.complete();
    }
}
