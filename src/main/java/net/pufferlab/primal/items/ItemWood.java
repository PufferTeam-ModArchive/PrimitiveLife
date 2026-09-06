package net.pufferlab.primal.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;
import net.pufferlab.primal.Registry;
import net.pufferlab.primal.utils.WoodType;

public class ItemWood extends ItemMeta {

    public WoodType[] woodType;
    public IIcon icon;

    public ItemWood(WoodType[] woodType, String name) {
        super(WoodType.getNames(woodType), name);
        this.woodType = woodType;
        for (int i = 0; i < woodType.length; i++) {
            woodType[i].setBarkItem(this, i);
        }
    }

    @Override
    public CreativeTabs getCreativeTab() {
        return Registry.creativeTabWorld;
    }
}
