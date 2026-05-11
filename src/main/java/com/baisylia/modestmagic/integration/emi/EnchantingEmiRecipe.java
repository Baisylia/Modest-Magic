package com.baisylia.modestmagic.integration.emi;

import com.baisylia.modestmagic.block.ModBlocks;
import com.baisylia.modestmagic.recipe.custom.EnchantingRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EnchantingEmiRecipe implements EmiRecipe {

    private static final ResourceLocation BACKGROUND = new ResourceLocation("modestmagic", "textures/gui/emi_background.png");
    private final ResourceLocation id;
    private final List<EmiIngredient> inputs;
    private final EmiIngredient baseIngredient;
    private final List<EmiStack> outputs;

    public EnchantingEmiRecipe(EnchantingRecipe recipe) {
        this.id = recipe.getId();
        this.inputs = recipe.getIngredients().stream().map(EmiIngredient::of).collect(Collectors.toList());

        List<EmiStack> validBases = new ArrayList<>();
        List<EmiStack> validOutputs = new ArrayList<>();

        // test all registered items
        for (Item item : ForgeRegistries.ITEMS) {
            ItemStack testStack = new ItemStack(item);
            boolean isValid = false;

            for (List<Enchantment> pool : recipe.getEnchantmentPools()) {
                boolean poolValid = true;
                for (Enchantment e : pool) {
                    if (!e.canEnchant(testStack) && !testStack.is(Items.BOOK)) {
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

                for (List<Enchantment> pool : recipe.getEnchantmentPools()) {
                    ItemStack outStack = testStack.copy();
                    if (outStack.getItem() == Items.BOOK) {
                        outStack = new ItemStack(Items.ENCHANTED_BOOK);
                    }
                    Map<Enchantment, Integer> appliedEnchantments = new HashMap<>();
                    for (Enchantment e : pool) appliedEnchantments.put(e, 1);
                    EnchantmentHelper.setEnchantments(appliedEnchantments, outStack);
                    validOutputs.add(EmiStack.of(outStack));
                }
            }
        }

        // fallback
        if (validBases.isEmpty()) {
            validBases.add(EmiStack.of(Items.BOOK));
            ItemStack out = new ItemStack(Items.ENCHANTED_BOOK);
            Map<Enchantment, Integer> map = new HashMap<>();
            if (!recipe.getEnchantmentPools().isEmpty()) {
                for (Enchantment e : recipe.getEnchantmentPools().get(0)) map.put(e, 1);
            }
            EnchantmentHelper.setEnchantments(map, out);
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
                new ResourceLocation("modestmagic", "textures/gui/enchanted_letters.png"),
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