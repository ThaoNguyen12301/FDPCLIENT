/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/SkidderMC/FDPClient/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.Category
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.aac.*
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.hypixel.HypixelHop
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.matrix.*
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.ncp.*
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.other.*
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.spartan.SpartanYPort
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.spectre.SpectreBHop
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.spectre.SpectreLowHop
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.spectre.SpectreOnGround
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.verus.NewVerusLowHop
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.verus.VerusHop
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.verus.VerusLowHop
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.verus.VerusSpeeds
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.vulcan.VulcanGround288
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.vulcan.VulcanHop
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.vulcan.VulcanLowHop
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.ListValue

object Speed : Module("Speed", Category.MOVEMENT, hideModule = false) {

    private val speedModes = arrayOf(

        // NCP
        NCPBHop,
        NCPFHop,
        SNCPBHop,
        NCPHop,
        NCPYPort,
        UNCPHop,
        UNCPHop2,

        // AAC
        AACHop3313,
        AACHop350,
        AACHop4,
        AACHop5,

        // Spartan
        SpartanYPort,

        // Spectre
        SpectreLowHop,
        SpectreBHop,
        SpectreOnGround,

        // Verus
        VerusHop,
        VerusLowHop,
        NewVerusLowHop,
        VerusSpeeds,

        // Vulcan
        VulcanHop,
        VulcanLowHop,
        VulcanGround288,

        // Matrix
        OldMatrixHop,
        MatrixHop,
        MatrixSlowHop,
        MatrixSpeeds,

        // Server specific
        TeleportCubeCraft,
        HypixelHop,

        // Other
        BlocksMCSpeed,
        Boost,
        Frame,
        MiJump,
        OnGround,
        SlowHop,
        Legit,
        CustomSpeed,
        MineBlazeHop,
        MineBlazeTimer
    )

    private val modes = speedModes.map { it.modeName }.toTypedArray()

    val mode by object : ListValue("Mode", modes, "NCPBHop") {
        override fun onChange(oldValue: String, newValue: String): String {
            if (state)
                onDisable()

            return super.onChange(oldValue, newValue)
        }

        override fun onChanged(oldValue: String, newValue: String) {
            if (state)
                onEnable()
        }
    }
    // Custom
    val customSpeed by FloatValue("CustomSpeed", 1.6f, 0.2f..2f) { mode == "Custom" }
    val customY by FloatValue("CustomY", 0f, 0f..4f) { mode == "Custom" }
    val customTimer by FloatValue("CustomTimer", 1f, 0.1f..2f) { mode == "Custom" }
    val customStrafe by BoolValue("CustomStrafe", true) { mode == "Custom" }
    val resetXZ by BoolValue("CustomResetXZ", false) { mode == "Custom" }
    val resetY by BoolValue("CustomResetY", false) { mode == "Custom" }

    // Verus
    val verusSpeed by ListValue("Verus-Mode", arrayOf("OldHop", "Float", "Ground", "YPort", "YPort2"), "OldHop") { mode == "VerusSpeeds" }
    val verusYPortspeedValue by FloatValue("YPort-Speed", 0.61f, 0.1f.. 1f)  { mode == "VerusSpeeds" }
    val verusYPort2speedValue by FloatValue("YPort2-Speed", 0.61f, 0.1f.. 1f)  { mode == "VerusSpeeds" }

    // Matrix
    val matrixSpeed by ListValue("Matrix-Mode", arrayOf("MatrixHop2", "Matrix6.6.1", "Matrix6.9.2"), "MatrixHop2") { mode == "MatrixSpeeds" }
    val matrixGroundStrafe by BoolValue("GroundStrafe-Hop2", false) { mode == "MatrixSpeeds" }
    val matrixVeloBoostValue by BoolValue("VelocBoost-6.6.1", true) { mode == "MatrixSpeeds" }
    val matrixTimerBoostValue by BoolValue("TimerBoost-6.6.1", false) { mode == "MatrixSpeeds" }
    val matrixUsePreMotion by BoolValue("UsePreMotion6.6.1", false) { mode == "MatrixSpeeds" }

    // Server
    val cubecraftPortLength by FloatValue("CubeCraft-PortLength", 1f, 0.1f..2f) { mode == "TeleportCubeCraft" }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val thePlayer = mc.thePlayer ?: return

        if (thePlayer.isSneaking)
            return

        if (isMoving && !sprintManually)
            thePlayer.isSprinting = true

        modeModule.onUpdate()
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        val thePlayer = mc.thePlayer ?: return

        if (thePlayer.isSneaking || event.eventState != EventState.PRE)
            return

        if (isMoving && !sprintManually)
            thePlayer.isSprinting = true

        modeModule.onMotion()
    }

    @EventTarget
    fun onMove(event: MoveEvent) {
        if (mc.thePlayer.isSneaking)
            return

        modeModule.onMove(event)
    }

    @EventTarget
    fun onTick(event: GameTickEvent) {
        if (mc.thePlayer.isSneaking)
            return

        modeModule.onTick()
    }

    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        if (mc.thePlayer.isSneaking)
            return

        modeModule.onStrafe()
    }

    @EventTarget
    fun onJump(event: JumpEvent) {
        if (mc.thePlayer.isSneaking)
            return

        modeModule.onJump(event)
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (mc.thePlayer.isSneaking)
            return

        modeModule.onPacket(event)
    }

    override fun onEnable() {
        if (mc.thePlayer == null)
            return

        mc.timer.timerSpeed = 1f

        modeModule.onEnable()
    }

    override fun onDisable() {
        if (mc.thePlayer == null)
            return

        mc.timer.timerSpeed = 1f
        mc.thePlayer.speedInAir = 0.02f

        modeModule.onDisable()
    }

    override val tag
        get() = mode

    private val modeModule
        get() = speedModes.find { it.modeName == mode }!!

    private val sprintManually
        // Maybe there are more but for now there's the Legit mode.
        get() = modeModule in arrayOf(Legit)
}
