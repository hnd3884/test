package sun.util.resources.cldr.es;

import sun.util.resources.TimeZoneNamesBundle;

public class TimeZoneNames_es_AR extends TimeZoneNamesBundle
{
    @Override
    protected final Object[][] getContents() {
        final String[] array = { "Hora est\u00e1ndar de Argentina", "ART", "Hora de verano de Argentina", "ARST", "Hora de Argentina", "ART" };
        return new Object[][] { { "America/Argentina/San_Juan", array }, { "America/Cordoba", array }, { "America/Argentina/Tucuman", array }, { "America/Argentina/Ushuaia", array }, { "America/Mendoza", array }, { "America/Argentina/Rio_Gallegos", array }, { "America/Jujuy", array }, { "America/Catamarca", array }, { "America/Argentina/Salta", array }, { "America/Argentina/La_Rioja", array }, { "America/Buenos_Aires", array }, { "America/Argentina/San_Luis", { "Hora est\u00e1ndar de Argentina occidental", "WART", "Hora de verano de Argentina occidental", "WARST", "Hora de Argentina occidental", "WART" } } };
    }
}
