package resonant.lib.world

import net.minecraft.block.Block
import net.minecraft.entity.Entity
import net.minecraft.init.Blocks
import net.minecraft.util.Vec3
import net.minecraft.world.World
import resonant.lib.transform.vector.{IVector3, Vector3}

import scala.beans.BeanProperty

/** Simple version of Vector3 that is used to track location
  * along with the block to be set at that location.
  *
 * Created by robert on 12/2/2014.
 */
class Vector3Change(x :Double,y :Double,z :Double) extends Vector3(x,y,z)
{
  @BeanProperty
  var block : Block = Blocks.air
  @BeanProperty
  var meta : Int = 0;

  def this() = this(0, 0, 0)

  def this(x :Double,y :Double,z :Double, block: Block, meta: Int) =
  {
    this(x, y, z)
    this.block = block;
    this.meta = meta;
  }

  def this(x :Double,y :Double,z :Double, block: Block) =
  {
    this(x, y, z, block, 0)
  }

  def this(entity: Entity) = this(entity.posX, entity.posY, entity.posZ)

  def this(vec: IVector3) = this(vec.x, vec.y, vec.z)

  def this(vec: Vector3) = this(vec.x, vec.y, vec.z)

  def this(vec: Vec3) = this(vec.xCoord, vec.yCoord, vec.zCoord)

  def setBlock(world: World)
  {
    super.setBlock(world, block, meta)
  }
}
