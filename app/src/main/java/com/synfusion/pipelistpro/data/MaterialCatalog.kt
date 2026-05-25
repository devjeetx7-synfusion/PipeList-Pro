package com.synfusion.pipelistpro.data

import com.synfusion.pipelistpro.model.MaterialItem
import com.synfusion.pipelistpro.model.ProjectItem
import java.util.UUID

object MaterialCatalog {
    val commonSizes = listOf("½\"", "¾\"", "1\"", "1¼\"", "1½\"", "2\"", "2½\"", "3\"", "4\"", "6\"")
    val reducerSizes = listOf("¾\" × ½\"", "1\" × ¾\"", "1¼\" × 1\"", "1½\" × 1¼\"", "2\" × 1½\"", "3\" × 2\"", "4\" × 3\"")
    val drainageSizes = listOf("75mm", "110mm", "160mm")

    val materials = listOf(
        // CPVC
        MaterialItem("cpvc_pipe", "CPVC Pipe", "CPVC", commonSizes, "length", listOf("pipe", "cpvc", "hot water")),
        MaterialItem("cpvc_elbow", "CPVC Elbow 90°", "CPVC", commonSizes, "pcs", listOf("bend", "corner", "cpvc")),
        MaterialItem("cpvc_tee", "CPVC Tee", "CPVC", commonSizes, "pcs", listOf("t-joint", "three-way", "cpvc")),
        MaterialItem("cpvc_reducer", "CPVC Reducer", "CPVC", reducerSizes, "pcs", listOf("reducing coupler", "cpvc")),
        MaterialItem("cpvc_coupler", "CPVC Coupler", "CPVC", commonSizes, "pcs", listOf("joiner", "socket", "cpvc")),
        MaterialItem("cpvc_union", "CPVC Union", "CPVC", commonSizes, "pcs", listOf("joint", "detachable", "cpvc")),
        MaterialItem("cpvc_fta", "CPVC FTA", "CPVC", commonSizes, "pcs", listOf("female thread adapter", "brass thread", "cpvc")),
        MaterialItem("cpvc_mta", "CPVC MTA", "CPVC", commonSizes, "pcs", listOf("male thread adapter", "cpvc")),
        MaterialItem("cpvc_ball_valve", "CPVC Ball Valve", "CPVC", commonSizes, "pcs", listOf("stopcock", "handle valve", "cpvc")),
        MaterialItem("cpvc_end_cap", "CPVC End Cap", "CPVC", commonSizes, "pcs", listOf("plug", "stopper", "cpvc")),
        MaterialItem("cpvc_solution", "CPVC Solution", "Accessories", listOf("100ml", "250ml", "500ml"), "tin", listOf("glue", "suleshan", "bond", "cpvc")),

        // UPVC
        MaterialItem("upvc_pipe", "UPVC Pipe", "UPVC", commonSizes, "length", listOf("pipe", "upvc", "cold water")),
        MaterialItem("upvc_elbow", "UPVC Elbow 90°", "UPVC", commonSizes, "pcs", listOf("bend", "corner", "upvc")),
        MaterialItem("upvc_tee", "UPVC Tee", "UPVC", commonSizes, "pcs", listOf("t-joint", "three-way", "upvc")),
        MaterialItem("upvc_reducer", "UPVC Reducer", "UPVC", reducerSizes, "pcs", listOf("upvc reducer")),
        MaterialItem("upvc_coupler", "UPVC Coupler", "UPVC", commonSizes, "pcs", listOf("upvc coupler")),
        MaterialItem("upvc_union", "UPVC Union", "UPVC", commonSizes, "pcs", listOf("upvc union")),
        MaterialItem("upvc_fta", "UPVC FTA", "UPVC", commonSizes, "pcs", listOf("upvc fta")),
        MaterialItem("upvc_mta", "UPVC MTA", "UPVC", commonSizes, "pcs", listOf("upvc mta")),
        MaterialItem("upvc_ball_valve", "UPVC Ball Valve", "UPVC", commonSizes, "pcs", listOf("upvc ball valve")),
        MaterialItem("upvc_end_cap", "UPVC End Cap", "UPVC", commonSizes, "pcs", listOf("upvc end cap")),
        MaterialItem("upvc_solvent", "Solvent Cement", "Accessories", listOf("100ml", "250ml", "500ml"), "tin", listOf("solution", "upvc glue", "pvc solution")),

        // SWR / Drainage
        MaterialItem("swr_pipe", "SWR Pipe", "SWR", drainageSizes, "length", listOf("drainage pipe", "swr")),
        MaterialItem("swr_bend", "SWR Bend", "SWR", drainageSizes, "pcs", listOf("elbow", "swr bend")),
        MaterialItem("swr_tee", "SWR Tee", "SWR", drainageSizes, "pcs", listOf("swr tee")),
        MaterialItem("swr_coupler", "SWR Coupler", "SWR", drainageSizes, "pcs", listOf("swr coupler")),
        MaterialItem("swr_door_bend", "SWR Door Bend", "SWR", drainageSizes, "pcs", listOf("cleaning bend", "door elbow")),
        MaterialItem("swr_cleaning_pipe", "SWR Cleaning Pipe", "SWR", drainageSizes, "pcs", listOf("door pipe")),
        MaterialItem("nahani_trap", "Nahani Trap", "SWR", listOf("3\"", "4\""), "pcs", listOf("floor trap")),
        MaterialItem("floor_trap", "Floor Trap", "SWR", listOf("3\"", "4\""), "pcs", listOf("multitrap")),
        MaterialItem("grating", "Grating", "Accessories", listOf("3\"", "4\"", "5\""), "pcs", listOf("jali", "trap cover")),

        // Bathroom / Sanitary
        MaterialItem("angle_valve", "Angle Valve", "Bathroom", listOf("Standard"), "pcs", listOf("stop tap")),
        MaterialItem("health_faucet", "Health Faucet", "Bathroom", listOf("Standard"), "pcs", listOf("hand spray", "jet spray")),
        MaterialItem("shower", "Shower", "Bathroom", listOf("Standard"), "pcs", listOf("head shower")),
        MaterialItem("bib_cock", "Bib Cock", "Bathroom", listOf("Standard"), "pcs", listOf("tap", "faucet")),
        MaterialItem("pillar_cock", "Pillar Cock", "Bathroom", listOf("Standard"), "pcs", listOf("basin tap")),
        MaterialItem("sink_cock", "Sink Cock", "Bathroom", listOf("Standard"), "pcs", listOf("kitchen tap")),
        MaterialItem("waste_coupling", "Waste Coupling", "Bathroom", listOf("Standard"), "pcs", listOf("basin waste")),
        MaterialItem("waste_pipe", "Waste Pipe", "Bathroom", listOf("Standard"), "pcs", listOf("flexible pipe")),
        MaterialItem("flush_tank", "Flush Tank", "Bathroom", listOf("Standard"), "pcs", listOf("cistern")),
        MaterialItem("bottle_trap", "Bottle Trap", "Bathroom", listOf("Standard"), "pcs", listOf("basin trap")),

        // Accessories
        MaterialItem("teflon_tape", "Teflon Tape", "Accessories", listOf("Standard"), "roll", listOf("thread tape", "white tape")),
        MaterialItem("pipe_clamp", "Pipe Clamp", "Accessories", commonSizes, "pcs", listOf("clamp", "bracket")),
        MaterialItem("tank_connector", "Tank Connector", "Accessories", commonSizes, "pcs", listOf("tank nipple")),
        MaterialItem("float_valve", "Float Valve", "Accessories", listOf("¾\"", "1\""), "pcs", listOf("ball cock")),
        MaterialItem("nrv", "NRV", "Accessories", commonSizes, "pcs", listOf("non return valve", "check valve"))
    )

    fun getBathroomTemplate(): List<ProjectItem> = listOf(
        ProjectItem("CPVC Pipe", "CPVC", "¾\"", 5, "length"),
        ProjectItem("CPVC Elbow 90°", "CPVC", "¾\"", 10, "pcs"),
        ProjectItem("CPVC Tee", "CPVC", "¾\"", 5, "pcs"),
        ProjectItem("Angle Valve", "Bathroom", "Standard", 2, "pcs"),
        ProjectItem("Health Faucet", "Bathroom", "Standard", 1, "pcs"),
        ProjectItem("Teflon Tape", "Accessories", "Standard", 2, "roll"),
        ProjectItem("CPVC Solution", "Accessories", "100ml", 1, "tin")
    )

    fun getKitchenTemplate(): List<ProjectItem> = listOf(
        ProjectItem("CPVC Pipe", "CPVC", "¾\"", 3, "length"),
        ProjectItem("CPVC Elbow 90°", "CPVC", "¾\"", 6, "pcs"),
        ProjectItem("Sink Cock", "Bathroom", "Standard", 1, "pcs"),
        ProjectItem("Waste Pipe", "Bathroom", "Standard", 1, "pcs"),
        ProjectItem("Teflon Tape", "Accessories", "Standard", 1, "roll")
    )

    fun getWaterTankTemplate(): List<ProjectItem> = listOf(
        ProjectItem("UPVC Pipe", "UPVC", "1\"", 2, "length"),
        ProjectItem("Tank Connector", "Accessories", "1\"", 1, "pcs"),
        ProjectItem("Ball Valve", "UPVC", "1\"", 1, "pcs"),
        ProjectItem("Union", "UPVC", "1\"", 1, "pcs"),
        ProjectItem("Float Valve", "Accessories", "¾\"", 1, "pcs")
    )

    fun getDrainageTemplate(): List<ProjectItem> = listOf(
        ProjectItem("SWR Pipe", "SWR", "110mm", 3, "length"),
        ProjectItem("SWR Bend", "SWR", "110mm", 4, "pcs"),
        ProjectItem("Nahani Trap", "SWR", "4\"", 1, "pcs"),
        ProjectItem("Grating", "Accessories", "4\"", 1, "pcs")
    )
}
