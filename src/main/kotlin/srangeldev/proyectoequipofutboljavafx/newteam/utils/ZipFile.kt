package srangeldev.proyectoequipofutboljavafx.newteam.utils

import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

/**
 * Utilidad para trabajar con archivos ZIP.
 * Proporciona funciones para extraer y crear archivos ZIP.
 */
object ZipFile {
    /**
     * Extrae el contenido de un archivo ZIP a una ruta de destino.
     *
     * @param zipFilePath Ruta del archivo ZIP a extraer.
     * @param destinationPath Ruta de destino donde se extraerán los archivos.
     */
    fun extractFileToPath(zipFilePath: String, destinationPath: String) {
        val buffer = ByteArray(1024)
        val zipFile = File(zipFilePath)
        val destDir = File(destinationPath)

        // Ensure destination directory exists
        if (!destDir.exists()) {
            destDir.mkdirs()
        }

        // Check if zip file exists
        if (!zipFile.exists()) {
            throw IllegalArgumentException("El archivo ZIP no existe: $zipFilePath")
        }

        ZipFile(zipFile).use { zip ->
            zip.entries().asSequence().forEach { entry ->
                val entryName = entry.name.replace("/", File.separator)
                val newFile = File(destDir, entryName)

                if (entry.isDirectory) {
                    // Create directory if it doesn't exist
                    if (!newFile.exists()) {
                        newFile.mkdirs()
                    }
                } else {
                    // Ensure parent directories exist
                    val parent = newFile.parentFile
                    if (parent != null && !parent.exists()) {
                        parent.mkdirs()
                    }

                    // Extract file
                    zip.getInputStream(entry).use { input ->
                        FileOutputStream(newFile).use { output ->
                            var len: Int
                            while (input.read(buffer).also { len = it } > 0) {
                                output.write(buffer, 0, len)
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Crea un archivo ZIP a partir del contenido de un directorio.
     *
     * @param sourceDirPath Ruta del directorio fuente que contiene los archivos a comprimir.
     * @param zipFilePath Ruta donde se creará el archivo ZIP.
     * @throws IllegalArgumentException Si el directorio de origen no existe.
     */
    fun createZipFile(sourceDirPath: String, zipFilePath: String) {
        val sourceDir = File(sourceDirPath)
        val zipFile = File(zipFilePath)

        if (!sourceDir.exists()) {
            throw IllegalArgumentException("El directorio de origen no existe: $sourceDirPath")
        }

        ZipOutputStream(FileOutputStream(zipFile)).use { zipOut ->
            sourceDir.walkTopDown().forEach { file ->
                // Skip the source directory itself
                if (file == sourceDir) return@forEach

                // Create relative path for the entry
                val entryName = file.relativeTo(sourceDir).path.replace("\\", "/")

                if (file.isDirectory) {
                    // Add trailing slash for directories
                    val dirEntry = if (entryName.endsWith("/")) entryName else "$entryName/"
                    val entry = ZipEntry(dirEntry)
                    zipOut.putNextEntry(entry)
                    zipOut.closeEntry()
                } else {
                    // Regular file entry
                    val entry = ZipEntry(entryName)
                    zipOut.putNextEntry(entry)
                    file.inputStream().use { input ->
                        input.copyTo(zipOut)
                    }
                    zipOut.closeEntry()
                }
            }
        }
    }
}
