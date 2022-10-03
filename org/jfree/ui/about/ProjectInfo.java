package org.jfree.ui.about;

import org.jfree.base.Library;
import java.util.Iterator;
import java.util.List;
import java.awt.Image;
import org.jfree.base.BootableProjectInfo;

public class ProjectInfo extends BootableProjectInfo
{
    private Image logo;
    private String licenceText;
    private List contributors;
    
    public ProjectInfo() {
    }
    
    public ProjectInfo(final String name, final String version, final String info, final Image logo, final String copyright, final String licenceName, final String licenceText) {
        super(name, version, info, copyright, licenceName);
        this.logo = logo;
        this.licenceText = licenceText;
    }
    
    public List getContributors() {
        return this.contributors;
    }
    
    public String getLicenceText() {
        return this.licenceText;
    }
    
    public Image getLogo() {
        return this.logo;
    }
    
    public void setContributors(final List contributors) {
        this.contributors = contributors;
    }
    
    public void setLicenceText(final String licenceText) {
        this.licenceText = licenceText;
    }
    
    public void setLogo(final Image logo) {
        this.logo = logo;
    }
    
    public String toString() {
        final StringBuffer result = new StringBuffer();
        result.append(this.getName());
        result.append(" version ");
        result.append(this.getVersion());
        result.append(".\n");
        result.append(this.getCopyright());
        result.append(".\n");
        result.append("\n");
        result.append("For terms of use, see the licence below.\n");
        result.append("\n");
        result.append("FURTHER INFORMATION:");
        result.append(this.getInfo());
        result.append("\n");
        result.append("CONTRIBUTORS:");
        if (this.contributors != null) {
            final Iterator iterator = this.contributors.iterator();
            while (iterator.hasNext()) {
                final Contributor contributor = iterator.next();
                result.append(contributor.getName());
                result.append(" (");
                result.append(contributor.getEmail());
                result.append(").");
            }
        }
        else {
            result.append("None");
        }
        result.append("\n");
        result.append("OTHER LIBRARIES USED BY ");
        result.append(this.getName());
        result.append(":");
        final Library[] libraries = this.getLibraries();
        if (libraries.length != 0) {
            for (int i = 0; i < libraries.length; ++i) {
                final Library lib = libraries[i];
                result.append(lib.getName());
                result.append(" ");
                result.append(lib.getVersion());
                result.append(" (");
                result.append(lib.getInfo());
                result.append(").");
            }
        }
        else {
            result.append("None");
        }
        result.append("\n");
        result.append(this.getName());
        result.append(" LICENCE TERMS:");
        result.append("\n");
        result.append(this.getLicenceText());
        return result.toString();
    }
}
