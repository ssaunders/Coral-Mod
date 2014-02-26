/**
   The Point3D class holds a Z coordinate. The data type
   of the coordinate is generic.
 */
package coral;
import java.awt.Point;

public class Point3D extends Point
{
    public int z=0;  // The z coordinate

    /**
      Constructor
      @param x The X coordinate.
      @param y The Y coordinate.
      @param z The Z coordinate.
     */

    public Point3D(int x, int y, int z)
    {
        // Call the Point class constructor.
        super(x, y);

        // Assign the Z coordinate.
        setZ(z);
    }


    /**
      The setZ method sets the Z coordinate.
      @param z The value for the Z coordinate.
     */

    public void setZ(int z_c)
    {
        z = z_c;
    }

    /**
      The getZ method returns the Z coordinate.
      @return The value of the Z coordinate.
     */

    public int getZ()
    {
        return z;
    }

    public void setLocation(int x, int y, int z) {
    	setLocation(x, y);
    	setZ(z);
    }
    
    public String toString() { 
    	return "coral.Point3D[x="+x+",y="+y+",z="+z+"]";
    }
    public String toPoint() { 
    	return "("+x+","+y+","+z+")";
    }
    
    @Override
    public int hashCode() {
    	return x*10000+y*100+z;
//        long bits = java.lang.Double.doubleToLongBits(getX());
//        bits ^= java.lang.Double.doubleToLongBits(getY()) * 31;
//        bits ^= java.lang.Double.doubleToLongBits(getZ()) * 31;
//        return (((int) bits) ^ ((int) (bits >> 32)));
    }
}
