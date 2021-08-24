package elucent.rootsclassic.item;

import elucent.rootsclassic.registry.RootsRegistry;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class RootsFoodItem extends Item {
	private static final int HEAL_LARGE = 5;
	private static final int HEAL_SMALL = 2;

	public RootsFoodItem(Properties properties) {
		super(properties);
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
		super.onItemUseFinish(stack, worldIn, entityLiving);
		if (stack.getItem() == RootsRegistry.REDCURRANT.get()) {
			entityLiving.heal(HEAL_SMALL);
		}
		if (stack.getItem() == RootsRegistry.ELDERBERRY.get()) {
			entityLiving.clearActivePotions();
		}
		if (stack.getItem() == RootsRegistry.HEALING_POULTICE.get()) {
			entityLiving.heal(HEAL_LARGE);
		}
		return stack;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		if (stack.getItem() == RootsRegistry.REDCURRANT.get()) {
			tooltip.add(new TranslationTextComponent("rootsclassic.healingitem.tooltip").mergeStyle(TextFormatting.GRAY));
		}
		if (stack.getItem() == RootsRegistry.ELDERBERRY.get()) {
			tooltip.add(new TranslationTextComponent("rootsclassic.clearpotionsitem.tooltip").mergeStyle(TextFormatting.GRAY));
		}
		if (stack.getItem() == RootsRegistry.HEALING_POULTICE.get()) {
			tooltip.add(new TranslationTextComponent("rootsclassic.healingitem.tooltip", HEAL_LARGE).mergeStyle(TextFormatting.GRAY));
		}
		if (stack.getItem() == RootsRegistry.NIGHTSHADE.get()) {
			tooltip.add(new TranslationTextComponent("rootsclassic.poisonitem.tooltip").mergeStyle(TextFormatting.GRAY));
		}
	}
}