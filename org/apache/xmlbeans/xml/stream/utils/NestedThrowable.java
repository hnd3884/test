package org.apache.xmlbeans.xml.stream.utils;

import java.lang.reflect.InvocationTargetException;
import java.io.PrintWriter;
import java.io.PrintStream;

public interface NestedThrowable
{
    Throwable getNested();
    
    String superToString();
    
    void superPrintStackTrace(final PrintStream p0);
    
    void superPrintStackTrace(final PrintWriter p0);
    
    public static class Util
    {
        private static String EOL;
        
        public static String toString(final NestedThrowable nt) {
            final Throwable nested = nt.getNested();
            if (nested == null) {
                return nt.superToString();
            }
            return nt.superToString() + " - with nested exception:" + Util.EOL + "[" + nestedToString(nested) + "]";
        }
        
        private static String nestedToString(final Throwable nested) {
            if (nested instanceof InvocationTargetException) {
                final InvocationTargetException ite = (InvocationTargetException)nested;
                return nested.toString() + " - with target exception:" + Util.EOL + "[" + ite.getTargetException().toString() + "]";
            }
            return nested.toString();
        }
        
        public static void printStackTrace(final NestedThrowable nt, final PrintStream s) {
            final Throwable nested = nt.getNested();
            if (nested != null) {
                nested.printStackTrace(s);
                s.println("--------------- nested within: ------------------");
            }
            nt.superPrintStackTrace(s);
        }
        
        public static void printStackTrace(final NestedThrowable nt, final PrintWriter w) {
            final Throwable nested = nt.getNested();
            if (nested != null) {
                nested.printStackTrace(w);
                w.println("--------------- nested within: ------------------");
            }
            nt.superPrintStackTrace(w);
        }
        
        static {
            Util.EOL = System.getProperty("line.separator");
        }
    }
}
