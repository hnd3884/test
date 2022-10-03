package com.adventnet.swissqlapi.sql.statement.update;

import com.adventnet.swissqlapi.config.SwisSQLOptions;
import java.util.StringTokenizer;

public class HintClause
{
    private String msSQLServerHint;
    private String oracleHint;
    
    public void setOracleHint(final String s) {
        this.oracleHint = s;
    }
    
    public void setSQLServerHint(final String s) {
        this.msSQLServerHint = s;
    }
    
    public String getOracleHint() {
        return this.oracleHint;
    }
    
    public String getSQLServerHint() {
        return this.msSQLServerHint;
    }
    
    public void toOracle() {
        final String with = "WITH";
        final StringBuffer newHint = new StringBuffer();
        boolean added = false;
        if (this.msSQLServerHint != null) {
            if (this.msSQLServerHint.trim().startsWith(with)) {
                this.msSQLServerHint = this.msSQLServerHint.substring(4);
            }
            if (this.msSQLServerHint.startsWith("(")) {
                this.msSQLServerHint = this.msSQLServerHint.substring(1);
            }
            if (this.msSQLServerHint.endsWith(")")) {
                this.msSQLServerHint = this.msSQLServerHint.substring(0, this.msSQLServerHint.length() - 1);
            }
            String delimit = null;
            if (this.msSQLServerHint.indexOf(",") > -1) {
                delimit = ",";
            }
            else {
                delimit = " ";
            }
            final StringTokenizer st = new StringTokenizer(this.msSQLServerHint, delimit);
            int i = 0;
            while (st.hasMoreTokens()) {
                final String tok = st.nextToken();
                if (!tok.equalsIgnoreCase("NOLOCK") && !tok.equalsIgnoreCase("ROWLOCK")) {
                    if (!tok.equalsIgnoreCase("XLOCK")) {
                        if (tok.equalsIgnoreCase("UPDLOCK") || tok.equalsIgnoreCase("TABLOCK") || tok.equalsIgnoreCase("TABLOCKX") || tok.equalsIgnoreCase("UPDLOCK")) {
                            if (i > 0 && added) {
                                newHint.append(",");
                            }
                            if (!added) {
                                newHint.append(with);
                                newHint.append("(");
                                added = true;
                            }
                            newHint.append(tok);
                        }
                    }
                }
                ++i;
            }
            if (added) {
                newHint.append(")");
            }
        }
        this.msSQLServerHint = newHint.toString();
        if (!SwisSQLOptions.handleLOCK_HINTSforOracle) {
            this.msSQLServerHint = null;
        }
    }
    
    public void toSQLServer() {
        if (this.oracleHint != null) {
            this.oracleHint = null;
        }
    }
    
    public void toDB2() {
        this.makeNull();
    }
    
    public void toMySQL() {
        this.makeNull();
    }
    
    public void toPostgreSQL() {
        this.makeNull();
    }
    
    public void toInformix() {
        this.makeNull();
    }
    
    public void makeNull() {
        this.oracleHint = null;
        this.msSQLServerHint = null;
    }
    
    @Override
    public String toString() {
        if (this.msSQLServerHint != null) {
            return this.msSQLServerHint;
        }
        if (this.oracleHint != null) {
            return this.oracleHint;
        }
        return null;
    }
}
