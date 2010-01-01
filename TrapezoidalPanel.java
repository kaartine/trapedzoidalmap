/****************************************************
 * This class handels the buttons and check boxes
 * 
 *	This file is written by Jukka Kaartinen
 * jukka.kaartinen@tut.fi
 *************************************************/

import java.awt.*;
import java.awt.event.*;

class TrapezoidalPanel extends Panel implements ActionListener {

	Button build, clear, query, find, bNext;
	Checkbox rand, route, speed, cNext;

	static final String BUILD = "build";
	static final String CLEAR = "clear";
	static final String QUERY = "query";
	static final String FIND = "find";
	static final String NEXT = "next"; // Takes next step when building the map
	
	private String state = BUILD;
	
	String command;
		
	Data data = null;
	TRMap map = null;
	InfoBox info = null;
	Component parent = null;

   TrapezoidalPanel(Data d, TRMap m, InfoBox i, Component parent) {
   	this.parent = parent;
   	data = d;
   	map = m;
   	info = i;
   	
   	build = new Button();
      build.setLabel("Build Map");
		build.setActionCommand(BUILD);
		
		bNext = new Button("Next Step");
   	bNext.setActionCommand(NEXT);
		bNext.setEnabled(false);
		
   	clear = new Button("Clear");
   	clear.setActionCommand(CLEAR);

   	query = new Button("Query Point");
   	query.setEnabled(false);
		query.setActionCommand(QUERY);
		
		find = new Button("Find");
   	find.setEnabled(false);
		find.setActionCommand(FIND);
		
		rand = new Checkbox("Rand. order", true);		
		route = new Checkbox("Show the path", false);		
		speed = new Checkbox("Fast", true);
		cNext = new Checkbox("Insert manually", false);

		//Listen for actions on buttons
		build.addActionListener(this);
		clear.addActionListener(this);
		query.addActionListener(this);
		find.addActionListener(this);
		bNext.addActionListener(this);

   	//Add Components to the Applet
   	setLayout(new GridLayout(15,1,1,1));
   	add(build);
   	add(bNext);
   	add(clear);
   	add(query);
   	add(find);
   	add(rand);
   	add(route);
   	add(speed);
   	add(cNext);
   }

	/** Draws a box around this panel. */
   public void paint(Graphics g) {}
   public void update(Graphics g){
		// override update to avoid flickering
		paint(g);
	}
	
  /**
   * Puts a little breathing space between
   * the panel and its contents, which lets us draw a box
   * in the paint() method.
   */
   public Insets getInsets() {
   	return new Insets(2,2,2,2);
	}
   
   public void actionPerformed(ActionEvent e) {
   	command = e.getActionCommand();
		
      if (command == BUILD) {      	
      	if(data.numSegments > 0) {
      		boolean fast = speed.getState();
				boolean next = cNext.getState();
      		map.clear();
      		info.addItem("\nStarted to make the map!");
      		data.noMoreSegments();
      		
      		if(next == true) {
      			bNext.setEnabled(true);
      		} else {
      			bNext.setEnabled(false);
      		}
      		
      		data.drawTrapezoids(speed.getState());
      		
      		if(next == false) {
       			map.makeMap(data.segments,data.numSegments,rand.getState(),fast);
       		       			
         		info.addItem("The Map is ready!");
         	} else {
         		map.makeMapNext(data.segments,data.numSegments,rand.getState());
         	}
         	
         	query.setEnabled(true);
       		find.setEnabled(true);
         } else {
         	// No segments
         	info.addItem("You haven't inserted any segments!");
         }
      } else if( command == CLEAR) {
      	data.clear();
      	map.clear();
      	query.setEnabled(false);
      	find.setEnabled(false);
      	bNext.setEnabled(false);
      	info.addItem("Database Cleared!");
      } else if(command == QUERY) {
      	data.quering();
      } else if(command == FIND) {
      	map.findPoint(data.queryPoint,route.getState());
      } else if(command == NEXT ) {
      	data.drawTrapezoids(speed.getState());
      	if(map.insertNextSegment() == true) {
      		bNext.setEnabled(false);
      	}
      } else {}
	}
}