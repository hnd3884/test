package org.apache.poi.poifs.macros;

import java.util.Iterator;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import org.apache.poi.util.StringUtil;
import java.io.FileOutputStream;
import java.util.Map;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;

public class VBAMacroExtractor
{
    public static void main(final String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("Use:");
            System.err.println("   VBAMacroExtractor <office.doc> [output]");
            System.err.println();
            System.err.println("If an output directory is given, macros are written there");
            System.err.println("Otherwise they are output to the screen");
            System.exit(1);
        }
        final File input = new File(args[0]);
        File output = null;
        if (args.length > 1) {
            output = new File(args[1]);
        }
        final VBAMacroExtractor extractor = new VBAMacroExtractor();
        extractor.extract(input, output);
    }
    
    public void extract(final File input, final File outputDir, final String extension) throws IOException {
        if (!input.exists()) {
            throw new FileNotFoundException(input.toString());
        }
        System.err.print("Extracting VBA Macros from " + input + " to ");
        if (outputDir != null) {
            if (!outputDir.exists() && !outputDir.mkdirs()) {
                throw new IOException("Output directory " + outputDir + " could not be created");
            }
            System.err.println(outputDir);
        }
        else {
            System.err.println("STDOUT");
        }
        Map<String, String> macros;
        try (final VBAMacroReader reader = new VBAMacroReader(input)) {
            macros = reader.readMacros();
        }
        final String divider = "---------------------------------------";
        for (final Map.Entry<String, String> entry : macros.entrySet()) {
            final String moduleName = entry.getKey();
            final String moduleCode = entry.getValue();
            if (outputDir == null) {
                System.out.println("---------------------------------------");
                System.out.println(moduleName);
                System.out.println();
                System.out.println(moduleCode);
            }
            else {
                final File out = new File(outputDir, moduleName + extension);
                try (final FileOutputStream fout = new FileOutputStream(out);
                     final OutputStreamWriter fwriter = new OutputStreamWriter(fout, StringUtil.UTF8)) {
                    fwriter.write(moduleCode);
                }
                System.out.println("Extracted " + out);
            }
        }
        if (outputDir == null) {
            System.out.println("---------------------------------------");
        }
    }
    
    public void extract(final File input, final File outputDir) throws IOException {
        this.extract(input, outputDir, ".vba");
    }
}
