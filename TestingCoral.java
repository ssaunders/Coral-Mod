package coral;

import java.awt.Color;

public class TestingCoral extends BlockCoral {

	public TestingCoral(int id) {
		super(id);
	}
	
	String[] attributes = {"atThreshold","light level","splitPoint","expansionCost",
							"livingCost","growthFactor","photoFactor"};
	int[] attrBtmVals = {0,  0,  22,  5, 1, 1, 1};
	int[] attrTopVals = {1, 15, 100, 15, 5, 5, 4};
	
	//output 
		//a png file?
		//a csv file, then conditional format?
	//Test which attributes - test one attribute, hold others constant
	 //ENVIRONMENT
	   //# of neighbors
	   //lightlevel
	   		//soilpref
	 //CORAL
	   //Primary:
		 //livingCost
		 //threshold
		 //growthFactor
	 	 //photoFactor
	   //Secondary:
		 //splitPoint
		 //expansionCost
	
	/* JUST LIKE IN THE MANDLEBROT, WE LOOK FOR DIVERGENCE.
	 * AREAS WHERE THERE IS RAPID CHANGE (GROWTH);
	 */
	
	/** Runs the chosen coral type through the different environments. */
	public void testCoralInEnv(CORAL_TYPE type, int equation) {
		//new ary[2][15] = [][]
		//fill with startHealth
		//set equation
		
		//for atThreshold
			//for lightlevel
				//add to stringA (run 5 iters on cell[][]);
				//add to stringB (run 5 more iters on cell[][]);
				//add to stringC (run 5 more iters on cell[][]);
			//append newlines to a,b,c
		
		//output to file A,B,C
	}
	
	public int scaleTo255(int val) {
		int high = 100,
			low  = 0;
		return val;
	}
	public Color convertToColor(int val) {
		return null;
	}
}
