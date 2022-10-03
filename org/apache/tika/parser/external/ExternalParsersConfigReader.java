package org.apache.tika.parser.external;

import java.util.StringTokenizer;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.HashSet;
import org.apache.tika.mime.MediaType;
import java.util.Set;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.apache.tika.mime.MimeTypeException;
import java.util.ArrayList;
import org.w3c.dom.Element;
import java.io.IOException;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import org.apache.tika.exception.TikaException;
import org.xml.sax.InputSource;
import org.apache.tika.utils.XMLReaderUtils;
import java.util.List;
import java.io.InputStream;

public final class ExternalParsersConfigReader implements ExternalParsersConfigReaderMetKeys
{
    public static List<ExternalParser> read(final InputStream stream) throws TikaException, IOException {
        try {
            final DocumentBuilder builder = XMLReaderUtils.getDocumentBuilder();
            final Document document = builder.parse(new InputSource(stream));
            return read(document);
        }
        catch (final SAXException e) {
            throw new TikaException("Invalid parser configuration", e);
        }
    }
    
    public static List<ExternalParser> read(final Document document) throws TikaException, IOException {
        return read(document.getDocumentElement());
    }
    
    public static List<ExternalParser> read(final Element element) throws TikaException, IOException {
        final List<ExternalParser> parsers = new ArrayList<ExternalParser>();
        if (element != null && element.getTagName().equals("external-parsers")) {
            final NodeList nodes = element.getChildNodes();
            for (int i = 0; i < nodes.getLength(); ++i) {
                final Node node = nodes.item(i);
                if (node.getNodeType() == 1) {
                    final Element child = (Element)node;
                    if (child.getTagName().equals("parser")) {
                        final ExternalParser p = readParser(child);
                        if (p != null) {
                            parsers.add(p);
                        }
                    }
                }
            }
            return parsers;
        }
        throw new MimeTypeException("Not a <external-parsers/> configuration document: " + element.getTagName());
    }
    
    private static ExternalParser readParser(final Element parserDef) throws TikaException {
        final ExternalParser parser = new ExternalParser();
        final NodeList children = parserDef.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            final Node node = children.item(i);
            if (node.getNodeType() == 1) {
                final Element child = (Element)node;
                final String tagName = child.getTagName();
                switch (tagName) {
                    case "check": {
                        final boolean present = readCheckTagAndCheck(child);
                        if (!present) {
                            return null;
                        }
                        break;
                    }
                    case "command": {
                        parser.setCommand(getString(child));
                        break;
                    }
                    case "mime-types": {
                        parser.setSupportedTypes(readMimeTypes(child));
                        break;
                    }
                    case "metadata": {
                        parser.setMetadataExtractionPatterns(readMetadataPatterns(child));
                        break;
                    }
                }
            }
        }
        return parser;
    }
    
    private static Set<MediaType> readMimeTypes(final Element mimeTypes) {
        final Set<MediaType> types = new HashSet<MediaType>();
        final NodeList children = mimeTypes.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            final Node node = children.item(i);
            if (node.getNodeType() == 1) {
                final Element child = (Element)node;
                if (child.getTagName().equals("mime-type")) {
                    types.add(MediaType.parse(getString(child)));
                }
            }
        }
        return types;
    }
    
    private static Map<Pattern, String> readMetadataPatterns(final Element metadataDef) {
        final Map<Pattern, String> metadata = new HashMap<Pattern, String>();
        final NodeList children = metadataDef.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            final Node node = children.item(i);
            if (node.getNodeType() == 1) {
                final Element child = (Element)node;
                if (child.getTagName().equals("match")) {
                    final String metadataKey = child.getAttribute("key");
                    final Pattern pattern = Pattern.compile(getString(child));
                    metadata.put(pattern, metadataKey);
                }
            }
        }
        return metadata;
    }
    
    private static boolean readCheckTagAndCheck(final Element checkDef) {
        String command = null;
        final List<Integer> errorVals = new ArrayList<Integer>();
        final NodeList children = checkDef.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            final Node node = children.item(i);
            if (node.getNodeType() == 1) {
                final Element child = (Element)node;
                if (child.getTagName().equals("command")) {
                    command = getString(child);
                }
                if (child.getTagName().equals("error-codes")) {
                    final String errs = getString(child);
                    final StringTokenizer st = new StringTokenizer(errs, ",");
                    while (st.hasMoreElements()) {
                        try {
                            final String s = st.nextToken();
                            errorVals.add(Integer.parseInt(s));
                        }
                        catch (final NumberFormatException ex) {}
                    }
                }
            }
        }
        if (command != null) {
            final String[] theCommand = command.split(" ");
            final int[] errVals = new int[errorVals.size()];
            for (int j = 0; j < errVals.length; ++j) {
                errVals[j] = errorVals.get(j);
            }
            return ExternalParser.check(theCommand, errVals);
        }
        return true;
    }
    
    private static String getString(final Element element) {
        final StringBuffer s = new StringBuffer();
        final NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            final Node node = children.item(i);
            if (node.getNodeType() == 3) {
                s.append(node.getNodeValue());
            }
        }
        return s.toString();
    }
}
