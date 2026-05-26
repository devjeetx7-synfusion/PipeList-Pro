package com.synfusion.pipelistpro

import com.synfusion.pipelistpro.data.models.MaterialItem
import com.synfusion.pipelistpro.core.utils.SearchUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchUtilsTest {

    private val testMaterials = listOf(
        MaterialItem("1", "UPVC Elbow", "UPVC", listOf("1\""), "pcs", listOf("bend")),
        MaterialItem("2", "CPVC Tee", "CPVC", listOf("¾\""), "pcs", listOf("three-way")),
        MaterialItem("3", "Solvent Cement", "Accessories", listOf("100ml"), "tin", listOf("suleshan"))
    )

    @Test
    fun testSearchByName() {
        val result = SearchUtils.filterMaterials(testMaterials, "elbow")
        assertEquals(1, result.size)
        assertEquals("UPVC Elbow", result[0].name)
    }

    @Test
    fun testSearchByKeyword() {
        val result = SearchUtils.filterMaterials(testMaterials, "bend")
        assertEquals(1, result.size)
        assertEquals("UPVC Elbow", result[0].name)
    }

    @Test
    fun testSearchByIndianSynonym() {
        val result = SearchUtils.filterMaterials(testMaterials, "suleshan")
        assertEquals(1, result.size)
        assertEquals("Solvent Cement", result[0].name)
    }

    @Test
    fun testMultipleQueryParts() {
        val result = SearchUtils.filterMaterials(testMaterials, "upvc elbow")
        assertEquals(1, result.size)
        assertEquals("UPVC Elbow", result[0].name)
    }

    @Test
    fun testEmptyQueryReturnsAll() {
        val result = SearchUtils.filterMaterials(testMaterials, "")
        assertEquals(3, result.size)
    }
}
