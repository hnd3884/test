package org.apache.poi.hssf.record;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.hssf.record.common.UnicodeString;
import org.apache.poi.util.IntMapper;
import org.apache.poi.util.POILogger;

class SSTDeserializer
{
    private static POILogger logger;
    private IntMapper<UnicodeString> strings;
    
    public SSTDeserializer(final IntMapper<UnicodeString> strings) {
        this.strings = strings;
    }
    
    public void manufactureStrings(final int stringCount, final RecordInputStream in) {
        for (int i = 0; i < stringCount; ++i) {
            UnicodeString str;
            if (in.available() == 0 && !in.hasNextRecord()) {
                SSTDeserializer.logger.log(7, "Ran out of data before creating all the strings! String at index " + i + "");
                str = new UnicodeString("");
            }
            else {
                str = new UnicodeString(in);
            }
            addToStringTable(this.strings, str);
        }
    }
    
    public static void addToStringTable(final IntMapper<UnicodeString> strings, final UnicodeString string) {
        strings.add(string);
    }
    
    static {
        SSTDeserializer.logger = POILogFactory.getLogger(SSTDeserializer.class);
    }
}
