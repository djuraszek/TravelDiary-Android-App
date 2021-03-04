package com.android.traveldiary.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import android.util.Log;

import com.android.traveldiary.diaryentries.DiaryEntry;
import com.android.traveldiary.diaryentries.Note;
import com.android.traveldiary.diaryentries.Photo;
import com.android.traveldiary.classes.Travel;


import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.android.traveldiary.database.Consts.*;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "traveldiary.db";
    private static final int DATABASE_VERSION = 1;


    public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME_COUNTRY);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME_COUNTRY_VISIT);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME_TRANSPORT);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME_MAP);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME_NOTE);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME_VOICE_NOTE);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME_TRAVEL);

        db.execSQL(SQL_CREATE_TABLE_TRAVEL);
        db.execSQL(SQL_CREATE_TABLE_TRANSPORT);
        db.execSQL(SQL_CREATE_TABLE_NOTE);
        db.execSQL(SQL_CREATE_TABLE_VOICE_NOTE);
        db.execSQL(SQL_CREATE_TABLE_PHOTO);
        db.execSQL(SQL_CREATE_TABLE_MAP);
//        db.execSQL(SQL_CREATE_TABLE_COUNTRY);
        db.execSQL(SQL_CREATE_TABLE_COUNTRY_VISIT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
//        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME_COUNTRY);
//        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME_COUNTRY_VISIT);
//        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME_TRANSPORT);
//        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME_MAP);
//        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME_NOTE);
//        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME_VOICE_NOTE);
//        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME_TRAVEL);
        onCreate(db);
    }

    public void addTravel(Travel obj) {
        Log.e("MyDBHandler", "addTravel");
        ContentValues values = new ContentValues();
        System.out.println("DB Travel: " + obj.toString());


//        values.put(TRAVEL_COLUMN_TRAVEL_ID, obj.getTravelID());
        values.put(TRAVEL_COLUMN_NAME, obj.getTitle());
        values.put(TRAVEL_COLUMN_DATE_FROM, obj.getStartDate());
        values.put(TRAVEL_COLUMN_DATE_TO, obj.getEndDate());
        values.put(TRAVEL_COLUMN_COVER_PHOTO_URI, obj.getImagePath());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAME_TRAVEL, null, values);
        db.close();
    }

    /**
     * ADD TO DATABASE
     *
     * @param obj
     */

    public void addEntry(Photo obj) {
        Log.e("MyDBHandler", "addEntry: Photo");
        ContentValues values = new ContentValues();
        System.out.println("DB Photo: " + obj.toString());

//        values.put(PHOTO_COLUMN_ID, obj.getTravelID());
        values.put(PHOTO_COLUMN_TITLE, obj.getTitle());
        values.put(PHOTO_COLUMN_IMAGE_URI, obj.getPhotoPath());
        values.put(PHOTO_COLUMN_DATE, obj.getDate());
        values.put(PHOTO_COLUMN_POSITION, obj.getPosition());
        values.put(PHOTO_COLUMN_TRAVEL_ID, obj.getTravelID());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAME_PHOTO, null, values);
        db.close();
    }

    public void addEntry(Note obj) {
        Log.e("MyDBHandler", "addEntry: Note");
        ContentValues values = new ContentValues();
        System.out.println("DB Note: " + obj.toString());


//        values.put(NOTE_COLUMN_ID, obj.getTravelID());
        values.put(NOTE_COLUMN_TITLE, obj.getTitle());
        values.put(NOTE_COLUMN_NOTE, obj.getNote());
        values.put(NOTE_COLUMN_POSITION, obj.getPosition());
        values.put(NOTE_COLUMN_DATE, obj.getDate());
        values.put(TRAVEL_COLUMN_TRAVEL_ID, obj.getTravelID());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAME_NOTE, null, values);
        db.close();
    }
//
//    public void addEntry(VoiceNote obj) {
//        Log.e("MyDBHandler", "addEntry: VoiceNote");
//        ContentValues values = new ContentValues();
//        System.out.println("DB VoiceNote: " + obj.toString());
//
//
////        values.put(NOTE_COLUMN_ID, obj.getTravelID());
//        values.put(VOICE_NOTE_COLUMN_TITLE, obj.getTitle());
//        values.put(VOICE_NOTE_COLUMN_URI, obj.getRecordURI());
//        values.put(VOICE_NOTE_COLUMN_POSITION, obj.getPosition());
//        values.put(VOICE_NOTE_COLUMN_DATE, obj.getDate());
//        values.put(VOICE_NOTE_COLUMN_TRAVEL_ID, obj.getTravelID());
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.insert(TABLE_NAME_VOICE_NOTE, null, values);
//        db.close();
//    }
//
//    public void addEntry(Transport obj) {
//        Log.e("MyDBHandler", "addEntry: Transport");
//        ContentValues values = new ContentValues();
//        System.out.println("DB Transport: " + obj.toString());
//
//
////        values.put(NOTE_COLUMN_ID, obj.getTravelID());
//        values.put(TRANSPORT_COLUMN_ID, obj.getID());
//        values.put(TRANSPORT_COLUMN_TRANSPORT_TYPE, obj.getTransportType());
//        values.put(TRANSPORT_COLUMN_DEPARTURE_PLACE, obj.getDeparturePlace());
//        values.put(TRANSPORT_COLUMN_DEPARTURE_DATE, obj.getDepartureDate());
//        values.put(TRANSPORT_COLUMN_DEPARTURE_TIME, obj.getDepartureTime());
//        values.put(TRANSPORT_COLUMN_ARRIVAL_PLACE, obj.getArrivalPlace());
//        values.put(TRANSPORT_COLUMN_ARRIVAL_TIME, obj.getArrivalTime());
//        values.put(TRANSPORT_COLUMN_ARRIVAL_DATE, obj.getArrivalDate());
//        values.put(VOICE_NOTE_COLUMN_POSITION, obj.getPosition());
//        values.put(TRANSPORT_COLUMN_TRAVEL_ID, obj.getTravelID());
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.insert(TABLE_NAME_TRANSPORT, null, values);
//        db.close();
//    }
//
//    public void addEntry(MapMarker obj) {
//        Log.e("MyDBHandler", "addEntry: MapCoords");
//        ContentValues values = new ContentValues();
//        System.out.println("DB Map Coords: " + obj.toString());
//
////        values.put(NOTE_COLUMN_ID, obj.getTravelID());
//        values.put(MAP_COLUMN_ID, obj.getID());
//        values.put(MAP_COLUMN_TITLE, obj.getTitle());
//        values.put(MAP_COLUMN_DESCRIPTION, obj.getDescription());
//        values.put(MAP_COLUMN_LONGITUDE, obj.getLongitude());
//        values.put(MAP_COLUMN_LATITUDE, obj.getLatitude());
//        values.put(MAP_COLUMN_DATE, obj.getDate());
//        values.put(MAP_COLUMN_POSITION, obj.getPosition());
//        values.put(MAP_COLUMN_TRAVEL_ID, obj.getTravelID());
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.insert(TABLE_NAME_MAP, null, values);
//        db.close();
//    }
//
//    public void addCountryVisit(String country, int travelID) {
//        Log.e("MyDBHandler", "addCountryVisit");
//        ContentValues values = new ContentValues();
//        System.out.println("DB COuntryVisit: " + country + " (" + travelID + ")");
//
////        values.put(COUNTRY_VISIT_COLUMN_ID, null);
//        values.put(COUNTRY_VISIT_COLUMN_COUNTRY, country);
//        values.put(COUNTRY_VISIT_COLUMN_TRAVEL_ID, travelID);
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.insert(TABLE_NAME_COUNTRY_VISIT, null, values);
//        db.close();
//    }

    /**
     * GET FROM DATABASE
     *
     * @return list of objects
     */

    public List<Travel> getTravelsList() {
        Log.e("DatabaseHelper", "getTravelList");
        List<Travel> travels = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME_TRAVEL;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all records and adding to the list
        if (c.moveToFirst()) {
            do {
                int travelID = c.getInt(c.getColumnIndex(TRAVEL_COLUMN_TRAVEL_ID));
                String name = c.getString(c.getColumnIndex(TRAVEL_COLUMN_NAME));
                String dateFROM = c.getString(c.getColumnIndex(TRAVEL_COLUMN_DATE_FROM));
                String dateTo = c.getString(c.getColumnIndex(TRAVEL_COLUMN_DATE_TO));
                String photoURI = c.getString(c.getColumnIndex(TRAVEL_COLUMN_COVER_PHOTO_URI));

                Travel r = new Travel(travelID, name, dateFROM, dateTo);
                if (!photoURI.matches("")) r.setImageURI(photoURI);

                travels.add(r);
            } while (c.moveToNext());
        }
//        Log.e("DatabaseHelper","travels.size()" +travels.size());
//        Log.e("DatabaseHelper","" +travels);
        db.close();
        Collections.reverse(travels);
        return travels;
    }

    public List<Photo> getTravelPhotos(int travelID, String givenDate) {
        Log.e("DatabaseHelper", "getTravelPhotos");
        List<Photo> photos = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME_PHOTO
                + " WHERE " + PHOTO_COLUMN_TRAVEL_ID + " == " + travelID;
        if (!givenDate.matches("")) {
            selectQuery += " AND " + PHOTO_COLUMN_DATE + " == \'" + givenDate + "\'";
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all records and adding to the list
        if (c.moveToFirst()) {
            do {
                int id = c.getInt(c.getColumnIndex(PHOTO_COLUMN_ID));
                String name = c.getString(c.getColumnIndex(PHOTO_COLUMN_TITLE));
                String photoURI = c.getString(c.getColumnIndex(PHOTO_COLUMN_IMAGE_URI));
                String date = c.getString(c.getColumnIndex(PHOTO_COLUMN_DATE));
                int position = c.getInt(c.getColumnIndex(PHOTO_COLUMN_POSITION));

                Photo r = new Photo(id, name, photoURI, date, position, travelID);
                photos.add(r);
            } while (c.moveToNext());
        }
//        Log.e("DatabaseHelper","photos.size()" +photos.size());
//        Log.e("DatabaseHelper","" +photos);
        db.close();
        return photos;
    }

    public List<Note> getTravelNotes(int travelID, String givenDate) {
        Log.e("DatabaseHelper", "getTravelNotes");
        List<Note> notes = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME_NOTE
                + " WHERE " + NOTE_COLUMN_TRAVEL_ID + " == " + travelID;

        if (!givenDate.matches("")) {
            selectQuery += " AND " + NOTE_COLUMN_DATE + " == \'" + givenDate + "\'";
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all records and adding to the list
        if (c.moveToFirst()) {
            do {
                int id = c.getInt(c.getColumnIndex(NOTE_COLUMN_ID));
                String name = c.getString(c.getColumnIndex(NOTE_COLUMN_TITLE));
                String note = c.getString(c.getColumnIndex(NOTE_COLUMN_NOTE));
                String date = c.getString(c.getColumnIndex(NOTE_COLUMN_DATE));
                int position = c.getInt(c.getColumnIndex(NOTE_COLUMN_POSITION));

                Note r = new Note(id, name, note, date, position, travelID);
                notes.add(r);
            } while (c.moveToNext());
        }
//        Log.e("DatabaseHelper","photos.size()" +photos.size());
//        Log.e("DatabaseHelper","" +photos);
        db.close();
        return notes;
    }
//
//    public List<VoiceNote> getTravelVoiceNotes(int travelID, String givenDate) {
//        Log.e("DatabaseHelper", "getTravelVoiceNotes");
//        List<VoiceNote> notes = new ArrayList<>();
//        String selectQuery = "SELECT  * FROM " + TABLE_NAME_VOICE_NOTE
//                + " WHERE " + VOICE_NOTE_COLUMN_TRAVEL_ID + " == " + travelID;
//
//        if (!givenDate.matches("")) {
//            selectQuery += " AND " + VOICE_NOTE_COLUMN_DATE + " == \'" + givenDate + "\'";
//        }
//
//
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor c = db.rawQuery(selectQuery, null);
//        // looping through all records and adding to the list
//        if (c.moveToFirst()) {
//            do {
//                long id = c.getLong(c.getColumnIndex(VOICE_NOTE_COLUMN_ID));
//                String name = c.getString(c.getColumnIndex(VOICE_NOTE_COLUMN_TITLE));
//                String uri = c.getString(c.getColumnIndex(VOICE_NOTE_COLUMN_URI));
//                String date = c.getString(c.getColumnIndex(VOICE_NOTE_COLUMN_DATE));
//                int position = c.getInt(c.getColumnIndex(VOICE_NOTE_COLUMN_POSITION));
//
//                VoiceNote r = new VoiceNote(id, name, uri, date, position, travelID);
//                notes.add(r);
//            } while (c.moveToNext());
//        }
////        Log.e("DatabaseHelper","photos.size()" +photos.size());
////        Log.e("DatabaseHelper","" +photos);
//        db.close();
//        return notes;
//    }
//
//    public List<Transport> getTravelTransports(int travelID, String givenDate) {
//        Log.e("DatabaseHelper", "getTravelTransports");
//        List<Transport> transports = new ArrayList<>();
//        String selectQuery = "SELECT  * FROM " + TABLE_NAME_TRANSPORT
//                + " WHERE " + TRANSPORT_COLUMN_TRAVEL_ID + " == " + travelID;
//
//        if (!givenDate.matches("")) {
//            selectQuery += " AND " + TRANSPORT_COLUMN_DEPARTURE_DATE + " == \'" + givenDate + "\'";
//        }
//
//
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor c = db.rawQuery(selectQuery, null);
//        // looping through all records and adding to the list
//        if (c.moveToFirst()) {
//            do {
//                long id = c.getLong(c.getColumnIndex(TRANSPORT_COLUMN_ID));
//                String transportType = c.getString(c.getColumnIndex(TRANSPORT_COLUMN_TRANSPORT_TYPE));
//                String aPlace = c.getString(c.getColumnIndex(TRANSPORT_COLUMN_ARRIVAL_PLACE));
//                String aDate = c.getString(c.getColumnIndex(TRANSPORT_COLUMN_ARRIVAL_DATE));
//                String aTime = c.getString(c.getColumnIndex(TRANSPORT_COLUMN_ARRIVAL_TIME));
//                String dPlace = c.getString(c.getColumnIndex(TRANSPORT_COLUMN_DEPARTURE_PLACE));
//                String dDate = c.getString(c.getColumnIndex(TRANSPORT_COLUMN_DEPARTURE_DATE));
//                String dTime = c.getString(c.getColumnIndex(TRANSPORT_COLUMN_DEPARTURE_TIME));
//                int position = c.getInt(c.getColumnIndex(TRANSPORT_COLUMN_POSITION));
//
//                Transport r = new Transport(id, transportType, dPlace, dDate, dTime, aPlace, aDate, aTime, position, travelID);
//                transports.add(r);
//            } while (c.moveToNext());
//        }
////        Log.e("DatabaseHelper","photos.size()" +photos.size());
////        Log.e("DatabaseHelper","" +photos);
//        db.close();
//        return transports;
//    }

//    public List<MapMarker> getTravelMapCoord(int travelID, String givenDate) {
//        Log.e("DatabaseHelper", "getAllRecipeList");
//        List<MapMarker> mapCoordinates = new ArrayList<>();
//        String selectQuery = "SELECT  * FROM " + TABLE_NAME_MAP
//                + " WHERE " + MAP_COLUMN_TRAVEL_ID + " == " + travelID;
//
//        if (!givenDate.matches("")) {
//            selectQuery += " AND " + MAP_COLUMN_DATE + " == \'" + givenDate + "\'";
//        }
//
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor c = db.rawQuery(selectQuery, null);
//        // looping through all records and adding to the list
//        if (c.moveToFirst()) {
//            do {
//                long id = c.getLong(c.getColumnIndex(MAP_COLUMN_ID));
//                String title = c.getString(c.getColumnIndex(MAP_COLUMN_TITLE));
//                String description = c.getString(c.getColumnIndex(MAP_COLUMN_DESCRIPTION));
//                float longitude = c.getFloat(c.getColumnIndex(MAP_COLUMN_LONGITUDE));
//                float latitude = c.getFloat(c.getColumnIndex(MAP_COLUMN_LATITUDE));
//                int position = c.getInt(c.getColumnIndex(MAP_COLUMN_POSITION));
//                String date = c.getString(c.getColumnIndex(MAP_COLUMN_DATE));
//
//                MapMarker r = new MapMarker(id, title, description, longitude, latitude, date, position, travelID);
//                mapCoordinates.add(r);
//            } while (c.moveToNext());
//        }
////        Log.e("DatabaseHelper","photos.size()" +photos.size());
////        Log.e("DatabaseHelper","" +photos);
//        db.close();
//        return mapCoordinates;
//    }

    public List<DiaryEntry> getEntries(int travelID, String givenDate) {
        List<Photo> photos = getTravelPhotos(travelID, givenDate);
        List<Note> notes = getTravelNotes(travelID, givenDate);
//        List<VoiceNote> voiceNotes = getTravelVoiceNotes(travelID, givenDate);
//        List<Transport> transports = getTravelTransports(travelID, givenDate);
//        List<MapMarker> mapCoords = getTravelMapCoord(travelID, givenDate);

        List<DiaryEntry> entries = new ArrayList<DiaryEntry>();
        entries.addAll(photos);
        entries.addAll(notes);
//        entries.addAll(voiceNotes);
//        entries.addAll(transports);
//        entries.addAll(mapCoords);

        Collections.sort(entries);

        return entries;
    }

    public List<String> getTravelCountryVisits(int travelID) {
        List<String> visits = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_NAME_COUNTRY_VISIT
                + " WHERE " + COUNTRY_VISIT_COLUMN_TRAVEL_ID + " == " + travelID;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                String country = c.getString(c.getColumnIndex(COUNTRY_VISIT_COLUMN_COUNTRY));

                visits.add(country);
            } while (c.moveToNext());
        }
        db.close();
        return visits;
    }

//    public List<String> getDistinctCountryVisits() {
//        List<String> visits = new ArrayList<>();
//
//        String selectQuery = "SELECT Distinct(CountryName) FROM " + TABLE_NAME_COUNTRY_VISIT;
////        String selectQuery = "SELECT CountryName FROM " + TABLE_NAME_COUNTRY_VISIT;
//
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor c = db.rawQuery(selectQuery, null);
//        if (c.moveToFirst()) {
//            do {
//                String country = c.getString(c.getColumnIndex(COUNTRY_VISIT_COLUMN_COUNTRY));
//
//                visits.add(country);
//            } while (c.moveToNext());
//        }
//        db.close();
//        return visits;
//    }

    /**
     * UPDATE IN DATABASE
     */

    public void update(Travel t) {
        String update = "UPDATE " + TABLE_NAME_TRAVEL +
                " SET " + TRAVEL_COLUMN_NAME + " = '" + t.getTitle() + "', " +
                TRAVEL_COLUMN_DATE_FROM + " = '" + t.getStartDate() + "', " +
                TRAVEL_COLUMN_DATE_TO + " = '" + t.getEndDate() + "', " +
                TRAVEL_COLUMN_COVER_PHOTO_URI + " = '" + t.getImagePath() + "' " +
                "WHERE " + TRAVEL_COLUMN_TRAVEL_ID + " = " + t.getTravelID() + ";";

        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(update);
        db.close();
    }

//    public void update(Transport t) {
//        String update = "UPDATE " + TABLE_NAME_TRANSPORT +
//                " SET " + TRANSPORT_COLUMN_TRANSPORT_TYPE + " = '" + t.getTransportType() + "', " +
//                TRANSPORT_COLUMN_DEPARTURE_PLACE + " = '" + t.getDeparturePlace() + "', " +
//                TRANSPORT_COLUMN_DEPARTURE_DATE + " = '" + t.getDepartureDate() + "', " +
//                TRANSPORT_COLUMN_DEPARTURE_TIME + " = '" + t.getDepartureTime() + "', " +
//                TRANSPORT_COLUMN_ARRIVAL_PLACE + " = '" + t.getArrivalPlace() + "', " +
//                TRANSPORT_COLUMN_ARRIVAL_DATE + " = '" + t.getArrivalDate() + "', " +
//                TRANSPORT_COLUMN_ARRIVAL_TIME + " = '" + t.getArrivalTime() + "', " +
//
//                "WHERE " + TRANSPORT_COLUMN_ID + " = " + t.getID() + ";";
//
//        SQLiteDatabase db = this.getReadableDatabase();
//        db.execSQL(update);
//        db.close();
//    }

    public void update(Note t) {
        String update = "UPDATE " + TABLE_NAME_NOTE +

                " SET " + NOTE_COLUMN_TITLE + " = '" + t.getTitle() + "', " +
                NOTE_COLUMN_NOTE + " = '" + t.getNote() + "', " +
                NOTE_COLUMN_DATE + " = '" + t.getDate() + "', " +
                NOTE_COLUMN_POSITION + " = '" + t.getPosition() + "', " +

                "WHERE " + NOTE_COLUMN_ID + " = " + t.getID() + ";";

        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(update);
        db.close();
    }

//    public void update(VoiceNote t) {
//        String update = "UPDATE " + TABLE_NAME_VOICE_NOTE +
//
//                " SET " + VOICE_NOTE_COLUMN_TITLE + " = '" + t.getTitle() + "', " +
//                VOICE_NOTE_COLUMN_URI + " = '" + t.getRecordURI() + "', " +
//                VOICE_NOTE_COLUMN_DATE + " = '" + t.getDate() + "', " +
//                VOICE_NOTE_COLUMN_POSITION + " = '" + t.getPosition() + "', " +
//
//                "WHERE " + VOICE_NOTE_COLUMN_ID + " = " + t.getID() + ";";
//
//        SQLiteDatabase db = this.getReadableDatabase();
//        db.execSQL(update);
//        db.close();
//    }

    public void update(Photo t) {
        String update = "UPDATE " + TABLE_NAME_PHOTO +

                " SET " + PHOTO_COLUMN_TITLE + " = '" + t.getTitle() + "', " +
                PHOTO_COLUMN_IMAGE_URI + " = '" + t.getPhotoPath() + "', " +
                PHOTO_COLUMN_DATE + " = '" + t.getDate() + "', " +
                PHOTO_COLUMN_POSITION + " = '" + t.getPosition() + "', " +

                "WHERE " + PHOTO_COLUMN_ID + " = " + t.getID() + ";";

        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(update);
        db.close();
    }

//    public void update(MapMarker t) {
//        String update = "UPDATE " + TABLE_NAME_MAP +
//
//                " SET " + MAP_COLUMN_TITLE + " = '" + t.getTitle() + "', " +
//                MAP_COLUMN_DESCRIPTION + " = '" + t.getDescription() + "', " +
//                MAP_COLUMN_LONGITUDE + " = '" + t.getLongitude() + "', " +
//                MAP_COLUMN_LATITUDE+ " = '" + t.getLatitude() + "', " +
//                MAP_COLUMN_DATE + " = '" + t.getDate() + "', " +
//                MAP_COLUMN_POSITION + " = '" + t.getPosition() + "', " +
//
//                "WHERE " + MAP_COLUMN_ID + " = " + t.getID() + ";";
//
//        SQLiteDatabase db = this.getReadableDatabase();
//        db.execSQL(update);
//        db.close();
//    }

    /**
     * UPDATE IN DATABASE
     */
    private static final String DELETE_SQL = " DELETE FROM ";

    public void delete(int travelID) {
        String delete = "";
        SQLiteDatabase db = this.getReadableDatabase();

        delete = DELETE_SQL + TABLE_NAME_TRANSPORT +
                " WHERE " + TRANSPORT_COLUMN_TRAVEL_ID + " == " + travelID;
        db.execSQL(delete);

        delete = DELETE_SQL + TABLE_NAME_PHOTO +
                " WHERE " + PHOTO_COLUMN_TRAVEL_ID + " == " + travelID;
        db.execSQL(delete);

        delete = DELETE_SQL + TABLE_NAME_NOTE +
                " WHERE " + NOTE_COLUMN_TRAVEL_ID + " == " + travelID;
        db.execSQL(delete);

        delete = DELETE_SQL + TABLE_NAME_VOICE_NOTE +
                " WHERE " + VOICE_NOTE_COLUMN_TRAVEL_ID + " == " + travelID;
        db.execSQL(delete);

        delete = DELETE_SQL + TABLE_NAME_MAP +
                " WHERE " + MAP_COLUMN_TRAVEL_ID + " == " + travelID;
        db.execSQL(delete);

        delete = DELETE_SQL + TABLE_NAME_COUNTRY_VISIT +
                " WHERE " + COUNTRY_VISIT_COLUMN_TRAVEL_ID + " == " + travelID;
        db.execSQL(delete);

        delete = DELETE_SQL + TABLE_NAME_TRAVEL +
                " WHERE " + TRAVEL_COLUMN_TRAVEL_ID + " == " + travelID;
        db.execSQL(delete);


        db.close();
    }

    public void removeEntry(long id, String entryType) {
//        DELETE FROM table
//                WHERE
//        condition;
        Log.i("DatabaseHelper","removeEntry("+id+", "+entryType+")");

        String delete = "DELETE FROM ";
        if (entryType.matches(ENTRY_TYPE_PHOTO)) {
            delete += TABLE_NAME_PHOTO +
                    " WHERE " + PHOTO_COLUMN_ID + " == " + id;
        } else if (entryType.matches(ENTRY_TYPE_NOTE)) {
            delete += TABLE_NAME_NOTE +
                    " WHERE " + NOTE_COLUMN_ID + " == " + id;
        }  else{
            Log.e("DatabaseHelper","removeEntry: DIDINT FIND TRANSPORTTYPE");
        }
        delete += ";";
        System.out.println(""+delete);

        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(delete);
        db.close();
    }

    public void removeCountryVisit(String country, int travelID) {
        Log.e("MyDBHandler", "removeCountryVisit");
        System.out.println("DB remove CountryVisit: " + country + " (" + travelID + ")");

        String remove = "DELETE FROM " + TABLE_NAME_COUNTRY_VISIT
                + " WHERE " + COUNTRY_VISIT_COLUMN_TRAVEL_ID + " == " + travelID
                + " AND " + COUNTRY_VISIT_COLUMN_COUNTRY + " == '" + country + "';";

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(remove);
        db.close();
    }


    public Travel getTravel(int travelID) {
        Log.e("DatabaseHelper", "getAllRecipeList");
        List<Travel> travels = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME_TRAVEL;
        selectQuery += " WHERE " + TRAVEL_COLUMN_TRAVEL_ID + " == " + travelID;

        SQLiteDatabase db = this.getReadableDatabase();
        Travel travel = null;
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all records and adding to the list
        if (c.moveToFirst()) {
            do {
                String name = c.getString(c.getColumnIndex(TRAVEL_COLUMN_NAME));
                String dateFROM = c.getString(c.getColumnIndex(TRAVEL_COLUMN_DATE_FROM));
                String dateTo = c.getString(c.getColumnIndex(TRAVEL_COLUMN_DATE_TO));
                String photoURI = c.getString(c.getColumnIndex(TRAVEL_COLUMN_COVER_PHOTO_URI));

                travel = new Travel(travelID, name, dateFROM, dateTo);
                if (!photoURI.matches("")) travel.setImageURI(photoURI);

            } while (c.moveToNext());
        }
//        Log.e("DatabaseHelper","travels.size()" +travels.size());
//        Log.e("DatabaseHelper","" +travels);
        db.close();
        return travel;
    }

    public int getTravelId(Travel t) {
        Log.e("DatabaseHelper", "getAllRecipeList");
        List<Travel> travels = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME_TRAVEL;
        selectQuery += " WHERE " + TRAVEL_COLUMN_NAME + " == '" + t.getTitle() +
                "' AND " + TRAVEL_COLUMN_DATE_FROM + " == '" + t.getStartDate() +
                "' AND " + TRAVEL_COLUMN_DATE_TO + " == '" + t.getEndDate() + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Travel travel = null;
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all records and adding to the list
        int travelID = 0;
        if (c.moveToFirst()) {
            do {
                travelID = c.getInt(c.getColumnIndex(TRAVEL_COLUMN_TRAVEL_ID));
                db.close();
                return travelID;
            } while (c.moveToNext());
        } else {
            db.close();
            return -1;
        }
    }


    private LocalDate stringToDate(String date) throws ParseException {
        Log.d("Date to parse", date);
        return LocalDate.parse(date, DateTimeFormatter.ofPattern(Consts.STRING_DATE_PATTERN));
    }

    private String dateToString(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern(Consts.STRING_DATE_PATTERN));
    }


    private void sortTravelList(List<Travel> list){

    }
}
