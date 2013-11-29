/**
   The Point3D class holds a Z coordinate. The data type
   of the coordinate is generic.
*/
package coral;
import java.awt.Point;

public class Point3D extends Point
{
   private int zCoordinate;  // The z coordinate
   
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
      zCoordinate = z;
   }
   
   /**
      The setZ method sets the Z coordinate.
      @param z The value for the Z coordinate.
   */
   
   public void setZ(int z)
   {
      zCoordinate = z;
   }

   /**
      The getZ method returns the Z coordinate.
      @return The value of the Z coordinate.
   */
   
   public int getZ()
   {
      return zCoordinate;
   }
}
