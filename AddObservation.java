public void addObservation(String name, int hike_id, String time, String comment) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(HIKE_ID_FK, hike_id);
    values.put(NAME_COLUMN, name);
    values.put(TIME_COLUMN, time);
    values.put(COMMENT_COLUMN, comment);
    long result = db.insert(OBSERVATION_TABLE, null, values);
}

