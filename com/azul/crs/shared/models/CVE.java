package com.azul.crs.shared.models;

import java.util.Objects;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CVE extends Payload
{
    private String cveId;
    private String bugId;
    private Float score;
    private String subsystem;
    private Map<String, Object> metadata;
    private List<CVEClassFix> classFixes;
    private Long registerTime;
    
    public String getCveId() {
        return this.cveId;
    }
    
    public String getBugId() {
        return this.bugId;
    }
    
    public Float getScore() {
        return this.score;
    }
    
    public String getSubsystem() {
        return this.subsystem;
    }
    
    public Map<String, Object> getMetadata() {
        return this.metadata;
    }
    
    public List<CVEClassFix> getClassFixes() {
        return this.classFixes;
    }
    
    public Long getRegisterTime() {
        return this.registerTime;
    }
    
    public void setCveId(final String cveId) {
        this.cveId = cveId;
    }
    
    public void setBugId(final String bugId) {
        this.bugId = bugId;
    }
    
    public void setScore(final Float score) {
        this.score = score;
    }
    
    public void setSubsystem(final String subsystem) {
        this.subsystem = subsystem;
    }
    
    public void setMetadata(final Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    public void setClassFixes(final List<CVEClassFix> classFixes) {
        this.classFixes = classFixes;
    }
    
    public void setRegisterTime(final Long registerTime) {
        this.registerTime = registerTime;
    }
    
    public CVE cveId(final String cveId) {
        this.setCveId(cveId);
        return this;
    }
    
    public CVE bugId(final String bugId) {
        this.setBugId(bugId);
        return this;
    }
    
    public CVE score(final Float score) {
        this.setScore(score);
        return this;
    }
    
    public CVE subsystem(final String subsystem) {
        this.setSubsystem(subsystem);
        return this;
    }
    
    public CVE metadata(final Map<String, Object> metadata) {
        this.setMetadata(metadata);
        return this;
    }
    
    public CVE classFixes(final List<CVEClassFix> classFixes) {
        this.setClassFixes(classFixes);
        return this;
    }
    
    public CVE registerTime(final Long registerTime) {
        this.setRegisterTime(registerTime);
        return this;
    }
    
    @JsonIgnore
    public CVE classFix(final CVEClassFix classFix) {
        if (this.classFixes == null) {
            this.classFixes = new LinkedList<CVEClassFix>();
        }
        this.classFixes.add(classFix);
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
        final CVE cve = (CVE)o;
        return Objects.equals(this.cveId, cve.cveId) && Objects.equals(this.bugId, cve.bugId) && Objects.equals(this.score, cve.score) && Objects.equals(this.subsystem, cve.subsystem) && Objects.equals(this.metadata, cve.metadata) && Objects.equals(this.classFixes, cve.classFixes) && Objects.equals(this.registerTime, cve.registerTime);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.cveId, this.bugId, this.score, this.subsystem, this.metadata, this.classFixes, this.registerTime);
    }
}
