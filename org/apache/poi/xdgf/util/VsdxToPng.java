package org.apache.poi.xdgf.util;

import java.io.InputStream;
import java.io.FileInputStream;
import org.apache.poi.xdgf.usermodel.shape.ShapeDebuggerRenderer;
import java.util.Iterator;
import org.apache.poi.xdgf.usermodel.XmlVisioDocument;
import java.awt.Graphics2D;
import org.apache.poi.xdgf.geom.Dimension2dDouble;
import java.io.OutputStream;
import java.awt.image.RenderedImage;
import javax.imageio.ImageIO;
import java.io.FileOutputStream;
import org.apache.poi.xdgf.usermodel.shape.ShapeVisitor;
import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import org.apache.poi.xdgf.usermodel.shape.ShapeRenderer;
import org.apache.poi.xdgf.usermodel.XDGFPage;

public class VsdxToPng
{
    public static void renderToPng(final XDGFPage page, final String outFilename, final double scale, final ShapeRenderer renderer) throws IOException {
        renderToPng(page, new File(outFilename), scale, renderer);
    }
    
    public static void renderToPngDir(final XDGFPage page, final File outDir, final double scale, final ShapeRenderer renderer) throws IOException {
        final File pageFile = new File(outDir, "page" + page.getPageNumber() + "-" + Util.sanitizeFilename(page.getName()) + ".png");
        System.out.println("** Writing image to " + pageFile);
        renderToPng(page, pageFile, scale, renderer);
    }
    
    public static void renderToPng(final XDGFPage page, final File outFile, final double scale, final ShapeRenderer renderer) throws IOException {
        final Dimension2dDouble sz = page.getPageSize();
        final int width = (int)(scale * sz.getWidth());
        final int height = (int)(scale * sz.getHeight());
        final BufferedImage img = new BufferedImage(width, height, 1);
        final Graphics2D graphics = img.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        graphics.setColor(Color.black);
        graphics.setBackground(Color.white);
        graphics.clearRect(0, 0, width, height);
        graphics.translate(0, img.getHeight());
        graphics.scale(scale, -scale);
        renderer.setGraphics(graphics);
        page.getContent().visitShapes(renderer);
        graphics.dispose();
        try (final FileOutputStream out = new FileOutputStream(outFile)) {
            ImageIO.write(img, "png", out);
        }
    }
    
    public static void renderToPng(final XmlVisioDocument document, final String outDirname, final double scale, final ShapeRenderer renderer) throws IOException {
        final File outDir = new File(outDirname);
        for (final XDGFPage page : document.getPages()) {
            renderToPngDir(page, outDir, scale, renderer);
        }
    }
    
    public static void main(final String[] args) throws Exception {
        if (args.length > 2) {
            System.err.println("Usage: [--debug] in.vsdx outdir");
            System.exit(1);
        }
        ShapeRenderer renderer = new ShapeRenderer();
        String inFilename = args[0];
        String pngDir = args[1];
        if (args[0].equals("--debug")) {
            inFilename = args[1];
            pngDir = args[2];
            renderer = new ShapeDebuggerRenderer();
        }
        try (final FileInputStream is = new FileInputStream(inFilename)) {
            final XmlVisioDocument doc = new XmlVisioDocument(is);
            renderToPng(doc, pngDir, 181.8181818181818, renderer);
        }
    }
}
