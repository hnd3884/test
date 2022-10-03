package org.apache.tika.sax;

public class StandardReference
{
    private String mainOrganization;
    private String separator;
    private String secondOrganization;
    private String identifier;
    private double score;
    
    private StandardReference(final String mainOrganizationAcronym, final String separator, final String secondOrganizationAcronym, final String identifier, final double score) {
        this.mainOrganization = mainOrganizationAcronym;
        this.separator = separator;
        this.secondOrganization = secondOrganizationAcronym;
        this.identifier = identifier;
        this.score = score;
    }
    
    public String getMainOrganizationAcronym() {
        return this.mainOrganization;
    }
    
    public void setMainOrganizationAcronym(final String mainOrganizationAcronym) {
        this.mainOrganization = mainOrganizationAcronym;
    }
    
    public String getSeparator() {
        return this.separator;
    }
    
    public void setSeparator(final String separator) {
        this.separator = separator;
    }
    
    public String getSecondOrganizationAcronym() {
        return this.secondOrganization;
    }
    
    public void setSecondOrganizationAcronym(final String secondOrganizationAcronym) {
        this.secondOrganization = secondOrganizationAcronym;
    }
    
    public String getIdentifier() {
        return this.identifier;
    }
    
    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }
    
    public double getScore() {
        return this.score;
    }
    
    public void setScore(final double score) {
        this.score = score;
    }
    
    @Override
    public String toString() {
        String standardReference = this.mainOrganization;
        if (this.separator != null && !this.separator.isEmpty()) {
            standardReference = standardReference + this.separator + this.secondOrganization;
        }
        standardReference = standardReference + " " + this.identifier;
        return standardReference;
    }
    
    public static class StandardReferenceBuilder
    {
        private final String mainOrganization;
        private final String identifier;
        private String separator;
        private String secondOrganization;
        private double score;
        
        public StandardReferenceBuilder(final String mainOrganization, final String identifier) {
            this.mainOrganization = mainOrganization;
            this.separator = null;
            this.secondOrganization = null;
            this.identifier = identifier;
            this.score = 0.0;
        }
        
        public StandardReferenceBuilder setSecondOrganization(final String separator, final String secondOrganization) {
            this.separator = separator;
            this.secondOrganization = secondOrganization;
            return this;
        }
        
        public StandardReferenceBuilder setScore(final double score) {
            this.score = score;
            return this;
        }
        
        public StandardReference build() {
            return new StandardReference(this.mainOrganization, this.separator, this.secondOrganization, this.identifier, this.score, null);
        }
    }
}
