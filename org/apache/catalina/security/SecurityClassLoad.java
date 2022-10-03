package org.apache.catalina.security;

public final class SecurityClassLoad
{
    public static void securityClassLoad(final ClassLoader loader) throws Exception {
        securityClassLoad(loader, true);
    }
    
    static void securityClassLoad(final ClassLoader loader, final boolean requireSecurityManager) throws Exception {
        if (requireSecurityManager && System.getSecurityManager() == null) {
            return;
        }
        loadCorePackage(loader);
        loadCoyotePackage(loader);
        loadLoaderPackage(loader);
        loadRealmPackage(loader);
        loadServletsPackage(loader);
        loadSessionPackage(loader);
        loadUtilPackage(loader);
        loadJavaxPackage(loader);
        loadConnectorPackage(loader);
        loadTomcatPackage(loader);
    }
    
    private static final void loadCorePackage(final ClassLoader loader) throws Exception {
        final String basePackage = "org.apache.catalina.core.";
        loader.loadClass("org.apache.catalina.core.AccessLogAdapter");
        loader.loadClass("org.apache.catalina.core.ApplicationContextFacade$PrivilegedExecuteMethod");
        loader.loadClass("org.apache.catalina.core.ApplicationDispatcher$PrivilegedForward");
        loader.loadClass("org.apache.catalina.core.ApplicationDispatcher$PrivilegedInclude");
        loader.loadClass("org.apache.catalina.core.ApplicationPushBuilder");
        loader.loadClass("org.apache.catalina.core.AsyncContextImpl");
        loader.loadClass("org.apache.catalina.core.AsyncContextImpl$AsyncRunnable");
        loader.loadClass("org.apache.catalina.core.AsyncContextImpl$DebugException");
        loader.loadClass("org.apache.catalina.core.AsyncListenerWrapper");
        loader.loadClass("org.apache.catalina.core.ContainerBase$PrivilegedAddChild");
        loader.loadClass("org.apache.catalina.core.DefaultInstanceManager$AnnotationCacheEntry");
        loader.loadClass("org.apache.catalina.core.DefaultInstanceManager$AnnotationCacheEntryType");
        loader.loadClass("org.apache.catalina.core.DefaultInstanceManager$PrivilegedGetField");
        loader.loadClass("org.apache.catalina.core.DefaultInstanceManager$PrivilegedGetMethod");
        loader.loadClass("org.apache.catalina.core.DefaultInstanceManager$PrivilegedLoadClass");
        loader.loadClass("org.apache.catalina.core.ApplicationHttpRequest$AttributeNamesEnumerator");
    }
    
    private static final void loadLoaderPackage(final ClassLoader loader) throws Exception {
        final String basePackage = "org.apache.catalina.loader.";
        loader.loadClass("org.apache.catalina.loader.WebappClassLoaderBase$PrivilegedFindClassByName");
        loader.loadClass("org.apache.catalina.loader.WebappClassLoaderBase$PrivilegedHasLoggingConfig");
    }
    
    private static final void loadRealmPackage(final ClassLoader loader) throws Exception {
        final String basePackage = "org.apache.catalina.realm.";
        loader.loadClass("org.apache.catalina.realm.LockOutRealm$LockRecord");
    }
    
    private static final void loadServletsPackage(final ClassLoader loader) throws Exception {
        final String basePackage = "org.apache.catalina.servlets.";
        loader.loadClass("org.apache.catalina.servlets.DefaultServlet");
    }
    
    private static final void loadSessionPackage(final ClassLoader loader) throws Exception {
        final String basePackage = "org.apache.catalina.session.";
        loader.loadClass("org.apache.catalina.session.StandardSession");
        loader.loadClass("org.apache.catalina.session.StandardSession$PrivilegedNewSessionFacade");
        loader.loadClass("org.apache.catalina.session.StandardManager$PrivilegedDoUnload");
    }
    
    private static final void loadUtilPackage(final ClassLoader loader) throws Exception {
        final String basePackage = "org.apache.catalina.util.";
        loader.loadClass("org.apache.catalina.util.ParameterMap");
        loader.loadClass("org.apache.catalina.util.RequestUtil");
        loader.loadClass("org.apache.catalina.util.TLSUtil");
    }
    
    private static final void loadCoyotePackage(final ClassLoader loader) throws Exception {
        final String basePackage = "org.apache.coyote.";
        loader.loadClass("org.apache.coyote.http11.Constants");
        final Class<?> clazz = loader.loadClass("org.apache.coyote.Constants");
        clazz.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
        loader.loadClass("org.apache.coyote.http2.Stream$PrivilegedPush");
    }
    
    private static final void loadJavaxPackage(final ClassLoader loader) throws Exception {
        loader.loadClass("javax.servlet.http.Cookie");
    }
    
    private static final void loadConnectorPackage(final ClassLoader loader) throws Exception {
        final String basePackage = "org.apache.catalina.connector.";
        loader.loadClass("org.apache.catalina.connector.RequestFacade$GetAttributePrivilegedAction");
        loader.loadClass("org.apache.catalina.connector.RequestFacade$GetParameterMapPrivilegedAction");
        loader.loadClass("org.apache.catalina.connector.RequestFacade$GetRequestDispatcherPrivilegedAction");
        loader.loadClass("org.apache.catalina.connector.RequestFacade$GetParameterPrivilegedAction");
        loader.loadClass("org.apache.catalina.connector.RequestFacade$GetParameterNamesPrivilegedAction");
        loader.loadClass("org.apache.catalina.connector.RequestFacade$GetParameterValuePrivilegedAction");
        loader.loadClass("org.apache.catalina.connector.RequestFacade$GetCharacterEncodingPrivilegedAction");
        loader.loadClass("org.apache.catalina.connector.RequestFacade$GetHeadersPrivilegedAction");
        loader.loadClass("org.apache.catalina.connector.RequestFacade$GetHeaderNamesPrivilegedAction");
        loader.loadClass("org.apache.catalina.connector.RequestFacade$GetCookiesPrivilegedAction");
        loader.loadClass("org.apache.catalina.connector.RequestFacade$GetLocalePrivilegedAction");
        loader.loadClass("org.apache.catalina.connector.RequestFacade$GetLocalesPrivilegedAction");
        loader.loadClass("org.apache.catalina.connector.ResponseFacade$SetContentTypePrivilegedAction");
        loader.loadClass("org.apache.catalina.connector.ResponseFacade$DateHeaderPrivilegedAction");
        loader.loadClass("org.apache.catalina.connector.RequestFacade$GetSessionPrivilegedAction");
        loader.loadClass("org.apache.catalina.connector.ResponseFacade$FlushBufferPrivilegedAction");
        loadAnonymousInnerClasses(loader, "org.apache.catalina.connector.OutputBuffer");
        loadAnonymousInnerClasses(loader, "org.apache.catalina.connector.CoyoteInputStream");
        loader.loadClass("org.apache.catalina.connector.CoyoteOutputStream");
        loadAnonymousInnerClasses(loader, "org.apache.catalina.connector.InputBuffer");
        loadAnonymousInnerClasses(loader, "org.apache.catalina.connector.Response");
    }
    
    private static final void loadTomcatPackage(final ClassLoader loader) throws Exception {
        final String basePackage = "org.apache.tomcat.";
        loader.loadClass("org.apache.tomcat.util.buf.B2CConverter");
        loader.loadClass("org.apache.tomcat.util.buf.ByteBufferUtils");
        loader.loadClass("org.apache.tomcat.util.buf.C2BConverter");
        loader.loadClass("org.apache.tomcat.util.buf.HexUtils");
        loader.loadClass("org.apache.tomcat.util.buf.StringCache");
        loader.loadClass("org.apache.tomcat.util.buf.StringCache$ByteEntry");
        loader.loadClass("org.apache.tomcat.util.buf.StringCache$CharEntry");
        loader.loadClass("org.apache.tomcat.util.buf.UriUtil");
        Class<?> clazz = loader.loadClass("org.apache.tomcat.util.collections.CaseInsensitiveKeyMap");
        clazz.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
        loader.loadClass("org.apache.tomcat.util.collections.CaseInsensitiveKeyMap$EntryImpl");
        loader.loadClass("org.apache.tomcat.util.collections.CaseInsensitiveKeyMap$EntryIterator");
        loader.loadClass("org.apache.tomcat.util.collections.CaseInsensitiveKeyMap$EntrySet");
        loader.loadClass("org.apache.tomcat.util.collections.CaseInsensitiveKeyMap$Key");
        loader.loadClass("org.apache.tomcat.util.http.CookieProcessor");
        loader.loadClass("org.apache.tomcat.util.http.NamesEnumerator");
        clazz = loader.loadClass("org.apache.tomcat.util.http.FastHttpDateFormat");
        clazz.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
        loader.loadClass("org.apache.tomcat.util.http.parser.HttpParser");
        loader.loadClass("org.apache.tomcat.util.http.parser.MediaType");
        loader.loadClass("org.apache.tomcat.util.http.parser.MediaTypeCache");
        loader.loadClass("org.apache.tomcat.util.http.parser.SkipResult");
        loader.loadClass("org.apache.tomcat.util.net.Constants");
        loader.loadClass("org.apache.tomcat.util.net.DispatchType");
        loader.loadClass("org.apache.tomcat.util.net.NioBlockingSelector$BlockPoller$RunnableAdd");
        loader.loadClass("org.apache.tomcat.util.net.NioBlockingSelector$BlockPoller$RunnableCancel");
        loader.loadClass("org.apache.tomcat.util.net.NioBlockingSelector$BlockPoller$RunnableRemove");
        loader.loadClass("org.apache.tomcat.util.net.AprEndpoint$AprSocketWrapper$AprOperationState");
        loader.loadClass("org.apache.tomcat.util.net.NioEndpoint$NioSocketWrapper$NioOperationState");
        loader.loadClass("org.apache.tomcat.util.net.Nio2Endpoint$Nio2SocketWrapper$Nio2OperationState");
        loader.loadClass("org.apache.tomcat.util.net.SocketWrapperBase$BlockingMode");
        loader.loadClass("org.apache.tomcat.util.net.SocketWrapperBase$CompletionCheck");
        loader.loadClass("org.apache.tomcat.util.net.SocketWrapperBase$CompletionHandlerCall");
        loader.loadClass("org.apache.tomcat.util.net.SocketWrapperBase$CompletionState");
        loader.loadClass("org.apache.tomcat.util.net.SocketWrapperBase$VectoredIOCompletionHandler");
        loader.loadClass("org.apache.tomcat.util.net.TLSClientHelloExtractor");
        loader.loadClass("org.apache.tomcat.util.net.TLSClientHelloExtractor$ExtractorResult");
        loader.loadClass("org.apache.tomcat.util.security.PrivilegedGetTccl");
        loader.loadClass("org.apache.tomcat.util.security.PrivilegedSetTccl");
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
