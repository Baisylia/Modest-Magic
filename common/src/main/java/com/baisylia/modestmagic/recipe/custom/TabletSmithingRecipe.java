package com.baisylia.modestmagic.recipe.custom;

import com.baisylia.modestmagic.recipe.ModRecipes;
import com.google.gson.*;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.UpgradeRecipe;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Map;

public class TabletSmithingRecipe extends UpgradeRecipe {
    private final ResourceLocation recipeId;
    private final Ingredient base;
    private final Ingredient addition;
    private final NonNullList<Enchantment> enchantments;

    public TabletSmithingRecipe(ResourceLocation recipeId, Ingredient base, Ingredient addition, NonNullList<Enchantment> enchantments) {
        super(recipeId, base, addition, ItemStack.EMPTY);
        this.recipeId = recipeId;
        this.base = base;
        this.addition = addition;
        this.enchantments = enchantments;
    }

    @Override
    public boolean matches(Container inv, Level level) {
        ItemStack baseStack = inv.getItem(0);
        ItemStack additionStack = inv.getItem(1);

        if (baseStack.isEmpty() || additionStack.isEmpty()) {
            return false;
        }

        if (!this.addition.test(additionStack)) {
            return false;
        }

        if (!this.base.isEmpty() && !this.base.test(baseStack)) {
            return false;
        }

        return !assemble(inv).isEmpty();
    }

    @Override
    public ItemStack assemble(Container inv) {
        ItemStack itemstack = inv.getItem(0).copy();
        CompoundTag compoundtag = inv.getItem(0).getTag();

        if (itemstack.isEmpty()) return ItemStack.EMPTY;

        if (compoundtag != null) {
            itemstack.setTag(compoundtag.copy());
        }

        boolean itemEnchanted = false;

        outerLoop:
        for (Enchantment enchantment : enchantments) {
            if (enchantment.canEnchant(itemstack) && areEnchantsCompatible(itemstack, enchantment)) {
                ListTag nbtList = itemstack.getEnchantmentTags();

                for (int i = 0; i < nbtList.size(); i++) {
                    CompoundTag idTag = nbtList.getCompound(i);
                    ResourceLocation enchantId = ForgeRegistries.ENCHANTMENTS.getKey(enchantment);

                    if (enchantId != null && idTag.getString("id").equals(enchantId.toString())) {
                        int targetLevel = idTag.getInt("lvl") + 1;
                        if (targetLevel > enchantment.getMaxLevel()) {
                            continue outerLoop;
                        }
                        itemEnchanted = true;
                        nbtList.remove(i);
                        itemstack.enchant(enchantment, targetLevel);
                        continue outerLoop;
                    }
                }

                itemEnchanted = true;
                itemstack.enchant(enchantment, 1);
            }
        }

        return itemEnchanted ? itemstack : ItemStack.EMPTY;
    }

    private boolean areEnchantsCompatible(ItemStack itemStack, Enchantment enchant) {
        Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemStack);
        for (Enchantment e : map.keySet()) {
            if (enchant != e && !enchant.isCompatibleWith(e)) {
                return false;
            }
        }
        return true;
    }

    public Ingredient getBase() {
        return base;
    }

    public Ingredient getAddition() {
        return addition;
    }

    public NonNullList<Enchantment> getEnchantments() {
        return enchantments;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return this.recipeId;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.TABLET_SMITHING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.SMITHING;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public static class Serializer implements RecipeSerializer<TabletSmithingRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        private static NonNullList<Enchantment> readEnchantments(JsonArray enchantmentArray) {
            NonNullList<Enchantment> enchantments = NonNullList.create();
            for (int i = 0; i < enchantmentArray.size(); ++i) {
                enchantments.add(parseEnchantment(enchantmentArray.get(i)));
            }
            return enchantments;
        }

        private static Enchantment parseEnchantment(JsonElement element) {
            if (element.isJsonArray()) {
                throw new JsonSyntaxException("Expected string to be a single Enchantment");
            }
            ResourceLocation enchantId = ResourceLocation.tryParse(element.getAsString());
            Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(enchantId);

            if (enchantment == null) {
                throw new JsonSyntaxException("No valid Enchantment name supplied: " + element.getAsString());
            }
            return enchantment;
        }

        @Override
        public TabletSmithingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            Ingredient base = Ingredient.EMPTY;
            if (json.has("base")) {
                base = Ingredient.fromJson(json.get("base"));
            }

            Ingredient addition = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "addition"));
            NonNullList<Enchantment> enchantmentList = readEnchantments(GsonHelper.getAsJsonArray(json, "enchantments"));

            if (enchantmentList.isEmpty()) {
                throw new JsonParseException("No enchantments provided for tablet smithing recipe");
            }

            return new TabletSmithingRecipe(recipeId, base, addition, enchantmentList);
        }

        @Nullable
        @Override
        public TabletSmithingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            Ingredient base = Ingredient.fromNetwork(buffer);
            Ingredient addition = Ingredient.fromNetwork(buffer);

            int k = buffer.readVarInt();
            NonNullList<Enchantment> enchantmentList = NonNullList.create();

            for (int j = 0; j < k; j++) {
                Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(buffer.readResourceLocation());
                if (enchantment != null) {
                    enchantmentList.add(enchantment);
                }
            }

            return new TabletSmithingRecipe(recipeId, base, addition, enchantmentList);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, TabletSmithingRecipe recipe) {
            recipe.base.toNetwork(buffer);
            recipe.addition.toNetwork(buffer);

            buffer.writeVarInt(recipe.enchantments.size());
            for (Enchantment enchantment : recipe.enchantments) {
                ResourceLocation key = ForgeRegistries.ENCHANTMENTS.getKey(enchantment);
                if (key != null) {
                    buffer.writeResourceLocation(key);
                }
            }
        }
    }
}