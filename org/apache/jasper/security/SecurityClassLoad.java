package org.apache.jasper.security;

import org.apache.juli.logging.Log;
import org.apache.jasper.compiler.Localizer;
import org.apache.juli.logging.LogFactory;

public final class SecurityClassLoad
{
    public static void securityClassLoad(final ClassLoader loader) {
        if (System.getSecurityManager() == null) {
            return;
        }
        final String basePackage = "org.apache.jasper.";
        try {
            loader.loadClass("org.apache.jasper.compiler.EncodingDetector");
            loader.loadClass("org.apache.jasper.runtime.JspContextWrapper");
            loader.loadClass("org.apache.jasper.runtime.JspFactoryImpl$PrivilegedGetPageContext");
            loader.loadClass("org.apache.jasper.runtime.JspFactoryImpl$PrivilegedReleasePageContext");
            loader.loadClass("org.apache.jasper.runtime.JspFragmentHelper");
            loader.loadClass("org.apache.jasper.runtime.JspRuntimeLibrary");
            loader.loadClass("org.apache.jasper.runtime.PageContextImpl");
            loadAnonymousInnerClasses(loader, "org.apache.jasper.runtime.PageContextImpl");
            loader.loadClass("org.apache.jasper.runtime.ProtectedFunctionMapper");
            loader.loadClass("org.apache.jasper.runtime.ServletResponseWrapperInclude");
            loader.loadClass("org.apache.jasper.runtime.TagHandlerPool");
            SecurityUtil.isPackageProtectionEnabled();
            loader.loadClass("org.apache.jasper.servlet.JspServletWrapper");
        }
        catch (final Exception ex) {
            final Log log = LogFactory.getLog((Class)SecurityClassLoad.class);
            log.error((Object)Localizer.getMessage("jsp.error.securityPreload"), (Throwable)ex);
        }
    }
    
    private static final void loadAnonymousInnerClasses(final ClassLoader loader, final String enclosingClass) {
        try {
            int i = 1;
            while (true) {
                loader.loadClass(enclosingClass + '$' + i);
                ++i;
            }
        }
        catch (final ClassNotFoundException ex) {}
    }
}
