package srangeldev.view

import org.lighthousegames.logging.logging
import srangeldev.config.Config
import srangeldev.controller.Controller

/**
 * Clase que representa la vista de la aplicación.
 */
class View {
    private val logger = logging()
    private val controller = Controller()

    /**
     * Muestra el menú de la aplicación.
     */
    fun showMenu() {
        println("Bienvenido a la aplicación de Entrenadores")
        var opcion: Int
        do {
            println("1. Cargar datos de los miembros del equipo.")
            println("2. Crear miembro del equipo.")
            println("3. Actualizar miembro de equipo.")
            println("4. Eliminar miembro del equipo")
            println("5. Copiar datos a fichero según la especificación realizada.")
            println("6. Salir.")

            print("Seleccione una opción: ")
            opcion = readln().toInt()

            when(opcion) {
                1 -> cargarDatos()
                2 -> crearMiembro()
                3 -> actualizarMiembro()
                4 -> eliminarMiembro()
                5 -> copiarDatos()
                6 -> println("Saliendo de la aplicación...")
                else -> println("Opción no válida.")
            }
        } while (opcion != 6)
    }

    /**
     * Manda al controlador cargar los datos.
     */
    private fun cargarDatos() {
        logger.debug { "Cargando datos" }
        println("Cargando datos de los miembros del equipo...")
        cargarDatosCsv()
        cargarDatosXml()
        cargarDatosJson()
        println("Datos cargados correctamente.")
    }

    private fun cargarDatosCsv() {
        logger.debug { "Cargando datos CSV" }
        controller.cargarDatos("CSV")
    }

    private fun cargarDatosXml() {
        logger.debug { "Cargando datos XML" }
        controller.cargarDatos("XML")
    }

    private fun cargarDatosJson() {
        logger.debug { "Cargando datos JSON" }
        controller.cargarDatos("JSON")
    }

    /**
     * Manda al controlador copiar los datos.
     */
    private fun copiarDatos() {
        logger.debug { "Copiando datos" }
        println("¿A que quieres exportar los datos?")
        println("1. CSV")
        println("2. XML")
        println("3. JSON")
        print("Seleccione una opción: ")
        val opcion = readln().toInt()
        when (opcion) {
            1 -> copiarDatosCSV()
            2 -> copiarDatosXML()
            3 -> copiarDatosJSON()
            else -> println("Opción no válida.")
        }
        println("Datos copiados correctamente.")
    }

    private fun copiarDatosCSV() {
        logger.debug { "Copiando datos en formato CSV" }
        controller.copiarDatos("csv")
    }

    private fun copiarDatosXML() {
        logger.debug { "Copiando datos en formato XML" }
        controller.copiarDatos("xml")
    }

    private fun copiarDatosJSON() {
        logger.debug { "Copiando datos en formato JSON" }
        controller.copiarDatos("json")
    }

    /**
     * Manda al controlador crear un miembro.
     */
    private fun crearMiembro() {
        logger.debug { "Creando miembro" }
        controller.crearMiembro()
    }

    /**
     * Manda al controlador actualizar un miembro.
     */
    private fun actualizarMiembro() {
        logger.debug { "Actualizando miembro" }
        controller.actualizarMiembro()
    }

    /**
     * Manda al controlador eliminar un miembro.
     */
    private fun eliminarMiembro() {
        logger.debug { "Eliminando miembro" }
        controller.eliminarMiembro()
    }
}