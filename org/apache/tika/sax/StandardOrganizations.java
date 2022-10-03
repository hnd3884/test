package org.apache.tika.sax;

import java.util.TreeMap;
import java.util.Map;

public class StandardOrganizations
{
    private static final Map<String, String> organizations;
    
    public static Map<String, String> getOrganizations() {
        return StandardOrganizations.organizations;
    }
    
    public static String getOrganzationsRegex() {
        return "(" + String.join("|", StandardOrganizations.organizations.keySet()) + ")";
    }
    
    static {
        (organizations = new TreeMap<String, String>()).put("3GPP", "3rd Generation Partnership Project");
        StandardOrganizations.organizations.put("3GPP2", "3rd Generation Partnership Project 2");
        StandardOrganizations.organizations.put("Accellera", "Accellera Organization");
        StandardOrganizations.organizations.put("A4L", "Access for Learning Community (formerly known as the Schools Interoperability Framework)");
        StandardOrganizations.organizations.put("AES", "Audio Engineering Society");
        StandardOrganizations.organizations.put("AIIM", "Association for Information and Image Management");
        StandardOrganizations.organizations.put("ASAM", "Association for Automation and Measuring Systems - Automotive technology");
        StandardOrganizations.organizations.put("ASHRAE", "American Society of Heating, Refrigerating and Air-Conditioning Engineers (ASHRAE is an international organization, despite its name)");
        StandardOrganizations.organizations.put("ASME", "formerly The American Society of Mechanical Engineers");
        StandardOrganizations.organizations.put("ASTM", "ASTM (American Society for Testing and Materials) International");
        StandardOrganizations.organizations.put("ATIS", "Alliance for Telecommunications Industry Solutions");
        StandardOrganizations.organizations.put("AUTOSAR", "Automotive technology");
        StandardOrganizations.organizations.put("BIPM, CGPM, and CIPM", "Bureau International des Poids et Mesures and the related organizations established under the Metre Convention of 1875.");
        StandardOrganizations.organizations.put("CableLabs", "Cable Television Laboratories");
        StandardOrganizations.organizations.put("CCSDS", "Consultative Committee for Space Data Sciences");
        StandardOrganizations.organizations.put("CISPR", "International Special Committee on Radio Interference");
        StandardOrganizations.organizations.put("CFA", "Compact flash association");
        StandardOrganizations.organizations.put("DCMI", "Dublin Core Metadata Initiative");
        StandardOrganizations.organizations.put("DMTF", "Distributed Management Task Force");
        StandardOrganizations.organizations.put("Ecma International", "Ecma International (previously called ECMA)");
        StandardOrganizations.organizations.put("EKOenergy", "EKOenergy Network managed by environmental NGOs");
        StandardOrganizations.organizations.put("FAI", "F\u00e9d\u00e9ration A\u00e9ronautique Internationale");
        StandardOrganizations.organizations.put("GlobalPlatform", "Secure element and TEE standards");
        StandardOrganizations.organizations.put("GS1", "Global supply chain standards (identification numbers, barcodes, electronic commerce transactions, RFID)");
        StandardOrganizations.organizations.put("HGI", "Home Gateway Initiative");
        StandardOrganizations.organizations.put("HFSB", "Hedge Fund Standards Board");
        StandardOrganizations.organizations.put("IATA", "International Air Transport Association");
        StandardOrganizations.organizations.put("IAU*", "International Arabic Union");
        StandardOrganizations.organizations.put("ICAO", "International Civil Aviation Organization");
        StandardOrganizations.organizations.put("IEC", "International Electrotechnical Commission");
        StandardOrganizations.organizations.put("IEEE", "Institute of Electrical and Electronics Engineers");
        StandardOrganizations.organizations.put("IEEE-SA", "IEEE Standards Association");
        StandardOrganizations.organizations.put("IETF", "Internet Engineering Task Force");
        StandardOrganizations.organizations.put("IFOAM", "International Federation of Organic Agriculture Movements");
        StandardOrganizations.organizations.put("IFSWF", "International Forum of Sovereign Wealth Funds");
        StandardOrganizations.organizations.put("IMO", "International Maritime Organization");
        StandardOrganizations.organizations.put("IMS", "IMS Global Learning Consortium");
        StandardOrganizations.organizations.put("ISO", "International Organization for Standardization");
        StandardOrganizations.organizations.put("IPTC", "International Press Telecommunications Council");
        StandardOrganizations.organizations.put("ITU", "The International Telecommunication Union");
        StandardOrganizations.organizations.put("ITU-R", "ITU Radiocommunications Sector (formerly known as CCIR)");
        StandardOrganizations.organizations.put("CCIR", "Comit\u00e9 Consultatif International pour la Radio, a forerunner of the ITU-R");
        StandardOrganizations.organizations.put("ITU-T", "ITU Telecommunications Sector (formerly known as CCITT)");
        StandardOrganizations.organizations.put("CCITT", "Comit\u00e9 Consultatif International T\u00e9l\u00e9phonique et T\u00e9l\u00e9graphique, renamed ITU-T in 1993");
        StandardOrganizations.organizations.put("ITU-D", "ITU Telecom Development (formerly known as BDT)");
        StandardOrganizations.organizations.put("BDT", "Bureau de d\u00e9veloppement des t\u00e9l\u00e9communications, renamed ITU-D");
        StandardOrganizations.organizations.put("IUPAC", "International Union of Pure and Applied Chemistry");
        StandardOrganizations.organizations.put("Liberty Alliance", "Liberty Alliance");
        StandardOrganizations.organizations.put("Media Grid", "Media Grid Standards Organization");
        StandardOrganizations.organizations.put("NACE International", "Formerly known as National Association of Corrosion Engineers");
        StandardOrganizations.organizations.put("OASIS", "Organization for the Advancement of Structured Information Standards");
        StandardOrganizations.organizations.put("OGC", "Open Geospatial Consortium");
        StandardOrganizations.organizations.put("OHICC", "Organization of Hotel Industry Classification & Certification");
        StandardOrganizations.organizations.put("OMA", "Open Mobile Alliance");
        StandardOrganizations.organizations.put("OMG", "Object Management Group");
        StandardOrganizations.organizations.put("OGF", "Open Grid Forum (merger of Global Grid Forum (GGF) and Enterprise Grid Alliance (EGA))");
        StandardOrganizations.organizations.put("GGF", "Global Grid Forum");
        StandardOrganizations.organizations.put("EGA", "Enterprise Grid Alliance");
        StandardOrganizations.organizations.put("OpenTravel Alliance", "OpenTravel Alliance (previously known as OTA)");
        StandardOrganizations.organizations.put("OTA", "OpenTravel Alliance");
        StandardOrganizations.organizations.put("OSGi", "OSGi Alliance");
        StandardOrganizations.organizations.put("PESC", "P20 Education Standards Council[1]");
        StandardOrganizations.organizations.put("SAI", "Social Accountability International");
        StandardOrganizations.organizations.put("SDA", "Secure Digital Association");
        StandardOrganizations.organizations.put("SNIA", "Storage Networking Industry Association");
        StandardOrganizations.organizations.put("SMPTE", "Society of Motion Picture and Television Engineers");
        StandardOrganizations.organizations.put("SSDA", "Solid State Drive Alliance");
        StandardOrganizations.organizations.put("The Open Group", "The Open Group");
        StandardOrganizations.organizations.put("TIA", "Telecommunications Industry Association");
        StandardOrganizations.organizations.put("TM Forum", "Telemanagement Forum");
        StandardOrganizations.organizations.put("UIC", "International Union of Railways");
        StandardOrganizations.organizations.put("UL", "Underwriters Laboratories");
        StandardOrganizations.organizations.put("UPU", "Universal Postal Union");
        StandardOrganizations.organizations.put("WMO", "World Meteorological Organization");
        StandardOrganizations.organizations.put("W3C", "World Wide Web Consortium");
        StandardOrganizations.organizations.put("WSA", "Website Standards Association");
        StandardOrganizations.organizations.put("WHO", "World Health Organization");
        StandardOrganizations.organizations.put("XSF", "The XMPP Standards Foundation");
        StandardOrganizations.organizations.put("FAO", "Food and Agriculture Organization");
        StandardOrganizations.organizations.put("ARSO", "African Regional Organization for Standarization");
        StandardOrganizations.organizations.put("SADCSTAN", "Southern African Development Community (SADC) Cooperation in Standarization");
        StandardOrganizations.organizations.put("COPANT", "Pan American Standards Commission");
        StandardOrganizations.organizations.put("AMN", "MERCOSUR Standardization Association");
        StandardOrganizations.organizations.put("CROSQ", "CARICOM Regional Organization for Standards and Quality");
        StandardOrganizations.organizations.put("AAQG", "America's Aerospace Quality Group");
        StandardOrganizations.organizations.put("PASC", "Pacific Area Standards Congress");
        StandardOrganizations.organizations.put("ACCSQ", "ASEAN Consultative Committee for Standards and Quality");
        StandardOrganizations.organizations.put("RoyalCert", "RoyalCert International Registrars");
        StandardOrganizations.organizations.put("CEN", "European Committee for Standardization");
        StandardOrganizations.organizations.put("CENELEC", "European Committee for Electrotechnical Standardization");
        StandardOrganizations.organizations.put("URS", "United Registrar of Systems, UK");
        StandardOrganizations.organizations.put("ETSI", "European Telecommunications Standards Institute");
        StandardOrganizations.organizations.put("EASC", "Euro-Asian Council for Standardization, Metrology and Certification");
        StandardOrganizations.organizations.put("IRMM", "Institute for Reference Materials and Measurements (European Union)");
        StandardOrganizations.organizations.put("AIDMO", "Arab Industrial Development and Mining Organization");
        StandardOrganizations.organizations.put("IAU", "International Arabic Union");
        StandardOrganizations.organizations.put("BSI", "British Standards Institution aka BSI Group");
        StandardOrganizations.organizations.put("DStan", "UK Defence Standardization");
        StandardOrganizations.organizations.put("ANSI", "American National Standards Institute");
        StandardOrganizations.organizations.put("ACI", "American Concrete Institute");
        StandardOrganizations.organizations.put("NIST", "National Institute of Standards and Technology");
    }
}
