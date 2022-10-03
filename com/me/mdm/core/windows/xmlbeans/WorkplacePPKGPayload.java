package com.me.mdm.core.windows.xmlbeans;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Workplace")
class WorkplacePPKGPayload
{
    private EnrollmentContainer erollmentContainer;
    
    public EnrollmentContainer getErollmentContainer() {
        return this.erollmentContainer;
    }
    
    @XmlElement(name = "Enrollments")
    public void setErollmentContainer(final EnrollmentContainer erollmentContainer) {
        this.erollmentContainer = erollmentContainer;
    }
}
