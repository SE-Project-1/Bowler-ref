/**
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

public class LaneStatusView implements ActionListener, LaneObserver {

	private JPanel jp;

	private JLabel curBowler, foul, pinsDown;
	private JButton viewLane;
	private JButton viewPinSetter, maintenance;

	private PinSetterView psv;
	private LaneView lv;
	private Lane lane;
	int laneNum;

	boolean laneShowing;
	boolean psShowing;

	public LaneStatusView(Lane lane, int laneNum ) {

		this.lane = lane;
		this.laneNum = laneNum;

		laneShowing=false;
		psShowing=false;

		psv = new PinSetterView( laneNum );
		Pinsetter ps = lane.getPinsetter();
		ps.subscribe(psv);

		lv = new LaneView( lane, laneNum );
		lane.subscribe(lv);


		jp = new JPanel();
		jp.setLayout(new FlowLayout());
		JLabel cLabel = new JLabel( "Now Bowling: " );
		curBowler = new JLabel( "(no one)" );
		JLabel fLabel = new JLabel( "Foul: " );
		foul = new JLabel( " " );
		JLabel pdLabel = new JLabel( "Pins Down: " );
		pinsDown = new JLabel( "0" );

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());

		viewLane = Components.createJButton("View Lane", buttonPanel);
		viewLane.addActionListener(this);

		viewPinSetter = Components.createJButton("Pinsetter", buttonPanel);
		viewPinSetter.addActionListener(this);

		maintenance = Components.createJButton("		", buttonPanel);
		maintenance.setBackground( Color.GREEN );
		maintenance.addActionListener(this);

		viewLane.setEnabled( false );
		viewPinSetter.setEnabled( false );

		jp.add( cLabel );
		jp.add( curBowler );
		jp.add( pdLabel );
		jp.add( pinsDown );
		jp.add(buttonPanel);

	}

	public JPanel showLane() {
		return jp;
	}

	public void actionPerformed( ActionEvent e ) {
		if ( lane.isPartyAssigned() ) { 
			if (e.getSource().equals(viewPinSetter)) {
				if ( psShowing == false ) {
					psv.show();
					psShowing=true;
				} else if ( psShowing == true ) {
					psv.hide();
					psShowing=false;
				}
			}
		}
		if (e.getSource().equals(viewLane)) {
			if ( lane.isPartyAssigned() ) { 
				if ( laneShowing == false ) {
					lv.show();
					laneShowing=true;
				} else if ( laneShowing == true ) {
					lv.hide();
					laneShowing=false;
				}
			}
		}
		if (e.getSource().equals(maintenance)) {
			if ( lane.isPartyAssigned() ) {
				lane.unPauseGame();
				maintenance.setBackground( Color.GREEN );
			}
		}
	}

	public void receiveLaneEvent(LaneEvent le) {

		if(le.pinsetterChange){
			pinsDown.setText( (Integer.valueOf(le.totalPinsDown())).toString() );
		}

		else {
			curBowler.setText(((Bowler) le.getBowler()).getNickName());
			if (le.isMechanicalProblem()) {
				maintenance.setBackground(Color.RED);
			}
			if (lane.isPartyAssigned() == false) {
				viewLane.setEnabled(false);
				viewPinSetter.setEnabled(false);
			} else {
				viewLane.setEnabled(true);
				viewPinSetter.setEnabled(true);
			}
		}
	}

}
