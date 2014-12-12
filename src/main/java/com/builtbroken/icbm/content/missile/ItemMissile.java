package com.builtbroken.icbm.content.missile;

import resonant.lib.world.explosive.ExplosiveItemUtility;
import resonant.api.items.IExplosiveItem;
import resonant.lib.world.explosive.ExplosiveRegistry;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import resonant.api.explosive.IExplosive;

public class ItemMissile extends Item implements IExplosiveItem
{
    public ItemMissile()
    {
        this.setUnlocalizedName("missile");
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setMaxStackSize(1);
    }

    @Override
    public int getMetadata(int damage)
    {
        return damage;
    }

    @Override
    public String getUnlocalizedName()
    {
        return "icbm.missile";
    }

    public ItemStack getStackFor(String ex)
    {
        return getStackFor(ExplosiveRegistry.get(ex));
    }

    public ItemStack getStackFor(IExplosive ex)
    {
        if(ex != null && ExplosiveRegistry.isRegistered(ex))
        {
            ItemStack stack = new ItemStack(this);
            setExplosive(stack, ex);
            return stack;
        }
        return null;
    }

    @Override
    public IExplosive getExplosive(ItemStack itemStack)
    {
       return ExplosiveItemUtility.getExplosive(itemStack);
    }

    public void setExplosive(ItemStack itemStack, IExplosive ex)
    {
        ExplosiveItemUtility.setExplosive(itemStack, ex);
    }

    @Override
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List)
    {
       ExplosiveItemUtility.getSubItems(par1, par3List);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean bool)
    {
        super.addInformation(stack, player, list, bool);
        ExplosiveItemUtility.addInformation(stack, player, list, bool);
    }
}