package com.baisylia.modestmagic.block.entity;

import com.baisylia.modestmagic.Constants;
import com.baisylia.modestmagic.block.ModBlocks;
import com.baisylia.modestmagic.block.entity.custom.AltarBlockEntity;
import com.baisylia.modestmagic.block.entity.custom.PedestalBlockEntity;
import com.baisylia.modestmagic.platform.Services;
import com.baisylia.modestmagic.platform.services.IRegistryHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Set;
import java.util.function.Supplier;

public class ModBlockEntities {

    public static final IRegistryHelper.IRegistryProvider<BlockEntityType<?>> BLOCK_ENTITIES =
            Services.REGISTRIES.create(Registries.BLOCK_ENTITY_TYPE, Constants.MOD_ID);

    public static final Supplier<BlockEntityType<AltarBlockEntity>> ALTAR_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("altar_block_entity", () ->
                    new BlockEntityType<>(AltarBlockEntity::new, Set.of(ModBlocks.ALTAR.get())).build(key("altar_block_entity")));

    public static final Supplier<BlockEntityType<PedestalBlockEntity>> PEDESTAL_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("pedestal_block_entity", () ->
                    new BlockEntityType<>(PedestalBlockEntity::new, Set.of(ModBlocks.PEDESTAL.get())).build(key("pedestal_block_entity")));

	private static ResourceKey<BlockEntityType<?>> key(String name) {
		return ResourceKey.create(Registries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
	}

    public static void init() {
    }
}