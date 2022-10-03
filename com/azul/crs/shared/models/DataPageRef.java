package com.azul.crs.shared.models;

import java.util.Objects;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonIgnore;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonCreator;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonProperty;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataPageRef
{
    private final Integer limit;
    private final String afterToken;
    
    @JsonCreator
    private DataPageRef(@JsonProperty("limit") final Integer limit, @JsonProperty("afterToken") final String afterToken) {
        this.limit = limit;
        this.afterToken = afterToken;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public Integer getLimit() {
        return this.limit;
    }
    
    public String getAfterToken() {
        return this.afterToken;
    }
    
    @JsonIgnore
    public Integer getAfterTokenAsInt() {
        try {
            return (this.afterToken != null) ? Integer.valueOf(this.afterToken) : null;
        }
        catch (final NumberFormatException e) {
            return null;
        }
    }
    
    @Override
    public String toString() {
        return "DataPageRef{limit=" + this.limit + ", afterToken='" + this.afterToken + '\'' + '}';
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final DataPageRef pageItem = (DataPageRef)o;
        return Objects.equals(this.limit, pageItem.limit) && Objects.equals(this.afterToken, pageItem.afterToken);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.limit, this.afterToken);
    }
    
    public static final class Builder
    {
        private Integer limit;
        private String afterToken;
        
        private Builder() {
        }
        
        public Builder withLimit(final Integer limit) {
            this.limit = limit;
            return this;
        }
        
        public Builder withAfterToken(final String afterToken) {
            this.afterToken = afterToken;
            return this;
        }
        
        public Builder withAfterToken(final Integer offset) {
            this.afterToken = ((offset != null) ? String.valueOf(offset) : null);
            return this;
        }
        
        public DataPageRef build() {
            return new DataPageRef(this.limit, this.afterToken, null);
        }
    }
}
