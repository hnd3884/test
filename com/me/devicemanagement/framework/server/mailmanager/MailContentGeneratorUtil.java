package com.me.devicemanagement.framework.server.mailmanager;

import java.io.DataInputStream;
import java.io.BufferedInputStream;
import com.adventnet.persistence.xml.Do2XmlConverter;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;
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
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;

public class MailContentGeneratorUtil
{
    private Logger logger;
    private String sourceClass;
    
    public MailContentGeneratorUtil() {
        this.sourceClass = "MailContentGeneratorUtil";
        this.logger = SyMLogger.getSoMLogger();
    }
    
    public MailContentGeneratorUtil(final Logger log) {
        this.sourceClass = "MailContentGeneratorUtil";
        this.logger = log;
    }
    
    public boolean generateHTMLFile(final String xslFile, final String xmlFile, final String htmlFile) {
        final String sourceMethod = "generateHTMLFile";
        OutputStream fos = null;
        InputStream xslfileObj = null;
        boolean generated = Boolean.FALSE;
        try {
            SyMLogger.debug(this.logger, this.sourceClass, sourceMethod, "xslFile : " + xslFile);
            SyMLogger.debug(this.logger, this.sourceClass, sourceMethod, "xmlFile : " + xmlFile);
            SyMLogger.debug(this.logger, this.sourceClass, sourceMethod, "htmlFile : " + htmlFile);
            if (xslFile == null || xmlFile == null || htmlFile == null) {
                SyMLogger.warning(this.logger, this.sourceClass, sourceMethod, "File Name is NULL.  Cannot create HTML File");
                return false;
            }
            xslfileObj = ApiFactoryProvider.getFileAccessAPI().readFile(xslFile);
            fos = ApiFactoryProvider.getFileAccessAPI().writeFile(htmlFile);
            final Transformer transformer = XMLUtils.getTransformerInstance(xslfileObj);
            transformer.transform(new StreamSource(xmlFile), new StreamResult(fos));
            generated = Boolean.TRUE;
        }
        catch (final FileNotFoundException ex) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "FileNotFoundException while generating HTML file ", ex);
        }
        catch (final TransformerException ex2) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "TransformerException while generating HTML file ", ex2);
        }
        catch (final Exception ex3) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception while generating HTML file for ", ex3);
        }
        finally {
            if (xslfileObj != null) {
                try {
                    xslfileObj.close();
                }
                catch (final IOException ex4) {
                    SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception while closing file output stream", ex4);
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                }
                catch (final IOException ex4) {
                    SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception while closing file output stream", ex4);
                }
            }
        }
        return generated;
    }
    
    public String getHTMLContentFromXML(final String xslFile, final String xmlContent) {
        final String sourceMethod = "getHTMLContentFromXML";
        String htmlContent = "";
        InputStream xslInputStream = null;
        InputStream xmlInputStream = null;
        ByteArrayOutputStream htmloutputStream = null;
        ByteArrayOutputStream xmlOutputStream = null;
        try {
            htmloutputStream = new ByteArrayOutputStream();
            xmlOutputStream = new ByteArrayOutputStream();
            xslInputStream = new FileInputStream(xslFile);
            xmlInputStream = new ByteArrayInputStream(xmlContent.getBytes("UTF-8"));
            final Transformer transformer = XMLUtils.getTransformerInstance(xslInputStream);
            transformer.transform(new StreamSource(xmlInputStream), new StreamResult(htmloutputStream));
            htmlContent = new String(htmloutputStream.toByteArray(), "UTF-8");
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception while getting HTML content", e);
            if (xslInputStream != null) {
                try {
                    xslInputStream.close();
                }
                catch (final IOException ex) {
                    this.logger.log(Level.SEVERE, null, ex);
                }
            }
            if (xmlInputStream != null) {
                try {
                    xmlInputStream.close();
                }
                catch (final IOException ex) {
                    this.logger.log(Level.SEVERE, null, ex);
                }
            }
            if (xmlOutputStream != null) {
                try {
                    xmlOutputStream.close();
                }
                catch (final IOException ex) {
                    this.logger.log(Level.SEVERE, null, ex);
                }
            }
            if (htmloutputStream != null) {
                try {
                    htmloutputStream.close();
                }
                catch (final IOException ex) {
                    this.logger.log(Level.SEVERE, null, ex);
                }
            }
        }
        finally {
            if (xslInputStream != null) {
                try {
                    xslInputStream.close();
                }
                catch (final IOException ex2) {
                    this.logger.log(Level.SEVERE, null, ex2);
                }
            }
            if (xmlInputStream != null) {
                try {
                    xmlInputStream.close();
                }
                catch (final IOException ex2) {
                    this.logger.log(Level.SEVERE, null, ex2);
                }
            }
            if (xmlOutputStream != null) {
                try {
                    xmlOutputStream.close();
                }
                catch (final IOException ex2) {
                    this.logger.log(Level.SEVERE, null, ex2);
                }
            }
            if (htmloutputStream != null) {
                try {
                    htmloutputStream.close();
                }
                catch (final IOException ex2) {
                    this.logger.log(Level.SEVERE, null, ex2);
                }
            }
        }
        return htmlContent;
    }
    
    public String getHTMLContent(final String xslFile, final DataObject dateObject, final String xmlRoot) {
        final String sourceMethod = "getHTMLContent";
        String htmlContent = "";
        String xmlContent = "";
        InputStream xslInputStream = null;
        InputStream xmlInputStream = null;
        ByteArrayOutputStream htmloutputStream = null;
        ByteArrayOutputStream xmlOutputStream = null;
        try {
            htmloutputStream = new ByteArrayOutputStream();
            xmlOutputStream = new ByteArrayOutputStream();
            xslInputStream = new FileInputStream(xslFile);
            Do2XmlConverter.transform(dateObject, (OutputStream)xmlOutputStream, xmlRoot);
            xmlContent = new String(xmlOutputStream.toByteArray(), "UTF-8");
            xmlInputStream = new ByteArrayInputStream(xmlContent.getBytes());
            final Transformer transformer = XMLUtils.getTransformerInstance(xslInputStream);
            transformer.transform(new StreamSource(xmlInputStream), new StreamResult(htmloutputStream));
            htmlContent = new String(htmloutputStream.toByteArray(), "UTF-8");
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception while getting HTML content", e);
            if (xslInputStream != null) {
                try {
                    xslInputStream.close();
                }
                catch (final IOException ex) {
                    this.logger.log(Level.SEVERE, null, ex);
                }
            }
            if (xmlInputStream != null) {
                try {
                    xmlInputStream.close();
                }
                catch (final IOException ex) {
                    this.logger.log(Level.SEVERE, null, ex);
                }
            }
            if (xmlOutputStream != null) {
                try {
                    xmlOutputStream.close();
                }
                catch (final IOException ex) {
                    this.logger.log(Level.SEVERE, null, ex);
                }
            }
            if (htmloutputStream != null) {
                try {
                    htmloutputStream.close();
                }
                catch (final IOException ex) {
                    this.logger.log(Level.SEVERE, null, ex);
                }
            }
        }
        finally {
            if (xslInputStream != null) {
                try {
                    xslInputStream.close();
                }
                catch (final IOException ex2) {
                    this.logger.log(Level.SEVERE, null, ex2);
                }
            }
            if (xmlInputStream != null) {
                try {
                    xmlInputStream.close();
                }
                catch (final IOException ex2) {
                    this.logger.log(Level.SEVERE, null, ex2);
                }
            }
            if (xmlOutputStream != null) {
                try {
                    xmlOutputStream.close();
                }
                catch (final IOException ex2) {
                    this.logger.log(Level.SEVERE, null, ex2);
                }
            }
            if (htmloutputStream != null) {
                try {
                    htmloutputStream.close();
                }
                catch (final IOException ex2) {
                    this.logger.log(Level.SEVERE, null, ex2);
                }
            }
        }
        return htmlContent;
    }
    
    public String getMailContentFromHTML(final String htmlFile) {
        final String sourceMethod = "getMailContentFromHTML";
        String mailContents = null;
        InputStream fis = null;
        BufferedInputStream bis = null;
        DataInputStream dis = null;
        try {
            fis = ApiFactoryProvider.getFileAccessAPI().readFile(htmlFile);
            bis = new BufferedInputStream(fis);
            dis = new DataInputStream(bis);
            mailContents = "";
            if (dis != null) {
                for (String readLine = dis.readLine(); readLine != null; readLine = dis.readLine()) {
                    mailContents += readLine;
                }
            }
        }
        catch (final FileNotFoundException ex) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception while reading contents from " + htmlFile, ex);
        }
        catch (final IOException ex2) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception while reading contents from " + htmlFile, ex2);
        }
        catch (final Exception ex3) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception while reading contents from " + htmlFile, ex3);
        }
        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (bis != null) {
                    bis.close();
                }
                if (dis != null) {
                    dis.close();
                }
            }
            catch (final Exception e) {
                SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception while reading contents from " + htmlFile, e);
            }
        }
        return mailContents;
    }
}
