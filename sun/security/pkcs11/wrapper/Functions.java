package sun.security.pkcs11.wrapper;

import java.util.HashMap;
import java.util.Arrays;
import java.math.BigInteger;
import java.util.Map;

public class Functions
{
    private static final Map<Integer, String> mechNames;
    private static final Map<String, Integer> mechIds;
    private static final Map<String, Long> hashMechIds;
    private static final Map<Integer, String> keyNames;
    private static final Map<String, Integer> keyIds;
    private static final Map<Integer, String> attributeNames;
    private static final Map<String, Integer> attributeIds;
    private static final Map<Integer, String> objectClassNames;
    private static final Map<String, Integer> objectClassIds;
    private static final Map<Integer, String> mgfNames;
    private static final Map<String, Integer> mgfIds;
    private static final char[] HEX_DIGITS;
    private static final Flags slotInfoFlags;
    private static final Flags tokenInfoFlags;
    private static final Flags sessionInfoFlags;
    private static final Flags mechanismInfoFlags;
    
    public static String toFullHexString(final long n) {
        long n2 = n;
        final StringBuffer sb = new StringBuffer(16);
        for (int i = 0; i < 16; ++i) {
            sb.append(Functions.HEX_DIGITS[(int)n2 & 0xF]);
            n2 >>>= 4;
        }
        return sb.reverse().toString();
    }
    
    public static String toFullHexString(final int n) {
        int n2 = n;
        final StringBuffer sb = new StringBuffer(8);
        for (int i = 0; i < 8; ++i) {
            sb.append(Functions.HEX_DIGITS[n2 & 0xF]);
            n2 >>>= 4;
        }
        return sb.reverse().toString();
    }
    
    public static String toHexString(final long n) {
        return Long.toHexString(n);
    }
    
    public static String toHexString(final byte[] array) {
        if (array == null) {
            return null;
        }
        final StringBuffer sb = new StringBuffer(2 * array.length);
        for (int i = 0; i < array.length; ++i) {
            final int n = array[i] & 0xFF;
            if (n < 16) {
                sb.append('0');
            }
            sb.append(Integer.toString(n, 16));
        }
        return sb.toString();
    }
    
    public static String toBinaryString(final long n) {
        return Long.toString(n, 2);
    }
    
    public static String toBinaryString(final byte[] array) {
        return new BigInteger(1, array).toString(2);
    }
    
    public static String slotInfoFlagsToString(final long n) {
        return Functions.slotInfoFlags.toString(n);
    }
    
    public static String tokenInfoFlagsToString(final long n) {
        return Functions.tokenInfoFlags.toString(n);
    }
    
    public static String sessionInfoFlagsToString(final long n) {
        return Functions.sessionInfoFlags.toString(n);
    }
    
    public static String sessionStateToString(final long n) {
        String string;
        if (n == 0L) {
            string = "CKS_RO_PUBLIC_SESSION";
        }
        else if (n == 1L) {
            string = "CKS_RO_USER_FUNCTIONS";
        }
        else if (n == 2L) {
            string = "CKS_RW_PUBLIC_SESSION";
        }
        else if (n == 3L) {
            string = "CKS_RW_USER_FUNCTIONS";
        }
        else if (n == 4L) {
            string = "CKS_RW_SO_FUNCTIONS";
        }
        else {
            string = "ERROR: unknown session state 0x" + toFullHexString(n);
        }
        return string;
    }
    
    public static String mechanismInfoFlagsToString(final long n) {
        return Functions.mechanismInfoFlags.toString(n);
    }
    
    private static String getName(final Map<Integer, String> map, final long n) {
        String s = null;
        if (n >>> 32 == 0L) {
            s = map.get((int)n);
        }
        if (s == null) {
            if ((n & 0x80000000L) != 0x0L) {
                s = "(Vendor-Specific) 0x" + toFullHexString(n);
            }
            else {
                s = "(Unknown) 0x" + toFullHexString(n);
            }
        }
        return s;
    }
    
    public static long getId(final Map<String, Integer> map, final String s) {
        final Integer n = map.get(s);
        if (n == null) {
            throw new IllegalArgumentException("Unknown name " + s);
        }
        return (long)n & 0xFFFFFFFFL;
    }
    
    public static String getMechanismName(final long n) {
        return getName(Functions.mechNames, n);
    }
    
    public static long getMechanismId(final String s) {
        return getId(Functions.mechIds, s);
    }
    
    public static String getKeyName(final long n) {
        return getName(Functions.keyNames, n);
    }
    
    public static long getKeyId(final String s) {
        return getId(Functions.keyIds, s);
    }
    
    public static String getAttributeName(final long n) {
        return getName(Functions.attributeNames, n);
    }
    
    public static long getAttributeId(final String s) {
        return getId(Functions.attributeIds, s);
    }
    
    public static String getObjectClassName(final long n) {
        return getName(Functions.objectClassNames, n);
    }
    
    public static long getObjectClassId(final String s) {
        return getId(Functions.objectClassIds, s);
    }
    
    public static long getHashMechId(final String s) {
        return Functions.hashMechIds.get(s);
    }
    
    public static String getMGFName(final long n) {
        return getName(Functions.mgfNames, n);
    }
    
    public static long getMGFId(final String s) {
        return getId(Functions.mgfIds, s);
    }
    
    private static boolean equals(final char[] array, final char[] array2) {
        return Arrays.equals(array, array2);
    }
    
    public static boolean equals(final CK_DATE ck_DATE, final CK_DATE ck_DATE2) {
        return ck_DATE == ck_DATE2 || (ck_DATE != null && ck_DATE2 != null && equals(ck_DATE.year, ck_DATE2.year) && equals(ck_DATE.month, ck_DATE2.month) && equals(ck_DATE.day, ck_DATE2.day));
    }
    
    public static int hashCode(final byte[] array) {
        int n = 0;
        if (array != null) {
            for (int n2 = 0; n2 < 4 && n2 < array.length; ++n2) {
                n ^= (0xFF & array[n2]) << (n2 % 4 << 3);
            }
        }
        return n;
    }
    
    public static int hashCode(final char[] array) {
        int n = 0;
        if (array != null) {
            for (int n2 = 0; n2 < 4 && n2 < array.length; ++n2) {
                n ^= ('\uffff' & array[n2]) << (n2 % 2 << 4);
            }
        }
        return n;
    }
    
    public static int hashCode(final CK_DATE ck_DATE) {
        int n = 0;
        if (ck_DATE != null) {
            if (ck_DATE.year.length == 4) {
                n = (n ^ ('\uffff' & ck_DATE.year[0]) << 16 ^ ('\uffff' & ck_DATE.year[1]) ^ ('\uffff' & ck_DATE.year[2]) << 16 ^ ('\uffff' & ck_DATE.year[3]));
            }
            if (ck_DATE.month.length == 2) {
                n = (n ^ ('\uffff' & ck_DATE.month[0]) << 16 ^ ('\uffff' & ck_DATE.month[1]));
            }
            if (ck_DATE.day.length == 2) {
                n = (n ^ ('\uffff' & ck_DATE.day[0]) << 16 ^ ('\uffff' & ck_DATE.day[1]));
            }
        }
        return n;
    }
    
    private static void addMapping(final Map<Integer, String> map, final Map<String, Integer> map2, final long n, final String s) {
        if (n >>> 32 != 0L) {
            throw new AssertionError((Object)("Id has high bits set: " + n + ", " + s));
        }
        final Integer value = (int)n;
        if (map.put(value, s) != null) {
            throw new AssertionError((Object)("Duplicate id: " + n + ", " + s));
        }
        if (map2.put(s, value) != null) {
            throw new AssertionError((Object)("Duplicate name: " + n + ", " + s));
        }
    }
    
    private static void addMech(final long n, final String s) {
        addMapping(Functions.mechNames, Functions.mechIds, n, s);
    }
    
    private static void addKeyType(final long n, final String s) {
        addMapping(Functions.keyNames, Functions.keyIds, n, s);
    }
    
    private static void addAttribute(final long n, final String s) {
        addMapping(Functions.attributeNames, Functions.attributeIds, n, s);
    }
    
    private static void addObjectClass(final long n, final String s) {
        addMapping(Functions.objectClassNames, Functions.objectClassIds, n, s);
    }
    
    private static void addHashMech(final long n, final String... array) {
        for (int length = array.length, i = 0; i < length; ++i) {
            Functions.hashMechIds.put(array[i], n);
        }
    }
    
    private static void addMGF(final long n, final String s) {
        addMapping(Functions.mgfNames, Functions.mgfIds, n, s);
    }
    
    static {
        mechNames = new HashMap<Integer, String>();
        mechIds = new HashMap<String, Integer>();
        hashMechIds = new HashMap<String, Long>();
        keyNames = new HashMap<Integer, String>();
        keyIds = new HashMap<String, Integer>();
        attributeNames = new HashMap<Integer, String>();
        attributeIds = new HashMap<String, Integer>();
        objectClassNames = new HashMap<Integer, String>();
        objectClassIds = new HashMap<String, Integer>();
        mgfNames = new HashMap<Integer, String>();
        mgfIds = new HashMap<String, Integer>();
        HEX_DIGITS = "0123456789ABCDEF".toCharArray();
        slotInfoFlags = new Flags(new long[] { 1L, 2L, 4L }, new String[] { "CKF_TOKEN_PRESENT", "CKF_REMOVABLE_DEVICE", "CKF_HW_SLOT" });
        tokenInfoFlags = new Flags(new long[] { 1L, 2L, 4L, 8L, 32L, 64L, 256L, 512L, 1024L, 2048L, 65536L, 131072L, 262144L, 524288L, 1048576L, 2097152L, 4194304L, 8388608L }, new String[] { "CKF_RNG", "CKF_WRITE_PROTECTED", "CKF_LOGIN_REQUIRED", "CKF_USER_PIN_INITIALIZED", "CKF_RESTORE_KEY_NOT_NEEDED", "CKF_CLOCK_ON_TOKEN", "CKF_PROTECTED_AUTHENTICATION_PATH", "CKF_DUAL_CRYPTO_OPERATIONS", "CKF_TOKEN_INITIALIZED", "CKF_SECONDARY_AUTHENTICATION", "CKF_USER_PIN_COUNT_LOW", "CKF_USER_PIN_FINAL_TRY", "CKF_USER_PIN_LOCKED", "CKF_USER_PIN_TO_BE_CHANGED", "CKF_SO_PIN_COUNT_LOW", "CKF_SO_PIN_FINAL_TRY", "CKF_SO_PIN_LOCKED", "CKF_SO_PIN_TO_BE_CHANGED" });
        sessionInfoFlags = new Flags(new long[] { 2L, 4L }, new String[] { "CKF_RW_SESSION", "CKF_SERIAL_SESSION" });
        mechanismInfoFlags = new Flags(new long[] { 1L, 256L, 512L, 1024L, 2048L, 4096L, 8192L, 16384L, 32768L, 65536L, 131072L, 262144L, 524288L, 1048576L, 2097152L, 4194304L, 8388608L, 16777216L, 33554432L, 2147483648L }, new String[] { "CKF_HW", "CKF_ENCRYPT", "CKF_DECRYPT", "CKF_DIGEST", "CKF_SIGN", "CKF_SIGN_RECOVER", "CKF_VERIFY", "CKF_VERIFY_RECOVER", "CKF_GENERATE", "CKF_GENERATE_KEY_PAIR", "CKF_WRAP", "CKF_UNWRAP", "CKF_DERIVE", "CKF_EC_F_P", "CKF_EC_F_2M", "CKF_EC_ECPARAMETERS", "CKF_EC_NAMEDCURVE", "CKF_EC_UNCOMPRESS", "CKF_EC_COMPRESS", "CKF_EXTENSION" });
        addMech(0L, "CKM_RSA_PKCS_KEY_PAIR_GEN");
        addMech(1L, "CKM_RSA_PKCS");
        addMech(2L, "CKM_RSA_9796");
        addMech(3L, "CKM_RSA_X_509");
        addMech(4L, "CKM_MD2_RSA_PKCS");
        addMech(5L, "CKM_MD5_RSA_PKCS");
        addMech(6L, "CKM_SHA1_RSA_PKCS");
        addMech(7L, "CKM_RIPEMD128_RSA_PKCS");
        addMech(8L, "CKM_RIPEMD160_RSA_PKCS");
        addMech(9L, "CKM_RSA_PKCS_OAEP");
        addMech(10L, "CKM_RSA_X9_31_KEY_PAIR_GEN");
        addMech(11L, "CKM_RSA_X9_31");
        addMech(12L, "CKM_SHA1_RSA_X9_31");
        addMech(13L, "CKM_RSA_PKCS_PSS");
        addMech(14L, "CKM_SHA1_RSA_PKCS_PSS");
        addMech(16L, "CKM_DSA_KEY_PAIR_GEN");
        addMech(17L, "CKM_DSA");
        addMech(18L, "CKM_DSA_SHA1");
        addMech(19L, "CKM_DSA_SHA224");
        addMech(20L, "CKM_DSA_SHA256");
        addMech(21L, "CKM_DSA_SHA384");
        addMech(22L, "CKM_DSA_SHA512");
        addMech(32L, "CKM_DH_PKCS_KEY_PAIR_GEN");
        addMech(33L, "CKM_DH_PKCS_DERIVE");
        addMech(48L, "CKM_X9_42_DH_KEY_PAIR_GEN");
        addMech(49L, "CKM_X9_42_DH_DERIVE");
        addMech(50L, "CKM_X9_42_DH_HYBRID_DERIVE");
        addMech(51L, "CKM_X9_42_MQV_DERIVE");
        addMech(64L, "CKM_SHA256_RSA_PKCS");
        addMech(65L, "CKM_SHA384_RSA_PKCS");
        addMech(66L, "CKM_SHA512_RSA_PKCS");
        addMech(67L, "CKM_SHA256_RSA_PKCS_PSS");
        addMech(68L, "CKM_SHA384_RSA_PKCS_PSS");
        addMech(69L, "CKM_SHA512_RSA_PKCS_PSS");
        addMech(70L, "CKM_SHA224_RSA_PKCS");
        addMech(71L, "CKM_SHA224_RSA_PKCS_PSS");
        addMech(256L, "CKM_RC2_KEY_GEN");
        addMech(257L, "CKM_RC2_ECB");
        addMech(258L, "CKM_RC2_CBC");
        addMech(259L, "CKM_RC2_MAC");
        addMech(260L, "CKM_RC2_MAC_GENERAL");
        addMech(261L, "CKM_RC2_CBC_PAD");
        addMech(272L, "CKM_RC4_KEY_GEN");
        addMech(273L, "CKM_RC4");
        addMech(288L, "CKM_DES_KEY_GEN");
        addMech(289L, "CKM_DES_ECB");
        addMech(290L, "CKM_DES_CBC");
        addMech(291L, "CKM_DES_MAC");
        addMech(292L, "CKM_DES_MAC_GENERAL");
        addMech(293L, "CKM_DES_CBC_PAD");
        addMech(304L, "CKM_DES2_KEY_GEN");
        addMech(305L, "CKM_DES3_KEY_GEN");
        addMech(306L, "CKM_DES3_ECB");
        addMech(307L, "CKM_DES3_CBC");
        addMech(308L, "CKM_DES3_MAC");
        addMech(309L, "CKM_DES3_MAC_GENERAL");
        addMech(310L, "CKM_DES3_CBC_PAD");
        addMech(311L, "CKM_DES3_CMAC_GENERAL");
        addMech(312L, "CKM_DES3_CMAC");
        addMech(320L, "CKM_CDMF_KEY_GEN");
        addMech(321L, "CKM_CDMF_ECB");
        addMech(322L, "CKM_CDMF_CBC");
        addMech(323L, "CKM_CDMF_MAC");
        addMech(324L, "CKM_CDMF_MAC_GENERAL");
        addMech(325L, "CKM_CDMF_CBC_PAD");
        addMech(336L, "CKM_DES_OFB64");
        addMech(337L, "CKM_DES_OFB8");
        addMech(338L, "CKM_DES_CFB64");
        addMech(339L, "CKM_DES_CFB8");
        addMech(512L, "CKM_MD2");
        addMech(513L, "CKM_MD2_HMAC");
        addMech(514L, "CKM_MD2_HMAC_GENERAL");
        addMech(528L, "CKM_MD5");
        addMech(529L, "CKM_MD5_HMAC");
        addMech(530L, "CKM_MD5_HMAC_GENERAL");
        addMech(544L, "CKM_SHA_1");
        addMech(545L, "CKM_SHA_1_HMAC");
        addMech(546L, "CKM_SHA_1_HMAC_GENERAL");
        addMech(560L, "CKM_RIPEMD128");
        addMech(561L, "CKM_RIPEMD128_HMAC");
        addMech(562L, "CKM_RIPEMD128_HMAC_GENERAL");
        addMech(576L, "CKM_RIPEMD160");
        addMech(577L, "CKM_RIPEMD160_HMAC");
        addMech(578L, "CKM_RIPEMD160_HMAC_GENERAL");
        addMech(597L, "CKM_SHA224");
        addMech(598L, "CKM_SHA224_HMAC");
        addMech(599L, "CKM_SHA224_HMAC_GENERAL");
        addMech(592L, "CKM_SHA256");
        addMech(593L, "CKM_SHA256_HMAC");
        addMech(594L, "CKM_SHA256_HMAC_GENERAL");
        addMech(608L, "CKM_SHA384");
        addMech(609L, "CKM_SHA384_HMAC");
        addMech(610L, "CKM_SHA384_HMAC_GENERAL");
        addMech(624L, "CKM_SHA512");
        addMech(625L, "CKM_SHA512_HMAC");
        addMech(626L, "CKM_SHA512_HMAC_GENERAL");
        addMech(72L, "CKM_SHA512_224");
        addMech(73L, "CKM_SHA512_224_HMAC");
        addMech(74L, "CKM_SHA512_224_HMAC_GENERAL");
        addMech(75L, "CKM_SHA512_224_KEY_DERIVATION");
        addMech(76L, "CKM_SHA512_256");
        addMech(77L, "CKM_SHA512_256_HMAC");
        addMech(78L, "CKM_SHA512_256_HMAC_GENERAL");
        addMech(79L, "CKM_SHA512_256_KEY_DERIVATION");
        addMech(80L, "CKM_SHA512_T");
        addMech(81L, "CKM_SHA512_T_HMAC");
        addMech(82L, "CKM_SHA512_T_HMAC_GENERAL");
        addMech(83L, "CKM_SHA512_T_KEY_DERIVATION");
        addMech(640L, "CKM_SECURID_KEY_GEN");
        addMech(642L, "CKM_SECURID");
        addMech(656L, "CKM_HOTP_KEY_GEN");
        addMech(657L, "CKM_HOTP");
        addMech(672L, "CKM_ACTI");
        addMech(673L, "CKM_ACTI_KEY_GEN");
        addMech(768L, "CKM_CAST_KEY_GEN");
        addMech(769L, "CKM_CAST_ECB");
        addMech(770L, "CKM_CAST_CBC");
        addMech(771L, "CKM_CAST_MAC");
        addMech(772L, "CKM_CAST_MAC_GENERAL");
        addMech(773L, "CKM_CAST_CBC_PAD");
        addMech(784L, "CKM_CAST3_KEY_GEN");
        addMech(785L, "CKM_CAST3_ECB");
        addMech(786L, "CKM_CAST3_CBC");
        addMech(787L, "CKM_CAST3_MAC");
        addMech(788L, "CKM_CAST3_MAC_GENERAL");
        addMech(789L, "CKM_CAST3_CBC_PAD");
        addMech(800L, "CKM_CAST128_KEY_GEN");
        addMech(801L, "CKM_CAST128_ECB");
        addMech(802L, "CKM_CAST128_CBC");
        addMech(803L, "CKM_CAST128_MAC");
        addMech(804L, "CKM_CAST128_MAC_GENERAL");
        addMech(805L, "CKM_CAST128_CBC_PAD");
        addMech(816L, "CKM_RC5_KEY_GEN");
        addMech(817L, "CKM_RC5_ECB");
        addMech(818L, "CKM_RC5_CBC");
        addMech(819L, "CKM_RC5_MAC");
        addMech(820L, "CKM_RC5_MAC_GENERAL");
        addMech(821L, "CKM_RC5_CBC_PAD");
        addMech(832L, "CKM_IDEA_KEY_GEN");
        addMech(833L, "CKM_IDEA_ECB");
        addMech(834L, "CKM_IDEA_CBC");
        addMech(835L, "CKM_IDEA_MAC");
        addMech(836L, "CKM_IDEA_MAC_GENERAL");
        addMech(837L, "CKM_IDEA_CBC_PAD");
        addMech(848L, "CKM_GENERIC_SECRET_KEY_GEN");
        addMech(864L, "CKM_CONCATENATE_BASE_AND_KEY");
        addMech(866L, "CKM_CONCATENATE_BASE_AND_DATA");
        addMech(867L, "CKM_CONCATENATE_DATA_AND_BASE");
        addMech(868L, "CKM_XOR_BASE_AND_DATA");
        addMech(869L, "CKM_EXTRACT_KEY_FROM_KEY");
        addMech(880L, "CKM_SSL3_PRE_MASTER_KEY_GEN");
        addMech(881L, "CKM_SSL3_MASTER_KEY_DERIVE");
        addMech(882L, "CKM_SSL3_KEY_AND_MAC_DERIVE");
        addMech(883L, "CKM_SSL3_MASTER_KEY_DERIVE_DH");
        addMech(884L, "CKM_TLS_PRE_MASTER_KEY_GEN");
        addMech(885L, "CKM_TLS_MASTER_KEY_DERIVE");
        addMech(886L, "CKM_TLS_KEY_AND_MAC_DERIVE");
        addMech(887L, "CKM_TLS_MASTER_KEY_DERIVE_DH");
        addMech(888L, "CKM_TLS_PRF");
        addMech(896L, "CKM_SSL3_MD5_MAC");
        addMech(897L, "CKM_SSL3_SHA1_MAC");
        addMech(912L, "CKM_MD5_KEY_DERIVATION");
        addMech(913L, "CKM_MD2_KEY_DERIVATION");
        addMech(914L, "CKM_SHA1_KEY_DERIVATION");
        addMech(918L, "CKM_SHA224_KEY_DERIVATION");
        addMech(915L, "CKM_SHA256_KEY_DERIVATION");
        addMech(916L, "CKM_SHA384_KEY_DERIVATION");
        addMech(917L, "CKM_SHA512_KEY_DERIVATION");
        addMech(928L, "CKM_PBE_MD2_DES_CBC");
        addMech(929L, "CKM_PBE_MD5_DES_CBC");
        addMech(930L, "CKM_PBE_MD5_CAST_CBC");
        addMech(931L, "CKM_PBE_MD5_CAST3_CBC");
        addMech(932L, "CKM_PBE_MD5_CAST128_CBC");
        addMech(933L, "CKM_PBE_SHA1_CAST128_CBC");
        addMech(934L, "CKM_PBE_SHA1_RC4_128");
        addMech(935L, "CKM_PBE_SHA1_RC4_40");
        addMech(936L, "CKM_PBE_SHA1_DES3_EDE_CBC");
        addMech(937L, "CKM_PBE_SHA1_DES2_EDE_CBC");
        addMech(938L, "CKM_PBE_SHA1_RC2_128_CBC");
        addMech(939L, "CKM_PBE_SHA1_RC2_40_CBC");
        addMech(944L, "CKM_PKCS5_PBKD2");
        addMech(960L, "CKM_PBA_SHA1_WITH_SHA1_HMAC");
        addMech(976L, "CKM_WTLS_PRE_MASTER_KEY_GEN");
        addMech(977L, "CKM_WTLS_MASTER_KEY_DERIVE");
        addMech(978L, "CKM_WTLS_MASTER_KEY_DERIVE_DH_ECC");
        addMech(979L, "CKM_WTLS_PRF");
        addMech(980L, "CKM_WTLS_SERVER_KEY_AND_MAC_DERIVE");
        addMech(981L, "CKM_WTLS_CLIENT_KEY_AND_MAC_DERIVE");
        addMech(982L, "CKM_TLS10_MAC_SERVER");
        addMech(983L, "CKM_TLS10_MAC_CLIENT");
        addMech(984L, "CKM_TLS12_MAC");
        addMech(985L, "CKM_TLS12_KDF");
        addMech(992L, "CKM_TLS12_MASTER_KEY_DERIVE");
        addMech(993L, "CKM_TLS12_KEY_AND_MAC_DERIVE");
        addMech(994L, "CKM_TLS12_MASTER_KEY_DERIVE_DH");
        addMech(995L, "CKM_TLS12_KEY_SAFE_DERIVE");
        addMech(996L, "CKM_TLS_MAC");
        addMech(997L, "CKM_TLS_KDF");
        addMech(1024L, "CKM_KEY_WRAP_LYNKS");
        addMech(1025L, "CKM_KEY_WRAP_SET_OAEP");
        addMech(1280L, "CKM_CMS_SIG");
        addMech(1296L, "CKM_KIP_DERIVE");
        addMech(1297L, "CKM_KIP_WRAP");
        addMech(1298L, "CKM_KIP_MAC");
        addMech(1360L, "CKM_CAMELLIA_KEY_GEN");
        addMech(1361L, "CKM_CAMELLIA_ECB");
        addMech(1362L, "CKM_CAMELLIA_CBC");
        addMech(1363L, "CKM_CAMELLIA_MAC");
        addMech(1364L, "CKM_CAMELLIA_MAC_GENERAL");
        addMech(1365L, "CKM_CAMELLIA_CBC_PAD");
        addMech(1366L, "CKM_CAMELLIA_ECB_ENCRYPT_DATA");
        addMech(1367L, "CKM_CAMELLIA_CBC_ENCRYPT_DATA");
        addMech(1368L, "CKM_CAMELLIA_CTR");
        addMech(1376L, "CKM_ARIA_KEY_GEN");
        addMech(1377L, "CKM_ARIA_ECB");
        addMech(1378L, "CKM_ARIA_CBC");
        addMech(1379L, "CKM_ARIA_MAC");
        addMech(1380L, "CKM_ARIA_MAC_GENERAL");
        addMech(1381L, "CKM_ARIA_CBC_PAD");
        addMech(1382L, "CKM_ARIA_ECB_ENCRYPT_DATA");
        addMech(1383L, "CKM_ARIA_CBC_ENCRYPT_DATA");
        addMech(1616L, "CKM_SEED_KEY_GEN");
        addMech(1617L, "CKM_SEED_ECB");
        addMech(1618L, "CKM_SEED_CBC");
        addMech(1619L, "CKM_SEED_MAC");
        addMech(1620L, "CKM_SEED_MAC_GENERAL");
        addMech(1621L, "CKM_SEED_CBC_PAD");
        addMech(1622L, "CKM_SEED_ECB_ENCRYPT_DATA");
        addMech(1623L, "CKM_SEED_CBC_ENCRYPT_DATA");
        addMech(4096L, "CKM_SKIPJACK_KEY_GEN");
        addMech(4097L, "CKM_SKIPJACK_ECB64");
        addMech(4098L, "CKM_SKIPJACK_CBC64");
        addMech(4099L, "CKM_SKIPJACK_OFB64");
        addMech(4100L, "CKM_SKIPJACK_CFB64");
        addMech(4101L, "CKM_SKIPJACK_CFB32");
        addMech(4102L, "CKM_SKIPJACK_CFB16");
        addMech(4103L, "CKM_SKIPJACK_CFB8");
        addMech(4104L, "CKM_SKIPJACK_WRAP");
        addMech(4105L, "CKM_SKIPJACK_PRIVATE_WRAP");
        addMech(4106L, "CKM_SKIPJACK_RELAYX");
        addMech(4112L, "CKM_KEA_KEY_PAIR_GEN");
        addMech(4113L, "CKM_KEA_KEY_DERIVE");
        addMech(4128L, "CKM_FORTEZZA_TIMESTAMP");
        addMech(4144L, "CKM_BATON_KEY_GEN");
        addMech(4145L, "CKM_BATON_ECB128");
        addMech(4146L, "CKM_BATON_ECB96");
        addMech(4147L, "CKM_BATON_CBC128");
        addMech(4148L, "CKM_BATON_COUNTER");
        addMech(4149L, "CKM_BATON_SHUFFLE");
        addMech(4150L, "CKM_BATON_WRAP");
        addMech(4160L, "CKM_EC_KEY_PAIR_GEN");
        addMech(4161L, "CKM_ECDSA");
        addMech(4162L, "CKM_ECDSA_SHA1");
        addMech(4163L, "CKM_ECDSA_SHA224");
        addMech(4164L, "CKM_ECDSA_SHA256");
        addMech(4165L, "CKM_ECDSA_SHA384");
        addMech(4166L, "CKM_ECDSA_SHA512");
        addMech(4176L, "CKM_ECDH1_DERIVE");
        addMech(4177L, "CKM_ECDH1_COFACTOR_DERIVE");
        addMech(4178L, "CKM_ECMQV_DERIVE");
        addMech(4179L, "CKM_ECDH_AES_KEY_WRAP");
        addMech(4180L, "CKM_RSA_AES_KEY_WRAP");
        addMech(4192L, "CKM_JUNIPER_KEY_GEN");
        addMech(4193L, "CKM_JUNIPER_ECB128");
        addMech(4194L, "CKM_JUNIPER_CBC128");
        addMech(4195L, "CKM_JUNIPER_COUNTER");
        addMech(4196L, "CKM_JUNIPER_SHUFFLE");
        addMech(4197L, "CKM_JUNIPER_WRAP");
        addMech(4208L, "CKM_FASTHASH");
        addMech(4224L, "CKM_AES_KEY_GEN");
        addMech(4225L, "CKM_AES_ECB");
        addMech(4226L, "CKM_AES_CBC");
        addMech(4227L, "CKM_AES_MAC");
        addMech(4228L, "CKM_AES_MAC_GENERAL");
        addMech(4229L, "CKM_AES_CBC_PAD");
        addMech(4230L, "CKM_AES_CTR");
        addMech(4231L, "CKM_AES_GCM");
        addMech(4232L, "CKM_AES_CCM");
        addMech(4233L, "CKM_AES_CTS");
        addMech(4234L, "CKM_AES_CMAC");
        addMech(4235L, "CKM_AES_CMAC_GENERAL");
        addMech(4236L, "CKM_AES_XCBC_MAC");
        addMech(4237L, "CKM_AES_XCBC_MAC_96");
        addMech(4238L, "CKM_AES_GMAC");
        addMech(4240L, "CKM_BLOWFISH_KEY_GEN");
        addMech(4241L, "CKM_BLOWFISH_CBC");
        addMech(4242L, "CKM_TWOFISH_KEY_GEN");
        addMech(4243L, "CKM_TWOFISH_CBC");
        addMech(4244L, "CKM_BLOWFISH_CBC_PAD");
        addMech(4245L, "CKM_TWOFISH_CBC_PAD");
        addMech(4352L, "CKM_DES_ECB_ENCRYPT_DATA");
        addMech(4353L, "CKM_DES_CBC_ENCRYPT_DATA");
        addMech(4354L, "CKM_DES3_ECB_ENCRYPT_DATA");
        addMech(4355L, "CKM_DES3_CBC_ENCRYPT_DATA");
        addMech(4356L, "CKM_AES_ECB_ENCRYPT_DATA");
        addMech(4357L, "CKM_AES_CBC_ENCRYPT_DATA");
        addMech(4608L, "CKM_GOSTR3410_KEY_PAIR_GEN");
        addMech(4609L, "CKM_GOSTR3410");
        addMech(4610L, "CKM_GOSTR3410_WITH_GOSTR3411");
        addMech(4611L, "CKM_GOSTR3410_KEY_WRAP");
        addMech(4612L, "CKM_GOSTR3410_DERIVE");
        addMech(4624L, "CKM_GOSTR3411");
        addMech(4625L, "CKM_GOSTR3411_HMAC");
        addMech(4640L, "CKM_GOST28147_KEY_GEN");
        addMech(4641L, "CKM_GOST28147_ECB");
        addMech(4642L, "CKM_GOST28147");
        addMech(4643L, "CKM_GOST28147_MAC");
        addMech(4644L, "CKM_GOST28147_KEY_WRAP");
        addMech(8192L, "CKM_DSA_PARAMETER_GEN");
        addMech(8193L, "CKM_DH_PKCS_PARAMETER_GEN");
        addMech(8194L, "CKM_X9_42_DH_PARAMETER_GEN");
        addMech(8195L, "CKM_DSA_PROBABLISTIC_PARAMETER_GEN");
        addMech(8196L, "CKM_DSA_SHAWE_TAYLOR_PARAMETER_GEN");
        addMech(8452L, "CKM_AES_OFB");
        addMech(8453L, "CKM_AES_CFB64");
        addMech(8454L, "CKM_AES_CFB8");
        addMech(8455L, "CKM_AES_CFB128");
        addMech(8456L, "CKM_AES_CFB1");
        addMech(8457L, "CKM_AES_KEY_WRAP");
        addMech(8458L, "CKM_AES_KEY_WRAP_PAD");
        addMech(16385L, "CKM_RSA_PKCS_TPM_1_1");
        addMech(16386L, "CKM_RSA_PKCS_OAEP_TPM_1_1");
        addMech(2147483648L, "CKM_VENDOR_DEFINED");
        addMech(2147484531L, "CKM_NSS_TLS_PRF_GENERAL");
        addMech(2147483424L, "SecureRandom");
        addMech(2147483425L, "KeyStore");
        addHashMech(544L, "SHA-1", "SHA", "SHA1");
        addHashMech(597L, "SHA-224", "SHA224");
        addHashMech(592L, "SHA-256", "SHA256");
        addHashMech(608L, "SHA-384", "SHA384");
        addHashMech(624L, "SHA-512", "SHA512");
        addHashMech(72L, "SHA-512/224", "SHA512/224");
        addHashMech(76L, "SHA-512/256", "SHA512/256");
        addKeyType(0L, "CKK_RSA");
        addKeyType(1L, "CKK_DSA");
        addKeyType(2L, "CKK_DH");
        addKeyType(3L, "CKK_EC");
        addKeyType(4L, "CKK_X9_42_DH");
        addKeyType(5L, "CKK_KEA");
        addKeyType(16L, "CKK_GENERIC_SECRET");
        addKeyType(17L, "CKK_RC2");
        addKeyType(18L, "CKK_RC4");
        addKeyType(19L, "CKK_DES");
        addKeyType(20L, "CKK_DES2");
        addKeyType(21L, "CKK_DES3");
        addKeyType(22L, "CKK_CAST");
        addKeyType(23L, "CKK_CAST3");
        addKeyType(24L, "CKK_CAST128");
        addKeyType(25L, "CKK_RC5");
        addKeyType(26L, "CKK_IDEA");
        addKeyType(27L, "CKK_SKIPJACK");
        addKeyType(28L, "CKK_BATON");
        addKeyType(29L, "CKK_JUNIPER");
        addKeyType(30L, "CKK_CDMF");
        addKeyType(31L, "CKK_AES");
        addKeyType(32L, "CKK_BLOWFISH");
        addKeyType(33L, "CKK_TWOFISH");
        addKeyType(34L, "CKK_SECURID");
        addKeyType(35L, "CKK_HOTP");
        addKeyType(36L, "CKK_ACTI");
        addKeyType(37L, "CKK_CAMELLIA");
        addKeyType(38L, "CKK_ARIA");
        addKeyType(39L, "CKK_MD5_HMAC");
        addKeyType(40L, "CKK_SHA_1_HMAC");
        addKeyType(41L, "CKK_RIPEMD128_HMAC");
        addKeyType(42L, "CKK_RIPEMD160_HMAC");
        addKeyType(43L, "CKK_SHA256_HMAC");
        addKeyType(44L, "CKK_SHA384_HMAC");
        addKeyType(45L, "CKK_SHA512_HMAC");
        addKeyType(46L, "CKK_SHA224_HMAC");
        addKeyType(47L, "CKK_SEED");
        addKeyType(48L, "CKK_GOSTR3410");
        addKeyType(49L, "CKK_GOSTR3411");
        addKeyType(50L, "CKK_GOST28147");
        addKeyType(2147483648L, "CKK_VENDOR_DEFINED");
        addKeyType(2147483426L, "*");
        addAttribute(0L, "CKA_CLASS");
        addAttribute(1L, "CKA_TOKEN");
        addAttribute(2L, "CKA_PRIVATE");
        addAttribute(3L, "CKA_LABEL");
        addAttribute(16L, "CKA_APPLICATION");
        addAttribute(17L, "CKA_VALUE");
        addAttribute(18L, "CKA_OBJECT_ID");
        addAttribute(128L, "CKA_CERTIFICATE_TYPE");
        addAttribute(129L, "CKA_ISSUER");
        addAttribute(130L, "CKA_SERIAL_NUMBER");
        addAttribute(131L, "CKA_AC_ISSUER");
        addAttribute(132L, "CKA_OWNER");
        addAttribute(133L, "CKA_ATTR_TYPES");
        addAttribute(134L, "CKA_TRUSTED");
        addAttribute(135L, "CKA_CERTIFICATE_CATEGORY");
        addAttribute(136L, "CKA_JAVA_MIDP_SECURITY_DOMAIN");
        addAttribute(137L, "CKA_URL");
        addAttribute(138L, "CKA_HASH_OF_SUBJECT_PUBLIC_KEY");
        addAttribute(139L, "CKA_HASH_OF_ISSUER_PUBLIC_KEY");
        addAttribute(140L, "CKA_NAME_HASH_ALGORITHM");
        addAttribute(144L, "CKA_CHECK_VALUE");
        addAttribute(256L, "CKA_KEY_TYPE");
        addAttribute(257L, "CKA_SUBJECT");
        addAttribute(258L, "CKA_ID");
        addAttribute(259L, "CKA_SENSITIVE");
        addAttribute(260L, "CKA_ENCRYPT");
        addAttribute(261L, "CKA_DECRYPT");
        addAttribute(262L, "CKA_WRAP");
        addAttribute(263L, "CKA_UNWRAP");
        addAttribute(264L, "CKA_SIGN");
        addAttribute(265L, "CKA_SIGN_RECOVER");
        addAttribute(266L, "CKA_VERIFY");
        addAttribute(267L, "CKA_VERIFY_RECOVER");
        addAttribute(268L, "CKA_DERIVE");
        addAttribute(272L, "CKA_START_DATE");
        addAttribute(273L, "CKA_END_DATE");
        addAttribute(288L, "CKA_MODULUS");
        addAttribute(289L, "CKA_MODULUS_BITS");
        addAttribute(290L, "CKA_PUBLIC_EXPONENT");
        addAttribute(291L, "CKA_PRIVATE_EXPONENT");
        addAttribute(292L, "CKA_PRIME_1");
        addAttribute(293L, "CKA_PRIME_2");
        addAttribute(294L, "CKA_EXPONENT_1");
        addAttribute(295L, "CKA_EXPONENT_2");
        addAttribute(296L, "CKA_COEFFICIENT");
        addAttribute(297L, "CKA_PUBLIC_KEY_INFO");
        addAttribute(304L, "CKA_PRIME");
        addAttribute(305L, "CKA_SUBPRIME");
        addAttribute(306L, "CKA_BASE");
        addAttribute(307L, "CKA_PRIME_BITS");
        addAttribute(308L, "CKA_SUB_PRIME_BITS");
        addAttribute(352L, "CKA_VALUE_BITS");
        addAttribute(353L, "CKA_VALUE_LEN");
        addAttribute(354L, "CKA_EXTRACTABLE");
        addAttribute(355L, "CKA_LOCAL");
        addAttribute(356L, "CKA_NEVER_EXTRACTABLE");
        addAttribute(357L, "CKA_ALWAYS_SENSITIVE");
        addAttribute(358L, "CKA_KEY_GEN_MECHANISM");
        addAttribute(368L, "CKA_MODIFIABLE");
        addAttribute(369L, "CKA_COPYABLE");
        addAttribute(370L, "CKA_DESTROYABLE");
        addAttribute(384L, "CKA_EC_PARAMS");
        addAttribute(385L, "CKA_EC_POINT");
        addAttribute(512L, "CKA_SECONDARY_AUTH");
        addAttribute(513L, "CKA_AUTH_PIN_FLAGS");
        addAttribute(514L, "CKA_ALWAYS_AUTHENTICATE");
        addAttribute(528L, "CKA_WRAP_WITH_TRUSTED");
        addAttribute(1073742353L, "CKA_WRAP_TEMPLATE");
        addAttribute(1073742354L, "CKA_UNWRAP_TEMPLATE");
        addAttribute(1073742355L, "CKA_DERIVE_TEMPLATE");
        addAttribute(544L, "CKA_OTP_FORMAT");
        addAttribute(545L, "CKA_OTP_LENGTH");
        addAttribute(546L, "CKA_OTP_TIME_INTERVAL");
        addAttribute(547L, "CKA_OTP_USER_FRIENDLY_MODE");
        addAttribute(548L, "CKA_OTP_CHALLENGE_REQUIREMENT");
        addAttribute(549L, "CKA_OTP_TIME_REQUIREMENT");
        addAttribute(550L, "CKA_OTP_COUNTER_REQUIREMENT");
        addAttribute(551L, "CKA_OTP_PIN_REQUIREMENT");
        addAttribute(558L, "CKA_OTP_COUNTER");
        addAttribute(559L, "CKA_OTP_TIME");
        addAttribute(554L, "CKA_OTP_USER_IDENTIFIER");
        addAttribute(555L, "CKA_OTP_SERVICE_IDENTIFIER");
        addAttribute(556L, "CKA_OTP_SERVICE_LOGO");
        addAttribute(557L, "CKA_OTP_SERVICE_LOGO_TYPE");
        addAttribute(592L, "CKA_GOSTR3410_PARAMS");
        addAttribute(593L, "CKA_GOSTR3411_PARAMS");
        addAttribute(594L, "CKA_GOST28147_PARAMS");
        addAttribute(768L, "CKA_HW_FEATURE_TYPE");
        addAttribute(769L, "CKA_RESET_ON_INIT");
        addAttribute(770L, "CKA_HAS_RESET");
        addAttribute(1024L, "CKA_PIXEL_X");
        addAttribute(1025L, "CKA_PIXEL_Y");
        addAttribute(1026L, "CKA_RESOLUTION");
        addAttribute(1027L, "CKA_CHAR_ROWS");
        addAttribute(1028L, "CKA_CHAR_COLUMNS");
        addAttribute(1029L, "CKA_COLOR");
        addAttribute(1030L, "CKA_BITS_PER_PIXEL");
        addAttribute(1152L, "CKA_CHAR_SETS");
        addAttribute(1153L, "CKA_ENCODING_METHODS");
        addAttribute(1154L, "CKA_MIME_TYPES");
        addAttribute(1280L, "CKA_MECHANISM_TYPE");
        addAttribute(1281L, "CKA_REQUIRED_CMS_ATTRIBUTES");
        addAttribute(1282L, "CKA_DEFAULT_CMS_ATTRIBUTES");
        addAttribute(1283L, "CKA_SUPPORTED_CMS_ATTRIBUTES");
        addAttribute(1073743360L, "CKA_ALLOWED_MECHANISMS");
        addAttribute(2147483648L, "CKA_VENDOR_DEFINED");
        addAttribute(3584088832L, "CKA_NETSCAPE_DB");
        addAttribute(3461571416L, "CKA_NETSCAPE_TRUST_SERVER_AUTH");
        addAttribute(3461571417L, "CKA_NETSCAPE_TRUST_CLIENT_AUTH");
        addAttribute(3461571418L, "CKA_NETSCAPE_TRUST_CODE_SIGNING");
        addAttribute(3461571419L, "CKA_NETSCAPE_TRUST_EMAIL_PROTECTION");
        addAttribute(3461571508L, "CKA_NETSCAPE_CERT_SHA1_HASH");
        addAttribute(3461571509L, "CKA_NETSCAPE_CERT_MD5_HASH");
        addObjectClass(0L, "CKO_DATA");
        addObjectClass(1L, "CKO_CERTIFICATE");
        addObjectClass(2L, "CKO_PUBLIC_KEY");
        addObjectClass(3L, "CKO_PRIVATE_KEY");
        addObjectClass(4L, "CKO_SECRET_KEY");
        addObjectClass(5L, "CKO_HW_FEATURE");
        addObjectClass(6L, "CKO_DOMAIN_PARAMETERS");
        addObjectClass(2147483648L, "CKO_VENDOR_DEFINED");
        addObjectClass(2147483427L, "*");
        addMGF(1L, "CKG_MGF1_SHA1");
        addMGF(2L, "CKG_MGF1_SHA256");
        addMGF(3L, "CKG_MGF1_SHA384");
        addMGF(4L, "CKG_MGF1_SHA512");
        addMGF(5L, "CKG_MGF1_SHA224");
    }
    
    private static class Flags
    {
        private final long[] flagIds;
        private final String[] flagNames;
        
        Flags(final long[] flagIds, final String[] flagNames) {
            if (flagIds.length != flagNames.length) {
                throw new AssertionError((Object)"Array lengths do not match");
            }
            this.flagIds = flagIds;
            this.flagNames = flagNames;
        }
        
        String toString(final long n) {
            final StringBuilder sb = new StringBuilder();
            int n2 = 1;
            for (int i = 0; i < this.flagIds.length; ++i) {
                if ((n & this.flagIds[i]) != 0x0L) {
                    if (n2 == 0) {
                        sb.append(" | ");
                    }
                    sb.append(this.flagNames[i]);
                    n2 = 0;
                }
            }
            return sb.toString();
        }
    }
}
