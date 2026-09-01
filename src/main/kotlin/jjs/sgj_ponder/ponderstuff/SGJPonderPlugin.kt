package jjs.sgj_ponder.ponderstuff

import jjs.sgj_ponder.SGJPonder
import net.createmod.ponder.api.registration.PonderPlugin
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.registries.ForgeRegistries

/** Connects SGJ Ponder's Kotlin storyboards and tag to standalone Ponder. */
object SGJPonderPlugin : PonderPlugin {
    /** Mandatory SGJourney item used as the Ponder entry and tag icon. */
    val VARIANT_CRYSTAL_ITEM = ResourceLocation("sgjourney", "stargate_variant_crystal")

    /** Stable Ponder tag containing the variant-crystal tutorial entry. */
    val VARIANT_CRYSTAL_TAG = ResourceLocation(SGJPonder.MODID, "var_crystal")

    /** Supplies the namespace Ponder uses for this plugin's registrations. */
    override fun getModId(): String = SGJPonder.MODID

    /** Delegates all six storyboard registrations to [PonderScenes]. */
    override fun registerScenes(helper: PonderSceneRegistrationHelper<ResourceLocation>) {
        PonderScenes.register(helper)
    }

    /** Builds the index tag and fails clearly when SGJourney's required item is absent. */
    override fun registerTags(helper: PonderTagRegistrationHelper<ResourceLocation>) {
        // Metadata already requires SGJourney; this validates the exact API item we integrate.
        val iconItem = ForgeRegistries.ITEMS.getValue(VARIANT_CRYSTAL_ITEM)
            ?: throw IllegalStateException(
                "Required SGJourney item $VARIANT_CRYSTAL_ITEM is missing; " +
                    "the installed SGJourney version is incompatible or incomplete",
            )

        helper.registerTag(VARIANT_CRYSTAL_TAG)
            .title("Stargate Variant Crystal")
            .description("Used to change the appearance of stargates")
            .item(iconItem)
            .addToIndex()
            .register()

        helper.addToTag(VARIANT_CRYSTAL_TAG).add(VARIANT_CRYSTAL_ITEM)
    }
}
