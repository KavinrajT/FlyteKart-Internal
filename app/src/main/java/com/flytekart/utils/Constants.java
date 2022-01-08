package com.flytekart.utils;

public class Constants {

    public static final String IS_USER_LOGGED_IN = "isUserLoggedIn";
    public static final String SHARED_PREF_KEY_IS_MAIN_ACCOUNT_LOGGED_IN = "isMainAccountLoggedIn";
    public static final String SHARED_PREF_KEY_ACCESS_TOKEN = "accessToken";
    public static final String SHARED_PREF_KEY_USER_DETAILS = "userDetails";
    public static final String SHARED_PREF_KEY_CLIENT_ID = "clientId";
    public static final String SHARED_PREF_KEY_ORGANISATION = "key_organisation";
    public static final String SHARED_PREF_KEY_STORES = "key_stores";
    public static final String SHARED_PREF_KEY_PRODUCTS = "key_products";
    public static final String SHARED_PREF_KEY_CATEGORIES = "key_categories";
    public static final String SHARED_PREF_KEY_SUB_CATEGORIES = "key_sub_categories";

    public static final int TIMEOUT = 60;

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    public static final int STORAGE_PERMISSION_REQUEST_CODE = 1001;
    public static final int CREATE_ORG_ACTIVITY_REQUEST_CODE = 101;
    public static final int MAPS_ACTIVITY_REQUEST_CODE = 102;
    public static final int STORE_LIST_ACTIVITY_REQUEST_CODE = 201;
    public static final int ADD_STORE_ACTIVITY_REQUEST_CODE = 202;
    public static final int EDIT_STORE_ACTIVITY_REQUEST_CODE = 203;
    public static final int ADD_PRODUCT_ACTIVITY_REQUEST_CODE = 801;
    public static final int EDIT_PRODUCT_ACTIVITY_REQUEST_CODE = 802;
    public static final int ADD_VARIANT_ACTIVITY_REQUEST_CODE = 301;
    public static final int EDIT_VARIANT_ACTIVITY_REQUEST_CODE = 302;
    public static final int ADD_CATEGORY_ACTIVITY_REQUEST_CODE = 401;
    public static final int EDIT_CATEGORY_ACTIVITY_REQUEST_CODE = 402;
    public static final int ADD_SUB_CATEGORY_ACTIVITY_REQUEST_CODE = 501;
    public static final int CATEGORY_LIST_ACTIVITY_REQUEST_CODE = 601;
    public static final int EDIT_ORDER_ACTIVITY_REQUEST_CODE = 701;
    public static final int PRODUCT_ORDER_REPORT_FILTERS_ACTIVITY_REQUEST_CODE = 801;

    public static final String API_TOKEN_TAG = "Authorization";
    public static final String LOGIN_TYPE_MAIN_ACCOUNT = "Main Account";
    public static final String LOGIN_TYPE_CLIENT_ACCOUNT = "Client Account";


    public static final String EMPTY = "";
    public static final String SPACE = " ";
    public static final String COMMA_SPACE = ", ";
    public static final String COMMA = ",";
    public static final String STORE = "store";
    public static final String END_USER = "endUser";
    public static final String CATEGORY = "category";
    public static final String POSITION = "position";
    public static final String PRODUCT = "product";
    public static final String VARIANT = "variant";
    public static final String ORDER = "order";
    public static final String ADDRESS = "address";
    public static final String CURRENCY_RUPEE_PREFIX = "Rs";
    public static final String CLIENT_ID = "clientId";
    public static final String USERNAME = "username";
    public static final String MULTIPLE = "MULTIPLE";

    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final String SHARED_PREF_EMPLOYEE_PUSH_TOKEN_ID = "EMPLOYEE_PUSH_TOKEN_ID";

    public static final String STR_JPEG_FILE_EXTENSION = ".jpeg";
    public static final int MAX_IMAGE_SIZE = 200 ;// IN KB
    public static final int IMAGE_MAX_DIM = 500;

    public static final class OrderStatus {
        public static final String IN_PROGRESS = "IN_PROGRESS";
        public static final String PLACED = "PLACED";
        public static final String CANCELED = "CANCELED";
        public static final String ACCEPTED = "ACCEPTED";
        public static final String PROCESSING = "PROCESSING";
        public static final String PROCESSED = "PROCESSED";
        public static final String OUT_FOR_DELIVERY = "OUT_FOR_DELIVERY";
        public static final String DELIVERED = "DELIVERED";
    }

    public static final class OrderItemStatus {
        public static final String IN_PROGRESS = "IN_PROGRESS";
        public static final String DELETED = "DELETED";
        public static final String PLACED = "PLACED";
        public static final String CANCELED = "CANCELED";
        public static final String ACCEPTED = "ACCEPTED";
        public static final String PROCESSING = "PROCESSING";
        public static final String PROCESSED = "PROCESSED";
        public static final String OUT_FOR_DELIVERY = "OUT_FOR_DELIVERY";
        public static final String DELIVERED = "DELIVERED";
        public static final String REMOVED = "REMOVED";
    }

    public static final class ReturnOrderStatus {
        public static final String RETURN_IN_PROGRESS = "RETURN_IN_PROGRESS";
        public static final String RETURN_PLACED = "RETURN_PLACED";
        public static final String RETURN_CANCELED = "RETURN_CANCELED";
        public static final String RETURN_REQUEST_ACCEPTED = "RETURN_REQUEST_ACCEPTED";
        public static final String RETURN_REQUEST_REJECTED = "RETURN_REQUEST_REJECTED";
        public static final String RETURN_PICKED = "RETURN_PICKED";
        public static final String RETURN_RECEIVED = "RETURN_RECEIVED";
        public static final String RETURN_ACCEPTED = "RETURN_ACCEPTED";
        public static final String RETURN_REJECTED = "RETURN_REJECTED";
    }

    public static final class ReturnOrderItemStatus {
        public static final String RETURN_IN_PROGRESS = "RETURN_IN_PROGRESS";
        public static final String RETURN_ITEM_DELETED = "RETURN_ITEM_DELETED";
        public static final String RETURN_PLACED = "RETURN_PLACED";
        public static final String RETURN_CANCELED = "RETURN_CANCELED";
        public static final String RETURN_REQUEST_ACCEPTED = "RETURN_REQUEST_ACCEPTED";
        public static final String RETURN_REQUEST_REJECTED = "RETURN_REQUEST_REJECTED";
        public static final String RETURN_PICKED = "RETURN_PICKED";
        public static final String RETURN_RECEIVED = "RETURN_RECEIVED";
        public static final String RETURN_ACCEPTED = "RETURN_ACCEPTED";
        public static final String RETURN_REJECTED = "RETURN_REJECTED";
    }

    public static final class PaymentStatus {
        public static final String UNPAID = "UNPAID";
        public static final String PAID = "PAID";
    }

    public static final class PaymentType {
        public static final String COD = "COD"; // Covers both cash on delivery and pay on delivery
        public static final String RAZORPAY = "RAZORPAY";
    }
}
