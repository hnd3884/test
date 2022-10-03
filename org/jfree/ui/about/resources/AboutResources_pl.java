package org.jfree.ui.about.resources;

import javax.swing.KeyStroke;
import java.util.ListResourceBundle;

public class AboutResources_pl extends ListResourceBundle
{
    private static final Object[][] CONTENTS;
    
    static {
        CONTENTS = new Object[][] { { "about-frame.tab.about", "Informacja o" }, { "about-frame.tab.system", "System" }, { "about-frame.tab.contributors", "Tw\u00f3rcy" }, { "about-frame.tab.licence", "Licencja" }, { "about-frame.tab.libraries", "Biblioteki" }, { "contributors-table.column.name", "Nazwa:" }, { "contributors-table.column.contact", "Kontakt:" }, { "libraries-table.column.name", "Nazwa:" }, { "libraries-table.column.version", "Wersja:" }, { "libraries-table.column.licence", "Licencja:" }, { "libraries-table.column.info", "Inne informacje:" }, { "system-frame.title", "W?a\u015bciwo\u015bci systemowe" }, { "system-frame.button.close", "Zamknij" }, { "system-frame.button.close.mnemonic", new Character('Z') }, { "system-frame.menu.file", "Plik" }, { "system-frame.menu.file.mnemonic", new Character('P') }, { "system-frame.menu.file.close", "Zamknij" }, { "system-frame.menu.file.close.mnemonic", new Character('K') }, { "system-frame.menu.edit", "Edycja" }, { "system-frame.menu.edit.mnemonic", new Character('E') }, { "system-frame.menu.edit.copy", "Kopiuj" }, { "system-frame.menu.edit.copy.mnemonic", new Character('C') }, { "system-properties-table.column.name", "Nazwa w?a\u015bciwo\u015bci:" }, { "system-properties-table.column.value", "Warto\u015b\u0107:" }, { "system-properties-panel.popup-menu.copy", "Kopiuj" }, { "system-properties-panel.popup-menu.copy.accelerator", KeyStroke.getKeyStroke(67, 2) } };
    }
    
    public Object[][] getContents() {
        return AboutResources_pl.CONTENTS;
    }
}
