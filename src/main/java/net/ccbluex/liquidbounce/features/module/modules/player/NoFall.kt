/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.player

import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.Category
import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.aac.AAC
import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.aac.AAC3311
import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.aac.AAC3315
import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.aac.LAAC
import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.other.*
import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.other.Blink
import net.ccbluex.liquidbounce.features.module.modules.visual.FreeCam
import net.ccbluex.liquidbounce.utils.block.BlockUtils.collideBlock
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.block.BlockLiquid
import net.minecraft.util.AxisAlignedBB.fromBounds

object NoFall : Module("NoFall", Category.PLAYER, hideModule = false) {
    private val noFallModes = arrayOf(
        SpoofGround,
        NoGround,
        Packet,
        MLG,
        AAC,
        LAAC,
        AAC3311,
        AAC3315,
        Cancel,
        Spartan,
        CubeCraft,
        Hypixel,
        Blink,
        VulcanFast288
    )

    private val modes = noFallModes.map { it.modeName }.toTypedArray()

    val mode by ListValue("Mode", modes, "SpoofGround")

    val minFallDistance by FloatValue("MinMLGHeight", 5f, 2f..50f, subjective = true) { mode == "MLG" }
    val retrieveDelay by IntegerValue("RetrieveDelay", 100, 100..500, subjective = true) { mode == "MLG" }

    // Using too many times of simulatePlayer could result timer flag. Hence, why this is disabled by default.
    val checkFallDist by BoolValue("CheckFallDistance", false, subjective = true)

    val minFallDist: FloatValue = object : FloatValue("MinFallDistance", 2.5f, 0f..10f, subjective = true) {
        override fun isSupported() = mode == "Blink" && checkFallDist
        override fun onChange(oldValue: Float, newValue: Float) = newValue.coerceAtMost(maxFallDist.get())
    }
    val maxFallDist: FloatValue = object : FloatValue("MaxFallDistance", 20f, 0f..100f, subjective = true) {
        override fun isSupported() = mode == "Blink" && checkFallDist
        override fun onChange(oldValue: Float, newValue: Float) = newValue.coerceAtLeast(minFallDist.get())
    }

    val autoOff by BoolValue("AutoOff", true) { mode == "Blink" }
    val simulateDebug by BoolValue("SimulationDebug", false, subjective = true) { mode == "Blink" }
    val fakePlayer by BoolValue("FakePlayer", true, subjective = true) { mode == "Blink" }

    override fun onEnable() {
        modeModule.onEnable()
    }

    override fun onDisable() {
        modeModule.onDisable()
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val thePlayer = mc.thePlayer

        if (FreeCam.handleEvents()) return

        if (collideBlock(thePlayer.entityBoundingBox) { it is BlockLiquid } || collideBlock(
                fromBounds(
                    thePlayer.entityBoundingBox.maxX,
                    thePlayer.entityBoundingBox.maxY,
                    thePlayer.entityBoundingBox.maxZ,
                    thePlayer.entityBoundingBox.minX,
                    thePlayer.entityBoundingBox.minY - 0.01,
                    thePlayer.entityBoundingBox.minZ
                )
            ) { it is BlockLiquid }
        ) return

        modeModule.onUpdate()
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        modeModule.onRender3D(event)
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        mc.thePlayer ?: return

        modeModule.onPacket(event)
    }

    @EventTarget
    fun onBB(event: BlockBBEvent) {
        mc.thePlayer ?: return

        modeModule.onBB(event)
    }

    // Ignore condition used in LAAC mode
    @EventTarget(ignoreCondition = true)
    fun onJump(event: JumpEvent) {
        modeModule.onJump(event)
    }

    @EventTarget
    fun onStep(event: StepEvent) {
        modeModule.onStep(event)
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        modeModule.onMotion(event)
    }

    @EventTarget
    fun onMove(event: MoveEvent) {
        val thePlayer = mc.thePlayer

        if (collideBlock(thePlayer.entityBoundingBox) { it is BlockLiquid }
            || collideBlock(
                fromBounds(
                    thePlayer.entityBoundingBox.maxX,
                    thePlayer.entityBoundingBox.maxY,
                    thePlayer.entityBoundingBox.maxZ,
                    thePlayer.entityBoundingBox.minX,
                    thePlayer.entityBoundingBox.minY - 0.01,
                    thePlayer.entityBoundingBox.minZ
                )
            ) { it is BlockLiquid }
        ) return

        modeModule.onMove(event)
    }

    override val tag
        get() = mode

    private val modeModule
        get() = noFallModes.find { it.modeName == mode }!!
}