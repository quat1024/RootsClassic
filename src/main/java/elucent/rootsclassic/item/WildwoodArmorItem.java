package elucent.rootsclassic.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.LazyValue;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import elucent.rootsclassic.Const;
import elucent.rootsclassic.client.model.WildwoodArmorModel;
import elucent.rootsclassic.util.RootsUtil;

public class WildwoodArmorItem extends ArmorItem {

  private final LazyValue<BipedModel<?>> model;

  public WildwoodArmorItem(IArmorMaterial materialIn, EquipmentSlotType slot, Item.Properties builderIn) {
    super(materialIn, slot, builderIn);
    this.model = DistExecutor.unsafeRunForDist(() -> () -> new LazyValue<>(() -> this.provideArmorModelForSlot(slot)),
        () -> () -> null);
  }

  @Override
  public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
    return Const.MODID + ":textures/models/armor/wildwood.png";
  }

  @OnlyIn(Dist.CLIENT)
  @Nullable
  @Override
  public <A extends BipedModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, A _default) {
    return (A) model.getValue();
  }

  @OnlyIn(Dist.CLIENT)
  public BipedModel<?> provideArmorModelForSlot(EquipmentSlotType slot) {
    return new WildwoodArmorModel(slot);
  }

  @Override
  public void onArmorTick(ItemStack stack, World world, PlayerEntity player) {
    RootsUtil.randomlyRepair(world.rand, stack);
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    super.addInformation(stack, worldIn, tooltip, flagIn);
    tooltip.add(StringTextComponent.EMPTY);
    tooltip.add(new TranslationTextComponent("rootsclassic.attribute.equipped").mergeStyle(TextFormatting.GRAY));
    tooltip.add(new StringTextComponent(" ").appendSibling(new TranslationTextComponent("rootsclassic.attribute.increasedmanaregen")).mergeStyle(TextFormatting.BLUE));
  }
}
