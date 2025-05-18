package srangeldev.proyectoequipofutboljavafx.newteam.storage

import srangeldev.proyectoequipofutboljavafx.newteam.models.Personal
import java.io.File

/**
 * Interfaz para el almacenamiento de datos de personal en archivos.
 */
interface PersonalStorageFile {
    /**
     * Lee una lista de objetos Personal desde un archivo.
     *
     * @param file El archivo desde el cual leer los datos.
     * @return Una lista de objetos Personal leídos desde el archivo.
     */
    fun readFromFile(file: File): List<Personal>

    /**
     * Escribe una lista de objetos Personal en un archivo.
     *
     * @param file El archivo en el cual escribir los datos.
     * @param personalList La lista de objetos Personal a escribir en el archivo.
     */
    fun writeToFile(file: File, personalList: List<Personal>)
}
