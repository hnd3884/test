package sun.font;

public class CompositeFontDescriptor
{
    private String faceName;
    private int coreComponentCount;
    private String[] componentFaceNames;
    private String[] componentFileNames;
    private int[] exclusionRanges;
    private int[] exclusionRangeLimits;
    
    public CompositeFontDescriptor(final String faceName, final int coreComponentCount, final String[] componentFaceNames, final String[] componentFileNames, final int[] exclusionRanges, final int[] exclusionRangeLimits) {
        this.faceName = faceName;
        this.coreComponentCount = coreComponentCount;
        this.componentFaceNames = componentFaceNames;
        this.componentFileNames = componentFileNames;
        this.exclusionRanges = exclusionRanges;
        this.exclusionRangeLimits = exclusionRangeLimits;
    }
    
    public String getFaceName() {
        return this.faceName;
    }
    
    public int getCoreComponentCount() {
        return this.coreComponentCount;
    }
    
    public String[] getComponentFaceNames() {
        return this.componentFaceNames;
    }
    
    public String[] getComponentFileNames() {
        return this.componentFileNames;
    }
    
    public int[] getExclusionRanges() {
        return this.exclusionRanges;
    }
    
    public int[] getExclusionRangeLimits() {
        return this.exclusionRangeLimits;
    }
}
