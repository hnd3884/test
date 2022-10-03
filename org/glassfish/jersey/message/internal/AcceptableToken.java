package org.glassfish.jersey.message.internal;

import java.text.ParseException;

public class AcceptableToken extends Token implements Qualified
{
    protected int quality;
    
    public AcceptableToken(final String header) throws ParseException {
        this(HttpHeaderReader.newInstance(header));
    }
    
    public AcceptableToken(final HttpHeaderReader reader) throws ParseException {
        this.quality = 1000;
        reader.hasNext();
        this.token = reader.nextToken().toString();
        if (reader.hasNext()) {
            this.quality = HttpHeaderReader.readQualityFactorParameter(reader);
        }
    }
    
    @Override
    public int getQuality() {
        return this.quality;
    }
}
