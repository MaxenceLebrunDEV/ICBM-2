package icbm.zhapin.ex;

import icbm.zhapin.EZhaPin;
import icbm.zhapin.ZhaPin;
import net.minecraft.src.Block;
import net.minecraft.src.BlockFluid;
import net.minecraft.src.Entity;
import net.minecraft.src.World;
import universalelectricity.core.Vector3;

public class ExBingDan2 extends ZhaPin
{
	private static final int MAX_RADIUS = 40;

	public ExBingDan2(String name, int ID, int tier)
	{
		super(name, ID, tier);
		this.setYinXin(1);
	}

	@Override
	public void baoZhaQian(World worldObj, Vector3 position, Entity explosionSource)
	{
		if (!worldObj.isRemote)
		{
			EZhaPin source = (EZhaPin) explosionSource;

			for (int x = 0; x < MAX_RADIUS; ++x)
			{
				for (int y = 0; y < MAX_RADIUS; ++y)
				{
					for (int z = 0; z < MAX_RADIUS; ++z)
					{
						if (x == 0 || x == MAX_RADIUS - 1 || y == 0 || y == MAX_RADIUS - 1 || z == 0 || z == MAX_RADIUS - 1)
						{
							double xStep = (double) ((float) x / ((float) MAX_RADIUS - 1.0F) * 2.0F - 1.0F);
							double yStep = (double) ((float) y / ((float) MAX_RADIUS - 1.0F) * 2.0F - 1.0F);
							double zStep = (double) ((float) z / ((float) MAX_RADIUS - 1.0F) * 2.0F - 1.0F);
							double diagonalDistance = Math.sqrt(xStep * xStep + yStep * yStep + zStep * zStep);
							xStep /= diagonalDistance;
							yStep /= diagonalDistance;
							zStep /= diagonalDistance;
							float power = MAX_RADIUS * (0.7F + worldObj.rand.nextFloat() * 0.6F);
							double var15 = position.x;
							double var17 = position.y;
							double var19 = position.z;

							for (float var21 = 0.3F; power > 0.0F; power -= var21 * 0.75F)
							{
								Vector3 targetPosition = new Vector3(var15, var17, var19);
								double distanceFromCenter = position.distanceTo(targetPosition);
								int blockID = worldObj.getBlockId(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ());

								if (blockID > 0)
								{
									float resistance = 0;

									if (blockID == Block.bedrock.blockID)
									{
										break;
									}
									else if (Block.blocksList[blockID] instanceof BlockFluid)
									{
										resistance = 1f;
									}
									else
									{
										resistance = (Block.blocksList[blockID].getExplosionResistance(explosionSource, worldObj, targetPosition.intX(), targetPosition.intY(), targetPosition.intZ(), position.intX(), position.intY(), position.intZ()) + 0.3F) * var21;
									}

									power -= resistance;
								}

								if (power > 0.0F)
								{
									source.dataList.add(targetPosition.clone());
								}

								var15 += xStep * (double) var21;
								var17 += yStep * (double) var21;
								var19 += zStep * (double) var21;
							}
						}
					}
				}
			}
		}
	}

	@Override
	public boolean doBaoZha(World worldObj, Vector3 position, Entity explosionSource, int metadata, int callCount)
	{
		if (!worldObj.isRemote)
		{
			EZhaPin source = (EZhaPin) explosionSource;

			int radius = callCount;

			for (Object obj : source.dataList)
			{
				Vector3 targetPosition = (Vector3) obj;

				double distance = Vector3.distance(targetPosition, position);

				double distanceFromCenter = position.distanceTo(targetPosition);

				if (distanceFromCenter > radius || distanceFromCenter < radius - 2)
					continue;

				double chance = radius - (Math.random() * distanceFromCenter);

				if (chance > distanceFromCenter * 0.55)
				{
					// Check to see if the block
					// is an air block and there
					// is a block below it to
					// support the fire
					int blockID = worldObj.getBlockId((int) targetPosition.x, (int) targetPosition.y, (int) targetPosition.z);

					if (blockID == Block.fire.blockID || blockID == Block.lavaMoving.blockID || blockID == Block.lavaStill.blockID)
					{
						worldObj.setBlockWithNotify((int) targetPosition.x, (int) targetPosition.y, (int) targetPosition.z, Block.snow.blockID);
					}
					else if (blockID == 0 && worldObj.getBlockId((int) targetPosition.x, (int) targetPosition.y - 1, (int) targetPosition.z) != Block.ice.blockID && worldObj.getBlockId((int) targetPosition.x, (int) targetPosition.y - 1, (int) targetPosition.z) != 0)
					{
						worldObj.setBlockWithNotify((int) targetPosition.x, (int) targetPosition.y, (int) targetPosition.z, Block.ice.blockID);
					}
				}
			}
		}

		worldObj.playSoundEffect(position.x + 0.5D, position.y + 0.5D, position.z + 0.5D, "icbm.redmatter", 6.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 1F);

		if (callCount > MAX_RADIUS) { return false; }
		return true;
	}

	/**
	 * The interval in ticks before the next
	 * procedural call of this explosive
	 * 
	 * @return - Return -1 if this explosive does
	 *         not need proceudral calls
	 */
	public int proceduralInterval()
	{
		return 3;
	}

	/**
	 * Called every tick
	 * 
	 * @param ticksExisted
	 * @Override public void onUpdate(World
	 *           worldObj, Vector3 position, int
	 *           ticksExisted) { long worldTime =
	 *           worldObj.getWorldTime();
	 * 
	 *           while(worldTime > 23999) {
	 *           worldTime -= 23999; }
	 * 
	 *           if(worldTime < 0) {
	 *           worldObj.setWorldTime
	 *           (worldObj.getWorldTime()+150); }
	 *           }
	 */

}
