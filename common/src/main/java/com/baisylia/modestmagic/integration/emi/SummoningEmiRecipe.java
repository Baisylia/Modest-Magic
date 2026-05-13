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
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class SummoningEmiRecipe implements EmiRecipe {

    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/emi_background.png");
    private final ResourceLocation id;
    private final EmiIngredient base;
    private final List<EmiIngredient> inputs;

    private final List<SummoningRecipe.SummonOutcome> outcomes;
    private List<Entity> cachedEntities = null;

    public SummoningEmiRecipe(RecipeHolder<SummoningRecipe> recipeHolder) {
        this.id = recipeHolder.id();
        SummoningRecipe recipe = recipeHolder.value();
        this.base = EmiIngredient.of(recipe.getBase());

        this.inputs = new ArrayList<>();
        this.inputs.add(base);
        recipe.getIngredients().forEach(ing -> this.inputs.add(EmiIngredient.of(ing)));

        this.outcomes = recipe.getOutcomes();
    }

    private void initEntities() {
        if (cachedEntities == null) {
            cachedEntities = new ArrayList<>();
            if (Minecraft.getInstance().level != null) {
                for (SummoningRecipe.SummonOutcome outcome : outcomes) {
                    Entity entity = outcome.entity().create(Minecraft.getInstance().level);
                    if (entity != null) {
                        if (!outcome.nbt().isEmpty()) entity.load(outcome.nbt());
                        cachedEntities.add(entity);
                    }
                }
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
            initEntities();

            if (cachedEntities != null && !cachedEntities.isEmpty()) {
                int index = (int) ((System.currentTimeMillis() / 1500L) % cachedEntities.size());
                Entity currentEntity = cachedEntities.get(index);

                if (currentEntity instanceof LivingEntity living) {
                    float maxDim = Math.max(living.getBbWidth(), living.getBbHeight());
                    float scale = 24.0f / Math.max(maxDim, 0.5f);

                    float centerX = slotX + 9;
                    float centerY = slotY + 18;
                    float lookX = centerX - mouseX;
                    float lookY = centerY - 10 - mouseY;
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

                    InventoryScreen.renderEntityInInventory(
                            guiGraphics,
                            centerX, centerY,
                            scale,
                            new Vector3f(0, 0, 0),
                            pose,
                            camera,
                            living
                    );

                    living.yBodyRot = yBodyRot;
                    living.setYRot(yRot);
                    living.setXRot(xRot);
                    living.yHeadRotO = yHeadRotO;
                    living.yHeadRot = yHeadRot;
                }
            }
        });

        widgets.add(new WheelListTooltipWidget(cx, cy, radius, circleItems));
    }
}