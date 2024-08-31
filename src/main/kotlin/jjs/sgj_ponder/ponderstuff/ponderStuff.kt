package jjs.sgj_ponder.ponderstuff

import com.simibubi.create.foundation.ponder.PonderRegistrationHelper
import com.simibubi.create.foundation.ponder.PonderRegistry
import com.simibubi.create.foundation.ponder.PonderTag
import jjs.sgj_ponder.SGJPonder
import jjs.sgj_ponder.SGJPonder.LOGGER
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.registries.ForgeRegistries
import net.povstalec.sgjourney.StargateJourney

/**
fun variantDisplayUsing(variantType: String, variantName: String): (SceneBuilder, SceneBuildingUtil) -> Unit {
    val variantDisplayCaller = {scene: SceneBuilder, util: SceneBuildingUtil ->
        ponderScenes().variantDisplay(scene, util, variantType, variantName)
    }
    return variantDisplayCaller
}
**/

class ponderStuff {
    val VARIANT_CRYSTAL: PonderTag = PonderTag(ResourceLocation(SGJPonder.MODID, "var_crystal")).item(ForgeRegistries.ITEMS.getValue(ResourceLocation(StargateJourney.MODID+ ":stargate_variant_crystal")), true, true)
            .defaultLang("Stargate Variant Crystal", "Used to change the appearance of stargates")

    val HELPER: PonderRegistrationHelper = PonderRegistrationHelper(SGJPonder.MODID)

    val var_crystal_resource = ResourceLocation(StargateJourney.MODID+ ":stargate_variant_crystal")

    fun register() {
        LOGGER.info("Loading Ponder..")
        HELPER.addStoryBoard(var_crystal_resource, "gate_pedestal", ponderScenes()::variantCrystal, VARIANT_CRYSTAL)
        HELPER.addStoryBoard(var_crystal_resource, "gate_pedestal", ponderScenes()::variantCrystalDisplayMW, VARIANT_CRYSTAL)
        HELPER.addStoryBoard(var_crystal_resource, "gate_pedestal", ponderScenes()::variantCrystalDisplayPegasus, VARIANT_CRYSTAL)
        HELPER.addStoryBoard(var_crystal_resource, "gate_pedestal", ponderScenes()::variantCrystalDisplayUniverse, VARIANT_CRYSTAL)
        HELPER.addStoryBoard(var_crystal_resource, "gate_pedestal", ponderScenes()::variantCrystalDisplayTollan, VARIANT_CRYSTAL)
        HELPER.addStoryBoard(var_crystal_resource, "gate_pedestal", ponderScenes()::variantCrystalDisplayClassic, VARIANT_CRYSTAL)

        PonderRegistry.TAGS.forTag(VARIANT_CRYSTAL)
            .add(var_crystal_resource)
    }
}