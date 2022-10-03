package com.google.zxing.client.j2se;

import java.util.Collection;
import com.google.zxing.BarcodeFormat;
import java.util.EnumMap;
import com.google.zxing.DecodeHintType;
import java.util.Map;
import java.io.IOException;
import java.util.Locale;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

public final class CommandLineRunner
{
    private CommandLineRunner() {
    }
    
    public static void main(final String[] args) throws Exception {
        if (args.length == 0) {
            printUsage();
            return;
        }
        final Config config = new Config();
        final Inputs inputs = new Inputs();
        for (final String arg : args) {
            if ("--try_harder".equals(arg)) {
                config.setTryHarder(true);
            }
            else if ("--pure_barcode".equals(arg)) {
                config.setPureBarcode(true);
            }
            else if ("--products_only".equals(arg)) {
                config.setProductsOnly(true);
            }
            else if ("--dump_results".equals(arg)) {
                config.setDumpResults(true);
            }
            else if ("--dump_black_point".equals(arg)) {
                config.setDumpBlackPoint(true);
            }
            else if ("--multi".equals(arg)) {
                config.setMulti(true);
            }
            else if ("--brief".equals(arg)) {
                config.setBrief(true);
            }
            else if ("--recursive".equals(arg)) {
                config.setRecursive(true);
            }
            else if (arg.startsWith("--crop")) {
                final int[] crop = new int[4];
                final String[] tokens = arg.substring(7).split(",");
                for (int i = 0; i < crop.length; ++i) {
                    crop[i] = Integer.parseInt(tokens[i]);
                }
                config.setCrop(crop);
            }
            else if (arg.startsWith("--threads") && arg.length() >= 10) {
                final int threads = Integer.parseInt(arg.substring(10));
                if (threads > 1) {
                    config.setThreads(threads);
                }
            }
            else if (arg.startsWith("-")) {
                System.err.println("Unknown command line option " + arg);
                printUsage();
                return;
            }
        }
        config.setHints(buildHints(config));
        for (final String arg : args) {
            if (!arg.startsWith("--")) {
                addArgumentToInputs(arg, config, inputs);
            }
        }
        final List<DecodeThread> threads2 = new ArrayList<DecodeThread>(config.getThreads());
        for (int x = 0; x < config.getThreads(); ++x) {
            final DecodeThread thread = new DecodeThread(config, inputs);
            threads2.add(thread);
            thread.start();
        }
        int successful = 0;
        for (int x2 = 0; x2 < config.getThreads(); ++x2) {
            threads2.get(x2).join();
            successful += threads2.get(x2).getSuccessful();
        }
        final int total = inputs.getInputCount();
        if (total > 1) {
            System.out.println("\nDecoded " + successful + " files out of " + total + " successfully (" + successful * 100 / total + "%)\n");
        }
    }
    
    private static void addArgumentToInputs(final String argument, final Config config, final Inputs inputs) throws IOException {
        final File inputFile = new File(argument);
        if (inputFile.exists()) {
            if (inputFile.isDirectory()) {
                for (final File singleFile : inputFile.listFiles()) {
                    final String filename = singleFile.getName().toLowerCase(Locale.ENGLISH);
                    if (!filename.startsWith(".")) {
                        if (singleFile.isDirectory()) {
                            if (config.isRecursive()) {
                                addArgumentToInputs(singleFile.getAbsolutePath(), config, inputs);
                            }
                        }
                        else if (!filename.endsWith(".txt")) {
                            if (!filename.contains(".mono.png")) {
                                inputs.addInput(singleFile.getCanonicalPath());
                            }
                        }
                    }
                }
            }
            else {
                inputs.addInput(inputFile.getCanonicalPath());
            }
        }
        else {
            inputs.addInput(argument);
        }
    }
    
    private static Map<DecodeHintType, ?> buildHints(final Config config) {
        final Map<DecodeHintType, Object> hints = new EnumMap<DecodeHintType, Object>(DecodeHintType.class);
        final Collection<BarcodeFormat> vector = new ArrayList<BarcodeFormat>(8);
        vector.add(BarcodeFormat.UPC_A);
        vector.add(BarcodeFormat.UPC_E);
        vector.add(BarcodeFormat.EAN_13);
        vector.add(BarcodeFormat.EAN_8);
        vector.add(BarcodeFormat.RSS_14);
        vector.add(BarcodeFormat.RSS_EXPANDED);
        if (!config.isProductsOnly()) {
            vector.add(BarcodeFormat.CODE_39);
            vector.add(BarcodeFormat.CODE_93);
            vector.add(BarcodeFormat.CODE_128);
            vector.add(BarcodeFormat.ITF);
            vector.add(BarcodeFormat.QR_CODE);
            vector.add(BarcodeFormat.DATA_MATRIX);
            vector.add(BarcodeFormat.AZTEC);
            vector.add(BarcodeFormat.PDF_417);
            vector.add(BarcodeFormat.CODABAR);
            vector.add(BarcodeFormat.MAXICODE);
        }
        hints.put(DecodeHintType.POSSIBLE_FORMATS, vector);
        if (config.isTryHarder()) {
            hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        }
        if (config.isPureBarcode()) {
            hints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
        }
        return hints;
    }
    
    private static void printUsage() {
        System.err.println("Decode barcode images using the ZXing library\n");
        System.err.println("usage: CommandLineRunner { file | dir | url } [ options ]");
        System.err.println("  --try_harder: Use the TRY_HARDER hint, default is normal (mobile) mode");
        System.err.println("  --pure_barcode: Input image is a pure monochrome barcode image, not a photo");
        System.err.println("  --products_only: Only decode the UPC and EAN families of barcodes");
        System.err.println("  --dump_results: Write the decoded contents to input.txt");
        System.err.println("  --dump_black_point: Compare black point algorithms as input.mono.png");
        System.err.println("  --multi: Scans image for multiple barcodes");
        System.err.println("  --brief: Only output one line per file, omitting the contents");
        System.err.println("  --recursive: Descend into subdirectories");
        System.err.println("  --crop=left,top,width,height: Only examine cropped region of input image(s)");
        System.err.println("  --threads=n: The number of threads to use while decoding");
    }
}
