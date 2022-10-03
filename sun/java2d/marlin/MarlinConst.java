package sun.java2d.marlin;

interface MarlinConst
{
    public static final boolean ENABLE_LOGS = MarlinProperties.isLoggingEnabled();
    public static final boolean USE_LOGGER = MarlinConst.ENABLE_LOGS && MarlinProperties.isUseLogger();
    public static final boolean LOG_CREATE_CONTEXT = MarlinConst.ENABLE_LOGS && MarlinProperties.isLogCreateContext();
    public static final boolean LOG_UNSAFE_MALLOC = MarlinConst.ENABLE_LOGS && MarlinProperties.isLogUnsafeMalloc();
    public static final boolean DO_CHECK_UNSAFE = false;
    public static final boolean DO_STATS = MarlinConst.ENABLE_LOGS && MarlinProperties.isDoStats();
    public static final boolean DO_MONITORS = false;
    public static final boolean DO_CHECKS = MarlinConst.ENABLE_LOGS && MarlinProperties.isDoChecks();
    public static final boolean DO_AA_RANGE_CHECK = false;
    public static final boolean DO_LOG_WIDEN_ARRAY = false;
    public static final boolean DO_LOG_OVERSIZE = false;
    public static final boolean DO_TRACE = false;
    public static final boolean DO_FLUSH_STATS = true;
    public static final boolean DO_FLUSH_MONITORS = true;
    public static final boolean USE_DUMP_THREAD = false;
    public static final long DUMP_INTERVAL = 5000L;
    public static final boolean DO_CLEAN_DIRTY = false;
    public static final boolean USE_SIMPLIFIER = MarlinProperties.isUseSimplifier();
    public static final boolean USE_PATH_SIMPLIFIER = MarlinProperties.isUsePathSimplifier();
    public static final boolean DO_CLIP_SUBDIVIDER = MarlinProperties.isDoClipSubdivider();
    public static final boolean DO_LOG_BOUNDS = false;
    public static final boolean DO_LOG_CLIP = false;
    public static final int INITIAL_PIXEL_WIDTH = MarlinProperties.getInitialPixelWidth();
    public static final int INITIAL_PIXEL_HEIGHT = MarlinProperties.getInitialPixelHeight();
    public static final int INITIAL_ARRAY = 256;
    public static final int INITIAL_AA_ARRAY = MarlinConst.INITIAL_PIXEL_WIDTH;
    public static final int INITIAL_EDGES_COUNT = MarlinProperties.getInitialEdges();
    public static final int INITIAL_EDGES_CAPACITY = MarlinConst.INITIAL_EDGES_COUNT * 24;
    public static final byte BYTE_0 = 0;
    public static final int SUBPIXEL_LG_POSITIONS_X = MarlinProperties.getSubPixel_Log2_X();
    public static final int SUBPIXEL_LG_POSITIONS_Y = MarlinProperties.getSubPixel_Log2_Y();
    public static final int MIN_SUBPIXEL_LG_POSITIONS = Math.min(MarlinConst.SUBPIXEL_LG_POSITIONS_X, MarlinConst.SUBPIXEL_LG_POSITIONS_Y);
    public static final int SUBPIXEL_POSITIONS_X = 1 << MarlinConst.SUBPIXEL_LG_POSITIONS_X;
    public static final int SUBPIXEL_POSITIONS_Y = 1 << MarlinConst.SUBPIXEL_LG_POSITIONS_Y;
    public static final float MIN_SUBPIXELS = (float)(1 << MarlinConst.MIN_SUBPIXEL_LG_POSITIONS);
    public static final int MAX_AA_ALPHA = MarlinConst.SUBPIXEL_POSITIONS_X * MarlinConst.SUBPIXEL_POSITIONS_Y;
    public static final int TILE_H_LG = MarlinProperties.getTileSize_Log2();
    public static final int TILE_H = 1 << MarlinConst.TILE_H_LG;
    public static final int TILE_W_LG = MarlinProperties.getTileWidth_Log2();
    public static final int TILE_W = 1 << MarlinConst.TILE_W_LG;
    public static final int BLOCK_SIZE_LG = MarlinProperties.getBlockSize_Log2();
    public static final int BLOCK_SIZE = 1 << MarlinConst.BLOCK_SIZE_LG;
    public static final int WIND_EVEN_ODD = 0;
    public static final int WIND_NON_ZERO = 1;
    public static final int JOIN_MITER = 0;
    public static final int JOIN_ROUND = 1;
    public static final int JOIN_BEVEL = 2;
    public static final int CAP_BUTT = 0;
    public static final int CAP_ROUND = 1;
    public static final int CAP_SQUARE = 2;
    public static final int OUTCODE_TOP = 1;
    public static final int OUTCODE_BOTTOM = 2;
    public static final int OUTCODE_LEFT = 4;
    public static final int OUTCODE_RIGHT = 8;
    public static final int OUTCODE_MASK_T_B = 3;
    public static final int OUTCODE_MASK_L_R = 12;
    public static final int OUTCODE_MASK_T_B_L_R = 15;
    
    default static {
        if (MarlinConst.ENABLE_LOGS) {}
        if (MarlinConst.ENABLE_LOGS) {}
        if (MarlinConst.ENABLE_LOGS) {}
        if (MarlinConst.ENABLE_LOGS) {}
        if (MarlinConst.ENABLE_LOGS) {}
    }
}
