package elucent.rootsclassic.item;

import java.util.List;
import java.util.Random;
import elucent.rootsclassic.Roots;
import elucent.rootsclassic.Util;
import elucent.rootsclassic.capability.IManaCapability;
import elucent.rootsclassic.capability.RootsCapabilityManager;
import elucent.rootsclassic.component.ComponentBase;
import elucent.rootsclassic.component.ComponentManager;
import elucent.rootsclassic.component.EnumCastType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemStaff extends Item implements IManaRelatedItem {

  private static final double RANGE = 3.0;
  private static final double SIZE_PER_LEVEL = 2.0;
  private static final double SIZE_BASE = 3.0;
  Random random = new Random();

  public ItemStaff() {
    super();
    this.setMaxStackSize(1);
  }

  @Override
  public EnumAction getItemUseAction(ItemStack stack) {
    return EnumAction.BOW;
  }

  @Override
  public int getMaxItemUseDuration(ItemStack stack) {
    return 72000;
  }

  @Override
  public double getDurabilityForDisplay(ItemStack stack) {
    if (stack.hasTagCompound()) {
      return 1.0 - (double) stack.getTagCompound().getInteger("uses") / (double) stack.getTagCompound().getInteger("maxUses");
    }
    return 1.0;
  }

  @Override
  public boolean showDurabilityBar(ItemStack stack) {
    if (stack.hasTagCompound()) {
      if (stack.getTagCompound().getInteger("uses") < stack.getTagCompound().getInteger("maxUses")) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase caster, int timeLeft) {
    if (timeLeft < (72000 - 12)) {
      if (stack.hasTagCompound()) {
        if (stack.getTagCompound().getInteger("uses") >= 0 && caster.hasCapability(RootsCapabilityManager.manaCapability, null)) {
          stack.getTagCompound().setInteger("uses", stack.getTagCompound().getInteger("uses") - 1);
          ComponentBase comp = ComponentManager.getComponentFromName(stack.getTagCompound().getString("effect"));
          int potency = stack.getTagCompound().getInteger("potency");
          int efficiency = stack.getTagCompound().getInteger("efficiency");
          int size = stack.getTagCompound().getInteger("size");
          EntityPlayer player = (EntityPlayer) caster;
          if (player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() instanceof ItemDruidRobes
              && player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() instanceof ItemDruidRobes
              && player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() instanceof ItemDruidRobes
              && player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() instanceof ItemDruidRobes) {
            potency += 1;
          }
          IManaCapability manaCap = player.getCapability(RootsCapabilityManager.manaCapability, null);
          if (manaCap.getMana() >= ((float) comp.getManaCost()) / (efficiency + 1)) {
            manaCap.setMana(manaCap.getMana() - (((float) comp.getManaCost()) / (efficiency + 1)));
            Vec3d lookVec = caster.getLookVec();
            comp.doEffect(world, caster, EnumCastType.SPELL,
                caster.posX + RANGE * lookVec.x,
                caster.posY + RANGE * lookVec.y,
                caster.posZ + RANGE * lookVec.z, potency, efficiency,
                SIZE_BASE + SIZE_PER_LEVEL * size);
            for (int i = 0; i < 90; i++) {
              double offX = world.rand.nextFloat() * 0.5 - 0.25;
              double offY = world.rand.nextFloat() * 0.5 - 0.25;
              double offZ = world.rand.nextFloat() * 0.5 - 0.25;
              double coeff = (offX + offY + offZ) / 1.5 + 0.5;
              double dx = (lookVec.x + offX) * coeff;
              double dy = (lookVec.y + offY) * coeff;
              double dz = (lookVec.z + offZ) * coeff;
              if (world.rand.nextBoolean()) {
                Roots.proxy.spawnParticleMagicFX(world, caster.posX + dx, caster.posY + 1.5 + dy, caster.posZ + dz, dx, dy, dz, comp.primaryColor.x, comp.primaryColor.y, comp.primaryColor.z);
              }
              else {
                Roots.proxy.spawnParticleMagicFX(world, caster.posX + dx, caster.posY + 1.5 + dy, caster.posZ + dz, dx, dy, dz, comp.secondaryColor.x, comp.secondaryColor.y, comp.secondaryColor.z);
              }
            }
          }
        }
      }
    }
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
    ItemStack stack = player.getHeldItem(hand);
    if (world.isRemote && Minecraft.getMinecraft().currentScreen != null) {
      return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
    }
    else {
      player.setActiveHand(hand);
      return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
    }
  }

  @Override
  public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
    if (stack.hasTagCompound()) {
      if (stack.getTagCompound().getInteger("uses") <= 0) {
        if (entity instanceof EntityPlayer) {
          ((EntityPlayer) entity).inventory.setInventorySlotContents(slot, null);
        }
      }
    }
  }

  @Override
  public boolean shouldCauseReequipAnimation(ItemStack oldS, ItemStack newS, boolean slotChanged) {
    if (oldS.hasTagCompound() && newS.hasTagCompound()) {
      if (oldS.getTagCompound().getString("effect") != newS.getTagCompound().getString("effect")) {
        return true;
      }
    }
    return slotChanged;
  }

  @Override
  public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
    if (stack.hasTagCompound()) {
      ComponentBase comp = ComponentManager.getComponentFromName(stack.getTagCompound().getString("effect"));
      int potency = stack.getTagCompound().getInteger("potency");
      int efficiency = stack.getTagCompound().getInteger("efficiency");
      int size = stack.getTagCompound().getInteger("size");
      if (comp != null) {
        comp.castingAction((EntityPlayer) player, count, potency, efficiency, size);
        if (random.nextBoolean()) {
          Roots.proxy.spawnParticleMagicLineFX(player.getEntityWorld(), player.posX + 2.0 * (random.nextFloat() - 0.5), player.posY + 2.0 * (random.nextFloat() - 0.5) + 1.0, player.posZ + 2.0 * (random.nextFloat() - 0.5), player.posX, player.posY + 1.0, player.posZ, comp.primaryColor.x, comp.primaryColor.y, comp.primaryColor.z);
        }
        else {
          Roots.proxy.spawnParticleMagicLineFX(player.getEntityWorld(), player.posX + 2.0 * (random.nextFloat() - 0.5), player.posY + 2.0 * (random.nextFloat() - 0.5) + 1.0, player.posZ + 2.0 * (random.nextFloat() - 0.5), player.posX, player.posY + 1.0, player.posZ, comp.secondaryColor.x, comp.secondaryColor.y, comp.secondaryColor.z);
        }
      }
    }
  }

  public static void createData(ItemStack stack, String effect, int potency, int efficiency, int size) {
    stack.setTagCompound(new NBTTagCompound());
    stack.getTagCompound().setString("effect", effect);
    stack.getTagCompound().setInteger("potency", potency);
    stack.getTagCompound().setInteger("efficiency", efficiency);
    stack.getTagCompound().setInteger("size", size);
    stack.getTagCompound().setInteger("uses", 65 + 32 * efficiency);
    stack.getTagCompound().setInteger("maxUses", 65 + 32 * efficiency);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void addInformation(ItemStack stack, World player, List<String> tooltip, net.minecraft.client.util.ITooltipFlag advanced) {
    if (stack.hasTagCompound()) {
      ComponentBase comp = ComponentManager.getComponentFromName(stack.getTagCompound().getString("effect"));
      tooltip.add(TextFormatting.GOLD + I18n.format("roots.tooltip.spelltypeheading.name") + ": " + comp.getTextColor() + comp.getEffectName());
      tooltip.add(TextFormatting.RED + "  +" + stack.getTagCompound().getInteger("potency") + " " + I18n.format("roots.tooltip.spellpotency.name") + ".");
      tooltip.add(TextFormatting.RED + "  +" + stack.getTagCompound().getInteger("efficiency") + " " + I18n.format("roots.tooltip.spellefficiency.name") + ".");
      tooltip.add(TextFormatting.RED + "  +" + stack.getTagCompound().getInteger("size") + " " + I18n.format("roots.tooltip.spellsize.name") + ".");
      tooltip.add("");
      tooltip.add(TextFormatting.GOLD + Integer.toString(stack.getTagCompound().getInteger("uses")) + " " + I18n.format("roots.tooltip.usesremaining.name") + ".");
    }
  }

  @SideOnly(Side.CLIENT)
  public void initModel() {
    ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName() + "_0", "inventory"));
    ModelLoader.setCustomModelResourceLocation(this, 1, new ModelResourceLocation(getRegistryName() + "_1", "inventory"));
  }

  public static class ColorHandler implements IItemColor {

    public ColorHandler() {
      //
    }

    @Override
    public int getColorFromItemstack(ItemStack stack, int layer) {
      if (stack.hasTagCompound() && stack.getItem() instanceof ItemStaff) {
        if (layer == 2) {
          ComponentBase comp = ComponentManager.getComponentFromName(stack.getTagCompound().getString("effect"));
          return Util.intColor((int) comp.primaryColor.x, (int) comp.primaryColor.y, (int) comp.primaryColor.z);
        }
        if (layer == 1) {
          ComponentBase comp = ComponentManager.getComponentFromName(stack.getTagCompound().getString("effect"));
          return Util.intColor((int) comp.secondaryColor.x, (int) comp.secondaryColor.y, (int) comp.secondaryColor.z);
        }
      }
      return Util.intColor(255, 255, 255);
    }
  }
}
