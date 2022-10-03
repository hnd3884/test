package org.apache.xerces.impl.xs;

import org.eclipse.wst.xml.xpath2.processor.XPathParser;
import org.eclipse.wst.xml.xpath2.processor.Evaluator;
import org.eclipse.wst.xml.xpath2.processor.StaticChecker;
import java.util.ArrayList;
import org.eclipse.wst.xml.xpath2.processor.XPathParserException;
import org.apache.xerces.impl.xs.util.XS11TypeHelper;
import org.eclipse.wst.xml.xpath2.processor.JFlexCupParser;
import org.apache.xerces.impl.xs.traversers.XSDHandler;
import org.apache.xerces.impl.xs.assertion.XSAssertImpl;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.internal.types.XSBoolean;
import org.eclipse.wst.xml.xpath2.processor.internal.Focus;
import org.eclipse.wst.xml.xpath2.processor.internal.types.AnyType;
import org.eclipse.wst.xml.xpath2.processor.internal.types.ElementType;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.w3c.dom.Node;
import org.eclipse.wst.xml.xpath2.processor.DefaultEvaluator;
import org.eclipse.wst.xml.xpath2.processor.internal.ast.XPathNode;
import org.eclipse.wst.xml.xpath2.processor.StaticContext;
import org.eclipse.wst.xml.xpath2.processor.StaticNameResolver;
import org.w3c.dom.Element;
import org.eclipse.wst.xml.xpath2.processor.ast.XPath;
import java.util.Enumeration;
import java.util.List;
import org.eclipse.wst.xml.xpath2.processor.function.XSCtrLibrary;
import org.eclipse.wst.xml.xpath2.processor.internal.function.FunctionLibrary;
import org.eclipse.wst.xml.xpath2.processor.function.FnFunctionLibrary;
import org.apache.xerces.util.NamespaceSupport;
import org.eclipse.wst.xml.xpath2.processor.DefaultDynamicContext;
import java.util.Map;
import org.apache.xerces.xs.XSModel;
import org.w3c.dom.Document;
import org.eclipse.wst.xml.xpath2.processor.DynamicContext;

public class AbstractPsychoPathXPath2Impl
{
    private DynamicContext fXpath2DynamicContext;
    private Document fDomDoc;
    
    public AbstractPsychoPathXPath2Impl() {
        this.fXpath2DynamicContext = null;
        this.fDomDoc = null;
    }
    
    public DynamicContext initXPath2DynamicContext(final XSModel xsModel, final Document fDomDoc, final Map map) {
        this.fXpath2DynamicContext = (DynamicContext)new DefaultDynamicContext(xsModel, fDomDoc);
        final NamespaceSupport namespaceSupport = map.get("XPATH2_NS_CONTEXT");
        final Boolean b = map.get("CTA-EVALUATOR");
        if (b != null && b) {
            final String[] namespaceBindingInfo = namespaceSupport.getNamespaceBindingInfo();
            final List prefixesXS11CTA = this.getPrefixesXS11CTA(namespaceBindingInfo);
            for (int i = 0; i < prefixesXS11CTA.size(); ++i) {
                final String s = prefixesXS11CTA.get(i);
                this.addNamespaceBindingToXPath2DynamicContext(s, this.getURIXS11CTA(s, namespaceBindingInfo));
            }
        }
        else {
            final Enumeration allPrefixes = namespaceSupport.getAllPrefixes();
            while (allPrefixes.hasMoreElements()) {
                final String s2 = allPrefixes.nextElement();
                this.addNamespaceBindingToXPath2DynamicContext(s2, namespaceSupport.getURI(s2));
            }
            this.addNamespaceBindingToXPath2DynamicContext("xml", "http://www.w3.org/XML/1998/namespace");
        }
        this.fXpath2DynamicContext.add_function_library((FunctionLibrary)new FnFunctionLibrary());
        this.fXpath2DynamicContext.add_function_library((FunctionLibrary)new XSCtrLibrary());
        this.fDomDoc = fDomDoc;
        return this.fXpath2DynamicContext;
    }
    
    protected void addNamespaceBindingToXPath2DynamicContext(final String s, final String s2) {
        this.fXpath2DynamicContext.add_namespace(s, s2);
    }
    
    protected boolean evaluateXPathExpr(final XPath xPath, final Element element) throws Exception {
        ((StaticChecker)new StaticNameResolver((StaticContext)this.fXpath2DynamicContext)).check((XPathNode)xPath);
        DefaultEvaluator defaultEvaluator;
        if (element != null) {
            defaultEvaluator = new DefaultEvaluator(this.fXpath2DynamicContext, this.fDomDoc, (Node)this.fDomDoc.getDocumentElement());
            final ResultSequence create_new = ResultSequenceFactory.create_new();
            create_new.add((AnyType)new ElementType(element, this.fXpath2DynamicContext.node_position((Node)element)));
            this.fXpath2DynamicContext.set_focus(new Focus(create_new));
        }
        else {
            defaultEvaluator = new DefaultEvaluator(this.fXpath2DynamicContext, (Document)null);
        }
        final ResultSequence evaluate = ((Evaluator)defaultEvaluator).evaluate((XPathNode)xPath);
        boolean b;
        if (evaluate == null) {
            b = false;
        }
        else if (evaluate.size() == 1) {
            final AnyType value = evaluate.get(0);
            b = (value instanceof XSBoolean && ((XSBoolean)value).value());
        }
        else {
            b = false;
        }
        return b;
    }
    
    protected XPath compileXPathStr(final String s, final XSAssertImpl xsAssertImpl, final XSDHandler xsdHandler, final Element element) {
        final JFlexCupParser flexCupParser = new JFlexCupParser();
        XPath parse = null;
        try {
            parse = ((XPathParser)flexCupParser).parse("boolean(" + s + ")", true);
        }
        catch (final XPathParserException ex) {
            if ("Expression starts with / or //".equals(ex.getMessage())) {
                xsdHandler.reportSchemaWarning("cvc-xpath.3.13.4.2b", new Object[] { xsAssertImpl.getTest().getXPathStr(), XS11TypeHelper.getSchemaTypeName(xsAssertImpl.getTypeDefinition()) }, element);
            }
            else {
                xsdHandler.reportSchemaError("cvc-xpath.3.13.4.2a", new Object[] { xsAssertImpl.getTest().getXPathStr(), XS11TypeHelper.getSchemaTypeName(xsAssertImpl.getTypeDefinition()) }, element);
            }
        }
        return parse;
    }
    
    private List getPrefixesXS11CTA(final String[] array) {
        final ArrayList list = new ArrayList();
        for (int n = 0; n < array.length && array[n] != null; n += 2) {
            list.add(array[n]);
        }
        return list;
    }
    
    private String getURIXS11CTA(final String s, final String[] array) {
        String s2 = null;
        for (int n = 0; n < array.length && array[n] != null; n += 2) {
            if (s.equals(array[n])) {
                s2 = array[n + 1];
                break;
            }
        }
        return s2;
    }
}
