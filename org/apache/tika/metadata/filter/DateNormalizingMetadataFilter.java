package org.apache.tika.metadata.filter;

import org.slf4j.LoggerFactory;
import org.apache.tika.config.Field;
import java.time.ZoneId;
import org.apache.tika.exception.TikaException;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import org.apache.tika.metadata.Property;
import org.apache.tika.metadata.Metadata;
import org.slf4j.Logger;
import java.util.TimeZone;

public class DateNormalizingMetadataFilter extends MetadataFilter
{
    private static TimeZone UTC;
    private static final Logger LOGGER;
    private TimeZone defaultTimeZone;
    
    public DateNormalizingMetadataFilter() {
        this.defaultTimeZone = DateNormalizingMetadataFilter.UTC;
    }
    
    @Override
    public void filter(final Metadata metadata) throws TikaException {
        SimpleDateFormat dateFormatter = null;
        SimpleDateFormat utcFormatter = null;
        for (final String n : metadata.names()) {
            final Property property = Property.get(n);
            if (property != null && property.getValueType().equals(Property.ValueType.DATE)) {
                final String dateString = metadata.get(property);
                if (!dateString.endsWith("Z")) {
                    if (dateFormatter == null) {
                        dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                        dateFormatter.setTimeZone(this.defaultTimeZone);
                        utcFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
                        utcFormatter.setTimeZone(DateNormalizingMetadataFilter.UTC);
                    }
                    Date d = null;
                    try {
                        d = dateFormatter.parse(dateString);
                        metadata.set(property, utcFormatter.format(d));
                    }
                    catch (final ParseException e) {
                        DateNormalizingMetadataFilter.LOGGER.warn("Couldn't convert date to default time zone: >" + dateString + "<");
                    }
                }
            }
        }
    }
    
    @Field
    public void setDefaultTimeZone(final String timeZoneId) {
        this.defaultTimeZone = TimeZone.getTimeZone(ZoneId.of(timeZoneId));
    }
    
    static {
        DateNormalizingMetadataFilter.UTC = TimeZone.getTimeZone("UTC");
        LOGGER = LoggerFactory.getLogger((Class)DateNormalizingMetadataFilter.class);
    }
}
