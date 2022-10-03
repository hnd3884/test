package sun.misc.resources;

import java.util.ListResourceBundle;

public final class Messages_de extends ListResourceBundle
{
    @Override
    protected Object[][] getContents() {
        return new Object[][] { { "optpkg.versionerror", "ERROR: In JAR-Datei {0} wurde ein ung\u00fcltiges Versionsformat verwendet. Pr\u00fcfen Sie in der Dokumentation, welches Versionsformat unterst\u00fctzt wird." }, { "optpkg.attributeerror", "ERROR: In JAR-Datei {1} ist das erforderliche JAR-Manifestattribut {0} nicht festgelegt." }, { "optpkg.attributeserror", "ERROR: In JAR-Datei {0} sind einige erforderliche JAR-Manifestattribute nicht festgelegt." } };
    }
}
