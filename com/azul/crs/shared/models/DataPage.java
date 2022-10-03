package com.azul.crs.shared.models;

import java.util.Objects;
import java.util.LinkedList;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashMap;
import java.util.Collections;
import java.util.Map;
import java.util.Collection;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(value = { "size", "hasNext" }, allowGetters = true)
public class DataPage<T extends Payload> extends Payload
{
    @JsonUnwrapped
    private DataPageRef nextPage;
    private Collection<T> data;
    private Map<String, Object> summary;
    
    public DataPageRef getNextPage() {
        return this.nextPage;
    }
    
    public Boolean isHasNext() {
        return this.nextPage != null && this.nextPage.getAfterToken() != null;
    }
    
    public Collection<T> getData() {
        return (this.data == null) ? Collections.EMPTY_LIST : Collections.unmodifiableCollection((Collection<? extends T>)this.data);
    }
    
    public Map<String, Object> getSummary() {
        return this.summary;
    }
    
    public int getSize() {
        return (this.data != null) ? this.data.size() : 0;
    }
    
    public void setNextPage(final DataPageRef nextPage) {
        this.nextPage = nextPage;
    }
    
    public void setData(final Collection<T> data) {
        this.data = data;
    }
    
    public void setSummary(final Map<String, Object> summary) {
        this.summary = summary;
    }
    
    public DataPage<T> nextPage(final DataPageRef nextPage) {
        this.setNextPage(nextPage);
        return this;
    }
    
    public DataPage<T> data(final Collection<T> data) {
        this.setData(data);
        return this;
    }
    
    public DataPage<T> summary(final Map<String, Object> summary) {
        this.setSummary(summary);
        return this;
    }
    
    @JsonIgnore
    public DataPage<T> summary(final String key, final Object value) {
        if (this.summary == null) {
            this.summary = new HashMap<String, Object>();
        }
        this.summary.put(key, value);
        return this;
    }
    
    public DataPage<T> data(final T item) {
        if (this.data == null) {
            this.data = new LinkedList<T>();
        }
        this.data.add(item);
        return this;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final DataPage<?> dataPage = (DataPage<?>)o;
        return Objects.equals(this.nextPage, dataPage.nextPage) && Objects.equals(this.data, dataPage.data) && Objects.equals(this.summary, dataPage.summary);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.nextPage, this.data, this.summary);
    }
}
