package com.baisylia.modestmagic.block;

import com.baisylia.modestmagic.Constants;
import com.baisylia.modestmagic.block.custom.AltarBlock;
import com.baisylia.modestmagic.block.custom.PedestalBlock;
import com.baisylia.modestmagic.item.ModItems;
import com.baisylia.modestmagic.platform.Services;
import com.baisylia.modestmagic.platform.services.IRegistryHelper;
import com.mojang.datafixers.kinds.Const;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class ModBlocks {
    public static final IRegistryHelper.IRegistryProvider<Block> BLOCKS =
            Services.REGISTRIES.create(Registries.BLOCK, Constants.MOD_ID);

    public static final Supplier<Block> ALTAR = registerBlock("altar",
            AltarBlock::new, (BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN).noOcclusion()));

    public static final Supplier<Block> PEDESTAL = registerBlock("pedestal",
            PedestalBlock::new, (BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).noOcclusion()));

    private static Supplier<Block> registerBlock(String name, Function<BlockBehaviour.Properties, Block> blockFactory, BlockBehaviour.Properties settings) {
        ResourceKey<Block> blockKey = keyOfBlock(name);
        Block block = blockFactory.apply(settings.setId(blockKey));
		registerBlockItem(name, block);

		return BLOCKS.register(name, ()-> block);
    }

    private static void registerBlockItem(String name, Block block) {
        // Items need to be registered with a different type of registry key, but the ID
        // can be the same.
        ResourceKey<Item> itemKey = keyOfItem(name);

        BlockItem blockItem = new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix());

        ModItems.ITEMS.register(name, ()-> blockItem);
    }

    private static ResourceKey<Block> keyOfBlock(String name) {
        return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
    }

    private static ResourceKey<Item> keyOfItem(String name) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
    }

    public static void init() {
    }
}