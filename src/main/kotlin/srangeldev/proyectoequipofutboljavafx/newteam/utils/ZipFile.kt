package srangeldev.proyectoequipofutboljavafx.newteam.utils

import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

object ZipFile {
    fun extractFileToPath(zipFilePath: String, destinationPath: String) {
        val buffer = ByteArray(1024)
        val zipFile = ZipFile(zipFilePath)
        val destDir = File(destinationPath)
        if (!destDir.exists()) destDir.mkdirs()

        zipFile.use { zip ->
            zip.entries().asSequence().forEach { entry ->
                val newFile = java.io.File(destDir, entry.name)
                if (entry.isDirectory) {
                    newFile.mkdirs()
                } else {
                    newFile.parentFile?.mkdirs()
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

    fun createZipFile(sourceDirPath: String, zipFilePath: String) {
        val sourceDir = File(sourceDirPath)
        val zipFile = File(zipFilePath)

        if (!sourceDir.exists()) {
            throw IllegalArgumentException("Source directory does not exist: $sourceDirPath")
        }

        java.util.zip.ZipOutputStream(FileOutputStream(zipFile)).use { zipOut ->
            sourceDir.walkTopDown().forEach { file ->
                val entryName = file.relativeTo(sourceDir).path
                zipOut.putNextEntry(ZipEntry(entryName))
                if (file.isFile) {
                    file.inputStream().use { input ->
                        input.copyTo(zipOut)
                    }
                }
                zipOut.closeEntry()
            }
        }
    }
}