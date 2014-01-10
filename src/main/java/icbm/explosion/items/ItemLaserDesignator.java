package icbm.explosion.items;

import icbm.Reference;
import icbm.api.IItemFrequency;
import icbm.core.ICBMCore;
import icbm.core.Settings;
import icbm.core.prefab.item.ItemICBMElectrical;
import icbm.explosion.EntityLightBeam;
import icbm.explosion.ICBMExplosion;
import icbm.explosion.machines.MissileLauncherRegistry;
import icbm.explosion.machines.TileCruiseLauncher;
import icbm.explosion.machines.TileLauncherPrefab;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import universalelectricity.api.vector.Vector2;
import universalelectricity.api.vector.Vector3;
import calclavia.lib.network.IPacketReceiver;
import calclavia.lib.utility.LanguageUtility;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.PacketDispatcher;

public class ItemLaserDesignator extends ItemICBMElectrical implements IItemFrequency, IPacketReceiver
{
	public static final int BAN_JING = Settings.DAO_DAN_ZUI_YUAN;
	public static final int YONG_DIAN_LIANG = 6000;

	public ItemLaserDesignator(int id)
	{
		super(id, "laserDesignator");
	}

	/** Allows items to add custom lines of information to the mouseover description */
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
	{
		super.addInformation(itemStack, par2EntityPlayer, par3List, par4);

		if (this.getFrequency(itemStack) > 0)
		{
			par3List.add(LanguageUtility.getLocal("info.misc.freq") + " " + getFrequency(itemStack));
		}
		else
		{
			par3List.add(LanguageUtility.getLocal("info.designator.noFreq"));
		}
	}

	@Override
	public int getFrequency(ItemStack itemStack)
	{
		if (itemStack.stackTagCompound == null)
		{
			return 0;
		}
		return itemStack.stackTagCompound.getInteger("frequency");
	}

	@Override
	public void setFrequency(int frequency, ItemStack itemStack)
	{
		if (itemStack.stackTagCompound == null)
		{
			itemStack.setTagCompound(new NBTTagCompound());
		}

		itemStack.stackTagCompound.setInteger("frequency", frequency);
	}

	public int getLauncherCountDown(ItemStack par1ItemStack)
	{
		if (par1ItemStack.stackTagCompound == null)
		{
			return -1;
		}

		return par1ItemStack.stackTagCompound.getInteger("countDown");
	}

	public void setLauncherCountDown(ItemStack par1ItemStack, int value)
	{
		if (par1ItemStack.stackTagCompound == null)
		{
			par1ItemStack.setTagCompound(new NBTTagCompound());
		}

		par1ItemStack.stackTagCompound.setInteger("countDown", value);
	}

	public int getLauncherCount(ItemStack par1ItemStack)
	{
		if (par1ItemStack.stackTagCompound == null)
		{
			return 0;
		}
		return par1ItemStack.stackTagCompound.getInteger("launcherCount");
	}

	public void setLauncherCount(ItemStack par1ItemStack, int value)
	{
		if (par1ItemStack.stackTagCompound == null)
		{
			par1ItemStack.setTagCompound(new NBTTagCompound());
		}

		par1ItemStack.stackTagCompound.setInteger("launcherCount", value);
	}

	public int getLauncherDelay(ItemStack par1ItemStack)
	{
		if (par1ItemStack.stackTagCompound == null)
		{
			return 0;
		}
		return par1ItemStack.stackTagCompound.getInteger("launcherDelay");
	}

	public void setLauncherDelay(ItemStack par1ItemStack, int value)
	{
		if (par1ItemStack.stackTagCompound == null)
		{
			par1ItemStack.setTagCompound(new NBTTagCompound());
		}

		par1ItemStack.stackTagCompound.setInteger("launcherDelay", value);
	}

	/**
	 * Called each tick as long the item is on a player inventory. Uses by maps to check if is on a
	 * player hand and update it's contents.
	 */
	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5)
	{
		super.onUpdate(par1ItemStack, par2World, par3Entity, par4, par5);

		if (!par2World.isRemote)
		{
			List<TileLauncherPrefab> connectedLaunchers = new ArrayList<TileLauncherPrefab>();

			if (this.getLauncherCountDown(par1ItemStack) > 0 || this.getLauncherCount(par1ItemStack) > 0)
			{
				Vector3 position = new Vector3(par3Entity.posX, par3Entity.posY, par3Entity.posZ);
				HashSet<TileLauncherPrefab> launchers = MissileLauncherRegistry.naFaSheQiInArea(new Vector2(position.x - ItemLaserDesignator.BAN_JING, position.z - ItemLaserDesignator.BAN_JING), new Vector2(position.x + ItemLaserDesignator.BAN_JING, position.z + ItemLaserDesignator.BAN_JING));

				for (TileLauncherPrefab missileLauncher : launchers)
				{
					if (missileLauncher != null && missileLauncher.getFrequency() == this.getFrequency(par1ItemStack))
					{
						if (missileLauncher.canLaunch())
						{
							connectedLaunchers.add(missileLauncher);
						}
					}
				}
			}

			if (this.getLauncherCountDown(par1ItemStack) > 0 && connectedLaunchers.size() > 0)
			{
				if (this.getLauncherCountDown(par1ItemStack) % 20 == 0)
				{
					((EntityPlayer) par3Entity).addChatMessage(LanguageUtility.getLocal("message.designator.callTime") + " " + (int) Math.floor(this.getLauncherCountDown(par1ItemStack) / 20));
				}

				if (this.getLauncherCountDown(par1ItemStack) == 1)
				{
					this.setLauncherCount(par1ItemStack, connectedLaunchers.size());
					this.setLauncherDelay(par1ItemStack, 0);
					((EntityPlayer) par3Entity).addChatMessage(LanguageUtility.getLocal("message.designator.blast"));
				}

				this.setLauncherCountDown(par1ItemStack, this.getLauncherCountDown(par1ItemStack) - 1);
			}

			if (this.getLauncherCount(par1ItemStack) > 0 && this.getLauncherCount(par1ItemStack) <= connectedLaunchers.size() && connectedLaunchers.size() > 0)
			{
				// Launch a missile every two seconds from different launchers
				if (this.getLauncherDelay(par1ItemStack) % 40 == 0)
				{
					connectedLaunchers.get(this.getLauncherCount(par1ItemStack) - 1).launch();
					this.setLauncherCount(par1ItemStack, this.getLauncherCount(par1ItemStack) - 1);
				}

				if (this.getLauncherCount(par1ItemStack) == 0)
				{
					this.setLauncherDelay(par1ItemStack, 0);
					connectedLaunchers.clear();
				}

				this.setLauncherDelay(par1ItemStack, this.getLauncherDelay(par1ItemStack) + 1);
			}
		}
	}

	/**
	 * Callback for item usage. If the item does something special on right clicking, he will have
	 * one of those. Return True if something happen and false if it don't. This is for ITEMS, not
	 * BLOCKS !
	 */
	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int x, int y, int z, int par7, float par8, float par9, float par10)
	{
		if (!par3World.isRemote)
		{
			// SET FREQUENCY OF REMOTE
			TileEntity tileEntity = par3World.getBlockTileEntity(x, y, z);

			if (tileEntity != null)
			{
				if (tileEntity instanceof TileLauncherPrefab)
				{
					TileLauncherPrefab missileLauncher = (TileLauncherPrefab) tileEntity;

					if (missileLauncher.getFrequency() > 0)
					{
						this.setFrequency(missileLauncher.getFrequency(), par1ItemStack);
						par2EntityPlayer.addChatMessage(LanguageUtility.getLocal("message.designator.setFreq") + " " + this.getFrequency(par1ItemStack));
					}
					else
					{
						par2EntityPlayer.addChatMessage(LanguageUtility.getLocal("message.designator.failFreq"));
					}
				}
			}
		}

		return false;
	}

	/**
	 * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack,
	 * world, entityPlayer
	 */
	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer player)
	{
		if (par2World.isRemote)
		{
			MovingObjectPosition objectMouseOver = player.rayTrace(BAN_JING * 2, 1);

			if (objectMouseOver != null && objectMouseOver.typeOfHit == EnumMovingObjectType.TILE)
			{
				// Check for short-fused TNT. If
				// there is a short fused TNT,
				// then blow it up.
				int blockId = par2World.getBlockId(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);
				int blockMetadata = par2World.getBlockMetadata(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);

				if (this.getLauncherCountDown(par1ItemStack) > 0)
				{
					return par1ItemStack;
				}

				// Prevents calling air strike if
				// the user is trying to set the
				// frequency of the remote.
				if (blockId == ICBMExplosion.blockMachine.blockID)
				{
					return par1ItemStack;
				}
				else
				{
					// Update the
					// airStrikeFrequency
					int airStrikeFreq = this.getFrequency(par1ItemStack);

					// Check if it is possible to
					// do an air strike. If so, do
					// one.
					if (airStrikeFreq > 0)
					{
						if (this.getEnergy(par1ItemStack) > YONG_DIAN_LIANG)
						{
							Vector3 position = new Vector3(player.posX, player.posY, player.posZ);
							HashSet<TileLauncherPrefab> launchers = MissileLauncherRegistry.naFaSheQiInArea(new Vector2(position.x - ItemLaserDesignator.BAN_JING, position.z - ItemLaserDesignator.BAN_JING), new Vector2(position.x + ItemLaserDesignator.BAN_JING, position.z + ItemLaserDesignator.BAN_JING));

							boolean doAirStrike = false;
							int errorCount = 0;

							for (TileLauncherPrefab missileLauncher : launchers)
							{
								if (missileLauncher != null && missileLauncher.getFrequency() == airStrikeFreq)
								{
									if (missileLauncher instanceof TileCruiseLauncher)
									{
										missileLauncher.setTarget(new Vector3(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ));
										PacketDispatcher.sendPacketToServer(ICBMCore.PACKET_TILE.getPacket(missileLauncher, 2, missileLauncher.getTarget().x, missileLauncher.getTarget().y, missileLauncher.getTarget().z));
									}
									else
									{

										double previousY = 0;

										if (missileLauncher.getTarget() != null)
										{
											previousY = missileLauncher.getTarget().y;
										}

										missileLauncher.setTarget(new Vector3(objectMouseOver.blockX, previousY, objectMouseOver.blockZ));
										PacketDispatcher.sendPacketToServer(ICBMCore.PACKET_TILE.getPacket(missileLauncher, 2, missileLauncher.getTarget().x, missileLauncher.getTarget().y, missileLauncher.getTarget().z));
									}

									if (missileLauncher.canLaunch())
									{
										doAirStrike = true;
									}
									else
									{
										errorCount++;
										// par3EntityPlayer.addChatMessage("#"+errorCount+" Missile Launcher Error: "+missileLauncher.getStatus());
									}
								}
							}

							if (doAirStrike && this.getLauncherCountDown(par1ItemStack) >= 0)
							{
								PacketDispatcher.sendPacketToServer(ICBMCore.PACKET_ITEM.getPacket(player, objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ));
								player.addChatMessage(LanguageUtility.getLocal("message.designator.callBlast"));
							}
						}
						else
						{
							player.addChatMessage(LanguageUtility.getLocal("message.designator.nopower"));
						}
					}
					else
					{
						player.addChatMessage(LanguageUtility.getLocal("message.designator.noFreq"));
					}
				}
			}
		}

		return par1ItemStack;
	}

	@Override
	public long getVoltage(ItemStack itemStack)
	{
		return 30;
	}

	@Override
	public long getEnergyCapacity(ItemStack itemStack)
	{
		return 800;
	}

	@Override
	public void onReceivePacket(ByteArrayDataInput data, EntityPlayer player, Object... extra)
	{
		ItemStack itemStack = (ItemStack) extra[0];
		Vector3 position = new Vector3(data.readInt(), data.readInt(), data.readInt());

		((ItemLaserDesignator) ICBMExplosion.itemLaserDesignator).setLauncherCountDown(itemStack, 119);

		player.worldObj.playSoundEffect(position.intX(), player.worldObj.getHeightValue(position.intX(), position.intZ()), position.intZ(), Reference.PREFIX + "airstrike", 5.0F, (1.0F + (player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);

		player.worldObj.spawnEntityInWorld(new EntityLightBeam(player.worldObj, position, 5 * 20, 0F, 1F, 0F));

		ICBMExplosion.itemRadarGun.discharge(itemStack, ItemLaserDesignator.YONG_DIAN_LIANG, true);
	}
}