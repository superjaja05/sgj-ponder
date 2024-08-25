package jjs.sgj_ponder.ponderstuff

import com.simibubi.create.AllBlocks
import com.simibubi.create.foundation.ponder.SceneBuilder
import com.simibubi.create.foundation.ponder.SceneBuildingUtil
import com.simibubi.create.foundation.ponder.element.InputWindowElement
import com.simibubi.create.foundation.utility.Pointing
import jjs.sgj_ponder.SGJPonder.LOGGER
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.Direction
import net.minecraft.core.Registry
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraftforge.registries.ForgeRegistries
import net.povstalec.sgjourney.common.block_entities.stargate.ClassicStargateEntity
import net.povstalec.sgjourney.common.block_entities.stargate.MilkyWayStargateEntity
import net.povstalec.sgjourney.common.block_entities.stargate.PegasusStargateEntity
import net.povstalec.sgjourney.common.block_entities.stargate.TollanStargateEntity
import net.povstalec.sgjourney.common.block_entities.stargate.UniverseStargateEntity
import net.povstalec.sgjourney.common.init.BlockInit
import net.povstalec.sgjourney.common.stargate.StargateVariant

fun removePrefixUntilDelimiter(input: String, delimiter: String): String {
    return input.reversed().substringBeforeLast(delimiter).reversed()
}

fun removeSuffixUntilDelimiter(input: String, delimiter: String): String {
    return input.substringBeforeLast(delimiter)
}

fun variantDisplay(scene: SceneBuilder, util: SceneBuildingUtil, type: String, name: String) {
    val gate = util.grid.at(3,1,4)

    val type_space = removeSuffixUntilDelimiter(type, ":")
    val type_name = removePrefixUntilDelimiter(type, ":")

    val name_space = removeSuffixUntilDelimiter(name, ":")
    val name_name = removePrefixUntilDelimiter(name, ":")

    val display = util.grid.at(6, 9, 6)

    scene.world.setDisplayBoardText(display, 0, Component.literal(type_space))
    scene.world.setDisplayBoardText(display, 1, Component.literal(type_name))

    scene.world.setDisplayBoardText(display, 2, Component.literal(name_space))
    scene.world.setDisplayBoardText(display, 3, Component.literal(name_name))

    scene.addKeyframe()
    when (type) {
        "sgjourney:classic_stargate" -> {
            scene.world.setBlock(gate, ForgeRegistries.BLOCKS.getValue(BlockInit.CLASSIC_STARGATE.id)?.defaultBlockState(), false)
            scene.world.modifyBlockEntityNBT(util.select.position(gate), ClassicStargateEntity::class.java) { nbt ->
                nbt.putString("Symbols", "sgjourney:terra")
                nbt.putString("PointOfOrigin", "sgjourney:tauri")
                nbt.putString("Variant", name)
            }
        }
        "sgjourney:universe_stargate" -> {
            scene.world.setBlock(gate, ForgeRegistries.BLOCKS.getValue(BlockInit.UNIVERSE_STARGATE.id)?.defaultBlockState(), false)
            scene.world.modifyBlockEntityNBT(util.select.position(gate), UniverseStargateEntity::class.java) { nbt ->
                nbt.putString("Symbols", "sgjourney:universal")
                nbt.putString("PointOfOrigin", "sgjourney:universal")
                nbt.putString("Variant", name)
            }
        }
        "sgjourney:tollan_stargate" -> {
            scene.world.setBlock(gate, ForgeRegistries.BLOCKS.getValue(BlockInit.TOLLAN_STARGATE.id)?.defaultBlockState(), false)
            scene.world.modifyBlockEntityNBT(util.select.position(gate), TollanStargateEntity::class.java) { nbt ->
                nbt.putString("Variant", name)
            }
        }
        "sgjourney:milky_way_stargate" -> {
            scene.world.setBlock(gate, ForgeRegistries.BLOCKS.getValue(BlockInit.MILKY_WAY_STARGATE.id)?.defaultBlockState(), false)
            scene.world.modifyBlockEntityNBT(util.select.position(gate), MilkyWayStargateEntity::class.java) { nbt ->
                nbt.putString("Symbols", "sgjourney:terra")
                nbt.putString("PointOfOrigin", "sgjourney:terra")
                nbt.putString("Variant", name)
            }
        }
        "sgjourney:pegasus_stargate" -> {
            scene.world.setBlock(gate, ForgeRegistries.BLOCKS.getValue(BlockInit.PEGASUS_STARGATE.id)?.defaultBlockState(), false)
            scene.world.modifyBlockEntityNBT(util.select.position(gate), PegasusStargateEntity::class.java) { nbt ->
                nbt.putString("Symbols", "sgjourney:terra")
                nbt.putString("PointOfOrigin", "sgjourney:terra")
                nbt.putBoolean("DynamicSymbols", false)
                nbt.putString("Variant", name)
            }
        }
    }
    scene.idle(40);
}

class ponderScenes {
    fun variantCrystal(scene: SceneBuilder, util: SceneBuildingUtil) {
        scene.title("var_crystal", "Variant Crystals")
        scene.configureBasePlate(0, 0, 7)
        scene.world.showSection(util.select.layer(0), Direction.UP);
        scene.idle(5);

        val gate = util.grid.at(3,1,4)
        scene.world.showSection(util.select.fromTo(0,1,0, 7, 1, 3), Direction.SOUTH);
        scene.idle(10);
        scene.world.showSection(util.select.position(gate), Direction.DOWN);
        scene.world.setBlock(gate, ForgeRegistries.BLOCKS.getValue(BlockInit.MILKY_WAY_STARGATE.id)?.defaultBlockState(), true)
        scene.world.modifyBlockEntityNBT(util.select.position(gate), MilkyWayStargateEntity::class.java) { nbt ->
            nbt.putString("Symbols", "sgjourney:galaxy_milky_way")
            nbt.putString("PointOfOrigin", "sgjourney:tauri")
        }
        scene.idle(10);
        scene.scaleSceneView(0.9F)
        scene.setSceneOffsetY(-2F)
        scene.addKeyframe()
        scene.overlay.showControls(InputWindowElement(util.vector.topOf(gate), Pointing.DOWN).rightClick(), 25)
        scene.overlay.showText(40)
            .placeNearTarget()
            .pointAt(util.vector.topOf(gate))
            .text("Right click the gate to apply the variant")

        scene.idle(30);
        /**
        for (i in 1..9) {
            scene.world.showSection(util.select.fromTo(6, i, 6, 0, i, 6), Direction.DOWN)
            scene.idle(2);
        }

        scene.scaleSceneView(0.8F)
        scene.setSceneOffsetY(-3F)

        scene.rotateCameraY(15F)

        scene.idle(40)

        scene.world.setKineticSpeed(util.select.fromTo(6, 9, 6, 0, 8, 6), 192F)

        val registryKey: ResourceKey<Registry<StargateVariant>> = StargateVariant.REGISTRY_KEY
        LOGGER.info("RegistryKey: $registryKey")

        val minecraft = Minecraft.getInstance()
        val clientLevel: ClientLevel? = minecraft.level

        val registryAccess = clientLevel?.registryAccess()
        val stargateVariantRegistry = registryAccess?.registryOrThrow(StargateVariant.REGISTRY_KEY)

        val RegLookup = stargateVariantRegistry?.asLookup()

        LOGGER.info("Attempting to Load variants ponder..")
        if (RegLookup != null) {
            LOGGER.info("Loading Variants Ponder..")
            for (listElement in RegLookup.listElements()) {
                val listElementId = listElement.key()
                val variant_name = listElementId.location().toString()
                val variant_base = listElement.value().baseStargate.toString()
                LOGGER.info(("Found variant: $variant_name for: $variant_base"))
                variantDisplay(scene, util, variant_base, variant_name)
            }
        }
        **/

        scene.markAsFinished();
    }

    fun variantCrystalDisplay(scene: SceneBuilder, util: SceneBuildingUtil) {
        scene.title("var_crystal_display", "Variant Crystals")
        scene.configureBasePlate(0, 0, 7)
        scene.world.showSection(util.select.layer(0), Direction.UP);
        scene.idle(5);

        val gate = util.grid.at(3,1,4)
        scene.world.showSection(util.select.fromTo(0,1,0, 7, 1, 3), Direction.SOUTH);

        for (i in 1..9) {
            scene.world.showSection(util.select.fromTo(6, i, 6, 0, i, 6), Direction.DOWN)
            scene.idle(1);
        }

        scene.scaleSceneView(0.8F)
        scene.setSceneOffsetY(-3F)

        scene.rotateCameraY(15F)

        scene.world.showSection(util.select.position(gate), Direction.DOWN);
        scene.world.setBlock(gate, ForgeRegistries.BLOCKS.getValue(BlockInit.MILKY_WAY_STARGATE.id)?.defaultBlockState(), true)
        scene.world.modifyBlockEntityNBT(util.select.position(gate), MilkyWayStargateEntity::class.java) { nbt ->
            nbt.putString("Symbols", "sgjourney:galaxy_milky_way")
            nbt.putString("PointOfOrigin", "sgjourney:tauri")
        }

        scene.idle(20)

        scene.world.setKineticSpeed(util.select.fromTo(6, 9, 6, 0, 8, 6), 192F)

        val minecraft = Minecraft.getInstance()
        val clientLevel: ClientLevel? = minecraft.level

        val registryAccess = clientLevel?.registryAccess()
        val stargateVariantRegistry = registryAccess?.registryOrThrow(StargateVariant.REGISTRY_KEY)

        val RegLookup = stargateVariantRegistry?.asLookup()

        LOGGER.info("Attempting to Load variants ponder..")
        if (RegLookup != null) {
            LOGGER.info("Loading Variants Ponder..")
            for (listElement in RegLookup.listElements()) {
                val listElementId = listElement.key()
                val variant_name = listElementId.location().toString()
                val variant_base = listElement.value().baseStargate.toString()
                LOGGER.info(("Found variant: $variant_name for: $variant_base"))
                variantDisplay(scene, util, variant_base, variant_name)
            }
        }

        scene.markAsFinished();
    }
}