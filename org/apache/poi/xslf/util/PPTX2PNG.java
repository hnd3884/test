package org.apache.poi.xslf.util;

import java.io.InputStream;
import org.apache.poi.poifs.filesystem.FileMagic;
import java.io.FileOutputStream;
import org.apache.poi.sl.draw.EmbeddedExtractor;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.util.GenericRecordJsonWriter;
import java.io.IOException;
import java.awt.Graphics2D;
import java.util.Iterator;
import java.awt.geom.Dimension2D;
import java.util.Set;
import java.awt.image.RenderedImage;
import javax.imageio.ImageIO;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.lang.ref.WeakReference;
import org.apache.poi.sl.draw.Drawable;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import org.apache.poi.util.Dimension2DDouble;
import java.util.Locale;
import java.io.File;
import java.util.regex.Pattern;

public final class PPTX2PNG
{
    private static final String INPUT_PAT_REGEX = "(?<slideno>[^|]+)\\|(?<format>[^|]+)\\|(?<basename>.+)\\.(?<ext>[^.]++)";
    private static final Pattern INPUT_PATTERN;
    private static final String OUTPUT_PAT_REGEX = "${basename}-${slideno}.${format}";
    private String slidenumStr;
    private float scale;
    private File file;
    private String format;
    private File outdir;
    private String outfile;
    private boolean quiet;
    private String outPattern;
    private File dumpfile;
    private String fixSide;
    private boolean ignoreParse;
    private boolean extractEmbedded;
    
    private static void usage(final String error) {
        final String msg = "Usage: PPTX2PNG [options] <ppt or pptx file or 'stdin'>\n" + ((error == null) ? "" : ("Error: " + error + "\n")) + "Options:\n    -scale <float>    scale factor\n    -fixSide <side>   specify side (long,short,width,height) to fix - use <scale> as amount of pixels\n    -slide <integer>  1-based index of a slide to render\n    -format <type>    png,gif,jpg (,null for testing)\n    -outdir <dir>     output directory, defaults to origin of the ppt/pptx file\n    -outfile <file>   output filename, defaults to '" + "${basename}-${slideno}.${format}" + "'\n    -outpat <pattern> output filename pattern, defaults to '" + "${basename}-${slideno}.${format}" + "'\n                      patterns: basename, slideno, format, ext\n    -dump <file>      dump the annotated records to a file\n    -quiet            do not write to console (for normal processing)\n    -ignoreParse      ignore parsing error and continue with the records read until the error\n    -extractEmbedded  extract embedded parts";
        System.out.println(msg);
    }
    
    public static void main(final String[] args) throws Exception {
        final PPTX2PNG p2p = new PPTX2PNG();
        if (p2p.parseCommandLine(args)) {
            p2p.processFile();
        }
    }
    
    private PPTX2PNG() {
        this.slidenumStr = "-1";
        this.scale = 1.0f;
        this.file = null;
        this.format = "png";
        this.outdir = null;
        this.outfile = null;
        this.quiet = false;
        this.outPattern = "${basename}-${slideno}.${format}";
        this.dumpfile = null;
        this.fixSide = "scale";
        this.ignoreParse = false;
        this.extractEmbedded = false;
    }
    
    private boolean parseCommandLine(final String[] args) {
        if (args.length == 0) {
            usage(null);
            return false;
        }
        for (int i = 0; i < args.length; ++i) {
            final String opt = (i + 1 < args.length) ? args[i + 1] : null;
            final String s = args[i];
            switch (s) {
                case "-scale": {
                    if (opt != null) {
                        this.scale = Float.parseFloat(opt);
                        ++i;
                        break;
                    }
                    break;
                }
                case "-slide": {
                    this.slidenumStr = opt;
                    ++i;
                    break;
                }
                case "-format": {
                    this.format = opt;
                    ++i;
                    break;
                }
                case "-outdir": {
                    if (opt != null) {
                        this.outdir = new File(opt);
                        ++i;
                        break;
                    }
                    break;
                }
                case "-outfile": {
                    this.outfile = opt;
                    ++i;
                    break;
                }
                case "-outpat": {
                    this.outPattern = opt;
                    ++i;
                    break;
                }
                case "-quiet": {
                    this.quiet = true;
                    break;
                }
                case "-dump": {
                    if (opt != null) {
                        this.dumpfile = new File(opt);
                        ++i;
                        break;
                    }
                    this.dumpfile = new File("pptx2png.dump");
                    break;
                }
                case "-fixside": {
                    if (opt != null) {
                        this.fixSide = opt.toLowerCase(Locale.ROOT);
                        ++i;
                        break;
                    }
                    this.fixSide = "long";
                    break;
                }
                case "-ignoreParse": {
                    this.ignoreParse = true;
                    break;
                }
                case "-extractEmbedded": {
                    this.extractEmbedded = true;
                    break;
                }
                default: {
                    this.file = new File(args[i]);
                    break;
                }
            }
        }
        final boolean isStdin = this.file != null && "stdin".equalsIgnoreCase(this.file.getName());
        if (!isStdin && (this.file == null || !this.file.exists())) {
            usage("File not specified or it doesn't exist");
            return false;
        }
        if (this.format == null || !this.format.matches("^(png|gif|jpg|null)$")) {
            usage("Invalid format given");
            return false;
        }
        if (this.outdir == null) {
            if (isStdin) {
                usage("When reading from STDIN, you need to specify an outdir.");
                return false;
            }
            this.outdir = this.file.getParentFile();
        }
        if (!this.outdir.exists()) {
            usage("Outdir doesn't exist");
            return false;
        }
        if (!"null".equals(this.format) && (this.outdir == null || !this.outdir.exists() || !this.outdir.isDirectory())) {
            usage("Output directory doesn't exist");
            return false;
        }
        if (this.scale < 0.0f) {
            usage("Invalid scale given");
            return false;
        }
        if (!"long,short,width,height,scale".contains(this.fixSide)) {
            usage("<fixside> must be one of long / short / width / height");
            return false;
        }
        return true;
    }
    
    private void processFile() throws IOException {
        if (!this.quiet) {
            System.out.println("Processing " + this.file);
        }
        try (final MFProxy proxy = this.initProxy(this.file)) {
            final Set<Integer> slidenum = proxy.slideIndexes(this.slidenumStr);
            if (slidenum.isEmpty()) {
                usage("slidenum must be either -1 (for all) or within range: [1.." + proxy.getSlideCount() + "] for " + this.file);
                return;
            }
            final Dimension2D dim = (Dimension2D)new Dimension2DDouble();
            final double lenSide = this.getDimensions(proxy, dim);
            final int width = Math.max((int)Math.rint(dim.getWidth()), 1);
            final int height = Math.max((int)Math.rint(dim.getHeight()), 1);
            for (final int slideNo : slidenum) {
                proxy.setSlideNo(slideNo);
                if (!this.quiet) {
                    final String title = proxy.getTitle();
                    System.out.println("Rendering slide " + slideNo + ((title == null) ? "" : (": " + title.trim())));
                }
                this.dumpRecords(proxy);
                this.extractEmbedded(proxy, slideNo);
                final BufferedImage img = new BufferedImage(width, height, 2);
                final Graphics2D graphics = img.createGraphics();
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
                graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
                graphics.setRenderingHint((RenderingHints.Key)Drawable.BUFFERED_IMAGE, new WeakReference(img));
                graphics.scale(this.scale / lenSide, this.scale / lenSide);
                graphics.setComposite(AlphaComposite.Clear);
                graphics.fillRect(0, 0, width, height);
                graphics.setComposite(AlphaComposite.SrcOver);
                proxy.draw(graphics);
                if (!"null".equals(this.format)) {
                    ImageIO.write(img, this.format, new File(this.outdir, this.calcOutFile(proxy, slideNo)));
                }
                graphics.dispose();
                img.flush();
            }
        }
        catch (final NoScratchpadException e) {
            usage("'" + this.file.getName() + "': Format not supported - try to include poi-scratchpad.jar into the CLASSPATH.");
            return;
        }
        if (!this.quiet) {
            System.out.println("Done");
        }
    }
    
    private double getDimensions(final MFProxy proxy, final Dimension2D dim) {
        final Dimension2D pgsize = proxy.getSize();
        final String fixSide = this.fixSide;
        double lenSide = 0.0;
        switch (fixSide) {
            default: {
                lenSide = 1.0;
                break;
            }
            case "long": {
                lenSide = Math.max(pgsize.getWidth(), pgsize.getHeight());
                break;
            }
            case "short": {
                lenSide = Math.min(pgsize.getWidth(), pgsize.getHeight());
                break;
            }
            case "width": {
                lenSide = pgsize.getWidth();
                break;
            }
            case "height": {
                lenSide = pgsize.getHeight();
                break;
            }
        }
        dim.setSize(pgsize.getWidth() * this.scale / lenSide, pgsize.getHeight() * this.scale / lenSide);
        return lenSide;
    }
    
    private void dumpRecords(final MFProxy proxy) throws IOException {
        if (this.dumpfile == null) {
            return;
        }
        final GenericRecord gr = proxy.getRoot();
        try (final GenericRecordJsonWriter fw = new GenericRecordJsonWriter(this.dumpfile) {
            protected boolean printBytes(final String name, final Object o) {
                return false;
            }
        }) {
            if (gr == null) {
                fw.writeError(this.file.getName() + " doesn't support GenericRecord interface and can't be dumped to a file.");
            }
            else {
                fw.write(gr);
            }
        }
    }
    
    private void extractEmbedded(final MFProxy proxy, final int slideNo) throws IOException {
        if (!this.extractEmbedded) {
            return;
        }
        for (final EmbeddedExtractor.EmbeddedPart ep : proxy.getEmbeddings(slideNo)) {
            String filename = ep.getName();
            filename = new File((filename == null) ? "dummy.dat" : filename).getName();
            filename = this.calcOutFile(proxy, slideNo).replaceFirst("\\.\\w+$", "") + "_" + filename;
            try (final FileOutputStream fos = new FileOutputStream(new File(this.outdir, filename))) {
                fos.write(ep.getData().get());
            }
        }
    }
    
    private MFProxy initProxy(final File file) throws IOException {
        final String fileName = file.getName().toLowerCase(Locale.ROOT);
        MFProxy proxy = null;
        if ("stdin".equals(fileName)) {
            final InputStream bis = FileMagic.prepareToCheckMagic(System.in);
            final FileMagic fm = FileMagic.valueOf(bis);
            switch (fm) {
                case EMF: {
                    proxy = new EMFHandler();
                    break;
                }
                case WMF: {
                    proxy = new WMFHandler();
                    break;
                }
                default: {
                    proxy = new PPTHandler();
                    break;
                }
            }
            proxy.setIgnoreParse(this.ignoreParse);
            proxy.setQuite(this.quiet);
            proxy.parse(bis);
        }
        else {
            final String s = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(46)) : "";
            switch (s) {
                case ".emf": {
                    proxy = new EMFHandler();
                    break;
                }
                case ".wmf": {
                    proxy = new WMFHandler();
                    break;
                }
                default: {
                    proxy = new PPTHandler();
                    break;
                }
            }
            proxy.parse(file);
        }
        return proxy;
    }
    
    private String calcOutFile(final MFProxy proxy, final int slideNo) {
        if (this.outfile != null) {
            return this.outfile;
        }
        final String inname = String.format(Locale.ROOT, "%04d|%s|%s", slideNo, this.format, this.file.getName());
        final String outpat = (proxy.getSlideCount() > 1) ? this.outPattern : this.outPattern.replaceAll("-?\\$\\{slideno}", "");
        return PPTX2PNG.INPUT_PATTERN.matcher(inname).replaceAll(outpat);
    }
    
    static {
        INPUT_PATTERN = Pattern.compile("(?<slideno>[^|]+)\\|(?<format>[^|]+)\\|(?<basename>.+)\\.(?<ext>[^.]++)");
    }
    
    static class NoScratchpadException extends IOException
    {
        NoScratchpadException() {
        }
        
        NoScratchpadException(final Throwable cause) {
            super(cause);
        }
    }
}
