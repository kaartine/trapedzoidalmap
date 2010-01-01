/****************************************************
 * This class is the data structure for the map and
 * its search structure.
 * 
 *	This file is written by Jukka Kaartinen
 * jukka.kaartinen@tut.fi
 *************************************************/

//Trapezoidal map T
//search structure D for T(S);

class TrapezoidalMap {
	static final String xNode = "x";
	static final String yNode = "y";
	static final String Trapezoid = "t";
	
	private int numTrapezoids = 0;
		
	private Node D = null;
	private Trapezoid T = null;
		
	private boolean empty = true;
	
	
	TrapezoidalMap(Trapezoid R) {
		T = R;
		D = new Node();
		D.node = "t";
		D.t = R;
		numTrapezoids++;
	}
	
	public void insertSegment(LSegment si) {
		insertToT(si);
		insertToD(si);	
	}
	
	public void insertSegments(LSegment[] s, int ns) {	
		Trapezoid dToRemove;
		LSegment si = null;
		for(int i = 0; i < ns; i++) {
			si = s[i];
		// 4.	Find the set d0,d1,...,dk of trapezoids in T properly intersected by si.
			dToRemove = followSegment(si);
		//	5.	Remove d0,d1,...dk from T and replace them by the new trapezoids that
		//		appear because of the insertion of si.
			Trapezoid tr = updateT(dToRemove,si);
		//	6.	Remove the leaves for d0,d1,...,dk from D, and create leaves for
		//		the new trapezoids. Link the new leaves to the existing inner nodes
		//		by adding some new inner nodes.
			//updateD(dToRemove,si,tr);			
			
			data.drawMap(s,i);
		}
	}
	
	//	5.	Remove d0,d1,...dk from T and replace them by the new trapezoids that
	//		appear because of the insertion of si.
	public Trapezoid updateT(Trapezoid d, LSegment si) {
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
									
			D.top = si;			
			D.bottom = di.bottom;
			D.right = si.right;
			D.left = si.left;
			
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
				//A.name = String(name);
				
				C.lLeft = null;	// Lower left neighbor
				C.uLeft = A;		// upper left neighbor
				D.lLeft = A;		// Lower left neighbor
				D.uLeft = null;	// upper left neighbor				
				numNewTr++;
			} else {
				C.lLeft = di.lLeft;	// Lower left neighbor
				C.uLeft = di.uLeft;		// upper left neighbor
				D.lLeft = di.lLeft;		// Lower left neighbor
				D.uLeft = di.uLeft;	// upper left neighbor				
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
				
				C.lRight = null;	// lower right neighbor
				C.uRight = B;	// upper right neighbor				
				D.lRight = B;	// lower right neighbor
				D.uRight = null;	// upper right neighbor				
				numNewTr++;
			} else {
				C.lRight = di.lRight;	// Lower left neighbor
				C.uRight = di.uRight;	// upper left neighbor
				D.lRight = di.lRight;	// Lower left neighbor
				D.uRight = di.uRight;	// upper left neighbor				
			}
		} else { // More than one trapezoid is intersected
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
				
				B.top = di.top;
				B.bottom = si;
				B.left = si.left;
				if(si.right.x > di.top.right.x)
					B.right = di.top.right;
				else
					B.right = si.right;
				//B.right = di.top.right;
			
				B.lLeft = null;	// Lower left neighbor
				B.uLeft = A;	// upper left neighbor
				
				C.top = si;
				C.bottom = di.bottom;
				C.left = si.left;
				if(si.right.x > di.bottom.right.x)
					C.right = di.bottom.right;
				else
					C.right = si.right;
				//C.right = di.bottom.right;
			
				C.lLeft = A;	// Lower left neighbor
				C.uLeft = null;	// upper left neighbor
			} else {
				B = new Trapezoid();
				C = new Trapezoid();
				numNewTr = numNewTr + 2;
				
				B.top = di.top;
				B.bottom = si;
				B.left = si.left;
				if(si.right.x > di.top.right.x)
					B.right = di.top.right;
				else
					B.right = si.right;
				//B.right = di.top.right;
			
				B.lLeft = null;	// Lower left neighbor
				B.uLeft = di.uLeft;	// upper left neighbor
				
				C.top = si;
				C.bottom = di.bottom;
				C.left = si.left;
				if(si.right.x > di.bottom.right.x)
					C.right = di.bottom.right;
				else
					C.right = si.right;
				//C.right = di.bottom.right;
			
				C.lLeft = di.lLeft;	// Lower left neighbor
				C.uLeft = null;	// upper left neighbor
			}
			
			di = di.next;			
			// Trapezoids intersected between 1-(k-1)
			lastUpper = B; lastLower = C;
			Trapezoid newT = null;
			while(di.next != null) {
				newT = new Trapezoid();
				numNewTr++;
				
				// Trapezoid has only lower or upper neighbor
				if( ( (di.top.left == di.left) && (di.top.right == di.right) ) ||
					 ( (di.bottom.left == di.left) && (di.bottom.right == di.right) ) ||
					 ( (di.bottom.left == di.left) && (di.top.right == di.right) ) || 
					 ( (di.bottom.right == di.right) && (di.top.left == di.left) ) ) {//di.uRight == null || di.lRight == null) {
					if(di.top.left.x > di.bottom.left.x) {
						newT.top = di.top;
						newT.bottom = si;
						newT.left = di.left;
						
						newT.lLeft = lastUpper;
						lastUpper.right = di.left;
						lastUpper.lRight = newT;
						lastUpper = newT;
					} else {
						newT.top = si;
						newT.bottom = di.bottom;
						newT.left = di.left;
						
						newT.uLeft = lastLower;
						lastLower.right = di.left;
						lastLower.uRight = newT;
						lastLower = newT;
					}
				} else { // Trapezoid has both lower and upper neighbors
					newT.left = di.left;
					newT.right = di.right;
					if(di.uLeft != null || di.uRight != null) {
						newT.top = di.top;
						newT.bottom = si;
												
						newT.uLeft = di.uLeft;
						newT.uRight = di.uRight;
						newT.lLeft = lastUpper;
						lastUpper.right = di.left;
						lastUpper.lRight = newT;
						lastUpper = newT;
					} else {
						newT.top = si;
						newT.bottom = di.bottom;
												
						newT.lLeft = di.lLeft;
						newT.lRight = di.lRight;
						newT.uLeft = lastLower;						
						lastLower.right = di.left;
						lastLower.uRight = newT;
						lastLower = newT;
					}
				}
				di = di.next;
			}
			
			// Last intersected trapezoid
			Trapezoid N = null;
			Trapezoid L = null;
			Trapezoid M = null;

			if(lastLower == C) {
				L = C;
				L.right = si.right;
			} else {
				L = new Trapezoid();
				numNewTr++;
				
				L.top = di.top;
				L.bottom = si;
				if(si.left.x < di.top.left.x)
					L.left = di.top.left;
				else
					L.left = si.left;				
				L.right = si.right;
				
				L.uLeft = null;
				L.lLeft = lastUpper;
			}
			
			if(lastUpper == B) {					
				M = B;
				M.right = si.right;
			} else {
				M = new Trapezoid();
				numNewTr++;
				
				M.top = si;
				M.bottom = di.bottom;
				if(si.left.x < di.bottom.left.x)
					M.left = di.bottom.left;
				else
					M.left = si.left;
				//M.left = di.bottom.left;
				M.right = si.right;
				M.uLeft = lastLower;
				M.lLeft = null;
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
			
				L.lRight = null;	// Lower left neighbor
				L.uRight = N;	// upper left neighbor
				M.lRight = N;	// Lower left neighbor
				M.uRight = null;	// upper left neighbor	
				
			} else {
				L.lRight = null;	// Lower left neighbor
				L.uRight = di.uRight;	// upper left neighbor
				
				M.lRight = di.lRight;	// Lower left neighbor
				M.uRight = null;	// upper left neighbor								
			}
		}
		Trapezoid v = null;
		return v;				
	}
	
	// Iserts node to the left or right size
	private void insertTrLR(Trapezoid newTrs, LSegment si, Node node, String lr) {
		if(lr == "left") {
			while(newTrs != null) {
				if(newTrs.right == si.left) {
					node.t = newTrs;
					newTrs.node = node;
					break;
				} else {
					newTrs = newTrs.next;
				}
			}
		} else {
			while(newTrs != null) {
				if(newTrs.left == si.right) {
					node.t = newTrs;
					newTrs.node = node;
					break;
				} else {
					newTrs = newTrs.next;
				}
			}
		}
	}
	
	public void updateD(Trapezoid di, LSegment si, Trapezoid tr) {
		if(di.next == null) {
			// update D
			Trapezoid newTrs = tr;
			Node node = null;
						
			Node d = find(si.left);
			if(d != null) { // Data structure was not empty
				if(di.left != si.left) {
					node = d;
					node.node = "x";
					node.p = si.left;
					node.left = new Node();
					node.left.node = "t";
					insertTrLR(newTrs,si,node.left,"left");
					/*while(newTrs != null) {
						if(newTrs.right == si.left) {
							node.left.t = newTrs;
							newTrs.node = node.left;
							break;
						} else {
							newTrs = newTrs.next;
						}	
					}*/
				} else {	// Node already exist
					node = d;												
					if(di.right != si.right) {
						node.node = "x";
						node.p = si.right;
						node.right = new Node();
						node.right.node = "t";
						insertTrLR(newTrs,si,node.right,"right");
						/*while(newTrs != null) {
							if(newTrs.left == si.right) {
								node.right.t = newTrs;
								newTrs.node = node.right;
								break;
							} else {
								newTrs = newTrs.next;
							}
						}*/
						node.left = new Node();
						node.left.node = "y";
						node.left.segment = si;
						node.left.left = new Node();
						node.left.left.node = "t";
						insertTrLR(newTrs,si,node.left.left,"left");					
						node.left.right = new Node();
						insertTrLR(newTrs,si,node.left.right,"right");
						
					} else {
						node.node = "y";
						node.segment = si;
						node.left = new Node();
						node.left.node = "t";
						insertTrLR(newTrs,si,node.left,"left");					
						node.right = new Node();
						insertTrLR(newTrs,si,node.right,"right");
					}
				}
			}
		}
	}
	
	// returns trapezoid where q lies
	public Node find(Vertice q) {
		Node di = D;
		if(di != null) {
			while(true) {
				if(di.node == xNode) {
					if(di.isLeft(q)) {
						di = di.left;
					} else {
						di = di.right;
					}
				} else if(di.node == yNode) {
					if(di.isAbove(q)) {
						di = di.left;
					} else {
						di = di.right;
					}
				} else {
					return di;
				}
			}
		} else {
			return di;
		}
	}
	
	private void insertToT(LSegment si) {
		//	5.	Remove d0,d1,...dk from T and replace them by the new trapezoids that
		//		appear because of the insertion of si.
	//	if()
	}
	
	private void insertToD(LSegment si) {
		//	6.	Remove the leaves for d0,d1,...,dk from D, and create leaves for
		//		the new trapezoids. Link the new leaves to the existing inner nodes
		//		by adding some new inner nodes.
		
	}
	
	/*****************************************************************
	 *  Input:	a new segment si
	 * Output:	The sequence d0,...,dk of trapezoids intersected by si
	 *****************************************************************/
	private Trapezoid followSegment(LSegment si)
	{
		// 1.	Let p and q be the left and right endpoint if si.
		Vertice p = si.startp();
		Vertice q = si.endp();
		
		// 2. Search with p in the search structure D to find d0.
		Node node = find(p);
		Trapezoid dj = node.t;
		System.out.println("trapezoid: " + dj.name);
		Trapezoid tmpDj = dj;
		if( dj != null ) {
			// 4. while(q lies to the right of rightp(dj)
			while( q.liesToRightOf( dj.rightp() ))
			{
			// 5.	do if rightp(dj) lies above si
				if(si.isAbove(dj.rightp())) {
			//		6.	then Let d(j+1) be the lower right neigbor of dj
					dj.next = dj.lRight;
					//dj = dj.lRight;
				}	
			//		7. else Let d(j+1) be the upper right neighbor of dj
				else {
					dj.next = dj.uRight;
					//dj = dj.uRight;
				}
				System.out.println("	tr: " + dj.name);
				dj = dj.next;				
				if(dj == null) {
					return tmpDj;
				}
			}
		}
		//		9. return d0,...,dj
		return tmpDj;
	}
	//	d0 == first added tr 
	// dk == last added tr
	// d == deleted tr
	private void updateNeigbors(Trapezoid d0, Trapezoid dk, Trapezoid d) {
		Trapezoid tmp = d.lLeft;
		// left neighbors of d
		if(tmp != null && d0 != null) {
			if(tmp.lRight == d) {
				tmp.lRight = d0;
			} else {
				tmp.uRight = d0;
			}
		}
		tmp = d.uLeft;
		if(tmp != null &&  d0 != null) {
			if(tmp.lRight == d) {
				tmp.lRight = d0;
			} else {
				tmp.uRight = d0;
			}
		}
		// right neighbor of d
		tmp = d.lRight;
		if(tmp != null && dk != null) {
			if(tmp.lLeft == d) {
				tmp.lLeft = dk;
			} else {
				tmp.uLeft = dk;
			}
		}
		tmp = d.uRight;
		if(tmp != null && dk != null) {
			if(tmp.lLeft == d) {
				tmp.lLeft = dk;
			} else {
				tmp.uLeft = dk;
			}
		}
	}
}

class Node {
	String node = "";
	
	public Vertice p = null;
	public LSegment segment = null;
	
	public Trapezoid t = null;	// Trapezoid these we are looking for	
	public Node left = null;	// left or above
	public Node right = null;	// right or below
		
//	public String leftIs = "";
//	public String rightIs = "";
		
	public boolean isLeft(Vertice q) {
		if(q.x < p.x) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isAbove(Vertice q) {//, LSegment s) {
		int above = Calculate.ccw(segment.left, segment.right, q);
		if(above == 0) { 	// if q is 			
			return true;
		} else {
			return false;
		}
	}
}

class Calculate {
	
	public static int ccw(Vertice p0, Vertice p1, Vertice p2)
	// Slightly deficient function to determine if the two lines p1, p2 and
	// p2, p3 turn in counter clockwise direction}
	{	
		int result = 0;	
		int dx1, dx2, dy1, dy2;
		dx1 = p1.getX() - p0.getX(); 
		dy1 = p1.getY() - p0.getY(); 
 		dx2 = p2.getX() - p0.getX(); 
 		dy2 = p2.getY() - p0.getY(); 
 		
 		if(dx1 * dy2 > dy1 * dx2)
 			result = 1; 
 		if(dx1 * dy2 < dy1 * dx2)
 			result = -1; 
 		if(dx1 * dy2 == dy1 * dx2)
  		{
   		if ((dx1*dx2<0) || (dy1*dy2<0))
   			result = -1;
   		else if( (dx1*dx1+dy1*dy1) >= (dx2*dx2+dy2*dy2) )
   			result = 0;
    		else
    			result = 1; 
  		}
  		
  		return result;	  	
	}
}