package com.baisylia.modestmagic.integration.emi;

import com.baisylia.modestmagic.Constants;
import com.baisylia.modestmagic.block.ModBlocks;
import com.baisylia.modestmagic.recipe.custom.EnchantingRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EnchantingEmiRecipe implements EmiRecipe {

    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/emi_background.png");
    private final ResourceLocation id;
    private final List<EmiIngredient> inputs;
    private final EmiIngredient baseIngredient;
    private final List<EmiStack> outputs;

    public EnchantingEmiRecipe(RecipeHolder<EnchantingRecipe> recipeHolder) {
        this.id = recipeHolder.id();
        EnchantingRecipe recipe = recipeHolder.value();
        this.inputs = recipe.getIngredients().stream().map(EmiIngredient::of).collect(Collectors.toList());

        List<EmiStack> validBases = new ArrayList<>();
        List<EmiStack> validOutputs = new ArrayList<>();

        Registry<Enchantment> enchantRegistry = Minecraft.getInstance().level.registryAccess().registryOrThrow(Registries.ENCHANTMENT);

        List<List<Holder<Enchantment>>> resolvedPools = new ArrayList<>();
        for (List<ResourceKey<Enchantment>> poolKeys : recipe.getEnchantmentPools()) {
            List<Holder<Enchantment>> pool = new ArrayList<>();
            for (ResourceKey<Enchantment> key : poolKeys) {
                enchantRegistry.getHolder(key).ifPresent(pool::add);
            }
            if (!pool.isEmpty()) resolvedPools.add(pool);
        }

        // test all registered items
        for (Item item : BuiltInRegistries.ITEM) {
            ItemStack testStack = new ItemStack(item);
            boolean isValid = false;

            for (List<Holder<Enchantment>> pool : resolvedPools) {
                boolean poolValid = true;
                for (Holder<Enchantment> e : pool) {
                    if (!e.value().canEnchant(testStack) && !testStack.is(Items.BOOK)) {
                        poolValid = false;
                        break;
                    }
                }
                if (poolValid) {
                    isValid = true;
                    break;
                }
            }

            if (isValid) {
                validBases.add(EmiStack.of(testStack));

                for (List<Holder<Enchantment>> pool : resolvedPools) {
                    ItemStack outStack = testStack.copy();
                    if (outStack.getItem() == Items.BOOK) {
                        outStack = new ItemStack(Items.ENCHANTED_BOOK);
                    }

                    ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
                    for (Holder<Enchantment> e : pool) {
                        enchantments.set(e, 1);
                    }

                    EnchantmentHelper.setEnchantments(outStack, enchantments.toImmutable());
                    validOutputs.add(EmiStack.of(outStack));
                }
            }
        }

        // fallback
        if (validBases.isEmpty()) {
            validBases.add(EmiStack.of(Items.BOOK));
            ItemStack out = new ItemStack(Items.ENCHANTED_BOOK);
            ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);

            if (!resolvedPools.isEmpty()) {
                for (Holder<Enchantment> e : resolvedPools.getFirst()) enchantments.set(e, 1);
            }
            EnchantmentHelper.setEnchantments(out, enchantments.toImmutable());
            validOutputs.add(EmiStack.of(out));
        }

        this.baseIngredient = EmiIngredient.of(validBases);
        this.outputs = validOutputs;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return ModestMagicEmiPlugin.ENCHANTING;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        List<EmiIngredient> allInputs = new ArrayList<>();
        allInputs.add(baseIngredient);
        allInputs.addAll(inputs);
        return allInputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return outputs;
    }

    @Override
    public int getDisplayWidth() {
        return 140;
    }

    @Override
    public int getDisplayHeight() {
        return 80;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(BACKGROUND, 0, 0, getDisplayWidth(), getDisplayHeight(), 0, 0);

        int cx = 35;
        int cy = getDisplayHeight() / 2;
        int radius = 24;

        List<EmiIngredient> circleItems;

        if (inputs.size() > 6) {
            circleItems = ModestMagicEmiPlugin.consolidateItems(inputs);
        } else {
            circleItems = inputs;
        }

        RotationState state = new RotationState(cx, cy, radius, circleItems.size());

        widgets.add(new RotatingLettersWidget(
                ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/enchanted_letters.png"),
                cx, cy, radius + 6
        ));

        widgets.add(new HoveringSlotWidget(baseIngredient, cx - 9, cy - 9, 0));

        for (int i = 0; i < circleItems.size(); i++) {
            widgets.add(new RotatingSlotWidget(state, circleItems.get(i), i + 1));
        }

        // Pedestal Count slot
        widgets.addSlot(EmiStack.of(new ItemStack(ModBlocks.PEDESTAL.get(), inputs.size())), getDisplayWidth() - 18, getDisplayHeight() - 18).drawBack(true);

        // Arrow and cycling enchanted item
        widgets.addTexture(EmiTexture.EMPTY_ARROW, cx + radius + 16, cy - 8);
        widgets.add(new HoveringSlotWidget(EmiIngredient.of(outputs), cx + radius + 51, cy - 9, 2)).recipeContext(this);

        widgets.add(new WheelListTooltipWidget(cx, cy, radius, circleItems));
    }
}