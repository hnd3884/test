package com.me.devicemanagement.framework.server.sql;

import java.util.logging.Level;
import com.adventnet.persistence.Row;
import org.xml.sax.Attributes;
import java.util.logging.Logger;
import com.adventnet.client.components.xml.ACSQLTableRowTransformer;

public class DMACSQLTableRowTransformer extends ACSQLTableRowTransformer
{
    private static final Logger OUT;
    String dbRangeCriteriaRegex;
    
    public DMACSQLTableRowTransformer() {
        this.dbRangeCriteriaRegex = "(\\$\\s*\\{\\s*DBRANGECRITERIA\\s*-\\s*)([\\w]+)(\\s*-\\s*[\\w]+){0,1}(\\s*\\})";
    }
    
    public Row createRow(final String tname, final Attributes atts) {
        final Row row = super.createRow(tname, atts);
        try {
            if (row != null) {
                final String sql = new String(atts.getValue("sql")) + "safety string for String.split() Count if DBRANGECRITERIA present at the end of the string";
                final int selectCount = sql.toLowerCase().split("select").length - 1;
                final int dbRangeCriteriaCount = sql.split(this.dbRangeCriteriaRegex).length - 1;
                if (selectCount != dbRangeCriteriaCount) {
                    throw new Exception("${DBRANGECRITERIA-TableName-TableAlais} in not added for all the select primary table in the ACSql query : " + sql);
                }
            }
        }
        catch (final Exception e) {
            DMACSQLTableRowTransformer.OUT.log(Level.SEVERE, null, e);
            return null;
        }
        return row;
    }
    
    static {
        OUT = Logger.getLogger(DMACSQLTableRowTransformer.class.getName());
    }
}
