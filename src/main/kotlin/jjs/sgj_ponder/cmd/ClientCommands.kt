package jjs.sgj_ponder.cmd

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import com.mojang.logging.LogUtils
import jjs.sgj_ponder.SGJPonder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.core.Registry
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.povstalec.sgjourney.common.stargate.StargateVariant


object ClientCommands {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack?>) {
        dispatcher.register(Commands.literal("sgjponder_getvars").executes(ClientCommands::execute))
    }

    private fun execute(command: CommandContext<CommandSourceStack>): Int {
        SGJPonder.LOGGER.info("Fetching gate variants..")

        val registryKey: ResourceKey<Registry<StargateVariant>> = StargateVariant.REGISTRY_KEY
        SGJPonder.LOGGER.info("RegistryKey: $registryKey")

        val level: ServerLevel = command.source.level

        val registryAccess = level.registryAccess()
        val stargateVariantRegistry = registryAccess.registryOrThrow(StargateVariant.REGISTRY_KEY)

        val RegLookup = stargateVariantRegistry.asLookup()

        for (listElement in RegLookup.listElements()) {
            val listElementId = listElement.key()
            val variant_name = listElementId.location().toString()
            val variant_base = listElement.value().baseStargate.toString()
            SGJPonder.LOGGER.info(("Found variant: $variant_name for: $variant_base"))
            command.source.sendSystemMessage(Component.literal("Found variant: $variant_name for: $variant_base"))
        }

        return Command.SINGLE_SUCCESS
    }
}