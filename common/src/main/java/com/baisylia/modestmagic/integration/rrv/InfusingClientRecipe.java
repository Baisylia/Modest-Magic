package com.baisylia.modestmagic.integration.rrv;

import cc.cassian.rrv.api.client.RecipeScreenContext;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import com.baisylia.modestmagic.Constants;
import com.baisylia.modestmagic.block.ModBlocks;
import com.baisylia.modestmagic.recipe.custom.InfusingRecipe;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;

public class InfusingClientRecipe implements ReliableClientRecipe {

    private final Identifier id;
    private final SlotContent base;
    private final List<SlotContent> inputs;
    private final List<ItemStack> outputs;

    public InfusingClientRecipe(RecipeHolder<InfusingRecipe> recipeHolder) {
        this.id = recipeHolder.id().identifier();
        InfusingRecipe recipe = recipeHolder.value();
        this.base = SlotContent.of(recipe.getBase());
        this.outputs = recipe.getResults();

        this.inputs = new ArrayList<>();
        this.inputs.add(base);
        recipe.getIngredients().forEach(ing -> this.inputs.add(SlotContent.of(ing)));
    }

    @Override
    public ReliableClientRecipeType getType() {
        return InfusingClientRecipeType.INSTANCE;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public List<SlotContent> getIngredients() {
        return inputs;
    }

    @Override
    public List<SlotContent> getResults() {
        return outputs.stream().map(SlotContent::of).toList();
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
        // Pedestal Count slot
        List<SlotContent> pedestalItems = inputs.subList(1, inputs.size());
        slotFillContext.bindOptionalSlot(0, SlotContent.of(new ItemStack(ModBlocks.PEDESTAL.get(), pedestalItems.size())), RecipeViewMenu.OptionalSlotRenderer.DEFAULT);
    }

    @Override
    public void addRecipeWidgets(RecipeScreenContext context) {
        int cx = 35;
        int cy = getType().getDisplayHeight() / 2;
        int radius = 24;

        List<SlotContent> pedestalItems = inputs.subList(1, inputs.size());
        List<SlotContent> circleItems;

        if (pedestalItems.size() > 6) {
            circleItems = ModestMagicRrvPlugin.consolidateItems(pedestalItems);
        } else {
            circleItems = pedestalItems;
        }

        RotationState state = new RotationState(cx, cy, radius, circleItems.size(), context.recipePosition());

        context.widgets().addRecipeWidget(new RotatingLettersWidget(
                Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/enchanted_letters.png"),
                cx, cy, radius + 6,
                context.recipePosition()));

        HoveringSlotWidget base = new HoveringSlotWidget(this.base, null, cx - 9, cy - 9, 0, context.recipePosition());
        context.widgets().addRecipeWidget(base);

        for (int i = 0; i < circleItems.size(); i++) {
            context.widgets().addRecipeWidget(new RotatingSlotWidget(state, circleItems.get(i), i + 1, context.recipePosition()));
        }

//        widgets.addTexture(EmiTexture.EMPTY_ARROW, cx + radius + 16, cy - 8);
        context.widgets().addRecipeWidget(new HoveringSlotWidget(SlotContent.of(outputs), base, cx + radius + 51, cy - 9, 2, context.recipePosition()));
        context.widgets().addRecipeWidget(new WheelListTooltipWidget(cx, cy, radius, circleItems, context.recipePosition()));
    }
}