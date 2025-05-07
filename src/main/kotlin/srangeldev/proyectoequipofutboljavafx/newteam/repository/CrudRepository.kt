package srangeldev.repository

/**
 * Interfaz que define las operaciones básicas de un repositorio.
 */
interface CrudRepository<ID, T> {
    fun getAll(): List<T>
    fun getById(id: ID): T?
    fun save(entidad: T): T
    fun update(id: ID, entidad: T): T?
    fun delete(id: ID): T?
}