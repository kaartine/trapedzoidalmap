/****************************************************
 * Data structure for the node.
 * It is the node of the search structure
 * 
 * 
 *	This file is written by Jukka Kaartinen
 * jukka.kaartinen@tut.fi
 *************************************************/

class Node {
	String node = "";
	
	public Vertice p = null;
	public LSegment segment = null;
	
	public Trapezoid t = null;	// Trapezoid these we are looking for	
	public Node left = null;	// left or above
	public Node right = null;	// right or below
		
	public boolean isLeft(Vertice q) {
		if(q.x < p.x) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isAbove(Vertice q) {
		int above = Calculate.ccw(segment.left, segment.right, q);
		if(above <= 0) { 	// if q is
			return true;
		} else {
			return false;
		}
	}
}

