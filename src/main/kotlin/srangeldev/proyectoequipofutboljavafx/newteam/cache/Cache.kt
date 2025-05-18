package srangeldev.proyectoequipofutboljavafx.newteam.cache

/**
 * Interfaz que define los métodos para una caché genérica.
 *
 * @param K El tipo de las claves utilizadas en la caché.
 * @param V El tipo de los valores almacenados en la caché.
 */
interface Cache<K, V> {
    /**
     * Obtiene el valor asociado a la clave especificada.
     *
     * @param key La clave cuyo valor se desea obtener.
     * @return El valor asociado a la clave, o null si no existe.
     */
    fun get(key: K): V?

    /**
     * Almacena un valor en la caché asociado a la clave especificada.
     *
     * @param key La clave con la que se asociará el valor.
     * @param value El valor a almacenar en la caché.
     * @return El valor anterior asociado a la clave, o null si no existía.
     */
    fun put(key: K, value: V): V?

    /**
     * Obtiene todas las claves almacenadas en la caché.
     *
     * @return Un conjunto de todas las claves en la caché.
     */
    fun keys(): Set<K>

    /**
     * Obtiene todos los valores almacenados en la caché.
     *
     * @return Una colección de todos los valores en la caché.
     */
    fun values(): Collection<V>

    /**
     * Obtiene todas las entradas (pares clave-valor) almacenadas en la caché.
     *
     * @return Un conjunto de todas las entradas en la caché.
     */
    fun entries(): Set<Map.Entry<K, V>>

    /**
     * Elimina el valor asociado a la clave especificada.
     *
     * @param key La clave cuyo valor se desea eliminar.
     * @return El valor eliminado, o null si no existía.
     */
    fun remove(key: K): V?

    /**
     * Elimina todas las entradas de la caché.
     */
    fun clear()

    /**
     * Obtiene el número de entradas almacenadas en la caché.
     *
     * @return El número de entradas en la caché.
     */
    fun size(): Int
}