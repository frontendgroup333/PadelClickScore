package com.padel.clickscore;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.*;
import java.util.*;

public class MatchDb extends SQLiteOpenHelper {
    public MatchDb(Context c) { super(c, "padel_matches.db", null, 1); }
    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE matches(id INTEGER PRIMARY KEY AUTOINCREMENT, played_at INTEGER, team_a TEXT, team_b TEXT, score TEXT, winner TEXT, duration INTEGER)");
    }
    @Override public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {}
    public void add(String a, String b, String score, String winner, long duration) {
        ContentValues v = new ContentValues();
        v.put("played_at", System.currentTimeMillis()); v.put("team_a", a); v.put("team_b", b);
        v.put("score", score); v.put("winner", winner); v.put("duration", duration);
        getWritableDatabase().insert("matches", null, v);
    }
    public List<String> all() {
        ArrayList<String> out = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery("SELECT played_at,team_a,team_b,score,winner,duration FROM matches ORDER BY id DESC", null);
        while(c.moveToNext()) {
            java.text.DateFormat df = java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.SHORT, java.text.DateFormat.SHORT);
            String when = df.format(new java.util.Date(c.getLong(0)));
            long sec = c.getLong(5)/1000;
            out.add(when+"\n"+c.getString(1)+" vs "+c.getString(2)+"\n"+c.getString(3)+" • Winner: "+c.getString(4)+" • "+(sec/60)+"m "+(sec%60)+"s");
        }
        c.close(); return out;
    }
}
