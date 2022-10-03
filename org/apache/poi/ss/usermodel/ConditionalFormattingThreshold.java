package org.apache.poi.ss.usermodel;

public interface ConditionalFormattingThreshold
{
    RangeType getRangeType();
    
    void setRangeType(final RangeType p0);
    
    String getFormula();
    
    void setFormula(final String p0);
    
    Double getValue();
    
    void setValue(final Double p0);
    
    public enum RangeType
    {
        NUMBER(1, "num"), 
        MIN(2, "min"), 
        MAX(3, "max"), 
        PERCENT(4, "percent"), 
        PERCENTILE(5, "percentile"), 
        UNALLOCATED(6, (String)null), 
        FORMULA(7, "formula");
        
        public final int id;
        public final String name;
        
        @Override
        public String toString() {
            return this.id + " - " + this.name;
        }
        
        public static RangeType byId(final int id) {
            return values()[id - 1];
        }
        
        public static RangeType byName(final String name) {
            for (final RangeType t : values()) {
                if (t.name.equals(name)) {
                    return t;
                }
            }
            return null;
        }
        
        private RangeType(final int id, final String name) {
            this.id = id;
            this.name = name;
        }
    }
}
