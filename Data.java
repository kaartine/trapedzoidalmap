/****************************************************
 * This is the panel that takes the points,
 * stores them, check if they are legal and
 * draws them to screen
 * 
 *	This file is written by Jukka Kaartinen
 * jukka.kaartinen@tut.fi
 *************************************************/

import java.awt.*;
import java.awt.event.*;

class Data extends Canvas implements MouseListener {

	static final String BUILDING = "building";
	static final String FASTBUILD = "fastbuild";
	static final String QUERY = "query";
	static final String INSERTING = "inserting";
	
	private String state = INSERTING;	// The state of our program look above
	
   private static final int RADIUS = 5;
   private int MAX = 100;
   public int numVertices = 0;
   public int numSegments = 0;
	private int counter = 0;	// Tells when we insert a segment
	private int inserted = -1;	// # of inserted segments
   
	public boolean fast = true;		// speed of darwing the trapezoids while
												// making the map 
   
   public LSegment[] segments = new LSegment[MAX];
   public LSegment[] randSeg = new LSegment[MAX];
   
   public Vertice[] vertices = new Vertice[MAX];
   public Vertice queryPoint = new Vertice();
   private Vertice tmpVerticeS = null;		// Start of Segment
	private Vertice tmpVerticeE = null;		// End of Segment
	private Vertice qPoint = new Vertice(0,0,-2);	// Query point
	
	private TrPolygon trapezoid = null;	// Trapezoid that is intersected
   
   private Component parent = null, object = null;

   Graphics g = null;
   
   Data(Component parent) {
   	this.parent = parent;
   	setSize(500,0);
   	addMouseListener(this);
   }
 
	public void clear() {
		// This realy doesn't clear all the data it just makes it
		// disapear! SWEEET!!!
		numVertices = 0;
		numSegments = 0;
		counter = 0;
		inserted = -1;
		state = INSERTING;
		repaint();
	}
	
	public void noMoreSegments() {
		repaint();
	}

	public void setTrapezoid(TrPolygon tr) {
		trapezoid = tr;
	}

	// Draws when building in slow motion
   public void draw() {
		// draw a black border and a white background
		Dimension d = getSize();
   	g.setColor(Color.white);
      g.fillRect(0, 0, d.width - 1, d.height - 1);
      g.setColor(Color.red);
      g.drawRect(0, 0, d.width - 1, d.height - 1);

      if(state == BUILDING) {
      	if(trapezoid != null) {
      		g.setColor(Color.yellow);
      		g.fillPolygon(trapezoid.x,trapezoid.y,4);
      	}
      	trapezoid = null;
      }
      
      LSegment tmpSeg = null;
      Vertice verticel = null;
      Vertice verticer = null;
      
		for(int i=0; i < numSegments; i++) {
      	if(state == INSERTING) {
      		if(i == (numSegments - 1))	{// Draw last one on black
      			g.setColor(Color.black);
      		} else {
      			g.setColor(Color.green);	// others with green
      		}
      	} else if(state == QUERY) {
      		if(i > inserted) {
      			g.setColor(Color.green);
      		} else {
      			g.setColor(Color.black);
      		}
      	} else if(state == FASTBUILD) {
      		g.setColor(Color.black);
      	} else if(state == BUILDING) {
      		if(i < (inserted)) {	// Draw the inserted ones on black
      			g.setColor(Color.black);
      		} else if(i == (inserted))	{ // Draw inserted one on cyan
      			g.setColor(Color.cyan);
      		} else if(i == (inserted + 1) ) {
      			g.setColor(Color.red);	
      		} else {
      			g.setColor(Color.green);	// others with green
      		}
      	}
      	
      	tmpSeg = segments[i];
      	
			if (tmpSeg != null) {
      	  	g.drawLine(tmpSeg.startp().getX(), tmpSeg.startp().getY(), 
      	  					tmpSeg.endp().getX(), tmpSeg.endp().getY());
	      	verticel = tmpSeg.left;
   	  		verticer = tmpSeg.right;    		
        	
	     		if((state != INSERTING && ( (i <= inserted && i >= 0) &&
	     										(state == BUILDING || state == FASTBUILD || 
	     										state == QUERY))) &&
	     					 verticel != null && verticer != null) {
   	  			g.setColor(Color.blue);
     				g.drawLine(verticel.getX(), verticel.up, 
     								verticel.getX(), verticel.down);
     				g.drawLine(verticer.getX(), verticer.up, 
     								verticer.getX(), verticer.down);      		
     			}
     		}
     	}
     	
     	for(int i=0; i < numVertices; i++) {
      	if (vertices[i] != null) {
      		g.setColor(Color.red);
           	g.fillOval(vertices[i].getX() - RADIUS, 
           					vertices[i].getY() - RADIUS, RADIUS * 2, RADIUS * 2);
        	}        	   
      }
     	 
      if(state == QUERY) {
      	g.setColor(Color.blue);
         g.fillOval(queryPoint.getX() - RADIUS, 
         				queryPoint.getY() - RADIUS, RADIUS * 2, RADIUS * 2);      	
      }
      
      if(numVertices == MAX) {
      	g.setColor(Color.black);
			g.drawString("Not enough memory to add moro points! Max: " + 
								+ numVertices, 0, 15 );
      }
	}
	
	public void update(Graphics g){
		// override update to avoid flickering
		paint(g);
	}
	
   /**
     * Puts a little breathing space between
     * the panel and its contents, which lets us draw a box
     * in the paint() method.
     * We add more pixels to the right, to work around a
     * Choice bug.
     */
   public Insets getInsets() {
   	return new Insets(5,5,5,5);
   }
     
   // Call this when starting to build the map
   public void drawTrapezoids(boolean s) {
   	fast = s;
   	inserted = -1;
   }
   
   public void drawMap(LSegment[] s, boolean next) {
   	if(fast && !next)
   		state = FASTBUILD;
   	else
   		state = BUILDING;   		
   	randSeg = s;
   }
   
   // Set the number of inserted segments
   public void setInserted(int si) {
   	inserted = si;
   }
   
   public void paint(Graphics gr) {
   	g = getGraphics();
   	draw();
   	
   	if(!fast && object != null) {
			synchronized ( object ) {
      		object.notifyAll();
      	}
      }
   }
   
   public void setObject(Component object) {
   	this.object = object;
   }
   
   public void mousePressed(MouseEvent event) {
   	if(state == INSERTING && numVertices < MAX) {
     		if(numVertices == 0) {
    			Vertice vertice = new Vertice(event.getX(),event.getY(),
    													numVertices);
    			vertices[numVertices] = vertice;
    			tmpVerticeS = vertice;
    			numVertices++;
    			counter=1;
    		} else if(numVertices < MAX) {
	    		boolean foundV = false;			// found vertice
    			boolean foundS = false;			// found segment
    			boolean intersection = false;	// found intersection
    			Vertice tmpVer = null;
    			int x = event.getX();
				int y = event.getY();
    		
    			for(int i = 0; i < numVertices; i++) {	
    			// Find if vertice already exists
	    			if(vertices[i].getX() < x+6 && vertices[i].getX() > x-6 && 
   	 			  vertices[i].getY() < y+6 && vertices[i].getY() > y-6) {
    					tmpVer = vertices[i];
    					foundV = true;
    					break;
    				}   
    			}    		
    			if(!foundV)	{// Didn't found vertice so make new vertice
    				Vertice vertice = new Vertice(x, y, numVertices);
   				tmpVer = vertice;
	  			}
    		
    			if(counter == 1) { 	
    			// End of segment
	    			tmpVerticeE = tmpVer;
    				counter=2;
    			
	    			if(tmpVerticeS.getIndex() == tmpVerticeE.getIndex()) {	
   	 			// Select point if it is pressed twise
    					tmpVerticeS = tmpVer;
    					counter=1;
	    			}
   	 		} else if(counter == 0)	{	
   	 		// Start of segment
    				tmpVerticeS = tmpVer;
    				counter=1;
    			} 
    		    		   		
    			// Using counter so we can only insert segments
    			if(counter == 2) {    			
    				foundS = false;	
    				for( int i = 0; i < numSegments; i++) {
    					if((segments[i].startp().getIndex()==tmpVerticeS.getIndex()||
    						 segments[i].startp().getIndex()==tmpVerticeE.getIndex())&&
    						(segments[i].endp().getIndex() == tmpVerticeS.getIndex()|| 
    						 segments[i].endp().getIndex() == tmpVerticeE.getIndex()))
    					{	// Inserted segment is already in our data structure
    						foundS = true;
    						break;
    					}
    				}
    				// Segment wasn't in our data structure so make new segment
    				if(!foundS) {
    					LSegment segment = new LSegment(tmpVerticeS, tmpVerticeE);
    					for(int j = 0; intersection == false && j < numSegments; j++) {
    						intersection = Calculate.non_crossing(segment, segments[j]);
    						if(intersection) {	
    						// If our new line starts att the same vertice then it is not intersection
    							if( (segment.startp().getIndex() == segments[j].startp().getIndex()) ||
    								(segment.startp().getIndex() == segments[j].endp().getIndex()) ||
    								(segment.endp().getIndex() == segments[j].endp().getIndex()) ||
    								(segment.endp().getIndex() == segments[j].startp().getIndex())) {
    								intersection = false;
    							} 
    						}
    					}
    					if(!intersection)	{	
    					// Add the new segment to array
	    					segments[numSegments] = segment;
   	 					numSegments++;
    					}
    				}
    				counter = 0;
    			}
    			if(!foundV && !intersection) {	
    			// Add the new vertice to the array
    				vertices[numVertices] = tmpVer;
    				numVertices++;
    			}    		
      	}
    	} else if(state == QUERY) {
    		queryPoint.x = event.getX();
			queryPoint.y = event.getY();
    	}
    	repaint();
   }
   
  	public void quering() {
    	state = QUERY;
   	repaint();
   }
   
   public void mouseClicked(MouseEvent event) {}
   public void mouseReleased(MouseEvent event) {}
   public void mouseEntered(MouseEvent event) {}
   public void mouseExited(MouseEvent event) {}
}