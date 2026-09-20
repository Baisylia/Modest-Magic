package com.baisylia.modestmagic.recipe.custom;

import com.baisylia.modestmagic.platform.Services;
import com.baisylia.modestmagic.recipe.ModRecipes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public record TabletSmithingRecipe(Ingredient template, Ingredient base, Optional<Ingredient> addition,
                                   NonNullList<ResourceKey<Enchantment>> enchantments) implements SmithingRecipe {

    public TabletSmithingRecipe(Ingredient template, Ingredient base, NonNullList<ResourceKey<Enchantment>> enchantments) {
        this(template, base, Optional.empty(), enchantments);
    }

    @Override
    public Optional<Ingredient> templateIngredient() {
        return Optional.ofNullable(this.template);
    }

    @Override
    public Ingredient baseIngredient() {
        return this.base;
    }

    @Override
    public Optional<Ingredient> additionIngredient() {
        return this.addition;
    }

    @Override
    public boolean matches(SmithingRecipeInput inv, @NotNull Level level) {
        ItemStack templateStack = inv.template();
        ItemStack baseStack = inv.base();
        ItemStack additionStack = inv.addition();

        if (templateStack.isEmpty() || baseStack.isEmpty()) {
            return false;
        }
        if (!this.template.test(templateStack)) {
            return false;
        }
        if (!this.base.isEmpty() && !this.base.test(baseStack)) {
            return false;
        }
        if (!Ingredient.testOptionalIngredient(this.addition, additionStack)) {
            return false;
        }

        return !assemble(inv).isEmpty();
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public @NotNull ItemStack assemble(SmithingRecipeInput inv) {
        ItemStack itemstack = inv.base().copy();
        if (itemstack.isEmpty()) return ItemStack.EMPTY;
        if (itemstack.is(Items.BOOK)) {
            itemstack = new ItemStack(Items.ENCHANTED_BOOK, itemstack.getCount());
        }

        var enchantRegistry = Services.PLATFORM.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        ItemEnchantments existing = EnchantmentHelper.getEnchantmentsForCrafting(itemstack);
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(existing);

        boolean itemEnchanted = false;

        for (ResourceKey<Enchantment> key : enchantments) {
            Optional<Holder.Reference<Enchantment>> opt = enchantRegistry.get(key);
            if (opt.isEmpty()) continue;
            Holder<Enchantment> enchantHolder = opt.get();
            Enchantment enchantment = enchantHolder.value();

            if ((Services.PLATFORM.isPrimaryEnchantItem(itemstack, enchantHolder) || itemstack.is(Items.ENCHANTED_BOOK)) && areEnchantsCompatible(existing, enchantHolder)) {
                int currentLevel = mutable.getLevel(enchantHolder);
                int targetLevel = currentLevel + 1;
                if (targetLevel <= enchantment.getMaxLevel()) {
                    mutable.set(enchantHolder, targetLevel);
                    itemEnchanted = true;
                }
            }
        }

        if (itemEnchanted) {
            EnchantmentHelper.setEnchantments(itemstack, mutable.toImmutable());
            return itemstack;
        }

        return ItemStack.EMPTY;
    }

    private boolean areEnchantsCompatible(ItemEnchantments existing, Holder<Enchantment> enchant) {
        for (Holder<Enchantment> e : existing.keySet()) {
            if (!e.equals(enchant) && !Enchantment.areCompatible(e, enchant)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public RecipeSerializer<? extends SmithingRecipe> getSerializer() {
        return ModRecipes.TABLET_SMITHING_SERIALIZER.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.createFromOptionals(List.of(Optional.of(template), Optional.of(base), addition));
    }

    public static class Serializer {

        public static final MapCodec<TabletSmithingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC.fieldOf("template").forGetter(r -> r.template),
                Ingredient.CODEC.fieldOf("base").forGetter(r -> r.base),
                Ingredient.CODEC.optionalFieldOf("addition").forGetter(r -> r.addition),
                ResourceKey.codec(Registries.ENCHANTMENT).listOf().fieldOf("enchantments").forGetter(r -> r.enchantments)
        ).apply(inst, (template, base, addition, enchantments) -> {
            NonNullList<ResourceKey<Enchantment>> list = NonNullList.create();
            list.addAll(enchantments);
            return new TabletSmithingRecipe(template, base, addition, list);
        }));

        public static final StreamCodec<RegistryFriendlyByteBuf, TabletSmithingRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, r -> r.template,
                Ingredient.CONTENTS_STREAM_CODEC, r -> r.base,
                Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC, r -> r.addition,
                ResourceKey.streamCodec(Registries.ENCHANTMENT).apply(ByteBufCodecs.list()), r -> r.enchantments,
                (template, base, addition, enchantments) -> {
                    NonNullList<ResourceKey<Enchantment>> list = NonNullList.create();
                    list.addAll(enchantments);
                    return new TabletSmithingRecipe(template, base, addition, list);
                }
        );

        public static final RecipeSerializer<TabletSmithingRecipe> INSTANCE = new RecipeSerializer<>(CODEC, STREAM_CODEC);
    }
}