package com.baisylia.modestmagic.integration.rrv;

import cc.cassian.rrv.api.client.RecipeScreenContext;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import cc.cassian.rrv.common.rendering.RrvGuiRenderHelper;
import com.baisylia.modestmagic.Constants;
import com.baisylia.modestmagic.block.ModBlocks;
import com.baisylia.modestmagic.recipe.custom.SummoningRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.storage.TagValueInput;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class SummoningClientRecipe extends AbstractModestMagicClientRecipe {

    private final Identifier id;
    private final SlotContent base;
    private final List<SlotContent> inputs;

    private final List<SummoningRecipe.SummonOutcome> outcomes;
    private List<Entity> cachedEntities = null;

    public SummoningClientRecipe(RecipeHolder<SummoningRecipe> recipeHolder) {
        this.id = recipeHolder.id().identifier();
        SummoningRecipe recipe = recipeHolder.value();
        this.base = SlotContent.of(recipe.getBase());

        this.inputs = new ArrayList<>();
        this.inputs.add(base);
        recipe.getIngredients().forEach(ing -> this.inputs.add(SlotContent.of(ing)));

        this.outcomes = recipe.getOutcomes();
    }

    private void initEntities() {
        if (cachedEntities == null) {
            cachedEntities = new ArrayList<>();
            if (Minecraft.getInstance().level != null) {
                for (SummoningRecipe.SummonOutcome outcome : outcomes) {
                    Entity entity = outcome.entity().create(Minecraft.getInstance().level, EntitySpawnReason.LOAD);
                    if (entity != null) {
                        if (!outcome.nbt().isEmpty()) entity.load(TagValueInput.create(ProblemReporter.DISCARDING, Minecraft.getInstance().level.registryAccess(), outcome.nbt()));
                        cachedEntities.add(entity);
                    }
                }
            }
        }
    }

	@Override
	public ReliableClientRecipeType getType() {
		return SummoningClientRecipeType.INSTANCE;
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
		return List.of();
	}

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
        // Pedestal Count slot
        slotFillContext.bindOptionalSlot(0, SlotContent.of(new ItemStack(ModBlocks.PEDESTAL.get(), inputs.size())), RecipeViewMenu.OptionalSlotRenderer.DEFAULT);
    }

    @Override
    public void initRecipe(RecipeViewScreen screen, RecipePosition recipePosition, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {

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

        RotationState state = new RotationState(cx, cy, radius, circleItems.size(), recipePosition);

        screen.addRecipeWidget(new RotatingLettersWidget(
                Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/enchanted_letters.png"),
                cx, cy, radius + 6, recipePosition
        ));

        screen.addRecipeWidget(new HoveringSlotWidget(base, null, cx - 9, cy - 9, 0, recipePosition));

        for (int i = 0; i < circleItems.size(); i++) {
            screen.addRecipeWidget(new RotatingSlotWidget(state, circleItems.get(i), i + 1, recipePosition));
        }

//        widgets.addTexture(EmiTexture.EMPTY_ARROW, cx + radius + 16, cy - 8);



        initEntities();

        screen.addRecipeWidget(new WheelListTooltipWidget(cx, cy, radius, circleItems, recipePosition));
    }

    @Override
    public void renderRecipe(RecipeScreenContext context) {
        super.renderRecipe(context);
        int cx = 35;
        int cy = getType().getDisplayHeight() / 2;
        int radius = 24;
        int slotX = cx + radius - 4;
        int slotY = cy - 24;
        if (cachedEntities != null && !cachedEntities.isEmpty()) {
            int index = (int) ((System.currentTimeMillis() / 1500L) % cachedEntities.size());
            Entity currentEntity = cachedEntities.get(index);

            if (currentEntity instanceof LivingEntity living) {
                float maxDim = Math.max(living.getBbWidth(), living.getBbHeight());
                float scale = 24.0f / Math.max(maxDim, 0.5f);

                float centerX = slotX + 9;
                float centerY = slotY + 18;
                float lookX = centerX - context.mouseX();
                float lookY = centerY - 10 - context.mouseY();
                float f2 = (float) Math.atan(lookX / 40.0F);
                float f3 = (float) Math.atan(lookY / 40.0F);

                Quaternionf pose = (new Quaternionf()).rotateZ((float) Math.PI);
                Quaternionf camera = (new Quaternionf()).rotateX(f3 * 20.0F * ((float) Math.PI / 180F));
                pose.mul(camera);

                float yBodyRot = living.yBodyRot;
                float yRot = living.getYRot();
                float xRot = living.getXRot();
                float yHeadRotO = living.yHeadRotO;
                float yHeadRot = living.yHeadRot;

                living.yBodyRot = 180.0F + f2 * 20.0F;
                living.setYRot(180.0F + f2 * 40.0F);
                living.setXRot(-f3 * 20.0F);
                living.yHeadRot = living.getYRot();
                living.yHeadRotO = living.getYRot();

                RrvGuiRenderHelper.renderEntityOnScreen(context.guiGraphics(), living,
                        context.recipePosition().left() + 105, context.recipePosition().top() + 20,
                        context.recipePosition().left() + 105 + 28, context.recipePosition().top() + 20 + 28,
                        scale,
                        new Vector3f(0.0F, (28.0F / scale / 2.0F), 0.0F),  // translation
                        pose, // rotation
                        null);

                living.yBodyRot = yBodyRot;
                living.setYRot(yRot);
                living.setXRot(xRot);
                living.yHeadRotO = yHeadRotO;
                living.yHeadRot = yHeadRot;
            }
        }
    }
}