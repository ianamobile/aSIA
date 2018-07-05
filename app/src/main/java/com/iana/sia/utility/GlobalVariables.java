package com.iana.sia.utility;

/**
 * Created by Saumil on 5/31/2018.
 */

public class GlobalVariables {

    //API RESPONSE CODE
    public static final int ERROR = 1;
    public static final int WARNING = 2;
    public static final int INFO = 3;
    public static final int OK = 4;
    public static final int TOO_BUSY = 5;

    public static final String SUCCESS = "SUCCESS";

    public static final String ROLE_MC = "MC";
    public static final String ROLE_EP = "EP";
    public static final String ROLE_IDD = "IDD";
    public static final String ROLE_SEC = "SEC";
    public static final String ROLE_TPU = "TPU";

    public static final String KEY_EP_SCAC = "epScac";
    public static final String KEY_EP_COMPANY_NAME = "epCompanyName";
    public static final String KEY_MC_SCAC = "mcScac";
    public static final String KEY_MC_COMPANY_NAME = "mcCompanyName";
    public static final String KEY_CONTAINER_NUMBER = "containerNumber";
    public static final String KEY_EXPORT_BOOKING_NUMBER = "exportBookingNumber";
    public static final String KEY_IMPORT_BL = "importBL";
    public static final String KEY_CHASSIS_ID = "chassisId";
    public static final String KEY_IEP_SCAC = "iepScac";

    public static final String KEY_SEARCH_FOR_LOCATION="searchForLocation";
    public static final String ORIGIN_FROM_EQUIPMENT = "equipment";
    public static final String ORIGIN_FROM_ORIGINAL = "original";

    public static final String ORIGIN_FROM_STREET_TURN = "StreetTurn";
    public static final String ORIGIN_FROM_STREET_INTERCHANGE = "StreetInterchange";
    public static final String ORIGIN_FROM_NOTIF_AVAIl = "NotifAvail";

    public static final String IR_REQUEST_TYPE_ST = "StreetTurn";
    public static final String IR_REQUEST_TYPE_SI = "StreetInterchange";

    public static final String KEY_SI_ST_NA = "SI_ST_NA";
    public static final String KEY_ORIGIN_FROM = "originFrom";
    public static final String KEY_ROLE = "role";
    public static final String KEY_MEM_TYPE = "memType";
    public static final String KEY_COMPANY_NAME = "companyName";
    public static final String KEY_SCAC = "scac";
    public static final String KEY_IEP_SCAC_MESSAGE = "iepScacMessage";

    public static final String KEY_INTERCHANGE_REQUESTS_SEARCH_OBJ = "interchangeRequestsSearchObj";
    public static final String KEY_INTERCHANGE_REQUESTS_OBJ = "interchangeRequestsObj";

    public static final String KEY_NOTIF_AVAIL_SEARCH_OBJ = "notifAvailSearchObj";
    public static final String KEY_NOTIF_AVAIL_OBJ = "notifAvailObj";

    public static final String KEY_SECURITY_OBJ = "securityObj";

    public static final String ORIGIN_FROM_IDDPIN_SCAC = "IDDPIN_SCAC";
    public static final String ORIGIN_FROM_DRVLIC_STATE_SCAC = "DRVLIC_STATE_SCAC";


    public static final String MENU_TITLE_NOTIF_AVAIL = "Notification of Available Equipment";
    public static final String MENU_TITLE_SEARCH_EQUIP_AVAIL = "Search Equipment Availability";
    public static final String MENU_TITLE_INITIATE_SI = "Initiate Street Interchange";
    public static final String MENU_TITLE_SEARCH_INTERCHANGE_REQUESTS = "Search Interchange Requests";
    public static final String MENU_TITLE_INITIATE_ST = "Initiate Street Turn";
    public static final String MENU_TITLE_SEARCH_INTERCHANGE_REQUESTS_BY_TPU = "Search Interchange Requests Submitted By you";
    public static final String MENU_TITLE_LIST_EP_USERS = "List EP Users";
    public static final String MENU_TITLE_PENDING_INTERCHANGE_REQUESTS = "Pending Interchange Requests";
    public static final String MENU_TITLE_LOGOUT = "Logout";

    public static final String[]  menuTitleArr = new String[]
                                                        {
                                                            "Notification of Available Equipment",
                                                            "Search Equipment Availability",
                                                            "Initiate Street Interchange",
                                                            "Search Interchange Requests",
                                                            "Initiate Street Turn",
                                                            "Search Interchange Requests Submitted By you",
                                                            "List EP Users",
                                                            "Pending Interchange Requests", "", "", "", "", "", "", "", "", "", "", "", "",
                                                            "Logout"
                                                        };

    public static final String[] menuIconArr = new String[]
                                                        {   "plus","search","plus","search","plus","search","search",
                                                            "search","","","","","","","","","","","","",
                                                            "logout"
                                                        };

    public static final String KEY_INITIATE_INTERCHANGE = "initiateInterchange";

    public static final String KEY_LOCATION_NAME = "locationCode";
    public static final String KEY_LOCATION_ADDRESS = "locationAddress";
    public static final String KEY_LOCATION_CITY = "locationCity";
    public static final String KEY_LOCATION_STATE = "locationState";
    public static final String KEY_LOCATION_ZIP = "locationZip";
    public static final String KEY_LOCATION_IANA_CODE = "locationIanaCode";
    public static final String KEY_LOCATION_SPLC_CODE = "locationSplcCode";
    public static final String KEY_RETURN_FROM = "returnFrom";

    public static final String RETURN_FROM_LOCATION_SEARCH = "locationSearch";
    public static final String RETURN_FROM_VERIFY_DETAILS = "verifyDetails";

    public static final String FIELD_INFO_BLANK = "blank";
    public static final String FIELD_INFO_EMPTY = "empty";
    public static final String FIELD_INFO_TITLE = "title";
    public static final String FIELD_INFO_VALUE = "value";

    public static final String DEFUALT_CHASSIS_NUM = "ZZZZ999999";

    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_REJECTED = "REJECTED";
    public static final String STATUS_CANCELLED = "CANCELLED";
    public static final String STATUS_ONHOLD = "ONHOLD";
    public static final String STATUS_DISABLED = "DISABLED";

    public static final String N = "N";
    public static final String Y = "Y";

    public static final String KEY_BASE_ORIGIN_FROM = "baseOriginFrom";

    public static final String INITIATOR_MCB = "MCB";
    public static final String INITIATOR_MCA = "MCA";
    public static final String INITIATOR_EP = "EP";

}
