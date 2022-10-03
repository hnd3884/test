package com.adventnet.iam.security;

import java.util.logging.Level;
import java.util.logging.Logger;

class Version
{
    private static final Logger LOGGER;
    private double tagVersion;
    private String tagName;
    
    Version(final String tagName, final String major, final String minor) {
        this.tagName = tagName;
        this.tagVersion = createVersion(this.checkVersion(major), this.checkVersion(minor));
    }
    
    public static double createVersion(final double major, final double minor) {
        if (minor != 0.0) {
            final int numberOfDigit = (int)Math.log10(minor) + 1;
            return major + minor / Math.pow(10.0, numberOfDigit);
        }
        return major;
    }
    
    double checkVersion(final String version) {
        double vrs = 0.0;
        if (!"".equals(version)) {
            vrs = Double.parseDouble(version);
            if (vrs < 0.0) {
                Version.LOGGER.log(Level.SEVERE, " REQUEST HEADER INITIATOR : negative value \"{0}\" as version for {1} is not allowed ", new Object[] { version, this.tagName });
                throw new IAMSecurityException("INVALID_VALUE_NOT_ALLOWED");
            }
        }
        return vrs;
    }
    
    void limitCheck(final String tagValue, final double major, final double minor) {
        if (this.tagVersion > 0.0) {
            final double version = createVersion(major, minor);
            if (this.tagVersion < version) {
                Version.LOGGER.log(Level.SEVERE, " REQUEST HEADER VALIDATION : {0} - \"{1}\" version \"{2}\" is not allowed to make request , expected version is <= \"{3}\" ", new Object[] { this.tagName, tagValue, version, this.tagVersion });
                throw new IAMSecurityException("VERSION_MISMATCHED");
            }
        }
    }
    
    @Override
    public String toString() {
        return "" + this.tagVersion;
    }
    
    static {
        LOGGER = Logger.getLogger(Version.class.getName());
    }
}
