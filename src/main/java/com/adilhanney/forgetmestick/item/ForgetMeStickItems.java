package com.adilhanney.forgetmestick.item;

import com.adilhanney.forgetmestick.ForgetMeStick;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ForgetMeStickItems {
  public static final ForgetMeStickItem FORGET_ME_STICK = registerItem("forget_me_stick",
    new ForgetMeStickItem(new Item.Settings()
        .maxDamage(8)
        .rarity(Rarity.UNCOMMON)
        .fireproof()
    )
  );

  private static <T extends Item> T registerItem(String name, T item) {
    return Registry.register(Registries.ITEM, Identifier.of(ForgetMeStick.MOD_ID, name), item);
  }

  public static void registerItems() {
    ForgetMeStick.LOGGER.info("Registering Items for " + ForgetMeStick.MOD_ID);

    ItemGroupEvents.modifyEntriesEvent(ItemGroups.OPERATOR).register(entries -> {
      entries.add(FORGET_ME_STICK);
    });
  }
}
