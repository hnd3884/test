package org.apache.lucene.util;

import java.util.Locale;
import java.text.ParseException;

public final class Version
{
    @Deprecated
    public static final Version LUCENE_4_0_0_ALPHA;
    @Deprecated
    public static final Version LUCENE_4_0_0_BETA;
    @Deprecated
    public static final Version LUCENE_4_0_0;
    @Deprecated
    public static final Version LUCENE_4_1_0;
    @Deprecated
    public static final Version LUCENE_4_2_0;
    @Deprecated
    public static final Version LUCENE_4_2_1;
    @Deprecated
    public static final Version LUCENE_4_3_0;
    @Deprecated
    public static final Version LUCENE_4_3_1;
    @Deprecated
    public static final Version LUCENE_4_4_0;
    @Deprecated
    public static final Version LUCENE_4_5_0;
    @Deprecated
    public static final Version LUCENE_4_5_1;
    @Deprecated
    public static final Version LUCENE_4_6_0;
    @Deprecated
    public static final Version LUCENE_4_6_1;
    @Deprecated
    public static final Version LUCENE_4_7_0;
    @Deprecated
    public static final Version LUCENE_4_7_1;
    @Deprecated
    public static final Version LUCENE_4_7_2;
    @Deprecated
    public static final Version LUCENE_4_8_0;
    @Deprecated
    public static final Version LUCENE_4_8_1;
    @Deprecated
    public static final Version LUCENE_4_9_0;
    @Deprecated
    public static final Version LUCENE_4_9_1;
    @Deprecated
    public static final Version LUCENE_4_10_0;
    @Deprecated
    public static final Version LUCENE_4_10_1;
    @Deprecated
    public static final Version LUCENE_4_10_2;
    @Deprecated
    public static final Version LUCENE_4_10_3;
    @Deprecated
    public static final Version LUCENE_4_10_4;
    @Deprecated
    public static final Version LUCENE_5_0_0;
    @Deprecated
    public static final Version LUCENE_5_1_0;
    @Deprecated
    public static final Version LUCENE_5_2_0;
    @Deprecated
    public static final Version LUCENE_5_2_1;
    @Deprecated
    public static final Version LUCENE_5_3_0;
    @Deprecated
    public static final Version LUCENE_5_3_1;
    @Deprecated
    public static final Version LUCENE_5_3_2;
    @Deprecated
    public static final Version LUCENE_5_4_0;
    @Deprecated
    public static final Version LUCENE_5_4_1;
    @Deprecated
    public static final Version LUCENE_5_5_0;
    @Deprecated
    public static final Version LUCENE_5_5_1;
    @Deprecated
    public static final Version LUCENE_5_5_2;
    public static final Version LUCENE_5_5_3;
    public static final Version LATEST;
    @Deprecated
    public static final Version LUCENE_CURRENT;
    @Deprecated
    public static final Version LUCENE_4_0;
    @Deprecated
    public static final Version LUCENE_4_1;
    @Deprecated
    public static final Version LUCENE_4_2;
    @Deprecated
    public static final Version LUCENE_4_3;
    @Deprecated
    public static final Version LUCENE_4_4;
    @Deprecated
    public static final Version LUCENE_4_5;
    @Deprecated
    public static final Version LUCENE_4_6;
    @Deprecated
    public static final Version LUCENE_4_7;
    @Deprecated
    public static final Version LUCENE_4_8;
    @Deprecated
    public static final Version LUCENE_4_9;
    public final int major;
    public final int minor;
    public final int bugfix;
    public final int prerelease;
    private final int encodedValue;
    
    public static Version parse(final String version) throws ParseException {
        final StrictStringTokenizer tokens = new StrictStringTokenizer(version, '.');
        if (!tokens.hasMoreTokens()) {
            throw new ParseException("Version is not in form major.minor.bugfix(.prerelease) (got: " + version + ")", 0);
        }
        String token = tokens.nextToken();
        int major;
        try {
            major = Integer.parseInt(token);
        }
        catch (final NumberFormatException nfe) {
            final ParseException p = new ParseException("Failed to parse major version from \"" + token + "\" (got: " + version + ")", 0);
            p.initCause(nfe);
            throw p;
        }
        if (!tokens.hasMoreTokens()) {
            throw new ParseException("Version is not in form major.minor.bugfix(.prerelease) (got: " + version + ")", 0);
        }
        token = tokens.nextToken();
        int minor;
        try {
            minor = Integer.parseInt(token);
        }
        catch (final NumberFormatException nfe2) {
            final ParseException p2 = new ParseException("Failed to parse minor version from \"" + token + "\" (got: " + version + ")", 0);
            p2.initCause(nfe2);
            throw p2;
        }
        int bugfix = 0;
        int prerelease = 0;
        if (tokens.hasMoreTokens()) {
            token = tokens.nextToken();
            try {
                bugfix = Integer.parseInt(token);
            }
            catch (final NumberFormatException nfe3) {
                final ParseException p3 = new ParseException("Failed to parse bugfix version from \"" + token + "\" (got: " + version + ")", 0);
                p3.initCause(nfe3);
                throw p3;
            }
            if (tokens.hasMoreTokens()) {
                token = tokens.nextToken();
                try {
                    prerelease = Integer.parseInt(token);
                }
                catch (final NumberFormatException nfe3) {
                    final ParseException p3 = new ParseException("Failed to parse prerelease version from \"" + token + "\" (got: " + version + ")", 0);
                    p3.initCause(nfe3);
                    throw p3;
                }
                if (prerelease == 0) {
                    throw new ParseException("Invalid value " + prerelease + " for prerelease; should be 1 or 2 (got: " + version + ")", 0);
                }
                if (tokens.hasMoreTokens()) {
                    throw new ParseException("Version is not in form major.minor.bugfix(.prerelease) (got: " + version + ")", 0);
                }
            }
        }
        try {
            return new Version(major, minor, bugfix, prerelease);
        }
        catch (final IllegalArgumentException iae) {
            final ParseException pe = new ParseException("failed to parse version string \"" + version + "\": " + iae.getMessage(), 0);
            pe.initCause(iae);
            throw pe;
        }
    }
    
    public static Version parseLeniently(String version) throws ParseException {
        final String versionOrig = version;
        final String upperCase;
        version = (upperCase = version.toUpperCase(Locale.ROOT));
        switch (upperCase) {
            case "LATEST":
            case "LUCENE_CURRENT": {
                return Version.LATEST;
            }
            case "LUCENE_4_0_0": {
                return Version.LUCENE_4_0_0;
            }
            case "LUCENE_4_0_0_ALPHA": {
                return Version.LUCENE_4_0_0_ALPHA;
            }
            case "LUCENE_4_0_0_BETA": {
                return Version.LUCENE_4_0_0_BETA;
            }
            default: {
                version = version.replaceFirst("^LUCENE_(\\d+)_(\\d+)_(\\d+)$", "$1.$2.$3").replaceFirst("^LUCENE_(\\d+)_(\\d+)$", "$1.$2.0").replaceFirst("^LUCENE_(\\d)(\\d)$", "$1.$2.0");
                try {
                    return parse(version);
                }
                catch (final ParseException pe) {
                    final ParseException pe2 = new ParseException("failed to parse lenient version string \"" + versionOrig + "\": " + pe.getMessage(), 0);
                    pe2.initCause(pe);
                    throw pe2;
                }
                break;
            }
        }
    }
    
    public static Version fromBits(final int major, final int minor, final int bugfix) {
        return new Version(major, minor, bugfix);
    }
    
    private Version(final int major, final int minor, final int bugfix) {
        this(major, minor, bugfix, 0);
    }
    
    private Version(final int major, final int minor, final int bugfix, final int prerelease) {
        this.major = major;
        this.minor = minor;
        this.bugfix = bugfix;
        this.prerelease = prerelease;
        if (major > 255 || major < 0) {
            throw new IllegalArgumentException("Illegal major version: " + major);
        }
        if (minor > 255 || minor < 0) {
            throw new IllegalArgumentException("Illegal minor version: " + minor);
        }
        if (bugfix > 255 || bugfix < 0) {
            throw new IllegalArgumentException("Illegal bugfix version: " + bugfix);
        }
        if (prerelease > 2 || prerelease < 0) {
            throw new IllegalArgumentException("Illegal prerelease version: " + prerelease);
        }
        if (prerelease != 0 && (minor != 0 || bugfix != 0)) {
            throw new IllegalArgumentException("Prerelease version only supported with major release (got prerelease: " + prerelease + ", minor: " + minor + ", bugfix: " + bugfix + ")");
        }
        this.encodedValue = (major << 18 | minor << 10 | bugfix << 2 | prerelease);
        assert this.encodedIsValid();
    }
    
    public boolean onOrAfter(final Version other) {
        return this.encodedValue >= other.encodedValue;
    }
    
    @Override
    public String toString() {
        if (this.prerelease == 0) {
            return "" + this.major + "." + this.minor + "." + this.bugfix;
        }
        return "" + this.major + "." + this.minor + "." + this.bugfix + "." + this.prerelease;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && o instanceof Version && ((Version)o).encodedValue == this.encodedValue;
    }
    
    private boolean encodedIsValid() {
        assert this.major == (this.encodedValue >>> 18 & 0xFF);
        assert this.minor == (this.encodedValue >>> 10 & 0xFF);
        assert this.bugfix == (this.encodedValue >>> 2 & 0xFF);
        assert this.prerelease == (this.encodedValue & 0x3);
        return true;
    }
    
    @Override
    public int hashCode() {
        return this.encodedValue;
    }
    
    static {
        LUCENE_4_0_0_ALPHA = new Version(4, 0, 0, 0);
        LUCENE_4_0_0_BETA = new Version(4, 0, 0, 1);
        LUCENE_4_0_0 = new Version(4, 0, 0, 2);
        LUCENE_4_1_0 = new Version(4, 1, 0);
        LUCENE_4_2_0 = new Version(4, 2, 0);
        LUCENE_4_2_1 = new Version(4, 2, 1);
        LUCENE_4_3_0 = new Version(4, 3, 0);
        LUCENE_4_3_1 = new Version(4, 3, 1);
        LUCENE_4_4_0 = new Version(4, 4, 0);
        LUCENE_4_5_0 = new Version(4, 5, 0);
        LUCENE_4_5_1 = new Version(4, 5, 1);
        LUCENE_4_6_0 = new Version(4, 6, 0);
        LUCENE_4_6_1 = new Version(4, 6, 1);
        LUCENE_4_7_0 = new Version(4, 7, 0);
        LUCENE_4_7_1 = new Version(4, 7, 1);
        LUCENE_4_7_2 = new Version(4, 7, 2);
        LUCENE_4_8_0 = new Version(4, 8, 0);
        LUCENE_4_8_1 = new Version(4, 8, 1);
        LUCENE_4_9_0 = new Version(4, 9, 0);
        LUCENE_4_9_1 = new Version(4, 9, 1);
        LUCENE_4_10_0 = new Version(4, 10, 0);
        LUCENE_4_10_1 = new Version(4, 10, 1);
        LUCENE_4_10_2 = new Version(4, 10, 2);
        LUCENE_4_10_3 = new Version(4, 10, 3);
        LUCENE_4_10_4 = new Version(4, 10, 4);
        LUCENE_5_0_0 = new Version(5, 0, 0);
        LUCENE_5_1_0 = new Version(5, 1, 0);
        LUCENE_5_2_0 = new Version(5, 2, 0);
        LUCENE_5_2_1 = new Version(5, 2, 1);
        LUCENE_5_3_0 = new Version(5, 3, 0);
        LUCENE_5_3_1 = new Version(5, 3, 1);
        LUCENE_5_3_2 = new Version(5, 3, 2);
        LUCENE_5_4_0 = new Version(5, 4, 0);
        LUCENE_5_4_1 = new Version(5, 4, 1);
        LUCENE_5_5_0 = new Version(5, 5, 0);
        LUCENE_5_5_1 = new Version(5, 5, 1);
        LUCENE_5_5_2 = new Version(5, 5, 2);
        LUCENE_5_5_3 = new Version(5, 5, 3);
        LATEST = Version.LUCENE_5_5_3;
        LUCENE_CURRENT = Version.LATEST;
        LUCENE_4_0 = Version.LUCENE_4_0_0_ALPHA;
        LUCENE_4_1 = Version.LUCENE_4_1_0;
        LUCENE_4_2 = Version.LUCENE_4_2_0;
        LUCENE_4_3 = Version.LUCENE_4_3_0;
        LUCENE_4_4 = Version.LUCENE_4_4_0;
        LUCENE_4_5 = Version.LUCENE_4_5_0;
        LUCENE_4_6 = Version.LUCENE_4_6_0;
        LUCENE_4_7 = Version.LUCENE_4_7_0;
        LUCENE_4_8 = Version.LUCENE_4_8_0;
        LUCENE_4_9 = Version.LUCENE_4_9_0;
    }
}
