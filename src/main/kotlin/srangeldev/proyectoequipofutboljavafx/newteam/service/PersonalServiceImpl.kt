package srangeldev.proyectoequipofutboljavafx.newteam.service

import org.lighthousegames.logging.logging
import srangeldev.proyectoequipofutboljavafx.newteam.cache.Cache
import srangeldev.proyectoequipofutboljavafx.newteam.cache.CacheImpl
import srangeldev.proyectoequipofutboljavafx.newteam.exceptions.PersonalException
import srangeldev.proyectoequipofutboljavafx.newteam.models.Personal
import srangeldev.proyectoequipofutboljavafx.newteam.repository.PersonalRepository
import srangeldev.proyectoequipofutboljavafx.newteam.storage.FileFormat
import srangeldev.proyectoequipofutboljavafx.newteam.storage.PersonalStorage
import srangeldev.proyectoequipofutboljavafx.newteam.storage.PersonalStorageImpl
import srangeldev.proyectoequipofutboljavafx.newteam.validator.ValidatorFactory
import java.io.File

private const val CACHE_SIZE = 5

/**
 * Clase que implementa el servicio de gestión de personal.
 */
class PersonalServiceImpl(
    private val storage: PersonalStorage = PersonalStorageImpl(),
    private val repository: PersonalRepository,
    private val cache: Cache<Int, Personal> = CacheImpl(CACHE_SIZE)
): PersonalService {
    private val logger = logging()

    init {
        logger.debug { "Inicializando servicio de personal." }
    }

    private fun readFromFile(filePath: String, fileFormat: FileFormat): List<Personal> {
        logger.debug { "Leyendo personal de fichero: $filePath" }
        val rawPersonalList = storage.readFromFile(File(filePath), fileFormat)
        val validPersonal = mutableListOf<Personal>()

        rawPersonalList.forEach { personal ->
            try {
                // Validar cada objeto antes de añadirlo a la lista
                ValidatorFactory.validate(personal)
                validPersonal.add(personal)
                logger.debug { "Personal válido leído: $personal" }
            } catch (e: Exception) {
                // Registrar error para objetos inválidos pero continuar con el resto
                logger.error { "Error al validar personal: $personal. Error: ${e.message}" }
            }
        }

        logger.info { "Personal leído: ${validPersonal.size} válidos de ${rawPersonalList.size} totales" }
        return validPersonal
    }

    private fun writeToFile(filePath: String, fileFormat: FileFormat, personalList: List<Personal>) {
        logger.debug { "Escribiendo personal en fichero: $filePath" }
        storage.writeToFile(File(filePath), fileFormat, personalList)
    }

    override fun importFromFile(filePath: String, format: FileFormat) {
        logger.info { "Importando personal de fichero: $filePath" }
        val personalList = readFromFile(filePath, format)
        val validPersonal = mutableListOf<Personal>()

        personalList.forEach { personal ->
            try {
                // Validar cada objeto antes de guardarlo
                ValidatorFactory.validate(personal)
                validPersonal.add(personal)
                repository.save(personal)
                logger.debug { "Personal válido guardado: $personal" }
            } catch (e: Exception) {
                // Registrar error para objetos inválidos pero continuar con el resto
                logger.error { "Error al validar personal: $personal. Error: ${e.message}" }
            }
        }

        logger.info { "Personal importado: ${validPersonal.size} válidos de ${personalList.size} totales" }
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
        ValidatorFactory.validate(personal)
        return repository.save(personal)
    }

    override fun update(id: Int, personal: Personal): Personal? {
        logger.info { "Actualizando personal con id: $id" }
        ValidatorFactory.validate(personal)
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
