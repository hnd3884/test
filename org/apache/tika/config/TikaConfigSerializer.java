package org.apache.tika.config;

import java.util.Set;
import java.util.Collection;
import org.apache.tika.mime.MediaType;
import java.util.TreeSet;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.multiple.AbstractMultipleParser;
import org.apache.tika.parser.CompositeParser;
import java.util.Collections;
import org.apache.tika.parser.ParserDecorator;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.DefaultParser;
import org.apache.tika.detect.Detector;
import org.apache.tika.detect.CompositeDetector;
import org.apache.tika.detect.DefaultDetector;
import java.util.Iterator;
import java.util.List;
import org.apache.tika.detect.EncodingDetector;
import org.apache.tika.detect.CompositeEncodingDetector;
import org.apache.tika.detect.DefaultEncodingDetector;
import org.apache.tika.language.translate.Translator;
import org.apache.tika.language.translate.DefaultTranslator;
import java.util.concurrent.ExecutorService;
import javax.xml.transform.Transformer;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.Node;
import org.apache.tika.utils.XMLReaderUtils;
import java.nio.charset.Charset;
import java.io.Writer;

public class TikaConfigSerializer
{
    public static void serialize(final TikaConfig config, final Mode mode, final Writer writer, final Charset charset) throws Exception {
        final DocumentBuilder docBuilder = XMLReaderUtils.getDocumentBuilder();
        final Document doc = docBuilder.newDocument();
        final Element rootElement = doc.createElement("properties");
        doc.appendChild(rootElement);
        addMimeComment(mode, rootElement, doc);
        addServiceLoader(mode, rootElement, doc, config);
        addExecutorService(mode, rootElement, doc, config);
        addEncodingDetectors(mode, rootElement, doc, config);
        addTranslator(mode, rootElement, doc, config);
        addDetectors(mode, rootElement, doc, config);
        addParsers(mode, rootElement, doc, config);
        final Transformer transformer = XMLReaderUtils.getTransformer();
        transformer.setOutputProperty("indent", "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.setOutputProperty("encoding", charset.name());
        final DOMSource source = new DOMSource(doc);
        final StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);
    }
    
    private static void addExecutorService(final Mode mode, final Element rootElement, final Document doc, final TikaConfig config) {
        final ExecutorService executor = config.getExecutorService();
    }
    
    private static void addServiceLoader(final Mode mode, final Element rootElement, final Document doc, final TikaConfig config) {
        final ServiceLoader loader = config.getServiceLoader();
        if (mode == Mode.MINIMAL && loader.isDynamic() && loader.getLoadErrorHandler() == LoadErrorHandler.IGNORE) {
            return;
        }
        final Element dslEl = doc.createElement("service-loader");
        dslEl.setAttribute("dynamic", Boolean.toString(loader.isDynamic()));
        dslEl.setAttribute("loadErrorHandler", loader.getLoadErrorHandler().toString());
        rootElement.appendChild(dslEl);
    }
    
    private static void addTranslator(final Mode mode, final Element rootElement, final Document doc, final TikaConfig config) {
        Translator translator = config.getTranslator();
        if (mode == Mode.MINIMAL && translator instanceof DefaultTranslator) {
            final Node mimeComment = doc.createComment("for example: <translator class=\"org.apache.tika.language.translate.GoogleTranslator\"/>");
            rootElement.appendChild(mimeComment);
        }
        else {
            if (translator instanceof DefaultTranslator && (mode == Mode.STATIC || mode == Mode.STATIC_FULL)) {
                translator = ((DefaultTranslator)translator).getTranslator();
            }
            if (translator != null) {
                final Element translatorElement = doc.createElement("translator");
                translatorElement.setAttribute("class", translator.getClass().getCanonicalName());
                rootElement.appendChild(translatorElement);
            }
            else {
                rootElement.appendChild(doc.createComment("No translators available"));
            }
        }
    }
    
    private static void addMimeComment(final Mode mode, final Element rootElement, final Document doc) {
        final Node mimeComment = doc.createComment("for example: <mimeTypeRepository resource=\"/org/apache/tika/mime/tika-mimetypes.xml\"/>");
        rootElement.appendChild(mimeComment);
    }
    
    private static void addEncodingDetectors(final Mode mode, final Element rootElement, final Document doc, final TikaConfig config) throws Exception {
        final EncodingDetector encDetector = config.getEncodingDetector();
        if (mode == Mode.MINIMAL && encDetector instanceof DefaultEncodingDetector) {
            final Node detComment = doc.createComment("for example: <encodingDetectors><encodingDetector class=\"org.apache.tika.detect.DefaultEncodingDetector\"></encodingDetectors>");
            rootElement.appendChild(detComment);
            return;
        }
        final Element encDetectorsElement = doc.createElement("encodingDetectors");
        if ((mode == Mode.CURRENT && encDetector instanceof DefaultEncodingDetector) || !(encDetector instanceof CompositeEncodingDetector)) {
            final Element encDetectorElement = doc.createElement("encodingDetector");
            encDetectorElement.setAttribute("class", encDetector.getClass().getCanonicalName());
            encDetectorsElement.appendChild(encDetectorElement);
        }
        else {
            final List<EncodingDetector> children = ((CompositeEncodingDetector)encDetector).getDetectors();
            for (final EncodingDetector d : children) {
                final Element encDetectorElement2 = doc.createElement("encodingDetector");
                encDetectorElement2.setAttribute("class", d.getClass().getCanonicalName());
                encDetectorsElement.appendChild(encDetectorElement2);
            }
        }
        rootElement.appendChild(encDetectorsElement);
    }
    
    private static void addDetectors(final Mode mode, final Element rootElement, final Document doc, final TikaConfig config) throws Exception {
        final Detector detector = config.getDetector();
        if (mode == Mode.MINIMAL && detector instanceof DefaultDetector) {
            final Node detComment = doc.createComment("for example: <detectors><detector class=\"org.apache.tika.detector.MimeTypes\"></detectors>");
            rootElement.appendChild(detComment);
            return;
        }
        final Element detectorsElement = doc.createElement("detectors");
        if ((mode == Mode.CURRENT && detector instanceof DefaultDetector) || !(detector instanceof CompositeDetector)) {
            final Element detectorElement = doc.createElement("detector");
            detectorElement.setAttribute("class", detector.getClass().getCanonicalName());
            detectorsElement.appendChild(detectorElement);
        }
        else {
            final List<Detector> children = ((CompositeDetector)detector).getDetectors();
            for (final Detector d : children) {
                final Element detectorElement2 = doc.createElement("detector");
                detectorElement2.setAttribute("class", d.getClass().getCanonicalName());
                detectorsElement.appendChild(detectorElement2);
            }
        }
        rootElement.appendChild(detectorsElement);
    }
    
    private static void addParsers(Mode mode, final Element rootElement, final Document doc, final TikaConfig config) throws Exception {
        final Parser parser = config.getParser();
        if (mode == Mode.MINIMAL && parser instanceof DefaultParser) {
            return;
        }
        if (mode == Mode.MINIMAL) {
            mode = Mode.CURRENT;
        }
        final Element parsersElement = doc.createElement("parsers");
        rootElement.appendChild(parsersElement);
        addParser(mode, parsersElement, doc, parser);
    }
    
    private static void addParser(final Mode mode, Element rootElement, final Document doc, Parser parser) throws Exception {
        ParserDecorator decoration = null;
        if (parser instanceof ParserDecorator && parser.getClass().getName().startsWith(ParserDecorator.class.getName() + "$")) {
            decoration = (ParserDecorator)parser;
            parser = decoration.getWrappedParser();
        }
        boolean outputParser = true;
        List<Parser> children = Collections.emptyList();
        if (mode != Mode.CURRENT || !(parser instanceof DefaultParser)) {
            if (parser instanceof CompositeParser) {
                children = ((CompositeParser)parser).getAllComponentParsers();
                if (parser.getClass().equals(CompositeParser.class)) {
                    outputParser = false;
                }
                if (parser instanceof DefaultParser && (mode == Mode.STATIC || mode == Mode.STATIC_FULL)) {
                    outputParser = false;
                }
            }
            else if (parser instanceof AbstractMultipleParser) {
                children = ((AbstractMultipleParser)parser).getAllParsers();
            }
        }
        if (outputParser) {
            rootElement = addParser(mode, rootElement, doc, parser, decoration);
        }
        for (final Parser childParser : children) {
            addParser(mode, rootElement, doc, childParser);
        }
    }
    
    private static Element addParser(final Mode mode, final Element rootElement, final Document doc, final Parser parser, final ParserDecorator decorator) throws Exception {
        final ParseContext context = new ParseContext();
        final Set<MediaType> addedTypes = new TreeSet<MediaType>();
        final Set<MediaType> excludedTypes = new TreeSet<MediaType>();
        if (decorator != null) {
            final Set<MediaType> types = new TreeSet<MediaType>(decorator.getSupportedTypes(context));
            addedTypes.addAll(types);
            for (final MediaType type : parser.getSupportedTypes(context)) {
                if (!types.contains(type)) {
                    excludedTypes.add(type);
                }
                addedTypes.remove(type);
            }
        }
        else if (mode == Mode.STATIC_FULL) {
            addedTypes.addAll(parser.getSupportedTypes(context));
        }
        final String className = parser.getClass().getCanonicalName();
        final Element parserElement = doc.createElement("parser");
        parserElement.setAttribute("class", className);
        rootElement.appendChild(parserElement);
        if (parser instanceof AbstractMultipleParser) {
            final Element paramsElement = doc.createElement("params");
            final Element paramElement = doc.createElement("param");
            paramElement.setAttribute("name", "metadataPolicy");
            paramElement.setAttribute("value", ((AbstractMultipleParser)parser).getMetadataPolicy().toString());
            paramsElement.appendChild(paramElement);
            parserElement.appendChild(paramsElement);
        }
        for (final MediaType type2 : addedTypes) {
            final Element mimeElement = doc.createElement("mime");
            mimeElement.appendChild(doc.createTextNode(type2.toString()));
            parserElement.appendChild(mimeElement);
        }
        for (final MediaType type2 : excludedTypes) {
            final Element mimeElement = doc.createElement("mime-exclude");
            mimeElement.appendChild(doc.createTextNode(type2.toString()));
            parserElement.appendChild(mimeElement);
        }
        return parserElement;
    }
    
    public enum Mode
    {
        MINIMAL, 
        CURRENT, 
        STATIC, 
        STATIC_FULL;
    }
}
