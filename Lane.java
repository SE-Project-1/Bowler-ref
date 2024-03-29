
/* $Id$
 *
 * Revisions:
 *   $Log: Lane.java,v $
 *   Revision 1.52  2003/02/20 20:27:45  ???
 *   Fouls disables.
 *
 *   Revision 1.51  2003/02/20 20:01:32  ???
 *   Added things.
 *
 *   Revision 1.50  2003/02/20 19:53:52  ???
 *   Added foul support.  Still need to update laneview and test this.
 *
 *   Revision 1.49  2003/02/20 11:18:22  ???
 *   Works beautifully.
 *
 *   Revision 1.48  2003/02/20 04:10:58  ???
 *   Score reporting code should be good.
 *
 *   Revision 1.47  2003/02/17 00:25:28  ???
 *   Added disbale controls for View objects.
 *
 *   Revision 1.46  2003/02/17 00:20:47  ???
 *   fix for event when game ends
 *
 *   Revision 1.43  2003/02/17 00:09:42  ???
 *   fix for event when game ends
 *
 *   Revision 1.42  2003/02/17 00:03:34  ???
 *   Bug fixed
 *
 *   Revision 1.41  2003/02/16 23:59:49  ???
 *   Reporting of sorts.
 *
 *   Revision 1.40  2003/02/16 23:44:33  ???
 *   added mechnanical problem flag
 *
 *   Revision 1.39  2003/02/16 23:43:08  ???
 *   added mechnanical problem flag
 *
 *   Revision 1.38  2003/02/16 23:41:05  ???
 *   added mechnanical problem flag
 *
 *   Revision 1.37  2003/02/16 23:00:26  ???
 *   added mechnanical problem flag
 *
 *   Revision 1.36  2003/02/16 21:31:04  ???
 *   Score logging.
 *
 *   Revision 1.35  2003/02/09 21:38:00  ???
 *   Added lots of comments
 *
 *   Revision 1.34  2003/02/06 00:27:46  ???
 *   Fixed a race condition
 *
 *   Revision 1.33  2003/02/05 11:16:34  ???
 *   Boom-Shacka-Lacka!!!
 *
 *   Revision 1.32  2003/02/05 01:15:19  ???
 *   Real close now.  Honest.
 *
 *   Revision 1.31  2003/02/04 22:02:04  ???
 *   Still not quite working...
 *
 *   Revision 1.30  2003/02/04 13:33:04  ???
 *   Lane may very well work now.
 *
 *   Revision 1.29  2003/02/02 23:57:27  ???
 *   fix on pinsetter hack
 *
 *   Revision 1.28  2003/02/02 23:49:48  ???
 *   Pinsetter generates an event when all pins are reset
 *
 *   Revision 1.27  2003/02/02 23:26:32  ???
 *   ControlDesk now runs its own thread and polls for free lanes to assign queue members to
 *
 *   Revision 1.26  2003/02/02 23:11:42  ???
 *   parties can now play more than 1 game on a lane, and lanes are properly released after games
 *
 *   Revision 1.25  2003/02/02 22:52:19  ???
 *   Lane compiles
 *
 *   Revision 1.24  2003/02/02 22:50:10  ???
 *   Lane compiles
 *
 *   Revision 1.23  2003/02/02 22:47:34  ???
 *   More observering.
 *
 *   Revision 1.22  2003/02/02 22:15:40  ???
 *   Add accessor for pinsetter.
 *
 *   Revision 1.21  2003/02/02 21:59:20  ???
 *   added conditions for the party choosing to play another game
 *
 *   Revision 1.20  2003/02/02 21:51:54  ???
 *   LaneEvent may very well be observer method.
 *
 *   Revision 1.19  2003/02/02 20:28:59  ???
 *   fixed sleep thread bug in lane
 *
 *   Revision 1.18  2003/02/02 18:18:51  ???
 *   more changes. just need to fix scoring.
 *
 *   Revision 1.17  2003/02/02 17:47:02  ???
 *   Things are pretty close to working now...
 *
 *   Revision 1.16  2003/01/30 22:09:32  ???
 *   Worked on scoring.
 *
 *   Revision 1.15  2003/01/30 21:45:08  ???
 *   Fixed speling of received in Lane.
 *
 *   Revision 1.14  2003/01/30 21:29:30  ???
 *   Fixed some MVC stuff
 *
 *   Revision 1.13  2003/01/30 03:45:26  ???
 *   *** empty log message ***
 *
 *   Revision 1.12  2003/01/26 23:16:10  ???
 *   Improved thread handeling in lane/controldesk
 *
 *   Revision 1.11  2003/01/26 22:34:44  ???
 *   Total rewrite of lane and pinsetter for R2's observer model
 *   Added Lane/Pinsetter Observer
 *   Rewrite of scoring algorythm in lane
 *
 *   Revision 1.10  2003/01/26 20:44:05  ???
 *   small changes
 *
 * 
 */

import java.util.Vector;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Date;

public class Lane extends Thread implements PinsetterObserver {	
	private Pinsetter setter;
	private boolean partyAssigned;
	private boolean gameFinished;
	private Iterator bowlerIterator;
	private boolean tenthFrameStrike;
	private int ball;
	private int frameNumber;
	private boolean canThrowAgain;
	
	private int[][] finalScores;
	private int gameNumber;
	
	private Bowler currentThrower;			// = the thrower who just took a throw

	private LaneScorer laneScorer;

	/** Lane()
	 * 
	 * Constructs a new lane and starts its thread
	 * 
	 * @pre none
	 * @post a new lane has been created and its thered is executing
	 */
	public Lane() { 
		setter = new Pinsetter();
		partyAssigned = false;
		gameNumber = 0;
		

		setter.subscribe( this );
		
		this.start();
	}

	/** run()
	 * 
	 * entry point for execution of this lane 
	 */

	void continue_bowling()
	{
		currentThrower = (Bowler)bowlerIterator.next();
		//System.out.println("CONTINUE");
		canThrowAgain = true;
		tenthFrameStrike = false;
		ball = 0;
		while (canThrowAgain) {
			setter.ballThrown();		// simulate the thrower's ball hiting
			ball++;
		}

		if (frameNumber == 9){
			finalScores[laneScorer.bowlIndex][gameNumber] = laneScorer.cumulScores[laneScorer.bowlIndex][9];
			try{
				Date date = new Date();
				String dateString = "" + date.getHours() + ":" + date.getMinutes() + " " + date.getMonth() + "/" + date.getDay() + "/" + (date.getYear() + 1900);
				BowlerFile.addScore(currentThrower.getNickName(), dateString, new Integer(laneScorer.cumulScores[laneScorer.bowlIndex][9]).toString());
			} catch (Exception e) {System.err.println("Exception in addScore. "+ e );}
		}
		setter.reset();
		laneScorer.bowlIndex++;
	}

	void stop_bowling()
	{
		frameNumber++;
		resetBowlerIterator();
		laneScorer.bowlIndex = 0;
		if (frameNumber > 9) {
			gameFinished = true;
			gameNumber++;
		}
	}

	void continue_game()
	{
		while (laneScorer.gameIsHalted) {
			try {
				sleep(10);
			} catch (Exception e) {}
		}
		if (bowlerIterator.hasNext()) {
			continue_bowling();
			
		} else {
			stop_bowling();
		}
	}



	void generate_score(Iterator scoreIt,Vector printVector)
	{
		int myIndex = 0;

		while (scoreIt.hasNext()) {
			Bowler thisBowler = (Bowler) scoreIt.next();
			ScoreReport sr = new ScoreReport(thisBowler, finalScores[myIndex++], gameNumber);
			sr.sendEmail(thisBowler.getEmail());
			Iterator printIt = printVector.iterator();
			while (printIt.hasNext()) {
				if (thisBowler.getNickName() == (String) printIt.next()) {
					System.out.println("Printing " + thisBowler.getNickName());
					sr.sendPrintout();
				}
			}
		}
	}

	void stop_game() {
		Vector printVector;
		String name = ((Bowler) laneScorer.party.getMembers().get(0)).getNickName();
		EndGameReport egr = new EndGameReport(name + "'s Party", laneScorer.party);
		printVector = egr.getResult();
		partyAssigned = false;
		Iterator scoreIt = laneScorer.party.getMembers().iterator();
		laneScorer.party = null;
		partyAssigned = false;

		laneScorer.publish(laneScorer.lanePublish(ball,frameNumber,currentThrower, false, new boolean[10]));

		generate_score(scoreIt,printVector);
	}

	void play_game()
	{
		resetScores();
		resetBowlerIterator();
	}

	void finish_game()
	{
		EndGamePrompt egp = new EndGamePrompt( ((Bowler) laneScorer.party.getMembers().get(0)).getNickName() + "'s Party" );
		int result = egp.getResult();
		egp.distroy();
		egp = null;
		System.out.println("result was: " + result);
		
		// TODO: send record of scores to control desk
		if (result == 1) {					// yes, want to play again
			play_game();
			
		} else {// no, dont want to play another game
			stop_game();
		}
	}

	public void run() {
		laneScorer = new LaneScorer();
		while (true) {
			if (partyAssigned && !gameFinished) {	// we have a party on this lane, 
								// so next bower can take a throw
			
				continue_game();
			} else if (partyAssigned && gameFinished) {
				finish_game();
				
			}
			try {
				sleep(10);
			} catch (Exception e) {}
		}
	}
	
	/** recievePinsetterEvent()
	 * 
	 * recieves the thrown event from the pinsetter
	 *
	 * @pre none
	 * @post the event has been acted upon if desiered
	 * 
	 * @param pe 		The pinsetter event that has been received.
	 */

	void tenth_frame(PinsetterEvent pe)
	{
		if (pe.totalPinsDown() == 10) {
			setter.resetPins();
			if(pe.getThrowNumber() == 1) {
				tenthFrameStrike = true;
			}
		}
		else
		{
			if (pe.getThrowNumber() == 2 && !tenthFrameStrike) {
				canThrowAgain = false;
			}
		}

		if (pe.getThrowNumber() == 3) {
			canThrowAgain = false;
		}
	}

	public void receivePinsetterEvent(PinsetterEvent pe) {
		
			if (pe.pinsDownOnThisThrow() >=  0) {			// this is a real throw
				laneScorer.markScore(currentThrower, frameNumber + 1, pe.getThrowNumber(), pe.pinsDownOnThisThrow(),frameNumber,ball);
	
				// next logic handles the ?: what conditions dont allow them another throw?
				// handle the case of 10th frame first
				if (frameNumber == 9) {
					tenth_frame(pe);
				} else { // its not the 10th frame
			
					if (pe.pinsDownOnThisThrow() == 10) {		// threw a strike
						canThrowAgain = false;
					} else if (pe.getThrowNumber() == 2) {
						canThrowAgain = false;
					} else if (pe.getThrowNumber() == 3)  
						System.out.println("I'm here...");
				}
			} else {								//  this is not a real throw, probably a reset
			}
			boolean[] ps = new boolean[10];
			for (int i=0; i <= 9; i++) {
				ps[i] = !pe.pinKnockedDown(i);
			}
			laneScorer.publish(laneScorer.lanePublish(ball,frameNumber,currentThrower,true, ps));
	}
	
	/** resetBowlerIterator()
	 * 
	 * sets the current bower iterator back to the first bowler
	 * 
	 * @pre the party as been assigned
	 * @post the iterator points to the first bowler in the party
	 */
	private void resetBowlerIterator() {
		bowlerIterator = (laneScorer.party.getMembers()).iterator();
	}

	/** resetScores()
	 * 
	 * resets the scoring mechanism, must be called before scoring starts
	 * 
	 * @pre the party has been assigned
	 * @post scoring system is initialized
	 */
	private void resetScores() {
		Iterator bowlIt = (laneScorer.party.getMembers()).iterator();

		while ( bowlIt.hasNext() ) {
			int[] toPut = new int[25];
			for ( int i = 0; i != 25; i++){
				toPut[i] = -1;
			}
			laneScorer.scores.put( bowlIt.next(), toPut );
		}
		gameFinished = false;
		frameNumber = 0;
	}
		
	/** assignParty()
	 * 
	 * assigns a party to this lane
	 * 
	 * @pre none
	 * @post the party has been assigned to the lane
	 * 
	 * @param theParty		Party to be assigned
	 */
	public void assignParty( Party theParty ) {
		laneScorer.party = theParty;
		resetBowlerIterator();
		partyAssigned = true;
		
		laneScorer.curScores = new int[laneScorer.party.getMembers().size()];
		laneScorer.cumulScores = new int[laneScorer.party.getMembers().size()][10];
		finalScores = new int[laneScorer.party.getMembers().size()][128]; //Hardcoding a max of 128 games, bite me.
		gameNumber = 0;
		
		resetScores();
		System.out.println("INSIDE LANE");
	}

	/** markScore()
	 *
	 * Method that marks a bowlers score on the board.
	 * 
	 * @param Cur		The current bowler
	 * @param frame	The frame that bowler is on
	 * @param ball		The ball the bowler is on
	 * @param score	The bowler's score 
	 */
	

	/** lanePublish()
	 *
	 * Method that creates and returns a newly created laneEvent
	 * 
	 * @return		The new lane event
	 */
	

	/** getScore()
	 *
	 * Method that calculates a bowlers score
	 * 
	 *
	 * @return			The bowlers total score
	 */

	/** isPartyAssigned()
	 * 
	 * checks if a party is assigned to this lane
	 * 
	 * @return true if party assigned, false otherwise
	 */
	public boolean isPartyAssigned() {
		return partyAssigned;
	}
	
	/** isGameFinished
	 * 
	 * @return true if the game is done, false otherwise
	 */
	public boolean isGameFinished() {
		return gameFinished;
	}

	/** subscribe
	 * 
	 * Method that will add a subscriber
	 * 
	 */

	public void subscribe( LaneObserver adding ) {
		laneScorer.subscribers.add( adding );
	}

	/** unsubscribe
	 * 
	 * Method that unsubscribes an observer from this object
	 * 
	 * @param removing	The observer to be removed
	 */
	
	public void unsubscribe( LaneObserver removing ) {
		laneScorer.subscribers.remove( removing );
	}

	/** publish
	 *
	 * Method that publishes an event to subscribers
	 * 
	 * @param event	Event that is to be published
	 */

	

	/**
	 * Accessor to get this Lane's pinsetter
	 * 
	 * @return		A reference to this lane's pinsetter
	 */

	public Pinsetter getPinsetter() {
		return setter;	
	}

	/**
	 * Pause the execution of this game
	 */
	public void pauseGame() {
		laneScorer.gameIsHalted = true;
		laneScorer.publish(laneScorer.lanePublish(ball,frameNumber,currentThrower, false, new boolean[10]));
	}
	
	/**
	 * Resume the execution of this game
	 */
	public void unPauseGame() {
		laneScorer.gameIsHalted = false;
		laneScorer.publish(laneScorer.lanePublish(ball,frameNumber,currentThrower, false, new boolean[10]));
	}

}

