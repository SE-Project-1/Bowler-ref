import java.io.*;
import java.util.Vector;
import java.util.Iterator;
import java.util.HashMap;
import java.net.*;
import java.awt.*;
import java.awt.print.*;


// scores    lanePublish()    cumulScores     bowlIndex   ball

public class LaneScorer {

    protected HashMap scores;
    protected int[][] cumulScores;
	protected int bowlIndex;
	protected Party party;
   // protected Bowler currentThrower;
    protected int[] curScores;
    protected boolean gameIsHalted;
    protected Vector subscribers;

    public LaneScorer()
    {
        scores = new HashMap();
		subscribers = new Vector();
        gameIsHalted = false;

    }

    public void publish( LaneEvent event ) {
        System.out.println("0th element = "+subscribers.get(0));
		if( subscribers.size() > 0 ) {
			Iterator eventIterator = subscribers.iterator();
			
			while ( eventIterator.hasNext() ) {
				( (LaneObserver) eventIterator.next()).receiveLaneEvent( event );
			}
		}
        System.out.println(2);
	}
    
    public LaneEvent lanePublish(int ball,int frameNumber,Bowler currentThrower) {
        System.out.println("ball");
		System.out.println(ball);
        LaneEvent laneEvent = new LaneEvent(party, bowlIndex, currentThrower, cumulScores, scores, frameNumber+1, curScores, ball, gameIsHalted);
		return laneEvent;
	}

    public void markScore( Bowler Cur, int frame, int ball, int score,int frameNumber ,int thisBall){
		int[] curScore;
		int index =  ( (frame - 1) * 2 + ball);

		curScore = (int[]) scores.get(Cur);


		curScore[ index - 1] = score;
		scores.put(Cur, curScore);
		getScore( Cur, frame, ball );
		publish( lanePublish(thisBall,frameNumber,Cur) );
	}

    int count_strikeballs(int[] curScore,int i)
    {
        int strikeballs = 1;
        if (curScore[i + 3] != -1) {
            //Still got em.
            strikeballs = 2;
        } else if (curScore[i + 4] != -1) {
            //Ok, got it.
            strikeballs = 2;
        }
        return strikeballs;
    }

    void add(int[] curScore,int i)
    {
        cumulScores[bowlIndex][i / 2] += curScore[i + 1] + cumulScores[bowlIndex][(i / 2) - 1];
        if (curScore[i + 2] != -1) {
            if (curScore[i + 2] != -2) {
                cumulScores[bowlIndex][(i / 2)] += curScore[i + 2];
            }
        } else {
            if (curScore[i + 3] != -2) {
                cumulScores[bowlIndex][(i / 2)] += curScore[i + 3];
            }
        }
    }

    void add_strike(int[] curScore, int i)
    {
        cumulScores[bowlIndex][i / 2] += 10;
        if (curScore[i + 1] != -1) {
            add(curScore,i);
        } else {
            if (i / 2 > 0) {
                cumulScores[bowlIndex][i / 2] += curScore[i + 2] + cumulScores[bowlIndex][(i / 2) - 1];
            } else {
                cumulScores[bowlIndex][i / 2] += curScore[i + 2];
            }
            if (curScore[i + 3] != -1) {
                if (curScore[i + 3] != -2) {
                    cumulScores[bowlIndex][(i / 2)] += curScore[i + 3];
                }
            } else {
                cumulScores[bowlIndex][(i / 2)] += curScore[i + 4];
            }
        }
    }

    int first_strike(int[] curScore,int i) {
        int strikeballs = 0;
        if (curScore[i + 2] != -1) {
            strikeballs = count_strikeballs(curScore,i);

        }
        if (strikeballs == 2) {
            //Add up the strike.
            //Add the next two balls to the current cumulscore.
            add_strike(curScore,i);
        } else {
            return 1;
        }
        return 0;
    }

    void normal_throw(int[] curScore,int i)
    {
        if( i%2 == 0 && i < 18){
            if ( i/2 == 0 ) {
                //First frame, first ball.  Set his cumul score to the first ball
                if(curScore[i] != -2){
                    cumulScores[bowlIndex][i/2] += curScore[i];
                }
            }
            else if (i/2 != 9){
                //add his last frame's cumul to this ball, make it this frame's cumul.
                if(curScore[i] != -2){
                    cumulScores[bowlIndex][i/2] += cumulScores[bowlIndex][i/2 - 1] + curScore[i];
                } else {
                    cumulScores[bowlIndex][i/2] += cumulScores[bowlIndex][i/2 - 1];
                }
            }
        } else if (i < 18){
            if(curScore[i] != -1 && i > 2){
                if(curScore[i] != -2){
                    cumulScores[bowlIndex][i/2] += curScore[i];
                }
            }
        }
        if (i/2 == 9){
            if (i == 18){
                cumulScores[bowlIndex][9] += cumulScores[bowlIndex][8];
            }
            if(curScore[i] != -2){
                cumulScores[bowlIndex][9] += curScore[i];
            }
        } else if (i/2 == 10) {
            if(curScore[i] != -2){
                cumulScores[bowlIndex][9] += curScore[i];
            }
        }
    }

    private int getScore( Bowler Cur, int frame,int ball) {
        int[] curScore;
        int totalScore = 0;
        curScore = (int[]) scores.get(Cur);
        for (int i = 0; i != 10; i++){
            cumulScores[bowlIndex][i] = 0;
        }
        int current = 2*(frame - 1)+ball-1;
        //Iterate through each ball until the current one.
        for (int i = 0; i != current+2; i++){
            //Spare:
            if( i%2 == 1 && curScore[i - 1] + curScore[i] == 10 && i < current - 1 && i < 19){
                //This ball was a the second of a spare.  
                //Also, we're not on the current ball.
                //Add the next ball to the ith one in cumul.
                cumulScores[bowlIndex][(i/2)] += curScore[i+1] + curScore[i]; 

            } else if(i < current && i%2 == 0 && curScore[i] == 10  && i < 18){
                //This ball is the first ball, and was a strike.
                //If we can get 2 balls after it, good add them to cumul.
                int f = first_strike(curScore,i);
                if(f == 1)
                    break;
            }else {
                //We're dealing with a normal throw, add it and be on our way.
                normal_throw(curScore,i);
            }
        }
        return totalScore;
    }
}
