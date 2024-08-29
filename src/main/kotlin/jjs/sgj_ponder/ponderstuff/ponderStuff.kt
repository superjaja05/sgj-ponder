package jjs.sgj_ponder.ponderstuff

import com.simibubi.create.foundation.ponder.PonderRegistrationHelper
import com.simibubi.create.foundation.ponder.PonderRegistry
import com.simibubi.create.foundation.ponder.PonderStoryBoardEntry
import com.simibubi.create.foundation.ponder.PonderStoryBoardEntry.PonderStoryBoard
import com.simibubi.create.foundation.ponder.PonderTag
import com.simibubi.create.foundation.ponder.SceneBuilder
import com.simibubi.create.foundation.ponder.SceneBuildingUtil
import jjs.sgj_ponder.SGJPonder
import jjs.sgj_ponder.SGJPonder.LOGGER
import net.minecraft.client.Minecraft
import net.minecraft.core.Registry
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.server.ServerLifecycleHooks
import net.povstalec.sgjourney.StargateJourney
import net.povstalec.sgjourney.common.stargate.StargateVariant
import net.minecraft.client.multiplayer.ClientLevel

/**
fun variantDisplayUsing(variantType: String, variantName: String): (SceneBuilder, SceneBuildingUtil) -> Unit {
    val variantDisplayCaller = {scene: SceneBuilder, util: SceneBuildingUtil ->
        ponderScenes().variantDisplay(scene, util, variantType, variantName)
    }
    return variantDisplayCaller
}
**/

var server = ServerLifecycleHooks.getCurrentServer()

class ponderStuff {
    val VARIANT_CRYSTAL: PonderTag = PonderTag(ResourceLocation(SGJPonder.MODID, "var_crystal")).item(ForgeRegistries.ITEMS.getValue(ResourceLocation(StargateJourney.MODID+ ":stargate_variant_crystal")), true, true)
            .defaultLang("Stargate Variant Crystal", "Used to change the appearance of stargates")

    //val VARIANT_LIST: PonderTag = PonderTag(ResourceLocation(SGJPonder.MODID, "var_list")).item(ForgeRegistries.ITEMS.getValue(ResourceLocation(StargateJourney.MODID+ ":stargate_variant_crystal")), true, false)
    //    .defaultLang("Stargate Variants", "List of all the variant crystals")

    val HELPER: PonderRegistrationHelper = PonderRegistrationHelper(SGJPonder.MODID)


    fun register() {
        LOGGER.info("Loading Ponder..")
        HELPER.addStoryBoard(ResourceLocation(StargateJourney.MODID+ ":stargate_variant_crystal"), "gate_pedestal", ponderScenes()::variantCrystal, VARIANT_CRYSTAL)
        HELPER.addStoryBoard(ResourceLocation(StargateJourney.MODID+ ":stargate_variant_crystal"), "gate_pedestal", ponderScenes()::variantCrystalDisplayMW, VARIANT_CRYSTAL)
        HELPER.addStoryBoard(ResourceLocation(StargateJourney.MODID+ ":stargate_variant_crystal"), "gate_pedestal", ponderScenes()::variantCrystalDisplayPegasus, VARIANT_CRYSTAL)
        HELPER.addStoryBoard(ResourceLocation(StargateJourney.MODID+ ":stargate_variant_crystal"), "gate_pedestal", ponderScenes()::variantCrystalDisplayUniverse, VARIANT_CRYSTAL)
        HELPER.addStoryBoard(ResourceLocation(StargateJourney.MODID+ ":stargate_variant_crystal"), "gate_pedestal", ponderScenes()::variantCrystalDisplayTollan, VARIANT_CRYSTAL)
        HELPER.addStoryBoard(ResourceLocation(StargateJourney.MODID+ ":stargate_variant_crystal"), "gate_pedestal", ponderScenes()::variantCrystalDisplayClassic, VARIANT_CRYSTAL)

        PonderRegistry.TAGS.forTag(VARIANT_CRYSTAL)
            .add(ResourceLocation(StargateJourney.MODID+ ":stargate_variant_crystal"))
    }
}