/****************************************************
 * class Line Segment this is data structure that 
 * contains one segment
 *
 *	This file is written by Jukka Kaartinen
 * jukka.kaartinen@tut.fi
 *************************************************/

class LSegment {
	public Vertice left = null;
	public Vertice right = null;

	public LSegment(Vertice s, Vertice e) {	
	// Determine wich of the vertices is on the left size
		if(s.x < e.x) {
			left = s;
			right = e;
		} else if(s.x > e.x)	{
			left = e;
			right = s;
		} else {
			if(s.y < e.y) {
				left = s;
				right = e;
			} else {
				left = e;
				right = s;
			}
		}				
	}

	public boolean isAbove(Vertice q) {
		int above = Calculate.ccw(left,right,q);
		if(above <= 0) { 			
			return true;
		} else {
			return false;
		}
	}
		
	// Return pointers
	public Vertice startp() {
		return left;
	}

	public Vertice endp() {
		return right;
	}
	
	// Add pointers
	public void addStartp(Vertice v) {
		right = v;			
	}
	
	public void addEndp(Vertice v) {
		left = v;	
	}
}
