package org.apache.lucene.analysis.wikipedia;

import java.io.IOException;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import java.io.Reader;

class WikipediaTokenizerImpl
{
    public static final int YYEOF = -1;
    private static final int ZZ_BUFFERSIZE = 4096;
    public static final int YYINITIAL = 0;
    public static final int CATEGORY_STATE = 2;
    public static final int INTERNAL_LINK_STATE = 4;
    public static final int EXTERNAL_LINK_STATE = 6;
    public static final int TWO_SINGLE_QUOTES_STATE = 8;
    public static final int THREE_SINGLE_QUOTES_STATE = 10;
    public static final int FIVE_SINGLE_QUOTES_STATE = 12;
    public static final int DOUBLE_EQUALS_STATE = 14;
    public static final int DOUBLE_BRACE_STATE = 16;
    public static final int STRING = 18;
    private static final int[] ZZ_LEXSTATE;
    private static final String ZZ_CMAP_PACKED = "\t\u0000\u0001\u0014\u0001\u0013\u0001\u0000\u0001\u0014\u0001\u0012\u0012\u0000\u0001\u0014\u0001\u0000\u0001\n\u0001+\u0002\u0000\u0001\u0003\u0001\u0001\u0004\u0000\u0001\f\u0001\u0005\u0001\u0002\u0001\b\n\u000e\u0001\u0017\u0001\u0000\u0001\u0007\u0001\t\u0001\u000b\u0001+\u0001\u0004\u0002\r\u0001\u0018\u0005\r\u0001!\u0011\r\u0001\u0015\u0001\u0000\u0001\u0016\u0001\u0000\u0001\u0006\u0001\u0000\u0001\u0019\u0001#\u0002\r\u0001\u001b\u0001 \u0001\u001c\u0001(\u0001!\u0004\r\u0001\"\u0001\u001d\u0001)\u0001\r\u0001\u001e\u0001*\u0001\u001a\u0003\r\u0001$\u0001\u001f\u0001\r\u0001%\u0001'\u0001&B\u0000\u0017\r\u0001\u0000\u001f\r\u0001\u0000\u0568\r\n\u000f\u0086\r\n\u000f\u026c\r\n\u000fv\r\n\u000fv\r\n\u000fv\r\n\u000fv\r\n\u000fw\r\t\u000fv\r\n\u000fv\r\n\u000fv\r\n\u000f\u00e0\r\n\u000fv\r\n\u000f\u0166\r\n\u000f¶\r\u0100\r\u0e00\r\u1040\u0000\u0150\u0011`\u0000\u0010\u0011\u0100\u0000\u0080\u0011\u0080\u0000\u19c0\u0011@\u0000\u5200\u0011\u0c00\u0000\u2bb0\u0010\u2150\u0000\u0200\u0011\u0465\u0000;\u0011=\r\uffff\u0000\uffff\u0000\uffff\u0000\uffff\u0000\uffff\u0000\uffff\u0000\uffff\u0000\uffff\u0000\uffff\u0000\uffff\u0000\uffff\u0000\uffff\u0000\uffff\u0000\uffff\u0000\uffff\u0000\uffff\u00003\u0000";
    private static final char[] ZZ_CMAP;
    private static final int[] ZZ_ACTION;
    private static final String ZZ_ACTION_PACKED_0 = "\n\u0000\u0004\u0001\u0004\u0002\u0001\u0003\u0001\u0004\u0001\u0001\u0002\u0005\u0001\u0006\u0001\u0005\u0001\u0007\u0001\u0005\u0002\b\u0001\t\u0001\u0005\u0001\n\u0001\t\u0001\u000b\u0001\f\u0001\r\u0001\u000e\u0001\r\u0001\u000f\u0001\u0010\u0001\b\u0001\u0011\u0001\b\u0004\u0012\u0001\u0013\u0001\u0014\u0001\u0015\u0001\u0016\u0003\u0000\u0001\u0017\f\u0000\u0001\u0018\u0001\u0019\u0001\u001a\u0001\u001b\u0001\t\u0001\u0000\u0001\u001c\u0001\u001d\u0001\u001e\u0001\u0000\u0001\u001f\u0001\u0000\u0001 \u0003\u0000\u0001!\u0001\"\u0002#\u0001\"\u0002$\u0002\u0000\u0001#\u0001\u0000\f#\u0001\"\u0003\u0000\u0001\t\u0001%\u0003\u0000\u0001&\u0001'\u0005\u0000\u0001(\u0004\u0000\u0001(\u0002\u0000\u0002(\u0002\u0000\u0001\t\u0005\u0000\u0001\u0019\u0001\"\u0001#\u0001)\u0003\u0000\u0001\t\u0002\u0000\u0001*\u0018\u0000\u0001+\u0002\u0000\u0001,\u0001-\u0001.";
    private static final int[] ZZ_ROWMAP;
    private static final String ZZ_ROWMAP_PACKED_0 = "\u0000\u0000\u0000,\u0000X\u0000\u0084\u0000°\u0000\u00dc\u0000\u0108\u0000\u0134\u0000\u0160\u0000\u018c\u0000\u01b8\u0000\u01e4\u0000\u0210\u0000\u023c\u0000\u0268\u0000\u0294\u0000\u02c0\u0000\u02ec\u0000\u01b8\u0000\u0318\u0000\u0344\u0000\u01b8\u0000\u0370\u0000\u039c\u0000\u03c8\u0000\u03f4\u0000\u0420\u0000\u01b8\u0000\u0370\u0000\u044c\u0000\u0478\u0000\u01b8\u0000\u04a4\u0000\u04d0\u0000\u04fc\u0000\u0528\u0000\u0554\u0000\u0580\u0000\u05ac\u0000\u05d8\u0000\u0604\u0000\u0630\u0000\u065c\u0000\u01b8\u0000\u0688\u0000\u0370\u0000\u06b4\u0000\u06e0\u0000\u070c\u0000\u01b8\u0000\u01b8\u0000\u0738\u0000\u0764\u0000\u0790\u0000\u01b8\u0000\u07bc\u0000\u07e8\u0000\u0814\u0000\u0840\u0000\u086c\u0000\u0898\u0000\u08c4\u0000\u08f0\u0000\u091c\u0000\u0948\u0000\u0974\u0000\u09a0\u0000\u09cc\u0000\u09f8\u0000\u01b8\u0000\u01b8\u0000\u0a24\u0000\u0a50\u0000\u0a7c\u0000\u0a7c\u0000\u01b8\u0000\u0aa8\u0000\u0ad4\u0000\u0b00\u0000\u0b2c\u0000\u0b58\u0000\u0b84\u0000\u0bb0\u0000\u0bdc\u0000\u0c08\u0000\u0c34\u0000\u0c60\u0000\u0c8c\u0000\u0814\u0000\u0cb8\u0000\u0ce4\u0000\u0d10\u0000\u0d3c\u0000\u0d68\u0000\u0d94\u0000\u0dc0\u0000\u0dec\u0000\u0e18\u0000\u0e44\u0000\u0e70\u0000\u0e9c\u0000\u0ec8\u0000\u0ef4\u0000\u0f20\u0000\u0f4c\u0000\u0f78\u0000\u0fa4\u0000\u0fd0\u0000\u0ffc\u0000\u1028\u0000\u1054\u0000\u01b8\u0000\u1080\u0000\u10ac\u0000\u10d8\u0000\u1104\u0000\u01b8\u0000\u1130\u0000\u115c\u0000\u1188\u0000\u11b4\u0000\u11e0\u0000\u120c\u0000\u1238\u0000\u1264\u0000\u1290\u0000\u12bc\u0000\u12e8\u0000\u1314\u0000\u1340\u0000\u07e8\u0000\u0974\u0000\u136c\u0000\u1398\u0000\u13c4\u0000\u13f0\u0000\u141c\u0000\u1448\u0000\u1474\u0000\u14a0\u0000\u01b8\u0000\u14cc\u0000\u14f8\u0000\u1524\u0000\u1550\u0000\u157c\u0000\u15a8\u0000\u15d4\u0000\u1600\u0000\u162c\u0000\u01b8\u0000\u1658\u0000\u1684\u0000\u16b0\u0000\u16dc\u0000\u1708\u0000\u1734\u0000\u1760\u0000\u178c\u0000\u17b8\u0000\u17e4\u0000\u1810\u0000\u183c\u0000\u1868\u0000\u1894\u0000\u18c0\u0000\u18ec\u0000\u1918\u0000\u1944\u0000\u1970\u0000\u199c\u0000\u19c8\u0000\u19f4\u0000\u1a20\u0000\u1a4c\u0000\u1a78\u0000\u1aa4\u0000\u1ad0\u0000\u01b8\u0000\u01b8\u0000\u01b8";
    private static final int[] ZZ_TRANS;
    private static final String ZZ_TRANS_PACKED_0 = "\u0001\u000b\u0001\f\u0005\u000b\u0001\r\u0001\u000b\u0001\u000e\u0003\u000b\u0001\u000f\u0001\u0010\u0001\u0011\u0001\u0012\u0001\u0013\u0003\u000b\u0001\u0014\u0002\u000b\r\u000f\u0001\u0015\u0002\u000b\u0003\u000f\u0001\u000b\u0007\u0016\u0001\u0017\u0005\u0016\u0004\u0018\u0005\u0016\u0001\u0019\u0001\u0016\r\u0018\u0003\u0016\u0003\u0018\b\u0016\u0001\u0017\u0005\u0016\u0004\u001a\u0005\u0016\u0001\u001b\u0001\u0016\r\u001a\u0003\u0016\u0003\u001a\u0001\u0016\u0007\u001c\u0001\u001d\u0005\u001c\u0004\u001e\u0001\u001c\u0001\u001f\u0002\u0016\u0001\u001c\u0001 \u0001\u001c\r\u001e\u0003\u001c\u0001!\u0002\u001e\u0002\u001c\u0001\"\u0005\u001c\u0001\u001d\u0005\u001c\u0004#\u0004\u001c\u0001$\u0002\u001c\r#\u0003\u001c\u0003#\b\u001c\u0001\u001d\u0005\u001c\u0004%\u0004\u001c\u0001$\u0002\u001c\r%\u0003\u001c\u0003%\b\u001c\u0001\u001d\u0005\u001c\u0004%\u0004\u001c\u0001&\u0002\u001c\r%\u0003\u001c\u0003%\b\u001c\u0001\u001d\u0001\u001c\u0001'\u0003\u001c\u0004(\u0007\u001c\r(\u0003\u001c\u0003(\b\u001c\u0001)\u0005\u001c\u0004*\u0007\u001c\r*\u0001\u001c\u0001+\u0001\u001c\u0003*\u0001\u001c\u0001,\u0001-\u0005,\u0001.\u0001,\u0001/\u0003,\u00040\u0004,\u00011\u0002,\r0\u0002,\u00012\u00030\u0001,-\u0000\u000132\u0000\u00014\u0004\u0000\u00045\u0007\u0000\u00065\u00016\u00065\u0003\u0000\u00035\n\u0000\u00017#\u0000\u00018\u00019\u0001:\u0001;\u0002<\u0001\u0000\u0001=\u0003\u0000\u0001=\u0001\u000f\u0001\u0010\u0001\u0011\u0001\u0012\u0007\u0000\r\u000f\u0003\u0000\u0003\u000f\u0003\u0000\u0001>\u0001\u0000\u0001?\u0002@\u0001\u0000\u0001A\u0003\u0000\u0001A\u0003\u0010\u0001\u0012\u0007\u0000\r\u0010\u0003\u0000\u0003\u0010\u0002\u0000\u00018\u0001B\u0001:\u0001;\u0002@\u0001\u0000\u0001A\u0003\u0000\u0001A\u0001\u0011\u0001\u0010\u0001\u0011\u0001\u0012\u0007\u0000\r\u0011\u0003\u0000\u0003\u0011\u0003\u0000\u0001C\u0001\u0000\u0001?\u0002<\u0001\u0000\u0001=\u0003\u0000\u0001=\u0004\u0012\u0007\u0000\r\u0012\u0003\u0000\u0003\u0012\u0016\u0000\u0001D;\u0000\u0001E\u000e\u0000\u00014\u0004\u0000\u00045\u0007\u0000\r5\u0003\u0000\u00035\u000e\u0000\u0004\u0018\u0007\u0000\r\u0018\u0003\u0000\u0003\u0018\u0017\u0000\u0001F\"\u0000\u0004\u001a\u0007\u0000\r\u001a\u0003\u0000\u0003\u001a\u0017\u0000\u0001G\"\u0000\u0004\u001e\u0007\u0000\r\u001e\u0003\u0000\u0003\u001e\u0014\u0000\u0001\u0016%\u0000\u0004\u001e\u0007\u0000\u0002\u001e\u0001H\n\u001e\u0003\u0000\u0003\u001e\u0002\u0000\u0001I7\u0000\u0004#\u0007\u0000\r#\u0003\u0000\u0003#\u0016\u0000\u0001J#\u0000\u0004%\u0007\u0000\r%\u0003\u0000\u0003%\u0016\u0000\u0001K\u001f\u0000\u0001L/\u0000\u0004(\u0007\u0000\r(\u0003\u0000\u0003(\t\u0000\u0001M\u0004\u0000\u00045\u0007\u0000\r5\u0003\u0000\u00035\u000e\u0000\u0004*\u0007\u0000\r*\u0003\u0000\u0003*'\u0000\u0001L\u0006\u0000\u0001N3\u0000\u0001O/\u0000\u00040\u0007\u0000\r0\u0003\u0000\u00030\u0016\u0000\u0001P#\u0000\u00045\u0007\u0000\r5\u0003\u0000\u00035\f\u0000\u0001\u001c\u0001\u0000\u0004Q\u0001\u0000\u0003R\u0003\u0000\rQ\u0003\u0000\u0003Q\f\u0000\u0001\u001c\u0001\u0000\u0004Q\u0001\u0000\u0003R\u0003\u0000\u0003Q\u0001S\tQ\u0003\u0000\u0003Q\u000e\u0000\u0001T\u0001\u0000\u0001T\b\u0000\rT\u0003\u0000\u0003T\u000e\u0000\u0001U\u0001V\u0001W\u0001X\u0007\u0000\rU\u0003\u0000\u0003U\u000e\u0000\u0001Y\u0001\u0000\u0001Y\b\u0000\rY\u0003\u0000\u0003Y\u000e\u0000\u0001Z\u0001[\u0001Z\u0001[\u0007\u0000\rZ\u0003\u0000\u0003Z\u000e\u0000\u0001\\\u0002]\u0001^\u0007\u0000\r\\\u0003\u0000\u0003\\\u000e\u0000\u0001=\u0002_\b\u0000\r=\u0003\u0000\u0003=\u000e\u0000\u0001`\u0002a\u0001b\u0007\u0000\r`\u0003\u0000\u0003`\u000e\u0000\u0004[\u0007\u0000\r[\u0003\u0000\u0003[\u000e\u0000\u0001c\u0002d\u0001e\u0007\u0000\rc\u0003\u0000\u0003c\u000e\u0000\u0001f\u0002g\u0001h\u0007\u0000\rf\u0003\u0000\u0003f\u000e\u0000\u0001i\u0001a\u0001j\u0001b\u0007\u0000\ri\u0003\u0000\u0003i\u000e\u0000\u0001k\u0002V\u0001X\u0007\u0000\rk\u0003\u0000\u0003k\u0018\u0000\u0001l\u0001m4\u0000\u0001n\u0017\u0000\u0004\u001e\u0007\u0000\u0002\u001e\u0001o\n\u001e\u0003\u0000\u0003\u001e\u0002\u0000\u0001pA\u0000\u0001q\u0001r \u0000\u00045\u0007\u0000\u00065\u0001s\u00065\u0003\u0000\u00035\u0002\u0000\u0001t3\u0000\u0001u9\u0000\u0001v\u0001w\u001c\u0000\u0001x\u0001\u0000\u0001\u001c\u0001\u0000\u0004Q\u0001\u0000\u0003R\u0003\u0000\rQ\u0003\u0000\u0003Q\u000e\u0000\u0004y\u0001\u0000\u0003R\u0003\u0000\ry\u0003\u0000\u0003y\n\u0000\u0001x\u0001\u0000\u0001\u001c\u0001\u0000\u0004Q\u0001\u0000\u0003R\u0003\u0000\bQ\u0001z\u0004Q\u0003\u0000\u0003Q\u0002\u0000\u00018\u000b\u0000\u0001T\u0001\u0000\u0001T\b\u0000\rT\u0003\u0000\u0003T\u0003\u0000\u0001{\u0001\u0000\u0001?\u0002|\u0006\u0000\u0001U\u0001V\u0001W\u0001X\u0007\u0000\rU\u0003\u0000\u0003U\u0003\u0000\u0001}\u0001\u0000\u0001?\u0002~\u0001\u0000\u0001\u007f\u0003\u0000\u0001\u007f\u0003V\u0001X\u0007\u0000\rV\u0003\u0000\u0003V\u0003\u0000\u0001\u0080\u0001\u0000\u0001?\u0002~\u0001\u0000\u0001\u007f\u0003\u0000\u0001\u007f\u0001W\u0001V\u0001W\u0001X\u0007\u0000\rW\u0003\u0000\u0003W\u0003\u0000\u0001\u0081\u0001\u0000\u0001?\u0002|\u0006\u0000\u0004X\u0007\u0000\rX\u0003\u0000\u0003X\u0003\u0000\u0001\u0082\u0002\u0000\u0001\u0082\u0007\u0000\u0001Z\u0001[\u0001Z\u0001[\u0007\u0000\rZ\u0003\u0000\u0003Z\u0003\u0000\u0001\u0082\u0002\u0000\u0001\u0082\u0007\u0000\u0004[\u0007\u0000\r[\u0003\u0000\u0003[\u0003\u0000\u0001|\u0001\u0000\u0001?\u0002|\u0006\u0000\u0001\\\u0002]\u0001^\u0007\u0000\r\\\u0003\u0000\u0003\\\u0003\u0000\u0001~\u0001\u0000\u0001?\u0002~\u0001\u0000\u0001\u007f\u0003\u0000\u0001\u007f\u0003]\u0001^\u0007\u0000\r]\u0003\u0000\u0003]\u0003\u0000\u0001|\u0001\u0000\u0001?\u0002|\u0006\u0000\u0004^\u0007\u0000\r^\u0003\u0000\u0003^\u0003\u0000\u0001\u007f\u0002\u0000\u0002\u007f\u0001\u0000\u0001\u007f\u0003\u0000\u0001\u007f\u0003_\b\u0000\r_\u0003\u0000\u0003_\u0003\u0000\u0001C\u0001\u0000\u0001?\u0002<\u0001\u0000\u0001=\u0003\u0000\u0001=\u0001`\u0002a\u0001b\u0007\u0000\r`\u0003\u0000\u0003`\u0003\u0000\u0001>\u0001\u0000\u0001?\u0002@\u0001\u0000\u0001A\u0003\u0000\u0001A\u0003a\u0001b\u0007\u0000\ra\u0003\u0000\u0003a\u0003\u0000\u0001C\u0001\u0000\u0001?\u0002<\u0001\u0000\u0001=\u0003\u0000\u0001=\u0004b\u0007\u0000\rb\u0003\u0000\u0003b\u0003\u0000\u0001<\u0001\u0000\u0001?\u0002<\u0001\u0000\u0001=\u0003\u0000\u0001=\u0001c\u0002d\u0001e\u0007\u0000\rc\u0003\u0000\u0003c\u0003\u0000\u0001@\u0001\u0000\u0001?\u0002@\u0001\u0000\u0001A\u0003\u0000\u0001A\u0003d\u0001e\u0007\u0000\rd\u0003\u0000\u0003d\u0003\u0000\u0001<\u0001\u0000\u0001?\u0002<\u0001\u0000\u0001=\u0003\u0000\u0001=\u0004e\u0007\u0000\re\u0003\u0000\u0003e\u0003\u0000\u0001=\u0002\u0000\u0002=\u0001\u0000\u0001=\u0003\u0000\u0001=\u0001f\u0002g\u0001h\u0007\u0000\rf\u0003\u0000\u0003f\u0003\u0000\u0001A\u0002\u0000\u0002A\u0001\u0000\u0001A\u0003\u0000\u0001A\u0003g\u0001h\u0007\u0000\rg\u0003\u0000\u0003g\u0003\u0000\u0001=\u0002\u0000\u0002=\u0001\u0000\u0001=\u0003\u0000\u0001=\u0004h\u0007\u0000\rh\u0003\u0000\u0003h\u0003\u0000\u0001\u0083\u0001\u0000\u0001?\u0002<\u0001\u0000\u0001=\u0003\u0000\u0001=\u0001i\u0001a\u0001j\u0001b\u0007\u0000\ri\u0003\u0000\u0003i\u0003\u0000\u0001\u0084\u0001\u0000\u0001?\u0002@\u0001\u0000\u0001A\u0003\u0000\u0001A\u0001j\u0001a\u0001j\u0001b\u0007\u0000\rj\u0003\u0000\u0003j\u0003\u0000\u0001\u0081\u0001\u0000\u0001?\u0002|\u0006\u0000\u0001k\u0002V\u0001X\u0007\u0000\rk\u0003\u0000\u0003k\u0019\u0000\u0001m,\u0000\u0001\u00854\u0000\u0001\u0086\u0016\u0000\u0004\u001e\u0007\u0000\r\u001e\u0003\u0000\u0001\u001e\u0001\u0087\u0001\u001e\u0019\u0000\u0001r,\u0000\u0001\u0088\u001d\u0000\u0001\u001c\u0001\u0000\u0004Q\u0001\u0000\u0003R\u0003\u0000\u0003Q\u0001\u0089\tQ\u0003\u0000\u0003Q\u0002\u0000\u0001\u008aB\u0000\u0001w,\u0000\u0001\u008b\u001c\u0000\u0001\u008c*\u0000\u0001x\u0003\u0000\u0004y\u0007\u0000\ry\u0003\u0000\u0003y\n\u0000\u0001x\u0001\u0000\u0001\u008d\u0001\u0000\u0004Q\u0001\u0000\u0003R\u0003\u0000\rQ\u0003\u0000\u0003Q\u000e\u0000\u0001\u008e\u0001X\u0001\u008e\u0001X\u0007\u0000\r\u008e\u0003\u0000\u0003\u008e\u000e\u0000\u0004^\u0007\u0000\r^\u0003\u0000\u0003^\u000e\u0000\u0004b\u0007\u0000\rb\u0003\u0000\u0003b\u000e\u0000\u0004e\u0007\u0000\re\u0003\u0000\u0003e\u000e\u0000\u0004h\u0007\u0000\rh\u0003\u0000\u0003h\u000e\u0000\u0001\u008f\u0001b\u0001\u008f\u0001b\u0007\u0000\r\u008f\u0003\u0000\u0003\u008f\u000e\u0000\u0004X\u0007\u0000\rX\u0003\u0000\u0003X\u000e\u0000\u0004\u0090\u0007\u0000\r\u0090\u0003\u0000\u0003\u0090\u001b\u0000\u0001\u00911\u0000\u0001\u0092\u0018\u0000\u0004\u001e\u0006\u0000\u0001\u0093\r\u001e\u0003\u0000\u0002\u001e\u0001\u0094\u001b\u0000\u0001\u0095\u001a\u0000\u0001x\u0001\u0000\u0001\u001c\u0001\u0000\u0004Q\u0001\u0000\u0003R\u0003\u0000\bQ\u0001\u0096\u0004Q\u0003\u0000\u0003Q\u0002\u0000\u0001\u0097D\u0000\u0001\u0098\u001e\u0000\u0004\u0099\u0007\u0000\r\u0099\u0003\u0000\u0003\u0099\u0003\u0000\u0001{\u0001\u0000\u0001?\u0002|\u0006\u0000\u0001\u008e\u0001X\u0001\u008e\u0001X\u0007\u0000\r\u008e\u0003\u0000\u0003\u008e\u0003\u0000\u0001\u0083\u0001\u0000\u0001?\u0002<\u0001\u0000\u0001=\u0003\u0000\u0001=\u0001\u008f\u0001b\u0001\u008f\u0001b\u0007\u0000\r\u008f\u0003\u0000\u0003\u008f\u0003\u0000\u0001\u0082\u0002\u0000\u0001\u0082\u0007\u0000\u0004\u0090\u0007\u0000\r\u0090\u0003\u0000\u0003\u0090\u001c\u0000\u0001\u009a-\u0000\u0001\u009b\u0016\u0000\u0001\u009c0\u0000\u0004\u001e\u0006\u0000\u0001\u0093\r\u001e\u0003\u0000\u0003\u001e\u001c\u0000\u0001\u009d\u0019\u0000\u0001x\u0001\u0000\u0001L\u0001\u0000\u0004Q\u0001\u0000\u0003R\u0003\u0000\rQ\u0003\u0000\u0003Q\u001c\u0000\u0001\u009e\u001a\u0000\u0001\u009f\u0002\u0000\u0004\u0099\u0007\u0000\r\u0099\u0003\u0000\u0003\u0099\u001d\u0000\u0001 2\u0000\u0001¡\u0010\u0000\u0001¢?\u0000\u0001£+\u0000\u0001¤\u001a\u0000\u0001\u001c\u0001\u0000\u0004y\u0001\u0000\u0003R\u0003\u0000\ry\u0003\u0000\u0003y\u001e\u0000\u0001¥+\u0000\u0001¦\u001b\u0000\u0004§\u0007\u0000\r§\u0003\u0000\u0003§\u001e\u0000\u0001¨+\u0000\u0001©,\u0000\u0001ª1\u0000\u0001«\t\u0000\u0001¬\n\u0000\u0004§\u0007\u0000\r§\u0003\u0000\u0003§\u001f\u0000\u0001\u00ad+\u0000\u0001®,\u0000\u0001¯\u0012\u0000\u0001\u000b2\u0000\u0004°\u0007\u0000\r°\u0003\u0000\u0003° \u0000\u0001±+\u0000\u0001²#\u0000\u0001³\u0016\u0000\u0002°\u0001\u0000\u0002°\u0001\u0000\u0002°\u0002\u0000\u0005°\u0007\u0000\r°\u0003\u0000\u0004°\u0017\u0000\u0001´+\u0000\u0001µ\u0014\u0000";
    private static final int ZZ_UNKNOWN_ERROR = 0;
    private static final int ZZ_NO_MATCH = 1;
    private static final int ZZ_PUSHBACK_2BIG = 2;
    private static final String[] ZZ_ERROR_MSG;
    private static final int[] ZZ_ATTRIBUTE;
    private static final String ZZ_ATTRIBUTE_PACKED_0 = "\n\u0000\u0001\t\u0007\u0001\u0001\t\u0002\u0001\u0001\t\u0005\u0001\u0001\t\u0003\u0001\u0001\t\u000b\u0001\u0001\t\u0005\u0001\u0002\t\u0003\u0000\u0001\t\f\u0000\u0002\u0001\u0002\t\u0001\u0001\u0001\u0000\u0002\u0001\u0001\t\u0001\u0000\u0001\u0001\u0001\u0000\u0001\u0001\u0003\u0000\u0007\u0001\u0002\u0000\u0001\u0001\u0001\u0000\r\u0001\u0003\u0000\u0001\u0001\u0001\t\u0003\u0000\u0001\u0001\u0001\t\u0005\u0000\u0001\u0001\u0004\u0000\u0001\u0001\u0002\u0000\u0002\u0001\u0002\u0000\u0001\u0001\u0005\u0000\u0001\t\u0003\u0001\u0003\u0000\u0001\u0001\u0002\u0000\u0001\t\u0018\u0000\u0001\u0001\u0002\u0000\u0003\t";
    private Reader zzReader;
    private int zzState;
    private int zzLexicalState;
    private char[] zzBuffer;
    private int zzMarkedPos;
    private int zzCurrentPos;
    private int zzStartRead;
    private int zzEndRead;
    private int yyline;
    private int yychar;
    private int yycolumn;
    private boolean zzAtBOL;
    private boolean zzAtEOF;
    private boolean zzEOFDone;
    private int zzFinalHighSurrogate;
    public static final int ALPHANUM = 0;
    public static final int APOSTROPHE = 1;
    public static final int ACRONYM = 2;
    public static final int COMPANY = 3;
    public static final int EMAIL = 4;
    public static final int HOST = 5;
    public static final int NUM = 6;
    public static final int CJ = 7;
    public static final int INTERNAL_LINK = 8;
    public static final int EXTERNAL_LINK = 9;
    public static final int CITATION = 10;
    public static final int CATEGORY = 11;
    public static final int BOLD = 12;
    public static final int ITALICS = 13;
    public static final int BOLD_ITALICS = 14;
    public static final int HEADING = 15;
    public static final int SUB_HEADING = 16;
    public static final int EXTERNAL_LINK_URL = 17;
    private int currentTokType;
    private int numBalanced;
    private int positionInc;
    private int numLinkToks;
    private int numWikiTokensSeen;
    public static final String[] TOKEN_TYPES;
    
    private static int[] zzUnpackAction() {
        final int[] result = new int[181];
        int offset = 0;
        offset = zzUnpackAction("\n\u0000\u0004\u0001\u0004\u0002\u0001\u0003\u0001\u0004\u0001\u0001\u0002\u0005\u0001\u0006\u0001\u0005\u0001\u0007\u0001\u0005\u0002\b\u0001\t\u0001\u0005\u0001\n\u0001\t\u0001\u000b\u0001\f\u0001\r\u0001\u000e\u0001\r\u0001\u000f\u0001\u0010\u0001\b\u0001\u0011\u0001\b\u0004\u0012\u0001\u0013\u0001\u0014\u0001\u0015\u0001\u0016\u0003\u0000\u0001\u0017\f\u0000\u0001\u0018\u0001\u0019\u0001\u001a\u0001\u001b\u0001\t\u0001\u0000\u0001\u001c\u0001\u001d\u0001\u001e\u0001\u0000\u0001\u001f\u0001\u0000\u0001 \u0003\u0000\u0001!\u0001\"\u0002#\u0001\"\u0002$\u0002\u0000\u0001#\u0001\u0000\f#\u0001\"\u0003\u0000\u0001\t\u0001%\u0003\u0000\u0001&\u0001'\u0005\u0000\u0001(\u0004\u0000\u0001(\u0002\u0000\u0002(\u0002\u0000\u0001\t\u0005\u0000\u0001\u0019\u0001\"\u0001#\u0001)\u0003\u0000\u0001\t\u0002\u0000\u0001*\u0018\u0000\u0001+\u0002\u0000\u0001,\u0001-\u0001.", offset, result);
        return result;
    }
    
    private static int zzUnpackAction(final String packed, final int offset, final int[] result) {
        int i = 0;
        int j = offset;
        final int l = packed.length();
        while (i < l) {
            int count = packed.charAt(i++);
            final int value = packed.charAt(i++);
            do {
                result[j++] = value;
            } while (--count > 0);
        }
        return j;
    }
    
    private static int[] zzUnpackRowMap() {
        final int[] result = new int[181];
        int offset = 0;
        offset = zzUnpackRowMap("\u0000\u0000\u0000,\u0000X\u0000\u0084\u0000°\u0000\u00dc\u0000\u0108\u0000\u0134\u0000\u0160\u0000\u018c\u0000\u01b8\u0000\u01e4\u0000\u0210\u0000\u023c\u0000\u0268\u0000\u0294\u0000\u02c0\u0000\u02ec\u0000\u01b8\u0000\u0318\u0000\u0344\u0000\u01b8\u0000\u0370\u0000\u039c\u0000\u03c8\u0000\u03f4\u0000\u0420\u0000\u01b8\u0000\u0370\u0000\u044c\u0000\u0478\u0000\u01b8\u0000\u04a4\u0000\u04d0\u0000\u04fc\u0000\u0528\u0000\u0554\u0000\u0580\u0000\u05ac\u0000\u05d8\u0000\u0604\u0000\u0630\u0000\u065c\u0000\u01b8\u0000\u0688\u0000\u0370\u0000\u06b4\u0000\u06e0\u0000\u070c\u0000\u01b8\u0000\u01b8\u0000\u0738\u0000\u0764\u0000\u0790\u0000\u01b8\u0000\u07bc\u0000\u07e8\u0000\u0814\u0000\u0840\u0000\u086c\u0000\u0898\u0000\u08c4\u0000\u08f0\u0000\u091c\u0000\u0948\u0000\u0974\u0000\u09a0\u0000\u09cc\u0000\u09f8\u0000\u01b8\u0000\u01b8\u0000\u0a24\u0000\u0a50\u0000\u0a7c\u0000\u0a7c\u0000\u01b8\u0000\u0aa8\u0000\u0ad4\u0000\u0b00\u0000\u0b2c\u0000\u0b58\u0000\u0b84\u0000\u0bb0\u0000\u0bdc\u0000\u0c08\u0000\u0c34\u0000\u0c60\u0000\u0c8c\u0000\u0814\u0000\u0cb8\u0000\u0ce4\u0000\u0d10\u0000\u0d3c\u0000\u0d68\u0000\u0d94\u0000\u0dc0\u0000\u0dec\u0000\u0e18\u0000\u0e44\u0000\u0e70\u0000\u0e9c\u0000\u0ec8\u0000\u0ef4\u0000\u0f20\u0000\u0f4c\u0000\u0f78\u0000\u0fa4\u0000\u0fd0\u0000\u0ffc\u0000\u1028\u0000\u1054\u0000\u01b8\u0000\u1080\u0000\u10ac\u0000\u10d8\u0000\u1104\u0000\u01b8\u0000\u1130\u0000\u115c\u0000\u1188\u0000\u11b4\u0000\u11e0\u0000\u120c\u0000\u1238\u0000\u1264\u0000\u1290\u0000\u12bc\u0000\u12e8\u0000\u1314\u0000\u1340\u0000\u07e8\u0000\u0974\u0000\u136c\u0000\u1398\u0000\u13c4\u0000\u13f0\u0000\u141c\u0000\u1448\u0000\u1474\u0000\u14a0\u0000\u01b8\u0000\u14cc\u0000\u14f8\u0000\u1524\u0000\u1550\u0000\u157c\u0000\u15a8\u0000\u15d4\u0000\u1600\u0000\u162c\u0000\u01b8\u0000\u1658\u0000\u1684\u0000\u16b0\u0000\u16dc\u0000\u1708\u0000\u1734\u0000\u1760\u0000\u178c\u0000\u17b8\u0000\u17e4\u0000\u1810\u0000\u183c\u0000\u1868\u0000\u1894\u0000\u18c0\u0000\u18ec\u0000\u1918\u0000\u1944\u0000\u1970\u0000\u199c\u0000\u19c8\u0000\u19f4\u0000\u1a20\u0000\u1a4c\u0000\u1a78\u0000\u1aa4\u0000\u1ad0\u0000\u01b8\u0000\u01b8\u0000\u01b8", offset, result);
        return result;
    }
    
    private static int zzUnpackRowMap(final String packed, final int offset, final int[] result) {
        int i = 0;
        int j = offset;
        int high;
        for (int l = packed.length(); i < l; high = packed.charAt(i++) << 16, result[j++] = (high | packed.charAt(i++))) {}
        return j;
    }
    
    private static int[] zzUnpackTrans() {
        final int[] result = new int[6908];
        int offset = 0;
        offset = zzUnpackTrans("\u0001\u000b\u0001\f\u0005\u000b\u0001\r\u0001\u000b\u0001\u000e\u0003\u000b\u0001\u000f\u0001\u0010\u0001\u0011\u0001\u0012\u0001\u0013\u0003\u000b\u0001\u0014\u0002\u000b\r\u000f\u0001\u0015\u0002\u000b\u0003\u000f\u0001\u000b\u0007\u0016\u0001\u0017\u0005\u0016\u0004\u0018\u0005\u0016\u0001\u0019\u0001\u0016\r\u0018\u0003\u0016\u0003\u0018\b\u0016\u0001\u0017\u0005\u0016\u0004\u001a\u0005\u0016\u0001\u001b\u0001\u0016\r\u001a\u0003\u0016\u0003\u001a\u0001\u0016\u0007\u001c\u0001\u001d\u0005\u001c\u0004\u001e\u0001\u001c\u0001\u001f\u0002\u0016\u0001\u001c\u0001 \u0001\u001c\r\u001e\u0003\u001c\u0001!\u0002\u001e\u0002\u001c\u0001\"\u0005\u001c\u0001\u001d\u0005\u001c\u0004#\u0004\u001c\u0001$\u0002\u001c\r#\u0003\u001c\u0003#\b\u001c\u0001\u001d\u0005\u001c\u0004%\u0004\u001c\u0001$\u0002\u001c\r%\u0003\u001c\u0003%\b\u001c\u0001\u001d\u0005\u001c\u0004%\u0004\u001c\u0001&\u0002\u001c\r%\u0003\u001c\u0003%\b\u001c\u0001\u001d\u0001\u001c\u0001'\u0003\u001c\u0004(\u0007\u001c\r(\u0003\u001c\u0003(\b\u001c\u0001)\u0005\u001c\u0004*\u0007\u001c\r*\u0001\u001c\u0001+\u0001\u001c\u0003*\u0001\u001c\u0001,\u0001-\u0005,\u0001.\u0001,\u0001/\u0003,\u00040\u0004,\u00011\u0002,\r0\u0002,\u00012\u00030\u0001,-\u0000\u000132\u0000\u00014\u0004\u0000\u00045\u0007\u0000\u00065\u00016\u00065\u0003\u0000\u00035\n\u0000\u00017#\u0000\u00018\u00019\u0001:\u0001;\u0002<\u0001\u0000\u0001=\u0003\u0000\u0001=\u0001\u000f\u0001\u0010\u0001\u0011\u0001\u0012\u0007\u0000\r\u000f\u0003\u0000\u0003\u000f\u0003\u0000\u0001>\u0001\u0000\u0001?\u0002@\u0001\u0000\u0001A\u0003\u0000\u0001A\u0003\u0010\u0001\u0012\u0007\u0000\r\u0010\u0003\u0000\u0003\u0010\u0002\u0000\u00018\u0001B\u0001:\u0001;\u0002@\u0001\u0000\u0001A\u0003\u0000\u0001A\u0001\u0011\u0001\u0010\u0001\u0011\u0001\u0012\u0007\u0000\r\u0011\u0003\u0000\u0003\u0011\u0003\u0000\u0001C\u0001\u0000\u0001?\u0002<\u0001\u0000\u0001=\u0003\u0000\u0001=\u0004\u0012\u0007\u0000\r\u0012\u0003\u0000\u0003\u0012\u0016\u0000\u0001D;\u0000\u0001E\u000e\u0000\u00014\u0004\u0000\u00045\u0007\u0000\r5\u0003\u0000\u00035\u000e\u0000\u0004\u0018\u0007\u0000\r\u0018\u0003\u0000\u0003\u0018\u0017\u0000\u0001F\"\u0000\u0004\u001a\u0007\u0000\r\u001a\u0003\u0000\u0003\u001a\u0017\u0000\u0001G\"\u0000\u0004\u001e\u0007\u0000\r\u001e\u0003\u0000\u0003\u001e\u0014\u0000\u0001\u0016%\u0000\u0004\u001e\u0007\u0000\u0002\u001e\u0001H\n\u001e\u0003\u0000\u0003\u001e\u0002\u0000\u0001I7\u0000\u0004#\u0007\u0000\r#\u0003\u0000\u0003#\u0016\u0000\u0001J#\u0000\u0004%\u0007\u0000\r%\u0003\u0000\u0003%\u0016\u0000\u0001K\u001f\u0000\u0001L/\u0000\u0004(\u0007\u0000\r(\u0003\u0000\u0003(\t\u0000\u0001M\u0004\u0000\u00045\u0007\u0000\r5\u0003\u0000\u00035\u000e\u0000\u0004*\u0007\u0000\r*\u0003\u0000\u0003*'\u0000\u0001L\u0006\u0000\u0001N3\u0000\u0001O/\u0000\u00040\u0007\u0000\r0\u0003\u0000\u00030\u0016\u0000\u0001P#\u0000\u00045\u0007\u0000\r5\u0003\u0000\u00035\f\u0000\u0001\u001c\u0001\u0000\u0004Q\u0001\u0000\u0003R\u0003\u0000\rQ\u0003\u0000\u0003Q\f\u0000\u0001\u001c\u0001\u0000\u0004Q\u0001\u0000\u0003R\u0003\u0000\u0003Q\u0001S\tQ\u0003\u0000\u0003Q\u000e\u0000\u0001T\u0001\u0000\u0001T\b\u0000\rT\u0003\u0000\u0003T\u000e\u0000\u0001U\u0001V\u0001W\u0001X\u0007\u0000\rU\u0003\u0000\u0003U\u000e\u0000\u0001Y\u0001\u0000\u0001Y\b\u0000\rY\u0003\u0000\u0003Y\u000e\u0000\u0001Z\u0001[\u0001Z\u0001[\u0007\u0000\rZ\u0003\u0000\u0003Z\u000e\u0000\u0001\\\u0002]\u0001^\u0007\u0000\r\\\u0003\u0000\u0003\\\u000e\u0000\u0001=\u0002_\b\u0000\r=\u0003\u0000\u0003=\u000e\u0000\u0001`\u0002a\u0001b\u0007\u0000\r`\u0003\u0000\u0003`\u000e\u0000\u0004[\u0007\u0000\r[\u0003\u0000\u0003[\u000e\u0000\u0001c\u0002d\u0001e\u0007\u0000\rc\u0003\u0000\u0003c\u000e\u0000\u0001f\u0002g\u0001h\u0007\u0000\rf\u0003\u0000\u0003f\u000e\u0000\u0001i\u0001a\u0001j\u0001b\u0007\u0000\ri\u0003\u0000\u0003i\u000e\u0000\u0001k\u0002V\u0001X\u0007\u0000\rk\u0003\u0000\u0003k\u0018\u0000\u0001l\u0001m4\u0000\u0001n\u0017\u0000\u0004\u001e\u0007\u0000\u0002\u001e\u0001o\n\u001e\u0003\u0000\u0003\u001e\u0002\u0000\u0001pA\u0000\u0001q\u0001r \u0000\u00045\u0007\u0000\u00065\u0001s\u00065\u0003\u0000\u00035\u0002\u0000\u0001t3\u0000\u0001u9\u0000\u0001v\u0001w\u001c\u0000\u0001x\u0001\u0000\u0001\u001c\u0001\u0000\u0004Q\u0001\u0000\u0003R\u0003\u0000\rQ\u0003\u0000\u0003Q\u000e\u0000\u0004y\u0001\u0000\u0003R\u0003\u0000\ry\u0003\u0000\u0003y\n\u0000\u0001x\u0001\u0000\u0001\u001c\u0001\u0000\u0004Q\u0001\u0000\u0003R\u0003\u0000\bQ\u0001z\u0004Q\u0003\u0000\u0003Q\u0002\u0000\u00018\u000b\u0000\u0001T\u0001\u0000\u0001T\b\u0000\rT\u0003\u0000\u0003T\u0003\u0000\u0001{\u0001\u0000\u0001?\u0002|\u0006\u0000\u0001U\u0001V\u0001W\u0001X\u0007\u0000\rU\u0003\u0000\u0003U\u0003\u0000\u0001}\u0001\u0000\u0001?\u0002~\u0001\u0000\u0001\u007f\u0003\u0000\u0001\u007f\u0003V\u0001X\u0007\u0000\rV\u0003\u0000\u0003V\u0003\u0000\u0001\u0080\u0001\u0000\u0001?\u0002~\u0001\u0000\u0001\u007f\u0003\u0000\u0001\u007f\u0001W\u0001V\u0001W\u0001X\u0007\u0000\rW\u0003\u0000\u0003W\u0003\u0000\u0001\u0081\u0001\u0000\u0001?\u0002|\u0006\u0000\u0004X\u0007\u0000\rX\u0003\u0000\u0003X\u0003\u0000\u0001\u0082\u0002\u0000\u0001\u0082\u0007\u0000\u0001Z\u0001[\u0001Z\u0001[\u0007\u0000\rZ\u0003\u0000\u0003Z\u0003\u0000\u0001\u0082\u0002\u0000\u0001\u0082\u0007\u0000\u0004[\u0007\u0000\r[\u0003\u0000\u0003[\u0003\u0000\u0001|\u0001\u0000\u0001?\u0002|\u0006\u0000\u0001\\\u0002]\u0001^\u0007\u0000\r\\\u0003\u0000\u0003\\\u0003\u0000\u0001~\u0001\u0000\u0001?\u0002~\u0001\u0000\u0001\u007f\u0003\u0000\u0001\u007f\u0003]\u0001^\u0007\u0000\r]\u0003\u0000\u0003]\u0003\u0000\u0001|\u0001\u0000\u0001?\u0002|\u0006\u0000\u0004^\u0007\u0000\r^\u0003\u0000\u0003^\u0003\u0000\u0001\u007f\u0002\u0000\u0002\u007f\u0001\u0000\u0001\u007f\u0003\u0000\u0001\u007f\u0003_\b\u0000\r_\u0003\u0000\u0003_\u0003\u0000\u0001C\u0001\u0000\u0001?\u0002<\u0001\u0000\u0001=\u0003\u0000\u0001=\u0001`\u0002a\u0001b\u0007\u0000\r`\u0003\u0000\u0003`\u0003\u0000\u0001>\u0001\u0000\u0001?\u0002@\u0001\u0000\u0001A\u0003\u0000\u0001A\u0003a\u0001b\u0007\u0000\ra\u0003\u0000\u0003a\u0003\u0000\u0001C\u0001\u0000\u0001?\u0002<\u0001\u0000\u0001=\u0003\u0000\u0001=\u0004b\u0007\u0000\rb\u0003\u0000\u0003b\u0003\u0000\u0001<\u0001\u0000\u0001?\u0002<\u0001\u0000\u0001=\u0003\u0000\u0001=\u0001c\u0002d\u0001e\u0007\u0000\rc\u0003\u0000\u0003c\u0003\u0000\u0001@\u0001\u0000\u0001?\u0002@\u0001\u0000\u0001A\u0003\u0000\u0001A\u0003d\u0001e\u0007\u0000\rd\u0003\u0000\u0003d\u0003\u0000\u0001<\u0001\u0000\u0001?\u0002<\u0001\u0000\u0001=\u0003\u0000\u0001=\u0004e\u0007\u0000\re\u0003\u0000\u0003e\u0003\u0000\u0001=\u0002\u0000\u0002=\u0001\u0000\u0001=\u0003\u0000\u0001=\u0001f\u0002g\u0001h\u0007\u0000\rf\u0003\u0000\u0003f\u0003\u0000\u0001A\u0002\u0000\u0002A\u0001\u0000\u0001A\u0003\u0000\u0001A\u0003g\u0001h\u0007\u0000\rg\u0003\u0000\u0003g\u0003\u0000\u0001=\u0002\u0000\u0002=\u0001\u0000\u0001=\u0003\u0000\u0001=\u0004h\u0007\u0000\rh\u0003\u0000\u0003h\u0003\u0000\u0001\u0083\u0001\u0000\u0001?\u0002<\u0001\u0000\u0001=\u0003\u0000\u0001=\u0001i\u0001a\u0001j\u0001b\u0007\u0000\ri\u0003\u0000\u0003i\u0003\u0000\u0001\u0084\u0001\u0000\u0001?\u0002@\u0001\u0000\u0001A\u0003\u0000\u0001A\u0001j\u0001a\u0001j\u0001b\u0007\u0000\rj\u0003\u0000\u0003j\u0003\u0000\u0001\u0081\u0001\u0000\u0001?\u0002|\u0006\u0000\u0001k\u0002V\u0001X\u0007\u0000\rk\u0003\u0000\u0003k\u0019\u0000\u0001m,\u0000\u0001\u00854\u0000\u0001\u0086\u0016\u0000\u0004\u001e\u0007\u0000\r\u001e\u0003\u0000\u0001\u001e\u0001\u0087\u0001\u001e\u0019\u0000\u0001r,\u0000\u0001\u0088\u001d\u0000\u0001\u001c\u0001\u0000\u0004Q\u0001\u0000\u0003R\u0003\u0000\u0003Q\u0001\u0089\tQ\u0003\u0000\u0003Q\u0002\u0000\u0001\u008aB\u0000\u0001w,\u0000\u0001\u008b\u001c\u0000\u0001\u008c*\u0000\u0001x\u0003\u0000\u0004y\u0007\u0000\ry\u0003\u0000\u0003y\n\u0000\u0001x\u0001\u0000\u0001\u008d\u0001\u0000\u0004Q\u0001\u0000\u0003R\u0003\u0000\rQ\u0003\u0000\u0003Q\u000e\u0000\u0001\u008e\u0001X\u0001\u008e\u0001X\u0007\u0000\r\u008e\u0003\u0000\u0003\u008e\u000e\u0000\u0004^\u0007\u0000\r^\u0003\u0000\u0003^\u000e\u0000\u0004b\u0007\u0000\rb\u0003\u0000\u0003b\u000e\u0000\u0004e\u0007\u0000\re\u0003\u0000\u0003e\u000e\u0000\u0004h\u0007\u0000\rh\u0003\u0000\u0003h\u000e\u0000\u0001\u008f\u0001b\u0001\u008f\u0001b\u0007\u0000\r\u008f\u0003\u0000\u0003\u008f\u000e\u0000\u0004X\u0007\u0000\rX\u0003\u0000\u0003X\u000e\u0000\u0004\u0090\u0007\u0000\r\u0090\u0003\u0000\u0003\u0090\u001b\u0000\u0001\u00911\u0000\u0001\u0092\u0018\u0000\u0004\u001e\u0006\u0000\u0001\u0093\r\u001e\u0003\u0000\u0002\u001e\u0001\u0094\u001b\u0000\u0001\u0095\u001a\u0000\u0001x\u0001\u0000\u0001\u001c\u0001\u0000\u0004Q\u0001\u0000\u0003R\u0003\u0000\bQ\u0001\u0096\u0004Q\u0003\u0000\u0003Q\u0002\u0000\u0001\u0097D\u0000\u0001\u0098\u001e\u0000\u0004\u0099\u0007\u0000\r\u0099\u0003\u0000\u0003\u0099\u0003\u0000\u0001{\u0001\u0000\u0001?\u0002|\u0006\u0000\u0001\u008e\u0001X\u0001\u008e\u0001X\u0007\u0000\r\u008e\u0003\u0000\u0003\u008e\u0003\u0000\u0001\u0083\u0001\u0000\u0001?\u0002<\u0001\u0000\u0001=\u0003\u0000\u0001=\u0001\u008f\u0001b\u0001\u008f\u0001b\u0007\u0000\r\u008f\u0003\u0000\u0003\u008f\u0003\u0000\u0001\u0082\u0002\u0000\u0001\u0082\u0007\u0000\u0004\u0090\u0007\u0000\r\u0090\u0003\u0000\u0003\u0090\u001c\u0000\u0001\u009a-\u0000\u0001\u009b\u0016\u0000\u0001\u009c0\u0000\u0004\u001e\u0006\u0000\u0001\u0093\r\u001e\u0003\u0000\u0003\u001e\u001c\u0000\u0001\u009d\u0019\u0000\u0001x\u0001\u0000\u0001L\u0001\u0000\u0004Q\u0001\u0000\u0003R\u0003\u0000\rQ\u0003\u0000\u0003Q\u001c\u0000\u0001\u009e\u001a\u0000\u0001\u009f\u0002\u0000\u0004\u0099\u0007\u0000\r\u0099\u0003\u0000\u0003\u0099\u001d\u0000\u0001 2\u0000\u0001¡\u0010\u0000\u0001¢?\u0000\u0001£+\u0000\u0001¤\u001a\u0000\u0001\u001c\u0001\u0000\u0004y\u0001\u0000\u0003R\u0003\u0000\ry\u0003\u0000\u0003y\u001e\u0000\u0001¥+\u0000\u0001¦\u001b\u0000\u0004§\u0007\u0000\r§\u0003\u0000\u0003§\u001e\u0000\u0001¨+\u0000\u0001©,\u0000\u0001ª1\u0000\u0001«\t\u0000\u0001¬\n\u0000\u0004§\u0007\u0000\r§\u0003\u0000\u0003§\u001f\u0000\u0001\u00ad+\u0000\u0001®,\u0000\u0001¯\u0012\u0000\u0001\u000b2\u0000\u0004°\u0007\u0000\r°\u0003\u0000\u0003° \u0000\u0001±+\u0000\u0001²#\u0000\u0001³\u0016\u0000\u0002°\u0001\u0000\u0002°\u0001\u0000\u0002°\u0002\u0000\u0005°\u0007\u0000\r°\u0003\u0000\u0004°\u0017\u0000\u0001´+\u0000\u0001µ\u0014\u0000", offset, result);
        return result;
    }
    
    private static int zzUnpackTrans(final String packed, final int offset, final int[] result) {
        int i = 0;
        int j = offset;
        final int l = packed.length();
        while (i < l) {
            int count = packed.charAt(i++);
            int value = packed.charAt(i++);
            --value;
            do {
                result[j++] = value;
            } while (--count > 0);
        }
        return j;
    }
    
    private static int[] zzUnpackAttribute() {
        final int[] result = new int[181];
        int offset = 0;
        offset = zzUnpackAttribute("\n\u0000\u0001\t\u0007\u0001\u0001\t\u0002\u0001\u0001\t\u0005\u0001\u0001\t\u0003\u0001\u0001\t\u000b\u0001\u0001\t\u0005\u0001\u0002\t\u0003\u0000\u0001\t\f\u0000\u0002\u0001\u0002\t\u0001\u0001\u0001\u0000\u0002\u0001\u0001\t\u0001\u0000\u0001\u0001\u0001\u0000\u0001\u0001\u0003\u0000\u0007\u0001\u0002\u0000\u0001\u0001\u0001\u0000\r\u0001\u0003\u0000\u0001\u0001\u0001\t\u0003\u0000\u0001\u0001\u0001\t\u0005\u0000\u0001\u0001\u0004\u0000\u0001\u0001\u0002\u0000\u0002\u0001\u0002\u0000\u0001\u0001\u0005\u0000\u0001\t\u0003\u0001\u0003\u0000\u0001\u0001\u0002\u0000\u0001\t\u0018\u0000\u0001\u0001\u0002\u0000\u0003\t", offset, result);
        return result;
    }
    
    private static int zzUnpackAttribute(final String packed, final int offset, final int[] result) {
        int i = 0;
        int j = offset;
        final int l = packed.length();
        while (i < l) {
            int count = packed.charAt(i++);
            final int value = packed.charAt(i++);
            do {
                result[j++] = value;
            } while (--count > 0);
        }
        return j;
    }
    
    public final int getNumWikiTokensSeen() {
        return this.numWikiTokensSeen;
    }
    
    public final int yychar() {
        return this.yychar;
    }
    
    public final int getPositionIncrement() {
        return this.positionInc;
    }
    
    final void getText(final CharTermAttribute t) {
        t.copyBuffer(this.zzBuffer, this.zzStartRead, this.zzMarkedPos - this.zzStartRead);
    }
    
    final int setText(final StringBuilder buffer) {
        final int length = this.zzMarkedPos - this.zzStartRead;
        buffer.append(this.zzBuffer, this.zzStartRead, length);
        return length;
    }
    
    final void reset() {
        this.currentTokType = 0;
        this.numBalanced = 0;
        this.positionInc = 1;
        this.numLinkToks = 0;
        this.numWikiTokensSeen = 0;
    }
    
    WikipediaTokenizerImpl(final Reader in) {
        this.zzLexicalState = 0;
        this.zzBuffer = new char[4096];
        this.zzAtBOL = true;
        this.zzFinalHighSurrogate = 0;
        this.numBalanced = 0;
        this.positionInc = 1;
        this.numLinkToks = 0;
        this.numWikiTokensSeen = 0;
        this.zzReader = in;
    }
    
    private static char[] zzUnpackCMap(final String packed) {
        final char[] map = new char[1114112];
        int i = 0;
        int j = 0;
        while (i < 262) {
            int count = packed.charAt(i++);
            final char value = packed.charAt(i++);
            do {
                map[j++] = value;
            } while (--count > 0);
        }
        return map;
    }
    
    private boolean zzRefill() throws IOException {
        if (this.zzStartRead > 0) {
            this.zzEndRead += this.zzFinalHighSurrogate;
            this.zzFinalHighSurrogate = 0;
            System.arraycopy(this.zzBuffer, this.zzStartRead, this.zzBuffer, 0, this.zzEndRead - this.zzStartRead);
            this.zzEndRead -= this.zzStartRead;
            this.zzCurrentPos -= this.zzStartRead;
            this.zzMarkedPos -= this.zzStartRead;
            this.zzStartRead = 0;
        }
        if (this.zzCurrentPos >= this.zzBuffer.length - this.zzFinalHighSurrogate) {
            final char[] newBuffer = new char[this.zzBuffer.length * 2];
            System.arraycopy(this.zzBuffer, 0, newBuffer, 0, this.zzBuffer.length);
            this.zzBuffer = newBuffer;
            this.zzEndRead += this.zzFinalHighSurrogate;
            this.zzFinalHighSurrogate = 0;
        }
        int requested;
        int totalRead;
        int numRead;
        for (requested = this.zzBuffer.length - this.zzEndRead, totalRead = 0; totalRead < requested; totalRead += numRead) {
            numRead = this.zzReader.read(this.zzBuffer, this.zzEndRead + totalRead, requested - totalRead);
            if (numRead == -1) {
                break;
            }
        }
        if (totalRead > 0) {
            this.zzEndRead += totalRead;
            if (totalRead == requested && Character.isHighSurrogate(this.zzBuffer[this.zzEndRead - 1])) {
                --this.zzEndRead;
                this.zzFinalHighSurrogate = 1;
            }
            return false;
        }
        return true;
    }
    
    public final void yyclose() throws IOException {
        this.zzAtEOF = true;
        this.zzEndRead = this.zzStartRead;
        if (this.zzReader != null) {
            this.zzReader.close();
        }
    }
    
    public final void yyreset(final Reader reader) {
        this.zzReader = reader;
        this.zzAtBOL = true;
        this.zzAtEOF = false;
        this.zzEOFDone = false;
        final int n = 0;
        this.zzStartRead = n;
        this.zzEndRead = n;
        final int n2 = 0;
        this.zzMarkedPos = n2;
        this.zzCurrentPos = n2;
        this.zzFinalHighSurrogate = 0;
        final int yyline = 0;
        this.yycolumn = yyline;
        this.yychar = yyline;
        this.yyline = yyline;
        this.zzLexicalState = 0;
        if (this.zzBuffer.length > 4096) {
            this.zzBuffer = new char[4096];
        }
    }
    
    public final int yystate() {
        return this.zzLexicalState;
    }
    
    public final void yybegin(final int newState) {
        this.zzLexicalState = newState;
    }
    
    public final String yytext() {
        return new String(this.zzBuffer, this.zzStartRead, this.zzMarkedPos - this.zzStartRead);
    }
    
    public final char yycharat(final int pos) {
        return this.zzBuffer[this.zzStartRead + pos];
    }
    
    public final int yylength() {
        return this.zzMarkedPos - this.zzStartRead;
    }
    
    private void zzScanError(final int errorCode) {
        String message;
        try {
            message = WikipediaTokenizerImpl.ZZ_ERROR_MSG[errorCode];
        }
        catch (final ArrayIndexOutOfBoundsException e) {
            message = WikipediaTokenizerImpl.ZZ_ERROR_MSG[0];
        }
        throw new Error(message);
    }
    
    public void yypushback(final int number) {
        if (number > this.yylength()) {
            this.zzScanError(2);
        }
        this.zzMarkedPos -= number;
    }
    
    public int getNextToken() throws IOException {
        int zzEndReadL = this.zzEndRead;
        char[] zzBufferL = this.zzBuffer;
        final char[] zzCMapL = WikipediaTokenizerImpl.ZZ_CMAP;
        final int[] zzTransL = WikipediaTokenizerImpl.ZZ_TRANS;
        final int[] zzRowMapL = WikipediaTokenizerImpl.ZZ_ROWMAP;
        final int[] zzAttrL = WikipediaTokenizerImpl.ZZ_ATTRIBUTE;
        while (true) {
            int zzMarkedPosL = this.zzMarkedPos;
            this.yychar += zzMarkedPosL - this.zzStartRead;
            int zzAction = -1;
            final int n = zzMarkedPosL;
            this.zzStartRead = n;
            this.zzCurrentPos = n;
            int zzCurrentPosL = n;
            this.zzState = WikipediaTokenizerImpl.ZZ_LEXSTATE[this.zzLexicalState];
            int zzAttributes = zzAttrL[this.zzState];
            if ((zzAttributes & 0x1) == 0x1) {
                zzAction = this.zzState;
            }
            int zzInput;
            while (true) {
                if (zzCurrentPosL < zzEndReadL) {
                    zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL, zzEndReadL);
                    zzCurrentPosL += Character.charCount(zzInput);
                }
                else {
                    if (this.zzAtEOF) {
                        zzInput = -1;
                        break;
                    }
                    this.zzCurrentPos = zzCurrentPosL;
                    this.zzMarkedPos = zzMarkedPosL;
                    final boolean eof = this.zzRefill();
                    zzCurrentPosL = this.zzCurrentPos;
                    zzMarkedPosL = this.zzMarkedPos;
                    zzBufferL = this.zzBuffer;
                    zzEndReadL = this.zzEndRead;
                    if (eof) {
                        zzInput = -1;
                        break;
                    }
                    zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL, zzEndReadL);
                    zzCurrentPosL += Character.charCount(zzInput);
                }
                final int zzNext = zzTransL[zzRowMapL[this.zzState] + zzCMapL[zzInput]];
                if (zzNext == -1) {
                    break;
                }
                this.zzState = zzNext;
                zzAttributes = zzAttrL[this.zzState];
                if ((zzAttributes & 0x1) != 0x1) {
                    continue;
                }
                zzAction = this.zzState;
                zzMarkedPosL = zzCurrentPosL;
                if ((zzAttributes & 0x8) == 0x8) {
                    break;
                }
            }
            this.zzMarkedPos = zzMarkedPosL;
            switch ((zzAction < 0) ? zzAction : WikipediaTokenizerImpl.ZZ_ACTION[zzAction]) {
                case 1: {
                    this.numWikiTokensSeen = 0;
                    this.positionInc = 1;
                    continue;
                }
                case 47: {
                    continue;
                }
                case 2: {
                    this.positionInc = 1;
                    return 0;
                }
                case 48: {
                    continue;
                }
                case 3: {
                    this.positionInc = 1;
                    return 7;
                }
                case 49: {
                    continue;
                }
                case 4: {
                    this.numWikiTokensSeen = 0;
                    this.positionInc = 1;
                    this.currentTokType = 17;
                    this.yybegin(6);
                    continue;
                }
                case 50: {
                    continue;
                }
                case 5: {
                    this.positionInc = 1;
                    continue;
                }
                case 51: {
                    continue;
                }
                case 6: {
                    this.yybegin(2);
                    ++this.numWikiTokensSeen;
                    return this.currentTokType;
                }
                case 52: {
                    continue;
                }
                case 7: {
                    this.yybegin(4);
                    ++this.numWikiTokensSeen;
                    return this.currentTokType;
                }
                case 53: {
                    continue;
                }
                case 8: {
                    continue;
                }
                case 54: {
                    continue;
                }
                case 9: {
                    if (this.numLinkToks == 0) {
                        this.positionInc = 0;
                    }
                    else {
                        this.positionInc = 1;
                    }
                    ++this.numWikiTokensSeen;
                    this.currentTokType = 9;
                    this.yybegin(6);
                    ++this.numLinkToks;
                    return this.currentTokType;
                }
                case 55: {
                    continue;
                }
                case 10: {
                    this.numLinkToks = 0;
                    this.yybegin(this.positionInc = 0);
                    continue;
                }
                case 56: {
                    continue;
                }
                case 11: {
                    this.currentTokType = 12;
                    this.yybegin(10);
                    continue;
                }
                case 57: {
                    continue;
                }
                case 12: {
                    this.currentTokType = 13;
                    ++this.numWikiTokensSeen;
                    this.yybegin(18);
                    return this.currentTokType;
                }
                case 58: {
                    continue;
                }
                case 13: {
                    this.currentTokType = 9;
                    this.numWikiTokensSeen = 0;
                    this.yybegin(6);
                    continue;
                }
                case 59: {
                    continue;
                }
                case 14: {
                    this.yybegin(18);
                    ++this.numWikiTokensSeen;
                    return this.currentTokType;
                }
                case 60: {
                    continue;
                }
                case 15: {
                    this.currentTokType = 16;
                    this.numWikiTokensSeen = 0;
                    this.yybegin(18);
                    continue;
                }
                case 61: {
                    continue;
                }
                case 16: {
                    this.currentTokType = 15;
                    this.yybegin(14);
                    ++this.numWikiTokensSeen;
                    return this.currentTokType;
                }
                case 62: {
                    continue;
                }
                case 17: {
                    this.yybegin(16);
                    this.numWikiTokensSeen = 0;
                    return this.currentTokType;
                }
                case 63: {
                    continue;
                }
                case 18: {
                    continue;
                }
                case 64: {
                    continue;
                }
                case 19: {
                    this.yybegin(18);
                    ++this.numWikiTokensSeen;
                    return this.currentTokType;
                }
                case 65: {
                    continue;
                }
                case 20: {
                    this.numBalanced = 0;
                    this.numWikiTokensSeen = 0;
                    this.currentTokType = 9;
                    this.yybegin(6);
                    continue;
                }
                case 66: {
                    continue;
                }
                case 21: {
                    this.yybegin(18);
                    return this.currentTokType;
                }
                case 67: {
                    continue;
                }
                case 22: {
                    this.numWikiTokensSeen = 0;
                    this.positionInc = 1;
                    if (this.numBalanced == 0) {
                        ++this.numBalanced;
                        this.yybegin(8);
                        continue;
                    }
                    this.numBalanced = 0;
                    continue;
                }
                case 68: {
                    continue;
                }
                case 23: {
                    this.numWikiTokensSeen = 0;
                    this.positionInc = 1;
                    this.yybegin(14);
                    continue;
                }
                case 69: {
                    continue;
                }
                case 24: {
                    this.numWikiTokensSeen = 0;
                    this.positionInc = 1;
                    this.currentTokType = 8;
                    this.yybegin(4);
                    continue;
                }
                case 70: {
                    continue;
                }
                case 25: {
                    this.numWikiTokensSeen = 0;
                    this.positionInc = 1;
                    this.currentTokType = 10;
                    this.yybegin(16);
                    continue;
                }
                case 71: {
                    continue;
                }
                case 26: {
                    this.yybegin(0);
                    continue;
                }
                case 72: {
                    continue;
                }
                case 27: {
                    this.yybegin(this.numLinkToks = 0);
                    continue;
                }
                case 73: {
                    continue;
                }
                case 28: {
                    this.currentTokType = 8;
                    this.numWikiTokensSeen = 0;
                    this.yybegin(4);
                    continue;
                }
                case 74: {
                    continue;
                }
                case 29: {
                    this.currentTokType = 8;
                    this.numWikiTokensSeen = 0;
                    this.yybegin(4);
                    continue;
                }
                case 75: {
                    continue;
                }
                case 30: {
                    this.yybegin(0);
                    continue;
                }
                case 76: {
                    continue;
                }
                case 31: {
                    this.numBalanced = 0;
                    this.yybegin(this.currentTokType = 0);
                    continue;
                }
                case 77: {
                    continue;
                }
                case 32: {
                    this.numBalanced = 0;
                    this.numWikiTokensSeen = 0;
                    this.currentTokType = 8;
                    this.yybegin(4);
                    continue;
                }
                case 78: {
                    continue;
                }
                case 33: {
                    return this.positionInc = 1;
                }
                case 79: {
                    continue;
                }
                case 34: {
                    this.positionInc = 1;
                    return 5;
                }
                case 80: {
                    continue;
                }
                case 35: {
                    this.positionInc = 1;
                    return 6;
                }
                case 81: {
                    continue;
                }
                case 36: {
                    this.positionInc = 1;
                    return 3;
                }
                case 82: {
                    continue;
                }
                case 37: {
                    this.currentTokType = 14;
                    this.yybegin(12);
                    continue;
                }
                case 83: {
                    continue;
                }
                case 38: {
                    this.numBalanced = 0;
                    this.yybegin(this.currentTokType = 0);
                    continue;
                }
                case 84: {
                    continue;
                }
                case 39: {
                    this.numBalanced = 0;
                    this.yybegin(this.currentTokType = 0);
                    continue;
                }
                case 85: {
                    continue;
                }
                case 40: {
                    this.positionInc = 1;
                    return 2;
                }
                case 86: {
                    continue;
                }
                case 41: {
                    this.positionInc = 1;
                    return 4;
                }
                case 87: {
                    continue;
                }
                case 42: {
                    this.numBalanced = 0;
                    this.yybegin(this.currentTokType = 0);
                    continue;
                }
                case 88: {
                    continue;
                }
                case 43: {
                    this.positionInc = 1;
                    ++this.numWikiTokensSeen;
                    this.yybegin(6);
                    return this.currentTokType;
                }
                case 89: {
                    continue;
                }
                case 44: {
                    this.numWikiTokensSeen = 0;
                    this.positionInc = 1;
                    this.currentTokType = 11;
                    this.yybegin(2);
                    continue;
                }
                case 90: {
                    continue;
                }
                case 45: {
                    this.currentTokType = 11;
                    this.numWikiTokensSeen = 0;
                    this.yybegin(2);
                    continue;
                }
                case 91: {
                    continue;
                }
                case 46: {
                    this.numBalanced = 0;
                    this.numWikiTokensSeen = 0;
                    this.currentTokType = 11;
                    this.yybegin(2);
                    continue;
                }
                case 92: {
                    continue;
                }
                default: {
                    if (zzInput == -1 && this.zzStartRead == this.zzCurrentPos) {
                        this.zzAtEOF = true;
                        return -1;
                    }
                    this.zzScanError(1);
                    continue;
                }
            }
        }
    }
    
    static {
        ZZ_LEXSTATE = new int[] { 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9 };
        ZZ_CMAP = zzUnpackCMap("\t\u0000\u0001\u0014\u0001\u0013\u0001\u0000\u0001\u0014\u0001\u0012\u0012\u0000\u0001\u0014\u0001\u0000\u0001\n\u0001+\u0002\u0000\u0001\u0003\u0001\u0001\u0004\u0000\u0001\f\u0001\u0005\u0001\u0002\u0001\b\n\u000e\u0001\u0017\u0001\u0000\u0001\u0007\u0001\t\u0001\u000b\u0001+\u0001\u0004\u0002\r\u0001\u0018\u0005\r\u0001!\u0011\r\u0001\u0015\u0001\u0000\u0001\u0016\u0001\u0000\u0001\u0006\u0001\u0000\u0001\u0019\u0001#\u0002\r\u0001\u001b\u0001 \u0001\u001c\u0001(\u0001!\u0004\r\u0001\"\u0001\u001d\u0001)\u0001\r\u0001\u001e\u0001*\u0001\u001a\u0003\r\u0001$\u0001\u001f\u0001\r\u0001%\u0001'\u0001&B\u0000\u0017\r\u0001\u0000\u001f\r\u0001\u0000\u0568\r\n\u000f\u0086\r\n\u000f\u026c\r\n\u000fv\r\n\u000fv\r\n\u000fv\r\n\u000fv\r\n\u000fw\r\t\u000fv\r\n\u000fv\r\n\u000fv\r\n\u000f\u00e0\r\n\u000fv\r\n\u000f\u0166\r\n\u000f¶\r\u0100\r\u0e00\r\u1040\u0000\u0150\u0011`\u0000\u0010\u0011\u0100\u0000\u0080\u0011\u0080\u0000\u19c0\u0011@\u0000\u5200\u0011\u0c00\u0000\u2bb0\u0010\u2150\u0000\u0200\u0011\u0465\u0000;\u0011=\r\uffff\u0000\uffff\u0000\uffff\u0000\uffff\u0000\uffff\u0000\uffff\u0000\uffff\u0000\uffff\u0000\uffff\u0000\uffff\u0000\uffff\u0000\uffff\u0000\uffff\u0000\uffff\u0000\uffff\u0000\uffff\u00003\u0000");
        ZZ_ACTION = zzUnpackAction();
        ZZ_ROWMAP = zzUnpackRowMap();
        ZZ_TRANS = zzUnpackTrans();
        ZZ_ERROR_MSG = new String[] { "Unkown internal scanner error", "Error: could not match input", "Error: pushback value was too large" };
        ZZ_ATTRIBUTE = zzUnpackAttribute();
        TOKEN_TYPES = WikipediaTokenizer.TOKEN_TYPES;
    }
}
