package sun.print;

import java.util.Date;
import java.awt.print.Pageable;
import java.awt.Component;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.awt.geom.PathIterator;
import java.awt.print.Paper;
import java.nio.charset.CoderMalfunctionError;
import java.nio.CharBuffer;
import java.nio.ByteBuffer;
import java.awt.font.FontRenderContext;
import java.awt.Graphics;
import sun.awt.PlatformFont;
import java.nio.charset.CharsetEncoder;
import sun.awt.FontConfiguration;
import sun.font.FontUtilities;
import sun.awt.CharsetString;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.print.PrinterJob;
import java.awt.print.Printable;
import java.awt.print.PageFormat;
import javax.print.attribute.standard.Sides;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.security.PrivilegedAction;
import java.security.AccessController;
import java.io.IOException;
import java.awt.print.PrinterIOException;
import java.io.FileOutputStream;
import java.awt.print.PrinterException;
import javax.print.StreamPrintService;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.PrintService;
import javax.print.attribute.standard.PrinterName;
import javax.print.attribute.standard.Destination;
import javax.print.attribute.standard.DialogTypeSelection;
import java.util.Locale;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.Attribute;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.HashPrintRequestAttributeSet;
import java.awt.HeadlessException;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import sun.awt.SunToolkit;
import java.util.Properties;
import java.util.ArrayList;
import java.io.File;
import java.io.PrintStream;
import java.awt.FontMetrics;
import java.awt.geom.AffineTransform;
import java.awt.Shape;
import java.awt.Color;
import java.awt.Font;

public class PSPrinterJob extends RasterPrinterJob
{
    protected static final int FILL_EVEN_ODD = 1;
    protected static final int FILL_WINDING = 2;
    private static final int MAX_PSSTR = 65535;
    private static final int RED_MASK = 16711680;
    private static final int GREEN_MASK = 65280;
    private static final int BLUE_MASK = 255;
    private static final int RED_SHIFT = 16;
    private static final int GREEN_SHIFT = 8;
    private static final int BLUE_SHIFT = 0;
    private static final int LOWNIBBLE_MASK = 15;
    private static final int HINIBBLE_MASK = 240;
    private static final int HINIBBLE_SHIFT = 4;
    private static final byte[] hexDigits;
    private static final int PS_XRES = 300;
    private static final int PS_YRES = 300;
    private static final String ADOBE_PS_STR = "%!PS-Adobe-3.0";
    private static final String EOF_COMMENT = "%%EOF";
    private static final String PAGE_COMMENT = "%%Page: ";
    private static final String READIMAGEPROC = "/imStr 0 def /imageSrc {currentfile /ASCII85Decode filter /RunLengthDecode filter  imStr readstring pop } def";
    private static final String COPIES = "/#copies exch def";
    private static final String PAGE_SAVE = "/pgSave save def";
    private static final String PAGE_RESTORE = "pgSave restore";
    private static final String SHOWPAGE = "showpage";
    private static final String IMAGE_SAVE = "/imSave save def";
    private static final String IMAGE_STR = " string /imStr exch def";
    private static final String IMAGE_RESTORE = "imSave restore";
    private static final String COORD_PREP = " 0 exch translate 1 -1 scale[72 300 div 0 0 72 300 div 0 0]concat";
    private static final String SetFontName = "F";
    private static final String DrawStringName = "S";
    private static final String EVEN_ODD_FILL_STR = "EF";
    private static final String WINDING_FILL_STR = "WF";
    private static final String EVEN_ODD_CLIP_STR = "EC";
    private static final String WINDING_CLIP_STR = "WC";
    private static final String MOVETO_STR = " M";
    private static final String LINETO_STR = " L";
    private static final String CURVETO_STR = " C";
    private static final String GRESTORE_STR = "R";
    private static final String GSAVE_STR = "G";
    private static final String NEWPATH_STR = "N";
    private static final String CLOSEPATH_STR = "P";
    private static final String SETRGBCOLOR_STR = " SC";
    private static final String SETGRAY_STR = " SG";
    private int mDestType;
    private String mDestination;
    private boolean mNoJobSheet;
    private String mOptions;
    private Font mLastFont;
    private Color mLastColor;
    private Shape mLastClip;
    private AffineTransform mLastTransform;
    private EPSPrinter epsPrinter;
    FontMetrics mCurMetrics;
    PrintStream mPSStream;
    File spoolFile;
    private String mFillOpStr;
    private String mClipOpStr;
    ArrayList mGStateStack;
    private float mPenX;
    private float mPenY;
    private float mStartPathX;
    private float mStartPathY;
    private static Properties mFontProps;
    private static boolean isMac;
    
    private static Properties initProps() {
        final String property = System.getProperty("java.home");
        if (property != null) {
            final String language = SunToolkit.getStartupLocale().getLanguage();
            try {
                File file = new File(property + File.separator + "lib" + File.separator + "psfontj2d.properties." + language);
                if (!file.canRead()) {
                    file = new File(property + File.separator + "lib" + File.separator + "psfont.properties." + language);
                    if (!file.canRead()) {
                        file = new File(property + File.separator + "lib" + File.separator + "psfontj2d.properties");
                        if (!file.canRead()) {
                            file = new File(property + File.separator + "lib" + File.separator + "psfont.properties");
                            if (!file.canRead()) {
                                return null;
                            }
                        }
                    }
                }
                final BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file.getPath()));
                final Properties properties = new Properties();
                properties.load(bufferedInputStream);
                bufferedInputStream.close();
                return properties;
            }
            catch (final Exception ex) {
                return null;
            }
        }
        return null;
    }
    
    public PSPrinterJob() {
        this.mDestination = "lp";
        this.mNoJobSheet = false;
        this.epsPrinter = null;
        this.mFillOpStr = "WF";
        this.mClipOpStr = "WC";
        this.mGStateStack = new ArrayList();
    }
    
    @Override
    public boolean printDialog() throws HeadlessException {
        if (GraphicsEnvironment.isHeadless()) {
            throw new HeadlessException();
        }
        if (this.attributes == null) {
            this.attributes = new HashPrintRequestAttributeSet();
        }
        this.attributes.add(new Copies(this.getCopies()));
        this.attributes.add(new JobName(this.getJobName(), null));
        boolean b;
        if (this.attributes.get(DialogTypeSelection.class) == DialogTypeSelection.NATIVE) {
            this.attributes.remove(DialogTypeSelection.class);
            b = this.printDialog(this.attributes);
            this.attributes.add(DialogTypeSelection.NATIVE);
        }
        else {
            b = this.printDialog(this.attributes);
        }
        if (b) {
            final JobName jobName = (JobName)this.attributes.get(JobName.class);
            if (jobName != null) {
                this.setJobName(jobName.getValue());
            }
            final Copies copies = (Copies)this.attributes.get(Copies.class);
            if (copies != null) {
                this.setCopies(copies.getValue());
            }
            final Destination destination = (Destination)this.attributes.get(Destination.class);
            if (destination != null) {
                try {
                    this.mDestType = 1;
                    this.mDestination = new File(destination.getURI()).getPath();
                }
                catch (final Exception ex) {
                    this.mDestination = "out.ps";
                }
            }
            else {
                this.mDestType = 0;
                final PrintService printService = this.getPrintService();
                if (printService != null) {
                    this.mDestination = printService.getName();
                    if (PSPrinterJob.isMac) {
                        final PrintServiceAttributeSet attributes = printService.getAttributes();
                        if (attributes != null) {
                            this.mDestination = attributes.get(PrinterName.class).toString();
                        }
                    }
                }
            }
        }
        return b;
    }
    
    @Override
    protected void startDoc() throws PrinterException {
        if (this.epsPrinter == null) {
            OutputStream outputStream = null;
            Label_0177: {
                if (this.getPrintService() instanceof PSStreamPrintService) {
                    final StreamPrintService streamPrintService = (StreamPrintService)this.getPrintService();
                    this.mDestType = 2;
                    if (streamPrintService.isDisposed()) {
                        throw new PrinterException("service is disposed");
                    }
                    outputStream = streamPrintService.getOutputStream();
                    if (outputStream == null) {
                        throw new PrinterException("Null output stream");
                    }
                }
                else {
                    this.mNoJobSheet = super.noJobSheet;
                    if (super.destinationAttr != null) {
                        this.mDestType = 1;
                        this.mDestination = super.destinationAttr;
                    }
                    if (this.mDestType == 1) {
                        try {
                            this.spoolFile = new File(this.mDestination);
                            outputStream = new FileOutputStream(this.spoolFile);
                            break Label_0177;
                        }
                        catch (final IOException ex) {
                            throw new PrinterIOException(ex);
                        }
                    }
                    final PrinterOpener printerOpener = new PrinterOpener();
                    AccessController.doPrivileged((PrivilegedAction<Object>)printerOpener);
                    if (printerOpener.pex != null) {
                        throw printerOpener.pex;
                    }
                    outputStream = printerOpener.result;
                }
            }
            (this.mPSStream = new PrintStream(new BufferedOutputStream(outputStream))).println("%!PS-Adobe-3.0");
        }
        this.mPSStream.println("%%BeginProlog");
        this.mPSStream.println("/imStr 0 def /imageSrc {currentfile /ASCII85Decode filter /RunLengthDecode filter  imStr readstring pop } def");
        this.mPSStream.println("/BD {bind def} bind def");
        this.mPSStream.println("/D {def} BD");
        this.mPSStream.println("/C {curveto} BD");
        this.mPSStream.println("/L {lineto} BD");
        this.mPSStream.println("/M {moveto} BD");
        this.mPSStream.println("/R {grestore} BD");
        this.mPSStream.println("/G {gsave} BD");
        this.mPSStream.println("/N {newpath} BD");
        this.mPSStream.println("/P {closepath} BD");
        this.mPSStream.println("/EC {eoclip} BD");
        this.mPSStream.println("/WC {clip} BD");
        this.mPSStream.println("/EF {eofill} BD");
        this.mPSStream.println("/WF {fill} BD");
        this.mPSStream.println("/SG {setgray} BD");
        this.mPSStream.println("/SC {setrgbcolor} BD");
        this.mPSStream.println("/ISOF {");
        this.mPSStream.println("     dup findfont dup length 1 add dict begin {");
        this.mPSStream.println("             1 index /FID eq {pop pop} {D} ifelse");
        this.mPSStream.println("     } forall /Encoding ISOLatin1Encoding D");
        this.mPSStream.println("     currentdict end definefont");
        this.mPSStream.println("} BD");
        this.mPSStream.println("/NZ {dup 1 lt {pop 1} if} BD");
        this.mPSStream.println("/S {");
        this.mPSStream.println("     moveto 1 index stringwidth pop NZ sub");
        this.mPSStream.println("     1 index length 1 sub NZ div 0");
        this.mPSStream.println("     3 2 roll ashow newpath} BD");
        this.mPSStream.println("/FL [");
        if (PSPrinterJob.mFontProps == null) {
            this.mPSStream.println(" /Helvetica ISOF");
            this.mPSStream.println(" /Helvetica-Bold ISOF");
            this.mPSStream.println(" /Helvetica-Oblique ISOF");
            this.mPSStream.println(" /Helvetica-BoldOblique ISOF");
            this.mPSStream.println(" /Times-Roman ISOF");
            this.mPSStream.println(" /Times-Bold ISOF");
            this.mPSStream.println(" /Times-Italic ISOF");
            this.mPSStream.println(" /Times-BoldItalic ISOF");
            this.mPSStream.println(" /Courier ISOF");
            this.mPSStream.println(" /Courier-Bold ISOF");
            this.mPSStream.println(" /Courier-Oblique ISOF");
            this.mPSStream.println(" /Courier-BoldOblique ISOF");
        }
        else {
            for (int int1 = Integer.parseInt(PSPrinterJob.mFontProps.getProperty("font.num", "9")), i = 0; i < int1; ++i) {
                this.mPSStream.println("    /" + PSPrinterJob.mFontProps.getProperty("font." + String.valueOf(i), "Courier ISOF"));
            }
        }
        this.mPSStream.println("] D");
        this.mPSStream.println("/F {");
        this.mPSStream.println("     FL exch get exch scalefont");
        this.mPSStream.println("     [1 0 0 -1 0 0] makefont setfont} BD");
        this.mPSStream.println("%%EndProlog");
        this.mPSStream.println("%%BeginSetup");
        if (this.epsPrinter == null) {
            final PageFormat pageFormat = this.getPageable().getPageFormat(0);
            this.mPSStream.print("<< /PageSize [" + pageFormat.getPaper().getWidth() + " " + pageFormat.getPaper().getHeight() + "]");
            if (AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction() {
                final /* synthetic */ PrintService val$pservice = this.getPrintService();
                
                @Override
                public Object run() {
                    try {
                        final Class<?> forName = Class.forName("sun.print.IPPPrintService");
                        if (forName.isInstance(this.val$pservice)) {
                            return forName.getMethod("isPostscript", (Class[])null).invoke(this.val$pservice, (Object[])null);
                        }
                    }
                    catch (final Throwable t) {}
                    return Boolean.TRUE;
                }
            })) {
                this.mPSStream.print(" /DeferredMediaSelection true");
            }
            this.mPSStream.print(" /ImagingBBox null /ManualFeed false");
            this.mPSStream.print(this.isCollated() ? " /Collate true" : "");
            this.mPSStream.print(" /NumCopies " + this.getCopiesInt());
            if (this.sidesAttr != Sides.ONE_SIDED) {
                if (this.sidesAttr == Sides.TWO_SIDED_LONG_EDGE) {
                    this.mPSStream.print(" /Duplex true ");
                }
                else if (this.sidesAttr == Sides.TWO_SIDED_SHORT_EDGE) {
                    this.mPSStream.print(" /Duplex true /Tumble true ");
                }
            }
            this.mPSStream.println(" >> setpagedevice ");
        }
        this.mPSStream.println("%%EndSetup");
    }
    
    @Override
    protected void abortDoc() {
        if (this.mPSStream != null && this.mDestType != 2) {
            this.mPSStream.close();
        }
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            @Override
            public Object run() {
                if (PSPrinterJob.this.spoolFile != null && PSPrinterJob.this.spoolFile.exists()) {
                    PSPrinterJob.this.spoolFile.delete();
                }
                return null;
            }
        });
    }
    
    @Override
    protected void endDoc() throws PrinterException {
        if (this.mPSStream != null) {
            this.mPSStream.println("%%EOF");
            this.mPSStream.flush();
            if (this.mDestType != 2) {
                this.mPSStream.close();
            }
        }
        if (this.mDestType == 0) {
            final PrintService printService = this.getPrintService();
            if (printService != null) {
                this.mDestination = printService.getName();
                if (PSPrinterJob.isMac) {
                    final PrintServiceAttributeSet attributes = printService.getAttributes();
                    if (attributes != null) {
                        this.mDestination = attributes.get(PrinterName.class).toString();
                    }
                }
            }
            final PrinterSpooler printerSpooler = new PrinterSpooler();
            AccessController.doPrivileged((PrivilegedAction<Object>)printerSpooler);
            if (printerSpooler.pex != null) {
                throw printerSpooler.pex;
            }
        }
    }
    
    @Override
    protected void startPage(final PageFormat pageFormat, final Printable printable, final int n, final boolean b) throws PrinterException {
        final double height = pageFormat.getPaper().getHeight();
        final double width = pageFormat.getPaper().getWidth();
        final int n2 = n + 1;
        (this.mGStateStack = new ArrayList()).add(new GState());
        this.mPSStream.println("%%Page: " + n2 + " " + n2);
        if (n > 0 && b) {
            this.mPSStream.print("<< /PageSize [" + width + " " + height + "]");
            if (AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction() {
                final /* synthetic */ PrintService val$pservice = this.getPrintService();
                
                @Override
                public Object run() {
                    try {
                        final Class<?> forName = Class.forName("sun.print.IPPPrintService");
                        if (forName.isInstance(this.val$pservice)) {
                            return forName.getMethod("isPostscript", (Class[])null).invoke(this.val$pservice, (Object[])null);
                        }
                    }
                    catch (final Throwable t) {}
                    return Boolean.TRUE;
                }
            })) {
                this.mPSStream.print(" /DeferredMediaSelection true");
            }
            this.mPSStream.println(" >> setpagedevice");
        }
        this.mPSStream.println("/pgSave save def");
        this.mPSStream.println(height + " 0 exch translate 1 -1 scale[72 300 div 0 0 72 300 div 0 0]concat");
    }
    
    @Override
    protected void endPage(final PageFormat pageFormat, final Printable printable, final int n) throws PrinterException {
        this.mPSStream.println("pgSave restore");
        this.mPSStream.println("showpage");
    }
    
    protected void drawImageBGR(final byte[] array, final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final int n9, final int n10) {
        this.setTransform(new AffineTransform());
        this.prepDrawing();
        final int n11 = (int)n7;
        final int n12 = (int)n8;
        this.mPSStream.println("/imSave save def");
        int i;
        for (i = 3 * n11; i > 65535; i /= 2) {}
        this.mPSStream.println(i + " string /imStr exch def");
        this.mPSStream.println("[" + n3 + " 0 0 " + n4 + " " + n + " " + n2 + "]concat");
        this.mPSStream.println(n11 + " " + n12 + " " + 8 + "[" + n11 + " 0 0 " + n12 + " 0 " + 0 + "]/imageSrc load false 3 colorimage");
        final byte[] array2 = new byte[n11 * 3];
        try {
            int swapBGRtoRGB = (int)n6 * n9;
            for (int j = 0; j < n12; ++j) {
                swapBGRtoRGB = swapBGRtoRGB(array, swapBGRtoRGB + (int)n5, array2);
                this.mPSStream.write(this.ascii85Encode(this.rlEncode(array2)));
                this.mPSStream.println("");
            }
        }
        catch (final IOException ex) {}
        this.mPSStream.println("imSave restore");
    }
    
    @Override
    protected void printBand(final byte[] array, final int n, final int n2, final int n3, final int n4) throws PrinterException {
        this.mPSStream.println("/imSave save def");
        int i;
        for (i = 3 * n3; i > 65535; i /= 2) {}
        this.mPSStream.println(i + " string /imStr exch def");
        this.mPSStream.println("[" + n3 + " 0 0 " + n4 + " " + n + " " + n2 + "]concat");
        this.mPSStream.println(n3 + " " + n4 + " " + 8 + "[" + n3 + " 0 0 " + -n4 + " 0 " + n4 + "]/imageSrc load false 3 colorimage");
        int swapBGRtoRGB = 0;
        final byte[] array2 = new byte[n3 * 3];
        try {
            for (int j = 0; j < n4; ++j) {
                swapBGRtoRGB = swapBGRtoRGB(array, swapBGRtoRGB, array2);
                this.mPSStream.write(this.ascii85Encode(this.rlEncode(array2)));
                this.mPSStream.println("");
            }
        }
        catch (final IOException ex) {
            throw new PrinterIOException(ex);
        }
        this.mPSStream.println("imSave restore");
    }
    
    @Override
    protected Graphics2D createPathGraphics(final PeekGraphics peekGraphics, final PrinterJob printerJob, final Printable printable, final PageFormat pageFormat, final int n) {
        final PeekMetrics metrics = peekGraphics.getMetrics();
        Graphics2D graphics2D;
        if (!PSPrinterJob.forcePDL && (PSPrinterJob.forceRaster || metrics.hasNonSolidColors() || metrics.hasCompositing())) {
            graphics2D = null;
        }
        else {
            graphics2D = new PSPathGraphics(new BufferedImage(8, 8, 1).createGraphics(), printerJob, printable, pageFormat, n, !peekGraphics.getAWTDrawingOnly());
        }
        return graphics2D;
    }
    
    protected void selectClipPath() {
        this.mPSStream.println(this.mClipOpStr);
    }
    
    protected void setClip(final Shape mLastClip) {
        this.mLastClip = mLastClip;
    }
    
    protected void setTransform(final AffineTransform mLastTransform) {
        this.mLastTransform = mLastTransform;
    }
    
    protected boolean setFont(final Font mLastFont) {
        this.mLastFont = mLastFont;
        return true;
    }
    
    private int[] getPSFontIndexArray(final Font font, final CharsetString[] array) {
        int[] array2 = null;
        if (PSPrinterJob.mFontProps != null) {
            array2 = new int[array.length];
        }
        for (int n = 0; n < array.length && array2 != null; ++n) {
            final CharsetString charsetString = array[n];
            final CharsetEncoder encoder = charsetString.fontDescriptor.encoder;
            final String fontCharsetName = charsetString.fontDescriptor.getFontCharsetName();
            String charsetName;
            if ("Symbol".equals(fontCharsetName)) {
                charsetName = "symbol";
            }
            else if ("WingDings".equals(fontCharsetName) || "X11Dingbats".equals(fontCharsetName)) {
                charsetName = "dingbats";
            }
            else {
                charsetName = this.makeCharsetName(fontCharsetName, charsetString.charsetChars);
            }
            final String property = PSPrinterJob.mFontProps.getProperty(PSPrinterJob.mFontProps.getProperty(font.getFamily().toLowerCase(Locale.ENGLISH).replace(' ', '_'), "") + "." + charsetName + "." + FontConfiguration.getStyleString(font.getStyle() | FontUtilities.getFont2D(font).getStyle()), null);
            if (property != null) {
                try {
                    array2[n] = Integer.parseInt(PSPrinterJob.mFontProps.getProperty(property));
                }
                catch (final NumberFormatException ex) {
                    array2 = null;
                }
            }
            else {
                array2 = null;
            }
        }
        return array2;
    }
    
    private static String escapeParens(final String s) {
        if (s.indexOf(40) == -1 && s.indexOf(41) == -1) {
            return s;
        }
        int n = 0;
        for (int index = 0; (index = s.indexOf(40, index)) != -1; ++index) {
            ++n;
        }
        for (int index2 = 0; (index2 = s.indexOf(41, index2)) != -1; ++index2) {
            ++n;
        }
        final char[] charArray = s.toCharArray();
        final char[] array = new char[charArray.length + n];
        int n2 = 0;
        for (int i = 0; i < charArray.length; ++i) {
            if (charArray[i] == '(' || charArray[i] == ')') {
                array[n2++] = '\\';
            }
            array[n2++] = charArray[i];
        }
        return new String(array);
    }
    
    protected int platformFontCount(final Font font, final String s) {
        if (PSPrinterJob.mFontProps == null) {
            return 0;
        }
        final CharsetString[] multiCharsetString = ((PlatformFont)font.getPeer()).makeMultiCharsetString(s, false);
        if (multiCharsetString == null) {
            return 0;
        }
        final int[] psFontIndexArray = this.getPSFontIndexArray(font, multiCharsetString);
        return (psFontIndexArray == null) ? 0 : psFontIndexArray.length;
    }
    
    protected boolean textOut(final Graphics graphics, String removeControlChars, float n, final float n2, final Font font, final FontRenderContext fontRenderContext, final float n3) {
        boolean b = true;
        if (PSPrinterJob.mFontProps == null) {
            return false;
        }
        this.prepDrawing();
        removeControlChars = this.removeControlChars(removeControlChars);
        if (removeControlChars.length() == 0) {
            return true;
        }
        final CharsetString[] multiCharsetString = ((PlatformFont)font.getPeer()).makeMultiCharsetString(removeControlChars, false);
        if (multiCharsetString == null) {
            return false;
        }
        final int[] psFontIndexArray = this.getPSFontIndexArray(font, multiCharsetString);
        if (psFontIndexArray != null) {
            for (int i = 0; i < multiCharsetString.length; ++i) {
                final CharsetString charsetString = multiCharsetString[i];
                final CharsetEncoder encoder = charsetString.fontDescriptor.encoder;
                final StringBuffer sb = new StringBuffer();
                final byte[] array = new byte[charsetString.length * 2];
                int limit;
                try {
                    final ByteBuffer wrap = ByteBuffer.wrap(array);
                    encoder.encode(CharBuffer.wrap(charsetString.charsetChars, charsetString.offset, charsetString.length), wrap, true);
                    wrap.flip();
                    limit = wrap.limit();
                }
                catch (final IllegalStateException ex) {
                    continue;
                }
                catch (final CoderMalfunctionError coderMalfunctionError) {
                    continue;
                }
                float n4;
                if (multiCharsetString.length == 1 && n3 != 0.0f) {
                    n4 = n3;
                }
                else {
                    n4 = (float)font.getStringBounds(charsetString.charsetChars, charsetString.offset, charsetString.offset + charsetString.length, fontRenderContext).getWidth();
                }
                if (n4 == 0.0f) {
                    return b;
                }
                sb.append('<');
                for (int j = 0; j < limit; ++j) {
                    String s = Integer.toHexString(array[j]);
                    final int length = s.length();
                    if (length > 2) {
                        s = s.substring(length - 2, length);
                    }
                    else if (length == 1) {
                        s = "0" + s;
                    }
                    else if (length == 0) {
                        s = "00";
                    }
                    sb.append(s);
                }
                sb.append('>');
                this.getGState().emitPSFont(psFontIndexArray[i], font.getSize2D());
                this.mPSStream.println(sb.toString() + " " + n4 + " " + n + " " + n2 + " " + "S");
                n += n4;
            }
        }
        else {
            b = false;
        }
        return b;
    }
    
    protected void setFillMode(final int n) {
        switch (n) {
            case 1: {
                this.mFillOpStr = "EF";
                this.mClipOpStr = "EC";
                break;
            }
            case 2: {
                this.mFillOpStr = "WF";
                this.mClipOpStr = "WC";
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }
    
    protected void setColor(final Color mLastColor) {
        this.mLastColor = mLastColor;
    }
    
    protected void fillPath() {
        this.mPSStream.println(this.mFillOpStr);
    }
    
    protected void beginPath() {
        this.prepDrawing();
        this.mPSStream.println("N");
        this.mPenX = 0.0f;
        this.mPenY = 0.0f;
    }
    
    protected void closeSubpath() {
        this.mPSStream.println("P");
        this.mPenX = this.mStartPathX;
        this.mPenY = this.mStartPathY;
    }
    
    protected void moveTo(final float n, final float n2) {
        this.mPSStream.println(this.trunc(n) + " " + this.trunc(n2) + " M");
        this.mStartPathX = n;
        this.mStartPathY = n2;
        this.mPenX = n;
        this.mPenY = n2;
    }
    
    protected void lineTo(final float mPenX, final float mPenY) {
        this.mPSStream.println(this.trunc(mPenX) + " " + this.trunc(mPenY) + " L");
        this.mPenX = mPenX;
        this.mPenY = mPenY;
    }
    
    protected void bezierTo(final float n, final float n2, final float n3, final float n4, final float mPenX, final float mPenY) {
        this.mPSStream.println(this.trunc(n) + " " + this.trunc(n2) + " " + this.trunc(n3) + " " + this.trunc(n4) + " " + this.trunc(mPenX) + " " + this.trunc(mPenY) + " C");
        this.mPenX = mPenX;
        this.mPenY = mPenY;
    }
    
    String trunc(float n) {
        final float abs = Math.abs(n);
        if (abs >= 1.0f && abs <= 1000.0f) {
            n = Math.round(n * 1000.0f) / 1000.0f;
        }
        return Float.toString(n);
    }
    
    protected float getPenX() {
        return this.mPenX;
    }
    
    protected float getPenY() {
        return this.mPenY;
    }
    
    @Override
    protected double getXRes() {
        return 300.0;
    }
    
    @Override
    protected double getYRes() {
        return 300.0;
    }
    
    @Override
    protected double getPhysicalPrintableX(final Paper paper) {
        return 0.0;
    }
    
    @Override
    protected double getPhysicalPrintableY(final Paper paper) {
        return 0.0;
    }
    
    @Override
    protected double getPhysicalPrintableWidth(final Paper paper) {
        return paper.getImageableWidth();
    }
    
    @Override
    protected double getPhysicalPrintableHeight(final Paper paper) {
        return paper.getImageableHeight();
    }
    
    @Override
    protected double getPhysicalPageWidth(final Paper paper) {
        return paper.getWidth();
    }
    
    @Override
    protected double getPhysicalPageHeight(final Paper paper) {
        return paper.getHeight();
    }
    
    @Override
    protected int getNoncollatedCopies() {
        return 1;
    }
    
    @Override
    protected int getCollatedCopies() {
        return 1;
    }
    
    private String[] printExecCmd(final String s, final String s2, final boolean b, final String s3, final int n, final String s4) {
        final int n2 = 1;
        final int n3 = 2;
        final int n4 = 4;
        final int n5 = 8;
        final int n6 = 16;
        int n7 = 0;
        int n8 = 2;
        int n9 = 0;
        if (s != null && !s.equals("") && !s.equals("lp")) {
            n7 |= n2;
            ++n8;
        }
        if (s2 != null && !s2.equals("")) {
            n7 |= n3;
            ++n8;
        }
        if (s3 != null && !s3.equals("")) {
            n7 |= n4;
            ++n8;
        }
        if (n > 1) {
            n7 |= n5;
            ++n8;
        }
        if (b) {
            n7 |= n6;
            ++n8;
        }
        final String property = System.getProperty("os.name");
        String[] array;
        if (property.equals("Linux") || property.contains("OS X")) {
            array = new String[n8];
            array[n9++] = "/usr/bin/lpr";
            if ((n7 & n2) != 0x0) {
                array[n9++] = "-P" + s;
            }
            if ((n7 & n4) != 0x0) {
                array[n9++] = "-J" + s3;
            }
            if ((n7 & n5) != 0x0) {
                array[n9++] = "-#" + n;
            }
            if ((n7 & n6) != 0x0) {
                array[n9++] = "-h";
            }
            if ((n7 & n3) != 0x0) {
                array[n9++] = new String(s2);
            }
        }
        else {
            array = new String[++n8];
            array[n9++] = "/usr/bin/lp";
            array[n9++] = "-c";
            if ((n7 & n2) != 0x0) {
                array[n9++] = "-d" + s;
            }
            if ((n7 & n4) != 0x0) {
                array[n9++] = "-t" + s3;
            }
            if ((n7 & n5) != 0x0) {
                array[n9++] = "-n" + n;
            }
            if ((n7 & n6) != 0x0) {
                array[n9++] = "-o nobanner";
            }
            if ((n7 & n3) != 0x0) {
                array[n9++] = "-o" + s2;
            }
        }
        array[n9++] = s4;
        return array;
    }
    
    private static int swapBGRtoRGB(final byte[] array, int n, final byte[] array2) {
        for (int n2 = 0; n < array.length - 2 && n2 < array2.length - 2; array2[n2++] = array[n + 2], array2[n2++] = array[n + 1], array2[n2++] = array[n + 0], n += 3) {}
        return n;
    }
    
    private String makeCharsetName(final String s, final char[] array) {
        if (s.equals("Cp1252") || s.equals("ISO8859_1")) {
            return "latin1";
        }
        if (s.equals("UTF8")) {
            for (int i = 0; i < array.length; ++i) {
                if (array[i] > '\u00ff') {
                    return s.toLowerCase();
                }
            }
            return "latin1";
        }
        if (s.startsWith("ISO8859")) {
            for (int j = 0; j < array.length; ++j) {
                if (array[j] > '\u007f') {
                    return s.toLowerCase();
                }
            }
            return "latin1";
        }
        return s.toLowerCase();
    }
    
    private void prepDrawing() {
        while (!this.isOuterGState() && (!this.getGState().canSetClip(this.mLastClip) || !this.getGState().mTransform.equals(this.mLastTransform))) {
            this.grestore();
        }
        this.getGState().emitPSColor(this.mLastColor);
        if (this.isOuterGState()) {
            this.gsave();
            this.getGState().emitTransform(this.mLastTransform);
            this.getGState().emitPSClip(this.mLastClip);
        }
    }
    
    private GState getGState() {
        return this.mGStateStack.get(this.mGStateStack.size() - 1);
    }
    
    private void gsave() {
        this.mGStateStack.add(new GState(this.getGState()));
        this.mPSStream.println("G");
    }
    
    private void grestore() {
        this.mGStateStack.remove(this.mGStateStack.size() - 1);
        this.mPSStream.println("R");
    }
    
    private boolean isOuterGState() {
        return this.mGStateStack.size() == 1;
    }
    
    void convertToPSPath(final PathIterator pathIterator) {
        final float[] array = new float[6];
        int fillMode;
        if (pathIterator.getWindingRule() == 0) {
            fillMode = 1;
        }
        else {
            fillMode = 2;
        }
        this.beginPath();
        this.setFillMode(fillMode);
        while (!pathIterator.isDone()) {
            switch (pathIterator.currentSegment(array)) {
                case 0: {
                    this.moveTo(array[0], array[1]);
                    break;
                }
                case 1: {
                    this.lineTo(array[0], array[1]);
                    break;
                }
                case 2: {
                    final float penX = this.getPenX();
                    final float penY = this.getPenY();
                    this.bezierTo(penX + (array[0] - penX) * 2.0f / 3.0f, penY + (array[1] - penY) * 2.0f / 3.0f, array[2] - (array[2] - array[0]) * 2.0f / 3.0f, array[3] - (array[3] - array[1]) * 2.0f / 3.0f, array[2], array[3]);
                    break;
                }
                case 3: {
                    this.bezierTo(array[0], array[1], array[2], array[3], array[4], array[5]);
                    break;
                }
                case 4: {
                    this.closeSubpath();
                    break;
                }
            }
            pathIterator.next();
        }
    }
    
    protected void deviceFill(final PathIterator pathIterator, final Color color, final AffineTransform transform, final Shape clip) {
        this.setTransform(transform);
        this.setClip(clip);
        this.setColor(color);
        this.convertToPSPath(pathIterator);
        this.mPSStream.println("G");
        this.selectClipPath();
        this.fillPath();
        this.mPSStream.println("R N");
    }
    
    private byte[] rlEncode(final byte[] array) {
        int i = 0;
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        final byte[] array2 = new byte[array.length * 2 + 2];
        while (i < array.length) {
            if (n3 == 0) {
                n2 = i++;
                n3 = 1;
            }
            while (n3 < 128 && i < array.length && array[i] == array[n2]) {
                ++n3;
                ++i;
            }
            if (n3 > 1) {
                array2[n++] = (byte)(257 - n3);
                array2[n++] = array[n2];
                n3 = 0;
            }
            else {
                while (n3 < 128 && i < array.length && array[i] != array[i - 1]) {
                    ++n3;
                    ++i;
                }
                array2[n++] = (byte)(n3 - 1);
                for (int j = n2; j < n2 + n3; ++j) {
                    array2[n++] = array[j];
                }
                n3 = 0;
            }
        }
        array2[n++] = -128;
        final byte[] array3 = new byte[n];
        System.arraycopy(array2, 0, array3, 0, n);
        return array3;
    }
    
    private byte[] ascii85Encode(final byte[] array) {
        final byte[] array2 = new byte[(array.length + 4) * 5 / 4 + 2];
        final long n = 85L;
        final long n2 = n * n;
        final long n3 = n * n2;
        final long n4 = n * n3;
        final int n5 = 33;
        int i = 0;
        int n6 = 0;
        while (i + 3 < array.length) {
            final long n7 = ((long)(array[i++] & 0xFF) << 24) + ((long)(array[i++] & 0xFF) << 16) + ((long)(array[i++] & 0xFF) << 8) + (array[i++] & 0xFF);
            if (n7 == 0L) {
                array2[n6++] = 122;
            }
            else {
                final long n8 = n7;
                array2[n6++] = (byte)(n8 / n4 + n5);
                final long n9 = n8 % n4;
                array2[n6++] = (byte)(n9 / n3 + n5);
                final long n10 = n9 % n3;
                array2[n6++] = (byte)(n10 / n2 + n5);
                final long n11 = n10 % n2;
                array2[n6++] = (byte)(n11 / n + n5);
                array2[n6++] = (byte)(n11 % n + n5);
            }
        }
        if (i < array.length) {
            final int n12 = array.length - i;
            long n13;
            for (n13 = 0L; i < array.length; n13 = (n13 << 8) + (array[i++] & 0xFF)) {}
            int n14 = 4 - n12;
            while (n14-- > 0) {
                n13 <<= 8;
            }
            final byte[] array3 = new byte[5];
            final long n15 = n13;
            array3[0] = (byte)(n15 / n4 + n5);
            final long n16 = n15 % n4;
            array3[1] = (byte)(n16 / n3 + n5);
            final long n17 = n16 % n3;
            array3[2] = (byte)(n17 / n2 + n5);
            final long n18 = n17 % n2;
            array3[3] = (byte)(n18 / n + n5);
            array3[4] = (byte)(n18 % n + n5);
            for (int j = 0; j < n12 + 1; ++j) {
                array2[n6++] = array3[j];
            }
        }
        array2[n6++] = 126;
        array2[n6++] = 62;
        final byte[] array4 = new byte[n6];
        System.arraycopy(array2, 0, array4, 0, n6);
        return array4;
    }
    
    static {
        hexDigits = new byte[] { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70 };
        PSPrinterJob.mFontProps = null;
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            @Override
            public Object run() {
                PSPrinterJob.mFontProps = initProps();
                PSPrinterJob.isMac = System.getProperty("os.name").startsWith("Mac");
                return null;
            }
        });
    }
    
    private class PrinterOpener implements PrivilegedAction
    {
        PrinterException pex;
        OutputStream result;
        
        @Override
        public Object run() {
            try {
                (PSPrinterJob.this.spoolFile = Files.createTempFile("javaprint", ".ps", (FileAttribute<?>[])new FileAttribute[0]).toFile()).deleteOnExit();
                return this.result = new FileOutputStream(PSPrinterJob.this.spoolFile);
            }
            catch (final IOException ex) {
                this.pex = new PrinterIOException(ex);
                return null;
            }
        }
    }
    
    private class PrinterSpooler implements PrivilegedAction
    {
        PrinterException pex;
        
        private void handleProcessFailure(final Process process, final String[] array, final int n) throws IOException {
            try (final StringWriter stringWriter = new StringWriter();
                 final PrintWriter printWriter = new PrintWriter(stringWriter)) {
                printWriter.append("error=").append(Integer.toString(n));
                printWriter.append(" running:");
                for (int length = array.length, i = 0; i < length; ++i) {
                    printWriter.append(" '").append(array[i]).append("'");
                }
                try (final InputStream errorStream = process.getErrorStream();
                     final InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
                     final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                    while (bufferedReader.ready()) {
                        printWriter.println();
                        printWriter.append("\t\t").append(bufferedReader.readLine());
                    }
                }
                finally {
                    printWriter.flush();
                    throw new IOException(stringWriter.toString());
                }
            }
        }
        
        @Override
        public Object run() {
            if (PSPrinterJob.this.spoolFile == null || !PSPrinterJob.this.spoolFile.exists()) {
                this.pex = new PrinterException("No spool file");
                return null;
            }
            try {
                final String[] access$700 = PSPrinterJob.this.printExecCmd(PSPrinterJob.this.mDestination, PSPrinterJob.this.mOptions, PSPrinterJob.this.mNoJobSheet, PSPrinterJob.this.getJobNameInt(), 1, PSPrinterJob.this.spoolFile.getAbsolutePath());
                final Process exec = Runtime.getRuntime().exec(access$700);
                exec.waitFor();
                final int exitValue = exec.exitValue();
                if (exitValue != 0) {
                    this.handleProcessFailure(exec, access$700, exitValue);
                }
            }
            catch (final IOException ex) {
                this.pex = new PrinterIOException(ex);
            }
            catch (final InterruptedException ex2) {
                this.pex = new PrinterException(ex2.toString());
            }
            finally {
                PSPrinterJob.this.spoolFile.delete();
            }
            return null;
        }
    }
    
    private class GState
    {
        Color mColor;
        Shape mClip;
        Font mFont;
        AffineTransform mTransform;
        
        GState() {
            this.mColor = Color.black;
            this.mClip = null;
            this.mFont = null;
            this.mTransform = new AffineTransform();
        }
        
        GState(final GState gState) {
            this.mColor = gState.mColor;
            this.mClip = gState.mClip;
            this.mFont = gState.mFont;
            this.mTransform = gState.mTransform;
        }
        
        boolean canSetClip(final Shape shape) {
            return this.mClip == null || this.mClip.equals(shape);
        }
        
        void emitPSClip(final Shape mClip) {
            if (mClip != null && (this.mClip == null || !this.mClip.equals(mClip))) {
                final String access$900 = PSPrinterJob.this.mFillOpStr;
                PSPrinterJob.this.mClipOpStr;
                PSPrinterJob.this.convertToPSPath(mClip.getPathIterator(new AffineTransform()));
                PSPrinterJob.this.selectClipPath();
                this.mClip = mClip;
                PSPrinterJob.this.mClipOpStr = access$900;
                PSPrinterJob.this.mFillOpStr = access$900;
            }
        }
        
        void emitTransform(final AffineTransform mTransform) {
            if (mTransform != null && !mTransform.equals(this.mTransform)) {
                final double[] array = new double[6];
                mTransform.getMatrix(array);
                PSPrinterJob.this.mPSStream.println("[" + (float)array[0] + " " + (float)array[1] + " " + (float)array[2] + " " + (float)array[3] + " " + (float)array[4] + " " + (float)array[5] + "] concat");
                this.mTransform = mTransform;
            }
        }
        
        void emitPSColor(final Color mColor) {
            if (mColor != null && !mColor.equals(this.mColor)) {
                final float[] rgbColorComponents = mColor.getRGBColorComponents(null);
                if (rgbColorComponents[0] == rgbColorComponents[1] && rgbColorComponents[1] == rgbColorComponents[2]) {
                    PSPrinterJob.this.mPSStream.println(rgbColorComponents[0] + " SG");
                }
                else {
                    PSPrinterJob.this.mPSStream.println(rgbColorComponents[0] + " " + rgbColorComponents[1] + " " + rgbColorComponents[2] + " " + " SC");
                }
                this.mColor = mColor;
            }
        }
        
        void emitPSFont(final int n, final float n2) {
            PSPrinterJob.this.mPSStream.println(n2 + " " + n + " " + "F");
        }
    }
    
    public static class PluginPrinter implements Printable
    {
        private EPSPrinter epsPrinter;
        private Component applet;
        private PrintStream stream;
        private String epsTitle;
        private int bx;
        private int by;
        private int bw;
        private int bh;
        private int width;
        private int height;
        
        public PluginPrinter(final Component applet, final PrintStream stream, final int bx, final int by, final int bw, final int bh) {
            this.applet = applet;
            this.epsTitle = "Java Plugin Applet";
            this.stream = stream;
            this.bx = bx;
            this.by = by;
            this.bw = bw;
            this.bh = bh;
            this.width = applet.size().width;
            this.height = applet.size().height;
            this.epsPrinter = new EPSPrinter(this, this.epsTitle, stream, 0, 0, this.width, this.height);
        }
        
        public void printPluginPSHeader() {
            this.stream.println("%%BeginDocument: JavaPluginApplet");
        }
        
        public void printPluginApplet() {
            try {
                this.epsPrinter.print();
            }
            catch (final PrinterException ex) {}
        }
        
        public void printPluginPSTrailer() {
            this.stream.println("%%EndDocument: JavaPluginApplet");
            this.stream.flush();
        }
        
        public void printAll() {
            this.printPluginPSHeader();
            this.printPluginApplet();
            this.printPluginPSTrailer();
        }
        
        @Override
        public int print(final Graphics graphics, final PageFormat pageFormat, final int n) {
            if (n > 0) {
                return 1;
            }
            this.applet.printAll(graphics);
            return 0;
        }
    }
    
    public static class EPSPrinter implements Pageable
    {
        private PageFormat pf;
        private PSPrinterJob job;
        private int llx;
        private int lly;
        private int urx;
        private int ury;
        private Printable printable;
        private PrintStream stream;
        private String epsTitle;
        
        public EPSPrinter(final Printable printable, final String epsTitle, final PrintStream stream, final int llx, final int lly, final int n, final int n2) {
            this.printable = printable;
            this.epsTitle = epsTitle;
            this.stream = stream;
            this.llx = llx;
            this.lly = lly;
            this.urx = this.llx + n;
            this.ury = this.lly + n2;
            final Paper paper = new Paper();
            paper.setSize(n, n2);
            paper.setImageableArea(0.0, 0.0, n, n2);
            (this.pf = new PageFormat()).setPaper(paper);
        }
        
        public void print() throws PrinterException {
            this.stream.println("%!PS-Adobe-3.0 EPSF-3.0");
            this.stream.println("%%BoundingBox: " + this.llx + " " + this.lly + " " + this.urx + " " + this.ury);
            this.stream.println("%%Title: " + this.epsTitle);
            this.stream.println("%%Creator: Java Printing");
            this.stream.println("%%CreationDate: " + new Date());
            this.stream.println("%%EndComments");
            this.stream.println("/pluginSave save def");
            this.stream.println("mark");
            (this.job = new PSPrinterJob()).epsPrinter = this;
            this.job.mPSStream = this.stream;
            this.job.mDestType = 2;
            this.job.startDoc();
            try {
                this.job.printPage(this, 0);
            }
            catch (final Throwable t) {
                if (t instanceof PrinterException) {
                    throw (PrinterException)t;
                }
                throw new PrinterException(t.toString());
            }
            finally {
                this.stream.println("cleartomark");
                this.stream.println("pluginSave restore");
                this.job.endDoc();
            }
            this.stream.flush();
        }
        
        @Override
        public int getNumberOfPages() {
            return 1;
        }
        
        @Override
        public PageFormat getPageFormat(final int n) {
            if (n > 0) {
                throw new IndexOutOfBoundsException("pgIndex");
            }
            return this.pf;
        }
        
        @Override
        public Printable getPrintable(final int n) {
            if (n > 0) {
                throw new IndexOutOfBoundsException("pgIndex");
            }
            return this.printable;
        }
    }
}
