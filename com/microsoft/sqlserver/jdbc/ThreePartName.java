package com.microsoft.sqlserver.jdbc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ThreePartName
{
    private static final Pattern THREE_PART_NAME;
    private final String databasePart;
    private final String ownerPart;
    private final String procedurePart;
    
    private ThreePartName(final String databasePart, final String ownerPart, final String procedurePart) {
        this.databasePart = databasePart;
        this.ownerPart = ownerPart;
        this.procedurePart = procedurePart;
    }
    
    String getDatabasePart() {
        return this.databasePart;
    }
    
    String getOwnerPart() {
        return this.ownerPart;
    }
    
    String getProcedurePart() {
        return this.procedurePart;
    }
    
    static ThreePartName parse(final String theProcName) {
        String procedurePart = null;
        String ownerPart = null;
        String databasePart = null;
        if (null != theProcName) {
            Matcher matcher = ThreePartName.THREE_PART_NAME.matcher(theProcName);
            if (matcher.matches()) {
                if (matcher.group(2) != null) {
                    databasePart = matcher.group(1);
                    matcher = ThreePartName.THREE_PART_NAME.matcher(matcher.group(2));
                    if (matcher.matches()) {
                        if (null != matcher.group(2)) {
                            ownerPart = matcher.group(1);
                            procedurePart = matcher.group(2);
                        }
                        else {
                            ownerPart = databasePart;
                            databasePart = null;
                            procedurePart = matcher.group(1);
                        }
                    }
                }
                else {
                    procedurePart = matcher.group(1);
                }
            }
            else {
                procedurePart = theProcName;
            }
        }
        return new ThreePartName(databasePart, ownerPart, procedurePart);
    }
    
    static {
        THREE_PART_NAME = Pattern.compile(JDBCSyntaxTranslator.getSQLIdentifierWithGroups());
    }
}
