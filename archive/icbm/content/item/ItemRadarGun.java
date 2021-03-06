package icbm.content.items;

import icbm.ICBM;
import icbm.content.prefab.item.ItemICBMElectrical;

import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import resonant.engine.ResonantEngine;
import resonant.lib.network.discriminator.PacketPlayerItem;
import resonant.lib.network.discriminator.PacketTile;
import resonant.lib.network.discriminator.PacketType;
import resonant.lib.network.handle.IPacketReceiver;
import resonant.lib.prefab.item.ItemElectric;
import resonant.lib.transform.vector.Vector3;
import resonant.lib.utility.LanguageUtility;

public class ItemRadarGun extends ItemICBMElectrical implements IPacketReceiver
{
    public static final int energyCost = 12500;
    public static final int raycastDistance = 1000;

    public ItemRadarGun()
    {
        super("radarGun");
    }

    /** Allows items to add custom lines of information to the mouseover description */
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List par3List, boolean par4)
    {
        Vector3 coord = getLink(itemStack);
        par3List.add("\uaa74" + LanguageUtility.getLocal("info.radarGun.savedCoords"));
        par3List.add(LanguageUtility.getLocal("gui.misc.x") + " " + (int) coord.x() + ", " + LanguageUtility.getLocal("gui.misc.y") + " " + (int) coord.y() + ", " + LanguageUtility.getLocal("gui.misc.z") + " " + (int) coord.z());
        par3List.add((int) new Vector3(entityPlayer).distance(coord) + " " + LanguageUtility.getLocal("info.radarGun.meters") + " (" + (int) (new Vector3(entityPlayer).x() - coord.x()) + ", " + (int) (new Vector3(entityPlayer).y() - coord.y()) + ", " + (int) (new Vector3(entityPlayer).z() - coord.z()) + ")");

		super.addInformation(itemStack, entityPlayer, par3List, par4);
    }

    /** Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack,
     * world, entityPlayer */
    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World par2World, EntityPlayer entityPlayer)
    {
        if (par2World.isRemote)
        {
            MovingObjectPosition objectMouseOver = entityPlayer.rayTrace(raycastDistance, 1);

            if (objectMouseOver != null)
            {
                TileEntity tileEntity = par2World.getTileEntity(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);

                // Do not scan if the target is a
                // missile launcher
                if (!(tileEntity instanceof TileLauncherPrefab))
                {
                    // Check for electricity
                    if (this.getEnergy(itemStack) > energyCost)
                    {
                        ResonantEngine.instance.packetHandler.sendToServer(new PacketPlayerItem(entityPlayer, objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ));
                        this.discharge(itemStack, energyCost, true);
                        entityPlayer.addChatComponentMessage(new ChatComponentText(LanguageUtility.getLocal("message.radarGun.scanned").replaceAll("%x", "" + objectMouseOver.blockX).replace("%y", "" + objectMouseOver.blockY).replaceAll("%z", "" + objectMouseOver.blockZ).replaceAll("%d", "" + Math.round(new Vector3(entityPlayer).distance(new Vector3(objectMouseOver))))));
                    }
                    else
                    {
                        entityPlayer.addChatComponentMessage(new ChatComponentText(LanguageUtility.getLocal("message.radarGun.nopower")));
                    }
                }
            }
        }

        return itemStack;
    }

    /** Callback for item usage. If the item does something special on right clicking, he will have
     * one of those. Return True if something happen and false if it don't. This is for ITEMS, not
     * BLOCKS ! */
    @Override
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int x, int y, int z, int par7, float par8, float par9, float par10)
    {
        Block blockId = par3World.getBlock(x, y, z);
        int blockMetadata = par3World.getBlockMetadata(x, y, z);

        if (blockId == ICBM.blockMachine)
        {
            TileEntity tileEntity = par3World.getTileEntity(x, y, z);

            if (tileEntity != null)
            {
                if (tileEntity instanceof TileLauncherScreen)
                {
                    TileLauncherScreen missileLauncher = (TileLauncherScreen) tileEntity;

                    Vector3 savedCords = this.getLink(par1ItemStack);

                    // If the vector is NOT 0
                    if (!savedCords.equals(new Vector3()))
                    {
                        if (missileLauncher.getTarget() == null)
                        {
                            missileLauncher.setTarget(new Vector3());
                        }

                        missileLauncher.getTarget().x_$eq((int) savedCords.x());
                        missileLauncher.getTarget().z_$eq((int) savedCords.z());

                        if (par3World.isRemote)
                        {
                            ResonantEngine.instance.packetHandler.sendToServer(new PacketTile(missileLauncher, 2, savedCords.xi(), missileLauncher.getTarget().zi(), savedCords.zi()));
                            par2EntityPlayer.addChatComponentMessage(new ChatComponentText((LanguageUtility.getLocal("message.radarGun.transfer"))));
                        }
                    }
                    else
                    {
                        if (par3World.isRemote)
                            par2EntityPlayer.addChatComponentMessage(new ChatComponentText((LanguageUtility.getLocal("message.radarGun.noCoords"))));
                    }
                }
            }
        }

        return false;
    }

    public void setLink(ItemStack itemStack, Vector3 position)
    {
        // Saves the frequency in the ItemStack
        if (itemStack.getTagCompound() == null)
        {
            itemStack.setTagCompound(new NBTTagCompound());
        }

        position.writeIntNBT(itemStack.getTagCompound());
    }

    public Vector3 getLink(ItemStack itemStack)
    {
        if (itemStack.getTagCompound() == null)
        {
            itemStack.setTagCompound(new NBTTagCompound());
        }

        return new Vector3(itemStack.getTagCompound());
    }

    @Override
    public double getVoltage(ItemStack itemStack)
    {
        return 50;
    }

    @Override
    public double getEnergyCapacity(ItemStack theItem)
    {
        return 1000000;
    }

    @Override
    public void read(ByteBuf data, EntityPlayer player, PacketType type)
    {
        if(type instanceof PacketPlayerItem)
        {
            ItemStack itemStack = player.inventory.getStackInSlot(((PacketPlayerItem)type).slotId);
            if(itemStack != null)
            {
                this.setLink(itemStack, new Vector3(data.readInt(), data.readInt(), data.readInt()));
                if (ICBM.itemRadarGun instanceof ItemElectric)
                    ((ItemElectric) ICBM.itemRadarGun).discharge(itemStack, ItemRadarGun.energyCost, true);
            }
        }
    }

}
