package srangeldev.proyectoequipofutboljavafx.newteam.cache

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.Cache as CaffeineCache
import java.util.concurrent.ConcurrentHashMap

/**
 * Implementación de una caché con capacidad limitada usando Caffeine.
 *
 * @param capacidad La capacidad máxima de la caché.
 */
class CacheImpl<K : Any, V>(
    private val capacidad: Int = 5
) : Cache<K, V> {
    private val caffeineCache: CaffeineCache<K, Any> = Caffeine.newBuilder()
        .maximumSize(capacidad.toLong())
        .build()

    // Mapa auxiliar para mantener las entradas y poder implementar los métodos de la interfaz
    private val entryMap = ConcurrentHashMap<K, V>()

    @Suppress("UNCHECKED_CAST")
    override fun get(key: K): V? {
        return caffeineCache.getIfPresent(key) as V?
    }

    override fun remove(key: K): V? {
        val oldValue = entryMap.remove(key)
        caffeineCache.invalidate(key)
        return oldValue
    }

    override fun put(key: K, value: V): V? {
        val oldValue = entryMap.put(key, value)
        // Solo almacenamos valores no nulos en Caffeine
        if (value != null) {
            caffeineCache.put(key, value as Any)
        } else {
            caffeineCache.invalidate(key)
        }
        return oldValue
    }

    override fun keys(): Set<K> {
        return entryMap.keys
    }

    override fun values(): Collection<V> {
        return entryMap.values
    }

    override fun clear() {
        entryMap.clear()
        caffeineCache.invalidateAll()
    }

    override fun size(): Int {
        return entryMap.size
    }

    override fun entries(): Set<Map.Entry<K, V>> {
        return entryMap.entries
    }
}
