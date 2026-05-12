package com.baisylia.modestmagic.integration.emi;

import com.baisylia.modestmagic.Constants;
import com.baisylia.modestmagic.block.ModBlocks;
import com.baisylia.modestmagic.recipe.custom.SummoningRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;

public class SummoningEmiRecipe implements EmiRecipe {

    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/emi_background.png");
    private final ResourceLocation id;
    private final EmiIngredient base;
    private final List<EmiIngredient> inputs;
    private final List<Entity> cachedEntities;

    public SummoningEmiRecipe(RecipeHolder<SummoningRecipe> recipeHolder) {
        this.id = recipeHolder.id();
        SummoningRecipe recipe = recipeHolder.value();
        this.base = EmiIngredient.of(recipe.getBase());

        this.inputs = new ArrayList<>();
        this.inputs.add(base);
        recipe.getIngredients().forEach(ing -> this.inputs.add(EmiIngredient.of(ing)));

        this.cachedEntities = new ArrayList<>();
        if (Minecraft.getInstance().level != null) {
            for (SummoningRecipe.SummonOutcome outcome : recipe.getOutcomes()) {
                Entity entity = outcome.entity().create(Minecraft.getInstance().level);
                if (entity != null && !outcome.nbt().isEmpty()) entity.load(outcome.nbt());
                if (entity != null) cachedEntities.add(entity);
            }
        }
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return ModestMagicEmiPlugin.SUMMONING;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return inputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of();
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

        List<EmiIngredient> pedestalItems = inputs.subList(1, inputs.size());
        List<EmiIngredient> circleItems;

        if (pedestalItems.size() > 6) {
            circleItems = ModestMagicEmiPlugin.consolidateItems(pedestalItems);
        } else {
            circleItems = pedestalItems;
        }

        RotationState state = new RotationState(cx, cy, radius, circleItems.size());

        widgets.add(new RotatingLettersWidget(
                ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/enchanted_letters.png"),
                cx, cy, radius + 6
        ));

        widgets.add(new HoveringSlotWidget(base, cx - 9, cy - 9, 0));

        for (int i = 0; i < circleItems.size(); i++) {
            widgets.add(new RotatingSlotWidget(state, circleItems.get(i), i + 1));
        }

        widgets.addSlot(EmiStack.of(new ItemStack(ModBlocks.PEDESTAL.get(), pedestalItems.size())), getDisplayWidth() - 18, getDisplayHeight() - 18).drawBack(true);

        widgets.addTexture(EmiTexture.EMPTY_ARROW, cx + radius + 16, cy - 8);

        int slotX = cx + radius - 4;
        int slotY = cy - 24;

        widgets.addDrawable(slotX, slotY, 18, 18, (guiGraphics, mouseX, mouseY, delta) -> {
            if (!cachedEntities.isEmpty()) {
                int index = (int) ((System.currentTimeMillis() / 1500L) % cachedEntities.size());
                Entity currentEntity = cachedEntities.get(index);

                if (currentEntity instanceof LivingEntity living) {
                    double width = living.getBbWidth();
                    double height = living.getBbHeight();

                    float maxDim = (float) Math.max(width, height);
                    float scale = 24.0f / Math.max(maxDim, 0.5f);

                    InventoryScreen.renderEntityInInventoryFollowsMouse(
                            guiGraphics,
                            slotX, slotY, slotX + 18, slotY + 18, (int) scale,
                            0.05f, (float) mouseX, (float) mouseY, living
                    );
                }
            }
        });

        widgets.add(new WheelListTooltipWidget(cx, cy, radius, circleItems));
    }
}