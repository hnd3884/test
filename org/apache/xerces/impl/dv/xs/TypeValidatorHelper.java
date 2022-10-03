package org.apache.xerces.impl.dv.xs;

public abstract class TypeValidatorHelper
{
    private static int FACETS_GROUP1;
    private static int FACETS_GROUP2;
    private static int FACETS_GROUP3;
    private static int FACETS_GROUP4;
    private static int FACETS_GROUP5;
    private static int FACETS_GROUP6;
    private static int FACETS_GROUP7;
    private static int FACETS_GROUP8;
    private static int FACETS_GROUP9;
    private static int FACETS_GROUP10;
    private static int FACETS_GROUP11;
    private static int FACETS_GROUP12;
    private static int FACETS_GROUP13;
    private static final TypeValidatorHelper fHelper1_0;
    private static final TypeValidatorHelper fHelper1_1;
    
    public static TypeValidatorHelper getInstance(final short n) {
        if (n < 4) {
            return TypeValidatorHelper.fHelper1_0;
        }
        return TypeValidatorHelper.fHelper1_1;
    }
    
    public boolean isXMLSchema11() {
        return false;
    }
    
    public abstract int getAllowedFacets(final short p0);
    
    protected TypeValidatorHelper() {
    }
    
    static {
        TypeValidatorHelper.FACETS_GROUP1 = 0;
        TypeValidatorHelper.FACETS_GROUP2 = 24;
        TypeValidatorHelper.FACETS_GROUP3 = (TypeValidatorHelper.FACETS_GROUP2 | 0x1 | 0x2 | 0x4 | 0x800);
        TypeValidatorHelper.FACETS_GROUP4 = (TypeValidatorHelper.FACETS_GROUP2 | 0x800 | 0x20 | 0x100 | 0x40 | 0x80);
        TypeValidatorHelper.FACETS_GROUP5 = (TypeValidatorHelper.FACETS_GROUP4 | 0x200 | 0x400);
        TypeValidatorHelper.FACETS_GROUP6 = 2056;
        TypeValidatorHelper.FACETS_GROUP7 = (TypeValidatorHelper.FACETS_GROUP2 | 0x4000);
        TypeValidatorHelper.FACETS_GROUP8 = (TypeValidatorHelper.FACETS_GROUP3 | 0x4000);
        TypeValidatorHelper.FACETS_GROUP9 = (TypeValidatorHelper.FACETS_GROUP4 | 0x4000);
        TypeValidatorHelper.FACETS_GROUP10 = (TypeValidatorHelper.FACETS_GROUP9 | 0xFFFF8000);
        TypeValidatorHelper.FACETS_GROUP11 = (TypeValidatorHelper.FACETS_GROUP5 | 0x4000);
        TypeValidatorHelper.FACETS_GROUP12 = (TypeValidatorHelper.FACETS_GROUP6 | 0x4000);
        TypeValidatorHelper.FACETS_GROUP13 = (TypeValidatorHelper.FACETS_GROUP4 | 0x200 | 0x4000 | 0x1000 | 0x2000);
        fHelper1_0 = new TypeValidatorHelper1_0();
        fHelper1_1 = new TypeValidatorHelper1_1();
    }
    
    static class TypeValidatorHelper1_0 extends TypeValidatorHelper
    {
        protected static int[] fAllowedFacets;
        
        static void createAllowedFacets() {
            TypeValidatorHelper1_0.fAllowedFacets = new int[] { TypeValidatorHelper.FACETS_GROUP1, TypeValidatorHelper.FACETS_GROUP3, TypeValidatorHelper.FACETS_GROUP2, TypeValidatorHelper.FACETS_GROUP5, TypeValidatorHelper.FACETS_GROUP4, TypeValidatorHelper.FACETS_GROUP4, TypeValidatorHelper.FACETS_GROUP4, TypeValidatorHelper.FACETS_GROUP4, TypeValidatorHelper.FACETS_GROUP4, TypeValidatorHelper.FACETS_GROUP4, TypeValidatorHelper.FACETS_GROUP4, TypeValidatorHelper.FACETS_GROUP4, TypeValidatorHelper.FACETS_GROUP4, TypeValidatorHelper.FACETS_GROUP4, TypeValidatorHelper.FACETS_GROUP4, TypeValidatorHelper.FACETS_GROUP3, TypeValidatorHelper.FACETS_GROUP3, TypeValidatorHelper.FACETS_GROUP3, TypeValidatorHelper.FACETS_GROUP3, TypeValidatorHelper.FACETS_GROUP1, TypeValidatorHelper.FACETS_GROUP3, TypeValidatorHelper.FACETS_GROUP3, TypeValidatorHelper.FACETS_GROUP3, TypeValidatorHelper.FACETS_GROUP3, TypeValidatorHelper.FACETS_GROUP5, TypeValidatorHelper.FACETS_GROUP3, TypeValidatorHelper.FACETS_GROUP6, TypeValidatorHelper.FACETS_GROUP1, TypeValidatorHelper.FACETS_GROUP1, TypeValidatorHelper.FACETS_GROUP1, TypeValidatorHelper.FACETS_GROUP1, TypeValidatorHelper.FACETS_GROUP1 };
        }
        
        public int getAllowedFacets(final short n) {
            return (n < TypeValidatorHelper1_0.fAllowedFacets.length) ? TypeValidatorHelper1_0.fAllowedFacets[n] : TypeValidatorHelper.FACETS_GROUP1;
        }
        
        static {
            createAllowedFacets();
        }
    }
    
    static class TypeValidatorHelper1_1 extends TypeValidatorHelper
    {
        protected static int[] fAllowedFacets;
        
        static void createAllowedFacets() {
            TypeValidatorHelper1_1.fAllowedFacets = new int[] { TypeValidatorHelper.FACETS_GROUP1, TypeValidatorHelper.FACETS_GROUP8, TypeValidatorHelper.FACETS_GROUP7, TypeValidatorHelper.FACETS_GROUP11, TypeValidatorHelper.FACETS_GROUP9, TypeValidatorHelper.FACETS_GROUP9, TypeValidatorHelper.FACETS_GROUP9, TypeValidatorHelper.FACETS_GROUP10, TypeValidatorHelper.FACETS_GROUP10, TypeValidatorHelper.FACETS_GROUP10, TypeValidatorHelper.FACETS_GROUP10, TypeValidatorHelper.FACETS_GROUP10, TypeValidatorHelper.FACETS_GROUP10, TypeValidatorHelper.FACETS_GROUP10, TypeValidatorHelper.FACETS_GROUP10, TypeValidatorHelper.FACETS_GROUP8, TypeValidatorHelper.FACETS_GROUP8, TypeValidatorHelper.FACETS_GROUP8, TypeValidatorHelper.FACETS_GROUP8, TypeValidatorHelper.FACETS_GROUP13, TypeValidatorHelper.FACETS_GROUP8, TypeValidatorHelper.FACETS_GROUP8, TypeValidatorHelper.FACETS_GROUP8, TypeValidatorHelper.FACETS_GROUP8, TypeValidatorHelper.FACETS_GROUP11, TypeValidatorHelper.FACETS_GROUP8, TypeValidatorHelper.FACETS_GROUP12, TypeValidatorHelper.FACETS_GROUP9, TypeValidatorHelper.FACETS_GROUP9, TypeValidatorHelper.FACETS_GROUP1, TypeValidatorHelper.FACETS_GROUP1, TypeValidatorHelper.FACETS_GROUP10 };
        }
        
        public int getAllowedFacets(final short n) {
            return (n < TypeValidatorHelper1_1.fAllowedFacets.length) ? TypeValidatorHelper1_1.fAllowedFacets[n] : TypeValidatorHelper.FACETS_GROUP1;
        }
        
        public boolean isXMLSchema11() {
            return true;
        }
        
        static {
            createAllowedFacets();
        }
    }
}
