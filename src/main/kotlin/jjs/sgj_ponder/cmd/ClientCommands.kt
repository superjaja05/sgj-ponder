package jjs.sgj_ponder.cmd

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import jjs.sgj_ponder.SGJPonder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.povstalec.sgjourney.common.sgjourney.StargateVariant

/** Registers SGJ Ponder's in-game registry diagnostic command. */
object ClientCommands {
    /** Attaches `/sgjponder_getvars` to Forge's command dispatcher. */
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(Commands.literal("sgjponder_getvars").executes(ClientCommands::execute))
    }

    /** Logs and reports every Stargate variant available in the command source's level. */
    private fun execute(command: CommandContext<CommandSourceStack>): Int {
        SGJPonder.LOGGER.info("Fetching gate variants..")

        val level: ServerLevel = command.source.level
        // The level registry includes data-pack variants, unlike a static built-in registry.
        val registry = level.registryAccess().registryOrThrow(StargateVariant.REGISTRY_KEY)

        registry.asLookup().listElements().forEach { entry ->
            val variantName = entry.key().location().toString()
            val variantBase = entry.value().baseStargate.toString()
            SGJPonder.LOGGER.info("Found variant: {} for: {}", variantName, variantBase)
            command.source.sendSystemMessage(Component.literal("Found variant: $variantName for: $variantBase"))
        }

        return Command.SINGLE_SUCCESS
    }
}
