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
 * @property storageBin Implementación de PersonalStorageFile para archivos BIN.
 */
class PersonalStorageImpl(
    private val storageJson: PersonalStorageFile = PersonalStorageJson(),
    private val storageCsv: PersonalStorageFile = PersonalStorageCsv(),
    private val storageXml: PersonalStorageFile = PersonalStorageXml(),
): PersonalStorage {

    private val logger = logging()

    /**
     * Lee una lista de objetos Personal desde un archivo en el formato especificado.
     *
     * @param file El archivo desde el cual leer los datos.
     * @param fileFormat El formato del archivo (JSON, CSV, XML, BIN, DEFAULT).
     * @return Una lista de objetos Personal leídos desde el archivo.
     */
    override fun readFromFile(file: File, fileFormat: FileFormat): List<Personal> {
        logger.debug { "Leyendo personal de fichero: $file" }

        // Determine the format from the file extension if DEFAULT
        val effectiveFormat = if (fileFormat == FileFormat.DEFAULT) {
            when (file.extension.lowercase()) {
                "json" -> FileFormat.JSON
                "csv" -> FileFormat.CSV
                "xml" -> FileFormat.XML
                else -> FileFormat.JSON
            }
        } else {
            fileFormat
        }

        return when (effectiveFormat) {
            FileFormat.JSON -> storageJson.readFromFile(file)
            FileFormat.CSV -> storageCsv.readFromFile(file)
            FileFormat.XML -> storageXml.readFromFile(file)
            FileFormat.DEFAULT -> storageJson.readFromFile(file)
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
            FileFormat.DEFAULT -> storageJson.writeToFile(file, personalList) // Por defecto se asume JSON
        }
    }
}
