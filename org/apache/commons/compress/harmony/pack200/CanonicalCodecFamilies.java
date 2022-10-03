package org.apache.commons.compress.harmony.pack200;

public class CanonicalCodecFamilies
{
    public static BHSDCodec[] nonDeltaUnsignedCodecs1;
    public static BHSDCodec[] nonDeltaUnsignedCodecs2;
    public static BHSDCodec[] nonDeltaUnsignedCodecs3;
    public static BHSDCodec[] nonDeltaUnsignedCodecs4;
    public static BHSDCodec[] nonDeltaUnsignedCodecs5;
    public static BHSDCodec[] deltaUnsignedCodecs1;
    public static BHSDCodec[] deltaUnsignedCodecs2;
    public static BHSDCodec[] deltaUnsignedCodecs3;
    public static BHSDCodec[] deltaUnsignedCodecs4;
    public static BHSDCodec[] deltaUnsignedCodecs5;
    public static BHSDCodec[] deltaSignedCodecs1;
    public static BHSDCodec[] deltaSignedCodecs2;
    public static BHSDCodec[] deltaSignedCodecs3;
    public static BHSDCodec[] deltaSignedCodecs4;
    public static BHSDCodec[] deltaSignedCodecs5;
    public static BHSDCodec[] deltaDoubleSignedCodecs1;
    public static BHSDCodec[] nonDeltaSignedCodecs1;
    public static BHSDCodec[] nonDeltaSignedCodecs2;
    public static BHSDCodec[] nonDeltaDoubleSignedCodecs1;
    
    static {
        CanonicalCodecFamilies.nonDeltaUnsignedCodecs1 = new BHSDCodec[] { CodecEncoding.getCanonicalCodec(5), CodecEncoding.getCanonicalCodec(9), CodecEncoding.getCanonicalCodec(13) };
        CanonicalCodecFamilies.nonDeltaUnsignedCodecs2 = new BHSDCodec[] { CodecEncoding.getCanonicalCodec(17), CodecEncoding.getCanonicalCodec(20), CodecEncoding.getCanonicalCodec(23), CodecEncoding.getCanonicalCodec(26), CodecEncoding.getCanonicalCodec(29) };
        CanonicalCodecFamilies.nonDeltaUnsignedCodecs3 = new BHSDCodec[] { CodecEncoding.getCanonicalCodec(47), CodecEncoding.getCanonicalCodec(48), CodecEncoding.getCanonicalCodec(49), CodecEncoding.getCanonicalCodec(50), CodecEncoding.getCanonicalCodec(51) };
        CanonicalCodecFamilies.nonDeltaUnsignedCodecs4 = new BHSDCodec[] { CodecEncoding.getCanonicalCodec(70), CodecEncoding.getCanonicalCodec(71), CodecEncoding.getCanonicalCodec(72), CodecEncoding.getCanonicalCodec(73), CodecEncoding.getCanonicalCodec(74) };
        CanonicalCodecFamilies.nonDeltaUnsignedCodecs5 = new BHSDCodec[] { CodecEncoding.getCanonicalCodec(93), CodecEncoding.getCanonicalCodec(94), CodecEncoding.getCanonicalCodec(95), CodecEncoding.getCanonicalCodec(96), CodecEncoding.getCanonicalCodec(97) };
        CanonicalCodecFamilies.deltaUnsignedCodecs1 = new BHSDCodec[] { CodecEncoding.getCanonicalCodec(3), CodecEncoding.getCanonicalCodec(7), CodecEncoding.getCanonicalCodec(11), CodecEncoding.getCanonicalCodec(15) };
        CanonicalCodecFamilies.deltaUnsignedCodecs2 = new BHSDCodec[] { CodecEncoding.getCanonicalCodec(32), CodecEncoding.getCanonicalCodec(35), CodecEncoding.getCanonicalCodec(38), CodecEncoding.getCanonicalCodec(41), CodecEncoding.getCanonicalCodec(44) };
        CanonicalCodecFamilies.deltaUnsignedCodecs3 = new BHSDCodec[] { CodecEncoding.getCanonicalCodec(52), CodecEncoding.getCanonicalCodec(54), CodecEncoding.getCanonicalCodec(56), CodecEncoding.getCanonicalCodec(58), CodecEncoding.getCanonicalCodec(60), CodecEncoding.getCanonicalCodec(62), CodecEncoding.getCanonicalCodec(64), CodecEncoding.getCanonicalCodec(66), CodecEncoding.getCanonicalCodec(68) };
        CanonicalCodecFamilies.deltaUnsignedCodecs4 = new BHSDCodec[] { CodecEncoding.getCanonicalCodec(75), CodecEncoding.getCanonicalCodec(77), CodecEncoding.getCanonicalCodec(79), CodecEncoding.getCanonicalCodec(81), CodecEncoding.getCanonicalCodec(83), CodecEncoding.getCanonicalCodec(85), CodecEncoding.getCanonicalCodec(87), CodecEncoding.getCanonicalCodec(89), CodecEncoding.getCanonicalCodec(91) };
        CanonicalCodecFamilies.deltaUnsignedCodecs5 = new BHSDCodec[] { CodecEncoding.getCanonicalCodec(98), CodecEncoding.getCanonicalCodec(100), CodecEncoding.getCanonicalCodec(102), CodecEncoding.getCanonicalCodec(104), CodecEncoding.getCanonicalCodec(106), CodecEncoding.getCanonicalCodec(108), CodecEncoding.getCanonicalCodec(110), CodecEncoding.getCanonicalCodec(112), CodecEncoding.getCanonicalCodec(114) };
        CanonicalCodecFamilies.deltaSignedCodecs1 = new BHSDCodec[] { CodecEncoding.getCanonicalCodec(4), CodecEncoding.getCanonicalCodec(8), CodecEncoding.getCanonicalCodec(12), CodecEncoding.getCanonicalCodec(16) };
        CanonicalCodecFamilies.deltaSignedCodecs2 = new BHSDCodec[] { CodecEncoding.getCanonicalCodec(33), CodecEncoding.getCanonicalCodec(36), CodecEncoding.getCanonicalCodec(39), CodecEncoding.getCanonicalCodec(42), CodecEncoding.getCanonicalCodec(45) };
        CanonicalCodecFamilies.deltaSignedCodecs3 = new BHSDCodec[] { CodecEncoding.getCanonicalCodec(53), CodecEncoding.getCanonicalCodec(55), CodecEncoding.getCanonicalCodec(57), CodecEncoding.getCanonicalCodec(59), CodecEncoding.getCanonicalCodec(61), CodecEncoding.getCanonicalCodec(63), CodecEncoding.getCanonicalCodec(65), CodecEncoding.getCanonicalCodec(67), CodecEncoding.getCanonicalCodec(69) };
        CanonicalCodecFamilies.deltaSignedCodecs4 = new BHSDCodec[] { CodecEncoding.getCanonicalCodec(76), CodecEncoding.getCanonicalCodec(78), CodecEncoding.getCanonicalCodec(80), CodecEncoding.getCanonicalCodec(82), CodecEncoding.getCanonicalCodec(84), CodecEncoding.getCanonicalCodec(86), CodecEncoding.getCanonicalCodec(88), CodecEncoding.getCanonicalCodec(90), CodecEncoding.getCanonicalCodec(92) };
        CanonicalCodecFamilies.deltaSignedCodecs5 = new BHSDCodec[] { CodecEncoding.getCanonicalCodec(99), CodecEncoding.getCanonicalCodec(101), CodecEncoding.getCanonicalCodec(103), CodecEncoding.getCanonicalCodec(105), CodecEncoding.getCanonicalCodec(107), CodecEncoding.getCanonicalCodec(109), CodecEncoding.getCanonicalCodec(111), CodecEncoding.getCanonicalCodec(113), CodecEncoding.getCanonicalCodec(115) };
        CanonicalCodecFamilies.deltaDoubleSignedCodecs1 = new BHSDCodec[] { CodecEncoding.getCanonicalCodec(34), CodecEncoding.getCanonicalCodec(37), CodecEncoding.getCanonicalCodec(40), CodecEncoding.getCanonicalCodec(43), CodecEncoding.getCanonicalCodec(46) };
        CanonicalCodecFamilies.nonDeltaSignedCodecs1 = new BHSDCodec[] { CodecEncoding.getCanonicalCodec(2), CodecEncoding.getCanonicalCodec(6), CodecEncoding.getCanonicalCodec(10), CodecEncoding.getCanonicalCodec(14) };
        CanonicalCodecFamilies.nonDeltaSignedCodecs2 = new BHSDCodec[] { CodecEncoding.getCanonicalCodec(18), CodecEncoding.getCanonicalCodec(21), CodecEncoding.getCanonicalCodec(24), CodecEncoding.getCanonicalCodec(27), CodecEncoding.getCanonicalCodec(30) };
        CanonicalCodecFamilies.nonDeltaDoubleSignedCodecs1 = new BHSDCodec[] { CodecEncoding.getCanonicalCodec(19), CodecEncoding.getCanonicalCodec(22), CodecEncoding.getCanonicalCodec(25), CodecEncoding.getCanonicalCodec(28), CodecEncoding.getCanonicalCodec(31) };
    }
}
