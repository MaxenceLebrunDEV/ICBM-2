package com.builtbroken.icbm.content.launcher.controller.remote.antenna;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.mc.api.map.radio.wireless.IWirelessNetwork;
import com.builtbroken.mc.api.map.radio.wireless.IWirelessNetworkObject;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.registry.implement.IRecipeContainer;
import com.builtbroken.mc.core.registry.implement.IRegistryInit;
import com.builtbroken.mc.lib.helper.WrenchUtility;
import com.builtbroken.mc.lib.helper.recipe.OreNames;
import com.builtbroken.mc.lib.helper.recipe.UniversalRecipe;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/28/2016.
 */
public class BlockAntennaParts extends BlockContainer implements IRegistryInit, IRecipeContainer
{
    public BlockAntennaParts()
    {
        super(Material.iron);
        this.setBlockName(ICBM.PREFIX + "antennaParts");
        this.blockHardness = 10;
        this.blockResistance = 10;
        this.opaque = false;
        this.useNeighborBrightness = true;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xx, float yy, float zz)
    {
        int meta = world.getBlockMetadata(x, y, z);

        //Prevent click with this block to make placement easier
        if (player.getHeldItem() != null)
        {
            if (player.getHeldItem().getItem() == Item.getItemFromBlock(this))
            {
                return false;
            }
            else if (Engine.runningAsDev)
            {
                if (player.getHeldItem().getItem() == Items.stick)
                {
                    if (!world.isRemote)
                    {
                        TileEntity tile = world.getTileEntity(x, y, z);
                        if (tile instanceof TileAntenna)
                        {
                            if (((TileAntenna) tile).wirelessNetwork != null)
                            {
                                player.addChatComponentMessage(new ChatComponentText("Debug: Wireless Network = " + ((TileAntenna) tile).wirelessNetwork));
                            }
                            else if (((TileAntenna) tile).getAttachedNetworks().size() > 0)
                            {
                                for (IWirelessNetwork network : ((TileAntenna) tile).getAttachedNetworks())
                                {
                                    player.addChatComponentMessage(new ChatComponentText("" + network));
                                }
                            }
                            else
                            {
                                player.addChatComponentMessage(new ChatComponentText("Debug: Wireless Network is null"));
                            }
                        }
                        else if (tile instanceof TileAntennaPart)
                        {
                            if (((TileAntennaPart) tile).antennaNetwork != null)
                            {
                                player.addChatComponentMessage(new ChatComponentText("Debug: Network contains " + ((TileAntennaPart) tile).antennaNetwork.size() + " parts, base = " + ((TileAntennaPart) tile).antennaNetwork.base));
                            }
                            else
                            {
                                player.addChatComponentMessage(new ChatComponentText("Debug: Network is null"));
                            }
                        }
                    }
                    return true;
                }
                else if (player.getHeldItem().getItem() == Items.blaze_rod)
                {
                    if (!world.isRemote)
                    {
                        TileEntity tile = world.getTileEntity(x, y, z);
                        if (tile instanceof TileAntenna)
                        {
                            player.addChatComponentMessage(new ChatComponentText("Debug: Networks " + ((TileAntenna) tile).getAttachedNetworks().size() + " connections"));
                        }
                        else if (tile instanceof TileAntennaPart)
                        {
                            player.addChatComponentMessage(new ChatComponentText("Debug: Parts " + ((TileAntennaPart) tile).connections.size() + " connections"));
                        }
                    }
                    return true;
                }
                else if (player.getHeldItem().getItem() == Items.arrow)
                {
                    if (!world.isRemote)
                    {
                        TileEntity tile = world.getTileEntity(x, y, z);
                        if (tile instanceof TileAntennaPart && ((TileAntennaPart) tile).antennaNetwork != null)
                        {
                            player.addChatComponentMessage(new ChatComponentText("Debug: Network Size Data: " + ((TileAntennaPart) tile).antennaNetwork.size));
                            player.addChatComponentMessage(new ChatComponentText("Debug: Network Sender:    " + ((TileAntennaPart) tile).antennaNetwork.senderRange));
                            player.addChatComponentMessage(new ChatComponentText("Debug: Network Receive:   " + ((TileAntennaPart) tile).antennaNetwork.receiveRange));
                        }
                        else
                        {
                            player.addChatComponentMessage(new ChatComponentText("Debug: Network is null"));
                        }
                    }
                    return true;
                }
                else if (player.getHeldItem().getItem() == Items.apple)
                {
                    if (!world.isRemote)
                    {
                        TileEntity tile = world.getTileEntity(x, y, z);
                        if (tile instanceof TileAntenna && ((TileAntenna) tile).wirelessNetwork != null)
                        {
                            int i = 0;
                            for (IWirelessNetworkObject obj : ((TileAntenna) tile).wirelessNetwork.getAttachedObjects())
                            {
                                player.addChatComponentMessage(new ChatComponentText("Device[" + (i++) + "] = " + obj));
                            }
                        }
                    }
                    return true;
                }
            }
        }
        if (meta == 2)
        {
            if (player.getHeldItem() != null)
            {
                if (WrenchUtility.isUsableWrench(player, x, y, z))
                {
                    TileEntity tile = world.getTileEntity(x, y, z);
                    if (tile instanceof TileAntenna)
                    {
                        if (!player.isSneaking())
                        {
                            ((TileAntenna) tile).setTowerStatus(((TileAntenna) tile).towerStatus.next());
                            if (!world.isRemote)
                            {
                                player.addChatComponentMessage(new ChatComponentText("Tower Mode set to: " + ((TileAntenna) tile).towerStatus));
                            }
                        }
                        else
                        {
                            ((TileAntenna) tile).setGenerateNetwork(!((TileAntenna) tile).isGeneratingNetwork());
                            if (!world.isRemote)
                            {
                                player.addChatComponentMessage(new ChatComponentText("Tower->GenerateNetwork: " + ((TileAntenna) tile).isGeneratingNetwork()));
                            }
                        }
                        return true;
                    }
                }
            }
            if (!world.isRemote)
            {
                player.openGui(ICBM.INSTANCE, 0, world, x, y, z);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
    {
        if (block instanceof BlockAntennaParts)
        {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TileAntennaPart)
            {
                ((TileAntennaPart) tile).doConnectionUpdate = true;
            }
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta)
    {
        if (!world.isRemote)
        {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TileAntennaPart && ((TileAntennaPart) tile).antennaNetwork != null)
            {
                ((TileAntennaPart) tile).antennaNetwork.split((TileAntennaPart) tile);
                if (((TileAntennaPart) tile).connections.size() > 0)
                {
                    for (TileEntity t : ((TileAntennaPart) tile).connections.values())
                    {
                        if (t instanceof TileAntennaPart)
                        {
                            ((TileAntennaPart) t).doConnectionUpdate = true;
                        }
                    }
                }
            }
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB bounds, List boxes, Entity entity)
    {
        AxisAlignedBB axisalignedbb1 = this.getCollisionBoundingBoxFromPool(world, x, y, z);

        if (axisalignedbb1 != null && bounds.intersectsWith(axisalignedbb1))
        {
            boxes.add(axisalignedbb1);

            int meta = world.getBlockMetadata(x, y, z);
            //Base
            if (meta == 2)
            {
                boxes.add(AxisAlignedBB.getBoundingBox(x + 0.3, y + 0.3, z + 0.3, x + 0.7, y + 1, z + 0.7));
            }
            //TODO implement intersection
            //TODO implement pike
            //TODO implement arm
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
    {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 0 || meta == 1)
        {
            return AxisAlignedBB.getBoundingBox(x + 0.3, y, z + 0.3, x + 0.7, y + 1, z + 0.7);
        }
        else if (meta == 2)
        {
            return AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 0.4, z + 1);
        }
        return AxisAlignedBB.getBoundingBox(x + this.minX, y + this.minY, z + this.minZ, x + this.maxX, y + this.maxY, z + this.maxZ);
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int p_149691_1_, int p_149691_2_)
    {
        return Blocks.hopper.getIcon(p_149691_1_, p_149691_2_);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister p_149651_1_)
    {
    }

    @Override
    public int getRenderType()
    {
        return -1;
    }

    @Override
    public void onRegistered()
    {
        GameRegistry.registerTileEntity(TileAntenna.class, "ICBMxTileAntenna");
        GameRegistry.registerTileEntity(TileAntennaPart.class, "ICBMxTileAntennaPart");
    }

    @Override
    public void onClientRegistered()
    {
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return meta == 2 ? new TileAntenna() : new TileAntennaPart();
    }

    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List list)
    {
        list.add(new ItemStack(item, 1, 0)); //Rod
        list.add(new ItemStack(item, 1, 2)); //Base
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player)
    {
        return new ItemStack(this, 1, getDamageValue(world, x, y, z));
    }

    @Override
    public int getDamageValue(World world, int x, int y, int z)
    {
        //Little faster than calling damageDropped
        return world.getBlockMetadata(x, y, z) == 2 ? 2 : 0;
    }

    @Override
    public int damageDropped(int meta)
    {
        return meta == 2 ? 2 : 0;
    }

    @Override
    public void genRecipes(List<IRecipe> recipes)
    {
        recipes.add(newShapedRecipe(new ItemStack(this, 1, 0), "SCS", "TCT", "WCW", 'S', OreNames.PLATE_IRON, 'C', OreNames.ROD_COPPER, 'T', OreNames.SCREW_IRON, 'W', OreNames.WIRE_IRON));
        recipes.add(newShapedRecipe(new ItemStack(this, 1, 2), "PCP", "SCS", "PIP", 'P', OreNames.PLATE_IRON, 'C', OreNames.ROD_COPPER, 'I', UniversalRecipe.CIRCUIT_T2.get(), 'S', OreNames.SCREW_IRON));
    }
}
