package org.apache.xmlbeans.impl.tool;

import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.SchemaType;
import java.util.Collection;
import java.util.Collections;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.SchemaTypeLoader;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import java.util.ArrayList;

public class XMLBeanXSTCHarness implements XSTCTester.Harness
{
    @Override
    public void runTestCase(final XSTCTester.TestCaseResult result) {
        final XSTCTester.TestCase testCase = result.getTestCase();
        try {
            final Collection errors = new ArrayList();
            boolean schemaValid = true;
            boolean instanceValid = true;
            if (testCase.getSchemaFile() == null) {
                return;
            }
            SchemaTypeLoader loader = null;
            try {
                final XmlObject schema = XmlObject.Factory.parse(testCase.getSchemaFile(), new XmlOptions().setErrorListener(errors).setLoadLineNumbers());
                XmlObject schema2 = null;
                if (testCase.getResourceFile() != null) {
                    schema2 = XmlObject.Factory.parse(testCase.getResourceFile(), new XmlOptions().setErrorListener(errors).setLoadLineNumbers());
                }
                final XmlObject[] schemas = (schema2 == null) ? new XmlObject[] { schema } : new XmlObject[] { schema, schema2 };
                final SchemaTypeSystem system = XmlBeans.compileXsd(schemas, XmlBeans.getBuiltinTypeSystem(), new XmlOptions().setErrorListener(errors));
                loader = XmlBeans.typeLoaderUnion(new SchemaTypeLoader[] { system, XmlBeans.getBuiltinTypeSystem() });
            }
            catch (final Exception e) {
                schemaValid = false;
                if (!(e instanceof XmlException) || errors.isEmpty()) {
                    result.setCrash(true);
                    final StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    result.addSvMessages(Collections.singleton(sw.toString()));
                }
            }
            result.addSvMessages(errors);
            result.setSvActual(schemaValid);
            errors.clear();
            if (loader == null) {
                return;
            }
            if (testCase.getInstanceFile() == null) {
                return;
            }
            try {
                final XmlObject instance = loader.parse(testCase.getInstanceFile(), null, new XmlOptions().setErrorListener(errors).setLoadLineNumbers());
                if (!instance.validate(new XmlOptions().setErrorListener(errors))) {
                    instanceValid = false;
                }
            }
            catch (final Exception e) {
                instanceValid = false;
                if (!(e instanceof XmlException) || errors.isEmpty()) {
                    result.setCrash(true);
                    final StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    result.addIvMessages(Collections.singleton(sw.toString()));
                }
            }
            result.addIvMessages(errors);
            result.setIvActual(instanceValid);
        }
        finally {}
    }
}
