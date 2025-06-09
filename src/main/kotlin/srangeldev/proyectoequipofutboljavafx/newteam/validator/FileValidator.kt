package srangeldev.proyectoequipofutboljavafx.newteam.validator

import srangeldev.proyectoequipofutboljavafx.newteam.exceptions.PersonalException
import srangeldev.proyectoequipofutboljavafx.newteam.validator.Validator
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import java.io.BufferedReader
import java.io.FileReader

class FileValidator : Validator<File> {

    override fun validate(item: File) {
        // Primero validamos las propiedades básicas
        validateBasicFileProperties(item)

        // Luego validamos según el tipo de archivo
        when {
            item.name.endsWith(".json", ignoreCase = true) -> validateJson(item)
            item.name.endsWith(".xml", ignoreCase = true) -> validateXml(item)
            item.name.endsWith(".csv", ignoreCase = true) -> validateCsv(item)
            else -> throw PersonalException.PersonalStorageException(
                "Error en el almacenamiento: El fichero no tiene extensión XML: $item"
            )
        }
    }


    private fun validateBasicFileProperties(file: File) {
        if (!file.exists()) {
            throw PersonalException.PersonalStorageException("El fichero no existe: $file")
        }
        if (!file.isFile) {
            throw PersonalException.PersonalStorageException("No es un fichero: $file")
        }
        if (!file.canRead()) {
            throw PersonalException.PersonalStorageException("No se puede leer el fichero: $file")
        }
        if (file.length() == 0L) {
            throw PersonalException.PersonalStorageException("El fichero está vacío: $file")
        }
    }

    private fun validateJson(file: File) {
        try {
            val firstChars = file.readText(Charsets.UTF_8).take(10).trim()
            if (!firstChars.startsWith("[") && !firstChars.startsWith("{")) {
                throw PersonalException.PersonalStorageException("El contenido del archivo no es JSON válido")
            }
        } catch (e: Exception) {
            throw PersonalException.PersonalStorageException("Error al validar JSON: ${e.message}")
        }
    }

    private fun validateXml(file: File) {
        try {
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            builder.parse(file)
        } catch (e: Exception) {
            throw PersonalException.PersonalStorageException("Error al validar XML: ${e.message}")
        }
    }

    private fun validateCsv(file: File) {
        try {
            BufferedReader(FileReader(file)).use { reader ->
                val firstLine = reader.readLine()
                if (firstLine == null || firstLine.trim().isEmpty()) {
                    throw PersonalException.PersonalStorageException("El archivo CSV está vacío o mal formado")
                }
                // Validar que todas las líneas tengan el mismo número de campos
                val expectedColumns = firstLine.split(",").size
                var lineNumber = 1
                reader.lineSequence().forEach { line ->
                    if (line.split(",").size != expectedColumns) {
                        throw PersonalException.PersonalStorageException(
                            "Error en línea $lineNumber: número incorrecto de columnas"
                        )
                    }
                    lineNumber++
                }
            }
        } catch (e: Exception) {
            throw PersonalException.PersonalStorageException("Error al validar CSV: ${e.message}")
        }
    }
}
