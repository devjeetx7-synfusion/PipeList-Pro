package com.synfusion.pipelistpro

import com.synfusion.pipelistpro.data.models.MaterialItem
import com.synfusion.pipelistpro.core.utils.SearchUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchUtilsTest {

    private val testMaterials = listOf(
        MaterialItem(
            id = "1",
            categoryId = "upvc",
            categoryName = "UPVC",
            system = "Cold Water",
            group = "Fitting",
            name = "UPVC Elbow",
            alias = "bend",
            sizes = listOf("1\""),
            unit = "pcs",
            jointType = "Solvent",
            purpose = "Turn",
            notes = ""
        ),
        MaterialItem(
            id = "2",
            categoryId = "cpvc",
            categoryName = "CPVC",
            system = "Hot Water",
            group = "Fitting",
            name = "CPVC Tee",
            alias = "three-way",
            sizes = listOf("¾\""),
            unit = "pcs",
            jointType = "Solvent",
            purpose = "Branch",
            notes = ""
        ),
        MaterialItem(
            id = "3",
            categoryId = "accessories",
            categoryName = "Accessories",
            system = "All",
            group = "Consumables",
            name = "Solvent Cement",
            alias = "suleshan",
            sizes = listOf("100ml"),
            unit = "tin",
            jointType = "",
            purpose = "Bonding",
            notes = ""
        )
    )

    @Test
    fun testSearchByName() {
        val result = SearchUtils.filterMaterials(testMaterials, "elbow")
        assertEquals(1, result.size)
        assertEquals("UPVC Elbow", result[0].name)
    }

    @Test
    fun testSearchByAlias() {
        val result = SearchUtils.filterMaterials(testMaterials, "bend")
        assertEquals(1, result.size)
        assertEquals("UPVC Elbow", result[0].name)
    }

    @Test
    fun testSearchByGroup() {
        val result = SearchUtils.filterMaterials(testMaterials, "Consumables")
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
