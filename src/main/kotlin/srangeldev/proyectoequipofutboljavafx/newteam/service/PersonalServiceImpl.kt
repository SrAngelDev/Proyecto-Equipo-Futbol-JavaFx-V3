package srangeldev.proyectoequipofutboljavafx.newteam.service

import org.lighthousegames.logging.logging
import srangeldev.proyectoequipofutboljavafx.newteam.cache.Cache
import srangeldev.proyectoequipofutboljavafx.newteam.cache.CacheImpl
import srangeldev.proyectoequipofutboljavafx.newteam.exceptions.PersonalException
import srangeldev.proyectoequipofutboljavafx.newteam.models.Personal
import srangeldev.proyectoequipofutboljavafx.newteam.repository.PersonalRepository
import srangeldev.proyectoequipofutboljavafx.newteam.repository.PersonalRespositoryImpl
import srangeldev.proyectoequipofutboljavafx.newteam.storage.FileFormat
import srangeldev.proyectoequipofutboljavafx.newteam.storage.PersonalStorage
import srangeldev.proyectoequipofutboljavafx.newteam.storage.PersonalStorageImpl
import srangeldev.validator.validate
import java.io.File

private const val CACHE_SIZE = 5

/**
 * Clase que implementa el servicio de gestión de personal.
 */
class PersonalServiceImpl(
    private val storage: PersonalStorage = PersonalStorageImpl(),
    private val repository: PersonalRepository = PersonalRespositoryImpl(),
    private val cache: Cache<Int, Personal> = CacheImpl(CACHE_SIZE)
): PersonalService {
    private val logger = logging()

    init {
        logger.debug { "Inicializando servicio de personal." }
    }

    private fun readFromFile(filePath: String, fileFormat: FileFormat): List<Personal> {
        logger.debug { "Leyendo personal de fichero: $filePath" }
        return storage.readFromFile(File(filePath), fileFormat)
    }

    private fun writeToFile(filePath: String, fileFormat: FileFormat, personalList: List<Personal>) {
        logger.debug { "Escribiendo personal en fichero: $filePath" }
        storage.writeToFile(File(filePath), fileFormat, personalList)
    }

    override fun importFromFile(filePath: String, format: FileFormat) {
        logger.info { "Importando personal de fichero: $filePath" }
        val personalList = readFromFile(filePath, format)
        personalList.forEach { repository.save(it) }
        logger.debug { "Personal guardado en repository: ${repository.getAll()}" }
    }

    override fun exportToFile(filePath: String, format: FileFormat) {
        logger.info { "Exportando personal a fichero: $filePath" }
        val personalList = repository.getAll()
        writeToFile(filePath, format, personalList)
    }

    override fun getAll(): List<Personal> {
        logger.info { "Obteniendo todo el personal" }
        return repository.getAll()
    }

    override fun getById(id: Int): Personal? {
        logger.info { "Obteniendo personal con id: $id" }
        return cache.get(id) ?: repository.getById(id)?.also {
            cache.put(id, it)
        } ?: throw PersonalException.PersonalNotFoundException(id)
    }

    override fun save(personal: Personal): Personal {
        logger.info { "Guardando personal: $personal" }
        personal.validate()
        return repository.save(personal)
    }

    override fun update(id: Int, personal: Personal): Personal? {
        logger.info { "Actualizando personal con id: $id" }
        personal.validate()
        return repository.update(id, personal)?.also {
            cache.remove(id)
        } ?: throw PersonalException.PersonalNotFoundException(id)
    }

    override fun delete(id: Int): Personal {
        logger.info { "Borrando personal con id: $id" }
        return repository.delete(id)?.also {
            cache.remove(id)
        } ?: throw PersonalException.PersonalNotFoundException(id)
    }
}