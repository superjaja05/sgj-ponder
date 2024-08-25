package jjs.sgj_ponder

import com.simibubi.create.foundation.data.CreateRegistrate
import com.simibubi.create.foundation.ponder.PonderLocalization
import com.tterrag.registrate.Registrate
import jjs.sgj_ponder.cmd.ClientCommands
import jjs.sgj_ponder.ponderstuff.ponderStuff
import net.minecraft.client.Minecraft
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.forge.runForDist


/**
 * Main mod class. Should be an `object` declaration annotated with `@Mod`.
 * The modid should be declared in this object and should match the modId entry
 * in mods.toml.
 *
 * An example for blocks is in the `blocks` package of this mod.
 */
@Mod(SGJPonder.MODID)
object SGJPonder {
    const val MODID = "sgj_ponder"

    // the logger for our mod
    val LOGGER: Logger = LogManager.getLogger(MODID)
    val REGISTRATE: CreateRegistrate = CreateRegistrate.create(MODID)

    init {
        LOGGER.log(Level.INFO, "Hello world!")

        val obj = runForDist(
            clientTarget = {
                MOD_BUS.addListener(this::onClientSetup)
                Minecraft.getInstance()
            },
            serverTarget = {
                MOD_BUS.addListener(this::onServerSetup)
                "test"
            })

        println(obj)
    }

    /**
     * This is used for initializing client specific
     * things such as renderers and keymaps
     * Fired on the mod specific event bus.
     */
    private fun onClientSetup(event: FMLClientSetupEvent) {
        LOGGER.log(Level.INFO, "Initializing client...")
        event.enqueueWork(ponderStuff()::register)
    }

    /**
     * Fired on the global Forge bus.
     */
    private fun onServerSetup(event: FMLDedicatedServerSetupEvent) {
        LOGGER.log(Level.INFO, "Server starting...")
    }
}
@EventBusSubscriber(modid = SGJPonder.MODID, bus = EventBusSubscriber.Bus.FORGE)
object ModEventListener {
    @SubscribeEvent
    fun registerCommands(event: RegisterCommandsEvent) {
        ClientCommands.register(event.dispatcher)
    }

}