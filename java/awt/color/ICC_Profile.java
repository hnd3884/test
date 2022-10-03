package java.awt.color;

import java.io.ObjectStreamException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.StringTokenizer;
import sun.java2d.cmm.PCMM;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import sun.misc.IOUtils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.security.AccessController;
import java.io.IOException;
import java.security.PrivilegedAction;
import sun.java2d.cmm.ProfileDataVerifier;
import sun.java2d.cmm.CMSManager;
import sun.java2d.cmm.ProfileDeferralMgr;
import sun.java2d.cmm.ProfileActivator;
import sun.java2d.cmm.ProfileDeferralInfo;
import sun.java2d.cmm.Profile;
import java.io.Serializable;

public class ICC_Profile implements Serializable
{
    private static final long serialVersionUID = -3938515861990936766L;
    private transient Profile cmmProfile;
    private transient ProfileDeferralInfo deferralInfo;
    private transient ProfileActivator profileActivator;
    private static ICC_Profile sRGBprofile;
    private static ICC_Profile XYZprofile;
    private static ICC_Profile PYCCprofile;
    private static ICC_Profile GRAYprofile;
    private static ICC_Profile LINEAR_RGBprofile;
    public static final int CLASS_INPUT = 0;
    public static final int CLASS_DISPLAY = 1;
    public static final int CLASS_OUTPUT = 2;
    public static final int CLASS_DEVICELINK = 3;
    public static final int CLASS_COLORSPACECONVERSION = 4;
    public static final int CLASS_ABSTRACT = 5;
    public static final int CLASS_NAMEDCOLOR = 6;
    public static final int icSigXYZData = 1482250784;
    public static final int icSigLabData = 1281450528;
    public static final int icSigLuvData = 1282766368;
    public static final int icSigYCbCrData = 1497588338;
    public static final int icSigYxyData = 1501067552;
    public static final int icSigRgbData = 1380401696;
    public static final int icSigGrayData = 1196573017;
    public static final int icSigHsvData = 1213421088;
    public static final int icSigHlsData = 1212961568;
    public static final int icSigCmykData = 1129142603;
    public static final int icSigCmyData = 1129142560;
    public static final int icSigSpace2CLR = 843271250;
    public static final int icSigSpace3CLR = 860048466;
    public static final int icSigSpace4CLR = 876825682;
    public static final int icSigSpace5CLR = 893602898;
    public static final int icSigSpace6CLR = 910380114;
    public static final int icSigSpace7CLR = 927157330;
    public static final int icSigSpace8CLR = 943934546;
    public static final int icSigSpace9CLR = 960711762;
    public static final int icSigSpaceACLR = 1094929490;
    public static final int icSigSpaceBCLR = 1111706706;
    public static final int icSigSpaceCCLR = 1128483922;
    public static final int icSigSpaceDCLR = 1145261138;
    public static final int icSigSpaceECLR = 1162038354;
    public static final int icSigSpaceFCLR = 1178815570;
    public static final int icSigInputClass = 1935896178;
    public static final int icSigDisplayClass = 1835955314;
    public static final int icSigOutputClass = 1886549106;
    public static final int icSigLinkClass = 1818848875;
    public static final int icSigAbstractClass = 1633842036;
    public static final int icSigColorSpaceClass = 1936744803;
    public static final int icSigNamedColorClass = 1852662636;
    public static final int icPerceptual = 0;
    public static final int icRelativeColorimetric = 1;
    public static final int icMediaRelativeColorimetric = 1;
    public static final int icSaturation = 2;
    public static final int icAbsoluteColorimetric = 3;
    public static final int icICCAbsoluteColorimetric = 3;
    public static final int icSigHead = 1751474532;
    public static final int icSigAToB0Tag = 1093812784;
    public static final int icSigAToB1Tag = 1093812785;
    public static final int icSigAToB2Tag = 1093812786;
    public static final int icSigBlueColorantTag = 1649957210;
    public static final int icSigBlueMatrixColumnTag = 1649957210;
    public static final int icSigBlueTRCTag = 1649693251;
    public static final int icSigBToA0Tag = 1110589744;
    public static final int icSigBToA1Tag = 1110589745;
    public static final int icSigBToA2Tag = 1110589746;
    public static final int icSigCalibrationDateTimeTag = 1667329140;
    public static final int icSigCharTargetTag = 1952543335;
    public static final int icSigCopyrightTag = 1668313716;
    public static final int icSigCrdInfoTag = 1668441193;
    public static final int icSigDeviceMfgDescTag = 1684893284;
    public static final int icSigDeviceModelDescTag = 1684890724;
    public static final int icSigDeviceSettingsTag = 1684371059;
    public static final int icSigGamutTag = 1734438260;
    public static final int icSigGrayTRCTag = 1800688195;
    public static final int icSigGreenColorantTag = 1733843290;
    public static final int icSigGreenMatrixColumnTag = 1733843290;
    public static final int icSigGreenTRCTag = 1733579331;
    public static final int icSigLuminanceTag = 1819635049;
    public static final int icSigMeasurementTag = 1835360627;
    public static final int icSigMediaBlackPointTag = 1651208308;
    public static final int icSigMediaWhitePointTag = 2004119668;
    public static final int icSigNamedColor2Tag = 1852009522;
    public static final int icSigOutputResponseTag = 1919251312;
    public static final int icSigPreview0Tag = 1886545200;
    public static final int icSigPreview1Tag = 1886545201;
    public static final int icSigPreview2Tag = 1886545202;
    public static final int icSigProfileDescriptionTag = 1684370275;
    public static final int icSigProfileSequenceDescTag = 1886610801;
    public static final int icSigPs2CRD0Tag = 1886610480;
    public static final int icSigPs2CRD1Tag = 1886610481;
    public static final int icSigPs2CRD2Tag = 1886610482;
    public static final int icSigPs2CRD3Tag = 1886610483;
    public static final int icSigPs2CSATag = 1886597747;
    public static final int icSigPs2RenderingIntentTag = 1886597737;
    public static final int icSigRedColorantTag = 1918392666;
    public static final int icSigRedMatrixColumnTag = 1918392666;
    public static final int icSigRedTRCTag = 1918128707;
    public static final int icSigScreeningDescTag = 1935897188;
    public static final int icSigScreeningTag = 1935897198;
    public static final int icSigTechnologyTag = 1952801640;
    public static final int icSigUcrBgTag = 1650877472;
    public static final int icSigViewingCondDescTag = 1987405156;
    public static final int icSigViewingConditionsTag = 1986618743;
    public static final int icSigChromaticityTag = 1667789421;
    public static final int icSigChromaticAdaptationTag = 1667785060;
    public static final int icSigColorantOrderTag = 1668051567;
    public static final int icSigColorantTableTag = 1668051572;
    public static final int icHdrSize = 0;
    public static final int icHdrCmmId = 4;
    public static final int icHdrVersion = 8;
    public static final int icHdrDeviceClass = 12;
    public static final int icHdrColorSpace = 16;
    public static final int icHdrPcs = 20;
    public static final int icHdrDate = 24;
    public static final int icHdrMagic = 36;
    public static final int icHdrPlatform = 40;
    public static final int icHdrFlags = 44;
    public static final int icHdrManufacturer = 48;
    public static final int icHdrModel = 52;
    public static final int icHdrAttributes = 56;
    public static final int icHdrRenderingIntent = 64;
    public static final int icHdrIlluminant = 68;
    public static final int icHdrCreator = 80;
    public static final int icHdrProfileID = 84;
    public static final int icTagType = 0;
    public static final int icTagReserved = 4;
    public static final int icCurveCount = 8;
    public static final int icCurveData = 12;
    public static final int icXYZNumberX = 8;
    private int iccProfileSerializedDataVersion;
    private transient ICC_Profile resolvedDeserializedProfile;
    
    ICC_Profile(final Profile cmmProfile) {
        this.iccProfileSerializedDataVersion = 1;
        this.cmmProfile = cmmProfile;
    }
    
    ICC_Profile(final ProfileDeferralInfo deferralInfo) {
        this.iccProfileSerializedDataVersion = 1;
        this.deferralInfo = deferralInfo;
        ProfileDeferralMgr.registerDeferral(this.profileActivator = new ProfileActivator() {
            @Override
            public void activate() throws ProfileDataException {
                ICC_Profile.this.activateDeferredProfile();
            }
        });
    }
    
    @Override
    protected void finalize() {
        if (this.cmmProfile != null) {
            CMSManager.getModule().freeProfile(this.cmmProfile);
        }
        else if (this.profileActivator != null) {
            ProfileDeferralMgr.unregisterDeferral(this.profileActivator);
        }
    }
    
    public static ICC_Profile getInstance(final byte[] array) {
        if (ProfileDeferralMgr.deferring) {
            ProfileDeferralMgr.activateProfiles();
        }
        ProfileDataVerifier.verify(array);
        Profile loadProfile;
        try {
            loadProfile = CMSManager.getModule().loadProfile(array);
        }
        catch (final CMMException ex) {
            throw new IllegalArgumentException("Invalid ICC Profile Data");
        }
        ICC_Profile icc_Profile;
        try {
            if (getColorSpaceType(loadProfile) == 6 && getData(loadProfile, 2004119668) != null && getData(loadProfile, 1800688195) != null) {
                icc_Profile = new ICC_ProfileGray(loadProfile);
            }
            else if (getColorSpaceType(loadProfile) == 5 && getData(loadProfile, 2004119668) != null && getData(loadProfile, 1918392666) != null && getData(loadProfile, 1733843290) != null && getData(loadProfile, 1649957210) != null && getData(loadProfile, 1918128707) != null && getData(loadProfile, 1733579331) != null && getData(loadProfile, 1649693251) != null) {
                icc_Profile = new ICC_ProfileRGB(loadProfile);
            }
            else {
                icc_Profile = new ICC_Profile(loadProfile);
            }
        }
        catch (final CMMException ex2) {
            icc_Profile = new ICC_Profile(loadProfile);
        }
        return icc_Profile;
    }
    
    public static ICC_Profile getInstance(final int n) {
        ICC_Profile icc_Profile = null;
        switch (n) {
            case 1000: {
                synchronized (ICC_Profile.class) {
                    if (ICC_Profile.sRGBprofile == null) {
                        ICC_Profile.sRGBprofile = getDeferredInstance(new ProfileDeferralInfo("sRGB.pf", 5, 3, 1));
                    }
                    icc_Profile = ICC_Profile.sRGBprofile;
                }
                break;
            }
            case 1001: {
                synchronized (ICC_Profile.class) {
                    if (ICC_Profile.XYZprofile == null) {
                        ICC_Profile.XYZprofile = getDeferredInstance(new ProfileDeferralInfo("CIEXYZ.pf", 0, 3, 1));
                    }
                    icc_Profile = ICC_Profile.XYZprofile;
                }
                break;
            }
            case 1002: {
                synchronized (ICC_Profile.class) {
                    if (ICC_Profile.PYCCprofile == null) {
                        if (!standardProfileExists("PYCC.pf")) {
                            throw new IllegalArgumentException("Can't load standard profile: PYCC.pf");
                        }
                        ICC_Profile.PYCCprofile = getDeferredInstance(new ProfileDeferralInfo("PYCC.pf", 13, 3, 1));
                    }
                    icc_Profile = ICC_Profile.PYCCprofile;
                }
                break;
            }
            case 1003: {
                synchronized (ICC_Profile.class) {
                    if (ICC_Profile.GRAYprofile == null) {
                        ICC_Profile.GRAYprofile = getDeferredInstance(new ProfileDeferralInfo("GRAY.pf", 6, 1, 1));
                    }
                    icc_Profile = ICC_Profile.GRAYprofile;
                }
                break;
            }
            case 1004: {
                synchronized (ICC_Profile.class) {
                    if (ICC_Profile.LINEAR_RGBprofile == null) {
                        ICC_Profile.LINEAR_RGBprofile = getDeferredInstance(new ProfileDeferralInfo("LINEAR_RGB.pf", 5, 3, 1));
                    }
                    icc_Profile = ICC_Profile.LINEAR_RGBprofile;
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown color space");
            }
        }
        return icc_Profile;
    }
    
    private static ICC_Profile getStandardProfile(final String s) {
        return AccessController.doPrivileged((PrivilegedAction<ICC_Profile>)new PrivilegedAction<ICC_Profile>() {
            @Override
            public ICC_Profile run() {
                ICC_Profile instance;
                try {
                    instance = ICC_Profile.getInstance(s);
                }
                catch (final IOException ex) {
                    throw new IllegalArgumentException("Can't load standard profile: " + s);
                }
                return instance;
            }
        });
    }
    
    public static ICC_Profile getInstance(final String s) throws IOException {
        FileInputStream fileInputStream = null;
        final File profileFile = getProfileFile(s);
        if (profileFile != null) {
            fileInputStream = new FileInputStream(profileFile);
        }
        if (fileInputStream == null) {
            throw new IOException("Cannot open file " + s);
        }
        final ICC_Profile instance = getInstance(fileInputStream);
        fileInputStream.close();
        return instance;
    }
    
    public static ICC_Profile getInstance(final InputStream inputStream) throws IOException {
        if (inputStream instanceof ProfileDeferralInfo) {
            return getDeferredInstance((ProfileDeferralInfo)inputStream);
        }
        final byte[] profileDataFromStream;
        if ((profileDataFromStream = getProfileDataFromStream(inputStream)) == null) {
            throw new IllegalArgumentException("Invalid ICC Profile Data");
        }
        return getInstance(profileDataFromStream);
    }
    
    static byte[] getProfileDataFromStream(final InputStream inputStream) throws IOException {
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        bufferedInputStream.mark(128);
        final byte[] nBytes = IOUtils.readNBytes(bufferedInputStream, 128);
        if (nBytes[36] != 97 || nBytes[37] != 99 || nBytes[38] != 115 || nBytes[39] != 112) {
            return null;
        }
        final int n = (nBytes[0] & 0xFF) << 24 | (nBytes[1] & 0xFF) << 16 | (nBytes[2] & 0xFF) << 8 | (nBytes[3] & 0xFF);
        bufferedInputStream.reset();
        try {
            return IOUtils.readNBytes(bufferedInputStream, n);
        }
        catch (final OutOfMemoryError outOfMemoryError) {
            throw new IOException("Color profile is too big");
        }
    }
    
    static ICC_Profile getDeferredInstance(final ProfileDeferralInfo profileDeferralInfo) {
        if (!ProfileDeferralMgr.deferring) {
            return getStandardProfile(profileDeferralInfo.filename);
        }
        if (profileDeferralInfo.colorSpaceType == 5) {
            return new ICC_ProfileRGB(profileDeferralInfo);
        }
        if (profileDeferralInfo.colorSpaceType == 6) {
            return new ICC_ProfileGray(profileDeferralInfo);
        }
        return new ICC_Profile(profileDeferralInfo);
    }
    
    void activateDeferredProfile() throws ProfileDataException {
        final String filename = this.deferralInfo.filename;
        this.profileActivator = null;
        this.deferralInfo = null;
        final FileInputStream fileInputStream;
        if ((fileInputStream = AccessController.doPrivileged((PrivilegedAction<FileInputStream>)new PrivilegedAction<FileInputStream>() {
            @Override
            public FileInputStream run() {
                final File access$000 = getStandardProfileFile(filename);
                if (access$000 != null) {
                    try {
                        return new FileInputStream(access$000);
                    }
                    catch (final FileNotFoundException ex) {}
                }
                return null;
            }
        })) == null) {
            throw new ProfileDataException("Cannot open file " + filename);
        }
        byte[] profileDataFromStream;
        try {
            profileDataFromStream = getProfileDataFromStream(fileInputStream);
            fileInputStream.close();
        }
        catch (final IOException ex) {
            final ProfileDataException ex2 = new ProfileDataException("Invalid ICC Profile Data" + filename);
            ex2.initCause(ex);
            throw ex2;
        }
        if (profileDataFromStream == null) {
            throw new ProfileDataException("Invalid ICC Profile Data" + filename);
        }
        try {
            this.cmmProfile = CMSManager.getModule().loadProfile(profileDataFromStream);
        }
        catch (final CMMException ex3) {
            final ProfileDataException ex4 = new ProfileDataException("Invalid ICC Profile Data" + filename);
            ex4.initCause(ex3);
            throw ex4;
        }
    }
    
    public int getMajorVersion() {
        return this.getData(1751474532)[8];
    }
    
    public int getMinorVersion() {
        return this.getData(1751474532)[9];
    }
    
    public int getProfileClass() {
        if (this.deferralInfo != null) {
            return this.deferralInfo.profileClass;
        }
        int n = 0;
        switch (intFromBigEndian(this.getData(1751474532), 12)) {
            case 1935896178: {
                n = 0;
                break;
            }
            case 1835955314: {
                n = 1;
                break;
            }
            case 1886549106: {
                n = 2;
                break;
            }
            case 1818848875: {
                n = 3;
                break;
            }
            case 1936744803: {
                n = 4;
                break;
            }
            case 1633842036: {
                n = 5;
                break;
            }
            case 1852662636: {
                n = 6;
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown profile class");
            }
        }
        return n;
    }
    
    public int getColorSpaceType() {
        if (this.deferralInfo != null) {
            return this.deferralInfo.colorSpaceType;
        }
        return getColorSpaceType(this.cmmProfile);
    }
    
    static int getColorSpaceType(final Profile profile) {
        return iccCStoJCS(intFromBigEndian(getData(profile, 1751474532), 16));
    }
    
    public int getPCSType() {
        if (ProfileDeferralMgr.deferring) {
            ProfileDeferralMgr.activateProfiles();
        }
        return getPCSType(this.cmmProfile);
    }
    
    static int getPCSType(final Profile profile) {
        return iccCStoJCS(intFromBigEndian(getData(profile, 1751474532), 20));
    }
    
    public void write(final String s) throws IOException {
        final byte[] data = this.getData();
        final FileOutputStream fileOutputStream = new FileOutputStream(s);
        fileOutputStream.write(data);
        fileOutputStream.close();
    }
    
    public void write(final OutputStream outputStream) throws IOException {
        outputStream.write(this.getData());
    }
    
    public byte[] getData() {
        if (ProfileDeferralMgr.deferring) {
            ProfileDeferralMgr.activateProfiles();
        }
        final PCMM module = CMSManager.getModule();
        final byte[] array = new byte[module.getProfileSize(this.cmmProfile)];
        module.getProfileData(this.cmmProfile, array);
        return array;
    }
    
    public byte[] getData(final int n) {
        if (ProfileDeferralMgr.deferring) {
            ProfileDeferralMgr.activateProfiles();
        }
        return getData(this.cmmProfile, n);
    }
    
    static byte[] getData(final Profile profile, final int n) {
        byte[] array;
        try {
            final PCMM module = CMSManager.getModule();
            array = new byte[module.getTagSize(profile, n)];
            module.getTagData(profile, n, array);
        }
        catch (final CMMException ex) {
            array = null;
        }
        return array;
    }
    
    public void setData(final int n, final byte[] array) {
        if (ProfileDeferralMgr.deferring) {
            ProfileDeferralMgr.activateProfiles();
        }
        CMSManager.getModule().setTagData(this.cmmProfile, n, array);
    }
    
    void setRenderingIntent(final int n) {
        final byte[] data = this.getData(1751474532);
        intToBigEndian(n, data, 64);
        this.setData(1751474532, data);
    }
    
    int getRenderingIntent() {
        return 0xFFFF & intFromBigEndian(this.getData(1751474532), 64);
    }
    
    public int getNumComponents() {
        if (this.deferralInfo != null) {
            return this.deferralInfo.numComponents;
        }
        int n = 0;
        switch (intFromBigEndian(this.getData(1751474532), 16)) {
            case 1196573017: {
                n = 1;
                break;
            }
            case 843271250: {
                n = 2;
                break;
            }
            case 860048466:
            case 1129142560:
            case 1212961568:
            case 1213421088:
            case 1281450528:
            case 1282766368:
            case 1380401696:
            case 1482250784:
            case 1497588338:
            case 1501067552: {
                n = 3;
                break;
            }
            case 876825682:
            case 1129142603: {
                n = 4;
                break;
            }
            case 893602898: {
                n = 5;
                break;
            }
            case 910380114: {
                n = 6;
                break;
            }
            case 927157330: {
                n = 7;
                break;
            }
            case 943934546: {
                n = 8;
                break;
            }
            case 960711762: {
                n = 9;
                break;
            }
            case 1094929490: {
                n = 10;
                break;
            }
            case 1111706706: {
                n = 11;
                break;
            }
            case 1128483922: {
                n = 12;
                break;
            }
            case 1145261138: {
                n = 13;
                break;
            }
            case 1162038354: {
                n = 14;
                break;
            }
            case 1178815570: {
                n = 15;
                break;
            }
            default: {
                throw new ProfileDataException("invalid ICC color space");
            }
        }
        return n;
    }
    
    float[] getMediaWhitePoint() {
        return this.getXYZTag(2004119668);
    }
    
    float[] getXYZTag(final int n) {
        final byte[] data = this.getData(n);
        final float[] array = new float[3];
        for (int i = 0, n2 = 8; i < 3; ++i, n2 += 4) {
            array[i] = intFromBigEndian(data, n2) / 65536.0f;
        }
        return array;
    }
    
    float getGamma(final int n) {
        final byte[] data = this.getData(n);
        if (intFromBigEndian(data, 8) != 1) {
            throw new ProfileDataException("TRC is not a gamma");
        }
        return (shortFromBigEndian(data, 12) & 0xFFFF) / 256.0f;
    }
    
    short[] getTRC(final int n) {
        final byte[] data = this.getData(n);
        final int intFromBigEndian = intFromBigEndian(data, 8);
        if (intFromBigEndian == 1) {
            throw new ProfileDataException("TRC is not a table");
        }
        final short[] array = new short[intFromBigEndian];
        for (int i = 0, n2 = 12; i < intFromBigEndian; ++i, n2 += 2) {
            array[i] = shortFromBigEndian(data, n2);
        }
        return array;
    }
    
    static int iccCStoJCS(final int n) {
        int n2 = 0;
        switch (n) {
            case 1482250784: {
                n2 = 0;
                break;
            }
            case 1281450528: {
                n2 = 1;
                break;
            }
            case 1282766368: {
                n2 = 2;
                break;
            }
            case 1497588338: {
                n2 = 3;
                break;
            }
            case 1501067552: {
                n2 = 4;
                break;
            }
            case 1380401696: {
                n2 = 5;
                break;
            }
            case 1196573017: {
                n2 = 6;
                break;
            }
            case 1213421088: {
                n2 = 7;
                break;
            }
            case 1212961568: {
                n2 = 8;
                break;
            }
            case 1129142603: {
                n2 = 9;
                break;
            }
            case 1129142560: {
                n2 = 11;
                break;
            }
            case 843271250: {
                n2 = 12;
                break;
            }
            case 860048466: {
                n2 = 13;
                break;
            }
            case 876825682: {
                n2 = 14;
                break;
            }
            case 893602898: {
                n2 = 15;
                break;
            }
            case 910380114: {
                n2 = 16;
                break;
            }
            case 927157330: {
                n2 = 17;
                break;
            }
            case 943934546: {
                n2 = 18;
                break;
            }
            case 960711762: {
                n2 = 19;
                break;
            }
            case 1094929490: {
                n2 = 20;
                break;
            }
            case 1111706706: {
                n2 = 21;
                break;
            }
            case 1128483922: {
                n2 = 22;
                break;
            }
            case 1145261138: {
                n2 = 23;
                break;
            }
            case 1162038354: {
                n2 = 24;
                break;
            }
            case 1178815570: {
                n2 = 25;
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown color space");
            }
        }
        return n2;
    }
    
    static int intFromBigEndian(final byte[] array, final int n) {
        return (array[n] & 0xFF) << 24 | (array[n + 1] & 0xFF) << 16 | (array[n + 2] & 0xFF) << 8 | (array[n + 3] & 0xFF);
    }
    
    static void intToBigEndian(final int n, final byte[] array, final int n2) {
        array[n2] = (byte)(n >> 24);
        array[n2 + 1] = (byte)(n >> 16);
        array[n2 + 2] = (byte)(n >> 8);
        array[n2 + 3] = (byte)n;
    }
    
    static short shortFromBigEndian(final byte[] array, final int n) {
        return (short)((array[n] & 0xFF) << 8 | (array[n + 1] & 0xFF));
    }
    
    static void shortToBigEndian(final short n, final byte[] array, final int n2) {
        array[n2] = (byte)(n >> 8);
        array[n2 + 1] = (byte)n;
    }
    
    private static File getProfileFile(final String s) {
        File standardProfileFile = new File(s);
        if (standardProfileFile.isAbsolute()) {
            return standardProfileFile.isFile() ? standardProfileFile : null;
        }
        final String property;
        if (!standardProfileFile.isFile() && (property = System.getProperty("java.iccprofile.path")) != null) {
            for (StringTokenizer stringTokenizer = new StringTokenizer(property, File.pathSeparator); stringTokenizer.hasMoreTokens() && (standardProfileFile == null || !standardProfileFile.isFile()); standardProfileFile = null) {
                final String nextToken = stringTokenizer.nextToken();
                standardProfileFile = new File(nextToken + File.separatorChar + s);
                if (!isChildOf(standardProfileFile, nextToken)) {}
            }
        }
        final String property2;
        if ((standardProfileFile == null || !standardProfileFile.isFile()) && (property2 = System.getProperty("java.class.path")) != null) {
            for (StringTokenizer stringTokenizer2 = new StringTokenizer(property2, File.pathSeparator); stringTokenizer2.hasMoreTokens() && (standardProfileFile == null || !standardProfileFile.isFile()); standardProfileFile = new File(stringTokenizer2.nextToken() + File.separatorChar + s)) {}
        }
        if (standardProfileFile == null || !standardProfileFile.isFile()) {
            standardProfileFile = getStandardProfileFile(s);
        }
        if (standardProfileFile != null && standardProfileFile.isFile()) {
            return standardProfileFile;
        }
        return null;
    }
    
    private static File getStandardProfileFile(final String s) {
        final String string = System.getProperty("java.home") + File.separatorChar + "lib" + File.separatorChar + "cmm";
        final File file = new File(string + File.separatorChar + s);
        return (file.isFile() && isChildOf(file, string)) ? file : null;
    }
    
    private static boolean isChildOf(final File file, final String s) {
        try {
            String s2 = new File(s).getCanonicalPath();
            if (!s2.endsWith(File.separator)) {
                s2 += File.separator;
            }
            return file.getCanonicalPath().startsWith(s2);
        }
        catch (final IOException ex) {
            return false;
        }
    }
    
    private static boolean standardProfileExists(final String s) {
        return AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return getStandardProfileFile(s) != null;
            }
        });
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        Object o = null;
        if (this == ICC_Profile.sRGBprofile) {
            o = "CS_sRGB";
        }
        else if (this == ICC_Profile.XYZprofile) {
            o = "CS_CIEXYZ";
        }
        else if (this == ICC_Profile.PYCCprofile) {
            o = "CS_PYCC";
        }
        else if (this == ICC_Profile.GRAYprofile) {
            o = "CS_GRAY";
        }
        else if (this == ICC_Profile.LINEAR_RGBprofile) {
            o = "CS_LINEAR_RGB";
        }
        Object data = null;
        if (o == null) {
            data = this.getData();
        }
        objectOutputStream.writeObject(o);
        objectOutputStream.writeObject(data);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        final String s = (String)objectInputStream.readObject();
        final byte[] array = (byte[])objectInputStream.readObject();
        int n = 0;
        boolean b = false;
        if (s != null) {
            b = true;
            if (s.equals("CS_sRGB")) {
                n = 1000;
            }
            else if (s.equals("CS_CIEXYZ")) {
                n = 1001;
            }
            else if (s.equals("CS_PYCC")) {
                n = 1002;
            }
            else if (s.equals("CS_GRAY")) {
                n = 1003;
            }
            else if (s.equals("CS_LINEAR_RGB")) {
                n = 1004;
            }
            else {
                b = false;
            }
        }
        if (b) {
            this.resolvedDeserializedProfile = getInstance(n);
        }
        else {
            this.resolvedDeserializedProfile = getInstance(array);
        }
    }
    
    protected Object readResolve() throws ObjectStreamException {
        return this.resolvedDeserializedProfile;
    }
}
