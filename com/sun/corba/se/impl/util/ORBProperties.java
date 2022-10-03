package com.sun.corba.se.impl.util;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.File;

public class ORBProperties
{
    public static final String ORB_CLASS = "org.omg.CORBA.ORBClass=com.sun.corba.se.impl.orb.ORBImpl";
    public static final String ORB_SINGLETON_CLASS = "org.omg.CORBA.ORBSingletonClass=com.sun.corba.se.impl.orb.ORBSingleton";
    
    public static void main(final String[] array) {
        try {
            final File file = new File(System.getProperty("java.home") + File.separator + "lib" + File.separator + "orb.properties");
            if (file.exists()) {
                return;
            }
            final FileOutputStream fileOutputStream = new FileOutputStream(file);
            final PrintWriter printWriter = new PrintWriter(fileOutputStream);
            try {
                printWriter.println("org.omg.CORBA.ORBClass=com.sun.corba.se.impl.orb.ORBImpl");
                printWriter.println("org.omg.CORBA.ORBSingletonClass=com.sun.corba.se.impl.orb.ORBSingleton");
            }
            finally {
                printWriter.close();
                fileOutputStream.close();
            }
        }
        catch (final Exception ex) {}
    }
}
