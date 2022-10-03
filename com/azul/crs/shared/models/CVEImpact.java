package com.azul.crs.shared.models;

import java.util.Set;
import java.util.HashSet;
import java.util.Objects;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Map;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Collections;
import java.util.List;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CVEImpact extends Payload
{
    private String vmId;
    private Float maxScore;
    private List<CVE> cves;
    private Long analysisTime;
    
    public String getVmId() {
        return this.vmId;
    }
    
    public Float getMaxScore() {
        return this.maxScore;
    }
    
    public List<CVE> getCves() {
        return this.cves;
    }
    
    public Long getAnalysisTime() {
        return this.analysisTime;
    }
    
    public void setVmId(final String vmId) {
        this.vmId = vmId;
    }
    
    public void setMaxScore(final Float maxScore) {
        this.maxScore = maxScore;
    }
    
    public void setCves(final List<CVE> cves) {
        this.cves = cves;
    }
    
    public void setAnalysisTime(final Long analysisTime) {
        this.analysisTime = analysisTime;
    }
    
    public CVEImpact vmId(final String vmId) {
        this.setVmId(vmId);
        return this;
    }
    
    public CVEImpact maxScore(final Float maxScore) {
        this.setMaxScore(maxScore);
        return this;
    }
    
    public CVEImpact cves(final List<CVE> cves) {
        this.setCves(cves);
        return this;
    }
    
    public CVEImpact analysisTime(final Long analysisTime) {
        this.setAnalysisTime(analysisTime);
        return this;
    }
    
    public CVEImpact cve(final CVE cve) {
        if (this.cves == null || this.cves == Collections.EMPTY_LIST) {
            this.cves = new LinkedList<CVE>();
        }
        this.cves.add(cve);
        return this;
    }
    
    public CVEImpact cve(final String json) {
        try {
            this.cve(Payload.fromJson(json, CVE.class));
            return this;
        }
        catch (final IOException ioe) {
            throw new IllegalArgumentException(ioe);
        }
    }
    
    public void merge(final CVEImpact newImpact) {
        if (newImpact == null) {
            return;
        }
        final String vmId = newImpact.getVmId();
        final Float oldMaxScore = this.getMaxScore();
        final Float newMaxScore = newImpact.getMaxScore();
        this.setVmId(vmId);
        this.setAnalysisTime(newImpact.getAnalysisTime());
        this.setMaxScore(this.max(newMaxScore, oldMaxScore));
        this.setCves(mergeCves(this.getCves(), newImpact.getCves()));
    }
    
    private Float max(final Float newMaxScore, final Float oldMaxScore) {
        if (newMaxScore == null) {
            return oldMaxScore;
        }
        if (oldMaxScore == null) {
            return newMaxScore;
        }
        return Math.max(newMaxScore, oldMaxScore);
    }
    
    private static List<CVE> mergeCves(final List<CVE> oldCves, final List<CVE> newCves) {
        if (oldCves == null) {
            return newCves;
        }
        if (newCves == null) {
            return oldCves;
        }
        final Map<String, CVE> mergedCves = oldCves.stream().collect(Collectors.toMap((Function<? super Object, ? extends String>)CVE::getCveId, cve -> cve));
        for (final CVE newCve : newCves) {
            final String cveId = newCve.getCveId();
            final CVE oldCve = mergedCves.get(cveId);
            if (oldCve != null) {
                final CVE mergedCve = oldCve.merge(newCve);
                mergedCves.put(cveId, mergedCve);
            }
            else {
                mergedCves.put(cveId, newCve);
            }
        }
        return new ArrayList<CVE>(mergedCves.values());
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final CVEImpact impact = (CVEImpact)o;
        return Objects.equals(this.vmId, impact.vmId) && Objects.equals(this.maxScore, impact.maxScore) && Objects.equals(this.cves, impact.cves) && Objects.equals(this.analysisTime, impact.analysisTime);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.vmId, this.maxScore, this.cves, this.analysisTime);
    }
    
    public static class VMClass extends Payload
    {
        private String className;
        private Integer version;
        
        public String getClassName() {
            return this.className;
        }
        
        public Integer getVersion() {
            return this.version;
        }
        
        public void setClassName(final String className) {
            this.className = className;
        }
        
        public void setVersion(final Integer version) {
            this.version = version;
        }
        
        public VMClass className(final String className) {
            this.setClassName(className);
            return this;
        }
        
        public VMClass version(final Integer version) {
            this.setVersion(version);
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
            final VMClass vmClass = (VMClass)o;
            return Objects.equals(this.className, vmClass.className) && Objects.equals(this.version, vmClass.version);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(this.className, this.version);
        }
    }
    
    public static class Component extends Payload
    {
        private String name;
        private Integer version;
        
        public String getName() {
            return this.name;
        }
        
        public Integer getVersion() {
            return this.version;
        }
        
        public void setName(final String name) {
            this.name = name;
        }
        
        public void setVersion(final Integer version) {
            this.version = version;
        }
        
        public Component name(final String name) {
            this.setName(name);
            return this;
        }
        
        public Component version(final Integer version) {
            this.setVersion(version);
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
            final Component component1 = (Component)o;
            return Objects.equals(this.name, component1.name) && Objects.equals(this.version, component1.version);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(this.name, this.version);
        }
    }
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CVE extends Payload
    {
        private String cveId;
        private String bugId;
        private Float score;
        private String subsystem;
        private List<Component> components;
        private List<VMClass> classes;
        
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
        
        public List<Component> getComponents() {
            return this.components;
        }
        
        public List<VMClass> getClasses() {
            return this.classes;
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
        
        public void setComponents(final List<Component> components) {
            this.components = components;
        }
        
        public void setClasses(final List<VMClass> classes) {
            this.classes = classes;
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
        
        public CVE components(final List<Component> components) {
            this.setComponents(components);
            return this;
        }
        
        public CVE classes(final List<VMClass> classes) {
            this.setClasses(classes);
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
            return Objects.equals(this.cveId, cve.cveId) && Objects.equals(this.bugId, cve.bugId) && Objects.equals(this.score, cve.score) && Objects.equals(this.subsystem, cve.subsystem) && Objects.equals(this.components, cve.components) && Objects.equals(this.classes, cve.classes);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(this.cveId, this.bugId, this.score, this.subsystem, this.components, this.classes);
        }
        
        public CVE merge(final CVE newCve) {
            final String newCveId = newCve.getCveId();
            if (this.cveId == null || newCveId == null) {
                throw new IllegalStateException("cveId must not be null: cveId=" + this.cveId + ", newCveId=" + newCveId);
            }
            if (!this.cveId.equals(newCveId)) {
                throw new IllegalStateException("CVEs must have equal cveId values: cveId=" + this.cveId + ", newCveId=" + newCveId);
            }
            final String newCveComponent = newCve.getSubsystem();
            return new CVE().cveId(this.cveId).bugId(newCve.getBugId()).score(newCve.getScore()).subsystem((newCveComponent != null) ? newCveComponent : this.subsystem).components(this.mergeComponents(newCve.getComponents()));
        }
        
        private List<Component> mergeComponents(final List<Component> newComponents) {
            final Set<Component> mergedComponents = new HashSet<Component>(this.components);
            mergedComponents.addAll(newComponents);
            return new ArrayList<Component>(mergedComponents);
        }
    }
}
