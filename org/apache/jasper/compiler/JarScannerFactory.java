package org.apache.jasper.compiler;

import org.apache.tomcat.util.scan.StandardJarScanner;
import org.apache.tomcat.JarScanner;
import javax.servlet.ServletContext;

public class JarScannerFactory
{
    private JarScannerFactory() {
    }
    
    public static JarScanner getJarScanner(final ServletContext ctxt) {
        JarScanner jarScanner = (JarScanner)ctxt.getAttribute(JarScanner.class.getName());
        if (jarScanner == null) {
            ctxt.log(Localizer.getMessage("jsp.warning.noJarScanner"));
            jarScanner = (JarScanner)new StandardJarScanner();
        }
        return jarScanner;
    }
}
