package org.apache.tika.parser.external;

import org.apache.tika.parser.Parser;
import org.apache.tika.parser.CompositeParser;
import org.apache.tika.config.TikaConfig;
import java.io.InputStream;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.net.URL;
import java.util.Collections;
import org.apache.tika.exception.TikaException;
import java.io.IOException;
import org.apache.tika.config.ServiceLoader;
import java.util.List;

public class ExternalParsersFactory
{
    public static List<ExternalParser> create() throws IOException, TikaException {
        return create(new ServiceLoader());
    }
    
    public static List<ExternalParser> create(final ServiceLoader loader) throws IOException, TikaException {
        return create("tika-external-parsers.xml", loader);
    }
    
    public static List<ExternalParser> create(final String filename, final ServiceLoader loader) throws IOException, TikaException {
        final String filepath = ExternalParsersFactory.class.getPackage().getName().replace('.', '/') + "/" + filename;
        final Enumeration<URL> files = loader.findServiceResources(filepath);
        final ArrayList<URL> list = Collections.list(files);
        final URL[] urls = list.toArray(new URL[0]);
        return create(urls);
    }
    
    public static List<ExternalParser> create(final URL... urls) throws IOException, TikaException {
        final List<ExternalParser> parsers = new ArrayList<ExternalParser>();
        for (final URL url : urls) {
            try (final InputStream stream = url.openStream()) {
                parsers.addAll(ExternalParsersConfigReader.read(stream));
            }
        }
        return parsers;
    }
    
    public static void attachExternalParsers(final TikaConfig config) throws IOException, TikaException {
        attachExternalParsers(create(), config);
    }
    
    public static void attachExternalParsers(final List<ExternalParser> parsers, final TikaConfig config) {
        final Parser parser = config.getParser();
        if (parser instanceof CompositeParser) {
            final CompositeParser cParser = (CompositeParser)parser;
            cParser.getParsers();
        }
    }
}
