package com.me.devicemanagement.framework.server.search;

public class AdvSearchConstants
{
    public static final int BASE_HIERARCHY = 0;
    public static final int INNER_HIERARCHY = 1;
    public static final int ALL = 0;
    public static final int ENTERPRISE_ONLY = 1;
    public static final int MSP_ONLY = 2;
    public static final String COMPUTERS = "dc.js.common.COMPUTERS";
    public static final String DEVICES = "dc.js.common.DEVICES";
    public static final String CONFIG = "dc.js.common.CONFIG";
    public static final String PATCH = "dc.js.common.PATCH";
    public static final String SD = "dc.js.common.SD";
    public static final String INVENTORY = "dc.js.common.INV";
    public static final String TOOLS = "dc.js.common.TOOLS";
    public static final String REPORTS = "dc.js.common.REP";
    public static final String SOM = "dc.js.common.SOM";
    public static final String RDS = "dc.js.common.RDS";
    public static final String GENERAL = "dc.js.common.GENERAL";
    public static final String CUSTOM_FIELDS = "dc.js.common.CUSTOM_FIELDS";
    public static final String ACTIVE_DIRECTORY = "dc.js.common.AD";
    public static final String COMPUTER_NAME = "dc.js.common.COMPUTER_NAME";
    public static final String DEVICE_NAME = "dc.js.common.DEVICE_NAME";
    public static final String GROUP_SEARCH_VIEW = "AllGroupsViewSearch";
    public static final String OU_SEARCH_VIEW = "AllContainerViewSearch";
    public static final String USER_SEARCH_VIEW = "AllUsersViewSearch";
    public static final String PATCH_SEARCH_VIEW = "ApplicablePatchViewSearch";
    public static final String RDS_SEARCH_VIEW = "RDSConnectionsViewSearch";
    public static final String INV_COMP_SEARCH_VIEW = "InvComputersSummarySearch";
    public static final String SOM_COMP_SEARCH_VIEW = "SoMManagedComputersSearch";
    public static final String PATCH_COMP_SEARCH_VIEW = "systemPMHealthViewTableSearch";
    public static final String ENROLL_DEVICE_SEARCH_VIEW = "EnrollmentRequestSearch";
    public static final String INV_DEVICE_SEARCH_VIEW = "DeviceListSearch";
    public static final int MAX_SEARCH_COUNT = 1000;
    public static final int MAX_FACET_COUNT = 1000;
    public static final int MAX_SUGGESTIONS = 20;
    public static final String SEARCHFILES_INDEXED = "SearchFilesIndexed";
    public static final String CONTENT = "content";
    public static final String TITLE = "title";
    public static final String NORMAL_TITLE = "normaltitle";
    public static final String DC_TITLE = "dc:title";
    public static final String OG_TITLE = "og:title";
    public static final String TWITTER_TITLE = "twitter:title";
    public static final String DESCRIPTION = "description";
    public static final String OG_DESCRIPTION = "og:description";
    public static final String TWITTER_DESCRIPTION = "twitter:description";
    public static final String KEYWORD = "keyword";
    public static final String KEYWORDS = "keywords";
    public static final String URL_LINK = "urllink";
    public static final String SCORE = "score";
    public static final String SCORE_EXPLANATION = "scoreexplanation";
    public static final String SELECTED_TAB = "selectedTab";
    public static final String SELECTED_TAB_FILTER = "selectedTabFilter";
    public static final String TAB_FILTER = "TabFilter";
    public static final String SELECTED_TREE_ELEM = "selectedTreeElem";
    public static final String ROLES = "Roles";
    public static final String ADDITIONAL_KEYWORDS = "additionalkeywords";
    public static final String LICENSE_TYPE = "licenseType";
    public static final String LICENSE_VALUE = "licenseValue";
    public static final String HTML_SUFFIX = "html";
    public static final String HTM_SUFFIX = "htm";
    public static final String LOGIN_ID = "LOGIN_ID";
    public static final String ROLE = "role";
    public static final String ROLES_LIST_FOR_AUTHKEY = "ROLES_LIST_FOR_AUTHKEY";
    public static final String ALLOWED = "allowed";
    public static final String NOT_ALLOWED = "notallowed";
    public static final String DB_NAME = "DBName";
    public static final String DB_CHECK_TYPE = "dbCheckType";
    public static final String DB_CHECK_VALUE = "dbCheckValue";
    public static final String DUMMPY_FACET_VALUE = "123456789";
    public static final String SEARCH_INDEX = "searchindex";
    public static final String DOC_INDEX = "docindex";
    public static final String SPELL_INDEX = "spellindex";
    public static final String STATIC_ACTION_INDEX = "staticactionindex";
    public static final String INDEX_PROPERTIES = "indexProperties";
    public static final String STATIC_ACTION_FILES = "staticActionFiles";
    public static final String SEARCH_DICTIONARY = "searchDictionary";
    public static final String ADV_SEARCH_PROPERTIES_FILE = "adv-search.properties";
    public static final String SEARCH_SETTINGS_BOOST_FILE = "search-settings-boost.properties";
    public static final String SEARCH_DOCUMENTS_BOOST_FILE = "search-documents-boost.properties";
    public static final String SEARCH_SCORE_ENABLED = "search.score.enabled";
    public static final String HELP_DIR = "help.dir";
    public static final String HELP_URL = "help.url";
    public static final String HELP_PREFIX = "help.prefix";
    public static final String HELP_INDEX_HELPERFILE = "help.index.helperfile";
    public static final String SITE_DIR = "site.dir";
    public static final String SITE_URL = "site.url";
    public static final String SITE_INDEX_HELPERFILE = "site.index.helperfile";
    public static final String TRACKING_CODE = "tracking.code";
    public static final String TYPE = "type";
    public static final String HTML_DATA_VALUE = "html_data_value";
    public static final String WHITELIST = "whitelist";
    public static final String BLACKLIST = "blacklist";
    public static final String STATIC_ACTION = "staticaction";
    public static final String SPELL_CHECKER = "spellchecker";
    public static final String DOCS = "docs";
    public static final String SEARCH_ENABLED = "search.enabled";
    public static final String SEARCH_SETTINGS_ENABLED = "search.settings.enabled";
    public static final String SEARCH_DOCUMENTS_ENABLED = "search.documents.enabled";
    public static final String SEARCH_SPELLCHECKER_ENABLED = "search.spellchecker.enabled";
    public static final String SEARCH_SETTINGS_FACETING_ENABLED = "search.settings.faceting.enabled";
    public static final String SEARCH_DOCUMENTS_FACETING_ENABLED = "search.documents.faceting.enabled";
    public static final String SEARCH_SETTINGS_DEFAULT_CATEGORY = "search.settings.default.category";
    public static final String SEARCH_SETTINGS_DEFAULT_OPERATOR = "search.settings.default.operator";
    public static final String SEARCH_INDEX_DIR = "search.index.dir";
    public static final String Q = "q";
    public static final String SETT = "sett";
    public static final String SRC = "src";
    public static final String SALL = "sall";
    public static final String STAB = "stab";
    public static final String STREE_ELEM = "streeelem";
    public static final String CATEGORY = "category";
    public static final String PAGE = "page";
    public static final String PAGE_LIMIT = "pagelimit";
    public static final String SEARCH_DATA = "searchdata";
    public static final String FACET_DATA = "facetdata";
    public static final String SUGGEST_DATA = "suggestdata";
    public static final String TOTAL = "total";
    public static final String DOCUMENTS = "documents";
    public static final String SETTINGS = "settings";
    public static final String SCORE_DATA_AVAILABLE = "scoredataavailable";
    public static final String LABEL = "label";
    public static final String VALUE = "value";
    public static final String SEARCH_FACETS = "searchFacets";
    public static final String ARRAY_INDEX = "arrayIndex";
    public static final String URL_LINK_AVAILABLE = "urlLinkAvailable";
    public static final String REMOVE_DATA = "removeData";
    public static final String BRAZILIAN_PORTUGUESE = "pt_BR";
    public static final String CHINESE = "zh_CN";
    public static final String DANISH = "da_DK";
    public static final String DUTCH = "nl_NL";
    public static final String ENGLISH = "en_US";
    public static final String FINNISH = "fi_FI";
    public static final String FRENCH = "fr_FR";
    public static final String GERMAN = "de_DE";
    public static final String ITALIAN = "it_IT";
    public static final String JAPANESE = "ja_JP";
    public static final String NORWEGIAN = "nb_NO";
    public static final String POLISH = "pl_PL";
    public static final String PORTUGUESE = "pt_PT";
    public static final String RUSSIAN = "ru_RU";
    public static final String SPANISH = "es_ES";
    public static final String SWEDISH = "sv_SE";
    public static final String TURKISH = "tr_TR";
    public static final Integer FEATURES_ARTICLES_PARAM_ID;
    public static final String FEATURES_ARTICLES = "dm.advsearch.features.articles";
    public static final String SELECTED_SEARCH_PARAM = "SelectedSearchParam";
    public static final String AUTH_PARAM_KEY = "authParamKey";
    public static final String SEARCH_PARAM_LIST = "searchParamList";
    public static final String SEARCH_PARAM_ID = "searchParamId";
    public static final String SEARCH_PARAM_NAME = "searchParamName";
    public static final String SEARCH_CHILD_NODE = "searchChildNode";
    public static final String SEARCH_TEXT_DECODE = "searchTextDecode";
    public static final String SEARCH_HISTORY_DATA = "searchHistoryData";
    public static final String ADV_SEARCH_CACHE = "advSearchCache";
    public static final String NON_INDEXED_PREFIX = "nonindexed";
    public static final String HELP_FILES = "helpFiles";
    public static final String SITE_FILES = "siteFiles";
    public static final String FILE_NAME_SEPARATOR = "_";
    public static final String INDEX_FOLDER_NAME = "indexFolderName";
    public static final String INDEX_PROP_NAME = "indexPropName";
    public static final String DOCS_INDEX_FOLDER_PROP_NAME = "docIndexInfo";
    public static final String STATIC_INDEX_FOLDER_PROP_NAME = "staticActionIndexInfo";
    public static final String INDEX_FOLDER_TO_REFER = "indexFolderToRefer";
    public static final String FILE_NAME_CHECKSUM = "fileNameChecksum";
    public static final String DOWNLOAD_CHECKSUM_JSON_FILE = "downloadChecksum.json";
    public static final String INDEX_CHECKSUM_JSON_FILE = "indexChecksum.json";
    public static final String JSON_FILETYPE_SUFFIX = ".json";
    public static final String DOC_MAIN_INDEX = "docMainIndex";
    public static final String STATIC_MAIN_INDEX = "staticMainIndex";
    public static final String SEARCH_MAIN_INDEX = "searchMainIndex";
    public static final String SEARCH_PROMOTION = "SEARCH_PROMOTION";
    public static final String STATIC_INDEX_VERSION = "staticIndexVersion";
    public static final String DOCUMENT_INDEX_VERSION = "documentIndexVersion";
    public static final String MSP_DOCUMENT_INDEX_VERSION = "mspDocumentIndexVersion";
    public static final String LAST_MODIFIED_TIME = "ADVSEARCH_LAST_MODIFIED_TIME";
    public static final String INDEX_CHECK_URL = "advsearch_index_check_url";
    public static final String ADVSEARCH_DOC_INDEX_CSR_URL = "advsearch_doc_index_csr_url";
    public static final String INDEX_FILE_NAME = "searchIndexUpdate";
    public static final String DOC_VERSION_AVAILABLE_LIST = "docVersionAvailableList";
    public static final String CRS_STATUS = "CRS Status";
    public static final String ADV_SEARCH_LOGGER = "AdvSearchLogger";
    public static final String ADV_SEARCH_ERROR_LOGGER = "AdvSearchError";
    public static final String SEARCH_DETAILS = "AdvSearchDetails";
    public static final String SEARCH_COUNT = "searchCount";
    public static final String ID = "id";
    public static final String SELECTED_ARTICLES_TAB_COUNT = "selectedArticlesTabCount";
    public static final String SELECTED_FEATURES_TAB_COUNT = "selectedFeaturesTabCount";
    public static final String LOAD_MORE_COUNT = "loadMoreCount";
    public static final String CHAT_COUNT = "chatCount";
    public static final String OVERALL_FILTERS_COUNT = "overallFiltersCount";
    public static final String OVERALL_SELECTED_FEATURES_RESULTS_COUNT = "overallSelectedFeaturesResultsCount";
    public static final String OVERALL_SELECTED_ARTICLES_RESULTS_COUNT = "overallSelectedArticlesResultsCount";
    public static final String LAST_DATE_USED = "lastDateUsed";
    public static final String SEL_ARTICLES_TAB_COUNT = "selDocTabCount";
    public static final String SEL_FEATURES_TAB_COUNT = "selSettTabCount";
    public static final String FILTERS_COUNT = "filtersCount";
    public static final String SEL_FEATURES_RESULTS_COUNT = "selSettResCount";
    public static final String SEL_ARTICLES_RESULTS_COUNT = "selDocResCount";
    public static final String TOTAL_DAYS_USED = "totalDaysUsed";
    public static final String SUGGESTION_CATEGORY = "suggestionCategory";
    public static final String SELECTED_SUGGEST_CATEGORY_COUNT = "selSuggest";
    public static final String NO_RESULT_FOUND_SETTING = "noSett";
    public static final String NO_RESULT_FOUND_DOCUMENT = "noDoc";
    public static final String IS_TRIGGER_FROM_MENU = "isTriggerFromMenu";
    public static final String TRIGGER = "trigger";
    public static final String IS_SUGGESTION_CATEGORY_REQUIRED = "isCategorySuggestionRequired";
    public static final String ADV_SEARCH_INDEX_UPDATE_FEATURE = "advSearchIndexUpdate";
    
    static {
        FEATURES_ARTICLES_PARAM_ID = Integer.MAX_VALUE;
    }
}