/****************************************************
 * Few functions to help our calculations.
 *
 * isAbove tells to you if the point q is above or below the line
 * from s to e
 *
 *	ccw tells  if the p2 is left or right from the line
 * p0 to p1. If it to left it repots -1, 1 if it to the right
 * and 0 if it is on the line
 * 
 *	This file is written by Jukka Kaartinen
 * jukka.kaartinen@tut.fi
 *************************************************/
 
import java.awt.Component;

class Calculate extends Component {
	
	public static boolean isAbove(Vertice s, Vertice e, Vertice q) {
		int above = ccw(s, e, q);
		if(above < 0) { 	// if q is above or in the same line
			return true;
		} else {
			return false;
		}
	}
	
	public static void verticalLine(Trapezoid trLeft, Trapezoid trRight, LSegment si) {
		if(trLeft != null) {
			float dxTop = (float)(trLeft.top.right.x*100) - (float)(trLeft.top.left.x*100);
			float dxBottom = (float)(trLeft.bottom.right.x*100) - (float)(trLeft.bottom.left.x*100);
			float k = 0.0f;
		
			if(dxTop != 0 && dxBottom != 0) {
				// y = kx + b
				k = ((float)(trLeft.top.right.y*100) - (float)(trLeft.top.left.y*100))/dxTop;
				si.left.up =   (int)(k*((float)(si.left.x*100) - (float)(trLeft.top.left.x*100)) + (float)(trLeft.top.left.y*100));
				si.left.up /= 100;
						
				k = ((float)(trLeft.bottom.right.y*100) - (float)(trLeft.bottom.left.y*100))/dxBottom;
				si.left.down = (int)(k*((float)(si.left.x*100) - (float)(trLeft.bottom.left.x*100)) + (float)(trLeft.bottom.left.y*100));
				si.left.down /= 100;
			}
		}
			
		if(trRight != null) {
			float dxTop = (float)(trRight.top.right.x*100) - (float)(trRight.top.left.x*100);
			float dxBottom = (float)(trRight.bottom.right.x*100) - (float)(trRight.bottom.left.x*100);
			float k = 0.0f;
		
			if(dxTop != 0.0f && dxBottom != 0.0f) {
							
				k = ((float)(trRight.top.right.y*100) - (float)(trRight.top.left.y*100))/(float)dxTop;
				si.right.up = (int)(k*((float)(si.right.x*100)  - (float)(trRight.top.left.x*100)) + (float)(trRight.top.left.y*100));
				si.right.up /= 100;
				
				k = ((float)(trRight.bottom.right.y*100) - (float)(trRight.bottom.left.y*100))/(float)dxBottom;
				si.right.down = (int)(k*((float)(si.right.x*100)  - (float)(trRight.bottom.left.x*100)) + (float)(trRight.bottom.left.y*100));
				si.right.down /= 100;
			}
		} else {
			// We have a vertical line and that is not permitted
		}
	}
	
	// Shortens eighter lower or upper left enf of the trapezoid
	public static void shortenLine(Trapezoid tr, LSegment si, boolean up) {
		float dx = si.right.x*100 - si.left.x*100;	
		float k = 0;
		
		if(dx != 0) {
			if(up) {
				// y = kx + b
				k = ((float)(si.right.y*100) - (float)(si.left.y*100))/(float)dx;
				tr.left.up = (int)(k*((float)(tr.left.x*100) - (float)(si.left.x*100)) + (float)(si.left.y*100));
				tr.left.up /= 100;
			} else {			
				k = ((float)(si.right.y*100) - (float)(si.left.y*100))/(float)dx;
				tr.left.down = (int)(k*((float)(tr.left.x*100) - (float)(si.left.x*100)) + (float)(si.left.y*100));
				tr.left.down /= 100;
			}
		} else {
			// We have a vertical line and that is not permitted			
		}
	}
	
	// Calculates trapzoids vertices
	public static TrPolygon polygon(Trapezoid t) {
		TrPolygon poly = null;
		if(t != null) {
			poly = new TrPolygon();
		
			float dxTop = (float)(t.top.right.x) - (float)(t.top.left.x);
			float dxBottom = (float)(t.bottom.right.x) - (float)(t.bottom.left.x);
		
			float k = 0.0f;
			if(dxTop != 0 && dxBottom != 0) {
				// y = kx + b
				k = ((float)(t.top.right.y) - (float)(t.top.left.y))/dxTop;
				poly.x[1] = t.left.x;
				poly.y[1] = (int)(k*((float)(t.left.x) - (float)(t.top.left.x)) + (float)(t.top.left.y));
							
				k = ((float)(t.bottom.right.y) - (float)(t.bottom.left.y))/dxBottom;
				poly.x[0] = t.left.x;
				poly.y[0] = (int)(k*((float)(t.left.x) - (float)(t.bottom.left.x)) + (float)(t.bottom.left.y));
			}
			
			dxTop = (float)(t.top.right.x) - (float)(t.top.left.x);
			dxBottom = (float)(t.bottom.right.x) - (float)(t.bottom.left.x);
			k = 0.0f;
		
			if(dxTop != 0.0f && dxBottom != 0.0f) {					
				k = ((float)(t.top.right.y) - (float)(t.top.left.y))/(float)dxTop;
				poly.x[2] = t.right.x;
				poly.y[2] = (int)(k*((float)(t.right.x)  - (float)(t.top.left.x)) + (float)(t.top.left.y));
						
				k = ((float)(t.bottom.right.y) - (float)(t.bottom.left.y))/(float)dxBottom;
				poly.x[3] = t.right.x;
				poly.y[3] = (int)(k*((float)(t.right.x)  - (float)(t.bottom.left.x)) + (float)(t.bottom.left.y));
			}
		}
				
		return poly;
	}
		
	/************************************************************************
   *	Took this two(non_crossing and ccw) from
   *	http://condor.informatik.uni-oldenburg.de/~stueker/graphic/index.html
   *	
   */
   public static boolean non_crossing(LSegment a, LSegment b) {
		int ccw11, ccw12, ccw21, ccw22;
		
 		ccw11 = ccw(a.startp(), a.endp(), b.startp());
 		ccw12 = ccw(a.startp(), a.endp(), b.endp());
 		ccw21 = ccw(b.startp(), b.endp(), a.startp());
 		ccw22 = ccw(b.startp(), b.endp(), a.endp());
 		
 		if( ( (ccw11*ccw12 < 0) && (ccw21*ccw22 < 0)) ||
  			(ccw11*ccw12*ccw21*ccw22 == 0) )
			return true;
		else
			return false;	
    }
    
	public static int ccw(Vertice p0, Vertice p1, Vertice p2) {
	// Slightly deficient function to determine if the two lines p1, p2 and
	// p2, p3 turn in counter clockwise direction}
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
 		if(dx1 * dy2 == dy1 * dx2)	{
   		if ((dx1*dx2<0) || (dy1*dy2<0))
   			result = -1;
   		else if( (dx1*dx1+dy1*dy1) >= (dx2*dx2+dy2*dy2) )
   			result = 0;
    		else
    			result = 1; 
  		}
  		//System.out.println("ccw1: " + result);
  		return result;
	}	
}