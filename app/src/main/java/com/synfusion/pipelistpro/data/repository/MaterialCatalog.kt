package com.synfusion.pipelistpro.data.repository

import com.synfusion.pipelistpro.data.models.MaterialItem
import com.synfusion.pipelistpro.data.models.CartItem
import java.util.UUID

object MaterialCatalog {
    val commonSizes = listOf("½\"", "¾\"", "1\"", "1¼\"", "1½\"", "2\"", "2½\"", "3\"", "4\"", "6\"")
    val reducerSizes = listOf("¾\" × ½\"", "1\" × ¾\"", "1¼\" × 1\"", "1½\" × 1¼\"", "2\" × 1½\"", "3\" × 2\"", "4\" × 3\"")
    val drainageSizes = listOf("75mm", "110mm", "160mm")

    val materials = listOf(
        // UPVC
        MaterialItem("upvc_pipe", "UPVC Pipe", "UPVC", commonSizes, "ft", listOf("pipe", "upvc", "cold water")),
        MaterialItem("upvc_elbow", "UPVC Elbow 90°", "UPVC", commonSizes, "pcs", listOf("bend", "corner", "upvc")),
        MaterialItem("upvc_tee", "UPVC Tee", "UPVC", commonSizes, "pcs", listOf("t-joint", "three-way", "upvc")),
        MaterialItem("upvc_reducer", "UPVC Reducer", "UPVC", reducerSizes, "pcs", listOf("upvc reducer")),
        MaterialItem("upvc_coupler", "UPVC Coupler", "UPVC", commonSizes, "pcs", listOf("upvc coupler")),
        MaterialItem("upvc_union", "UPVC Union", "UPVC", commonSizes, "pcs", listOf("upvc union")),
        MaterialItem("upvc_fta", "UPVC FTA", "UPVC", commonSizes, "pcs", listOf("upvc fta")),
        MaterialItem("upvc_mta", "UPVC MTA", "UPVC", commonSizes, "pcs", listOf("upvc mta")),
        MaterialItem("upvc_ball_valve", "UPVC Ball Valve", "UPVC", commonSizes, "pcs", listOf("upvc ball valve")),
        MaterialItem("upvc_end_cap", "UPVC End Cap", "UPVC", commonSizes, "pcs", listOf("upvc end cap")),
        MaterialItem("upvc_solvent", "Solvent Cement", "UPVC", listOf("100ml", "250ml", "500ml"), "ltr", listOf("solution", "upvc glue", "pvc solution")),

        // CPVC
        MaterialItem("cpvc_pipe", "CPVC Pipe", "CPVC", commonSizes, "ft", listOf("pipe", "cpvc", "hot water")),
        MaterialItem("cpvc_elbow", "CPVC Elbow 90°", "CPVC", commonSizes, "pcs", listOf("bend", "corner", "cpvc")),
        MaterialItem("cpvc_tee", "CPVC Tee", "CPVC", commonSizes, "pcs", listOf("t-joint", "three-way", "cpvc")),
        MaterialItem("cpvc_reducer", "CPVC Reducer", "CPVC", reducerSizes, "pcs", listOf("reducing coupler", "cpvc")),
        MaterialItem("cpvc_coupler", "CPVC Coupler", "CPVC", commonSizes, "pcs", listOf("joiner", "socket", "cpvc")),
        MaterialItem("cpvc_union", "CPVC Union", "CPVC", commonSizes, "pcs", listOf("joint", "detachable", "cpvc")),
        MaterialItem("cpvc_fta", "CPVC FTA", "CPVC", commonSizes, "pcs", listOf("female thread adapter", "brass thread", "cpvc")),
        MaterialItem("cpvc_mta", "CPVC MTA", "CPVC", commonSizes, "pcs", listOf("male thread adapter", "cpvc")),
        MaterialItem("cpvc_ball_valve", "CPVC Ball Valve", "CPVC", commonSizes, "pcs", listOf("stopcock", "handle valve", "cpvc")),
        MaterialItem("cpvc_end_cap", "CPVC End Cap", "CPVC", commonSizes, "pcs", listOf("plug", "stopper", "cpvc")),
        MaterialItem("cpvc_solution", "CPVC Solution", "CPVC", listOf("100ml", "250ml", "500ml"), "ltr", listOf("glue", "suleshan", "bond", "cpvc")),

        // PVC
        MaterialItem("pvc_pipe", "PVC Pipe", "PVC", commonSizes, "ft", listOf("pipe", "pvc")),
        MaterialItem("pvc_elbow", "PVC Elbow", "PVC", commonSizes, "pcs", listOf("pvc elbow")),
        MaterialItem("pvc_tee", "PVC Tee", "PVC", commonSizes, "pcs", listOf("pvc tee")),
        MaterialItem("pvc_coupler", "PVC Coupler", "PVC", commonSizes, "pcs", listOf("pvc coupler")),

        // SWR
        MaterialItem("swr_pipe", "SWR Pipe", "SWR", drainageSizes, "ft", listOf("drainage pipe", "swr")),
        MaterialItem("swr_bend", "SWR Bend", "SWR", drainageSizes, "pcs", listOf("elbow", "swr bend")),
        MaterialItem("swr_tee", "SWR Tee", "SWR", drainageSizes, "pcs", listOf("swr tee")),
        MaterialItem("swr_coupler", "SWR Coupler", "SWR", drainageSizes, "pcs", listOf("swr coupler")),
        MaterialItem("swr_door_bend", "SWR Door Bend", "SWR", drainageSizes, "pcs", listOf("cleaning bend", "door elbow")),
        MaterialItem("swr_cleaning_pipe", "SWR Cleaning Pipe", "SWR", drainageSizes, "pcs", listOf("door pipe")),
        MaterialItem("nahani_trap", "Nahani Trap", "SWR", listOf("3\"", "4\""), "pcs", listOf("floor trap")),
        MaterialItem("floor_trap", "Floor Trap", "SWR", listOf("3\"", "4\""), "pcs", listOf("multitrap")),

        // GI
        MaterialItem("gi_pipe", "GI Pipe", "GI", commonSizes, "ft", listOf("iron pipe", "gi")),
        MaterialItem("gi_elbow", "GI Elbow", "GI", commonSizes, "pcs", listOf("gi elbow")),
        MaterialItem("gi_tee", "GI Tee", "GI", commonSizes, "pcs", listOf("gi tee")),
        MaterialItem("gi_union", "GI Union", "GI", commonSizes, "pcs", listOf("gi union")),

        // HDPE
        MaterialItem("hdpe_pipe", "HDPE Pipe", "HDPE", commonSizes, "ft", listOf("hdpe", "black pipe")),
        MaterialItem("hdpe_coupler", "HDPE Coupler", "HDPE", commonSizes, "pcs", listOf("hdpe coupler")),

        // Tools/Other
        MaterialItem("teflon_tape", "Teflon Tape", "Tools/Other", listOf("Standard"), "pcs", listOf("thread tape", "white tape")),
        MaterialItem("pipe_clamp", "Pipe Clamp", "Tools/Other", commonSizes, "pcs", listOf("clamp", "bracket")),
        MaterialItem("tank_connector", "Tank Connector", "Tools/Other", commonSizes, "pcs", listOf("tank nipple")),
        MaterialItem("float_valve", "Float Valve", "Tools/Other", listOf("¾\"", "1\""), "pcs", listOf("ball cock")),
        MaterialItem("nrv", "NRV", "Tools/Other", commonSizes, "pcs", listOf("non return valve", "check valve")),
        MaterialItem("grating", "Grating", "Tools/Other", listOf("3\"", "4\"", "5\""), "pcs", listOf("jali", "trap cover")),
        MaterialItem("angle_valve", "Angle Valve", "Tools/Other", listOf("Standard"), "pcs", listOf("stop tap")),
        MaterialItem("health_faucet", "Health Faucet", "Tools/Other", listOf("Standard"), "pcs", listOf("hand spray", "jet spray")),
        MaterialItem("shower", "Shower", "Tools/Other", listOf("Standard"), "pcs", listOf("head shower")),
        MaterialItem("bib_cock", "Bib Cock", "Tools/Other", listOf("Standard"), "pcs", listOf("tap", "faucet")),
        MaterialItem("pillar_cock", "Pillar Cock", "Tools/Other", listOf("Standard"), "pcs", listOf("basin tap")),
        MaterialItem("sink_cock", "Sink Cock", "Tools/Other", listOf("Standard"), "pcs", listOf("kitchen tap")),
        MaterialItem("waste_coupling", "Waste Coupling", "Tools/Other", listOf("Standard"), "pcs", listOf("basin waste")),
        MaterialItem("waste_pipe", "Waste Pipe", "Tools/Other", listOf("Standard"), "pcs", listOf("flexible pipe")),
        MaterialItem("flush_tank", "Flush Tank", "Tools/Other", listOf("Standard"), "pcs", listOf("cistern")),
        MaterialItem("bottle_trap", "Bottle Trap", "Tools/Other", listOf("Standard"), "pcs", listOf("basin trap"))
    )

    fun getBathroomTemplate(): List<CartItem> = listOf(
        CartItem(materialId = "cpvc_pipe", name = "CPVC Pipe", category = "CPVC", size = "¾\"", quantity = 5, unit = "ft"),
        CartItem(materialId = "cpvc_elbow", name = "CPVC Elbow 90°", category = "CPVC", size = "¾\"", quantity = 10, unit = "pcs"),
        CartItem(materialId = "cpvc_tee", name = "CPVC Tee", category = "CPVC", size = "¾\"", quantity = 5, unit = "pcs"),
        CartItem(materialId = "angle_valve", name = "Angle Valve", category = "Tools/Other", size = "Standard", quantity = 2, unit = "pcs"),
        CartItem(materialId = "health_faucet", name = "Health Faucet", category = "Tools/Other", size = "Standard", quantity = 1, unit = "pcs"),
        CartItem(materialId = "teflon_tape", name = "Teflon Tape", category = "Tools/Other", size = "Standard", quantity = 2, unit = "pcs"),
        CartItem(materialId = "cpvc_solution", name = "CPVC Solution", category = "CPVC", size = "100ml", quantity = 1, unit = "ltr")
    )
}
