package com.unboundid.ldap.sdk;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class Version
{
    public static final String PRODUCT_NAME = "UnboundID LDAP SDK for Java";
    public static final String SHORT_NAME = "unboundid-ldapsdk";
    public static final int MAJOR_VERSION = 4;
    public static final int MINOR_VERSION = 0;
    public static final int POINT_VERSION = 14;
    public static final String VERSION_QUALIFIER = "";
    public static final String BUILD_TIMESTAMP = "20191213063540Z";
    public static final String REPOSITORY_TYPE = "git";
    public static final String REPOSITORY_URL = "https://github.com/pingidentity/ldapsdk.git";
    public static final String REPOSITORY_PATH = "/";
    public static final String REVISION_ID = "c0fb784eebf9d36a67c736d0428fb3577f2e25bb";
    @Deprecated
    public static final long REVISION_NUMBER = -1L;
    public static final String FULL_VERSION_STRING = "UnboundID LDAP SDK for Java 4.0.14";
    public static final String SHORT_VERSION_STRING = "unboundid-ldapsdk-4.0.14";
    public static final String NUMERIC_VERSION_STRING = "4.0.14";
    
    private Version() {
    }
    
    public static void main(final String... args) {
        for (final String line : getVersionLines()) {
            System.out.println(line);
        }
    }
    
    public static List<String> getVersionLines() {
        final ArrayList<String> versionLines = new ArrayList<String>(11);
        versionLines.add("Full Version String:   UnboundID LDAP SDK for Java 4.0.14");
        versionLines.add("Short Version String:  unboundid-ldapsdk-4.0.14");
        versionLines.add("Product Name:          UnboundID LDAP SDK for Java");
        versionLines.add("Short Name:            unboundid-ldapsdk");
        versionLines.add("Major Version:         4");
        versionLines.add("Minor Version:         0");
        versionLines.add("Point Version:         14");
        versionLines.add("Version Qualifier:     ");
        versionLines.add("Build Timestamp:       20191213063540Z");
        versionLines.add("Repository Type:       git");
        versionLines.add("Repository URL:        https://github.com/pingidentity/ldapsdk.git");
        versionLines.add("Repository Path:       /");
        versionLines.add("Revision:              c0fb784eebf9d36a67c736d0428fb3577f2e25bb");
        return Collections.unmodifiableList((List<? extends String>)versionLines);
    }
}
