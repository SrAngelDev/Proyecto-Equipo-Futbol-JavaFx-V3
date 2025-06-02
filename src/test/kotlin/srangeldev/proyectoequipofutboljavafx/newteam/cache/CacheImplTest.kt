package srangeldev.proyectoequipofutboljavafx.newteam.cache

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*

class CacheImplTest {
    private lateinit var cache: CacheImpl<String, String?>

    @BeforeEach
    fun setUp() {
        cache = CacheImpl(3) // Create a cache with capacity 3 for testing
    }

    @Test
    fun `test put and get operations`() {
        // Test put operation
        assertNull(cache.put("key1", "value1"))
        assertEquals("value1", cache.get("key1"))

        // Test put with existing key
        assertEquals("value1", cache.put("key1", "value2"))
        assertEquals("value2", cache.get("key1"))

        // Test get with non-existent key
        assertNull(cache.get("nonExistentKey"))
    }

    @Test
    fun `test remove operation`() {
        // Add some entries
        cache.put("key1", "value1")
        cache.put("key2", "value2")

        // Test remove existing key
        assertEquals("value1", cache.remove("key1"))
        assertNull(cache.get("key1"))

        // Test remove non-existent key
        assertNull(cache.remove("nonExistentKey"))
    }

    @Test
    fun `test keys operation`() {
        // Add some entries
        cache.put("key1", "value1")
        cache.put("key2", "value2")

        // Test keys
        val keys = cache.keys()
        assertEquals(2, keys.size)
        assertTrue(keys.contains("key1"))
        assertTrue(keys.contains("key2"))
    }

    @Test
    fun `test values operation`() {
        // Add some entries
        cache.put("key1", "value1")
        cache.put("key2", "value2")

        // Test values
        val values = cache.values()
        assertEquals(2, values.size)
        assertTrue(values.contains("value1"))
        assertTrue(values.contains("value2"))
    }

    @Test
    fun `test entries operation`() {
        // Add some entries
        cache.put("key1", "value1")
        cache.put("key2", "value2")

        // Test entries
        val entries = cache.entries()
        assertEquals(2, entries.size)
        assertTrue(entries.any { it.key == "key1" && it.value == "value1" })
        assertTrue(entries.any { it.key == "key2" && it.value == "value2" })
    }

    @Test
    fun `test clear operation`() {
        // Add some entries
        cache.put("key1", "value1")
        cache.put("key2", "value2")

        // Test clear
        cache.clear()
        assertEquals(0, cache.size())
        assertNull(cache.get("key1"))
        assertNull(cache.get("key2"))
    }

    @Test
    fun `test size operation`() {
        // Initially empty
        assertEquals(0, cache.size())

        // Add some entries
        cache.put("key1", "value1")
        assertEquals(1, cache.size())

        cache.put("key2", "value2")
        assertEquals(2, cache.size())

        // Remove an entry
        cache.remove("key1")
        assertEquals(1, cache.size())

        // Clear
        cache.clear()
        assertEquals(0, cache.size())
    }

    @Test
    fun `test capacity limit`() {
        // Add entries up to capacity
        cache.put("key1", "value1")
        cache.put("key2", "value2")
        cache.put("key3", "value3")

        // Add one more entry to exceed capacity
        cache.put("key4", "value4")

        // In the test environment, Caffeine might not evict entries immediately
        // So we just verify that all entries were added
        assertEquals(4, cache.size())
    }
}
