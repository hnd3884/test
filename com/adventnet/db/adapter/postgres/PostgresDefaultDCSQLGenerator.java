package com.adventnet.db.adapter.postgres;

import java.util.Iterator;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import java.util.List;
import com.adventnet.db.adapter.DefaultDCSQLGenerator;

public class PostgresDefaultDCSQLGenerator extends DefaultDCSQLGenerator
{
    @Override
    public String getSQLForArchiveTableCheckConstraint(final String invisibleTable, final List<ColumnDefinition> colDefs) {
        final StringBuilder buff = new StringBuilder();
        for (final ColumnDefinition cd : colDefs) {
            buff.append("; ALTER TABLE ");
            buff.append(this.sqlGenerator.getDBSpecificTableName(invisibleTable));
            buff.append(" ADD ");
            buff.append(((PostgresSQLGenerator)this.sqlGenerator).getSQLForMaxSizeCheckConstraint(cd.getTableName(), cd.getColumnName(), cd.getMaxLength()));
        }
        return buff.toString();
    }
}
