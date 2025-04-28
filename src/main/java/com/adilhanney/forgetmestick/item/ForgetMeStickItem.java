package com.adilhanney.forgetmestick.item;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.VillageGossipType;
import net.minecraft.world.World;

public class ForgetMeStickItem extends Item {
  public ForgetMeStickItem(Settings settings) {
    super(settings);
  }

  @Override
  public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
    if (!(attacker instanceof PlayerEntity player)) return false;

    if (target instanceof VillagerEntity villager) {
      return postHitVillager(stack, villager, player);
    } else if (target instanceof Angerable) {
      return postHitAngerableEntity(stack, target, player);
    } else {
      return false;
    }
  }

  //#if MC<=12101
  @Override
  public boolean canRepair(ItemStack stack, ItemStack ingredient) {
    // Allow repairing with ender pearls in an anvil
    final var item = ingredient.getItem();
    return item == Items.ENDER_PEARL || item == Items.ENDER_EYE;
  }
  //#endif


  @Override
  public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
    // Don't allow mining with the forget-me stick, like a sword
    return false;
  }

  private boolean postHitVillager(ItemStack stack, VillagerEntity villager, PlayerEntity player) {
    final int startReputation = villager.getReputation(player);
    if (startReputation == 0) return false; // No reputation to reset

    // Reset reputation
    final var gossips = villager.getGossip();
    for (final var gossipType : VillageGossipType.values()) {
      gossips.remove(player.getUuid(), gossipType);
    }

    // After-effects
    villager.setHealth(Math.min(
        villager.getHealth(),
        (float) Math.ceil(villager.getMaxHealth() * 0.1)
    ));
    villager.handleStatus(startReputation > 0
        ? EntityStatuses.ADD_VILLAGER_ANGRY_PARTICLES
        : EntityStatuses.ADD_VILLAGER_HAPPY_PARTICLES);
    applyStatusEffects(villager);

    // Reduce durability
    damage(stack, 1, player);

    return true;
  }

  private boolean postHitAngerableEntity(ItemStack stack, LivingEntity entity, PlayerEntity player) {
    if (!(entity instanceof Angerable angerableEntity)) return false;

    // Reset anger
    final boolean isAngry = angerableEntity.hasAngerTime();
    if (!isAngry) return false;
    angerableEntity.stopAnger();

    // After-effects
    entity.setHealth(Math.min(
        entity.getHealth(),
        (float) Math.ceil(entity.getMaxHealth() * 0.5)
    ));
    applyStatusEffects(entity);

    // Reduce durability
    damage(stack, 4, player);

    return true;
  }

  private void applyStatusEffects(LivingEntity villager) {
    final var tps = 20;
    final var longDuration = 15 * tps;
    final var shortDuration = 5 * tps;

    villager.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, longDuration, 0, true, true));
    villager.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, shortDuration, 9, true, true));
    villager.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, longDuration, 0, true, true));
    villager.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, shortDuration, 2, true, true));
    villager.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, longDuration, 0, true, true));
    villager.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, shortDuration, 9, true, true));
  }

  private void damage(ItemStack stack, int amount, PlayerEntity player) {
    //#if MC>=12101
    stack.damage(amount, player, EquipmentSlot.MAINHAND);
    //#else
    //$$stack.damage(amount, player, p -> p.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
    //#endif
  }
}
