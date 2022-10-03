package sun.nio.cs;

import sun.util.PreHashedMap;
import java.nio.charset.Charset;
import java.util.Map;

public class StandardCharsets extends FastCharsetProvider
{
    static final String[] aliases_US_ASCII;
    static final String[] aliases_UTF_8;
    static final String[] aliases_CESU_8;
    static final String[] aliases_UTF_16;
    static final String[] aliases_UTF_16BE;
    static final String[] aliases_UTF_16LE;
    static final String[] aliases_UTF_16LE_BOM;
    static final String[] aliases_UTF_32;
    static final String[] aliases_UTF_32LE;
    static final String[] aliases_UTF_32BE;
    static final String[] aliases_UTF_32LE_BOM;
    static final String[] aliases_UTF_32BE_BOM;
    static final String[] aliases_ISO_8859_1;
    static final String[] aliases_ISO_8859_2;
    static final String[] aliases_ISO_8859_4;
    static final String[] aliases_ISO_8859_5;
    static final String[] aliases_ISO_8859_7;
    static final String[] aliases_ISO_8859_9;
    static final String[] aliases_ISO_8859_13;
    static final String[] aliases_ISO_8859_15;
    static final String[] aliases_KOI8_R;
    static final String[] aliases_KOI8_U;
    static final String[] aliases_MS1250;
    static final String[] aliases_MS1251;
    static final String[] aliases_MS1252;
    static final String[] aliases_MS1253;
    static final String[] aliases_MS1254;
    static final String[] aliases_MS1257;
    static final String[] aliases_IBM437;
    static final String[] aliases_IBM737;
    static final String[] aliases_IBM775;
    static final String[] aliases_IBM850;
    static final String[] aliases_IBM852;
    static final String[] aliases_IBM855;
    static final String[] aliases_IBM857;
    static final String[] aliases_IBM858;
    static final String[] aliases_IBM862;
    static final String[] aliases_IBM866;
    static final String[] aliases_IBM874;
    
    public StandardCharsets() {
        super("sun.nio.cs", (Map<String, String>)new Aliases(), (Map<String, String>)new Classes(), (Map<String, Charset>)new Cache());
    }
    
    static {
        aliases_US_ASCII = new String[] { "iso-ir-6", "ANSI_X3.4-1986", "ISO_646.irv:1991", "ASCII", "ISO646-US", "us", "IBM367", "cp367", "csASCII", "default", "646", "iso_646.irv:1983", "ANSI_X3.4-1968", "ascii7" };
        aliases_UTF_8 = new String[] { "UTF8", "unicode-1-1-utf-8" };
        aliases_CESU_8 = new String[] { "CESU8", "csCESU-8" };
        aliases_UTF_16 = new String[] { "UTF_16", "utf16", "unicode", "UnicodeBig" };
        aliases_UTF_16BE = new String[] { "UTF_16BE", "ISO-10646-UCS-2", "X-UTF-16BE", "UnicodeBigUnmarked" };
        aliases_UTF_16LE = new String[] { "UTF_16LE", "X-UTF-16LE", "UnicodeLittleUnmarked" };
        aliases_UTF_16LE_BOM = new String[] { "UnicodeLittle" };
        aliases_UTF_32 = new String[] { "UTF_32", "UTF32" };
        aliases_UTF_32LE = new String[] { "UTF_32LE", "X-UTF-32LE" };
        aliases_UTF_32BE = new String[] { "UTF_32BE", "X-UTF-32BE" };
        aliases_UTF_32LE_BOM = new String[] { "UTF_32LE_BOM", "UTF-32LE-BOM" };
        aliases_UTF_32BE_BOM = new String[] { "UTF_32BE_BOM", "UTF-32BE-BOM" };
        aliases_ISO_8859_1 = new String[] { "iso-ir-100", "ISO_8859-1", "latin1", "l1", "IBM819", "cp819", "csISOLatin1", "819", "IBM-819", "ISO8859_1", "ISO_8859-1:1987", "ISO_8859_1", "8859_1", "ISO8859-1" };
        aliases_ISO_8859_2 = new String[] { "iso8859_2", "8859_2", "iso-ir-101", "ISO_8859-2", "ISO_8859-2:1987", "ISO8859-2", "latin2", "l2", "ibm912", "ibm-912", "cp912", "912", "csISOLatin2" };
        aliases_ISO_8859_4 = new String[] { "iso8859_4", "iso8859-4", "8859_4", "iso-ir-110", "ISO_8859-4", "ISO_8859-4:1988", "latin4", "l4", "ibm914", "ibm-914", "cp914", "914", "csISOLatin4" };
        aliases_ISO_8859_5 = new String[] { "iso8859_5", "8859_5", "iso-ir-144", "ISO_8859-5", "ISO_8859-5:1988", "ISO8859-5", "cyrillic", "ibm915", "ibm-915", "cp915", "915", "csISOLatinCyrillic" };
        aliases_ISO_8859_7 = new String[] { "iso8859_7", "8859_7", "iso-ir-126", "ISO_8859-7", "ISO_8859-7:1987", "ELOT_928", "ECMA-118", "greek", "greek8", "csISOLatinGreek", "sun_eu_greek", "ibm813", "ibm-813", "813", "cp813", "iso8859-7" };
        aliases_ISO_8859_9 = new String[] { "iso8859_9", "8859_9", "iso-ir-148", "ISO_8859-9", "ISO_8859-9:1989", "ISO8859-9", "latin5", "l5", "ibm920", "ibm-920", "920", "cp920", "csISOLatin5" };
        aliases_ISO_8859_13 = new String[] { "iso8859_13", "8859_13", "iso_8859-13", "ISO8859-13" };
        aliases_ISO_8859_15 = new String[] { "ISO_8859-15", "8859_15", "ISO-8859-15", "ISO8859_15", "ISO8859-15", "IBM923", "IBM-923", "cp923", "923", "LATIN0", "LATIN9", "L9", "csISOlatin0", "csISOlatin9", "ISO8859_15_FDIS" };
        aliases_KOI8_R = new String[] { "koi8_r", "koi8", "cskoi8r" };
        aliases_KOI8_U = new String[] { "koi8_u" };
        aliases_MS1250 = new String[] { "cp1250", "cp5346" };
        aliases_MS1251 = new String[] { "cp1251", "cp5347", "ansi-1251" };
        aliases_MS1252 = new String[] { "cp1252", "cp5348" };
        aliases_MS1253 = new String[] { "cp1253", "cp5349" };
        aliases_MS1254 = new String[] { "cp1254", "cp5350" };
        aliases_MS1257 = new String[] { "cp1257", "cp5353" };
        aliases_IBM437 = new String[] { "cp437", "ibm437", "ibm-437", "437", "cspc8codepage437", "windows-437" };
        aliases_IBM737 = new String[] { "cp737", "ibm737", "ibm-737", "737" };
        aliases_IBM775 = new String[] { "cp775", "ibm775", "ibm-775", "775" };
        aliases_IBM850 = new String[] { "cp850", "ibm-850", "ibm850", "850", "cspc850multilingual" };
        aliases_IBM852 = new String[] { "cp852", "ibm852", "ibm-852", "852", "csPCp852" };
        aliases_IBM855 = new String[] { "cp855", "ibm-855", "ibm855", "855", "cspcp855" };
        aliases_IBM857 = new String[] { "cp857", "ibm857", "ibm-857", "857", "csIBM857" };
        aliases_IBM858 = new String[] { "cp858", "ccsid00858", "cp00858", "858", "PC-Multilingual-850+euro" };
        aliases_IBM862 = new String[] { "cp862", "ibm862", "ibm-862", "862", "csIBM862", "cspc862latinhebrew" };
        aliases_IBM866 = new String[] { "cp866", "ibm866", "ibm-866", "866", "csIBM866" };
        aliases_IBM874 = new String[] { "cp874", "ibm874", "ibm-874", "874" };
    }
    
    private static final class Aliases extends PreHashedMap<String>
    {
        private static final int ROWS = 1024;
        private static final int SIZE = 211;
        private static final int SHIFT = 0;
        private static final int MASK = 1023;
        
        private Aliases() {
            super(1024, 211, 0, 1023);
        }
        
        @Override
        protected void init(final Object[] array) {
            array[1] = new Object[] { "csisolatin0", "iso-8859-15" };
            array[2] = new Object[] { "csisolatin1", "iso-8859-1" };
            array[3] = new Object[] { "csisolatin2", "iso-8859-2" };
            array[5] = new Object[] { "csisolatin4", "iso-8859-4" };
            array[6] = new Object[] { "csisolatin5", "iso-8859-9" };
            array[10] = new Object[] { "csisolatin9", "iso-8859-15" };
            array[19] = new Object[] { "unicodelittle", "x-utf-16le-bom" };
            array[24] = new Object[] { "iso646-us", "us-ascii" };
            array[25] = new Object[] { "iso_8859-7:1987", "iso-8859-7" };
            array[26] = new Object[] { "912", "iso-8859-2" };
            array[28] = new Object[] { "914", "iso-8859-4" };
            array[29] = new Object[] { "915", "iso-8859-5" };
            array[55] = new Object[] { "920", "iso-8859-9" };
            array[58] = new Object[] { "923", "iso-8859-15" };
            array[86] = new Object[] { "csisolatincyrillic", "iso-8859-5", { "8859_1", "iso-8859-1" } };
            array[87] = new Object[] { "8859_2", "iso-8859-2" };
            array[89] = new Object[] { "8859_4", "iso-8859-4" };
            array[90] = new Object[] { "813", "iso-8859-7", { "8859_5", "iso-8859-5" } };
            array[92] = new Object[] { "8859_7", "iso-8859-7" };
            array[94] = new Object[] { "8859_9", "iso-8859-9" };
            array[95] = new Object[] { "iso_8859-1:1987", "iso-8859-1" };
            array[96] = new Object[] { "819", "iso-8859-1" };
            array[106] = new Object[] { "unicode-1-1-utf-8", "utf-8" };
            array[121] = new Object[] { "x-utf-16le", "utf-16le" };
            array[125] = new Object[] { "ecma-118", "iso-8859-7" };
            array[134] = new Object[] { "koi8_r", "koi8-r" };
            array[137] = new Object[] { "koi8_u", "koi8-u" };
            array[141] = new Object[] { "cp912", "iso-8859-2" };
            array[143] = new Object[] { "cp914", "iso-8859-4" };
            array[144] = new Object[] { "cp915", "iso-8859-5" };
            array[170] = new Object[] { "cp920", "iso-8859-9" };
            array[173] = new Object[] { "cp923", "iso-8859-15" };
            array[177] = new Object[] { "utf_32le_bom", "x-utf-32le-bom" };
            array[192] = new Object[] { "utf_16be", "utf-16be" };
            array[199] = new Object[] { "cspc8codepage437", "ibm437", { "ansi-1251", "windows-1251" } };
            array[205] = new Object[] { "cp813", "iso-8859-7" };
            array[211] = new Object[] { "850", "ibm850", { "cp819", "iso-8859-1" } };
            array[213] = new Object[] { "852", "ibm852" };
            array[216] = new Object[] { "855", "ibm855" };
            array[218] = new Object[] { "857", "ibm857", { "iso-ir-6", "us-ascii" } };
            array[219] = new Object[] { "858", "ibm00858", { "737", "x-ibm737" } };
            array[225] = new Object[] { "csascii", "us-ascii" };
            array[244] = new Object[] { "862", "ibm862" };
            array[248] = new Object[] { "866", "ibm866" };
            array[253] = new Object[] { "x-utf-32be", "utf-32be" };
            array[254] = new Object[] { "iso_8859-2:1987", "iso-8859-2" };
            array[259] = new Object[] { "unicodebig", "utf-16" };
            array[269] = new Object[] { "iso8859_15_fdis", "iso-8859-15" };
            array[277] = new Object[] { "874", "x-ibm874" };
            array[280] = new Object[] { "unicodelittleunmarked", "utf-16le" };
            array[283] = new Object[] { "iso8859_1", "iso-8859-1" };
            array[284] = new Object[] { "iso8859_2", "iso-8859-2" };
            array[286] = new Object[] { "iso8859_4", "iso-8859-4" };
            array[287] = new Object[] { "iso8859_5", "iso-8859-5" };
            array[289] = new Object[] { "iso8859_7", "iso-8859-7" };
            array[291] = new Object[] { "iso8859_9", "iso-8859-9" };
            array[294] = new Object[] { "ibm912", "iso-8859-2" };
            array[296] = new Object[] { "ibm914", "iso-8859-4" };
            array[297] = new Object[] { "ibm915", "iso-8859-5" };
            array[305] = new Object[] { "iso_8859-13", "iso-8859-13" };
            array[307] = new Object[] { "iso_8859-15", "iso-8859-15" };
            array[312] = new Object[] { "greek8", "iso-8859-7", { "646", "us-ascii" } };
            array[321] = new Object[] { "ibm-912", "iso-8859-2" };
            array[323] = new Object[] { "ibm920", "iso-8859-9", { "ibm-914", "iso-8859-4" } };
            array[324] = new Object[] { "ibm-915", "iso-8859-5" };
            array[325] = new Object[] { "l1", "iso-8859-1" };
            array[326] = new Object[] { "cp850", "ibm850", { "ibm923", "iso-8859-15", { "l2", "iso-8859-2" } } };
            array[327] = new Object[] { "cyrillic", "iso-8859-5" };
            array[328] = new Object[] { "cp852", "ibm852", { "l4", "iso-8859-4" } };
            array[329] = new Object[] { "l5", "iso-8859-9" };
            array[331] = new Object[] { "cp855", "ibm855" };
            array[333] = new Object[] { "cp857", "ibm857", { "l9", "iso-8859-15" } };
            array[334] = new Object[] { "cp858", "ibm00858", { "cp737", "x-ibm737" } };
            array[336] = new Object[] { "iso_8859_1", "iso-8859-1" };
            array[339] = new Object[] { "koi8", "koi8-r" };
            array[341] = new Object[] { "775", "ibm775" };
            array[345] = new Object[] { "iso_8859-9:1989", "iso-8859-9" };
            array[350] = new Object[] { "ibm-920", "iso-8859-9" };
            array[353] = new Object[] { "ibm-923", "iso-8859-15" };
            array[358] = new Object[] { "ibm813", "iso-8859-7" };
            array[359] = new Object[] { "cp862", "ibm862" };
            array[363] = new Object[] { "cp866", "ibm866" };
            array[364] = new Object[] { "ibm819", "iso-8859-1" };
            array[378] = new Object[] { "ansi_x3.4-1968", "us-ascii" };
            array[385] = new Object[] { "ibm-813", "iso-8859-7" };
            array[391] = new Object[] { "ibm-819", "iso-8859-1" };
            array[392] = new Object[] { "cp874", "x-ibm874" };
            array[405] = new Object[] { "iso-ir-100", "iso-8859-1" };
            array[406] = new Object[] { "iso-ir-101", "iso-8859-2" };
            array[408] = new Object[] { "437", "ibm437" };
            array[421] = new Object[] { "iso-8859-15", "iso-8859-15" };
            array[428] = new Object[] { "latin0", "iso-8859-15" };
            array[429] = new Object[] { "latin1", "iso-8859-1" };
            array[430] = new Object[] { "latin2", "iso-8859-2" };
            array[432] = new Object[] { "latin4", "iso-8859-4" };
            array[433] = new Object[] { "latin5", "iso-8859-9" };
            array[436] = new Object[] { "iso-ir-110", "iso-8859-4" };
            array[437] = new Object[] { "latin9", "iso-8859-15" };
            array[438] = new Object[] { "ansi_x3.4-1986", "us-ascii" };
            array[443] = new Object[] { "utf-32be-bom", "x-utf-32be-bom" };
            array[456] = new Object[] { "cp775", "ibm775" };
            array[473] = new Object[] { "iso-ir-126", "iso-8859-7" };
            array[479] = new Object[] { "ibm850", "ibm850" };
            array[481] = new Object[] { "ibm852", "ibm852" };
            array[484] = new Object[] { "ibm855", "ibm855" };
            array[486] = new Object[] { "ibm857", "ibm857" };
            array[487] = new Object[] { "ibm737", "x-ibm737" };
            array[502] = new Object[] { "utf_16le", "utf-16le" };
            array[506] = new Object[] { "ibm-850", "ibm850" };
            array[508] = new Object[] { "ibm-852", "ibm852" };
            array[511] = new Object[] { "ibm-855", "ibm855" };
            array[512] = new Object[] { "ibm862", "ibm862" };
            array[513] = new Object[] { "ibm-857", "ibm857" };
            array[514] = new Object[] { "ibm-737", "x-ibm737" };
            array[516] = new Object[] { "ibm866", "ibm866" };
            array[520] = new Object[] { "unicodebigunmarked", "utf-16be" };
            array[523] = new Object[] { "cp437", "ibm437" };
            array[524] = new Object[] { "utf16", "utf-16" };
            array[533] = new Object[] { "iso-ir-144", "iso-8859-5" };
            array[537] = new Object[] { "iso-ir-148", "iso-8859-9" };
            array[539] = new Object[] { "ibm-862", "ibm862" };
            array[543] = new Object[] { "ibm-866", "ibm866" };
            array[545] = new Object[] { "ibm874", "x-ibm874" };
            array[563] = new Object[] { "x-utf-32le", "utf-32le" };
            array[572] = new Object[] { "ibm-874", "x-ibm874" };
            array[573] = new Object[] { "iso_8859-4:1988", "iso-8859-4" };
            array[577] = new Object[] { "default", "us-ascii" };
            array[582] = new Object[] { "utf32", "utf-32" };
            array[583] = new Object[] { "pc-multilingual-850+euro", "ibm00858" };
            array[588] = new Object[] { "elot_928", "iso-8859-7" };
            array[593] = new Object[] { "csisolatingreek", "iso-8859-7" };
            array[598] = new Object[] { "csibm857", "ibm857" };
            array[609] = new Object[] { "ibm775", "ibm775" };
            array[617] = new Object[] { "cp1250", "windows-1250" };
            array[618] = new Object[] { "cp1251", "windows-1251" };
            array[619] = new Object[] { "cp1252", "windows-1252" };
            array[620] = new Object[] { "cp1253", "windows-1253" };
            array[621] = new Object[] { "cp1254", "windows-1254" };
            array[624] = new Object[] { "csibm862", "ibm862", { "cp1257", "windows-1257" } };
            array[628] = new Object[] { "csibm866", "ibm866", { "cesu8", "cesu-8" } };
            array[632] = new Object[] { "iso8859_13", "iso-8859-13" };
            array[634] = new Object[] { "iso8859_15", "iso-8859-15", { "utf_32be", "utf-32be" } };
            array[635] = new Object[] { "utf_32be_bom", "x-utf-32be-bom" };
            array[636] = new Object[] { "ibm-775", "ibm775" };
            array[654] = new Object[] { "cp00858", "ibm00858" };
            array[669] = new Object[] { "8859_13", "iso-8859-13" };
            array[670] = new Object[] { "us", "us-ascii" };
            array[671] = new Object[] { "8859_15", "iso-8859-15" };
            array[676] = new Object[] { "ibm437", "ibm437" };
            array[679] = new Object[] { "cp367", "us-ascii" };
            array[686] = new Object[] { "iso-10646-ucs-2", "utf-16be" };
            array[703] = new Object[] { "ibm-437", "ibm437" };
            array[710] = new Object[] { "iso8859-13", "iso-8859-13" };
            array[712] = new Object[] { "iso8859-15", "iso-8859-15" };
            array[732] = new Object[] { "iso_8859-5:1988", "iso-8859-5" };
            array[733] = new Object[] { "unicode", "utf-16" };
            array[768] = new Object[] { "greek", "iso-8859-7" };
            array[774] = new Object[] { "ascii7", "us-ascii" };
            array[781] = new Object[] { "iso8859-1", "iso-8859-1" };
            array[782] = new Object[] { "iso8859-2", "iso-8859-2" };
            array[783] = new Object[] { "cskoi8r", "koi8-r" };
            array[784] = new Object[] { "iso8859-4", "iso-8859-4" };
            array[785] = new Object[] { "iso8859-5", "iso-8859-5" };
            array[787] = new Object[] { "iso8859-7", "iso-8859-7" };
            array[789] = new Object[] { "iso8859-9", "iso-8859-9" };
            array[813] = new Object[] { "ccsid00858", "ibm00858" };
            array[818] = new Object[] { "cspc862latinhebrew", "ibm862" };
            array[832] = new Object[] { "ibm367", "us-ascii" };
            array[834] = new Object[] { "iso_8859-1", "iso-8859-1" };
            array[835] = new Object[] { "iso_8859-2", "iso-8859-2", { "x-utf-16be", "utf-16be" } };
            array[836] = new Object[] { "sun_eu_greek", "iso-8859-7" };
            array[837] = new Object[] { "iso_8859-4", "iso-8859-4" };
            array[838] = new Object[] { "iso_8859-5", "iso-8859-5" };
            array[840] = new Object[] { "cspcp852", "ibm852", { "iso_8859-7", "iso-8859-7" } };
            array[842] = new Object[] { "iso_8859-9", "iso-8859-9" };
            array[843] = new Object[] { "cspcp855", "ibm855" };
            array[846] = new Object[] { "windows-437", "ibm437" };
            array[849] = new Object[] { "ascii", "us-ascii" };
            array[863] = new Object[] { "cscesu-8", "cesu-8" };
            array[881] = new Object[] { "utf8", "utf-8" };
            array[896] = new Object[] { "iso_646.irv:1983", "us-ascii" };
            array[909] = new Object[] { "cp5346", "windows-1250" };
            array[910] = new Object[] { "cp5347", "windows-1251" };
            array[911] = new Object[] { "cp5348", "windows-1252" };
            array[912] = new Object[] { "cp5349", "windows-1253" };
            array[925] = new Object[] { "iso_646.irv:1991", "us-ascii" };
            array[934] = new Object[] { "cp5350", "windows-1254" };
            array[937] = new Object[] { "cp5353", "windows-1257" };
            array[944] = new Object[] { "utf_32le", "utf-32le" };
            array[957] = new Object[] { "utf_16", "utf-16" };
            array[993] = new Object[] { "cspc850multilingual", "ibm850" };
            array[1009] = new Object[] { "utf-32le-bom", "x-utf-32le-bom" };
            array[1015] = new Object[] { "utf_32", "utf-32" };
        }
    }
    
    private static final class Classes extends PreHashedMap<String>
    {
        private static final int ROWS = 32;
        private static final int SIZE = 39;
        private static final int SHIFT = 1;
        private static final int MASK = 31;
        
        private Classes() {
            super(32, 39, 1, 31);
        }
        
        @Override
        protected void init(final Object[] array) {
            array[0] = new Object[] { "ibm862", "IBM862" };
            array[2] = new Object[] { "ibm866", "IBM866", { "utf-32", "UTF_32", { "utf-16le", "UTF_16LE" } } };
            array[3] = new Object[] { "windows-1251", "MS1251", { "windows-1250", "MS1250" } };
            array[4] = new Object[] { "windows-1253", "MS1253", { "windows-1252", "MS1252", { "utf-32be", "UTF_32BE" } } };
            array[5] = new Object[] { "windows-1254", "MS1254", { "utf-16", "UTF_16" } };
            array[6] = new Object[] { "windows-1257", "MS1257" };
            array[7] = new Object[] { "utf-16be", "UTF_16BE" };
            array[8] = new Object[] { "iso-8859-2", "ISO_8859_2", { "iso-8859-1", "ISO_8859_1" } };
            array[9] = new Object[] { "iso-8859-4", "ISO_8859_4", { "utf-8", "UTF_8" } };
            array[10] = new Object[] { "iso-8859-5", "ISO_8859_5" };
            array[11] = new Object[] { "x-ibm874", "IBM874", { "iso-8859-7", "ISO_8859_7" } };
            array[12] = new Object[] { "iso-8859-9", "ISO_8859_9" };
            array[14] = new Object[] { "x-ibm737", "IBM737" };
            array[15] = new Object[] { "ibm850", "IBM850" };
            array[16] = new Object[] { "ibm852", "IBM852", { "ibm775", "IBM775" } };
            array[17] = new Object[] { "iso-8859-13", "ISO_8859_13", { "us-ascii", "US_ASCII" } };
            array[18] = new Object[] { "ibm855", "IBM855", { "ibm437", "IBM437", { "iso-8859-15", "ISO_8859_15" } } };
            array[19] = new Object[] { "ibm00858", "IBM858", { "ibm857", "IBM857", { "x-utf-32le-bom", "UTF_32LE_BOM" } } };
            array[22] = new Object[] { "x-utf-16le-bom", "UTF_16LE_BOM" };
            array[23] = new Object[] { "cesu-8", "CESU_8" };
            array[24] = new Object[] { "x-utf-32be-bom", "UTF_32BE_BOM" };
            array[28] = new Object[] { "koi8-r", "KOI8_R" };
            array[29] = new Object[] { "koi8-u", "KOI8_U" };
            array[31] = new Object[] { "utf-32le", "UTF_32LE" };
        }
    }
    
    private static final class Cache extends PreHashedMap<Charset>
    {
        private static final int ROWS = 32;
        private static final int SIZE = 39;
        private static final int SHIFT = 1;
        private static final int MASK = 31;
        
        private Cache() {
            super(32, 39, 1, 31);
        }
        
        @Override
        protected void init(final Object[] array) {
            array[0] = new Object[] { "ibm862", null };
            array[2] = new Object[] { "ibm866", null, { "utf-32", null, { "utf-16le", null } } };
            array[3] = new Object[] { "windows-1251", null, { "windows-1250", null } };
            array[4] = new Object[] { "windows-1253", null, { "windows-1252", null, { "utf-32be", null } } };
            array[5] = new Object[] { "windows-1254", null, { "utf-16", null } };
            array[6] = new Object[] { "windows-1257", null };
            array[7] = new Object[] { "utf-16be", null };
            array[8] = new Object[] { "iso-8859-2", null, { "iso-8859-1", null } };
            array[9] = new Object[] { "iso-8859-4", null, { "utf-8", null } };
            array[10] = new Object[] { "iso-8859-5", null };
            array[11] = new Object[] { "x-ibm874", null, { "iso-8859-7", null } };
            array[12] = new Object[] { "iso-8859-9", null };
            array[14] = new Object[] { "x-ibm737", null };
            array[15] = new Object[] { "ibm850", null };
            array[16] = new Object[] { "ibm852", null, { "ibm775", null } };
            array[17] = new Object[] { "iso-8859-13", null, { "us-ascii", null } };
            array[18] = new Object[] { "ibm855", null, { "ibm437", null, { "iso-8859-15", null } } };
            array[19] = new Object[] { "ibm00858", null, { "ibm857", null, { "x-utf-32le-bom", null } } };
            array[22] = new Object[] { "x-utf-16le-bom", null };
            array[23] = new Object[] { "cesu-8", null };
            array[24] = new Object[] { "x-utf-32be-bom", null };
            array[28] = new Object[] { "koi8-r", null };
            array[29] = new Object[] { "koi8-u", null };
            array[31] = new Object[] { "utf-32le", null };
        }
    }
}
