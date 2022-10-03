package sun.java2d.pipe;

import java.awt.geom.Rectangle2D;
import java.awt.Rectangle;
import sun.java2d.ReentrantContext;
import sun.java2d.ReentrantContextProviderTL;
import java.awt.BasicStroke;
import java.awt.Shape;
import sun.java2d.SunGraphics2D;
import sun.java2d.ReentrantContextProvider;

public final class AAShapePipe implements ShapeDrawPipe, ParallelogramPipe
{
    static final RenderingEngine RDR_ENGINE;
    private static final ReentrantContextProvider<TileState> TILE_STATE_PROVIDER;
    final CompositePipe outpipe;
    
    public AAShapePipe(final CompositePipe outpipe) {
        this.outpipe = outpipe;
    }
    
    @Override
    public void draw(final SunGraphics2D sunGraphics2D, Shape strokedShape) {
        BasicStroke basicStroke;
        if (sunGraphics2D.stroke instanceof BasicStroke) {
            basicStroke = (BasicStroke)sunGraphics2D.stroke;
        }
        else {
            strokedShape = sunGraphics2D.stroke.createStrokedShape(strokedShape);
            basicStroke = null;
        }
        this.renderPath(sunGraphics2D, strokedShape, basicStroke);
    }
    
    @Override
    public void fill(final SunGraphics2D sunGraphics2D, final Shape shape) {
        this.renderPath(sunGraphics2D, shape, null);
    }
    
    @Override
    public void fillParallelogram(final SunGraphics2D sunGraphics2D, final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8, final double n9, final double n10) {
        final TileState tileState = AAShapePipe.TILE_STATE_PROVIDER.acquire();
        try {
            final int[] abox = tileState.abox;
            final AATileGenerator aaTileGenerator = AAShapePipe.RDR_ENGINE.getAATileGenerator(n5, n6, n7, n8, n9, n10, 0.0, 0.0, sunGraphics2D.getCompClip(), abox);
            if (aaTileGenerator != null) {
                this.renderTiles(sunGraphics2D, tileState.computeBBox(n, n2, n3, n4), aaTileGenerator, abox, tileState);
            }
        }
        finally {
            AAShapePipe.TILE_STATE_PROVIDER.release(tileState);
        }
    }
    
    @Override
    public void drawParallelogram(final SunGraphics2D sunGraphics2D, final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8, final double n9, final double n10, final double n11, final double n12) {
        final TileState tileState = AAShapePipe.TILE_STATE_PROVIDER.acquire();
        try {
            final int[] abox = tileState.abox;
            final AATileGenerator aaTileGenerator = AAShapePipe.RDR_ENGINE.getAATileGenerator(n5, n6, n7, n8, n9, n10, n11, n12, sunGraphics2D.getCompClip(), abox);
            if (aaTileGenerator != null) {
                this.renderTiles(sunGraphics2D, tileState.computeBBox(n, n2, n3, n4), aaTileGenerator, abox, tileState);
            }
        }
        finally {
            AAShapePipe.TILE_STATE_PROVIDER.release(tileState);
        }
    }
    
    public void renderPath(final SunGraphics2D sunGraphics2D, final Shape shape, final BasicStroke basicStroke) {
        final boolean b = basicStroke != null && sunGraphics2D.strokeHint != 2;
        final boolean b2 = sunGraphics2D.strokeState <= 1;
        final TileState tileState = AAShapePipe.TILE_STATE_PROVIDER.acquire();
        try {
            final int[] abox = tileState.abox;
            final AATileGenerator aaTileGenerator = AAShapePipe.RDR_ENGINE.getAATileGenerator(shape, sunGraphics2D.transform, sunGraphics2D.getCompClip(), basicStroke, b2, b, abox);
            if (aaTileGenerator != null) {
                this.renderTiles(sunGraphics2D, shape, aaTileGenerator, abox, tileState);
            }
        }
        finally {
            AAShapePipe.TILE_STATE_PROVIDER.release(tileState);
        }
    }
    
    public void renderTiles(final SunGraphics2D sunGraphics2D, final Shape shape, final AATileGenerator aaTileGenerator, final int[] array, final TileState tileState) {
        Object startSequence = null;
        try {
            startSequence = this.outpipe.startSequence(sunGraphics2D, shape, tileState.computeDevBox(array), array);
            final int n = array[0];
            final int n2 = array[1];
            final int n3 = array[2];
            final int n4 = array[3];
            final int tileWidth = aaTileGenerator.getTileWidth();
            final int tileHeight = aaTileGenerator.getTileHeight();
            final byte[] alphaTile = tileState.getAlphaTile(tileWidth * tileHeight);
            for (int i = n2; i < n4; i += tileHeight) {
                final int min = Math.min(tileHeight, n4 - i);
                for (int j = n; j < n3; j += tileWidth) {
                    final int min2 = Math.min(tileWidth, n3 - j);
                    final int typicalAlpha = aaTileGenerator.getTypicalAlpha();
                    if (typicalAlpha == 0 || !this.outpipe.needTile(startSequence, j, i, min2, min)) {
                        aaTileGenerator.nextTile();
                        this.outpipe.skipTile(startSequence, j, i);
                    }
                    else {
                        byte[] array2;
                        if (typicalAlpha == 255) {
                            array2 = null;
                            aaTileGenerator.nextTile();
                        }
                        else {
                            array2 = alphaTile;
                            aaTileGenerator.getAlpha(alphaTile, 0, tileWidth);
                        }
                        this.outpipe.renderPathTile(startSequence, array2, 0, tileWidth, j, i, min2, min);
                    }
                }
            }
        }
        finally {
            aaTileGenerator.dispose();
            if (startSequence != null) {
                this.outpipe.endSequence(startSequence);
            }
        }
    }
    
    static {
        RDR_ENGINE = RenderingEngine.getInstance();
        TILE_STATE_PROVIDER = new ReentrantContextProviderTL<TileState>(0) {
            @Override
            protected TileState newContext() {
                return new TileState();
            }
        };
    }
    
    static final class TileState extends ReentrantContext
    {
        private byte[] theTile;
        final int[] abox;
        private final Rectangle dev;
        private final Rectangle2D.Double bbox2D;
        
        TileState() {
            this.theTile = new byte[1024];
            this.abox = new int[4];
            this.dev = new Rectangle();
            this.bbox2D = new Rectangle2D.Double();
        }
        
        byte[] getAlphaTile(final int n) {
            byte[] theTile = this.theTile;
            if (theTile.length < n) {
                theTile = (this.theTile = new byte[n]);
            }
            return theTile;
        }
        
        Rectangle computeDevBox(final int[] array) {
            final Rectangle dev = this.dev;
            dev.x = array[0];
            dev.y = array[1];
            dev.width = array[2] - array[0];
            dev.height = array[3] - array[1];
            return dev;
        }
        
        Rectangle2D computeBBox(double x, double y, double width, double height) {
            if ((width -= x) < 0.0) {
                x += width;
                width = -width;
            }
            if ((height -= y) < 0.0) {
                y += height;
                height = -height;
            }
            final Rectangle2D.Double bbox2D = this.bbox2D;
            bbox2D.x = x;
            bbox2D.y = y;
            bbox2D.width = width;
            bbox2D.height = height;
            return bbox2D;
        }
    }
}
