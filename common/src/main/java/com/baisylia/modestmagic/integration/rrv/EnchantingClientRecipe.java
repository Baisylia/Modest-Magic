package com.baisylia.modestmagic.integration.rrv;

import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import com.baisylia.modestmagic.Constants;
import com.baisylia.modestmagic.block.ModBlocks;
import com.baisylia.modestmagic.recipe.custom.EnchantingRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
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

public class EnchantingClientRecipe extends AbstractModestMagicClientRecipe {

    private final Identifier id;
    private final List<SlotContent> inputs;
    private final SlotContent baseIngredient;
    private final List<ItemStack> outputs;

    public EnchantingClientRecipe(RecipeHolder<EnchantingRecipe> recipeHolder) {
        this.id = recipeHolder.id().identifier();
        EnchantingRecipe recipe = recipeHolder.value();
        this.inputs = recipe.getIngredients().stream().map(SlotContent::of).collect(Collectors.toList());

        List<ItemStack> validBases = new ArrayList<>();
        List<ItemStack> validOutputs = new ArrayList<>();

        Registry<Enchantment> enchantRegistry = Minecraft.getInstance().level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);

        List<List<Holder<Enchantment>>> resolvedPools = new ArrayList<>();
        for (List<ResourceKey<Enchantment>> poolKeys : recipe.getEnchantmentPools()) {
            List<Holder<Enchantment>> pool = new ArrayList<>();
            for (ResourceKey<Enchantment> key : poolKeys) {
                enchantRegistry.get(key).ifPresent(pool::add);
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
                validBases.add(testStack);

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
                    validOutputs.add(outStack);
                }
            }
        }

        // fallback
        if (validBases.isEmpty()) {
            validBases.add(new ItemStack(Items.BOOK));
            ItemStack out = new ItemStack(Items.ENCHANTED_BOOK);
            ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);

            if (!resolvedPools.isEmpty()) {
                for (Holder<Enchantment> e : resolvedPools.getFirst()) enchantments.set(e, 1);
            }
            EnchantmentHelper.setEnchantments(out, enchantments.toImmutable());
            validOutputs.add(out);
        }

        this.baseIngredient = SlotContent.of(validBases);
        this.outputs = validOutputs;
    }

    @Override
    public ReliableClientRecipeType getType() {
        return EnchantingClientRecipeType.INSTANCE;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
        // Pedestal Count slot
        slotFillContext.bindOptionalSlot(0, SlotContent.of(new ItemStack(ModBlocks.PEDESTAL.get(), inputs.size())), RecipeViewMenu.OptionalSlotRenderer.DEFAULT);
    }

    @Override
    public List<SlotContent> getIngredients() {
        List<SlotContent> allInputs = new ArrayList<>();
        allInputs.add(baseIngredient);
        allInputs.addAll(inputs);
        return allInputs;
    }

    @Override
    public List<SlotContent> getResults() {
        return outputs.stream().map(SlotContent::of).toList();
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public void initRecipe(RecipeViewScreen screen, RecipePosition recipePosition, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        int cx = 35;
        int cy = getType().getDisplayHeight() / 2;
        int radius = 24;

        List<SlotContent> circleItems;

        if (inputs.size() > 6) {
            circleItems = ModestMagicRrvPlugin.consolidateItems(inputs);
        } else {
            circleItems = inputs;
        }

        RotationState state = new RotationState(cx, cy, radius, circleItems.size(), recipePosition);

        screen.addRecipeWidget(new RotatingLettersWidget(
                Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/enchanted_letters.png"),
                cx, cy, radius + 6
        ));

        screen.addRecipeWidget(new HoveringSlotWidget(baseIngredient, cx - 9, cy - 9, 0, recipePosition));

        for (int i = 0; i < circleItems.size(); i++) {
            screen.addRecipeWidget(new RotatingSlotWidget(state, circleItems.get(i), i + 1, recipePosition));
        }

        // Arrow and cycling enchanted item
//        widgets.addTexture(EmiTexture.EMPTY_ARROW, cx + radius + 16, cy - 8);
        screen.addRecipeWidget(new HoveringSlotWidget(SlotContent.of(outputs), cx + radius + 51, cy - 9, 2, recipePosition));

        screen.addRecipeWidget(new WheelListTooltipWidget(cx, cy, radius, circleItems, recipePosition));
    }
}
