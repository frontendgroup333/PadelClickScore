package com.padel.clickscore;

import java.util.*;

public class ScoreEngine {
    public int pa=0,pb=0,ga=0,gb=0,sa=0,sb=0;
    public int tba=0,tbb=0;
    public boolean tie=false, finished=false, goldenPoint=false;
    public final ArrayList<int[]> undo = new ArrayList<>();
    public String teamA="Team A", teamB="Team B";

    private void snapshot(){ undo.add(new int[]{pa,pb,ga,gb,sa,sb,tba,tbb,tie?1:0,finished?1:0}); }
    public void undo(){ if(undo.isEmpty()) return; int[] x=undo.remove(undo.size()-1); pa=x[0];pb=x[1];ga=x[2];gb=x[3];sa=x[4];sb=x[5];tba=x[6];tbb=x[7];tie=x[8]==1;finished=x[9]==1; }
    public void point(boolean a){ if(finished)return; snapshot(); if(tie){ if(a)tba++;else tbb++; if((tba>=7||tbb>=7)&&Math.abs(tba-tbb)>=2){ boolean winner=tba>tbb; tie=false; tba=tbb=0; winSet(winner); } return; }
        if(a) pa++; else pb++;
        if(goldenPoint && pa>=3 && pb>=3){ winGame(a); return; }
        if((pa>=4||pb>=4)&&Math.abs(pa-pb)>=2) winGame(pa>pb);
    }
    private void winGame(boolean a){ pa=pb=0; if(a)ga++;else gb++;
        if(ga==6&&gb==6){tie=true;return;}
        if((ga>=6||gb>=6)&&Math.abs(ga-gb)>=2){ winSet(ga>gb); }
    }
    private void winSet(boolean a){ ga=gb=0; if(a)sa++;else sb++; if(sa==2||sb==2) finished=true; }
    public String pointA(){return tie?String.valueOf(tba):display(pa,pb,true);} public String pointB(){return tie?String.valueOf(tbb):display(pb,pa,false);}    
    private String display(int p,int other,boolean a){ if(p<=2)return new String[]{"0","15","30"}[p]; if(p==3)return "40"; if(p==other)return "40"; if(p>other)return "AD"; return "40"; }
    public String speech(){
        if(finished) return "Game, set and match, "+(sa>sb?teamA:teamB);
        if(tie) return "Tie break, "+tba+" "+tbb;
        if(pa>=3&&pb>=3){ if(pa==pb)return "Deuce"; return "Advantage "+(pa>pb?teamA:teamB); }
        String[] n={"love","15","30","40"}; String a=n[Math.min(pa,3)], b=n[Math.min(pb,3)];
        if(pa==pb && pa>0) return a+" all";
        return a+" "+b;
    }
    public String setScore(){return sa+" - "+sb;} public String gameScore(){return ga+" - "+gb;}
    public String finalScore(){return "Sets "+sa+"-"+sb+", Games "+ga+"-"+gb;}
}
