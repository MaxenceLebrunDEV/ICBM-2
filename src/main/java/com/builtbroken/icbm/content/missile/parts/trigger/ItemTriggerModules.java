package com.builtbroken.icbm.content.missile.parts.trigger;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.missile.parts.trigger.impact.ImpactTrigger;
import com.builtbroken.jlib.data.Colors;
import com.builtbroken.jlib.data.science.units.UnitDisplay;
import com.builtbroken.mc.api.modules.IModule;
import com.builtbroken.mc.prefab.module.ItemAbstractModule;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.List;

/**
 * Item for the modules to be contained in an inventory
 * Created by robert on 12/28/2014.
 */
public class ItemTriggerModules extends ItemAbstractModule
{
    public ItemTriggerModules()
    {
        this.setHasSubtypes(true);
        this.setUnlocalizedName(ICBM.PREFIX + "triggers");
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b)
    {
        //TODO translate
        IModule module = getModule(stack);
        if (module instanceof ImpactTrigger)
        {
            list.add(Colors.RED + "Not implemented");
            list.add("Kinetic Energy Trigger Values");
            list.add("Min: " + new UnitDisplay(UnitDisplay.Unit.JOULES, ((ImpactTrigger) module).getMinimalForce(), true));
            list.add("Max: " + new UnitDisplay(UnitDisplay.Unit.JOULES, ((ImpactTrigger) module).getMaximalForce(), true));
        }
    }

    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        for (Triggers engine : Triggers.values())
        {
            list.add(engine.newModuleStack());
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta)
    {
        return Triggers.get(meta) != null ? Triggers.get(meta).icon : this.itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        for (Triggers trigger : Triggers.values())
        {
            trigger.icon = reg.registerIcon(ICBM.PREFIX + trigger.moduleName);
        }
    }

    @Override
    public IModule newModule(ItemStack insert)
    {
        return Triggers.get(insert).buildModule(insert);
    }
}
