package org.apache.poi.sl.usermodel;

public enum Placeholder
{
    NONE(0, 0, 0, 0, 0), 
    TITLE(13, 1, 1, 1, 1), 
    BODY(14, 2, 12, 6, 2), 
    CENTERED_TITLE(15, 3, 3, 3, 3), 
    SUBTITLE(16, 4, 4, 4, 4), 
    DATETIME(7, 7, 7, 7, 5), 
    SLIDE_NUMBER(8, 8, 8, 8, 6), 
    FOOTER(9, 9, 9, 9, 7), 
    HEADER(10, 10, 10, 10, 8), 
    CONTENT(19, 19, 19, 19, 9), 
    CHART(20, 20, 20, 20, 10), 
    TABLE(21, 21, 21, 21, 11), 
    CLIP_ART(22, 22, 22, 22, 12), 
    DGM(23, 23, 23, 23, 13), 
    MEDIA(24, 24, 24, 24, 14), 
    SLIDE_IMAGE(11, 11, 11, 5, 15), 
    PICTURE(26, 26, 26, 26, 16), 
    VERTICAL_OBJECT(25, 25, 25, 25, -2), 
    VERTICAL_TEXT_TITLE(17, 17, 17, 17, -2), 
    VERTICAL_TEXT_BODY(18, 18, 18, 18, -2);
    
    public final int nativeSlideId;
    public final int nativeSlideMasterId;
    public final int nativeNotesId;
    public final int nativeNotesMasterId;
    public final int ooxmlId;
    
    private Placeholder(final int nativeSlideId, final int nativeSlideMasterId, final int nativeNotesId, final int nativeNotesMasterId, final int ooxmlId) {
        this.nativeSlideId = nativeSlideId;
        this.nativeSlideMasterId = nativeSlideMasterId;
        this.nativeNotesId = nativeNotesId;
        this.nativeNotesMasterId = nativeNotesMasterId;
        this.ooxmlId = ooxmlId;
    }
    
    public static Placeholder lookupNativeSlide(final int nativeId) {
        return lookupNative(nativeId, 0);
    }
    
    public static Placeholder lookupNativeSlideMaster(final int nativeId) {
        return lookupNative(nativeId, 1);
    }
    
    public static Placeholder lookupNativeNotes(final int nativeId) {
        return lookupNative(nativeId, 2);
    }
    
    public static Placeholder lookupNativeNotesMaster(final int nativeId) {
        return lookupNative(nativeId, 3);
    }
    
    private static Placeholder lookupNative(final int nativeId, final int type) {
        for (final Placeholder ph : values()) {
            if ((type == 0 && ph.nativeSlideId == nativeId) || (type == 1 && ph.nativeSlideMasterId == nativeId) || (type == 2 && ph.nativeNotesId == nativeId) || (type == 3 && ph.nativeNotesMasterId == nativeId)) {
                return ph;
            }
        }
        return null;
    }
    
    public static Placeholder lookupOoxml(final int ooxmlId) {
        for (final Placeholder ph : values()) {
            if (ph.ooxmlId == ooxmlId) {
                return ph;
            }
        }
        return null;
    }
}
