package com.baisylia.modestmagic.recipe.custom;

import com.baisylia.modestmagic.recipe.ModRecipes;
import com.google.gson.*;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class TabletSmithingRecipe implements SmithingRecipe {
    private final ResourceLocation recipeId;
    private final Ingredient template;
    private final Ingredient base;
    private final NonNullList<Enchantment> enchantments;

    public TabletSmithingRecipe(ResourceLocation recipeId, Ingredient template, Ingredient base, NonNullList<Enchantment> enchantments) {
        this.recipeId = recipeId;
        this.template = template;
        this.base = base;
        this.enchantments = enchantments;
    }

    @Override
    public boolean isTemplateIngredient(@NotNull ItemStack stack) {
        return this.template.test(stack);
    }

    @Override
    public boolean isBaseIngredient(@NotNull ItemStack stack) {
        if (!this.base.isEmpty()) {
            return this.base.test(stack);
        }

        if (stack.getItem() instanceof SmithingTemplateItem || this.template.test(stack)) {
            return false;
        }
        return stack.getMaxStackSize() == 1 || stack.is(Items.BOOK) || stack.is(Items.ENCHANTED_BOOK);
    }

    @Override
    public boolean isAdditionIngredient(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public boolean matches(Container inv, @NotNull Level level) {
        ItemStack templateStack = inv.getItem(0);
        ItemStack baseStack = inv.getItem(1);

        if (templateStack.isEmpty() || baseStack.isEmpty()) {
            return false;
        }

        if (!this.template.test(templateStack)) {
            return false;
        }

        if (!isBaseIngredient(baseStack)) {
            return false;
        }

        return !assemble(inv, level.registryAccess()).isEmpty();
    }

    @Override
    public @NotNull ItemStack assemble(Container inv, @NotNull RegistryAccess registryAccess) {
        ItemStack itemstack = inv.getItem(1).copy();
        CompoundTag compoundtag = inv.getItem(1).getTag();

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
                    ResourceLocation enchantId = BuiltInRegistries.ENCHANTMENT.getKey(enchantment);

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

    public Ingredient getTemplate() {
        return template;
    }

    public Ingredient getBase() {
        return base;
    }

    public NonNullList<Enchantment> getEnchantments() {
        return enchantments;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return this.recipeId;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipes.TABLET_SMITHING_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return RecipeType.SMITHING;
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
            Enchantment enchantment = BuiltInRegistries.ENCHANTMENT.get(enchantId);

            if (enchantment == null) {
                throw new JsonSyntaxException("No valid Enchantment name supplied: " + element.getAsString());
            }
            return enchantment;
        }

        @Override
        public @NotNull TabletSmithingRecipe fromJson(@NotNull ResourceLocation recipeId, JsonObject json) {
            Ingredient base = Ingredient.EMPTY;
            if (json.has("base")) {
                base = Ingredient.fromJson(json.get("base"));
            }

            Ingredient template = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "template"));
            NonNullList<Enchantment> enchantmentList = readEnchantments(GsonHelper.getAsJsonArray(json, "enchantments"));

            if (enchantmentList.isEmpty()) {
                throw new JsonParseException("No enchantments provided for tablet smithing recipe");
            }

            return new TabletSmithingRecipe(recipeId, template, base, enchantmentList);
        }

        @Override
        public @NotNull TabletSmithingRecipe fromNetwork(@NotNull ResourceLocation recipeId, @NotNull FriendlyByteBuf buffer) {
            Ingredient template = Ingredient.fromNetwork(buffer);
            Ingredient base = Ingredient.fromNetwork(buffer);

            int k = buffer.readVarInt();
            NonNullList<Enchantment> enchantmentList = NonNullList.create();

            for (int j = 0; j < k; j++) {
                Enchantment enchantment = BuiltInRegistries.ENCHANTMENT.get(buffer.readResourceLocation());
                if (enchantment != null) {
                    enchantmentList.add(enchantment);
                }
            }

            return new TabletSmithingRecipe(recipeId, template, base, enchantmentList);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, TabletSmithingRecipe recipe) {
            recipe.template.toNetwork(buffer);
            recipe.base.toNetwork(buffer);

            buffer.writeVarInt(recipe.enchantments.size());
            for (Enchantment enchantment : recipe.enchantments) {
                ResourceLocation key = BuiltInRegistries.ENCHANTMENT.getKey(enchantment);
                if (key != null) {
                    buffer.writeResourceLocation(key);
                }
            }
        }
    }
}