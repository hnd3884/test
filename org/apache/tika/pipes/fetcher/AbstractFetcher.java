package org.apache.tika.pipes.fetcher;

import org.apache.tika.config.Field;

public abstract class AbstractFetcher implements Fetcher
{
    private String name;
    
    public AbstractFetcher() {
    }
    
    public AbstractFetcher(final String name) {
        this.name = name;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Field
    public void setName(final String name) {
        this.name = name;
    }
}
