/****************************************************
 * This class is the algorith called Trapezoidal Map
 * It will take O(n log n) to build it,
 * the expected size of the search structure is O(n)
 * and expected query time is O(log n)
 * 
 *	This file is written by Jukka Kaartinen
 * jukka.kaartinen@tut.fi
 *
 * The algorithm is taken from the book:
 * Computational Geometry Algorithms and Aplications
 * second edition
 *
 * and it is written by: 
 * M. de Berg, M. van Kreveld, M. Overmars and 
 * O. Schwarzkopf
 *
 *
 *  Input:	A set S of n non-crossing line segments
 * Output:	The trapezoidal map T(S) and a search
 *			  	structure D for T(S) in a bounding box.
 *************************************************/

import java.awt.*;
import java.awt.Component;
import java.util.Random;

class TRMap extends Panel {
	InfoBox infoBox = null;
	static final String xNode = "x";
	static final String yNode = "y";
	static final String trNode = "t";
	static final boolean up = true;
	static final boolean down = false;
	
	private boolean fast = true;
	private int numTrapezoids = 0;
		
	private Node D = null;
	private Trapezoid T = null;
	private Data data = null;
		
	private LSegment[] S = null;
	private int numSegments = -1;
	private int lastSegment = -1;	// How many segments are inserted manually
	
	private int maxX = 0, maxY = 0, minX= 10000, minY = 10000;
	private Component parent, object = null;
	private Graphics gr = null;
		
	public TRMap(Data d, InfoBox i, Component p) {
		this.parent = p;
		data = d;
		infoBox = i;
	}
			
	public boolean makeMap(LSegment[] s, int ns, boolean randomize, boolean fast) {
		clear();
		parent.repaint();
		this.fast = fast;
		if(ns > 0) {
			numSegments = ns;		
			Trapezoid dToRemove = null; // trapezois that will be removed
		
			// 1.	Determine a bounding box R that contains all segments of S,
			// 	and initialize the trapezoidal map structure T and search
			// 	structure D for it.
			determineBox(s, numSegments);
			Vertice x1,x2,x3,x4;//,x5,x6;
			x1 = new Vertice(minX - 10,minY - 10,-1);
			x2 = new Vertice(maxX + 10,minY - 10,-1);
			x3 = new Vertice(minX - 10,maxY + 10,-1);
			x4 = new Vertice(maxX + 10,maxY + 10,-1);			
		
			LSegment s1 = new LSegment(x1, x2);
			LSegment s2 = new LSegment(x3, x4);
		
			T = new Trapezoid();
			T.name = "root";	// Give name to the root trapezoid
			T.top = s1;
			T.bottom = s2;
			T.left = x3;
			T.right = x2;
							
			T.lRight = null;	// lower right neighbor
			T.uRight = null;	// upper right neighbor
				
			T.lLeft = null;	// Lower left neighbor
			T.uLeft = null;	// upper left neighbor
			
			D = new Node();
			D.node = trNode;
			D.t = T;
			T.node = D;	// Pointer to node
			numTrapezoids++;
				
			// 2. Compute a random permutation s1,s2,...,sn of the elements of S.
			if(randomize) {
				infoBox.addItem("Randomizing insertion order!");
				S = randPerm(s,numSegments);
			} else {
				infoBox.addItem("Using insertion order!");
				S = s;
			}
			insertSegments(S, numSegments);
			infoBox.addItem("All " + (numSegments) + " segments inserted!");
		}		
		return true;
	}
	
	public boolean makeMapNext(LSegment[] s, int ns, boolean randomize) {
		clear();		
		if(ns > 0) {
			this.fast = false;
			//lastSegment=0;
			numSegments = ns;		
			Trapezoid dToRemove = null; // trapezois that will be removed
		
			// 1.	Determine a bounding box R that contains all segments of S,
			// 	and initialize the trapezoidal map structure T and search
			// 	structure D for it.
			determineBox(s, numSegments);
			Vertice x1,x2,x3,x4;//,x5,x6;
			x1 = new Vertice(minX - 10,minY - 10,-1);
			x2 = new Vertice(maxX + 10,minY - 10,-1);
			x3 = new Vertice(minX - 10,maxY + 10,-1);
			x4 = new Vertice(maxX + 10,maxY + 10,-1);			
		
			LSegment s1 = new LSegment(x1, x2);
			LSegment s2 = new LSegment(x3, x4);
		
			T = new Trapezoid();
			T.name = "root";	// Give name to the root trapezoid
			T.top = s1;
			T.bottom = s2;
			T.left = x3;
			T.right = x2;
							
			T.lRight = null;	// lower right neighbor
			T.uRight = null;	// upper right neighbor
				
			T.lLeft = null;	// Lower left neighbor
			T.uLeft = null;	// upper left neighbor
			
			D = new Node();
			D.node = trNode;
			D.t = T;
			T.node = D;	// Pointer to node
			numTrapezoids++;
				
			// 2. Compute a random permutation s1,s2,...,sn of the elements of S.
			if(randomize) {
				infoBox.addItem("Randomizing insertion order!");
				S = randPerm(s,numSegments);
			} else {
				infoBox.addItem("Using insertion order!");
				S = s;
			}
			
			infoBox.addItem("Trapezoidal map is now properly prepared!");
			infoBox.addItem("Press Next to add more segments or do a" +
								 " point location query!");
			
			data.setInserted(-1);
			draw(null);
		}		
		return true;
	}
	
	private void insertSegments(LSegment[] s, int ns) {
		Trapezoid dToRemove;
		LSegment si = null;
		data.drawMap(s,false);
		for(int i = 0; i < ns; i++) {
			data.setInserted(i);
			
			si = s[i];
		// 4.	Find the set d0,d1,...,dk of trapezoids in T properly 
		// intersected by si.
			dToRemove = followSegment(si);
		//	5.	Remove d0,d1,...dk from T and replace them by the 
		// new trapezoids that appear because of the insertion of si.
		
			Trapezoid tr = updateT(dToRemove,si);
		//	6.	Remove the leaves for d0,d1,...,dk from D, and create leaves for
		//		the new trapezoids. Link the new leaves to the existing inner nodes
		//		by adding some new inner nodes.
			updateD(dToRemove,si,tr);
		}
	}
	
	public boolean insertNextSegment() {
		lastSegment++;
		data.drawMap(S, true);
		
		if(lastSegment < numSegments) {
			Trapezoid dToRemove;
			LSegment si = S[lastSegment];
		
			data.setInserted(lastSegment);
		
			// 4.	Find the set d0,d1,...,dk of trapezoids in T properly 
			// intersected by si.
			dToRemove = followSegment(si);
			//	5.	Remove d0,d1,...dk from T and replace them by the new 
			//		trapezoids that appear because of the insertion of si.
		
			Trapezoid tr = updateT(dToRemove,si);
			//	6.	Remove the leaves for d0,d1,...,dk from D, and create leaves for
			//		the new trapezoids. Link the new leaves to the existing inner 
			//		nodes by adding some new inner nodes.
			updateD(dToRemove,si,tr);
			draw(null);
			if(lastSegment == numSegments - 1) {
				infoBox.addItem("All " + (lastSegment + 1) + " segments inserted!");
				return true;
			} else {
				infoBox.addItem("Number of inserted segments " + (lastSegment + 1));
				return false;
			}
		}
		return true;
	}
	
	private void draw(Trapezoid tr) {
		if(!(data.fast)) {

			data.setTrapezoid(Calculate.polygon(tr));
			
			data.setObject(this);			
			
			gr = getGraphics();
			// gr can be null if no graphics devices available, 
			// but this will not happen
			if ( gr != null ) {
				data.paint(gr);
				gr.dispose(); // we must dispose the graphics we obtained 
									// with getGraphics
			}
			
			// jäädään odottamaan paintin suorittamista.
			synchronized ( this ) {                                                              
        		try { wait(1000);  // tuohon voi laitta jonkun ajan parametriksi
                // millisekunteina, vaatii try catchin ympärille
				} catch (Exception e) {}
				// jatketaan suorittamista
			}
		} else if(!fast) {
			gr = getGraphics();
			// gr can be null if no graphics devices available, 
			// but this will not happen
			if ( gr != null ) {
				data.paint(gr);
				gr.dispose(); // we must dispose the graphics we 
									// obtained with getGraphics
			}
		} else {
			//data.repaint();
		}
	}
	
	public void paint(Graphics g) {}
	public void update(Graphics g) {
		paint(g);
	}
	
	//	5.	Remove d0,d1,...dk from T and replace them by the new trapezoids that
	//		appear because of the insertion of si.
	private Trapezoid updateT(Trapezoid d, LSegment si) {
		// Simple case where si is completely contained in a trapezoid d 
		// figure 6.7 in the book
		int numNewTr = 0;
		Trapezoid di = d;		
		
		// Only one trapezoid is intersected
		if(di.next == null) {			
			// update T
			Trapezoid A = null;
			Trapezoid B = null;
			Trapezoid C = new Trapezoid();
			Trapezoid D = new Trapezoid();
			numNewTr = 2;
			
			C.left = si.left;
			C.right = si.right;
			C.bottom = si;
			C.top = di.top;
											
			C.name = "C" + numTrapezoids;
			numTrapezoids++;
												
			D.top = si;	
			D.bottom = di.bottom;
			D.right = si.right;
			D.left = si.left;
			
			D.name = "D" + numTrapezoids;
			numTrapezoids++;
			
			if(si.left != di.left) {	// If inserted segment doesn't have
				A = new Trapezoid();		// same left vertex as some other then
				A.top = di.top;			// make new trapezoid
				A.bottom = di.bottom;
				A.left = di.left;
				A.right = si.left;
				
				A.lLeft = di.lLeft;	// Lower left neighbor
				A.uLeft = di.uLeft;	// upper left neighbor
				
				A.lRight = D;	// lower right neighbor
				A.uRight = C;	// upper right neighbor
				A.node = di.node;	// Pointer to the corresponding node in the search
									// structure
				
				A.name = "A" + numTrapezoids;
				numTrapezoids++;
				
				upDateLeftNeighbors(A,di);
				
				C.lLeft = null;	// Lower left neighbor
				C.uLeft = A;		// upper left neighbor
				D.lLeft = A;		// Lower left neighbor
				D.uLeft = null;	// upper left neighbor				
				numNewTr++;
				
				// Calculate the up and down extencion
				Calculate.verticalLine(A,null,si);
			} else {				
				// si:in oikeareuna ylempana kuin di:in oikeareuna
				// lower new trapezoid doesn't have left neighbors
				C.uLeft = di.uLeft;		// upper left neighbor
				C.lLeft = null;
				D.lLeft = di.lLeft;		// Lower left neighbor
				D.uLeft = null;
				
				if(di.uLeft != null) {
					di.uLeft.uRight = C;
				}
				if(di.lLeft != null) {
					di.lLeft.lRight = D;
				}				
			}
			if(si.right != di.right) {
				B = new Trapezoid();
				B.top = di.top;
				B.bottom = di.bottom;
				B.left = si.right;
				B.right = di.right;
							
				B.lRight = di.lRight;	// lower right neighbor
				B.uRight = di.uRight;	// upper right neighbor
				
				B.lLeft = D;	// Lower left neighbor
				B.uLeft = C;	// upper left neighbor				
				B.node = di.node;
				
				B.name = "B" + numTrapezoids;
				numTrapezoids++;
				
				upDateRightNeighbors(B,di);
				
				C.lRight = null;	// lower right neighbor
				C.uRight = B;	// upper right neighbor
				D.lRight = B;	// lower right neighbor
				D.uRight = null;	// upper right neighbor	
				numNewTr++;
				
				// Calculate the up and down extencion
				Calculate.verticalLine(null,B,si);
				
			} else {
				// lower new trapezoid doesn't have left neighbors
				C.uRight = di.uRight;		// upper left neighbor
				C.lRight = null;
				D.lRight = di.lRight;				// Lower left neighbor
				D.uRight = null;
					
				if(di.uRight != null) {
					di.uRight.uLeft = C;
				}
				if(di.lRight != null) {
					di.lRight.lLeft = D;
				}
			}
			
			D.node = di.node;	// Pointer to the corresponding node in the search
									// structure
			C.node = di.node;	// Pointer to the corresponding node in the search
									// structure
						
			// Put new trapezoids into line
			C.next = D;
			D.next = null;
			
			draw(di);
						
			if(A != null) {
				if(B != null) {
					A.next = B;
					B.next = C;
				} else {
					A.next = C;
				}				
				return A;
			} else {
				if(B != null) {
					B.next = C;					
					return B;
				} else {
					return C;
				}
			}
		} else { // More than one trapezoid is intersected
			Trapezoid TrsInLine = null;
			Trapezoid lastUpper = null, lastLower = null;
			Trapezoid A = null;
			Trapezoid B = null;
			Trapezoid C = null;
			
			// first intersected trapezoid						
			if(si.left != di.left) {
				A = new Trapezoid();
				B = new Trapezoid();
				C = new Trapezoid();
				numNewTr = numNewTr + 3;
				
				A.top = di.top;
				A.bottom = di.bottom;
				A.left = di.left;
				A.right = si.left;
				
				A.lLeft = di.lLeft;	// Lower left neighbor
				A.uLeft = di.uLeft;	// upper left neighbor
								
				A.lRight = C;	// lower right neighbor
				A.uRight = B;	// upper right neighbor
				A.node = di.node;	// Pointer to the corresponding node in the search
									// structure
				
				A.name = "A" + numTrapezoids;
				numTrapezoids++;
				
				upDateLeftNeighbors(A,di);
				
				B.top = di.top;
				B.bottom = si;
				B.left = si.left;
							
				B.lLeft = null;	// Lower left neighbor
				B.uLeft = A;	// upper left neighbor
				//B.uRight = di.uRight;
				
				B.name = "B" + numTrapezoids;
				numTrapezoids++;
			
				C.top = si;
				C.bottom = di.bottom;
				C.left = si.left;
											
				C.lLeft = A;	// Lower left neighbor
				C.uLeft = null;	// upper left neighbor
				//C.lRight = di.lRight;
				
				C.name = "C" + numTrapezoids;
				numTrapezoids++;			
			} else {
				B = new Trapezoid();
				C = new Trapezoid();
				numNewTr = numNewTr + 2;
				
				B.top = di.top;
				B.bottom = si;
				B.left = di.left;
				B.name = "B" + numTrapezoids;
				numTrapezoids++;
				
				B.uLeft = di.uLeft;		// upper left neighbor
				B.lLeft = null;
				C.lLeft = di.lLeft;		// Lower left neighbor
				C.uLeft = null;
					
				if(di.uLeft != null) {
					di.uLeft.uRight = B;
				}
				if(di.lLeft != null) {
					di.lLeft.lRight = C;
				}		
				
				C.top = si;
				C.bottom = di.bottom;
				C.left = di.left;
								
				C.name = "C" + numTrapezoids;
				numTrapezoids++;
			}
			
			B.uRight = di.uRight;		// upper left neighbor
			B.lRight = null;
			C.lRight = di.lRight;	 	// Lower left neighbor
			C.uRight = null;
					
			if(di.uRight != null) {
				di.uRight.uLeft = B;
			}
			if(di.lRight != null) {
				di.lRight.lLeft = C;
			}
				
			B.node = di.node;	// Pointer to the corresponding node in the search
									// structure
			C.node = di.node;	// Pointer to the corresponding node in the search
									// structure
			
			di.end = A;
			di.up = B;
			di.down = C;

			draw(di);
			di = di.next;
			
			// Trapezoids intersected between 1-(k-1)
			lastUpper = B; lastLower = C;			

			Trapezoid newT = null;
			while(di.next != null) {
				newT = new Trapezoid();
				newT.name = "U" + numTrapezoids;				
				numTrapezoids++;
				
				numNewTr++;
				
				if(Calculate.isAbove(si.left,si.right,di.left)) {
					// Merg lower and make new upper
					lastUpper.right = di.left;
										
					newT.top = di.top;
					newT.bottom = si;
					
					newT.left = di.left;
				
					newT.lLeft = lastUpper;	// Lower left neighbor
					newT.uLeft = di.uLeft;	// upper left neighbor
					newT.uRight = di.uRight;
				
					lastUpper.lRight = newT;	// lower right neighbor
					
					lastUpper = newT;
					
					if(di.uLeft != null) {
						di.uLeft.uRight = newT;
					}									
					if(di.uRight != null) {
						di.uRight.uLeft = newT;
					}	
					
					// Shorten down line
					Calculate.shortenLine(di,si,down);
					
					lastLower.lRight = di.lRight;
					lastLower.node = di.node;	// Pointer to the corresponding node 
														// in the search structure
				} else {
					// Merge upper and make new lower
					lastLower.right = di.left;
					
					newT.top = si;
					newT.bottom = di.bottom;
					
					newT.left = di.left;
				
					newT.lLeft = di.lLeft;	// Lower left neighbor
					newT.uLeft = lastLower;	// upper left neighbor
					newT.lRight = di.lRight;
				
					lastLower.uRight = newT;	// upper right neighbor
					
					lastLower = newT;
					
					if(di.lLeft != null) {
						di.lLeft.lRight = newT;
					}
					if(di.lRight != null) {
						di.lRight.lLeft = newT;
					}
					
					// Shorten down line
					Calculate.shortenLine(di,si,up);
					
					lastUpper.uRight = di.uRight;
					lastUpper.node = di.node;	// Pointer to the corresponding 
														// node in the search structure
				}
						
				newT.node = di.node;	// Pointer to the corresponding node in 
											// the search structure
								
				di.up = lastUpper;
				di.down = lastLower;
				
				draw(di);
				di = di.next;
			}
			
			// Last intersected trapezoid
			Trapezoid N = null;
			Trapezoid L = null;
			Trapezoid M = null;

			// Make new upper trapezoid and merge lower
			if(Calculate.isAbove(si.left,si.right,di.left) ) {
				M = lastLower;
				M.right = si.right;
				
				L = new Trapezoid();
				L.top = di.top;
				L.bottom = si;
				L.left = di.left;
				L.right = si.right;
				
				L.lLeft = lastUpper;	// Lower left neighbor
				L.uLeft = di.uLeft;	// upper left neighbor
				
				if(di.uLeft != null) {
					di.uLeft.uRight = L;
				}
								
				L.name = "L" + numTrapezoids;
				numTrapezoids++;
				
				lastUpper.lRight = L;
				//lastUpper.uRight = di.lLeft.uRight;
				lastUpper.right = di.left;
		
				// Shorten down line
				Calculate.shortenLine(di,si,down);
								
				L.node = di.node;				
			} else {
				// Make new lower trapezoid and merge upper
				L = lastUpper;
				L.right = si.right;
				
				M = new Trapezoid();
				M.top = si;
				M.bottom = di.bottom;
				M.left = di.left;
				M.right = si.right;
				
				M.lLeft = di.lLeft;	// Lower left neighbor
				M.uLeft = lastLower;	// upper left neighbor
				
				if(di.lLeft != null) {
					di.lLeft.lRight = M;
				}
				
				M.name = "M" + numTrapezoids;
				numTrapezoids++;
				
				//lastLower.lRight = di.uLeft.lRight;
				lastLower.uRight = M;
				lastLower.right = di.left;
				
				// Shorten down line
				Calculate.shortenLine(di,si,up);
				
				M.node = di.node;
			}
						
			if(si.right != di.right) {
				N = new Trapezoid();
				
				numNewTr++;
				
				N.top = di.top;			
				N.bottom = di.bottom;
				N.left = si.right;
				N.right = di.right;
				
				N.lLeft = M;	// Lower left neighbor
				N.uLeft = L;	// upper left neighbor
												
				N.lRight = di.lRight;	// lower right neighbor
				N.uRight = di.uRight;	// upper right neighbor
				
				N.name = "N" + numTrapezoids;
				numTrapezoids++;
				
				N.node = di.node;
				
				upDateRightNeighbors(N,di);
			
				L.lRight = null;	// Lower right neighbor
				L.uRight = N;		// upper right neighbor
				M.lRight = N;		// Lower right neighbor
				M.uRight = null;	// upper right neighbor	
			} else {
				// lower new trapezoid doesn't have left neighbors
				L.uRight = di.uRight;		// upper left neighbor
				L.lRight = null;
				M.lRight = di.lRight;				// Lower left neighbor
				M.uRight = null;
				
				if(di.uRight != null) {
					di.uRight.uLeft = L;
				}
				if(di.lRight != null) {
					di.lRight.lLeft = M;
				}
			}	
			
			di.up = L;
			di.down = M;
			di.end = N;
			
			// Calculate the up and down extencion
			// d is the first insected and di the last
			Trapezoid left = null, right=null;
			if(d.left != si.left)
				left = d;
			if(di.right != si.right)
				right = di;
				
			Calculate.verticalLine(left,right,si);
			draw(di);
			
			return TrsInLine;
		}
	}
	
	// di = trapezoids that will be removed
	// tdi = new added trapezoids if only one trapezoid was intersected
	private void updateD(Trapezoid di, LSegment si, Trapezoid tdi) {
		if(di.next == null) {
			// update D
						
			Node node = tdi.node;//find(D, si.left, si.right);
			Trapezoid tmp = node.t;
			if(tmp.left != si.left) {	// Make new left end point
				node.node = xNode;
				node.p = si.left;
			
				node.t = null;
				node.left = new Node();
				node.left.node = trNode;
				node.left.t = tdi;
				node.left.t.node = node.left;
					
				node.right = new Node();
				node = node.right;
				tdi = tdi.next;	// To the next trapezoid
			}
				
			if(tmp.right != si.right) {	// make new right end point
				node.node = xNode;
				node.p = si.right;
				node.t = null;
				node.right = new Node();
				node.right.node = trNode;
				node.right.t = tdi;
				node.right.t.node = node.right;
					
				tdi = tdi.next;	// To the next trapezoid
					
				node.left = new Node();
				node.left.node = yNode;
				node.left.segment = si;
				node.left.t = null;
				node.left.left = new Node();
				node.left.left.node = trNode;
				node.left.left.t = tdi;
				node.left.left.t.node = node.left.left;
					
				tdi = tdi.next;	// To the next trapezoid
					
				node.left.right = new Node();
				node.left.right.node = trNode;
				node.left.right.t = tdi;
				node.left.right.t.node = node.left.right;
						
			} else {
				node.node = yNode;
				node.segment = si;
				node.t = null;
				node.left = new Node();
				node.left.node = trNode;
				node.left.t = tdi;
				node.left.t.node = node.left;
				tdi = tdi.next;	// To the next trapezoid
										
				node.right = new Node();
				node.right.node = trNode;
				node.right.t = tdi;
				node.right.t.node = node.right;
			}			
		} else {
			Trapezoid inserted = null;
			Trapezoid tmpInserted = null;
			Node node = di.node;
			if(di.left != si.left) {
				node.node = xNode;
				node.p = si.left;
				node.t = null;
				
				node.left = new Node();
				node.left.node = trNode;
				node.left.t = di.end;
				node.left.t.node = node.left;
								
				node.right = new Node();
				node.right.node = yNode;
				node.right.segment = si;
				
				node.right.left = new Node(); 
				node.right.left.node = trNode;
				node.right.left.t = di.up;
				node.right.left.t.node = node.right.left;
				
				node.right.right = new Node();
				node.right.right.node = trNode;
				node.right.right.t = di.down;
				node.right.right.t.node = node.right.right;
				
				di.end.next = null;
				inserted = di.end;						
				di.up.next = inserted;
				inserted = di.up;
				di.down.next = inserted;
				inserted = di.down;
				
			} else {
				node.node = yNode;
				node.segment = si;
				node.t = null;
				
				node.left = new Node();
				node.left.node = trNode;
				node.left.t = di.up;
				node.left.t.node = node.left;
				
				node.right = new Node();
				node.right.node = trNode;
				node.right.t = di.down;
				node.right.t.node = node.right;
				
				di.up.next = null;				
				inserted = di.up;
				di.down.next = inserted;
				inserted = di.down;
			}
				
			di = di.next;
			
			boolean sameUp = false;
			boolean sameDown = false;
			
			while(di.next != null) {
				tmpInserted = inserted;
				
				node = di.node;				
				node.node = yNode;
				node.segment = si;
				node.t = null;
				
				while(tmpInserted != null) {
					if(tmpInserted == di.up)
						sameUp = true;
					if(tmpInserted == di.down)
						sameDown = true;
						
					tmpInserted = tmpInserted.next;
				}
				
				if(sameUp) {
					node.left = di.up.node;
				} else {
					node.left = new Node();
					node.left.node = trNode;
					node.left.t = di.up;
					node.left.t.node = node.left;
					
					di.up.next = inserted;				
					inserted = di.up;
				}
				
				if(sameDown) {
					node.right = di.down.node;
				} else {
					node.right = new Node();
					node.right.node = trNode;
					node.right.t = di.down;
					node.right.t.node = node.right;
					
					di.down.next = inserted;
					inserted = di.down;
				}
				
				di = di.next;
				
				sameDown = false;
				sameUp = false;					
			}
			
			node = di.node;
			
			tmpInserted = inserted;
			while(tmpInserted != null) {	// Check that trapezoid is not already 
				if(tmpInserted == di.up)	// inserted
					sameUp = true;
				if(tmpInserted == di.down)
					sameDown = true;
					
				tmpInserted = tmpInserted.next;
			}
			
			if(di.right != si.right) {
				node.node = xNode;
				node.p = si.right;
				node.t = null;
				
				node.right = new Node();
				node.right.node = trNode;
				node.right.t = di.end;
				node.right.t.node = node.right;
				
				node.left = new Node();
				node.left.node = yNode;
				node.left.segment = si;
				
				if(sameDown) {
					node.left.right = di.down.node;
				} else {
					node.left.right = new Node(); 
					node.left.right.node = trNode;
					node.left.right.t = di.down;
					node.left.right.t.node = node.left.right;
				}
				
				if(sameUp) {
					node.left.left = di.up.node;
				} else {
					node.left.left = new Node();
					node.left.left.node = trNode;
					node.left.left.t = di.up;
					node.left.left.t.node = node.left.left;
				}				
			} else {
				node.node = yNode;
				node.segment = si;
				node.t = null;
				
				if(sameDown) {
					node.right = di.down.node;
				} else {
					node.right = new Node();
					node.right.node = trNode;
					node.right.t = di.down;
					node.right.t.node = node.right;
				}
				
				if(sameUp) {
					node.left = di.up.node;
				} else {
					node.left = new Node();
					node.left.node = trNode;
					node.left.t = di.up;
					node.left.t.node = node.left;
				}
			}
		}
	}
	
	// returns trapezoid where q lies
	private Node find(Node root, Vertice s, Vertice e) {
		Node di = root;		
		while(true) {
			if(di.node == xNode) {
				if(di.isLeft(s)) {
					di = di.left;
				} else {
					di = di.right;
				}
			} else if(di.node == yNode) {
				// If above
				if( (Calculate.ccw(di.segment.left,di.segment.right,s) == -1) ||
					 ((Calculate.ccw(di.segment.left,di.segment.right,s) == 0) &&
					 (Calculate.ccw(di.segment.left,di.segment.right,e) == -1)) ) {
						di = di.left;
				} else {					
					di = di.right;
				}
			} else {
				//System.out.println("found: " + di.t.name);
				return di;
			}
		}
	}
	
	// Same as above but it uses colors to tell where the search is going.
	public boolean findPoint(Vertice q, boolean route) {
		Node di = D;
		while(true) {
			if(di.node == xNode) {
				if(di.isLeft(q)) {
					di = di.left;
					if(route)
						infoBox.addItem("Left <-- ");
				} else {
					di = di.right;
					if(route)
						infoBox.addItem("Right --> ");
				}
			} else if(di.node == yNode) {
				if(Calculate.isAbove(di.segment.left,di.segment.right,q)) {
					di = di.left;
					if(route)
						infoBox.addItem("Above /\\ ");
				} else {
					di = di.right;
					if(route)
						infoBox.addItem("Below \\/ ");
				}
			} else {
				infoBox.addItem("Found: " + di.t.name);// + " node: " + di.t.node);
				if(di.t.uLeft != null)
					infoBox.addItem("   uLeft: " + di.t.uLeft.name );
					//+ " : " +di.t.uLeft + " node: " + di.t.uLeft.node);
				if(di.t.lLeft != null)
					infoBox.addItem("   lLeft: " + di.t.lLeft.name );
					//+ " : " +di.t.lLeft + " node: " + di.t.lLeft.node);
				if(di.t.uRight != null)
					infoBox.addItem("   uRight: " + di.t.uRight.name );
					//+ " : " +di.t.uRight + " node: " + di.t.uRight.node);
				if(di.t.lRight != null)
					infoBox.addItem("   lRight: " + di.t.lRight.name );
					//+ " : " +di.t.lRight + " node: " + di.t.lRight.node);				
				return true;
			}				
		}
	}
	
	/*****************************************************************
	 *  Input:	a new segment si
	 * Output:	The sequence d0,...,dk of trapezoids intersected by si
	 *****************************************************************/
	private Trapezoid followSegment(LSegment si)	{
		// 1.	Let p and q be the left and right endpoint if si.
		Vertice p = si.startp();
		Vertice q = si.endp();
		
		// 2. Search with p in the search structure D to find d0.
		Node node = find(D, p, q);
		Trapezoid dj = node.t;
	
		Trapezoid tmpDj = dj;
		tmpDj.next = null;
		// 4. while(q lies to the right of rightp(dj)
		while( q.liesToRightOf( dj.rightp() ))	{												
		// 5.	do if rightp(dj) lies above si
			if(Calculate.isAbove(si.left,si.right,dj.rightp())) {
		//		6.	then Let d(j+1) be the lower right neigbor of dj					
				dj.next = dj.lRight;
			} else {	//		7. else Let d(j+1) be the upper right neighbor of dj					
				dj.next = dj.uRight;
			}
			dj = dj.next;
			if(dj == null)
				return tmpDj;
		}
		dj.next = null;
		//		9. return d0,...,dj		
		return tmpDj;
	}
	
	private void upDateLeftNeighbors(Trapezoid newT, Trapezoid oldT) {
		if(oldT.uLeft != null) {
			oldT.uLeft.uRight = newT;
		}
		if(oldT.lLeft != null) {
			oldT.lLeft.lRight = newT;
		}
	}
	
	private void upDateRightNeighbors(Trapezoid newT, Trapezoid oldT) {
		if(oldT.uRight != null) {
			oldT.uRight.uLeft = newT;
		}
		if(oldT.lRight != null) {
			oldT.lRight.lLeft = newT;
		}
	}
			
	private LSegment[] randPerm(LSegment[] s, int ns) {
		int random = 0;
		Random rand = new Random();
		LSegment tmp = null;
		for(int i = (numSegments - 1); i >= 2; i--) {
			random =(int)(Math.random()*ns);//nextInt(numSegments,rand);
			rand.setSeed((long) i*100);
			tmp = s[i];
			s[i] = s[random];
			s[random] = tmp;
		}
		return s;
	}
	
	private int nextInt(int n, Random rand) {		
     	if (n<=0)
			throw new IllegalArgumentException("n must be positive");
		int val, bits;
		do {		
			bits = rand.nextInt();
			val = bits % n;			
		} while( bits - val + (n-1) < 0);		
		return val;
	}
	
	private void determineBox(LSegment[] s, int ns) {		
		int x1,y1,x2,y2;
		for(int i = 0; i < ns; i++) {
			x1 = s[i].startp().x;
			y1 = s[i].startp().y;
			
			x2 = s[i].endp().x;
			y2 = s[i].endp().y;
			
			if(x1 < minX) minX = x1;
			if(x1 > maxX) maxX = x1;
			if(y1 < minY) minY = y1;
			if(y1 > maxY) maxY = y1;
			 	
			if(x2 < minX) minX = x2;
			if(x2 > maxX) maxX = x2;
			if(y2 < minY) minY = y2;
			if(y2 > maxY) maxY = y2;
		}
	}
	
	public void clear() {
		numTrapezoids = 0;
		lastSegment=-1;
		
		D = null;
		T = null;

		S = null;
		
		numSegments = -1;
		maxX = -10;
		maxY = -10; 
		minX = 10000;
		minY = 10000;
	}
}