/****************************************************
 * Data structure to the trapezoid
 * 
 * 
 *	This file is written by Jukka Kaartinen
 * jukka.kaartinen@tut.fi
 *************************************************/

class Trapezoid {

	public Trapezoid next = null;
	public Trapezoid up = null;	// New upper trapezoid
	public Trapezoid down = null;	// New lower trapezoid
	public Trapezoid end = null;	// New end trapezoid
		
	public LSegment top = null, bottom = null;
	public Vertice left = null, right = null;
	public Node node = null;
		
	public Trapezoid lLeft = null;	// Lower left neighbor
	public Trapezoid uLeft = null;	// upper left neighbor
	public Trapezoid lRight = null;	// lower right neighbor
	public Trapezoid uRight = null;	// upper right neighbor
	
	public String name;		// Name of the trapezoid
	
	Trapezoid() {
		next = null;		
		top = null; bottom = null;
		left = null; right = null;
	}
	
	Trapezoid(LSegment t, LSegment b, Vertice l, Vertice r) {
		top = t; bottom = b;
		left = l; right = r;
	}
	
	public LSegment top() {
		return top;
	}

	public LSegment bottom() {
		return bottom;
	}

	public Vertice leftp() {
		return left;
	}	
	
	public Vertice rightp() {
		return right;
	}
	
	public void addTop(LSegment t) {
		top = t;
	}

	public void bottom(LSegment t) {
		bottom = t;
	}

	public void leftp(Vertice t) {
		left = t;
	}	
	
	public void rightp(Vertice t) {
		right = t;
	}	
}
