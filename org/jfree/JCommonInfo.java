package org.jfree;

import org.jfree.base.Library;
import java.util.List;
import java.util.Arrays;
import org.jfree.ui.about.Contributor;
import org.jfree.ui.about.Licences;
import java.util.ResourceBundle;
import org.jfree.ui.about.ProjectInfo;

public class JCommonInfo extends ProjectInfo
{
    private static JCommonInfo singleton;
    static /* synthetic */ Class class$org$jfree$base$BaseBoot;
    
    private JCommonInfo() {
        final String baseResourceClass = "org.jfree.resources.JCommonResources";
        final ResourceBundle resources = ResourceBundle.getBundle("org.jfree.resources.JCommonResources");
        this.setName(resources.getString("project.name"));
        this.setVersion(resources.getString("project.version"));
        this.setInfo(resources.getString("project.info"));
        this.setCopyright(resources.getString("project.copyright"));
        this.setLicenceName("LGPL");
        this.setLicenceText(Licences.getInstance().getLGPL());
        this.setContributors(Arrays.asList(new Contributor("Anthony Boulestreau", "-"), new Contributor("Jeremy Bowman", "-"), new Contributor("J. David Eisenberg", "-"), new Contributor("Paul English", "-"), new Contributor("David Gilbert", "david.gilbert@object-refinery.com"), new Contributor("Hans-Jurgen Greiner", "-"), new Contributor("Arik Levin", "-"), new Contributor("Achilleus Mantzios", "-"), new Contributor("Thomas Meier", "-"), new Contributor("Aaron Metzger", "-"), new Contributor("Thomas Morgner", "-"), new Contributor("Krzysztof Paz", "-"), new Contributor("Nabuo Tamemasa", "-"), new Contributor("Mark Watson", "-"), new Contributor("Matthew Wright", "-"), new Contributor("Hari", "-"), new Contributor("Sam (oldman)", "-")));
        this.addOptionalLibrary(new Library("JUnit", "3.8", "IBM Public Licence", "http://www.junit.org/"));
        this.setBootClass(((JCommonInfo.class$org$jfree$base$BaseBoot != null) ? JCommonInfo.class$org$jfree$base$BaseBoot : (JCommonInfo.class$org$jfree$base$BaseBoot = class$("org.jfree.base.BaseBoot"))).getName());
    }
    
    static /* synthetic */ Class class$(final String class$) {
        try {
            return Class.forName(class$);
        }
        catch (final ClassNotFoundException forName) {
            throw new NoClassDefFoundError(forName.getMessage());
        }
    }
    
    public static synchronized JCommonInfo getInstance() {
        if (JCommonInfo.singleton == null) {
            JCommonInfo.singleton = new JCommonInfo();
        }
        return JCommonInfo.singleton;
    }
}
