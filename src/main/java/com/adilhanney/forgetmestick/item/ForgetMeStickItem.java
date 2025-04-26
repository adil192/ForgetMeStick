package com.adilhanney.forgetmestick.item;

import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.village.VillageGossipType;

public class ForgetMeStickItem extends Item {
  public ForgetMeStickItem(Settings settings) {
    super(settings);
  }

  @Override
  public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
    if (!(target instanceof VillagerEntity villager)) return false;
    if (!(attacker instanceof PlayerEntity player)) return false;

    final int startReputation = villager.getReputation(player);
    if (startReputation == 0) return false; // No reputation to reset

    // Reset reputation
    final var gossips = villager.getGossip();
    for (final var gossipType : VillageGossipType.values()) {
      gossips.remove(player.getUuid(), gossipType);
    }

    // After-effects
    villager.setHealth(Math.min(2, villager.getMaxHealth()));
    villager.handleStatus(startReputation > 0
        ? EntityStatuses.ADD_VILLAGER_ANGRY_PARTICLES
        : EntityStatuses.ADD_VILLAGER_HAPPY_PARTICLES);
    applyStatusEffects(villager);

    // Reduce durability
    stack.damage(1, player, LivingEntity.getSlotForHand(Hand.MAIN_HAND));

    return true;
  }

  @Override
  public boolean canRepair(ItemStack stack, ItemStack ingredient) {
    // Allow repairing with bones
    final var item = ingredient.getItem();
    return item == Items.ENDER_PEARL || item == Items.ENDER_EYE;
  }

  private void applyStatusEffects(VillagerEntity villager) {
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
}
