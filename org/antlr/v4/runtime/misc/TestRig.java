package org.antlr.v4.runtime.misc;

import java.lang.reflect.Method;

public class TestRig
{
    public static void main(final String[] args) {
        try {
            final Class<?> testRigClass = Class.forName("org.antlr.v4.gui.TestRig");
            System.err.println("Warning: TestRig moved to org.antlr.v4.gui.TestRig; calling automatically");
            try {
                final Method mainMethod = testRigClass.getMethod("main", String[].class);
                mainMethod.invoke(null, args);
            }
            catch (final Exception nsme) {
                System.err.println("Problems calling org.antlr.v4.gui.TestRig.main(args)");
            }
        }
        catch (final ClassNotFoundException cnfe) {
            System.err.println("Use of TestRig now requires the use of the tool jar, antlr-4.X-complete.jar");
            System.err.println("Maven users need group ID org.antlr and artifact ID antlr4");
        }
    }
}
