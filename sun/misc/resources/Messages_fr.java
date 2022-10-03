package sun.misc.resources;

import java.util.ListResourceBundle;

public final class Messages_fr extends ListResourceBundle
{
    @Override
    protected Object[][] getContents() {
        return new Object[][] { { "optpkg.versionerror", "ERREUR : le format de version utilis\u00e9 pour le fichier JAR {0} n''est pas valide. Pour conna\u00eetre le format de version pris en charge, consultez la documentation." }, { "optpkg.attributeerror", "ERREUR : l''attribut manifest JAR {0} obligatoire n''est pas d\u00e9fini dans le fichier JAR {1}." }, { "optpkg.attributeserror", "ERREUR : certains attributs manifest JAR obligatoires ne sont pas d\u00e9finis dans le fichier JAR {0}." } };
    }
}
