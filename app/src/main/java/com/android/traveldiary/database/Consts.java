package com.android.traveldiary.database;

public final class Consts {

    private static final String URL = "https://api.travellist.tk/api/";

    public static final String STRING_DATE_PATTERN_V2 = "dd.MM.yyyy";
    public static final String STRING_DATE_PATTERN = "yyyy-MM-dd";
    public static final String STRING_TRAVEL_ID = "travelID";
    public static final String STRING_CURRENT_DATE = "travel_date";
    public static final String STRING_START_DATE = "travel_start_date";

    public static final String LONG_START_DATE = "long_travel_start_date";
    public static final String LONG_END_DATE = "long_travel_end_date";

    public static final String STRING_END_DATE = "travel_end_date";
    public static final String STRING_ENTRY_POSITION = "post_position";
    public static final String LOG_TAG = "AudioRecordTest";
    public static final String IMAGE_DIRECTORY = "/TravelDiary";
    public static final int DELAY = 100;
    public static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    public static final int REQUEST_GALLERY = 100, REQUEST_CAMERA = 200;


    private Consts() {
        throw new AssertionError();
    }


    public static final String ENTRY_TYPE_TRANSPORT = "transport";
    public static final String ENTRY_TYPE_NOTE = "note";
    public static final String ENTRY_TYPE_VOICE_NOTE = "voice note";
    public static final String ENTRY_TYPE_PHOTO = "photo";
    public static final String ENTRY_TYPE_MAP_MARKER = "map marker";

    public static final int ENTRY_TYPE_TRANSPORT_INT = 1;
    public static final int ENTRY_TYPE_NOTE_INT = 2;
    public static final int ENTRY_TYPE_VOICE_NOTE_INT = 3;
    public static final int ENTRY_TYPE_PHOTO_INT = 4;
    public static final int ENTRY_TYPE_MAP_COORDS_INT = 5;


    /**
     * TRANSPORT TYPE CONSTS
     */

    public static final String TRANSPORT_TYPE_BIKE = "bike";
    public static final String TRANSPORT_TYPE_BOAT = "boat";
    public static final String TRANSPORT_TYPE_CAR = "car";
    public static final String TRANSPORT_TYPE_TRAIN = "train";
    public static final String TRANSPORT_TYPE_PLANE = "plane";
    public static final String TRANSPORT_TYPE_MOTORCYCLE = "motorcycle";


    /**
     * DATABASE CONSTS
     */
    public static final String CREATE_TABLE = "CREATE";

    public static final String TABLE_NAME_TRAVEL = "Travel";
    public static final String TRAVEL_COLUMN_TRAVEL_ID = "TravelID";
    public static final String TRAVEL_COLUMN_NAME = "TravelName";
    public static final String TRAVEL_COLUMN_DATE_FROM = "DateFrom";
    public static final String TRAVEL_COLUMN_DATE_TO = "DateTo";
    public static final String TRAVEL_COLUMN_COVER_PHOTO_URI = "PhotoURI";

    public static final String TABLE_NAME_PHOTO = "Photo";
    public static final String PHOTO_COLUMN_ID = "PhotoID";
    public static final String PHOTO_COLUMN_TITLE = "Title";
    public static final String PHOTO_COLUMN_IMAGE_URI = "ImageURI";
    public static final String PHOTO_COLUMN_DATE = "Date";
    public static final String PHOTO_COLUMN_POSITION = "Position";
    public static final String PHOTO_COLUMN_TRAVEL_ID = "TravelID";

    public static final String TABLE_NAME_NOTE = "Note";
    public static final String NOTE_COLUMN_ID = "NoteID";
    public static final String NOTE_COLUMN_TITLE = "Title";
    public static final String NOTE_COLUMN_NOTE = "Note";
    public static final String NOTE_COLUMN_DATE = "Date";
    public static final String NOTE_COLUMN_POSITION = "Position";
    public static final String NOTE_COLUMN_TRAVEL_ID = "TravelID";


    public static final String TABLE_NAME_VOICE_NOTE = "VoiceNote";
    public static final String VOICE_NOTE_COLUMN_ID = "VoiceNoteID";
    public static final String VOICE_NOTE_COLUMN_TITLE = "Title";
    public static final String VOICE_NOTE_COLUMN_URI = "RecordURI";
    public static final String VOICE_NOTE_COLUMN_DATE = "Date";
    public static final String VOICE_NOTE_COLUMN_POSITION = "Position";
    public static final String VOICE_NOTE_COLUMN_TRAVEL_ID = "TravelID";


    public static final String TABLE_NAME_TRANSPORT = "Transport";
    public static final String TRANSPORT_COLUMN_TRAVEL_ID = "TravelID";
    public static final String TRANSPORT_COLUMN_ID = "TransportID";
    public static final String TRANSPORT_COLUMN_TRANSPORT_TYPE = "TransportType";
    public static final String TRANSPORT_COLUMN_DEPARTURE_PLACE = "DeparturePlace";
    public static final String TRANSPORT_COLUMN_DEPARTURE_DATE = "DepartureDate";
    public static final String TRANSPORT_COLUMN_DEPARTURE_TIME = "DepartureTime";
    public static final String TRANSPORT_COLUMN_ARRIVAL_PLACE = "ArrivalPlace";
    public static final String TRANSPORT_COLUMN_ARRIVAL_DATE = "ArrivalDate";
    public static final String TRANSPORT_COLUMN_ARRIVAL_TIME = "ArrivalTime";
    public static final String TRANSPORT_COLUMN_POSITION = "Position";

    public static final String TABLE_NAME_MAP = "Map";
    public static final String MAP_COLUMN_ID = "MapID";
    public static final String MAP_COLUMN_TITLE = "Title";
    public static final String MAP_COLUMN_DESCRIPTION = "Description";
    public static final String MAP_COLUMN_LONGITUDE = "Longitude";
    public static final String MAP_COLUMN_LATITUDE = "Latitude";
    public static final String MAP_COLUMN_DATE = "Date";
    public static final String MAP_COLUMN_POSITION = "Position";
    public static final String MAP_COLUMN_TRAVEL_ID = "TravelID";

    public static final String TABLE_NAME_COUNTRY = "Country";
    public static final String COUNTRY_COLUMN_ID = "CountryID";
    public static final String COUNTRY_COLUMN_NAME = "Name";
    public static final String COUNTRY_COLUMN_CONTINENT = "Continent";
    public static final String COUNTRY_COLUMN_TRAVEL_ID = "TravelID";

    public static final String TABLE_NAME_COUNTRY_VISIT = "CountryVisit";
    public static final String COUNTRY_VISIT_COLUMN_ID = "VisitID";
    public static final String COUNTRY_VISIT_COLUMN_COUNTRY = "CountryName";
    public static final String COUNTRY_VISIT_COLUMN_TRAVEL_ID = "TravelID";

    public static final String SQL_CREATE_TABLE_TRAVEL =
            "CREATE TABLE " + TABLE_NAME_TRAVEL + " ( "
                    + TRAVEL_COLUMN_TRAVEL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + TRAVEL_COLUMN_NAME + " TEXT, "
                    + TRAVEL_COLUMN_DATE_FROM + " TEXT, "
                    + TRAVEL_COLUMN_DATE_TO + " TEXT, "
                    + TRAVEL_COLUMN_COVER_PHOTO_URI + " TEXT "
                    + "); ";

    public static final String SQL_CREATE_TABLE_PHOTO =
            "CREATE TABLE " + TABLE_NAME_PHOTO + " ( "
                    + PHOTO_COLUMN_ID + " INTEGER  PRIMARY KEY AUTOINCREMENT, "
                    + PHOTO_COLUMN_TITLE + " TEXT, "
                    + PHOTO_COLUMN_IMAGE_URI + " TEXT NOT NULL, "
                    + PHOTO_COLUMN_DATE + " TEXT, "
                    + PHOTO_COLUMN_POSITION + " INTEGER, "
                    + PHOTO_COLUMN_TRAVEL_ID + " INTEGER "
                    + "); ";

    public static final String SQL_CREATE_TABLE_NOTE =
            "CREATE TABLE " + TABLE_NAME_NOTE + " ( "
                    + NOTE_COLUMN_ID + " INTEGER  PRIMARY KEY AUTOINCREMENT, "
                    + NOTE_COLUMN_TITLE + " TEXT, "
                    + NOTE_COLUMN_NOTE + " TEXT NOT NULL, "
                    + NOTE_COLUMN_DATE + " TEXT, "
                    + NOTE_COLUMN_POSITION + " INTEGER, "
                    + TRAVEL_COLUMN_TRAVEL_ID + " INTEGER "
                    + "); ";

    public static final String SQL_CREATE_TABLE_VOICE_NOTE =
            "CREATE TABLE " + TABLE_NAME_VOICE_NOTE + " ( "
                    + VOICE_NOTE_COLUMN_ID + " INTEGER  PRIMARY KEY AUTOINCREMENT, "
                    + VOICE_NOTE_COLUMN_TITLE + " TEXT, "
                    + VOICE_NOTE_COLUMN_URI + " TEXT NOT NULL, "
                    + VOICE_NOTE_COLUMN_DATE + " TEXT, "
                    + VOICE_NOTE_COLUMN_POSITION + " INTEGER, "
                    + VOICE_NOTE_COLUMN_TRAVEL_ID + " INTEGER "
                    + "); ";

    public static final String SQL_CREATE_TABLE_TRANSPORT =
            "CREATE TABLE " + TABLE_NAME_TRANSPORT + " ( "
                    + TRANSPORT_COLUMN_ID + " INTEGER  PRIMARY KEY AUTOINCREMENT, "
                    + TRANSPORT_COLUMN_TRANSPORT_TYPE + " TEXT, "
                    + TRANSPORT_COLUMN_DEPARTURE_PLACE + " TEXT, "
                    + TRANSPORT_COLUMN_DEPARTURE_DATE + " DATE, "
                    + TRANSPORT_COLUMN_DEPARTURE_TIME + " TEXT, "
                    + TRANSPORT_COLUMN_ARRIVAL_PLACE + " TEXT, "
                    + TRANSPORT_COLUMN_ARRIVAL_DATE + " TEXT, "
                    + TRANSPORT_COLUMN_ARRIVAL_TIME + " TEXT, "
                    + TRANSPORT_COLUMN_POSITION + " INTEGER, "
                    + TRANSPORT_COLUMN_TRAVEL_ID + " INTEGER "
                    + "); ";

    public static final String SQL_CREATE_TABLE_MAP =
            "CREATE TABLE " + TABLE_NAME_MAP + " ( "
                    + MAP_COLUMN_ID + " INTEGER  PRIMARY KEY AUTOINCREMENT, "
                    + MAP_COLUMN_TITLE + " TEXT, "
                    + MAP_COLUMN_DESCRIPTION + " TEXT, "
                    + MAP_COLUMN_LONGITUDE + " REAL, "
                    + MAP_COLUMN_LATITUDE+ " REAL, "
                    + MAP_COLUMN_DATE + " DATE, "
                    + MAP_COLUMN_POSITION + " INTEGER, "
                    + MAP_COLUMN_TRAVEL_ID + " INTEGER "
                    + "); ";

    public static final String SQL_CREATE_TABLE_COUNTRY =
            "CREATE TABLE " + TABLE_NAME_COUNTRY + " ( "
                    + COUNTRY_COLUMN_ID + " INTEGER  PRIMARY KEY AUTOINCREMENT, "
                    + COUNTRY_COLUMN_NAME + " TEXT, "
                    + COUNTRY_COLUMN_CONTINENT + " TEXT, "
                    + COUNTRY_COLUMN_TRAVEL_ID + " INTEGER "
                    + "); ";

    public static final String SQL_CREATE_TABLE_COUNTRY_VISIT =
            "CREATE TABLE " + TABLE_NAME_COUNTRY_VISIT + " ( "
                    + COUNTRY_VISIT_COLUMN_ID + " INTEGER  PRIMARY KEY AUTOINCREMENT, "
                    + COUNTRY_VISIT_COLUMN_COUNTRY + " TEXT, "
                    + COUNTRY_VISIT_COLUMN_TRAVEL_ID + " INTEGER "
                    + "); ";



    public static String getServerUrl(){
        return URL;
    }
}
