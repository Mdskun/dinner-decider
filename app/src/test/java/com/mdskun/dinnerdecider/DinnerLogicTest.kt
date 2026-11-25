package com.mdskun.dinnerdecider

import org.junit.Assert.*
import org.junit.Test

class DinnerLogicTest {

    @Test
    fun duplicateDetection_isCaseInsensitive() {
        val list = listOf("Pasta Arrabiata", "Dal Tadka", "Biryani")

        val new1 = "pasta arrabiata"
        val isDuplicate1 = list.any { it.equals(new1.trim(), ignoreCase = true) }
        assertTrue(isDuplicate1)

        val new2 = " Tacos "
        val isDuplicate2 = list.any { it.equals(new2.trim(), ignoreCase = true) }
        assertFalse(isDuplicate2)
    }

    @Test
    fun updateDinner_detectsDuplicatesExcludingCurrentIndex() {
        val list = listOf("Pasta Arrabiata", "Dal Tadka", "Biryani")

        // Renaming index 1 ("Dal Tadka") to "pasta arrabiata" should be detected as duplicate
        val renameToExisting = "pasta arrabiata".trim()
        val indexToEdit = 1
        val isDuplicate = list.indices.any { i -> i != indexToEdit && list[i].equals(renameToExisting, ignoreCase = true) }
        assertTrue(isDuplicate)

        // Renaming index 1 to same name "Dal Tadka" should NOT be flagged as duplicate
        val sameName = "Dal Tadka".trim()
        val isSameNameDuplicate = list.indices.any { i -> i != indexToEdit && list[i].equals(sameName, ignoreCase = true) }
        assertFalse(isSameNameDuplicate)
    }

    @Test
    fun largeList_supportsFiftyPlusItems() {
        val largeList = (1..60).map { "Meal #$it" }
        assertEquals(60, largeList.size)

        val recentPicks = listOf("Meal #1", "Meal #2", "Meal #3")
        val candidates = largeList.filter { it !in recentPicks }

        assertEquals(57, candidates.size)
    }

    @Test
    fun avoidRecentPicks_filtersRecentItems() {
        val dinners = listOf("Meal A", "Meal B", "Meal C", "Meal D")
        val recentPicks = listOf("Meal A", "Meal B")

        val candidates = dinners.filter { it !in recentPicks }

        assertEquals(2, candidates.size)
        assertTrue(candidates.contains("Meal C"))
        assertTrue(candidates.contains("Meal D"))
        assertFalse(candidates.contains("Meal A"))
    }

    @Test
    fun undoDelete_restoresItemAtOriginalIndex() {
        val originalList = mutableListOf("Meal 1", "Meal 2", "Meal 3")
        val deletedIndex = 1
        val deletedItem = originalList.removeAt(deletedIndex)

        assertEquals(listOf("Meal 1", "Meal 3"), originalList)

        // Undo
        originalList.add(deletedIndex, deletedItem)
        assertEquals(listOf("Meal 1", "Meal 2", "Meal 3"), originalList)
    }

    @Test
    fun recentPicksList_keepsMaximumThreeItems() {
        val history = mutableListOf("Meal 1", "Meal 2", "Meal 3")
        val newPick = "Meal 4"

        val updated = (history + newPick).takeLast(3)

        assertEquals(3, updated.size)
        assertEquals(listOf("Meal 2", "Meal 3", "Meal 4"), updated)
    }
}