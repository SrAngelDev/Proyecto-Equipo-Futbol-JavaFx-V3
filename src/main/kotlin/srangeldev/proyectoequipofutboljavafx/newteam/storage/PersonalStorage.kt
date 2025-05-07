package srangeldev.storage

import srangeldev.models.Personal
import java.io.File

/**
 * Interfaz para el almacenamiento de datos de personal en diferentes formatos de archivo.
 */
interface PersonalStorage {
    /**
     * Lee una lista de objetos Personal desde un archivo en el formato especificado.
     *
     * @param file El archivo desde el cual leer los datos.
     * @param fileFormat El formato del archivo (CSV, Bin, etc.).
     * @return Una lista de objetos Personal leídos desde el archivo.
     */
    fun readFromFile(file: File, fileFormat: FileFormat): List<Personal>

    /**
     * Escribe una lista de objetos Personal en un archivo en el formato especificado.
     *
     * @param file El archivo en el cual escribir los datos.
     * @param fileFormat El formato del archivo (CSV, Bin, etc.).
     * @param personalList La lista de objetos Personal a escribir en el archivo.
     */
    fun writeToFile(file: File, fileFormat: FileFormat, personalList: List<Personal>)
}