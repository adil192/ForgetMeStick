package com.adilhanney.forgetmestick;

import com.adilhanney.forgetmestick.item.ForgetMeStickItems;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ForgetMeStick implements ModInitializer {
	public static final String MOD_ID = "forgetmestick";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
		ForgetMeStickItems.registerItems();
	}
}
