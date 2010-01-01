/****************************************************
 * Data structure for the vertice
 *
 *	This file is written by Jukka Kaartinen
 * jukka.kaartinen@tut.fi
 *************************************************/

class Vertice {
	public int x = 0;
	public int y = 0;
	public int index;
	public int up = 0;	// How far we have to draw the line up from the vertex
	public int down = 0; // How far we have to draw the line down from the vertex
	
	
	public Vertice() {}
		
	public Vertice(int X, int Y, int i) {
		x = X;
		y = Y;
		index = i;
	}
	
	// True if this lies to the right of p
	public boolean liesToRightOf(Vertice p) {
		if(x > p.x) {
			return true;
		} else {
			return false;
		} 		
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public void addX(int c) {
		x = c;
	}

	public void addY(int c) {
		y = c;
	}
	
	public int getIndex() {
		return index;
	}		
}
