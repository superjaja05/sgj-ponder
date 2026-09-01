package jjs.sgj_ponder

import jjs.sgj_ponder.cmd.ClientCommands
import jjs.sgj_ponder.ponderstuff.PonderSceneScaler
import jjs.sgj_ponder.ponderstuff.SGJPonderPlugin
import net.createmod.ponder.foundation.PonderIndex
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.forge.MOD_BUS

/** Forge entry point and lifecycle coordinator for SGJ Ponder. */
@Mod(SGJPonder.MODID)
object SGJPonder {
    /** Stable namespace shared by code, metadata, commands, and resources. */
    const val MODID = "sgj_ponder"

    /** Shared logger used by initialization, registry discovery, and diagnostics. */
    val LOGGER: Logger = LogManager.getLogger(MODID)

    /** Guards Ponder's global index against duplicate client lifecycle callbacks. */
    private var ponderPluginRegistered = false

    init {
        LOGGER.info("Initializing SGJ Ponder")
        // Client and dedicated-server setup events are delivered on the mod event bus.
        MOD_BUS.addListener(::onClientSetup)
        MOD_BUS.addListener(::onServerSetup)
    }

    /** Installs client-only Ponder integration after mod construction has completed. */
    private fun onClientSetup(event: FMLClientSetupEvent) {
        LOGGER.info("Initializing SGJ Ponder client support")
        event.enqueueWork {
            if (!ponderPluginRegistered) {
                ponderPluginRegistered = true
                PonderIndex.addPlugin(SGJPonderPlugin)
                PonderSceneScaler.register()
            }
        }
    }

    /** Confirms dedicated-server setup without touching the client-only scaler. */
    private fun onServerSetup(event: FMLDedicatedServerSetupEvent) {
        LOGGER.info("Initializing SGJ Ponder server support")
    }
}

/** Forge game-bus subscriber for registrations that are not mod lifecycle events. */
@EventBusSubscriber(modid = SGJPonder.MODID, bus = EventBusSubscriber.Bus.FORGE)
object ModEventListener {
    /** Adds the diagnostic Stargate-variant command to the active dispatcher. */
    @JvmStatic
    @SubscribeEvent
    fun registerCommands(event: RegisterCommandsEvent) {
        ClientCommands.register(event.dispatcher)
    }
}
