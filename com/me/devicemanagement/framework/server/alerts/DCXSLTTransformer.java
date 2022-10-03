package com.me.devicemanagement.framework.server.alerts;

import javax.xml.transform.Transformer;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import javax.xml.transform.TransformerException;
import java.io.FileNotFoundException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import com.me.devicemanagement.framework.utils.XMLUtils;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.FileInputStream;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;

public class DCXSLTTransformer
{
    private static final Logger EMAILLOGGER;
    private static String sourceClass;
    private static DCXSLTTransformer xsltTransformer;
    
    public static DCXSLTTransformer getInstance() {
        if (DCXSLTTransformer.xsltTransformer == null) {
            DCXSLTTransformer.xsltTransformer = new DCXSLTTransformer();
        }
        return DCXSLTTransformer.xsltTransformer;
    }
    
    public boolean generateHTMLFile(final String xslFile, final String xmlFile, final String htmlFile) {
        final String sourceMethod = "generateHTMLFile";
        OutputStream fos = null;
        InputStream xslfileObj = null;
        boolean generated = false;
        try {
            SyMLogger.info(DCXSLTTransformer.EMAILLOGGER, DCXSLTTransformer.sourceClass, sourceMethod, "xslFile : " + xslFile);
            SyMLogger.info(DCXSLTTransformer.EMAILLOGGER, DCXSLTTransformer.sourceClass, sourceMethod, "xmlFile : " + xmlFile);
            SyMLogger.info(DCXSLTTransformer.EMAILLOGGER, DCXSLTTransformer.sourceClass, sourceMethod, "htmlFile : " + htmlFile);
            if (xslFile == null || xmlFile == null || htmlFile == null) {
                SyMLogger.warning(DCXSLTTransformer.EMAILLOGGER, DCXSLTTransformer.sourceClass, sourceMethod, "File Name is NULL.  Cannot create HTML File");
                return false;
            }
            xslfileObj = new FileInputStream(xslFile);
            fos = ApiFactoryProvider.getFileAccessAPI().writeFile(htmlFile);
            final Transformer transformer = XMLUtils.getTransformerInstance(xslfileObj);
            transformer.transform(new StreamSource(xmlFile), new StreamResult(fos));
            transformer.setOutputProperty("indent", "yes");
            generated = true;
        }
        catch (final FileNotFoundException ex) {
            SyMLogger.error(DCXSLTTransformer.EMAILLOGGER, DCXSLTTransformer.sourceClass, sourceMethod, "FileNotFoundException while generating HTML file for inventory mail alerts", ex);
        }
        catch (final TransformerException ex2) {
            SyMLogger.error(DCXSLTTransformer.EMAILLOGGER, DCXSLTTransformer.sourceClass, sourceMethod, "TransformerException while generating HTML file for inventory mail alerts", ex2);
        }
        catch (final Exception ex3) {
            SyMLogger.error(DCXSLTTransformer.EMAILLOGGER, DCXSLTTransformer.sourceClass, sourceMethod, "Exception while generating HTML file for inventory mail alerts", ex3);
        }
        finally {
            if (xslfileObj != null) {
                try {
                    xslfileObj.close();
                }
                catch (final IOException ex4) {
                    SyMLogger.error(DCXSLTTransformer.EMAILLOGGER, DCXSLTTransformer.sourceClass, sourceMethod, "Exception while closing file output stream", ex4);
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                }
                catch (final IOException ex4) {
                    SyMLogger.error(DCXSLTTransformer.EMAILLOGGER, DCXSLTTransformer.sourceClass, sourceMethod, "Exception while closing file output stream", ex4);
                }
            }
        }
        return generated;
    }
    
    static {
        EMAILLOGGER = Logger.getLogger("emailalerts");
        DCXSLTTransformer.sourceClass = "DCXSLTTransformer";
        DCXSLTTransformer.xsltTransformer = null;
    }
}
