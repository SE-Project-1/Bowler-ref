/*  $Id$
 *
 *  Revisions:
 *    $Log: LaneEvent.java,v $
 *    Revision 1.6  2003/02/16 22:59:34  ???
 *    added mechnanical problem flag
 *
 *    Revision 1.5  2003/02/02 23:55:31  ???
 *    Many many changes.
 *
 *    Revision 1.4  2003/02/02 22:44:26  ???
 *    More data.
 *
 *    Revision 1.3  2003/02/02 17:49:31  ???
 *    Modified.
 *
 *    Revision 1.2  2003/01/30 21:21:07  ???
 *    *** empty log message ***
 *
 *    Revision 1.1  2003/01/19 22:12:40  ???
 *    created laneevent and laneobserver
 *
 *
 */

import java.util.HashMap;

public class LaneEvent {

	private Party p;
	int frame;
	int ball;
	Bowler bowler;
	int[][] cumulScore;
	HashMap score;
	int index;
	int frameNum;
	int[] curScores;
	boolean mechProb;
	boolean pinsetterChange;
	private boolean[] pinsStillStanding;

	public int totalPinsDown() {
		int count = 0;

		for (int i=0; i <= 9; i++) {
			if (!pinsStillStanding[i]) {
				count++;
			}
		}
		return count;
	}

	public LaneEvent( Party pty, int theIndex, Bowler theBowler, int[][] theCumulScore, HashMap theScore, int theFrameNum, int[] theCurScores, int theBall, boolean mechProblem, boolean psChange, boolean[] ps) {
		p = pty;
		index = theIndex;
		bowler = theBowler;
		cumulScore = theCumulScore;
		score = theScore;
		curScores = theCurScores;
		frameNum = theFrameNum;
		ball = theBall;
		mechProb = mechProblem;

		pinsStillStanding = new boolean[10];
		pinsetterChange = psChange;
		for (int i=0; i <= 9; i++) {
			pinsStillStanding[i] = ps[i];
		}
	}

	public boolean isMechanicalProblem() {
		return mechProb;
	}

	public int getFrameNum() {
		return frameNum;
	}

	public HashMap getScore( ) {
		return score;
	}


	public int[] getCurScores(){
		return curScores;
	}

	public int getIndex() {
		return index;
	}

	public int getFrame( ) {
		return frame;
	}

	public int getBall( ) {
		return ball;
	}

	public int[][] getCumulScore(){
		return cumulScore;
	}

	public Party getParty() {
		return p;
	}

	public Bowler getBowler() {
		return bowler;
	}

};
 
