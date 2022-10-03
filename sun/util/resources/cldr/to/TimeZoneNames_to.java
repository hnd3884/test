package sun.util.resources.cldr.to;

import sun.util.resources.TimeZoneNamesBundle;

public class TimeZoneNames_to extends TimeZoneNamesBundle
{
    @Override
    protected final Object[][] getContents() {
        final String[] array = { "houa fakaniu\u0113", "NT", "Niue Summer Time", "NST", "Niue Time", "NT" };
        final String[] array2 = { "houa faka\u02bbuvea mo futuna", "WFT", "Wallis and Futuna Summer Time", "WFST", "Wallis and Futuna Time", "WFT" };
        final String[] array3 = { "houa fakat\u016bvalu", "TT", "Tuvalu Summer Time", "TST", "Tuvalu Time", "TT" };
        final String[] array4 = { "houa fakatahisi", "TT", "Tahiti Summer Time", "TST", "Tahiti Time", "TT" };
        final String[] array5 = { "houa fakatonga fakas\u012bpinga", "TST", "houa fakatonga lotoh\u0113", "TST", "houa fakatonga", "TT" };
        final String[] array6 = { "houa fakahaua\u02bbi fakas\u012bpinga", "HAST", "houa fakahaua\u02bbi fakamaama", "HADT", "houa fakahaua\u02bbi", "HAT" };
        final String[] array7 = { "houa fakatokelau", "TT", "Tokelau Summer Time", "TST", "Tokelau Time", "TT" };
        final String[] array8 = { "houa fakanu\u02bbusila fakas\u012bpinga", "NZST", "houa fakanu\u02bbusila fakamaama", "NZDT", "houa fakanu\u02bbusila", "NZT" };
        final String[] array9 = { "houa fakaha\u02bbamoa", "SST", "Samoa Daylight Time", "SDT", "Samoa Time", "ST" };
        return new Object[][] { { "Pacific/Honolulu", array6 }, { "Pacific/Funafuti", array3 }, { "Pacific/Pago_Pago", array9 }, { "Pacific/Midway", array9 }, { "Pacific/Wallis", array2 }, { "Antarctica/McMurdo", array8 }, { "Pacific/Apia", array9 }, { "America/Adak", array6 }, { "Pacific/Niue", array }, { "Pacific/Tongatapu", array5 }, { "Pacific/Tahiti", array4 }, { "Pacific/Fiji", { "houa fakafisi fakas\u012bpinga", "FST", "houa fakafisi fakamaama", "FST", "houa fakafisi", "FT" } }, { "Pacific/Fakaofo", array7 }, { "Pacific/Auckland", array8 }, { "Pacific/Johnston", array6 } };
    }
}
