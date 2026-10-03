package com.padel.clickscore;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.Color;
import android.speech.tts.TextToSpeech;
import android.view.*;
import android.widget.*;
import java.util.*;

public class MainActivity extends Activity implements TextToSpeech.OnInitListener {
    ScoreEngine s = new ScoreEngine(); MatchDb db; TextToSpeech tts;
    TextView pA,pB,games,sets,status; EditText nameA,nameB; long started=System.currentTimeMillis();
    Handler handler=new Handler(Looper.getMainLooper()); int pendingClicks=0; Runnable clickCommit;
    int green=Color.rgb(124,255,107), bg=Color.rgb(11,13,16), panel=Color.rgb(27,30,35), muted=Color.rgb(170,176,185);
    @Override public void onCreate(Bundle b){ super.onCreate(b); getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); db=new MatchDb(this); tts=new TextToSpeech(this,this); build(); }
    TextView tv(String text,int sp){ TextView v=new TextView(this); v.setText(text); v.setTextColor(Color.WHITE); v.setTextSize(sp); v.setGravity(Gravity.CENTER); v.setPadding(12,12,12,12); return v; }
    Button btn(String text){ Button b=new Button(this); b.setText(text); b.setTextSize(16); b.setTextColor(Color.WHITE); b.setBackgroundColor(panel); return b; }
    LinearLayout row(){ LinearLayout l=new LinearLayout(this); l.setOrientation(LinearLayout.HORIZONTAL); l.setGravity(Gravity.CENTER); return l; }
    void build(){
        ScrollView sc=new ScrollView(this); LinearLayout root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(20,24,20,30); root.setBackgroundColor(bg); sc.addView(root);
        TextView title=tv("PADEL CLICK SCORE",22); title.setTextColor(green); root.addView(title,new LinearLayout.LayoutParams(-1,-2));
        LinearLayout nr=row(); nameA=new EditText(this);nameB=new EditText(this); for(EditText e:new EditText[]{nameA,nameB}){e.setTextColor(Color.WHITE);e.setHintTextColor(muted);e.setSingleLine(true);e.setTextSize(17);} nameA.setHint("Team A");nameB.setHint("Team B"); nr.addView(nameA,new LinearLayout.LayoutParams(0,60,1));nr.addView(nameB,new LinearLayout.LayoutParams(0,60,1));root.addView(nr);
        LinearLayout score=row(); pA=tv("0",72);pB=tv("0",72); pA.setBackgroundColor(panel);pB.setBackgroundColor(panel); score.addView(pA,new LinearLayout.LayoutParams(0,150,1));score.addView(pB,new LinearLayout.LayoutParams(0,150,1));root.addView(score);
        LinearLayout add=row(); Button a=btn("+ POINT A");Button bb=btn("+ POINT B"); a.setTextColor(green);bb.setTextColor(green);add.addView(a,new LinearLayout.LayoutParams(0,62,1));add.addView(bb,new LinearLayout.LayoutParams(0,62,1));root.addView(add);
        games=tv("Games  0 - 0",28);sets=tv("Sets   0 - 0",28);status=tv("Ready",18); status.setTextColor(muted);root.addView(games);root.addView(sets);root.addView(status);
        LinearLayout controls=row();Button undo=btn("UNDO");Button hist=btn("HISTORY");Button reset=btn("NEW MATCH");controls.addView(undo,new LinearLayout.LayoutParams(0,58,1));controls.addView(hist,new LinearLayout.LayoutParams(0,58,1));controls.addView(reset,new LinearLayout.LayoutParams(0,58,1));root.addView(controls);
        CheckBox golden=new CheckBox(this);golden.setText("Golden Point");golden.setTextColor(Color.WHITE);root.addView(golden);
        TextView help=tv("Remote: single click = Team A • double click = Team B • long press = Undo\nSupported HID keys: Volume/Media/Enter/Space",14);help.setTextColor(muted);root.addView(help);
        a.setOnClickListener(v->point(true));bb.setOnClickListener(v->point(false));undo.setOnClickListener(v->{s.undo();refresh();speak("Undo");});reset.setOnClickListener(v->newMatch());hist.setOnClickListener(v->history());golden.setOnCheckedChangeListener((x,on)->s.goldenPoint=on);
        setContentView(sc); refresh();
    }
    void syncNames(){ if(nameA.getText().length()>0)s.teamA=nameA.getText().toString(); if(nameB.getText().length()>0)s.teamB=nameB.getText().toString(); }
    void point(boolean a){ syncNames(); boolean was=s.finished; s.point(a); refresh(); speak(s.speech()); if(!was&&s.finished){db.add(s.teamA,s.teamB,s.finalScore(),s.sa>s.sb?s.teamA:s.teamB,System.currentTimeMillis()-started);} }
    void refresh(){pA.setText(s.pointA());pB.setText(s.pointB());games.setText("Games  "+s.gameScore());sets.setText("Sets   "+s.setScore());status.setText(s.finished?"MATCH FINISHED":(s.tie?"TIE BREAK":s.speech()));}
    void speak(String x){ if(tts!=null)tts.speak(x,TextToSpeech.QUEUE_FLUSH,null,"score"); }
    public void onInit(int st){ if(st==TextToSpeech.SUCCESS){Locale en=Locale.US;tts.setLanguage(en);tts.setSpeechRate(0.92f);} }
    void newMatch(){new AlertDialog.Builder(this).setTitle("New match?").setMessage("Current score will be cleared.").setNegativeButton("Cancel",null).setPositiveButton("New",(d,w)->{s=new ScoreEngine();started=System.currentTimeMillis();refresh();}).show();}
    void history(){List<String> list=db.all(); if(list.isEmpty())list=Arrays.asList("No saved matches yet"); new AlertDialog.Builder(this).setTitle("Match History").setItems(list.toArray(new String[0]),null).setPositiveButton("Close",null).show();}
    boolean remoteKey(int key, KeyEvent e){ boolean ok=key==KeyEvent.KEYCODE_VOLUME_UP||key==KeyEvent.KEYCODE_VOLUME_DOWN||key==KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE||key==KeyEvent.KEYCODE_ENTER||key==KeyEvent.KEYCODE_SPACE||key==KeyEvent.KEYCODE_HEADSETHOOK; if(!ok)return false; if(e.getAction()==KeyEvent.ACTION_DOWN&&e.getRepeatCount()>0){handler.removeCallbacks(clickCommit);pendingClicks=0;s.undo();refresh();speak("Undo");return true;} if(e.getAction()==KeyEvent.ACTION_UP){pendingClicks++;handler.removeCallbacks(clickCommit);clickCommit=()->{int c=pendingClicks;pendingClicks=0;if(c>=2)point(false);else point(true);};handler.postDelayed(clickCommit,320);return true;} return true; }
    @Override public boolean dispatchKeyEvent(KeyEvent e){ if(remoteKey(e.getKeyCode(),e))return true;return super.dispatchKeyEvent(e); }
    @Override protected void onDestroy(){if(tts!=null){tts.stop();tts.shutdown();}super.onDestroy();}
}
