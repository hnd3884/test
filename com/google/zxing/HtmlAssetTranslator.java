package com.google.zxing;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.ls.LSSerializer;
import org.w3c.dom.NodeList;
import java.util.Queue;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.Node;
import java.util.LinkedList;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.io.FileFilter;
import java.util.ArrayList;
import java.io.IOException;
import java.util.Iterator;
import java.util.Collection;
import java.io.File;
import java.util.regex.Pattern;

public final class HtmlAssetTranslator
{
    private static final Pattern COMMA;
    
    private HtmlAssetTranslator() {
    }
    
    public static void main(final String[] args) throws IOException {
        final File assetsDir = new File(args[0]);
        final Collection<String> languagesToTranslate = parseLanguagesToTranslate(assetsDir, args[1]);
        final Collection<String> filesToTranslate = parseFilesToTranslate(args);
        for (final String language : languagesToTranslate) {
            translateOneLanguage(assetsDir, language, filesToTranslate);
        }
    }
    
    private static Collection<String> parseLanguagesToTranslate(final File assetsDir, final CharSequence languageArg) {
        final Collection<String> languages = new ArrayList<String>();
        if ("all".equals(languageArg)) {
            final FileFilter fileFilter = new FileFilter() {
                @Override
                public boolean accept(final File file) {
                    return file.isDirectory() && file.getName().startsWith("html-") && !"html-en".equals(file.getName());
                }
            };
            for (final File languageDir : assetsDir.listFiles(fileFilter)) {
                languages.add(languageDir.getName().substring(5));
            }
        }
        else {
            languages.addAll(Arrays.asList(HtmlAssetTranslator.COMMA.split(languageArg)));
        }
        return languages;
    }
    
    private static Collection<String> parseFilesToTranslate(final String[] args) {
        final Collection<String> fileNamesToTranslate = new ArrayList<String>();
        for (int i = 2; i < args.length; ++i) {
            fileNamesToTranslate.add(args[i]);
        }
        return fileNamesToTranslate;
    }
    
    private static void translateOneLanguage(final File assetsDir, final String language, final Collection<String> filesToTranslate) throws IOException {
        final File targetHtmlDir = new File(assetsDir, "html-" + language);
        targetHtmlDir.mkdirs();
        final File englishHtmlDir = new File(assetsDir, "html-en");
        final String translationTextTranslated = StringsResourceTranslator.translateString("Translated by Google Translate.", language);
        final File[] arr$;
        final File[] sourceFiles = arr$ = englishHtmlDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return name.endsWith(".html") && (filesToTranslate.isEmpty() || filesToTranslate.contains(name));
            }
        });
        for (final File sourceFile : arr$) {
            translateOneFile(language, targetHtmlDir, sourceFile, translationTextTranslated);
        }
    }
    
    private static void translateOneFile(final String language, final File targetHtmlDir, final File sourceFile, final String translationTextTranslated) throws IOException {
        final File destFile = new File(targetHtmlDir, sourceFile.getName());
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document document;
        try {
            final DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(sourceFile);
        }
        catch (final ParserConfigurationException pce) {
            throw new IllegalStateException(pce);
        }
        catch (final SAXException sae) {
            throw new IOException(sae);
        }
        final Element rootElement = document.getDocumentElement();
        rootElement.normalize();
        final Queue<Node> nodes = new LinkedList<Node>();
        nodes.add(rootElement);
        while (!nodes.isEmpty()) {
            final Node node = nodes.poll();
            if (shouldTranslate(node)) {
                final NodeList children = node.getChildNodes();
                for (int i = 0; i < children.getLength(); ++i) {
                    nodes.add(children.item(i));
                }
            }
            if (node.getNodeType() == 3) {
                String text = node.getTextContent();
                if (text.trim().length() <= 0) {
                    continue;
                }
                text = StringsResourceTranslator.translateString(text, language);
                node.setTextContent(' ' + text + ' ');
            }
        }
        final Node translateText = document.createTextNode(translationTextTranslated);
        final Node paragraph = document.createElement("p");
        paragraph.appendChild(translateText);
        final Node body = rootElement.getElementsByTagName("body").item(0);
        body.appendChild(paragraph);
        DOMImplementationRegistry registry;
        try {
            registry = DOMImplementationRegistry.newInstance();
        }
        catch (final ClassNotFoundException cnfe) {
            throw new IllegalStateException(cnfe);
        }
        catch (final InstantiationException ie) {
            throw new IllegalStateException(ie);
        }
        catch (final IllegalAccessException iae) {
            throw new IllegalStateException(iae);
        }
        final DOMImplementationLS impl = (DOMImplementationLS)registry.getDOMImplementation("LS");
        final LSSerializer writer = impl.createLSSerializer();
        writer.writeToURI(document, destFile.toURI().toString());
    }
    
    private static boolean shouldTranslate(final Node node) {
        final NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            final Node classAttribute = attributes.getNamedItem("class");
            if (classAttribute != null) {
                final String textContent = classAttribute.getTextContent();
                if (textContent != null && textContent.contains("notranslate")) {
                    return false;
                }
            }
        }
        final String nodeName = node.getNodeName();
        if ("script".equalsIgnoreCase(nodeName)) {
            return false;
        }
        final String textContent = node.getTextContent();
        if (textContent != null) {
            for (int i = 0; i < textContent.length(); ++i) {
                if (Character.isLetter(textContent.charAt(i))) {
                    return true;
                }
            }
        }
        return false;
    }
    
    static {
        COMMA = Pattern.compile(",");
    }
}
