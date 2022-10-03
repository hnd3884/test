package org.apache.poi.ooxml.dev;

import javax.xml.transform.TransformerException;
import javax.xml.transform.Source;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import org.w3c.dom.Node;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.poi.util.XMLHelper;
import org.w3c.dom.Document;
import java.util.Enumeration;
import org.apache.poi.util.IOUtils;
import java.io.InputStream;
import org.xml.sax.InputSource;
import java.util.zip.ZipEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import java.io.IOException;
import java.util.zip.ZipOutputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import org.apache.poi.openxml4j.opc.internal.ZipHelper;
import java.io.File;
import org.apache.poi.ooxml.util.DocumentHelper;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import javax.xml.parsers.DocumentBuilder;

public class OOXMLPrettyPrint
{
    private static final String XML_INDENT_AMOUNT = "{http://xml.apache.org/xslt}indent-amount";
    private final DocumentBuilder documentBuilder;
    
    public OOXMLPrettyPrint() {
        ZipSecureFile.setMinInflateRatio(1.0E-5);
        this.documentBuilder = DocumentHelper.newDocumentBuilder();
    }
    
    public static void main(final String[] args) throws Exception {
        if (args.length <= 1 || args.length % 2 != 0) {
            System.err.println("Use:");
            System.err.println("\tjava OOXMLPrettyPrint [<filename> <outfilename>] ...");
            System.exit(1);
        }
        for (int i = 0; i < args.length; i += 2) {
            final File f = new File(args[i]);
            if (!f.exists()) {
                System.err.println("Error, file not found!");
                System.err.println("\t" + f);
                System.exit(2);
            }
            handleFile(f, new File(args[i + 1]));
        }
        System.out.println("Done.");
    }
    
    private static void handleFile(final File file, final File outFile) throws IOException {
        System.out.println("Reading zip-file " + file + " and writing pretty-printed XML to " + outFile);
        try (final ZipSecureFile zipFile = ZipHelper.openZipFile(file);
             final ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(outFile)))) {
            new OOXMLPrettyPrint().handle(zipFile, out);
        }
        finally {
            System.out.println();
        }
    }
    
    private void handle(final ZipSecureFile file, final ZipOutputStream out) throws IOException {
        final Enumeration<? extends ZipArchiveEntry> entries = file.getEntries();
        while (entries.hasMoreElements()) {
            final ZipArchiveEntry entry = (ZipArchiveEntry)entries.nextElement();
            final String name = entry.getName();
            out.putNextEntry(new ZipEntry(name));
            try {
                if (name.endsWith(".xml") || name.endsWith(".rels")) {
                    final Document document = this.documentBuilder.parse(new InputSource(file.getInputStream(entry)));
                    document.setXmlStandalone(true);
                    pretty(document, out, 2);
                }
                else {
                    System.out.println("Not pretty-printing non-XML file " + name);
                    IOUtils.copy((InputStream)file.getInputStream(entry), (OutputStream)out);
                }
            }
            catch (final Exception e) {
                throw new IOException("While handling entry " + name, e);
            }
            finally {
                out.closeEntry();
            }
            System.out.print(".");
        }
    }
    
    private static void pretty(final Document document, final OutputStream outputStream, final int indent) throws TransformerException {
        final Transformer transformer = XMLHelper.newTransformer();
        if (indent > 0) {
            transformer.setOutputProperty("indent", "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", Integer.toString(indent));
        }
        final Result result = new StreamResult(outputStream);
        final Source source = new DOMSource(document);
        transformer.transform(source, result);
    }
}
