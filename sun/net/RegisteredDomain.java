package sun.net;

import java.util.HashMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class RegisteredDomain
{
    private static Set<String> top1Set;
    private static Set<String> top2Set;
    private static Set<String> top4Set;
    private static Set<String> top3Set;
    private static Set<String> ukSet;
    private static Set<String> arSet;
    private static Set<String> omSet;
    private static Set<String> top5Set;
    private static Set<String> jpSet;
    private static Set<String> jp2Set;
    private static Set<String> usStateSet;
    private static Set<String> usSubStateSet;
    private static Map<String, Set<String>> topMap;
    private static Map<String, Set<String>> top3Map;
    
    public static sun.security.util.RegisteredDomain registeredDomain(final String s) {
        final String registeredDomain = getRegisteredDomain(s);
        if (registeredDomain.equals(s)) {
            return null;
        }
        return new sun.security.util.RegisteredDomain() {
            private String rname = registeredDomain;
            
            @Override
            public String name() {
                return this.rname;
            }
            
            @Override
            public Type type() {
                return Type.ICANN;
            }
            
            @Override
            public String publicSuffix() {
                return this.rname.substring(this.rname.indexOf(".") + 1);
            }
        };
    }
    
    public static String getRegisteredDomain(String substring) {
        int n = substring.lastIndexOf(46);
        if (n == -1) {
            return substring;
        }
        if (n == 0) {
            return "";
        }
        if (n == substring.length() - 1) {
            substring = substring.substring(0, substring.length() - 1);
            n = substring.lastIndexOf(46);
            if (n == -1) {
                return substring;
            }
            if (n == 0) {
                return "";
            }
        }
        if (n == substring.length() - 1) {
            return "";
        }
        final int lastIndex = substring.lastIndexOf(46, n - 1);
        if (lastIndex == -1) {
            return substring;
        }
        if (lastIndex == 0) {
            return "";
        }
        final int lastIndex2 = substring.lastIndexOf(46, lastIndex - 1);
        int lastIndex3 = -1;
        if (lastIndex2 > 0) {
            lastIndex3 = substring.lastIndexOf(46, lastIndex2 - 1);
        }
        int lastIndex4 = -1;
        if (lastIndex3 > 0) {
            lastIndex4 = substring.lastIndexOf(46, lastIndex3 - 1);
        }
        final String substring2 = substring.substring(n + 1);
        final String substring3 = substring.substring(lastIndex + 1, n);
        if (lastIndex3 != -1 && substring2.equals("us") && RegisteredDomain.usStateSet.contains(substring3)) {
            final String substring4 = substring.substring(lastIndex2 + 1, lastIndex);
            final String substring5 = substring.substring(lastIndex3 + 1, lastIndex2);
            if (substring4.equals("k12")) {
                if (substring3.equals("ma") && (substring5.equals("chtr") || substring5.equals("paroch"))) {
                    return substring.substring(lastIndex4 + 1);
                }
                if (substring5.equals("pvt")) {
                    return substring.substring(lastIndex4 + 1);
                }
            }
        }
        final String substring6 = substring.substring(lastIndex2 + 1);
        if (lastIndex2 != -1) {
            final Set set = RegisteredDomain.top3Map.get(substring2);
            if (set != null) {
                if (set.contains(substring6)) {
                    return substring.substring(lastIndex3 + 1);
                }
            }
            else if (substring2.equals("us") && RegisteredDomain.usStateSet.contains(substring3)) {
                if (RegisteredDomain.usSubStateSet.contains(substring.substring(lastIndex2 + 1, lastIndex))) {
                    return (lastIndex3 != -1) ? substring.substring(lastIndex3 + 1) : substring;
                }
                return substring.substring(lastIndex2 + 1);
            }
            else if (substring2.equals("uk")) {
                if (substring3.equals("sch")) {
                    return substring.substring(lastIndex3 + 1);
                }
            }
            else if (substring2.equals("jp") && RegisteredDomain.jpSet.contains(substring3)) {
                if (RegisteredDomain.jp2Set.contains(substring6)) {
                    return substring.substring(lastIndex2 + 1);
                }
                return substring.substring(lastIndex3 + 1);
            }
        }
        if (RegisteredDomain.jp2Set.contains(substring6)) {
            return substring.substring(lastIndex2 + 1);
        }
        final Set set2 = RegisteredDomain.topMap.get(substring2);
        if (set2 != null) {
            if (set2.contains(substring3)) {
                return substring.substring(lastIndex2 + 1);
            }
            if ((!substring2.equals("us") || !RegisteredDomain.usStateSet.contains(substring3)) && (!substring2.equals("jp") || !RegisteredDomain.jpSet.contains(substring3))) {
                return substring.substring(lastIndex + 1);
            }
        }
        else if (RegisteredDomain.top2Set.contains(substring2)) {
            if (substring3.equals("gov")) {
                return substring.substring(lastIndex2 + 1);
            }
            return substring.substring(lastIndex + 1);
        }
        else if (RegisteredDomain.top3Set.contains(substring2)) {
            if ((substring2.equals("ad") && substring3.equals("nom")) || (substring2.equals("aw") && substring3.equals("com")) || (substring2.equals("be") && substring3.equals("ac")) || (substring2.equals("cl") && substring3.equals("gov")) || (substring2.equals("cl") && substring3.equals("gob")) || (substring2.equals("fi") && substring3.equals("aland")) || (substring2.equals("int") && substring3.equals("eu")) || (substring2.equals("io") && substring3.equals("com")) || (substring2.equals("mc") && substring3.equals("tm")) || (substring2.equals("mc") && substring3.equals("asso")) || (substring2.equals("vc") && substring3.equals("com"))) {
                return substring.substring(lastIndex2 + 1);
            }
            return substring.substring(lastIndex + 1);
        }
        else if (RegisteredDomain.top4Set.contains(substring2)) {
            if (substring3.equals("com") || substring3.equals("edu") || substring3.equals("gov") || substring3.equals("net") || substring3.equals("org")) {
                return substring.substring(lastIndex2 + 1);
            }
            return substring.substring(lastIndex + 1);
        }
        else if (RegisteredDomain.top5Set.contains(substring2)) {
            return substring.substring(lastIndex2 + 1);
        }
        if (substring2.equals("tr")) {
            if (!substring3.equals("nic") && !substring3.equals("tsk")) {
                return substring.substring(lastIndex2 + 1);
            }
            return substring.substring(lastIndex + 1);
        }
        else if (substring2.equals("uk")) {
            if (!RegisteredDomain.ukSet.contains(substring3)) {
                return substring.substring(lastIndex2 + 1);
            }
            return substring.substring(lastIndex + 1);
        }
        else if (substring2.equals("ar")) {
            if (!RegisteredDomain.arSet.contains(substring3)) {
                return substring.substring(lastIndex2 + 1);
            }
            return substring.substring(lastIndex + 1);
        }
        else if (substring2.equals("om")) {
            if (!RegisteredDomain.omSet.contains(substring3)) {
                return substring.substring(lastIndex2 + 1);
            }
            return substring.substring(lastIndex + 1);
        }
        else {
            if (RegisteredDomain.top1Set.contains(substring2)) {
                return substring.substring(lastIndex + 1);
            }
            return substring;
        }
    }
    
    static {
        RegisteredDomain.top1Set = new HashSet<String>(Arrays.asList("asia", "biz", "cat", "coop", "edu", "info", "gov", "jobs", "travel", "am", "aq", "ax", "cc", "cf", "cg", "ch", "cv", "cz", "de", "dj", "dk", "fm", "fo", "ga", "gd", "gf", "gl", "gm", "gq", "gs", "gw", "hm", "li", "lu", "md", "mh", "mil", "mobi", "mq", "ms", "ms", "ne", "nl", "nu", "si", "sm", "sr", "su", "tc", "td", "tf", "tg", "tk", "tm", "tv", "va", "vg", "xn--mgbaam7a8h", "xn--fiqs8s", "xn--fiqz9s", "xn--wgbh1c", "xn--j6w193g", "xn--mgbayh7gpa", "xn--fzc2c9e2c", "xn--ygbi2ammx", "xn--p1ai", "xn--wgbl6a", "xn--mgberp4a5d4ar", "xn--yfro4i67o", "xn--o3cw4h", "xn--pgbs0dh", "xn--kpry57d", "xn--kprw13d", "xn--clchc0ea0b2g2a9gcd"));
        RegisteredDomain.top2Set = new HashSet<String>(Arrays.asList("as", "bf", "cd", "cx", "ie", "lt", "mr", "tl"));
        RegisteredDomain.top4Set = new HashSet<String>(Arrays.asList("af", "bm", "bs", "bt", "bz", "dm", "ky", "lb", "lr", "mo", "sc", "sl", "ws"));
        RegisteredDomain.top3Set = new HashSet<String>(Arrays.asList("ad", "aw", "be", "bw", "cl", "fi", "int", "io", "mc"));
        RegisteredDomain.ukSet = new HashSet<String>(Arrays.asList("bl", "british-library", "jet", "nhs", "nls", "parliament", "mod", "police"));
        RegisteredDomain.arSet = new HashSet<String>(Arrays.asList("argentina", "educ", "gobiernoelectronico", "nic", "promocion", "retina", "uba"));
        RegisteredDomain.omSet = new HashSet<String>(Arrays.asList("mediaphone", "nawrastelecom", "nawras", "omanmobile", "omanpost", "omantel", "rakpetroleum", "siemens", "songfest", "statecouncil", "shura", "peie", "omran", "omnic", "omanet", "oman", "muriya", "kom"));
        RegisteredDomain.top5Set = new HashSet<String>(Arrays.asList("au", "arpa", "bd", "bn", "ck", "cy", "er", "et", "fj", "fk", "gt", "gu", "il", "jm", "ke", "kh", "kw", "mm", "mt", "mz", "ni", "np", "nz", "pg", "sb", "sv", "tz", "uy", "ve", "ye", "za", "zm", "zw"));
        RegisteredDomain.jpSet = new HashSet<String>(Arrays.asList("aichi", "akita", "aomori", "chiba", "ehime", "fukui", "fukuoka", "fukushima", "gifu", "gunma", "hiroshima", "hokkaido", "hyogo", "ibaraki", "ishikawa", "iwate", "kagawa", "kagoshima", "kanagawa", "kawasaki", "kitakyushu", "kobe", "kochi", "kumamoto", "kyoto", "mie", "miyagi", "miyazaki", "nagano", "nagasaki", "nagoya", "nara", "niigata", "oita", "okayama", "okinawa", "osaka", "saga", "saitama", "sapporo", "sendai", "shiga", "shimane", "shizuoka", "tochigi", "tokushima", "tokyo", "tottori", "toyama", "wakayama", "yamagata", "yamaguchi", "yamanashi", "yokohama"));
        RegisteredDomain.jp2Set = new HashSet<String>(Arrays.asList("metro.tokyo.jp", "pref.aichi.jp", "pref.akita.jp", "pref.aomori.jp", "pref.chiba.jp", "pref.ehime.jp", "pref.fukui.jp", "pref.fukuoka.jp", "pref.fukushima.jp", "pref.gifu.jp", "pref.gunma.jp", "pref.hiroshima.jp", "pref.hokkaido.jp", "pref.hyogo.jp", "pref.ibaraki.jp", "pref.ishikawa.jp", "pref.iwate.jp", "pref.kagawa.jp", "pref.kagoshima.jp", "pref.kanagawa.jp", "pref.kochi.jp", "pref.kumamoto.jp", "pref.kyoto.jp", "pref.mie.jp", "pref.miyagi.jp", "pref.miyazaki.jp", "pref.nagano.jp", "pref.nagasaki.jp", "pref.nara.jp", "pref.niigata.jp", "pref.oita.jp", "pref.okayama.jp", "pref.okinawa.jp", "pref.osaka.jp", "pref.saga.jp", "pref.saitama.jp", "pref.shiga.jp", "pref.shimane.jp", "pref.shizuoka.jp", "pref.tochigi.jp", "pref.tokushima.jp", "pref.tottori.jp", "pref.toyama.jp", "pref.wakayama.jp", "pref.yamagata.jp", "pref.yamaguchi.jp", "pref.yamanashi.jp", "city.chiba.jp", "city.fukuoka.jp", "city.hamamatsu.jp", "city.hiroshima.jp", "city.kawasaki.jp", "city.kitakyushu.jp", "city.kobe.jp", "city.kyoto.jp", "city.nagoya.jp", "city.niigata.jp", "city.okayama.jp", "city.osaka.jp", "city.sagamihara.jp", "city.saitama.jp", "city.sapporo.jp", "city.sendai.jp", "city.shizuoka.jp", "city.yokohama.jp"));
        RegisteredDomain.usStateSet = new HashSet<String>(Arrays.asList("ak", "al", "ar", "as", "az", "ca", "co", "ct", "dc", "de", "fl", "ga", "gu", "hi", "ia", "id", "il", "in", "ks", "ky", "la", "ma", "md", "me", "mi", "mn", "mo", "ms", "mt", "nc", "nd", "ne", "nh", "nj", "nm", "nv", "ny", "oh", "ok", "or", "pa", "pr", "ri", "sc", "sd", "tn", "tx", "ut", "vi", "vt", "va", "wa", "wi", "wv", "wy"));
        RegisteredDomain.usSubStateSet = new HashSet<String>(Arrays.asList("state", "lib", "k12", "cc", "tec", "gen", "cog", "mus", "dst"));
        RegisteredDomain.topMap = new HashMap<String, Set<String>>();
        RegisteredDomain.top3Map = new HashMap<String, Set<String>>();
        RegisteredDomain.topMap.put("ac", new HashSet<String>(Arrays.asList("com", "co", "edu", "gov", "net", "mil", "org")));
        RegisteredDomain.topMap.put("ae", new HashSet<String>(Arrays.asList("co", "net", "org", "sch", "ac", "gov", "mil")));
        RegisteredDomain.topMap.put("aero", new HashSet<String>(Arrays.asList("accident-investigation", "accident-prevention", "aerobatic", "aeroclub", "aerodrome", "agents", "aircraft", "airline", "airport", "air-surveillance", "airtraffic", "air-traffic-control", "ambulance", "amusement", "association", "author", "ballooning", "broker", "caa", "cargo", "catering", "certification", "championship", "charter", "civilaviation", "club", "conference", "consultant", "consulting", "control", "council", "crew", "design", "dgca", "educator", "emergency", "engine", "engineer", "entertainment", "equipment", "exchange", "express", "federation", "flight", "freight", "fuel", "gliding", "government", "groundhandling", "group", "hanggliding", "homebuilt", "insurance", "journal", "journalist", "leasing", "logistics", "magazine", "maintenance", "marketplace", "media", "microlight", "modelling", "navigation", "parachuting", "paragliding", "passenger-association", "pilot", "press", "production", "recreation", "repbody", "res", "research", "rotorcraft", "safety", "scientist", "services", "show", "skydiving", "software", "student", "taxi", "trader", "trading", "trainer", "union", "workinggroup", "works")));
        RegisteredDomain.topMap.put("ag", new HashSet<String>(Arrays.asList("com", "org", "net", "co", "nom")));
        RegisteredDomain.topMap.put("ai", new HashSet<String>(Arrays.asList("off", "com", "net", "org")));
        RegisteredDomain.topMap.put("al", new HashSet<String>(Arrays.asList("com", "edu", "gov", "mil", "net", "org")));
        RegisteredDomain.topMap.put("an", new HashSet<String>(Arrays.asList("com")));
        RegisteredDomain.topMap.put("ao", new HashSet<String>(Arrays.asList("ed", "gv", "og", "co", "pb", "it")));
        RegisteredDomain.topMap.put("at", new HashSet<String>(Arrays.asList("ac", "co", "gv", "or", "biz", "info", "priv")));
        RegisteredDomain.topMap.put("az", new HashSet<String>(Arrays.asList("com", "net", "int", "gov", "org", "edu", "info", "pp", "mil", "name", "biz")));
        RegisteredDomain.topMap.put("ba", new HashSet<String>(Arrays.asList("org", "net", "edu", "gov", "mil", "unbi", "unmo", "unsa", "untz", "unze", "co", "com", "rs")));
        RegisteredDomain.topMap.put("bb", new HashSet<String>(Arrays.asList("biz", "com", "edu", "gov", "info", "net", "org", "store")));
        RegisteredDomain.topMap.put("bg", new HashSet<String>(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9")));
        RegisteredDomain.topMap.put("bh", new HashSet<String>(Arrays.asList("com", "info", "cc", "edu", "biz", "net", "org", "gov")));
        RegisteredDomain.topMap.put("bi", new HashSet<String>(Arrays.asList("co", "com", "edu", "gov", "info", "or", "org")));
        RegisteredDomain.topMap.put("bj", new HashSet<String>(Arrays.asList("asso", "barreau", "com", "edu", "gouv", "gov", "mil")));
        RegisteredDomain.topMap.put("bo", new HashSet<String>(Arrays.asList("com", "edu", "gov", "gob", "int", "org", "net", "mil", "tv")));
        RegisteredDomain.topMap.put("br", new HashSet<String>(Arrays.asList("adm", "adv", "agr", "am", "arq", "art", "ato", "b", "bio", "blog", "bmd", "cim", "cng", "cnt", "com", "coop", "ecn", "edu", "emp", "eng", "esp", "etc", "eti", "far", "flog", "fm", "fnd", "fot", "fst", "g12", "ggf", "gov", "imb", "ind", "inf", "jor", "jus", "lel", "mat", "med", "mil", "mus", "net", "nom", "not", "ntr", "odo", "org", "ppg", "pro", "psc", "psi", "qsl", "radio", "rec", "slg", "srv", "taxi", "teo", "tmp", "trd", "tur", "tv", "vet", "vlog", "wiki", "zlg")));
        RegisteredDomain.topMap.put("bw", new HashSet<String>(Arrays.asList("co", "gov", "org")));
        RegisteredDomain.topMap.put("by", new HashSet<String>(Arrays.asList("gov", "mil", "com", "of")));
        RegisteredDomain.topMap.put("ca", new HashSet<String>(Arrays.asList("ab", "bc", "mb", "nb", "nf", "nl", "ns", "nt", "nu", "on", "pe", "qc", "sk", "yk", "gc")));
        RegisteredDomain.topMap.put("ci", new HashSet<String>(Arrays.asList("org", "or", "com", "co", "edu", "ed", "ac", "net", "go", "asso", "xn--aroport-bya", "int", "presse", "md", "gouv")));
        RegisteredDomain.topMap.put("com", new HashSet<String>(Arrays.asList("ad", "ar", "br", "cn", "de", "eu", "gb", "gr", "hu", "jpn", "kr", "no", "qc", "ru", "sa", "se", "uk", "us", "uy", "za")));
        RegisteredDomain.topMap.put("cm", new HashSet<String>(Arrays.asList("co", "com", "gov", "net")));
        RegisteredDomain.topMap.put("cn", new HashSet<String>(Arrays.asList("ac", "com", "edu", "gov", "net", "org", "mil", "xn--55qx5d", "xn--io0a7i", "ah", "bj", "cq", "fj", "gd", "gs", "gz", "gx", "ha", "hb", "he", "hi", "hl", "hn", "jl", "js", "jx", "ln", "nm", "nx", "qh", "sc", "sd", "sh", "sn", "sx", "tj", "xj", "xz", "yn", "zj", "hk", "mo", "tw")));
        RegisteredDomain.topMap.put("co", new HashSet<String>(Arrays.asList("arts", "com", "edu", "firm", "gov", "info", "int", "mil", "net", "nom", "org", "rec", "web")));
        RegisteredDomain.topMap.put("cr", new HashSet<String>(Arrays.asList("ac", "co", "ed", "fi", "go", "or", "sa")));
        RegisteredDomain.topMap.put("cu", new HashSet<String>(Arrays.asList("com", "edu", "org", "net", "gov", "inf")));
        RegisteredDomain.topMap.put("do", new HashSet<String>(Arrays.asList("com", "edu", "org", "net", "gov", "gob", "web", "art", "sld", "mil")));
        RegisteredDomain.topMap.put("dz", new HashSet<String>(Arrays.asList("com", "org", "net", "gov", "edu", "asso", "pol", "art")));
        RegisteredDomain.topMap.put("ec", new HashSet<String>(Arrays.asList("com", "info", "net", "fin", "k12", "med", "pro", "org", "edu", "gov", "gob", "mil")));
        RegisteredDomain.topMap.put("ee", new HashSet<String>(Arrays.asList("edu", "gov", "riik", "lib", "med", "com", "pri", "aip", "org", "fie")));
        RegisteredDomain.topMap.put("eg", new HashSet<String>(Arrays.asList("com", "edu", "eun", "gov", "mil", "name", "net", "org", "sci")));
        RegisteredDomain.topMap.put("es", new HashSet<String>(Arrays.asList("com", "nom", "org", "gob", "edu")));
        RegisteredDomain.topMap.put("eu", new HashSet<String>(Arrays.asList("europa")));
        RegisteredDomain.topMap.put("fr", new HashSet<String>(Arrays.asList("com", "asso", "nom", "prd", "presse", "tm", "aeroport", "assedic", "avocat", "avoues", "cci", "chambagri", "chirurgiens-dentistes", "experts-comptables", "geometre-expert", "gouv", "greta", "huissier-justice", "medecin", "notaires", "pharmacien", "port", "veterinaire")));
        RegisteredDomain.topMap.put("ge", new HashSet<String>(Arrays.asList("com", "edu", "gov", "org", "mil", "net", "pvt")));
        RegisteredDomain.topMap.put("gg", new HashSet<String>(Arrays.asList("co", "org", "net", "sch", "gov")));
        RegisteredDomain.topMap.put("gh", new HashSet<String>(Arrays.asList("com", "edu", "gov", "org", "mil")));
        RegisteredDomain.topMap.put("gi", new HashSet<String>(Arrays.asList("com", "ltd", "gov", "mod", "edu", "org")));
        RegisteredDomain.topMap.put("gn", new HashSet<String>(Arrays.asList("ac", "com", "edu", "gov", "org", "net")));
        RegisteredDomain.topMap.put("gp", new HashSet<String>(Arrays.asList("com", "net", "mobi", "edu", "org", "asso")));
        RegisteredDomain.topMap.put("gr", new HashSet<String>(Arrays.asList("com", "co", "net", "edu", "org", "gov", "mil", "mod", "sch")));
        RegisteredDomain.topMap.put("gy", new HashSet<String>(Arrays.asList("co", "com", "net", "org", "edu", "gov")));
        RegisteredDomain.topMap.put("hk", new HashSet<String>(Arrays.asList("com", "edu", "gov", "idv", "net", "org", "xn--55qx5d", "xn--wcvs22d", "xn--mxtq1m", "xn--gmqw5a", "xn--od0alg", "xn--uc0atv")));
        RegisteredDomain.topMap.put("xn--j6w193g", new HashSet<String>(Arrays.asList("xn--55qx5d", "xn--wcvs22d", "xn--mxtq1m", "xn--gmqw5a", "xn--od0alg", "xn--uc0atv")));
        RegisteredDomain.topMap.put("hn", new HashSet<String>(Arrays.asList("com", "edu", "org", "net", "mil", "gob")));
        RegisteredDomain.topMap.put("hr", new HashSet<String>(Arrays.asList("iz.hr", "from.hr", "name.hr", "com.hr")));
        RegisteredDomain.topMap.put("ht", new HashSet<String>(Arrays.asList("com", "shop", "firm", "info", "adult", "net", "pro", "org", "med", "art", "coop", "pol", "asso", "edu", "rel", "gouv", "perso")));
        RegisteredDomain.topMap.put("hu", new HashSet<String>(Arrays.asList("co", "info", "org", "priv", "sport", "tm", "2000", "agrar", "bolt", "casino", "city", "erotica", "erotika", "film", "forum", "games", "hotel", "ingatlan", "jogasz", "konyvelo", "lakas", "media", "news", "reklam", "sex", "shop", "suli", "szex", "tozsde", "utazas", "video")));
        RegisteredDomain.topMap.put("id", new HashSet<String>(Arrays.asList("ac", "co", "go", "mil", "net", "or", "sch", "web")));
        RegisteredDomain.topMap.put("im", new HashSet<String>(Arrays.asList("co.im", "com", "net.im", "gov.im", "org.im", "ac.im")));
        RegisteredDomain.topMap.put("in", new HashSet<String>(Arrays.asList("co", "firm", "ernet", "net", "org", "gen", "ind", "nic", "ac", "edu", "res", "gov", "mil")));
        RegisteredDomain.topMap.put("iq", new HashSet<String>(Arrays.asList("gov", "edu", "mil", "com", "org", "net")));
        RegisteredDomain.topMap.put("ir", new HashSet<String>(Arrays.asList("ac", "co", "gov", "id", "net", "org", "sch")));
        RegisteredDomain.topMap.put("is", new HashSet<String>(Arrays.asList("net", "com", "edu", "gov", "org", "int")));
        RegisteredDomain.topMap.put("it", new HashSet<String>(Arrays.asList("gov", "edu", "agrigento", "ag", "alessandria", "al", "ancona", "an", "aosta", "aoste", "ao", "arezzo", "ar", "ascoli-piceno", "ascolipiceno", "ap", "asti", "at", "avellino", "av", "bari", "ba", "andria-barletta-trani", "andriabarlettatrani", "trani-barletta-andria", "tranibarlettaandria", "barletta-trani-andria", "barlettatraniandria", "andria-trani-barletta", "andriatranibarletta", "trani-andria-barletta", "traniandriabarletta", "bt", "belluno", "bl", "benevento", "bn", "bergamo", "bg", "biella", "bi", "bologna", "bo", "bolzano", "bozen", "balsan", "alto-adige", "altoadige", "suedtirol", "bz", "brescia", "bs", "brindisi", "br", "cagliari", "ca", "caltanissetta", "cl", "campobasso", "cb", "carboniaiglesias", "carbonia-iglesias", "iglesias-carbonia", "iglesiascarbonia", "ci", "caserta", "ce", "catania", "ct", "catanzaro", "cz", "chieti", "ch", "como", "co", "cosenza", "cs", "cremona", "cr", "crotone", "kr", "cuneo", "cn", "dell-ogliastra", "dellogliastra", "ogliastra", "og", "enna", "en", "ferrara", "fe", "fermo", "fm", "firenze", "florence", "fi", "foggia", "fg", "forli-cesena", "forlicesena", "cesena-forli", "cesenaforli", "fc", "frosinone", "fr", "genova", "genoa", "ge", "gorizia", "go", "grosseto", "gr", "imperia", "im", "isernia", "is", "laquila", "aquila", "aq", "la-spezia", "laspezia", "sp", "latina", "lt", "lecce", "le", "lecco", "lc", "livorno", "li", "lodi", "lo", "lucca", "lu", "macerata", "mc", "mantova", "mn", "massa-carrara", "massacarrara", "carrara-massa", "carraramassa", "ms", "matera", "mt", "medio-campidano", "mediocampidano", "campidano-medio", "campidanomedio", "vs", "messina", "me", "milano", "milan", "mi", "modena", "mo", "monza", "monza-brianza", "monzabrianza", "monzaebrianza", "monzaedellabrianza", "monza-e-della-brianza", "mb", "napoli", "naples", "na", "novara", "no", "nuoro", "nu", "oristano", "or", "padova", "padua", "pd", "palermo", "pa", "parma", "pr", "pavia", "pv", "perugia", "pg", "pescara", "pe", "pesaro-urbino", "pesarourbino", "urbino-pesaro", "urbinopesaro", "pu", "piacenza", "pc", "pisa", "pi", "pistoia", "pt", "pordenone", "pn", "potenza", "pz", "prato", "po", "ragusa", "rg", "ravenna", "ra", "reggio-calabria", "reggiocalabria", "rc", "reggio-emilia", "reggioemilia", "re", "rieti", "ri", "rimini", "rn", "roma", "rome", "rm", "rovigo", "ro", "salerno", "sa", "sassari", "ss", "savona", "sv", "siena", "si", "siracusa", "sr", "sondrio", "so", "taranto", "ta", "tempio-olbia", "tempioolbia", "olbia-tempio", "olbiatempio", "ot", "teramo", "te", "terni", "tr", "torino", "turin", "to", "trapani", "tp", "trento", "trentino", "tn", "treviso", "tv", "trieste", "ts", "udine", "ud", "varese", "va", "venezia", "venice", "ve", "verbania", "vb", "vercelli", "vc", "verona", "vr", "vibo-valentia", "vibovalentia", "vv", "vicenza", "vi", "viterbo", "vt")));
        RegisteredDomain.topMap.put("je", new HashSet<String>(Arrays.asList("co", "org", "net", "sch", "gov")));
        RegisteredDomain.topMap.put("jo", new HashSet<String>(Arrays.asList("com", "org", "net", "edu", "sch", "gov", "mil", "name")));
        RegisteredDomain.topMap.put("jp", new HashSet<String>(Arrays.asList("ac", "ad", "co", "ed", "go", "gr", "lg", "ne", "or")));
        RegisteredDomain.topMap.put("kg", new HashSet<String>(Arrays.asList("org", "net", "com", "edu", "gov", "mil")));
        RegisteredDomain.topMap.put("ki", new HashSet<String>(Arrays.asList("edu", "biz", "net", "org", "gov", "info", "com")));
        RegisteredDomain.topMap.put("km", new HashSet<String>(Arrays.asList("org", "nom", "gov", "prd", "tm", "edu", "mil", "ass", "com", "coop", "asso", "presse", "medecin", "notaires", "pharmaciens", "veterinaire", "gouv")));
        RegisteredDomain.topMap.put("kn", new HashSet<String>(Arrays.asList("net", "org", "edu", "gov")));
        RegisteredDomain.topMap.put("kp", new HashSet<String>(Arrays.asList("com", "edu", "gov", "org", "rep", "tra")));
        RegisteredDomain.topMap.put("kr", new HashSet<String>(Arrays.asList("ac", "co", "es", "go", "hs", "kg", "mil", "ms", "ne", "or", "pe", "re", "sc", "busan", "chungbuk", "chungnam", "daegu", "daejeon", "gangwon", "gwangju", "gyeongbuk", "gyeonggi", "gyeongnam", "incheon", "jeju", "jeonbuk", "jeonnam", "seoul", "ulsan")));
        RegisteredDomain.topMap.put("kz", new HashSet<String>(Arrays.asList("org", "edu", "net", "gov", "mil", "com")));
        RegisteredDomain.topMap.put("la", new HashSet<String>(Arrays.asList("int", "net", "info", "edu", "gov", "per", "com", "org", "c")));
        RegisteredDomain.topMap.put("lc", new HashSet<String>(Arrays.asList("com", "net", "co", "org", "edu", "gov", "l.lc", "p.lc")));
        RegisteredDomain.topMap.put("lk", new HashSet<String>(Arrays.asList("gov", "sch", "net", "int", "com", "org", "edu", "ngo", "soc", "web", "ltd", "assn", "grp", "hotel")));
        RegisteredDomain.topMap.put("ls", new HashSet<String>(Arrays.asList("co", "gov", "ac", "org")));
        RegisteredDomain.topMap.put("lv", new HashSet<String>(Arrays.asList("com", "edu", "gov", "org", "mil", "id", "net", "asn", "conf")));
        RegisteredDomain.topMap.put("ly", new HashSet<String>(Arrays.asList("com", "net", "gov", "plc", "edu", "sch", "med", "org", "id")));
        RegisteredDomain.topMap.put("ma", new HashSet<String>(Arrays.asList("co", "net", "gov", "org", "ac", "press")));
        RegisteredDomain.topMap.put("me", new HashSet<String>(Arrays.asList("co", "net", "org", "edu", "ac", "gov", "its", "priv")));
        RegisteredDomain.topMap.put("mg", new HashSet<String>(Arrays.asList("org", "nom", "gov", "prd", "tm", "edu", "mil", "com")));
        RegisteredDomain.topMap.put("mk", new HashSet<String>(Arrays.asList("com", "org", "net", "edu", "gov", "inf", "name", "pro")));
        RegisteredDomain.topMap.put("ml", new HashSet<String>(Arrays.asList("com", "edu", "gouv", "gov", "net", "org", "presse")));
        RegisteredDomain.topMap.put("mn", new HashSet<String>(Arrays.asList("gov", "edu", "org")));
        RegisteredDomain.topMap.put("mp", new HashSet<String>(Arrays.asList("gov", "co", "org")));
        RegisteredDomain.topMap.put("mu", new HashSet<String>(Arrays.asList("com", "net", "org", "gov", "ac", "co", "or")));
        RegisteredDomain.topMap.put("museum", new HashSet<String>(Arrays.asList("academy", "agriculture", "air", "airguard", "alabama", "alaska", "amber", "ambulance", "american", "americana", "americanantiques", "americanart", "amsterdam", "and", "annefrank", "anthro", "anthropology", "antiques", "aquarium", "arboretum", "archaeological", "archaeology", "architecture", "art", "artanddesign", "artcenter", "artdeco", "arteducation", "artgallery", "arts", "artsandcrafts", "asmatart", "assassination", "assisi", "association", "astronomy", "atlanta", "austin", "australia", "automotive", "aviation", "axis", "badajoz", "baghdad", "bahn", "bale", "baltimore", "barcelona", "baseball", "basel", "baths", "bauern", "beauxarts", "beeldengeluid", "bellevue", "bergbau", "berkeley", "berlin", "bern", "bible", "bilbao", "bill", "birdart", "birthplace", "bonn", "boston", "botanical", "botanicalgarden", "botanicgarden", "botany", "brandywinevalley", "brasil", "bristol", "british", "britishcolumbia", "broadcast", "brunel", "brussel", "brussels", "bruxelles", "building", "burghof", "bus", "bushey", "cadaques", "california", "cambridge", "can", "canada", "capebreton", "carrier", "cartoonart", "casadelamoneda", "castle", "castres", "celtic", "center", "chattanooga", "cheltenham", "chesapeakebay", "chicago", "children", "childrens", "childrensgarden", "chiropractic", "chocolate", "christiansburg", "cincinnati", "cinema", "circus", "civilisation", "civilization", "civilwar", "clinton", "clock", "coal", "coastaldefence", "cody", "coldwar", "collection", "colonialwilliamsburg", "coloradoplateau", "columbia", "columbus", "communication", "communications", "community", "computer", "computerhistory", "xn--comunicaes-v6a2o", "contemporary", "contemporaryart", "convent", "copenhagen", "corporation", "xn--correios-e-telecomunicaes-ghc29a", "corvette", "costume", "countryestate", "county", "crafts", "cranbrook", "creation", "cultural", "culturalcenter", "culture", "cyber", "cymru", "dali", "dallas", "database", "ddr", "decorativearts", "delaware", "delmenhorst", "denmark", "depot", "design", "detroit", "dinosaur", "discovery", "dolls", "donostia", "durham", "eastafrica", "eastcoast", "education", "educational", "egyptian", "eisenbahn", "elburg", "elvendrell", "embroidery", "encyclopedic", "england", "entomology", "environment", "environmentalconservation", "epilepsy", "essex", "estate", "ethnology", "exeter", "exhibition", "family", "farm", "farmequipment", "farmers", "farmstead", "field", "figueres", "filatelia", "film", "fineart", "finearts", "finland", "flanders", "florida", "force", "fortmissoula", "fortworth", "foundation", "francaise", "frankfurt", "franziskaner", "freemasonry", "freiburg", "fribourg", "frog", "fundacio", "furniture", "gallery", "garden", "gateway", "geelvinck", "gemological", "geology", "georgia", "giessen", "glas", "glass", "gorge", "grandrapids", "graz", "guernsey", "halloffame", "hamburg", "handson", "harvestcelebration", "hawaii", "health", "heimatunduhren", "hellas", "helsinki", "hembygdsforbund", "heritage", "histoire", "historical", "historicalsociety", "historichouses", "historisch", "historisches", "history", "historyofscience", "horology", "house", "humanities", "illustration", "imageandsound", "indian", "indiana", "indianapolis", "indianmarket", "intelligence", "interactive", "iraq", "iron", "isleofman", "jamison", "jefferson", "jerusalem", "jewelry", "jewish", "jewishart", "jfk", "journalism", "judaica", "judygarland", "juedisches", "juif", "karate", "karikatur", "kids", "koebenhavn", "koeln", "kunst", "kunstsammlung", "kunstunddesign", "labor", "labour", "lajolla", "lancashire", "landes", "lans", "xn--lns-qla", "larsson", "lewismiller", "lincoln", "linz", "living", "livinghistory", "localhistory", "london", "losangeles", "louvre", "loyalist", "lucerne", "luxembourg", "luzern", "mad", "madrid", "mallorca", "manchester", "mansion", "mansions", "manx", "marburg", "maritime", "maritimo", "maryland", "marylhurst", "media", "medical", "medizinhistorisches", "meeres", "memorial", "mesaverde", "michigan", "midatlantic", "military", "mill", "miners", "mining", "minnesota", "missile", "missoula", "modern", "moma", "money", "monmouth", "monticello", "montreal", "moscow", "motorcycle", "muenchen", "muenster", "mulhouse", "muncie", "museet", "museumcenter", "museumvereniging", "music", "national", "nationalfirearms", "nationalheritage", "nativeamerican", "naturalhistory", "naturalhistorymuseum", "naturalsciences", "nature", "naturhistorisches", "natuurwetenschappen", "naumburg", "naval", "nebraska", "neues", "newhampshire", "newjersey", "newmexico", "newport", "newspaper", "newyork", "niepce", "norfolk", "north", "nrw", "nuernberg", "nuremberg", "nyc", "nyny", "oceanographic", "oceanographique", "omaha", "online", "ontario", "openair", "oregon", "oregontrail", "otago", "oxford", "pacific", "paderborn", "palace", "paleo", "palmsprings", "panama", "paris", "pasadena", "pharmacy", "philadelphia", "philadelphiaarea", "philately", "phoenix", "photography", "pilots", "pittsburgh", "planetarium", "plantation", "plants", "plaza", "portal", "portland", "portlligat", "posts-and-telecommunications", "preservation", "presidio", "press", "project", "public", "pubol", "quebec", "railroad", "railway", "research", "resistance", "riodejaneiro", "rochester", "rockart", "roma", "russia", "saintlouis", "salem", "salvadordali", "salzburg", "sandiego", "sanfrancisco", "santabarbara", "santacruz", "santafe", "saskatchewan", "satx", "savannahga", "schlesisches", "schoenbrunn", "schokoladen", "school", "schweiz", "science", "scienceandhistory", "scienceandindustry", "sciencecenter", "sciencecenters", "science-fiction", "sciencehistory", "sciences", "sciencesnaturelles", "scotland", "seaport", "settlement", "settlers", "shell", "sherbrooke", "sibenik", "silk", "ski", "skole", "society", "sologne", "soundandvision", "southcarolina", "southwest", "space", "spy", "square", "stadt", "stalbans", "starnberg", "state", "stateofdelaware", "station", "steam", "steiermark", "stjohn", "stockholm", "stpetersburg", "stuttgart", "suisse", "surgeonshall", "surrey", "svizzera", "sweden", "sydney", "tank", "tcm", "technology", "telekommunikation", "television", "texas", "textile", "theater", "time", "timekeeping", "topology", "torino", "touch", "town", "transport", "tree", "trolley", "trust", "trustee", "uhren", "ulm", "undersea", "university", "usa", "usantiques", "usarts", "uscountryestate", "usculture", "usdecorativearts", "usgarden", "ushistory", "ushuaia", "uslivinghistory", "utah", "uvic", "valley", "vantaa", "versailles", "viking", "village", "virginia", "virtual", "virtuel", "vlaanderen", "volkenkunde", "wales", "wallonie", "war", "washingtondc", "watchandclock", "watch-and-clock", "western", "westfalen", "whaling", "wildlife", "williamsburg", "windmill", "workshop", "york", "yorkshire", "yosemite", "youth", "zoological", "zoology", "xn--9dbhblg6di", "xn--h1aegh")));
        RegisteredDomain.topMap.put("mv", new HashSet<String>(Arrays.asList("aero", "biz", "com", "coop", "edu", "gov", "info", "int", "mil", "museum", "name", "net", "org", "pro")));
        RegisteredDomain.topMap.put("mw", new HashSet<String>(Arrays.asList("ac", "biz", "co", "com", "coop", "edu", "gov", "int", "museum", "net", "org")));
        RegisteredDomain.topMap.put("mx", new HashSet<String>(Arrays.asList("com", "org", "gob", "edu", "net")));
        RegisteredDomain.topMap.put("my", new HashSet<String>(Arrays.asList("com", "net", "org", "gov", "edu", "mil", "name", "sch")));
        RegisteredDomain.topMap.put("na", new HashSet<String>(Arrays.asList("co", "com", "org", "edu", "edunet", "net", "alt", "biz", "info")));
        RegisteredDomain.topMap.put("nc", new HashSet<String>(Arrays.asList("asso", "nom")));
        RegisteredDomain.topMap.put("net", new HashSet<String>(Arrays.asList("gb", "se", "uk", "za")));
        RegisteredDomain.topMap.put("ng", new HashSet<String>(Arrays.asList("name", "sch", "mil", "mobi", "com", "edu", "gov", "net", "org")));
        RegisteredDomain.topMap.put("nf", new HashSet<String>(Arrays.asList("com", "net", "per", "rec", "web", "arts", "firm", "info", "other", "store")));
        RegisteredDomain.topMap.put("no", new HashSet<String>(Arrays.asList("fhs", "vgs", "fylkesbibl", "folkebibl", "museum", "idrett", "priv", "mil", "stat", "dep", "kommune", "herad", "aa", "ah", "bu", "fm", "hl", "hm", "jan-mayen", "mr", "nl", "nt", "of", "ol", "oslo", "rl", "sf", "st", "svalbard", "tm", "tr", "va", "vf", "akrehamn", "xn--krehamn-dxa", "algard", "xn--lgrd-poac", "arna", "brumunddal", "bryne", "bronnoysund", "xn--brnnysund-m8ac", "drobak", "xn--drbak-wua", "egersund", "fetsund", "floro", "xn--flor-jra", "fredrikstad", "hokksund", "honefoss", "xn--hnefoss-q1a", "jessheim", "jorpeland", "xn--jrpeland-54a", "kirkenes", "kopervik", "krokstadelva", "langevag", "xn--langevg-jxa", "leirvik", "mjondalen", "xn--mjndalen-64a", "mo-i-rana", "mosjoen", "xn--mosjen-eya", "nesoddtangen", "orkanger", "osoyro", "xn--osyro-wua", "raholt", "xn--rholt-mra", "sandnessjoen", "xn--sandnessjen-ogb", "skedsmokorset", "slattum", "spjelkavik", "stathelle", "stavern", "stjordalshalsen", "xn--stjrdalshalsen-sqb", "tananger", "tranby", "vossevangen", "tranby", "vossevangen", "afjord", "xn--fjord-lra", "agdenes", "al", "xn--l-1fa", "alesund", "xn--lesund-hua", "alstahaug", "alta", "xn--lt-liac", "alaheadju", "xn--laheadju-7ya", "alvdal", "amli", "xn--mli-tla", "amot", "xn--mot-tla", "andebu", "andoy", "xn--andy-ira", "andasuolo", "ardal", "xn--rdal-poa", "aremark", "arendal", "xn--s-1fa", "aseral", "xn--seral-lra", "asker", "askim", "askvoll", "askoy", "xn--asky-ira", "asnes", "xn--snes-poa", "audnedaln", "aukra", "aure", "aurland", "aurskog-holand", "xn--aurskog-hland-jnb", "austevoll", "austrheim", "averoy", "xn--avery-yua", "balestrand", "ballangen", "balat", "xn--blt-elab", "balsfjord", "bahccavuotna", "xn--bhccavuotna-k7a", "bamble", "bardu", "beardu", "beiarn", "bajddar", "xn--bjddar-pta", "baidar", "xn--bidr-5nac", "berg", "bergen", "berlevag", "xn--berlevg-jxa", "bearalvahki", "xn--bearalvhki-y4a", "bindal", "birkenes", "bjarkoy", "xn--bjarky-fya", "bjerkreim", "bjugn", "bodo", "xn--bod-2na", "badaddja", "xn--bdddj-mrabd", "budejju", "bokn", "bremanger", "bronnoy", "xn--brnny-wuac", "bygland", "bykle", "barum", "xn--brum-voa", "bievat", "xn--bievt-0qa", "bomlo", "xn--bmlo-gra", "batsfjord", "xn--btsfjord-9za", "bahcavuotna", "xn--bhcavuotna-s4a", "dovre", "drammen", "drangedal", "dyroy", "xn--dyry-ira", "donna", "xn--dnna-gra", "eid", "eidfjord", "eidsberg", "eidskog", "eidsvoll", "eigersund", "elverum", "enebakk", "engerdal", "etne", "etnedal", "evenes", "evenassi", "xn--eveni-0qa01ga", "evje-og-hornnes", "farsund", "fauske", "fuossko", "fuoisku", "fedje", "fet", "finnoy", "xn--finny-yua", "fitjar", "fjaler", "fjell", "flakstad", "flatanger", "flekkefjord", "flesberg", "flora", "fla", "xn--fl-zia", "folldal", "forsand", "fosnes", "frei", "frogn", "froland", "frosta", "frana", "xn--frna-woa", "froya", "xn--frya-hra", "fusa", "fyresdal", "forde", "xn--frde-gra", "gamvik", "gangaviika", "xn--ggaviika-8ya47h", "gaular", "gausdal", "gildeskal", "xn--gildeskl-g0a", "giske", "gjemnes", "gjerdrum", "gjerstad", "gjesdal", "gjovik", "xn--gjvik-wua", "gloppen", "gol", "gran", "grane", "granvin", "gratangen", "grimstad", "grong", "kraanghke", "xn--kranghke-b0a", "grue", "gulen", "hadsel", "halden", "halsa", "hamar", "hamaroy", "habmer", "xn--hbmer-xqa", "hapmir", "xn--hpmir-xqa", "hammerfest", "hammarfeasta", "xn--hmmrfeasta-s4ac", "haram", "hareid", "harstad", "hasvik", "aknoluokta", "xn--koluokta-7ya57h", "hattfjelldal", "aarborte", "haugesund", "hemne", "hemnes", "hemsedal", "hitra", "hjartdal", "hjelmeland", "hobol", "xn--hobl-ira", "hof", "hol", "hole", "holmestrand", "holtalen", "xn--holtlen-hxa", "hornindal", "horten", "hurdal", "hurum", "hvaler", "hyllestad", "hagebostad", "xn--hgebostad-g3a", "hoyanger", "xn--hyanger-q1a", "hoylandet", "xn--hylandet-54a", "ha", "xn--h-2fa", "ibestad", "inderoy", "xn--indery-fya", "iveland", "jevnaker", "jondal", "jolster", "xn--jlster-bya", "karasjok", "karasjohka", "xn--krjohka-hwab49j", "karlsoy", "galsa", "xn--gls-elac", "karmoy", "xn--karmy-yua", "kautokeino", "guovdageaidnu", "klepp", "klabu", "xn--klbu-woa", "kongsberg", "kongsvinger", "kragero", "xn--krager-gya", "kristiansand", "kristiansund", "krodsherad", "xn--krdsherad-m8a", "kvalsund", "rahkkeravju", "xn--rhkkervju-01af", "kvam", "kvinesdal", "kvinnherad", "kviteseid", "kvitsoy", "xn--kvitsy-fya", "kvafjord", "xn--kvfjord-nxa", "giehtavuoatna", "kvanangen", "xn--kvnangen-k0a", "navuotna", "xn--nvuotna-hwa", "kafjord", "xn--kfjord-iua", "gaivuotna", "xn--givuotna-8ya", "larvik", "lavangen", "lavagis", "loabat", "xn--loabt-0qa", "lebesby", "davvesiida", "leikanger", "leirfjord", "leka", "leksvik", "lenvik", "leangaviika", "xn--leagaviika-52b", "lesja", "levanger", "lier", "lierne", "lillehammer", "lillesand", "lindesnes", "lindas", "xn--linds-pra", "lom", "loppa", "lahppi", "xn--lhppi-xqa", "lund", "lunner", "luroy", "xn--lury-ira", "luster", "lyngdal", "lyngen", "ivgu", "lardal", "lerdal", "xn--lrdal-sra", "lodingen", "xn--ldingen-q1a", "lorenskog", "xn--lrenskog-54a", "loten", "xn--lten-gra", "malvik", "masoy", "xn--msy-ula0h", "muosat", "xn--muost-0qa", "mandal", "marker", "marnardal", "masfjorden", "meland", "meldal", "melhus", "meloy", "xn--mely-ira", "meraker", "xn--merker-kua", "moareke", "xn--moreke-jua", "midsund", "midtre-gauldal", "modalen", "modum", "molde", "moskenes", "moss", "mosvik", "malselv", "xn--mlselv-iua", "malatvuopmi", "xn--mlatvuopmi-s4a", "namdalseid", "aejrie", "namsos", "namsskogan", "naamesjevuemie", "xn--nmesjevuemie-tcba", "laakesvuemie", "nannestad", "narvik", "narviika", "naustdal", "nedre-eiker", "nesna", "nesodden", "nesseby", "unjarga", "xn--unjrga-rta", "nesset", "nissedal", "nittedal", "nord-aurdal", "nord-fron", "nord-odal", "norddal", "nordkapp", "davvenjarga", "xn--davvenjrga-y4a", "nordre-land", "nordreisa", "raisa", "xn--risa-5na", "nore-og-uvdal", "notodden", "naroy", "xn--nry-yla5g", "notteroy", "xn--nttery-byae", "odda", "oksnes", "xn--ksnes-uua", "oppdal", "oppegard", "xn--oppegrd-ixa", "orkdal", "orland", "xn--rland-uua", "orskog", "xn--rskog-uua", "orsta", "xn--rsta-fra", "os.hedmark", "os.hordaland", "osen", "osteroy", "xn--ostery-fya", "ostre-toten", "xn--stre-toten-zcb", "overhalla", "ovre-eiker", "xn--vre-eiker-k8a", "oyer", "xn--yer-zna", "oygarden", "xn--ygarden-p1a", "oystre-slidre", "xn--ystre-slidre-ujb", "porsanger", "porsangu", "xn--porsgu-sta26f", "porsgrunn", "radoy", "xn--rady-ira", "rakkestad", "rana", "ruovat", "randaberg", "rauma", "rendalen", "rennebu", "rennesoy", "xn--rennesy-v1a", "rindal", "ringebu", "ringerike", "ringsaker", "rissa", "risor", "xn--risr-ira", "roan", "rollag", "rygge", "ralingen", "xn--rlingen-mxa", "rodoy", "xn--rdy-0nab", "romskog", "xn--rmskog-bya", "roros", "xn--rros-gra", "rost", "xn--rst-0na", "royken", "xn--ryken-vua", "royrvik", "xn--ryrvik-bya", "rade", "xn--rde-ula", "salangen", "siellak", "saltdal", "salat", "xn--slt-elab", "xn--slat-5na", "samnanger", "sandefjord", "sandnes", "sandoy", "xn--sandy-yua", "sarpsborg", "sauda", "sauherad", "sel", "selbu", "selje", "seljord", "sigdal", "siljan", "sirdal", "skaun", "skedsmo", "ski", "skien", "skiptvet", "skjervoy", "xn--skjervy-v1a", "skierva", "xn--skierv-uta", "skjak", "xn--skjk-soa", "skodje", "skanland", "xn--sknland-fxa", "skanit", "xn--sknit-yqa", "smola", "xn--smla-hra", "snillfjord", "snasa", "xn--snsa-roa", "snoasa", "snaase", "xn--snase-nra", "sogndal", "sokndal", "sola", "solund", "songdalen", "sortland", "spydeberg", "stange", "stavanger", "steigen", "steinkjer", "stjordal", "xn--stjrdal-s1a", "stokke", "stor-elvdal", "stord", "stordal", "storfjord", "omasvuotna", "strand", "stranda", "stryn", "sula", "suldal", "sund", "sunndal", "surnadal", "sveio", "svelvik", "sykkylven", "sogne", "xn--sgne-gra", "somna", "xn--smna-gra", "sondre-land", "xn--sndre-land-0cb", "sor-aurdal", "xn--sr-aurdal-l8a", "sor-fron", "xn--sr-fron-q1a", "sor-odal", "xn--sr-odal-q1a", "sor-varanger", "xn--sr-varanger-ggb", "matta-varjjat", "xn--mtta-vrjjat-k7af", "sorfold", "xn--srfold-bya", "sorreisa", "xn--srreisa-q1a", "sorum", "xn--srum-gra", "tana", "deatnu", "time", "tingvoll", "tinn", "tjeldsund", "dielddanuorri", "tjome", "xn--tjme-hra", "tokke", "tolga", "torsken", "tranoy", "xn--trany-yua", "tromso", "xn--troms-zua", "tromsa", "romsa", "trondheim", "troandin", "trysil", "trana", "xn--trna-woa", "trogstad", "xn--trgstad-r1a", "tvedestrand", "tydal", "tynset", "tysfjord", "divtasvuodna", "divttasvuotna", "tysnes", "tysvar", "xn--tysvr-vra", "tonsberg", "xn--tnsberg-q1a", "ullensaker", "ullensvang", "ulvik", "utsira", "vadso", "xn--vads-jra", "cahcesuolo", "xn--hcesuolo-7ya35b", "vaksdal", "valle", "vang", "vanylven", "vardo", "xn--vard-jra", "varggat", "xn--vrggt-xqad", "vefsn", "vaapste", "vega", "vegarshei", "xn--vegrshei-c0a", "vennesla", "verdal", "verran", "vestby", "vestnes", "vestre-slidre", "vestre-toten", "vestvagoy", "xn--vestvgy-ixa6o", "vevelstad", "vik", "vikna", "vindafjord", "volda", "voss", "varoy", "xn--vry-yla5g", "vagan", "xn--vgan-qoa", "voagat", "vagsoy", "xn--vgsy-qoa0j", "vaga", "xn--vg-yiab")));
        RegisteredDomain.topMap.put("nr", new HashSet<String>(Arrays.asList("biz", "info", "gov", "edu", "org", "net", "com", "co")));
        RegisteredDomain.topMap.put("pa", new HashSet<String>(Arrays.asList("ac", "gob", "com", "org", "sld", "edu", "net", "ing", "abo", "med", "nom")));
        RegisteredDomain.topMap.put("pe", new HashSet<String>(Arrays.asList("edu", "gob", "nom", "mil", "org", "com", "net", "sld")));
        RegisteredDomain.topMap.put("pf", new HashSet<String>(Arrays.asList("com")));
        RegisteredDomain.topMap.put("ph", new HashSet<String>(Arrays.asList("com", "net", "org", "gov", "edu", "ngo", "mil")));
        RegisteredDomain.topMap.put("pk", new HashSet<String>(Arrays.asList("com", "net", "edu", "org", "fam", "biz", "web", "gov", "gob", "gok", "gon", "gop", "gos", "gog", "gkp", "info")));
        RegisteredDomain.topMap.put("pl", new HashSet<String>(Arrays.asList("aid", "agro", "atm", "auto", "biz", "com", "edu", "gmina", "gsm", "info", "mail", "miasta", "media", "mil", "net", "nieruchomosci", "nom", "org", "pc", "powiat", "priv", "realestate", "rel", "sex", "shop", "sklep", "sos", "szkola", "targi", "tm", "tourism", "travel", "turystyka", "art", "gov", "ngo", "augustow", "babia-gora", "bedzin", "beskidy", "bialowieza", "bialystok", "bielawa", "bieszczady", "boleslawiec", "bydgoszcz", "bytom", "cieszyn", "czeladz", "czest", "dlugoleka", "elblag", "elk", "glogow", "gniezno", "gorlice", "grajewo", "ilawa", "jaworzno", "jelenia-gora", "jgora", "kalisz", "kazimierz-dolny", "karpacz", "kartuzy", "kaszuby", "katowice", "kepno", "ketrzyn", "klodzko", "kobierzyce", "kolobrzeg", "konin", "konskowola", "kutno", "lapy", "lebork", "legnica", "lezajsk", "limanowa", "lomza", "lowicz", "lubin", "lukow", "malbork", "malopolska", "mazowsze", "mazury", "mielec", "mielno", "mragowo", "naklo", "nowaruda", "nysa", "olawa", "olecko", "olkusz", "olsztyn", "opoczno", "opole", "ostroda", "ostroleka", "ostrowiec", "ostrowwlkp", "pila", "pisz", "podhale", "podlasie", "polkowice", "pomorze", "pomorskie", "prochowice", "pruszkow", "przeworsk", "pulawy", "radom", "rawa-maz", "rybnik", "rzeszow", "sanok", "sejny", "siedlce", "slask", "slupsk", "sosnowiec", "stalowa-wola", "skoczow", "starachowice", "stargard", "suwalki", "swidnica", "swiebodzin", "swinoujscie", "szczecin", "szczytno", "tarnobrzeg", "tgory", "turek", "tychy", "ustka", "walbrzych", "warmia", "warszawa", "waw", "wegrow", "wielun", "wlocl", "wloclawek", "wodzislaw", "wolomin", "wroclaw", "zachpomor", "zagan", "zarow", "zgora", "zgorzelec", "gda", "gdansk", "krakow", "poznan", "wroc", "co", "lodz", "lublin", "torun")));
        RegisteredDomain.topMap.put("pn", new HashSet<String>(Arrays.asList("gov", "co", "org", "edu", "net")));
        RegisteredDomain.topMap.put("pr", new HashSet<String>(Arrays.asList("com", "net", "org", "gov", "edu", "isla", "pro", "biz", "info", "name", "est", "prof", "ac", "gobierno")));
        RegisteredDomain.topMap.put("pro", new HashSet<String>(Arrays.asList("aca", "bar", "cpa", "jur", "law", "med", "eng")));
        RegisteredDomain.topMap.put("ps", new HashSet<String>(Arrays.asList("edu", "gov", "sec", "plo", "com", "org", "net")));
        RegisteredDomain.topMap.put("pt", new HashSet<String>(Arrays.asList("net", "gov", "org", "edu", "int", "publ", "com", "nome")));
        RegisteredDomain.topMap.put("pw", new HashSet<String>(Arrays.asList("co", "ne", "or", "ed", "go", "belau")));
        RegisteredDomain.topMap.put("qa", new HashSet<String>(Arrays.asList("com", "net", "org", "gov", "edu", "mil")));
        RegisteredDomain.topMap.put("re", new HashSet<String>(Arrays.asList("com", "asso", "nom")));
        RegisteredDomain.topMap.put("ro", new HashSet<String>(Arrays.asList("com", "org", "tm", "nt", "nom", "info", "rec", "arts", "firm", "store", "www")));
        RegisteredDomain.topMap.put("rs", new HashSet<String>(Arrays.asList("co", "org", "edu", "ac", "gov", "in")));
        RegisteredDomain.topMap.put("ru", new HashSet<String>(Arrays.asList("ac", "com", "edu", "int", "net", "org", "pp", "adygeya", "altai", "amur", "arkhangelsk", "astrakhan", "bashkiria", "belgorod", "bir", "bryansk", "buryatia", "cap", "cbg", "chel", "chelyabinsk", "chita", "chukotka", "dagestan", "e-burg", "grozny", "irkutsk", "ivanovo", "izhevsk", "jar", "joshkar-ola", "kalmykia", "kaluga", "kamchatka", "karelia", "kazan", "kchr", "kemerovo", "khabarovsk", "khakassia", "khv", "kirov", "koenig", "komi", "kostroma", "krasnoyarsk", "kuban", "kurgan", "kursk", "lipetsk", "magadan", "mari", "mari-el", "marine", "mordovia", "mosreg", "msk", "murmansk", "nalchik", "nnov", "nov", "novosibirsk", "nsk", "omsk", "orenburg", "oryol", "palana", "penza", "perm", "pskov", "ptz", "rnd", "ryazan", "sakhalin", "samara", "saratov", "simbirsk", "smolensk", "spb", "stavropol", "stv", "surgut", "tambov", "tatarstan", "tom", "tomsk", "tsaritsyn", "tsk", "tula", "tuva", "tver", "tyumen", "udm", "udmurtia", "ulan-ude", "vladikavkaz", "vladimir", "vladivostok", "volgograd", "vologda", "voronezh", "vrn", "vyatka", "yakutia", "yamal", "yaroslavl", "yekaterinburg", "yuzhno-sakhalinsk", "amursk", "baikal", "cmw", "fareast", "jamal", "kms", "k-uralsk", "kustanai", "kuzbass", "magnitka", "mytis", "nakhodka", "nkz", "norilsk", "oskol", "pyatigorsk", "rubtsovsk", "snz", "syzran", "vdonsk", "zgrad", "gov", "mil", "test")));
        RegisteredDomain.topMap.put("rw", new HashSet<String>(Arrays.asList("gov", "net", "edu", "ac", "com", "co", "int", "mil", "gouv")));
        RegisteredDomain.topMap.put("sa", new HashSet<String>(Arrays.asList("com", "net", "org", "gov", "med", "pub", "edu", "sch")));
        RegisteredDomain.topMap.put("sd", new HashSet<String>(Arrays.asList("com", "net", "org", "edu", "med", "gov", "info", "tv")));
        RegisteredDomain.topMap.put("se", new HashSet<String>(Arrays.asList("a", "ac", "b", "bd", "brand", "c", "d", "e", "f", "fh", "fhsk", "fhv", "g", "h", "i", "k", "komforb", "kommunalforbund", "komvux", "l", "lanarb", "lanbib", "m", "n", "naturbruksgymn", "o", "org", "p", "parti", "pp", "press", "r", "s", "sshn", "t", "tm", "u", "w", "x", "y", "z")));
        RegisteredDomain.topMap.put("sg", new HashSet<String>(Arrays.asList("com", "net", "org", "gov", "edu", "per")));
        RegisteredDomain.topMap.put("sh", new HashSet<String>(Arrays.asList("co", "com", "net", "org", "gov", "edu", "nom")));
        RegisteredDomain.topMap.put("sk", new HashSet<String>(Arrays.asList("gov", "edu")));
        RegisteredDomain.topMap.put("sn", new HashSet<String>(Arrays.asList("art", "com", "edu", "gouv", "org", "perso", "univ")));
        RegisteredDomain.topMap.put("so", new HashSet<String>(Arrays.asList("com", "net", "org")));
        RegisteredDomain.topMap.put("sr", new HashSet<String>(Arrays.asList("co", "com", "consulado", "edu", "embaixada", "gov", "mil", "net", "org", "principe", "saotome", "store")));
        RegisteredDomain.topMap.put("sy", new HashSet<String>(Arrays.asList("edu", "gov", "net", "mil", "com", "org", "news")));
        RegisteredDomain.topMap.put("sz", new HashSet<String>(Arrays.asList("co", "ac", "org")));
        RegisteredDomain.topMap.put("th", new HashSet<String>(Arrays.asList("ac", "co", "go", "in", "mi", "net", "or")));
        RegisteredDomain.topMap.put("tj", new HashSet<String>(Arrays.asList("ac", "biz", "co", "com", "edu", "go", "gov", "int", "mil", "name", "net", "nic", "org", "test", "web")));
        RegisteredDomain.topMap.put("tn", new HashSet<String>(Arrays.asList("com", "ens", "fin", "gov", "ind", "intl", "nat", "net", "org", "info", "perso", "tourism", "edunet", "rnrt", "rns", "rnu", "mincom", "agrinet", "defense", "turen")));
        RegisteredDomain.topMap.put("to", new HashSet<String>(Arrays.asList("gov")));
        RegisteredDomain.topMap.put("tt", new HashSet<String>(Arrays.asList("co", "com", "org", "net", "biz", "info", "pro", "int", "coop", "jobs", "mobi", "travel", "museum", "aero", "name", "gov", "edu", "cat", "tel", "mil")));
        RegisteredDomain.topMap.put("tw", new HashSet<String>(Arrays.asList("edu", "gov", "mil", "com", "net", "org", "idv", "game", "ebiz", "club", "xn--zf0ao64a", "xn--uc0atv", "xn--czrw28b")));
        RegisteredDomain.topMap.put("ua", new HashSet<String>(Arrays.asList("com", "edu", "gov", "in", "net", "org", "cherkassy", "chernigov", "chernovtsy", "ck", "cn", "crimea", "cv", "dn", "dnepropetrovsk", "donetsk", "dp", "if", "ivano-frankivsk", "kh", "kharkov", "kherson", "kiev", "kirovograd", "km", "kr", "ks", "lg", "lugansk", "lutsk", "lviv", "mk", "nikolaev", "od", "odessa", "pl", "poltava", "rovno", "rv", "sebastopol", "sumy", "te", "ternopil", "uzhgorod", "vinnica", "vn", "zaporizhzhe", "zp", "zhitomir", "zt", "cr", "lt", "lv", "sb", "sm", "tr", "co", "biz", "in", "ne", "pp", "uz", "dominic")));
        RegisteredDomain.topMap.put("ug", new HashSet<String>(Arrays.asList("co", "ac", "sc", "go", "ne", "or", "org", "com")));
        RegisteredDomain.topMap.put("us", new HashSet<String>(Arrays.asList("dni", "fed", "isa", "kids", "nsn", "kyschools")));
        RegisteredDomain.topMap.put("uz", new HashSet<String>(Arrays.asList("co", "com", "org", "gov", "ac", "edu", "int", "pp", "net")));
        RegisteredDomain.topMap.put("vc", new HashSet<String>(Arrays.asList("com", "net", "org", "gov")));
        RegisteredDomain.topMap.put("vi", new HashSet<String>(Arrays.asList("co", "com", "k12", "net", "org")));
        RegisteredDomain.topMap.put("vn", new HashSet<String>(Arrays.asList("com", "net", "org", "edu", "gov", "int", "ac", "biz", "info", "name", "pro", "health")));
        RegisteredDomain.topMap.put("vu", new HashSet<String>(Arrays.asList("co", "com", "net", "org", "edu", "gov", "de")));
        RegisteredDomain.topMap.put("org", new HashSet<String>(Arrays.asList("ae", "za")));
        RegisteredDomain.topMap.put("pro", new HashSet<String>(Arrays.asList("aca", "bar", "cpa", "jur", "law", "med", "eng")));
        RegisteredDomain.top3Map.put("au", new HashSet<String>(Arrays.asList("act.edu.au", "eq.edu.au", "nsw.edu.au", "nt.edu.au", "qld.edu.au", "sa.edu.au", "tas.edu.au", "vic.edu.au", "wa.edu.au", "act.gov.au", "nsw.gov.au", "nt.gov.au", "qld.gov.au", "sa.gov.au", "tas.gov.au", "vic.gov.au", "wa.gov.au")));
        RegisteredDomain.top3Map.put("im", new HashSet<String>(Arrays.asList("ltd.co.im", "plc.co.im")));
        RegisteredDomain.top3Map.put("no", new HashSet<String>(Arrays.asList("gs.aa.no", "gs.ah.no", "gs.bu.no", "gs.fm.no", "gs.hl.no", "gs.hm.no", "gs.jan-mayen.no", "gs.mr.no", "gs.nl.no", "gs.nt.no", "gs.of.no", "gs.ol.no", "gs.oslo.no", "gs.rl.no", "gs.sf.no", "gs.st.no", "gs.svalbard.no", "gs.tm.no", "gs.tr.no", "gs.va.no", "gs.vf.no", "bo.telemark.no", "xn--b-5ga.telemark.no", "bo.nordland.no", "xn--b-5ga.nordland.no", "heroy.more-og-romsdal.no", "xn--hery-ira.xn--mre-og-romsdal-qqb.no", "heroy.nordland.no", "xn--hery-ira.nordland.no", "nes.akershus.no", "nes.buskerud.no", "os.hedmark.no", "os.hordaland.no", "sande.more-og-romsdal.no", "sande.xn--mre-og-romsdal-qqb.no", "sande.vestfold.no", "valer.ostfold.no", "xn--vler-qoa.xn--stfold-9xa.no", "valer.hedmark.no", "xn--vler-qoa.hedmark.no")));
        RegisteredDomain.top3Map.put("tr", new HashSet<String>(Arrays.asList("gov.nc.tr")));
    }
}
