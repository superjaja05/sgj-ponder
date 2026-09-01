package jjs.sgj_ponder.ponderstuff

import com.simibubi.create.content.kinetics.base.KineticBlockEntity
import com.simibubi.create.content.trains.display.FlapDisplayBlockEntity
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Supplier
import jjs.sgj_ponder.SGJPonder
import net.createmod.catnip.math.Pointing
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper
import net.createmod.ponder.api.scene.PonderStoryBoard
import net.createmod.ponder.api.scene.SceneBuilder
import net.createmod.ponder.api.scene.SceneBuildingUtil
import net.minecraft.client.Minecraft
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.povstalec.sgjourney.common.block_entities.stargate.ClassicStargateEntity
import net.povstalec.sgjourney.common.block_entities.stargate.MilkyWayStargateEntity
import net.povstalec.sgjourney.common.block_entities.stargate.PegasusStargateEntity
import net.povstalec.sgjourney.common.block_entities.stargate.TollanStargateEntity
import net.povstalec.sgjourney.common.block_entities.stargate.UniverseStargateEntity
import net.povstalec.sgjourney.common.init.BlockInit
import net.povstalec.sgjourney.common.sgjourney.StargateVariant

/** Defines the six SGJourney variant-crystal storyboards and their shared scene helpers. */
object PonderScenes {
    /** Stable scene paths used both for registration and runtime scaler ownership checks. */
    private val SCENE_IDS = setOf(
        "var_crystal",
        "var_mw_display",
        "var_pegasus_display",
        "var_universe_display",
        "var_tollan_display",
        "var_classic_display",
    )

    /** Demonstration address written to each displayed gate variant. */
    private val DEMO_ADDRESS = intArrayOf(1, 2, 3, 4, 5, 6)

    // Authored transforms provide safe defaults before the runtime scaler's first client tick.
    private const val INTRO_SCENE_SCALE = 0.78f
    private const val INTRO_SCENE_OFFSET_Y = -2.1f
    private const val DISPLAY_SCENE_SCALE = 0.62f
    private const val DISPLAY_SCENE_OFFSET_Y = -3.2f
    private const val DISPLAY_SCENE_ROTATION_Y = 15.0f

    /** Milky Way block/entity details and the NBT required for its variant transitions. */
    private val MILKY_WAY = GateType(
        StargateVariant.MILKY_WAY_STARGATE,
        "var_mw_display",
        "Variant Crystals for Milky Way gates",
        Supplier { BlockInit.MILKY_WAY_STARGATE.get() },
        MilkyWayStargateEntity::class.java,
        Consumer { nbt ->
            nbt.putString("Symbols", "sgjourney:galaxy_milky_way")
            nbt.putString("PointOfOrigin", "sgjourney:tauri")
        },
        BiConsumer { nbt, variant ->
            nbt.putString("Symbols", "sgjourney:terra")
            nbt.putString("PointOfOrigin", "sgjourney:terra")
            putVariantAddress(nbt, variant)
        },
    )

    /** Pegasus block/entity details, including its fixed-symbol demonstration mode. */
    private val PEGASUS = GateType(
        StargateVariant.PEGASUS_STARGATE,
        "var_pegasus_display",
        "Variant Crystals for Pegasus gates",
        Supplier { BlockInit.PEGASUS_STARGATE.get() },
        PegasusStargateEntity::class.java,
        Consumer { nbt ->
            nbt.putString("Symbols", "sgjourney:galaxy_milky_way")
            nbt.putString("PointOfOrigin", "sgjourney:tauri")
            nbt.putBoolean("DynamicSymbols", false)
        },
        BiConsumer { nbt, variant ->
            nbt.putString("Symbols", "sgjourney:terra")
            nbt.putString("PointOfOrigin", "sgjourney:terra")
            nbt.putBoolean("DynamicSymbols", false)
            putVariantAddress(nbt, variant)
        },
    )

    /** Universe block/entity details and its universal symbol configuration. */
    private val UNIVERSE = GateType(
        StargateVariant.UNIVERSE_STARGATE,
        "var_universe_display",
        "Variant Crystals for Universe gates",
        Supplier { BlockInit.UNIVERSE_STARGATE.get() },
        UniverseStargateEntity::class.java,
        Consumer { nbt ->
            nbt.putString("Symbols", "sgjourney:universal")
            nbt.putString("PointOfOrigin", "sgjourney:universal")
        },
        BiConsumer { nbt, variant ->
            nbt.putString("Symbols", "sgjourney:universal")
            nbt.putString("PointOfOrigin", "sgjourney:universal")
            putVariantAddress(nbt, variant)
        },
    )

    /** Tollan block/entity details used by the dedicated display storyboard. */
    private val TOLLAN = GateType(
        StargateVariant.TOLLAN_STARGATE,
        "var_tollan_display",
        "Variant Crystals for Tollan gates",
        Supplier { BlockInit.TOLLAN_STARGATE.get() },
        TollanStargateEntity::class.java,
        Consumer { nbt ->
            nbt.putString("Symbols", "sgjourney:galaxy_milky_way")
            nbt.putString("PointOfOrigin", "sgjourney:tauri")
        },
        BiConsumer { nbt, variant -> putVariantAddress(nbt, variant) },
    )

    /** Classic block/entity details used by the dedicated display storyboard. */
    private val CLASSIC = GateType(
        StargateVariant.CLASSIC_STARGATE,
        "var_classic_display",
        "Variant Crystals for Classic gates",
        Supplier { BlockInit.CLASSIC_STARGATE.get() },
        ClassicStargateEntity::class.java,
        Consumer { nbt ->
            nbt.putString("Symbols", "sgjourney:galaxy_milky_way")
            nbt.putString("PointOfOrigin", "sgjourney:tauri")
        },
        BiConsumer { nbt, variant ->
            nbt.putString("Symbols", "sgjourney:terra")
            nbt.putString("PointOfOrigin", "sgjourney:tauri")
            putVariantAddress(nbt, variant)
        },
    )

    /** Registers the introduction and five gate-family display storyboards. */
    fun register(helper: PonderSceneRegistrationHelper<ResourceLocation>) {
        registerScene(helper) { scene, util -> variantCrystal(scene, util) }
        registerScene(helper) { scene, util -> variantCrystalDisplay(scene, util, MILKY_WAY) }
        registerScene(helper) { scene, util -> variantCrystalDisplay(scene, util, PEGASUS) }
        registerScene(helper) { scene, util -> variantCrystalDisplay(scene, util, UNIVERSE) }
        registerScene(helper) { scene, util -> variantCrystalDisplay(scene, util, TOLLAN) }
        registerScene(helper) { scene, util -> variantCrystalDisplay(scene, util, CLASSIC) }
    }

    /** Returns true only for one of this mod's six registered storyboards. */
    fun isSgjPonderScene(sceneId: ResourceLocation): Boolean {
        return sceneId.namespace == SGJPonder.MODID && sceneId.path in SCENE_IDS
    }

    /** Applies the common item, structure, and tag identifiers to one storyboard. */
    private fun registerScene(
        helper: PonderSceneRegistrationHelper<ResourceLocation>,
        scene: PonderStoryBoard,
    ) {
        helper.addStoryBoard(
            SGJPonderPlugin.VARIANT_CRYSTAL_ITEM,
            "gate_pedestal",
            scene,
            SGJPonderPlugin.VARIANT_CRYSTAL_TAG,
        )
    }

    /** Introduces applying one crystal and demonstrates its gate-family restriction. */
    private fun variantCrystal(scene: SceneBuilder, util: SceneBuildingUtil) {
        scene.title("var_crystal", "Variant Crystals")
        scene.configureBasePlate(0, 0, 7)

        val gate = util.grid().at(3, 1, 4)
        showBaseAndPedestal(scene, util)
        revealGate(scene, util)
        setGate(scene, util, MILKY_WAY, MILKY_WAY.defaultNbt, true)
        scene.idle(10)

        scene.scaleSceneView(INTRO_SCENE_SCALE)
        scene.setSceneOffsetY(INTRO_SCENE_OFFSET_Y)
        scene.addKeyframe()
        scene.overlay().showControls(util.vector().topOf(gate), Pointing.DOWN, 25).rightClick()
        scene.overlay().showText(35)
            .placeNearTarget()
            .pointAt(util.vector().topOf(gate))
            .text("Right click the gate to apply the variant")

        scene.idle(40)

        scene.overlay().showText(45)
            .placeNearTarget()
            .pointAt(util.vector().topOf(gate))
            .text("Each variant only works on one specific type of gate")

        scene.idle(10)
        setGate(scene, util, PEGASUS, PEGASUS.defaultNbt, false)
        scene.idle(40)
        setGate(scene, util, UNIVERSE, UNIVERSE.defaultNbt, false)
        scene.idle(40)
        setGate(scene, util, TOLLAN, TOLLAN.defaultNbt, false)
        scene.idle(40)
        setGate(scene, util, CLASSIC, CLASSIC.defaultNbt, false)
        scene.idle(40)
        setGate(scene, util, MILKY_WAY, MILKY_WAY.defaultNbt, false)

        scene.idle(40)
        scene.markAsFinished()
    }

    /** Cycles through every registered variant for a single Stargate family. */
    private fun variantCrystalDisplay(scene: SceneBuilder, util: SceneBuildingUtil, gateType: GateType) {
        scene.title(gateType.sceneId, gateType.title)
        scene.configureBasePlate(0, 0, 7)

        showBaseAndPedestal(scene, util)
        showVariantDisplayBoard(scene, util)

        scene.scaleSceneView(DISPLAY_SCENE_SCALE)
        scene.setSceneOffsetY(DISPLAY_SCENE_OFFSET_Y)
        scene.rotateCameraY(DISPLAY_SCENE_ROTATION_Y)

        revealGate(scene, util)
        setGate(scene, util, gateType, gateType.defaultNbt, true)
        scene.idle(20)
        spinDisplayBoard(scene, util)
        baseVariantDisplay(scene, util, gateType)

        // Data-pack variants are discovered at playback time from the active client registry.
        getVariantsForType(gateType.id).forEach { variantName ->
            SGJPonder.LOGGER.info("Adding variant: {} for: {}", variantName, gateType.id)
            variantDisplay(scene, util, gateType, variantName)
        }

        scene.markAsFinished()
    }

    /** Shows the implicit unmodified gate before the registry-provided variants. */
    private fun baseVariantDisplay(scene: SceneBuilder, util: SceneBuildingUtil, gateType: GateType) {
        SGJPonder.LOGGER.info("Adding implicit base variant for: {}", gateType.id)
        setDisplayBoardText(
            scene,
            util,
            gateType.id.namespace,
            gateType.id.path,
            gateType.id.namespace,
            "base",
        )
        setGate(scene, util, gateType, gateType.defaultNbt, false)
        scene.idle(5)
        scene.addKeyframe()
        scene.idle(40)
    }

    /** Updates the display board and gate NBT for one named registry variant. */
    private fun variantDisplay(
        scene: SceneBuilder,
        util: SceneBuildingUtil,
        gateType: GateType,
        variantName: String,
    ) {
        val variantId = ResourceLocation(variantName)
        setDisplayBoardText(
            scene,
            util,
            gateType.id.namespace,
            gateType.id.path,
            variantId.namespace,
            variantId.path,
        )
        setGate(
            scene,
            util,
            gateType,
            Consumer { nbt -> gateType.variantNbt.accept(nbt, variantName) },
            false,
        )
        scene.idle(5)
        scene.addKeyframe()
        scene.idle(40)
    }

    /** Reads all variants whose declared base Stargate matches [type]. */
    private fun getVariantsForType(type: ResourceLocation): List<String> {
        val level = Minecraft.getInstance().level ?: return emptyList()
        val registry = level.registryAccess().registryOrThrow(StargateVariant.REGISTRY_KEY)
        val variants = mutableListOf<String>()

        SGJPonder.LOGGER.info("Loading variants for {}", type)
        registry.asLookup().listElements().forEach { entry ->
            val variantName = entry.key().location().toString()
            if (type == entry.value().baseStargate) {
                variants += variantName
            }
        }
        SGJPonder.LOGGER.info("Loaded {} variants of type {}", variants.size, type)
        return variants
    }

    /** Reveals the base plate and pedestal before the Stargate appears. */
    private fun showBaseAndPedestal(scene: SceneBuilder, util: SceneBuildingUtil) {
        scene.world().showSection(util.select().layer(0), Direction.UP)
        scene.idle(5)
        scene.world().showSection(util.select().fromTo(0, 1, 0, 7, 1, 3), Direction.SOUTH)
        scene.idle(10)
    }

    /** Reveals the tall flap display one horizontal layer at a time. */
    private fun showVariantDisplayBoard(scene: SceneBuilder, util: SceneBuildingUtil) {
        for (y in 1..9) {
            scene.world().showSection(util.select().fromTo(6, y, 6, 0, y, 6), Direction.DOWN)
            scene.idle(1)
        }
    }

    /** Reveals the block-entity position occupied by the current Stargate. */
    private fun revealGate(scene: SceneBuilder, util: SceneBuildingUtil) {
        scene.world().showSection(util.select().position(util.grid().at(3, 1, 4)), Direction.DOWN)
    }

    /** Replaces the gate block and applies the selected family's NBT mutation. */
    private fun setGate(
        scene: SceneBuilder,
        util: SceneBuildingUtil,
        gateType: GateType,
        nbtWriter: Consumer<CompoundTag>,
        updateBlock: Boolean,
    ) {
        val gate = util.grid().at(3, 1, 4)
        scene.world().setBlock(gate, gateType.block.get().defaultBlockState(), updateBlock)
        scene.world().modifyBlockEntityNBT(
            util.select().position(gate),
            gateType.entityClass,
            nbtWriter,
            true,
        )
    }

    /** Assigns kinetic speed to every flap-display block in the two display rows. */
    private fun spinDisplayBoard(scene: SceneBuilder, util: SceneBuildingUtil) {
        for (x in 0..6) {
            for (y in 8..9) {
                scene.world().modifyBlockEntity(
                    util.grid().at(x, y, 6),
                    KineticBlockEntity::class.java,
                ) { display -> display.speed = 192.0f }
            }
        }
    }

    /** Writes the gate-family and variant resource IDs across the four display rows. */
    private fun setDisplayBoardText(
        scene: SceneBuilder,
        util: SceneBuildingUtil,
        typeNamespace: String,
        typePath: String,
        variantNamespace: String,
        variantPath: String,
    ) {
        val display = util.grid().at(6, 9, 6)
        scene.world().modifyBlockEntity(
            display,
            FlapDisplayBlockEntity::class.java,
        ) { board ->
            board.applyTextManually(0, componentJson(typeNamespace))
            board.applyTextManually(1, componentJson(typePath))
            board.applyTextManually(2, componentJson(variantNamespace))
            board.applyTextManually(3, componentJson(variantPath))
        }
    }

    /** Serializes literal display text in the JSON form required by Ponder 1.20.1. */
    private fun componentJson(text: String): String = Component.Serializer.toJson(Component.literal(text))

    /** Writes the shared variant and demonstration address fields into gate NBT. */
    private fun putVariantAddress(nbt: CompoundTag, variant: String) {
        nbt.putString("Variant", variant)
        nbt.putIntArray("Address", DEMO_ADDRESS)
    }

    /** All family-specific data and NBT writers required by the shared storyboard logic. */
    private data class GateType(
        val id: ResourceLocation,
        val sceneId: String,
        val title: String,
        val block: Supplier<out Block>,
        val entityClass: Class<out BlockEntity>,
        val defaultNbt: Consumer<CompoundTag>,
        val variantNbt: BiConsumer<CompoundTag, String>,
    )
}
