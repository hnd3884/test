package org.apache.poi.ooxml.extractor;

import org.apache.poi.extractor.POITextExtractor;
import java.io.File;

public class CommandLineTextExtractor
{
    public static final String DIVIDER = "=======================";
    
    public static void main(final String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Use:");
            System.err.println("   CommandLineTextExtractor <filename> [filename] [filename]");
            System.exit(1);
        }
        for (final String arg : args) {
            System.out.println("=======================");
            final File f = new File(arg);
            System.out.println(f);
            try (final POITextExtractor extractor = ExtractorFactory.createExtractor(f)) {
                final POITextExtractor metadataExtractor = extractor.getMetadataTextExtractor();
                System.out.println("   =======================");
                final String metaData = metadataExtractor.getText();
                System.out.println(metaData);
                System.out.println("   =======================");
                final String text = extractor.getText();
                System.out.println(text);
                System.out.println("=======================");
                System.out.println("Had " + metaData.length() + " characters of metadata and " + text.length() + " characters of text");
            }
        }
    }
}
