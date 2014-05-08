package icbm.sentry.items.weapons.conventional;

import icbm.core.prefab.item.ItemICBMBase;
import icbm.sentry.items.weapons.IItemAmmunition;

/**
 * I dont want this extending ItemAmmo please.
 * @author Archtikz
 */
public class ItemConventionalClip extends ItemICBMBase implements IItemAmmunition {

	public ItemConventionalClip(int id) {
		super(id, "conventionalClip");
		setMaxStackSize(1);
	}

}
