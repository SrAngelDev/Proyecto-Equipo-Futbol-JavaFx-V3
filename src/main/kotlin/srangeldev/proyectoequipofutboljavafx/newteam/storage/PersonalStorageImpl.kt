package srangeldev.proyectoequipofutboljavafx.newteam.storage

import org.lighthousegames.logging.logging
import srangeldev.proyectoequipofutboljavafx.newteam.models.Personal
import java.io.File

/**
 * Implementación de la interfaz PersonalStorage que maneja la lectura y escritura de datos personales
 * en diferentes formatos de archivo (JSON, CSV, XML, BIN).
 *
 * @property storageJson Implementación de PersonalStorageFile para archivos JSON.
 * @property storageCsv Implementación de PersonalStorageFile para archivos CSV.
 * @property storageXml Implementación de PersonalStorageFile para archivos XML.
 */
class PersonalStorageImpl(
    private val storageJson: PersonalStorageFile = PersonalStorageJson(),
    private val storageCsv: PersonalStorageFile = PersonalStorageCsv(),
    private val storageXml: PersonalStorageFile = PersonalStorageXml(),
) : PersonalStorage {

    private val logger = logging()


    /**
     * Lee una lista de objetos Personal desde un archivo en el formato especificado.
     *
     * @param file El archivo desde el cual leer los datos.
     * @param fileFormat El formato del archivo (JSON, CSV, XML).
     * @return Una lista de objetos Personal leídos desde el archivo.
     */
    override fun readFromFile(file: File, format: FileFormat): List<Personal> {
        if (!file.exists()) {
            throw IllegalArgumentException("El archivo no existe: ${file.name}")
        }

        // Usar el formato deducido en lugar del formato explícito
        return when(format) {
            FileFormat.JSON -> storageJson.readFromFile(file)
            FileFormat.CSV -> storageCsv.readFromFile(file)
            FileFormat.XML -> storageXml.readFromFile(file)
        }
    }

    /**
     * Escribe una lista de objetos Personal en un archivo en el formato especificado.
     *
     * @param file El archivo en el cual escribir los datos.
     * @param fileFormat El formato del archivo (JSON, CSV, XML, BIN, DEFAULT).
     * @param personalList La lista de objetos Personal a escribir en el archivo.
     */
    override fun writeToFile(file: File, fileFormat: FileFormat, personalList: List<Personal>) {
        logger.debug { "Escribiendo personal en fichero: $file" }
        when (fileFormat) {
            FileFormat.JSON -> storageJson.writeToFile(file, personalList)
            FileFormat.CSV -> storageCsv.writeToFile(file, personalList)
            FileFormat.XML -> storageXml.writeToFile(file, personalList)
        }
    }
}
