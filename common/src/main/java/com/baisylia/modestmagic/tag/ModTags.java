package com.baisylia.modestmagic.tag;

import com.baisylia.modestmagic.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags {

    public static class Items {
        public static final TagKey<Item> TABLETS = tag("tablets");
        public static final TagKey<Item> TABLET_ENCHANTABLE = tag("enchantable/tablets");

        private static TagKey<Item> tag(String name) {
            return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name));
        }
    }
}
