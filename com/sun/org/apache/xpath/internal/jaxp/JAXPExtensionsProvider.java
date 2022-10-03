package com.sun.org.apache.xpath.internal.jaxp;

import com.sun.org.apache.xpath.internal.functions.FuncExtFunction;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import java.util.List;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XNodeSet;
import java.util.ArrayList;
import javax.xml.xpath.XPathFunctionException;
import java.util.Vector;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathFunction;
import javax.xml.namespace.QName;
import com.sun.org.apache.xpath.internal.res.XPATHMessages;
import jdk.xml.internal.JdkXmlFeatures;
import javax.xml.xpath.XPathFunctionResolver;
import com.sun.org.apache.xpath.internal.ExtensionsProvider;

public class JAXPExtensionsProvider implements ExtensionsProvider
{
    private final XPathFunctionResolver resolver;
    private boolean extensionInvocationDisabled;
    
    public JAXPExtensionsProvider(final XPathFunctionResolver resolver) {
        this.extensionInvocationDisabled = false;
        this.resolver = resolver;
        this.extensionInvocationDisabled = false;
    }
    
    public JAXPExtensionsProvider(final XPathFunctionResolver resolver, final boolean featureSecureProcessing, final JdkXmlFeatures featureManager) {
        this.extensionInvocationDisabled = false;
        this.resolver = resolver;
        if (featureSecureProcessing && !featureManager.getFeature(JdkXmlFeatures.XmlFeature.ENABLE_EXTENSION_FUNCTION)) {
            this.extensionInvocationDisabled = true;
        }
    }
    
    @Override
    public boolean functionAvailable(final String ns, final String funcName) throws TransformerException {
        try {
            if (funcName == null) {
                final String fmsg = XPATHMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "Function Name" });
                throw new NullPointerException(fmsg);
            }
            final QName myQName = new QName(ns, funcName);
            final XPathFunction xpathFunction = this.resolver.resolveFunction(myQName, 0);
            return xpathFunction != null;
        }
        catch (final Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean elementAvailable(final String ns, final String elemName) throws TransformerException {
        return false;
    }
    
    @Override
    public Object extFunction(final String ns, final String funcName, final Vector argVec, final Object methodKey) throws TransformerException {
        try {
            if (funcName == null) {
                final String fmsg = XPATHMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "Function Name" });
                throw new NullPointerException(fmsg);
            }
            final QName myQName = new QName(ns, funcName);
            if (this.extensionInvocationDisabled) {
                final String fmsg2 = XPATHMessages.createXPATHMessage("ER_EXTENSION_FUNCTION_CANNOT_BE_INVOKED", new Object[] { myQName.toString() });
                throw new XPathFunctionException(fmsg2);
            }
            final int arity = argVec.size();
            final XPathFunction xpathFunction = this.resolver.resolveFunction(myQName, arity);
            final ArrayList argList = new ArrayList(arity);
            for (int i = 0; i < arity; ++i) {
                final Object argument = argVec.elementAt(i);
                if (argument instanceof XNodeSet) {
                    argList.add(i, ((XNodeSet)argument).nodelist());
                }
                else if (argument instanceof XObject) {
                    final Object passedArgument = ((XObject)argument).object();
                    argList.add(i, passedArgument);
                }
                else {
                    argList.add(i, argument);
                }
            }
            return xpathFunction.evaluate(argList);
        }
        catch (final XPathFunctionException xfe) {
            throw new WrappedRuntimeException(xfe);
        }
        catch (final Exception e) {
            throw new TransformerException(e);
        }
    }
    
    @Override
    public Object extFunction(final FuncExtFunction extFunction, final Vector argVec) throws TransformerException {
        try {
            final String namespace = extFunction.getNamespace();
            final String functionName = extFunction.getFunctionName();
            final int arity = extFunction.getArgCount();
            final QName myQName = new QName(namespace, functionName);
            if (this.extensionInvocationDisabled) {
                final String fmsg = XPATHMessages.createXPATHMessage("ER_EXTENSION_FUNCTION_CANNOT_BE_INVOKED", new Object[] { myQName.toString() });
                throw new XPathFunctionException(fmsg);
            }
            final XPathFunction xpathFunction = this.resolver.resolveFunction(myQName, arity);
            final ArrayList argList = new ArrayList(arity);
            for (int i = 0; i < arity; ++i) {
                final Object argument = argVec.elementAt(i);
                if (argument instanceof XNodeSet) {
                    argList.add(i, ((XNodeSet)argument).nodelist());
                }
                else if (argument instanceof XObject) {
                    final Object passedArgument = ((XObject)argument).object();
                    argList.add(i, passedArgument);
                }
                else {
                    argList.add(i, argument);
                }
            }
            return xpathFunction.evaluate(argList);
        }
        catch (final XPathFunctionException xfe) {
            throw new WrappedRuntimeException(xfe);
        }
        catch (final Exception e) {
            throw new TransformerException(e);
        }
    }
}
