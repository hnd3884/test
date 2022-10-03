package org.apache.poi.ss.usermodel;

public interface IconMultiStateFormatting
{
    IconSet getIconSet();
    
    void setIconSet(final IconSet p0);
    
    boolean isIconOnly();
    
    void setIconOnly(final boolean p0);
    
    boolean isReversed();
    
    void setReversed(final boolean p0);
    
    ConditionalFormattingThreshold[] getThresholds();
    
    void setThresholds(final ConditionalFormattingThreshold[] p0);
    
    ConditionalFormattingThreshold createThreshold();
    
    public enum IconSet
    {
        GYR_3_ARROW(0, 3, "3Arrows"), 
        GREY_3_ARROWS(1, 3, "3ArrowsGray"), 
        GYR_3_FLAGS(2, 3, "3Flags"), 
        GYR_3_TRAFFIC_LIGHTS(3, 3, "3TrafficLights1"), 
        GYR_3_TRAFFIC_LIGHTS_BOX(4, 3, "3TrafficLights2"), 
        GYR_3_SHAPES(5, 3, "3Signs"), 
        GYR_3_SYMBOLS_CIRCLE(6, 3, "3Symbols"), 
        GYR_3_SYMBOLS(7, 3, "3Symbols2"), 
        GYR_4_ARROWS(8, 4, "4Arrows"), 
        GREY_4_ARROWS(9, 4, "4ArrowsGray"), 
        RB_4_TRAFFIC_LIGHTS(10, 4, "4RedToBlack"), 
        RATINGS_4(11, 4, "4Rating"), 
        GYRB_4_TRAFFIC_LIGHTS(12, 4, "4TrafficLights"), 
        GYYYR_5_ARROWS(13, 5, "5Arrows"), 
        GREY_5_ARROWS(14, 5, "5ArrowsGray"), 
        RATINGS_5(15, 5, "5Rating"), 
        QUARTERS_5(16, 5, "5Quarters");
        
        protected static final IconSet DEFAULT_ICONSET;
        public final int id;
        public final int num;
        public final String name;
        
        @Override
        public String toString() {
            return this.id + " - " + this.name;
        }
        
        public static IconSet byId(final int id) {
            return values()[id];
        }
        
        public static IconSet byName(final String name) {
            for (final IconSet set : values()) {
                if (set.name.equals(name)) {
                    return set;
                }
            }
            return null;
        }
        
        private IconSet(final int id, final int num, final String name) {
            this.id = id;
            this.num = num;
            this.name = name;
        }
        
        static {
            DEFAULT_ICONSET = IconSet.GYR_3_TRAFFIC_LIGHTS;
        }
    }
}
