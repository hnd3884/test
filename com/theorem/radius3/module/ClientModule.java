package com.theorem.radius3.module;

import java.lang.reflect.InvocationTargetException;

public class ClientModule
{
    private String a;
    
    public ClientModule() {
        final Package package1 = this.getClass().getPackage();
        if (package1 != null) {
            this.a = package1.getName();
        }
        else {
            this.a = null;
        }
    }
    
    public final boolean test(final String s) {
        String string;
        if (s.indexOf(46) >= 0) {
            string = s;
        }
        else {
            if (this.a == null) {
                return false;
            }
            string = this.a + "." + s;
        }
        try {
            Class.forName(string);
            return true;
        }
        catch (final Exception ex) {
            return false;
        }
    }
    
    public final Object getInstance(final String s, final Object o) throws RADIUSModuleException {
        String string;
        if (this.a != null) {
            string = this.a + "." + s.toLowerCase() + "." + s.toUpperCase() + "Impl";
        }
        else {
            string = s;
        }
        Class<?> forName;
        try {
            forName = Class.forName(string);
        }
        catch (final Exception ex) {
            return null;
        }
        try {
            if (o != null) {
                return forName.getDeclaredConstructor(o.getClass()).newInstance(o);
            }
            return forName.newInstance();
        }
        catch (final InvocationTargetException ex2) {
            throw new RADIUSModuleException(ex2);
        }
        catch (final IllegalAccessException ex3) {
            String s2 = "Module: " + s + ": " + ex3.getMessage() + " for " + string;
            if (o != null) {
                s2 = s2 + " ((" + o.getClass().getName() + ") - Illegal access";
            }
            ex3.printStackTrace();
            throw new RADIUSModuleException(s2);
        }
        catch (final NoSuchMethodException ex4) {
            String s3 = "Module: " + s + ": " + ex4.getMessage() + " for " + string;
            if (o != null) {
                s3 = s3 + " ((" + o.getClass().getName() + ") - No such method found.";
            }
            ex4.printStackTrace();
            throw new RADIUSModuleException(s3);
        }
        catch (final InstantiationException ex5) {
            String s4 = "Module: " + s + ": " + ex5.getMessage() + " for " + string;
            if (o != null) {
                s4 = s4 + " ((" + o.getClass().getName() + ") - Can't instantiate";
            }
            ex5.printStackTrace();
            throw new RADIUSModuleException(s4);
        }
    }
}
