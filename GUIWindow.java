/****************************************************
 * This is the main program!
 *  Input: Lines that do not intersect
 * Output: Trapezoidal map and its Search structure
 *
 *	This file is written by Jukka Kaartinen
 * jukka.kaartinen@tut.fi
 *************************************************/

import java.awt.*;
import java.awt.event.*;
import java.applet.*;

public class GUIWindow extends Applet {
	
	TrapezoidalPanel panel = null;
	Data data = null;
	TRMap map = null;
	InfoBox info = null;
	
	public void start() {}
	
	public void init() {	
		
		//Use a GridLayout with 2 rows, as many columns as necessary,
      //and 5 pixels of padding around all edges of each cell.
      
//		Panel leftPanel = new Panel();
//		Panel rightPanel = new Panel();
//		Panel infoPanel = new Panel();
				
      setLayout(new BorderLayout());
      setSize(610,600);
      
      info = new InfoBox();
     	data = new Data(this);
     	map = new TRMap(data,info,this);
     			     	     
   	panel = new TrapezoidalPanel(data,map,info,this);
 
 		
   	//Add things at the left size of the window.
 //  	leftPanel.setLayout(new BorderLayout());//new GridLayout(1,1,1,1));
	//	leftPanel.add(data);
      //add(leftPanel, BorderLayout.WEST);
      add(data, BorderLayout.WEST);
               	 	
   	//Add things to the right size of the window.
      //rightPanel.setLayout(new GridLayout(1,1,1,1));
   	//rightPanel.add(panel);
   	//add(rightPanel, BorderLayout.EAST);  	
   	add(panel, BorderLayout.EAST);  	
		
		//Add things to the bottom of the window.
      //infoPanel.setLayout(new GridLayout(1,1,1,1));
   	//infoPanel.add(info);
   	//add(infoPanel, BorderLayout.SOUTH);
   	add(info, BorderLayout.SOUTH);
 
		add(map);  	
   	info.addItem("Insert segments!");
   	info.addItem("When you are ready press 'Build Map'!\n\n\n");   	
	}
	
	/** Draws a box around this panel. */
   public void paint(Graphics g) {
   	super.paint(g);	  	
   	
		Dimension d = getSize();
		g.setColor(Color.white);
      g.fillRect(0, 0, d.width - 1, d.height - 1);
   	g.setColor(Color.green);
   	g.drawRect(0,0, d.width - 1, d.height - 1);

   }
   
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
   	return new Insets(5,5,5,5);
	}
    
   public static void main(String[] args) {
 	
       //Create a new window.
		Frame f = new Frame("Trapezoidal Map");
		f.addWindowListener(new WindowAdapter() {
      	public void windowClosing(WindowEvent e) {
	      	System.exit(0);
         }
      });

      //Create a GUIWindow instance.
     	GUIWindow window = new GUIWindow();
     	// Initialize instance
		window.init();
     
     	//Add the Converter to the window and display the window.
		f.add("Center", window);
      f.pack();        //Resizes the window to its natural size.
      f.setVisible(true);    
	}
}

