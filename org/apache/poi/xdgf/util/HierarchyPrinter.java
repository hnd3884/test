package org.apache.poi.xdgf.util;

import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Iterator;
import org.apache.poi.xdgf.usermodel.XmlVisioDocument;
import java.awt.geom.AffineTransform;
import org.apache.poi.xdgf.usermodel.XDGFShape;
import org.apache.poi.xdgf.usermodel.shape.ShapeVisitor;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.File;
import org.apache.poi.xdgf.usermodel.XDGFPage;

public class HierarchyPrinter
{
    public static void printHierarchy(final XDGFPage page, final File outDir) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        final File pageFile = new File(outDir, "page" + page.getPageNumber() + "-" + Util.sanitizeFilename(page.getName()) + ".txt");
        try (final OutputStream os = new FileOutputStream(pageFile);
             final PrintStream pos = new PrintStream(os, false, "utf-8")) {
            printHierarchy(page, pos);
        }
    }
    
    public static void printHierarchy(final XDGFPage page, final PrintStream os) {
        page.getContent().visitShapes(new ShapeVisitor() {
            @Override
            public void visit(final XDGFShape shape, final AffineTransform globalTransform, final int level) {
                for (int i = 0; i < level; ++i) {
                    os.append("  ");
                }
                os.println(shape + " [" + shape.getShapeType() + ", " + shape.getSymbolName() + "] " + shape.getMasterShape() + " " + shape.getTextAsString().trim());
            }
        });
    }
    
    public static void printHierarchy(final XmlVisioDocument document, final String outDirname) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        final File outDir = new File(outDirname);
        for (final XDGFPage page : document.getPages()) {
            printHierarchy(page, outDir);
        }
    }
    
    public static void main(final String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: in.vsdx outdir");
            System.exit(1);
        }
        final String inFilename = args[0];
        final String outDir = args[1];
        try (final FileInputStream is = new FileInputStream(inFilename)) {
            final XmlVisioDocument doc = new XmlVisioDocument(is);
            printHierarchy(doc, outDir);
        }
    }
}
