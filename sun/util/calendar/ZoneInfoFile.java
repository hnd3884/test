package sun.util.calendar;

import java.util.zip.CRC32;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.Locale;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.util.Collections;
import java.io.DataInput;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public final class ZoneInfoFile
{
    private static String versionId;
    private static final Map<String, ZoneInfo> zones;
    private static Map<String, String> aliases;
    private static byte[][] ruleArray;
    private static String[] regions;
    private static int[] indices;
    private static final boolean USE_OLDMAPPING;
    private static String[][] oldMappings;
    private static final long UTC1900 = -2208988800L;
    private static final long UTC2037 = 2145916799L;
    private static final long LDT2037 = 2114380800L;
    private static final long CURRT;
    static final int SECONDS_PER_DAY = 86400;
    static final int DAYS_PER_CYCLE = 146097;
    static final long DAYS_0000_TO_1970 = 719528L;
    private static final int[] toCalendarDOW;
    private static final int[] toSTZTime;
    private static final long OFFSET_MASK = 15L;
    private static final long DST_MASK = 240L;
    private static final int DST_NSHIFT = 4;
    private static final int TRANSITION_NSHIFT = 12;
    private static final int LASTYEAR = 2037;
    
    public static String[] getZoneIds() {
        int n = ZoneInfoFile.regions.length + ZoneInfoFile.oldMappings.length;
        if (!ZoneInfoFile.USE_OLDMAPPING) {
            n += 3;
        }
        final String[] array = Arrays.copyOf(ZoneInfoFile.regions, n);
        int length = ZoneInfoFile.regions.length;
        if (!ZoneInfoFile.USE_OLDMAPPING) {
            array[length++] = "EST";
            array[length++] = "HST";
            array[length++] = "MST";
        }
        for (int i = 0; i < ZoneInfoFile.oldMappings.length; ++i) {
            array[length++] = ZoneInfoFile.oldMappings[i][0];
        }
        return array;
    }
    
    public static String[] getZoneIds(final int n) {
        final ArrayList list = new ArrayList();
        for (final String s : getZoneIds()) {
            if (getZoneInfo(s).getRawOffset() == n) {
                list.add(s);
            }
        }
        final String[] array = (String[])list.toArray(new String[list.size()]);
        Arrays.sort(array);
        return array;
    }
    
    public static ZoneInfo getZoneInfo(final String id) {
        if (id == null) {
            return null;
        }
        ZoneInfo zoneInfo0 = getZoneInfo0(id);
        if (zoneInfo0 != null) {
            zoneInfo0 = (ZoneInfo)zoneInfo0.clone();
            zoneInfo0.setID(id);
        }
        return zoneInfo0;
    }
    
    private static ZoneInfo getZoneInfo0(final String s) {
        try {
            final ZoneInfo zoneInfo = ZoneInfoFile.zones.get(s);
            if (zoneInfo != null) {
                return zoneInfo;
            }
            String s2 = s;
            if (ZoneInfoFile.aliases.containsKey(s)) {
                s2 = ZoneInfoFile.aliases.get(s);
            }
            final int binarySearch = Arrays.binarySearch(ZoneInfoFile.regions, s2);
            if (binarySearch < 0) {
                return null;
            }
            final ZoneInfo zoneInfo2 = getZoneInfo(new DataInputStream(new ByteArrayInputStream(ZoneInfoFile.ruleArray[ZoneInfoFile.indices[binarySearch]])), s2);
            ZoneInfoFile.zones.put(s, zoneInfo2);
            return zoneInfo2;
        }
        catch (final Exception ex) {
            throw new RuntimeException("Invalid binary time-zone data: TZDB:" + s + ", version: " + ZoneInfoFile.versionId, ex);
        }
    }
    
    public static Map<String, String> getAliasMap() {
        return Collections.unmodifiableMap((Map<? extends String, ? extends String>)ZoneInfoFile.aliases);
    }
    
    public static String getVersion() {
        return ZoneInfoFile.versionId;
    }
    
    public static ZoneInfo getCustomTimeZone(final String s, final int n) {
        return new ZoneInfo(toCustomID(n), n);
    }
    
    public static String toCustomID(final int n) {
        int n2 = n / 60000;
        char c;
        if (n2 >= 0) {
            c = '+';
        }
        else {
            c = '-';
            n2 = -n2;
        }
        final int n3 = n2 / 60;
        final int n4 = n2 % 60;
        final char[] array = { 'G', 'M', 'T', c, '0', '0', ':', '0', '0' };
        if (n3 >= 10) {
            final char[] array2 = array;
            final int n5 = 4;
            array2[n5] += (char)(n3 / 10);
        }
        final char[] array3 = array;
        final int n6 = 5;
        array3[n6] += (char)(n3 % 10);
        if (n4 != 0) {
            final char[] array4 = array;
            final int n7 = 7;
            array4[n7] += (char)(n4 / 10);
            final char[] array5 = array;
            final int n8 = 8;
            array5[n8] += (char)(n4 % 10);
        }
        return new String(array);
    }
    
    private ZoneInfoFile() {
    }
    
    private static void addOldMapping() {
        for (final String[] array : ZoneInfoFile.oldMappings) {
            ZoneInfoFile.aliases.put(array[0], array[1]);
        }
        if (ZoneInfoFile.USE_OLDMAPPING) {
            ZoneInfoFile.aliases.put("EST", "America/New_York");
            ZoneInfoFile.aliases.put("MST", "America/Denver");
            ZoneInfoFile.aliases.put("HST", "Pacific/Honolulu");
        }
        else {
            ZoneInfoFile.zones.put("EST", new ZoneInfo("EST", -18000000));
            ZoneInfoFile.zones.put("MST", new ZoneInfo("MST", -25200000));
            ZoneInfoFile.zones.put("HST", new ZoneInfo("HST", -36000000));
        }
    }
    
    public static boolean useOldMapping() {
        return ZoneInfoFile.USE_OLDMAPPING;
    }
    
    private static void load(final DataInputStream dataInputStream) throws ClassNotFoundException, IOException {
        if (dataInputStream.readByte() != 1) {
            throw new StreamCorruptedException("File format not recognised");
        }
        if (!"TZDB".equals(dataInputStream.readUTF())) {
            throw new StreamCorruptedException("File format not recognised");
        }
        final short short1 = dataInputStream.readShort();
        for (short n = 0; n < short1; ++n) {
            ZoneInfoFile.versionId = dataInputStream.readUTF();
        }
        final short short2 = dataInputStream.readShort();
        final String[] array = new String[short2];
        for (short n2 = 0; n2 < short2; ++n2) {
            array[n2] = dataInputStream.readUTF();
        }
        final short short3 = dataInputStream.readShort();
        ZoneInfoFile.ruleArray = new byte[short3][];
        for (short n3 = 0; n3 < short3; ++n3) {
            final byte[] array2 = new byte[dataInputStream.readShort()];
            dataInputStream.readFully(array2);
            ZoneInfoFile.ruleArray[n3] = array2;
        }
        for (short n4 = 0; n4 < short1; ++n4) {
            final short short4 = dataInputStream.readShort();
            ZoneInfoFile.regions = new String[short4];
            ZoneInfoFile.indices = new int[short4];
            for (short n5 = 0; n5 < short4; ++n5) {
                ZoneInfoFile.regions[n5] = array[dataInputStream.readShort()];
                ZoneInfoFile.indices[n5] = dataInputStream.readShort();
            }
        }
        ZoneInfoFile.zones.remove("ROC");
        for (short n6 = 0; n6 < short1; ++n6) {
            final short short5 = dataInputStream.readShort();
            ZoneInfoFile.aliases.clear();
            for (short n7 = 0; n7 < short5; ++n7) {
                ZoneInfoFile.aliases.put(array[dataInputStream.readShort()], array[dataInputStream.readShort()]);
            }
        }
        addOldMapping();
    }
    
    public static ZoneInfo getZoneInfo(final DataInput dataInput, final String s) throws Exception {
        dataInput.readByte();
        final int int1 = dataInput.readInt();
        final long[] array = new long[int1];
        for (int i = 0; i < int1; ++i) {
            array[i] = readEpochSec(dataInput);
        }
        final int[] array2 = new int[int1 + 1];
        for (int j = 0; j < array2.length; ++j) {
            array2[j] = readOffset(dataInput);
        }
        final int int2 = dataInput.readInt();
        final long[] array3 = new long[int2];
        for (int k = 0; k < int2; ++k) {
            array3[k] = readEpochSec(dataInput);
        }
        final int[] array4 = new int[int2 + 1];
        for (int l = 0; l < array4.length; ++l) {
            array4[l] = readOffset(dataInput);
        }
        final byte byte1 = dataInput.readByte();
        final ZoneOffsetTransitionRule[] array5 = new ZoneOffsetTransitionRule[byte1];
        for (byte b = 0; b < byte1; ++b) {
            array5[b] = new ZoneOffsetTransitionRule(dataInput);
        }
        return getZoneInfo(s, array, array2, array3, array4, array5);
    }
    
    public static int readOffset(final DataInput dataInput) throws IOException {
        final byte byte1 = dataInput.readByte();
        return (byte1 == 127) ? dataInput.readInt() : (byte1 * 900);
    }
    
    static long readEpochSec(final DataInput dataInput) throws IOException {
        final int n = dataInput.readByte() & 0xFF;
        if (n == 255) {
            return dataInput.readLong();
        }
        return ((n << 16) + ((dataInput.readByte() & 0xFF) << 8) + (dataInput.readByte() & 0xFF)) * 900L - 4575744000L;
    }
    
    private static ZoneInfo getZoneInfo(final String s, final long[] array, final int[] array2, final long[] array3, final int[] array4, final ZoneOffsetTransitionRule[] array5) {
        int n = 0;
        int n2 = 0;
        int[] array6 = null;
        boolean b = false;
        int n3;
        if (array.length > 0) {
            n3 = array2[array2.length - 1] * 1000;
            b = (array[array.length - 1] > ZoneInfoFile.CURRT);
        }
        else {
            n3 = array2[0] * 1000;
        }
        long[] array7 = null;
        int[] array8 = null;
        int n4 = 0;
        int n5 = 0;
        if (array3.length != 0) {
            array7 = new long[250];
            array8 = new int[100];
            int year = getYear(array3[array3.length - 1], array4[array3.length - 1]);
            int i = 0;
            int j = 1;
            while (i < array3.length && array3[i] < -2208988800L) {
                ++i;
            }
            if (i < array3.length) {
                if (i < array3.length) {
                    array8[0] = array2[array2.length - 1] * 1000;
                    n4 = 1;
                }
                n4 = addTrans(array7, n5++, array8, n4, -2208988800L, array4[i], getStandardOffset(array, array2, -2208988800L));
            }
            while (i < array3.length) {
                final long n6 = array3[i];
                if (n6 > 2145916799L) {
                    year = 2037;
                    break;
                }
                while (j < array.length) {
                    final long n7 = array[j];
                    if (n7 >= -2208988800L) {
                        if (n7 > n6) {
                            break;
                        }
                        if (n7 < n6) {
                            if (n4 + 2 >= array8.length) {
                                array8 = Arrays.copyOf(array8, array8.length + 100);
                            }
                            if (n5 + 1 >= array7.length) {
                                array7 = Arrays.copyOf(array7, array7.length + 100);
                            }
                            n4 = addTrans(array7, n5++, array8, n4, n7, array4[i], array2[j + 1]);
                        }
                    }
                    ++j;
                }
                if (n4 + 2 >= array8.length) {
                    array8 = Arrays.copyOf(array8, array8.length + 100);
                }
                if (n5 + 1 >= array7.length) {
                    array7 = Arrays.copyOf(array7, array7.length + 100);
                }
                n4 = addTrans(array7, n5++, array8, n4, n6, array4[i + 1], getStandardOffset(array, array2, n6));
                ++i;
            }
            while (j < array.length) {
                final long n8 = array[j];
                if (n8 >= -2208988800L) {
                    final int index = indexOf(array8, 0, n4, array4[i]);
                    if (index == n4) {
                        ++n4;
                    }
                    array7[n5++] = (n8 * 1000L << 12 | ((long)index & 0xFL));
                }
                ++j;
            }
            if (array5.length > 1) {
                while (year++ < 2037) {
                    for (final ZoneOffsetTransitionRule zoneOffsetTransitionRule : array5) {
                        final long transitionEpochSecond = zoneOffsetTransitionRule.getTransitionEpochSecond(year);
                        if (n4 + 2 >= array8.length) {
                            array8 = Arrays.copyOf(array8, array8.length + 100);
                        }
                        if (n5 + 1 >= array7.length) {
                            array7 = Arrays.copyOf(array7, array7.length + 100);
                        }
                        n4 = addTrans(array7, n5++, array8, n4, transitionEpochSecond, zoneOffsetTransitionRule.offsetAfter, zoneOffsetTransitionRule.standardOffset);
                    }
                }
                ZoneOffsetTransitionRule zoneOffsetTransitionRule2 = array5[array5.length - 2];
                ZoneOffsetTransitionRule zoneOffsetTransitionRule3 = array5[array5.length - 1];
                array6 = new int[10];
                if (zoneOffsetTransitionRule2.offsetAfter - zoneOffsetTransitionRule2.offsetBefore < 0 && zoneOffsetTransitionRule3.offsetAfter - zoneOffsetTransitionRule3.offsetBefore > 0) {
                    final ZoneOffsetTransitionRule zoneOffsetTransitionRule4 = zoneOffsetTransitionRule2;
                    zoneOffsetTransitionRule2 = zoneOffsetTransitionRule3;
                    zoneOffsetTransitionRule3 = zoneOffsetTransitionRule4;
                }
                array6[0] = zoneOffsetTransitionRule2.month - 1;
                final byte access$500 = zoneOffsetTransitionRule2.dom;
                final int access$501 = zoneOffsetTransitionRule2.dow;
                if (access$501 == -1) {
                    array6[1] = access$500;
                    array6[2] = 0;
                }
                else if (access$500 < 0 || (access$500 >= 24 && !s.equals("Asia/Gaza") && !s.equals("Asia/Hebron"))) {
                    array6[1] = -1;
                    array6[2] = ZoneInfoFile.toCalendarDOW[access$501];
                }
                else {
                    array6[1] = access$500;
                    array6[2] = -ZoneInfoFile.toCalendarDOW[access$501];
                }
                array6[3] = zoneOffsetTransitionRule2.secondOfDay * 1000;
                array6[4] = ZoneInfoFile.toSTZTime[zoneOffsetTransitionRule2.timeDefinition];
                array6[5] = zoneOffsetTransitionRule3.month - 1;
                final byte access$502 = zoneOffsetTransitionRule3.dom;
                final int access$503 = zoneOffsetTransitionRule3.dow;
                if (access$503 == -1) {
                    array6[6] = access$502;
                    array6[7] = 0;
                }
                else if (access$502 < 0 || (access$502 >= 24 && !s.equals("Asia/Gaza") && !s.equals("Asia/Hebron"))) {
                    array6[6] = -1;
                    array6[7] = ZoneInfoFile.toCalendarDOW[access$503];
                }
                else {
                    array6[6] = access$502;
                    array6[7] = -ZoneInfoFile.toCalendarDOW[access$503];
                }
                array6[8] = zoneOffsetTransitionRule3.secondOfDay * 1000;
                array6[9] = ZoneInfoFile.toSTZTime[zoneOffsetTransitionRule3.timeDefinition];
                n = (zoneOffsetTransitionRule2.offsetAfter - zoneOffsetTransitionRule2.offsetBefore) * 1000;
                if (array6[2] == 6 && array6[3] == 0 && s.equals("Asia/Amman")) {
                    array6[2] = 5;
                    array6[3] = 86400000;
                }
                if (array6[2] == 7 && array6[3] == 0 && s.equals("Asia/Amman")) {
                    array6[2] = 6;
                    array6[3] = 86400000;
                }
                if (array6[7] == 6 && array6[8] == 0 && s.equals("Africa/Cairo")) {
                    array6[7] = 5;
                    array6[8] = 86400000;
                }
            }
            else if (n5 > 0) {
                if (year < 2037) {
                    final long n9 = 2114380800L - n3 / 1000;
                    final int index2 = indexOf(array8, 0, n4, n3 / 1000);
                    if (index2 == n4) {
                        ++n4;
                    }
                    array7[n5++] = (n9 * 1000L << 12 | ((long)index2 & 0xFL));
                }
                else if (array3.length > 2) {
                    final int length2 = array3.length;
                    final long n10 = array3[length2 - 2];
                    final int n11 = array4[length2 - 2 + 1];
                    final int standardOffset = getStandardOffset(array, array2, n10);
                    final long n12 = array3[length2 - 1];
                    final int n13 = array4[length2 - 1 + 1];
                    final int standardOffset2 = getStandardOffset(array, array2, n12);
                    if (n11 > standardOffset && n13 == standardOffset2) {
                        final int n14 = array3.length - 2;
                        final ZoneOffset ofTotalSeconds = ZoneOffset.ofTotalSeconds(array4[n14]);
                        final ZoneOffset ofTotalSeconds2 = ZoneOffset.ofTotalSeconds(array4[n14 + 1]);
                        final LocalDateTime ofEpochSecond = LocalDateTime.ofEpochSecond(array3[n14], 0, ofTotalSeconds);
                        LocalDateTime plusSeconds;
                        if (ofTotalSeconds2.getTotalSeconds() > ofTotalSeconds.getTotalSeconds()) {
                            plusSeconds = ofEpochSecond;
                        }
                        else {
                            plusSeconds = ofEpochSecond.plusSeconds(array4[n14 + 1] - array4[n14]);
                        }
                        final int n15 = array3.length - 1;
                        final ZoneOffset ofTotalSeconds3 = ZoneOffset.ofTotalSeconds(array4[n15]);
                        final ZoneOffset ofTotalSeconds4 = ZoneOffset.ofTotalSeconds(array4[n15 + 1]);
                        final LocalDateTime ofEpochSecond2 = LocalDateTime.ofEpochSecond(array3[n15], 0, ofTotalSeconds3);
                        LocalDateTime plusSeconds2;
                        if (ofTotalSeconds4.getTotalSeconds() > ofTotalSeconds3.getTotalSeconds()) {
                            plusSeconds2 = ofEpochSecond2.plusSeconds(array4[n15 + 1] - array4[n15]);
                        }
                        else {
                            plusSeconds2 = ofEpochSecond2;
                        }
                        array6 = new int[] { plusSeconds.getMonthValue() - 1, plusSeconds.getDayOfMonth(), 0, plusSeconds.toLocalTime().toSecondOfDay() * 1000, 0, plusSeconds2.getMonthValue() - 1, plusSeconds2.getDayOfMonth(), 0, plusSeconds2.toLocalTime().toSecondOfDay() * 1000, 0 };
                        n = (n11 - standardOffset) * 1000;
                    }
                }
            }
            if (array7 != null && array7.length != n5) {
                if (n5 == 0) {
                    array7 = null;
                }
                else {
                    array7 = Arrays.copyOf(array7, n5);
                }
            }
            if (array8 != null && array8.length != n4) {
                if (n4 == 0) {
                    array8 = null;
                }
                else {
                    array8 = Arrays.copyOf(array8, n4);
                }
            }
            if (array7 != null) {
                final Checksum checksum = new Checksum();
                for (int l = 0; l < array7.length; ++l) {
                    final long n16 = array7[l];
                    final int n17 = (int)(n16 >>> 4 & 0xFL);
                    final int n18 = (n17 == 0) ? 0 : array8[n17];
                    final int n19 = (int)(n16 & 0xFL);
                    final int n20 = array8[n19];
                    checksum.update((n16 >> 12) + n19);
                    checksum.update(n19);
                    checksum.update((n17 == 0) ? -1 : n17);
                }
                n2 = (int)checksum.getValue();
            }
        }
        return new ZoneInfo(s, n3, n, n2, array7, array8, array6, b);
    }
    
    private static int getStandardOffset(final long[] array, final int[] array2, final long n) {
        int n2;
        for (n2 = 0; n2 < array.length && n >= array[n2]; ++n2) {}
        return array2[n2];
    }
    
    private static int getYear(final long n, final int n2) {
        long n3 = Math.floorDiv(n + n2, 86400L) + 719528L - 60L;
        long n4 = 0L;
        if (n3 < 0L) {
            final long n5 = (n3 + 1L) / 146097L - 1L;
            n4 = n5 * 400L;
            n3 += -n5 * 146097L;
        }
        long n6 = (400L * n3 + 591L) / 146097L;
        long n7 = n3 - (365L * n6 + n6 / 4L - n6 / 100L + n6 / 400L);
        if (n7 < 0L) {
            --n6;
            n7 = n3 - (365L * n6 + n6 / 4L - n6 / 100L + n6 / 400L);
        }
        return (int)(n6 + n4 + ((int)n7 * 5 + 2) / 153 / 10);
    }
    
    private static int indexOf(final int[] array, int i, final int n, int n2) {
        n2 *= 1000;
        while (i < n) {
            if (array[i] == n2) {
                return i;
            }
            ++i;
        }
        array[i] = n2;
        return i;
    }
    
    private static int addTrans(final long[] array, final int n, final int[] array2, int n2, final long n3, final int n4, final int n5) {
        final int index = indexOf(array2, 0, n2, n4);
        if (index == n2) {
            ++n2;
        }
        int index2 = 0;
        if (n4 != n5) {
            index2 = indexOf(array2, 1, n2, n4 - n5);
            if (index2 == n2) {
                ++n2;
            }
        }
        array[n] = (n3 * 1000L << 12 | ((long)(index2 << 4) & 0xF0L) | ((long)index & 0xFL));
        return n2;
    }
    
    static {
        zones = new ConcurrentHashMap<String, ZoneInfo>();
        ZoneInfoFile.aliases = new HashMap<String, String>();
        ZoneInfoFile.oldMappings = new String[][] { { "ACT", "Australia/Darwin" }, { "AET", "Australia/Sydney" }, { "AGT", "America/Argentina/Buenos_Aires" }, { "ART", "Africa/Cairo" }, { "AST", "America/Anchorage" }, { "BET", "America/Sao_Paulo" }, { "BST", "Asia/Dhaka" }, { "CAT", "Africa/Harare" }, { "CNT", "America/St_Johns" }, { "CST", "America/Chicago" }, { "CTT", "Asia/Shanghai" }, { "EAT", "Africa/Addis_Ababa" }, { "ECT", "Europe/Paris" }, { "IET", "America/Indiana/Indianapolis" }, { "IST", "Asia/Kolkata" }, { "JST", "Asia/Tokyo" }, { "MIT", "Pacific/Apia" }, { "NET", "Asia/Yerevan" }, { "NST", "Pacific/Auckland" }, { "PLT", "Asia/Karachi" }, { "PNT", "America/Phoenix" }, { "PRT", "America/Puerto_Rico" }, { "PST", "America/Los_Angeles" }, { "SST", "Pacific/Guadalcanal" }, { "VST", "Asia/Ho_Chi_Minh" } };
        final String lowerCase = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.timezone.ids.oldmapping", "false")).toLowerCase(Locale.ROOT);
        USE_OLDMAPPING = (lowerCase.equals("yes") || lowerCase.equals("true"));
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                try (final DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(new File(System.getProperty("java.home") + File.separator + "lib", "tzdb.dat"))))) {
                    load(dataInputStream);
                }
                catch (final Exception ex) {
                    throw new Error(ex);
                }
                return null;
            }
        });
        CURRT = System.currentTimeMillis() / 1000L;
        toCalendarDOW = new int[] { -1, 2, 3, 4, 5, 6, 7, 1 };
        toSTZTime = new int[] { 2, 0, 1 };
    }
    
    private static class Checksum extends CRC32
    {
        @Override
        public void update(final int n) {
            this.update(new byte[] { (byte)(n >>> 24), (byte)(n >>> 16), (byte)(n >>> 8), (byte)n });
        }
        
        void update(final long n) {
            this.update(new byte[] { (byte)(n >>> 56), (byte)(n >>> 48), (byte)(n >>> 40), (byte)(n >>> 32), (byte)(n >>> 24), (byte)(n >>> 16), (byte)(n >>> 8), (byte)n });
        }
    }
    
    private static class ZoneOffsetTransitionRule
    {
        private final int month;
        private final byte dom;
        private final int dow;
        private final int secondOfDay;
        private final boolean timeEndOfDay;
        private final int timeDefinition;
        private final int standardOffset;
        private final int offsetBefore;
        private final int offsetAfter;
        
        ZoneOffsetTransitionRule(final DataInput dataInput) throws IOException {
            final int int1 = dataInput.readInt();
            final int n = (int1 & 0x380000) >>> 19;
            final int n2 = (int1 & 0x7C000) >>> 14;
            final int n3 = (int1 & 0xFF0) >>> 4;
            final int n4 = (int1 & 0xC) >>> 2;
            final int n5 = int1 & 0x3;
            this.month = int1 >>> 28;
            this.dom = (byte)(((int1 & 0xFC00000) >>> 22) - 32);
            this.dow = ((n == 0) ? -1 : n);
            this.secondOfDay = ((n2 == 31) ? dataInput.readInt() : (n2 * 3600));
            this.timeEndOfDay = (n2 == 24);
            this.timeDefinition = (int1 & 0x3000) >>> 12;
            this.standardOffset = ((n3 == 255) ? dataInput.readInt() : ((n3 - 128) * 900));
            this.offsetBefore = ((n4 == 3) ? dataInput.readInt() : (this.standardOffset + n4 * 1800));
            this.offsetAfter = ((n5 == 3) ? dataInput.readInt() : (this.standardOffset + n5 * 1800));
        }
        
        long getTransitionEpochSecond(final int n) {
            long n2;
            if (this.dom < 0) {
                n2 = toEpochDay(n, this.month, lengthOfMonth(n, this.month) + 1 + this.dom);
                if (this.dow != -1) {
                    n2 = previousOrSame(n2, this.dow);
                }
            }
            else {
                n2 = toEpochDay(n, this.month, this.dom);
                if (this.dow != -1) {
                    n2 = nextOrSame(n2, this.dow);
                }
            }
            if (this.timeEndOfDay) {
                ++n2;
            }
            int n3 = 0;
            switch (this.timeDefinition) {
                case 0: {
                    n3 = 0;
                    break;
                }
                case 1: {
                    n3 = -this.offsetBefore;
                    break;
                }
                case 2: {
                    n3 = -this.standardOffset;
                    break;
                }
            }
            return n2 * 86400L + this.secondOfDay + n3;
        }
        
        static final boolean isLeapYear(final int n) {
            return (n & 0x3) == 0x0 && (n % 100 != 0 || n % 400 == 0);
        }
        
        static final int lengthOfMonth(final int n, final int n2) {
            switch (n2) {
                case 2: {
                    return isLeapYear(n) ? 29 : 28;
                }
                case 4:
                case 6:
                case 9:
                case 11: {
                    return 30;
                }
                default: {
                    return 31;
                }
            }
        }
        
        static final long toEpochDay(final int n, final int n2, final int n3) {
            final long n4 = n;
            final long n5 = n2;
            final long n6 = 0L + 365L * n4;
            long n7;
            if (n4 >= 0L) {
                n7 = n6 + ((n4 + 3L) / 4L - (n4 + 99L) / 100L + (n4 + 399L) / 400L);
            }
            else {
                n7 = n6 - (n4 / -4L - n4 / -100L + n4 / -400L);
            }
            long n8 = n7 + (367L * n5 - 362L) / 12L + (n3 - 1);
            if (n5 > 2L) {
                --n8;
                if (!isLeapYear(n)) {
                    --n8;
                }
            }
            return n8 - 719528L;
        }
        
        static final long previousOrSame(final long n, final int n2) {
            return adjust(n, n2, 1);
        }
        
        static final long nextOrSame(final long n, final int n2) {
            return adjust(n, n2, 0);
        }
        
        static final long adjust(final long n, final int n2, final int n3) {
            final int n4 = (int)Math.floorMod(n + 3L, 7L) + 1;
            if (n3 < 2 && n4 == n2) {
                return n;
            }
            if ((n3 & 0x1) == 0x0) {
                final int n5 = n4 - n2;
                return n + ((n5 >= 0) ? (7 - n5) : (-n5));
            }
            final int n6 = n2 - n4;
            return n - ((n6 >= 0) ? (7 - n6) : (-n6));
        }
    }
}
