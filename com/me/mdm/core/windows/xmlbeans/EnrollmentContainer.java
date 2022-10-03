package com.me.mdm.core.windows.xmlbeans;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Enrollments")
class EnrollmentContainer
{
    private ArrayList<EnrollmentUserPrincipalName> userPrincipalEnrollments;
    
    public ArrayList<EnrollmentUserPrincipalName> getUserPrincipalEnrollments() {
        return this.userPrincipalEnrollments;
    }
    
    @XmlElement(name = "UPN")
    public void setUserPrincipalEnrollments(final ArrayList<EnrollmentUserPrincipalName> userPrincipalEnrollments) {
        this.userPrincipalEnrollments = userPrincipalEnrollments;
    }
}
