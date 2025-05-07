package srangeldev.Cache

/**
 * Implementación de una caché con capacidad limitada.
 *
 * @param capacidad La capacidad máxima de la caché.
 */
class CacheImpl<K, V>(
    private val capacidad: Int = 5
) : Cache<K, V> {
    private val cache = object : LinkedHashMap<K, V>(
        capacidad, 0.75f, true
    ) {
        override fun removeEldestEntry(antiguo: MutableMap.MutableEntry<K, V>?): Boolean {
            return size > capacidad
        }
    }

    override fun get(key: K): V? {
        return cache[key]
    }

    override fun remove(key: K): V? {
        return cache.remove(key)
    }

    override fun put(key: K, value: V): V? {
        return cache.put(key, value)
    }

    override fun keys(): Set<K> {
        return cache.keys
    }

    override fun values(): Collection<V> {
        return cache.values
    }

    override fun clear() {
        cache.clear()
    }

    override fun size(): Int {
        return cache.size
    }

    override fun entries(): Set<Map.Entry<K, V>> {
        return cache.entries
    }
}