package org.apache.tika.metadata;

public interface Database
{
    public static final String PREFIX = "database:";
    public static final Property TABLE_NAME = Property.externalTextBag("database:table_name");
    public static final Property ROW_COUNT = Property.externalInteger("database:row_count");
    public static final Property COLUMN_COUNT = Property.externalInteger("database:column_count");
    public static final Property COLUMN_NAME = Property.externalTextBag("database:column_name");
}
