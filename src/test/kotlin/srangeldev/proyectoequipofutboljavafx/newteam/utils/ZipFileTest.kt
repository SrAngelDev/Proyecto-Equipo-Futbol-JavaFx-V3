package srangeldev.proyectoequipofutboljavafx.newteam.utils

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class ZipFileTest {

    @Test
    fun `extractFileToPath lanza excepcion si el ZIP no existe`(@TempDir tmp: Path) {
        val ex = assertThrows(IllegalArgumentException::class.java) {
            ZipFile.extractFileToPath(tmp.resolve("no.zip").toString(),
                tmp.resolve("out").toString())
        }
        assertTrue(ex.message!!.contains("no existe"))
    }

    @Test
    fun `createZipFile lanza excepcion si el origen no existe`(@TempDir tmp: Path) {
        val ex = assertThrows(IllegalArgumentException::class.java) {
            ZipFile.createZipFile(tmp.resolve("nada").toString(),
                tmp.resolve("dest.zip").toString())
        }
        assertTrue(ex.message!!.contains("no existe"))
    }

    @Test
    fun `extrae archivo plano creando destino si es necesario`(@TempDir tmp: Path) {
        val zip = tmp.resolve("simple.zip").toFile()
        ZipOutputStream(FileOutputStream(zip)).use { z ->
            z.putNextEntry(ZipEntry("hola.txt"))
            z.write("hola".toByteArray())
            z.closeEntry()
        }

        val out = tmp.resolve("out").toFile()         // NO existe todavía
        ZipFile.extractFileToPath(zip.path, out.path)

        assertEquals("hola", File(out, "hola.txt").readText())
    }

    @Test
    fun `extrae cuando el directorio destino YA existe y contiene dir vacio`(@TempDir tmp: Path) {

        // 1. ZIP con un directorio vacío y un fichero anidado
        val zip = tmp.resolve("mixto.zip").toFile()
        ZipOutputStream(FileOutputStream(zip)).use { z ->
            z.putNextEntry(ZipEntry("yaExiste/"))          // directorio
            z.closeEntry()
            z.putNextEntry(ZipEntry("carp/padre/f.txt"))   // fichero anidado
            z.write("x".toByteArray())
            z.closeEntry()
        }

        // 2. Creamos manualmente el destino y las carpetas que DEBEN existir
        val out = tmp.resolve("out").toFile()
        out.mkdirs()                                                // destDir ya existe
        File(out, "yaExiste").mkdirs()                              // dir yaExiste/ ya existe
        File(out, "carp/padre").mkdirs()                            // parent ya existe

        // 3. Extraemos
        ZipFile.extractFileToPath(zip.path, out.path)

        // 4. Asserts
        assertTrue(File(out, "yaExiste").isDirectory)               // se mantuvo
        assertEquals("x", File(out, "carp/padre/f.txt").readText()) // fichero escrito
    }

    @Test
    fun `crea ZIP que incluye directorios vacios`(@TempDir tmp: Path) {
        // 1. Arbol fuente con dir vacío y archivo normal
        val src       = tmp.resolve("src").toFile()
        val vacioDir  = File(src, "vacio")
        val normalDir = File(src, "normal")
        vacioDir.mkdirs()
        normalDir.mkdirs()
        File(normalDir, "dato.txt").writeText("dato")

        // 2. Comprimir
        val zip = tmp.resolve("todo.zip").toFile()
        ZipFile.createZipFile(src.path, zip.path)

        // 3. Extraer para comprobar que el directorio vacío también va
        val out = tmp.resolve("out").toFile()
        ZipFile.extractFileToPath(zip.path, out.path)

        assertTrue(File(out, "vacio").isDirectory)                  // dir vacío presente
        assertEquals("dato", File(out, "normal/dato.txt").readText())
    }

    @Test
    fun `extrae archivo anidado creando su carpeta padre`(@TempDir tmp: Path) {
        val zip = tmp.resolve("anidado.zip").toFile()
        ZipOutputStream(FileOutputStream(zip)).use { z ->
            z.putNextEntry(ZipEntry("subdir/archivo.txt"))
            z.write("contenido".toByteArray())
            z.closeEntry()
        }

        val out = tmp.resolve("out").toFile()
        // NO creamos "subdir", debe hacerlo el método (parent.mkdirs())

        ZipFile.extractFileToPath(zip.path, out.path)

        val extraido = File(out, "subdir/archivo.txt")
        assertTrue(extraido.exists())
        assertEquals("contenido", extraido.readText())
    }

}
