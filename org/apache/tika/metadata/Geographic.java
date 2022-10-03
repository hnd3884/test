package org.apache.tika.metadata;

public interface Geographic
{
    public static final Property LATITUDE = Property.internalReal("geo:lat");
    public static final Property LONGITUDE = Property.internalReal("geo:long");
    public static final Property ALTITUDE = Property.internalReal("geo:alt");
}
