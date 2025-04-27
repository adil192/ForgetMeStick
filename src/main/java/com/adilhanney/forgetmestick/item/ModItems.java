package com.adilhanney.forgetmestick.item;

import com.adilhanney.forgetmestick.ForgetMeStick;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.function.Function;

public class ModItems {
  public static final ForgetMeStickItem FORGET_ME_STICK = (ForgetMeStickItem) register("forget_me_stick", ForgetMeStickItem::new,
    new Item.Settings()
        .maxDamage(8)
        .rarity(Rarity.UNCOMMON)
        .fireproof()
        //#if MC>=12104
        //$$.repairable(Items.ENDER_PEARL)
        //$$.repairable(Items.ENDER_EYE)
        //#endif
  );

  private static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
    final var registryKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ForgetMeStick.MOD_ID, name));

    //#if MC>=12104
    //$$return Items.register(registryKey, itemFactory, settings);
    //#else
    return Items.register(registryKey, itemFactory.apply(settings));
    //#endif
  }

  public static void registerItems() {
    ForgetMeStick.LOGGER.info("Registering Items for " + ForgetMeStick.MOD_ID);

    ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
      entries.add(FORGET_ME_STICK);
    });
  }
}
