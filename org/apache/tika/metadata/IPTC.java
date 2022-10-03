package org.apache.tika.metadata;

public interface IPTC
{
    public static final String NAMESPACE_URI_IPTC_CORE = "http://iptc.org/std/Iptc4xmpCore/1.0/xmlns/";
    public static final String NAMESPACE_URI_IPTC_EXT = "http://iptc.org/std/Iptc4xmpExt/2008-02-29/";
    public static final String NAMESPACE_URI_PLUS = "http://ns.useplus.org/ldf/xmp/1.0/";
    public static final String PREFIX_IPTC_CORE = "Iptc4xmpCore";
    public static final String PREFIX_IPTC_EXT = "Iptc4xmpExt";
    public static final String PREFIX_PLUS = "plus";
    public static final Property CITY = Photoshop.CITY;
    public static final Property COUNTRY = Photoshop.COUNTRY;
    public static final Property COUNTRY_CODE = Property.internalText("Iptc4xmpCore:CountryCode");
    public static final Property DESCRIPTION = DublinCore.DESCRIPTION;
    public static final Property HEADLINE = Photoshop.HEADLINE;
    public static final Property INTELLECTUAL_GENRE = Property.internalText("Iptc4xmpCore:IntellectualGenre");
    public static final Property KEYWORDS = DublinCore.SUBJECT;
    public static final Property PROVINCE_OR_STATE = Photoshop.STATE;
    public static final Property SCENE_CODE = Property.internalTextBag("Iptc4xmpCore:Scene");
    public static final Property SUBJECT_CODE = Property.internalTextBag("Iptc4xmpCore:SubjectCode");
    public static final Property SUBLOCATION = Property.internalText("Iptc4xmpCore:Location");
    public static final Property DATE_CREATED = Photoshop.DATE_CREATED;
    public static final Property DESCRIPTION_WRITER = Photoshop.CAPTION_WRITER;
    public static final Property INSTRUCTIONS = Photoshop.INSTRUCTIONS;
    public static final Property JOB_ID = Photoshop.TRANSMISSION_REFERENCE;
    public static final Property TITLE = DublinCore.TITLE;
    public static final Property COPYRIGHT_NOTICE = DublinCore.RIGHTS;
    public static final Property CREATOR = DublinCore.CREATOR;
    public static final Property CREATORS_CONTACT_INFO = Property.internalText("Iptc4xmpCore:CreatorContactInfo");
    public static final Property CREATORS_JOB_TITLE = Photoshop.AUTHORS_POSITION;
    public static final Property CREDIT_LINE = Photoshop.CREDIT;
    public static final Property RIGHTS_USAGE_TERMS = XMPRights.USAGE_TERMS;
    public static final Property SOURCE = Photoshop.SOURCE;
    public static final Property CONTACT_INFO_ADDRESS = Property.internalTextBag("Iptc4xmpCore:CiAdrExtadr");
    public static final Property CONTACT_INFO_CITY = Property.internalText("Iptc4xmpCore:CiAdrCity");
    public static final Property CONTACT_INFO_COUNTRY = Property.internalText("Iptc4xmpCore:CiAdrCtry");
    public static final Property CONTACT_INFO_EMAIL = Property.internalTextBag("Iptc4xmpCore:CiEmailWork");
    public static final Property CONTACT_INFO_PHONE = Property.internalTextBag("Iptc4xmpCore:CiTelWork");
    public static final Property CONTACT_INFO_POSTAL_CODE = Property.internalText("Iptc4xmpCore:CiAdrPcode");
    public static final Property CONTACT_INFO_STATE_PROVINCE = Property.internalText("Iptc4xmpCore:CiAdrRegion");
    public static final Property CONTACT_INFO_WEB_URL = Property.internalTextBag("Iptc4xmpCore:CiUrlWork");
    @Deprecated
    public static final Property URGENCY = Photoshop.URGENCY;
    @Deprecated
    public static final Property CATEGORY = Photoshop.CATEGORY;
    @Deprecated
    public static final Property SUPPLEMENTAL_CATEGORIES = Photoshop.SUPPLEMENTAL_CATEGORIES;
    public static final Property ADDITIONAL_MODEL_INFO = Property.internalText("Iptc4xmpExt:AddlModelInfo");
    public static final Property ARTWORK_OR_OBJECT = Property.internalTextBag("Iptc4xmpExt:ArtworkOrObject");
    public static final Property ORGANISATION_CODE = Property.internalTextBag("Iptc4xmpExt:OrganisationInImageCode");
    public static final Property CONTROLLED_VOCABULARY_TERM = Property.internalTextBag("Iptc4xmpExt:CVterm");
    public static final Property LOCATION_SHOWN = Property.internalTextBag("Iptc4xmpExt:LocationShown");
    public static final Property MODEL_AGE = Property.internalTextBag("Iptc4xmpExt:ModelAge");
    public static final Property ORGANISATION_NAME = Property.internalTextBag("Iptc4xmpExt:OrganisationInImageName");
    public static final Property PERSON = Property.internalTextBag("Iptc4xmpExt:PersonInImage");
    public static final Property DIGITAL_IMAGE_GUID = Property.internalText("Iptc4xmpExt:DigImageGUID");
    @Deprecated
    public static final Property DIGITAL_SOURCE_FILE_TYPE = Property.internalText("Iptc4xmpExt:DigitalSourcefileType");
    public static final Property DIGITAL_SOURCE_TYPE = Property.internalText("Iptc4xmpExt:DigitalSourceType");
    public static final Property EVENT = Property.internalText("Iptc4xmpExt:Event");
    public static final Property IMAGE_REGISTRY_ENTRY = Property.internalTextBag("Iptc4xmpExt:RegistryId");
    public static final Property IMAGE_SUPPLIER = Property.internalText("plus:ImageSupplier");
    @Deprecated
    public static final String IMAGE_SUPPLIER_ID_WRONG_CASE = "plus:ImageSupplierId";
    public static final Property IMAGE_SUPPLIER_ID = Property.composite(Property.internalText("plus:ImageSupplierID"), new Property[] { Property.internalText("plus:ImageSupplierId") });
    public static final Property IMAGE_SUPPLIER_NAME = Property.internalText("plus:ImageSupplierName");
    public static final Property IMAGE_SUPPLIER_IMAGE_ID = Property.internalText("plus:ImageSupplierImageID");
    public static final Property IPTC_LAST_EDITED = Property.internalDate("Iptc4xmpExt:IptcLastEdited");
    public static final Property LOCATION_CREATED = Property.internalTextBag("Iptc4xmpExt:LocationCreated");
    public static final Property MAX_AVAIL_HEIGHT = Property.internalInteger("Iptc4xmpExt:MaxAvailHeight");
    public static final Property MAX_AVAIL_WIDTH = Property.internalInteger("Iptc4xmpExt:MaxAvailWidth");
    public static final Property PLUS_VERSION = Property.internalText("plus:Version");
    public static final Property COPYRIGHT_OWNER = Property.internalTextBag("plus:CopyrightOwner");
    @Deprecated
    public static final String COPYRIGHT_OWNER_ID_WRONG_CASE = "plus:CopyrightOwnerId";
    public static final Property COPYRIGHT_OWNER_ID = Property.composite(Property.internalTextBag("plus:CopyrightOwnerID"), new Property[] { Property.internalTextBag("plus:CopyrightOwnerId") });
    public static final Property COPYRIGHT_OWNER_NAME = Property.internalTextBag("plus:CopyrightOwnerName");
    public static final Property IMAGE_CREATOR = Property.internalTextBag("plus:ImageCreator");
    @Deprecated
    public static final String IMAGE_CREATOR_ID_WRONG_CASE = "plus:ImageCreatorId";
    public static final Property IMAGE_CREATOR_ID = Property.composite(Property.internalTextBag("plus:ImageCreatorID"), new Property[] { Property.internalTextBag("plus:ImageCreatorId") });
    public static final Property IMAGE_CREATOR_NAME = Property.internalTextBag("plus:ImageCreatorName");
    public static final Property LICENSOR = Property.internalTextBag("plus:Licensor");
    @Deprecated
    public static final String LICENSOR_ID_WRONG_CASE = "plus:LicensorId";
    public static final Property LICENSOR_ID = Property.composite(Property.internalTextBag("plus:LicensorID"), new Property[] { Property.internalTextBag("plus:LicensorId") });
    public static final Property LICENSOR_NAME = Property.internalTextBag("plus:LicensorName");
    public static final Property LICENSOR_CITY = Property.internalTextBag("plus:LicensorCity");
    public static final Property LICENSOR_COUNTRY = Property.internalTextBag("plus:LicensorCountry");
    public static final Property LICENSOR_EMAIL = Property.internalTextBag("plus:LicensorEmail");
    public static final Property LICENSOR_EXTENDED_ADDRESS = Property.internalTextBag("plus:LicensorExtendedAddress");
    public static final Property LICENSOR_POSTAL_CODE = Property.internalTextBag("plus:LicensorPostalCode");
    public static final Property LICENSOR_REGION = Property.internalTextBag("plus:LicensorRegion");
    public static final Property LICENSOR_STREET_ADDRESS = Property.internalTextBag("plus:LicensorStreetAddress");
    public static final Property LICENSOR_TELEPHONE_1 = Property.internalTextBag("plus:LicensorTelephone1");
    public static final Property LICENSOR_TELEPHONE_2 = Property.internalTextBag("plus:LicensorTelephone2");
    public static final Property LICENSOR_URL = Property.internalTextBag("plus:LicensorURL");
    public static final Property MINOR_MODEL_AGE_DISCLOSURE = Property.internalText("plus:MinorModelAgeDisclosure");
    public static final Property MODEL_RELEASE_ID = Property.internalTextBag("plus:ModelReleaseID");
    public static final Property MODEL_RELEASE_STATUS = Property.internalText("plus:ModelReleaseStatus");
    public static final Property PROPERTY_RELEASE_ID = Property.internalTextBag("plus:PropertyReleaseID");
    public static final Property PROPERTY_RELEASE_STATUS = Property.internalText("plus:PropertyReleaseStatus");
    public static final Property ARTWORK_OR_OBJECT_DETAIL_COPYRIGHT_NOTICE = Property.internalTextBag("Iptc4xmpExt:AOCopyrightNotice");
    public static final Property ARTWORK_OR_OBJECT_DETAIL_CREATOR = Property.internalTextBag("Iptc4xmpExt:AOCreator");
    public static final Property ARTWORK_OR_OBJECT_DETAIL_DATE_CREATED = Property.internalTextBag("Iptc4xmpExt:AODateCreated");
    public static final Property ARTWORK_OR_OBJECT_DETAIL_SOURCE = Property.internalTextBag("Iptc4xmpExt:AOSource");
    public static final Property ARTWORK_OR_OBJECT_DETAIL_SOURCE_INVENTORY_NUMBER = Property.internalTextBag("Iptc4xmpExt:AOSourceInvNo");
    public static final Property ARTWORK_OR_OBJECT_DETAIL_TITLE = Property.internalTextBag("Iptc4xmpExt:AOTitle");
    public static final Property LOCATION_SHOWN_CITY = Property.internalTextBag("Iptc4xmpExt:LocationShownCity");
    public static final Property LOCATION_SHOWN_COUNTRY_CODE = Property.internalTextBag("Iptc4xmpExt:LocationShownCountryCode");
    public static final Property LOCATION_SHOWN_COUNTRY_NAME = Property.internalTextBag("Iptc4xmpExt:LocationShownCountryName");
    public static final Property LOCATION_SHOWN_PROVINCE_OR_STATE = Property.internalTextBag("Iptc4xmpExt:LocationShownProvinceState");
    public static final Property LOCATION_SHOWN_SUBLOCATION = Property.internalTextBag("Iptc4xmpExt:LocationShownSublocation");
    public static final Property LOCATION_SHOWN_WORLD_REGION = Property.internalTextBag("Iptc4xmpExt:LocationShownWorldRegion");
    public static final Property LOCATION_CREATED_CITY = Property.internalText("Iptc4xmpExt:LocationCreatedCity");
    public static final Property LOCATION_CREATED_COUNTRY_CODE = Property.internalText("Iptc4xmpExt:LocationCreatedCountryCode");
    public static final Property LOCATION_CREATED_COUNTRY_NAME = Property.internalText("Iptc4xmpExt:LocationCreatedCountryName");
    public static final Property LOCATION_CREATED_PROVINCE_OR_STATE = Property.internalText("Iptc4xmpExt:LocationCreatedProvinceState");
    public static final Property LOCATION_CREATED_SUBLOCATION = Property.internalText("Iptc4xmpExt:LocationCreatedSublocation");
    public static final Property LOCATION_CREATED_WORLD_REGION = Property.internalText("Iptc4xmpExt:LocationCreatedWorldRegion");
    public static final Property REGISTRY_ENTRY_CREATED_ITEM_ID = Property.internalTextBag("Iptc4xmpExt:RegItemId");
    public static final Property REGISTRY_ENTRY_CREATED_ORGANISATION_ID = Property.internalTextBag("Iptc4xmpExt:RegOrgId");
    public static final Property[] PROPERTY_GROUP_IPTC_CORE = { IPTC.CITY, IPTC.COUNTRY, IPTC.COUNTRY_CODE, IPTC.DESCRIPTION, IPTC.HEADLINE, IPTC.INTELLECTUAL_GENRE, IPTC.KEYWORDS, IPTC.PROVINCE_OR_STATE, IPTC.SCENE_CODE, IPTC.SUBJECT_CODE, IPTC.SUBLOCATION, IPTC.DATE_CREATED, IPTC.DESCRIPTION_WRITER, IPTC.INSTRUCTIONS, IPTC.JOB_ID, IPTC.TITLE, IPTC.COPYRIGHT_NOTICE, IPTC.CREATOR, IPTC.CREATORS_JOB_TITLE, IPTC.CREDIT_LINE, IPTC.RIGHTS_USAGE_TERMS, IPTC.SOURCE, IPTC.CONTACT_INFO_ADDRESS, IPTC.CONTACT_INFO_CITY, IPTC.CONTACT_INFO_COUNTRY, IPTC.CONTACT_INFO_EMAIL, IPTC.CONTACT_INFO_PHONE, IPTC.CONTACT_INFO_POSTAL_CODE, IPTC.CONTACT_INFO_STATE_PROVINCE, IPTC.CONTACT_INFO_WEB_URL };
    public static final Property[] PROPERTY_GROUP_IPTC_EXT = { IPTC.ADDITIONAL_MODEL_INFO, IPTC.ORGANISATION_CODE, IPTC.CONTROLLED_VOCABULARY_TERM, IPTC.MODEL_AGE, IPTC.ORGANISATION_NAME, IPTC.PERSON, IPTC.DIGITAL_IMAGE_GUID, IPTC.DIGITAL_SOURCE_TYPE, IPTC.EVENT, IPTC.IMAGE_SUPPLIER_ID, IPTC.IMAGE_SUPPLIER_NAME, IPTC.IMAGE_SUPPLIER_IMAGE_ID, IPTC.IPTC_LAST_EDITED, IPTC.MAX_AVAIL_HEIGHT, IPTC.MAX_AVAIL_WIDTH, IPTC.PLUS_VERSION, IPTC.COPYRIGHT_OWNER_ID, IPTC.COPYRIGHT_OWNER_NAME, IPTC.IMAGE_CREATOR_ID, IPTC.IMAGE_CREATOR_NAME, IPTC.LICENSOR_ID, IPTC.LICENSOR_NAME, IPTC.LICENSOR_CITY, IPTC.LICENSOR_COUNTRY, IPTC.LICENSOR_EMAIL, IPTC.LICENSOR_EXTENDED_ADDRESS, IPTC.LICENSOR_POSTAL_CODE, IPTC.LICENSOR_REGION, IPTC.LICENSOR_STREET_ADDRESS, IPTC.LICENSOR_TELEPHONE_1, IPTC.LICENSOR_TELEPHONE_2, IPTC.LICENSOR_URL, IPTC.MINOR_MODEL_AGE_DISCLOSURE, IPTC.MODEL_RELEASE_ID, IPTC.MODEL_RELEASE_STATUS, IPTC.PROPERTY_RELEASE_ID, IPTC.PROPERTY_RELEASE_STATUS, IPTC.ARTWORK_OR_OBJECT_DETAIL_COPYRIGHT_NOTICE, IPTC.ARTWORK_OR_OBJECT_DETAIL_CREATOR, IPTC.ARTWORK_OR_OBJECT_DETAIL_DATE_CREATED, IPTC.ARTWORK_OR_OBJECT_DETAIL_SOURCE, IPTC.ARTWORK_OR_OBJECT_DETAIL_SOURCE_INVENTORY_NUMBER, IPTC.ARTWORK_OR_OBJECT_DETAIL_TITLE, IPTC.LOCATION_SHOWN_CITY, IPTC.LOCATION_SHOWN_COUNTRY_CODE, IPTC.LOCATION_SHOWN_COUNTRY_NAME, IPTC.LOCATION_SHOWN_PROVINCE_OR_STATE, IPTC.LOCATION_SHOWN_SUBLOCATION, IPTC.LOCATION_SHOWN_WORLD_REGION, IPTC.LOCATION_CREATED_CITY, IPTC.LOCATION_CREATED_COUNTRY_CODE, IPTC.LOCATION_CREATED_COUNTRY_NAME, IPTC.LOCATION_CREATED_PROVINCE_OR_STATE, IPTC.LOCATION_CREATED_SUBLOCATION, IPTC.LOCATION_CREATED_WORLD_REGION, IPTC.REGISTRY_ENTRY_CREATED_ITEM_ID, IPTC.REGISTRY_ENTRY_CREATED_ORGANISATION_ID };
}
