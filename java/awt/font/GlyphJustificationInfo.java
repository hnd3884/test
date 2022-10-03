package java.awt.font;

public final class GlyphJustificationInfo
{
    public static final int PRIORITY_KASHIDA = 0;
    public static final int PRIORITY_WHITESPACE = 1;
    public static final int PRIORITY_INTERCHAR = 2;
    public static final int PRIORITY_NONE = 3;
    public final float weight;
    public final int growPriority;
    public final boolean growAbsorb;
    public final float growLeftLimit;
    public final float growRightLimit;
    public final int shrinkPriority;
    public final boolean shrinkAbsorb;
    public final float shrinkLeftLimit;
    public final float shrinkRightLimit;
    
    public GlyphJustificationInfo(final float weight, final boolean growAbsorb, final int growPriority, final float growLeftLimit, final float growRightLimit, final boolean shrinkAbsorb, final int shrinkPriority, final float shrinkLeftLimit, final float shrinkRightLimit) {
        if (weight < 0.0f) {
            throw new IllegalArgumentException("weight is negative");
        }
        if (!priorityIsValid(growPriority)) {
            throw new IllegalArgumentException("Invalid grow priority");
        }
        if (growLeftLimit < 0.0f) {
            throw new IllegalArgumentException("growLeftLimit is negative");
        }
        if (growRightLimit < 0.0f) {
            throw new IllegalArgumentException("growRightLimit is negative");
        }
        if (!priorityIsValid(shrinkPriority)) {
            throw new IllegalArgumentException("Invalid shrink priority");
        }
        if (shrinkLeftLimit < 0.0f) {
            throw new IllegalArgumentException("shrinkLeftLimit is negative");
        }
        if (shrinkRightLimit < 0.0f) {
            throw new IllegalArgumentException("shrinkRightLimit is negative");
        }
        this.weight = weight;
        this.growAbsorb = growAbsorb;
        this.growPriority = growPriority;
        this.growLeftLimit = growLeftLimit;
        this.growRightLimit = growRightLimit;
        this.shrinkAbsorb = shrinkAbsorb;
        this.shrinkPriority = shrinkPriority;
        this.shrinkLeftLimit = shrinkLeftLimit;
        this.shrinkRightLimit = shrinkRightLimit;
    }
    
    private static boolean priorityIsValid(final int n) {
        return n >= 0 && n <= 3;
    }
}
