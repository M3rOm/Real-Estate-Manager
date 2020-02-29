package com.example.realestatemanager.utils

class Constants {
    interface InternetType {
        companion object {
            const val INTERNET_NONE = -1
            const val INTERNET_WIFI = 0
            const val INTERNET_3G = 1
        }
    }

    interface NotificationsChannels {
        companion object {
            const val DEFAULT_CHANNEL_ID = "REAL_ESTATE_MANAGER"
        }
    }

    interface TypesList {
        companion object {
            const val TYPE_LIST_KEY = "TYPE_LIST_KEY"
            const val ALL = 0
            const val FILTERED = 1
            const val SEARCH = 2
        }
    }

    interface BundleKeys {
        companion object {
            const val BUNDLE_EXTRA = "BUNDLE_EXTRA"
            const val REAL_ESTATE_OBJECT_KEY = "REAL_ESTATE_OBJECT_KEY"
            const val FILTERED_PARAMS_KEY = "FILTERED_PARAMS_KEY"
            const val BUNDLE_CURRENCY_KEY = "BUNDLE_CURRENCY_KEY"
            const val SEARCH_PARAM_KEY = "SEARCH_PARAM_KEY"
        }
    }

    interface Status {
        companion object {
            const val SOLD = "SOLD"
            const val AVAILABLE = "AVAILABLE"
        }
    }

    interface Currencies {
        companion object {
            const val EURO = "EURO"
            const val DOLLAR = "DOLLAR"
        }
    }

    interface MapsCodes {
        companion object {
            const val ERROR_DIALOG_REQUEST = 9001
            const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9002
            const val PERMISSIONS_REQUEST_ENABLE_GPS = 9003
            const val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
        }
    }

    interface PrefesKeys {
        companion object {
            const val PREFS_KEY = "SHARED_PREFERENCES_KEY"
            const val CURRENCY_KEY = "CURRENCY_KEY"
        }
    }
}