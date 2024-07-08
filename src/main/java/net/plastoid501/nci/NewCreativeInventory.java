package net.plastoid501.nci;

import net.fabricmc.api.ModInitializer;

//? if <=1.16.5 {
/*import org.apache.logging.log4j.LogManager;*/
/*import org.apache.logging.log4j.Logger;*/
//?} else {
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//?}

public class NewCreativeInventory implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER =
			//? if <=1.16.5 {
			/*LogManager.getLogger("new-creative-inventory");*/
			//?} else {
			LoggerFactory.getLogger("new-creative-inventory");
			//?}

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

	}
}