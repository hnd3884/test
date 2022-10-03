package com.adventnet.swissqlapi.sql.statement;

import java.util.StringTokenizer;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.util.ArrayList;
import java.io.Serializable;

public class CommentClass implements Serializable
{
    private ArrayList specialTokenList;
    private int SQLDialect;
    private String comment;
    
    public CommentClass() {
        this.specialTokenList = new ArrayList();
        this.SQLDialect = 0;
        this.comment = null;
    }
    
    public void setSQLDialect(final int SQLDialect) {
        this.SQLDialect = SQLDialect;
    }
    
    public void setComment(final String comment) {
        this.comment = comment;
    }
    
    public void setSpecialToken(final ArrayList specialTokenList) {
        for (int i = 0; i < specialTokenList.size(); ++i) {
            if (!this.specialTokenList.contains(specialTokenList.get(i))) {
                this.specialTokenList.add(specialTokenList.get(i));
            }
        }
    }
    
    public int getSQLDialect() {
        return this.SQLDialect;
    }
    
    public ArrayList getSpecialToken() {
        return this.specialTokenList;
    }
    
    @Override
    public String toString() {
        if (this.comment != null) {
            return this.comment;
        }
        final StringBuffer tempBuffer = new StringBuffer();
        for (int i = 0; i < this.specialTokenList.size(); ++i) {
            String specialToken = this.specialTokenList.get(i);
            if (specialToken.trim().startsWith("--")) {
                if (specialToken.indexOf("*/") != -1) {
                    specialToken = StringFunctions.replaceAll("*//*", "*/", specialToken);
                }
                specialToken = StringFunctions.replaceFirst("/*", "--", specialToken);
                specialToken += "*/";
                tempBuffer.append(specialToken);
            }
            else if (specialToken.trim().toUpperCase().indexOf("%SSTD%") != -1 && this.SQLDialect == 12) {
                final String teradataCom = "%SSTD%";
                tempBuffer.append(specialToken.replaceAll("/\\*", "").replaceAll(teradataCom, "").replaceAll("\\*/", ""));
                if (!specialToken.trim().endsWith(";")) {
                    tempBuffer.append(";");
                }
                tempBuffer.append("\n");
            }
            else if (specialToken.trim().startsWith("/*")) {
                final StringTokenizer newLineTokenizer = new StringTokenizer(specialToken, "\n");
                tempBuffer.append(newLineTokenizer.nextToken());
                while (newLineTokenizer.hasMoreTokens()) {
                    final String tokenBeforeLine = newLineTokenizer.nextToken();
                    tempBuffer.append("\n");
                    tempBuffer.append(tokenBeforeLine);
                }
            }
            else {
                System.err.println("SpecialToken inside Comment Clause toStirng " + specialToken);
            }
            tempBuffer.append("\n");
        }
        return tempBuffer.toString();
    }
    
    public String toTSQLString() {
        final StringBuffer tempBuffer = new StringBuffer();
        for (int i = 0; i < this.specialTokenList.size(); ++i) {
            final String specialToken = this.specialTokenList.get(i);
            if (specialToken.trim().startsWith("/*")) {
                final StringTokenizer newLineTokenizer = new StringTokenizer(specialToken, "\n");
                tempBuffer.append(newLineTokenizer.nextToken());
                while (newLineTokenizer.hasMoreTokens()) {
                    final String tokenBeforeLine = newLineTokenizer.nextToken();
                    tempBuffer.append("\n");
                    tempBuffer.append(tokenBeforeLine);
                }
            }
            tempBuffer.append("\n");
        }
        return tempBuffer.toString();
    }
}
