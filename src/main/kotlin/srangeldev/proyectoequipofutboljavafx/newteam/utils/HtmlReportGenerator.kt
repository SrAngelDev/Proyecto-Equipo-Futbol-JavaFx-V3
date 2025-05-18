package srangeldev.proyectoequipofutboljavafx.newteam.utils

import srangeldev.proyectoequipofutboljavafx.newteam.models.Entrenador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Personal
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Utilidad para generar informes HTML de la plantilla del equipo.
 */
object HtmlReportGenerator {
    
    /**
     * Genera un informe HTML de la plantilla del equipo.
     * 
     * @param personal Lista de personal (jugadores y entrenadores).
     * @param outputPath Ruta donde se guardará el archivo HTML.
     * @return La ruta del archivo HTML generado.
     */
    fun generateReport(personal: List<Personal>, outputPath: String): String {
        val html = buildHtml(personal)
        val file = File(outputPath)
        file.writeText(html)
        return file.absolutePath
    }
    
    /**
     * Construye el contenido HTML del informe.
     * 
     * @param personal Lista de personal (jugadores y entrenadores).
     * @return El contenido HTML del informe.
     */
    private fun buildHtml(personal: List<Personal>): String {
        val jugadores = personal.filterIsInstance<Jugador>()
        val entrenadores = personal.filterIsInstance<Entrenador>()
        
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        
        return """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Plantilla del Club de Fútbol New Team</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        margin: 0;
                        padding: 20px;
                        color: #333;
                    }
                    h1, h2 {
                        color: #4CAF50;
                    }
                    table {
                        width: 100%;
                        border-collapse: collapse;
                        margin-bottom: 30px;
                    }
                    th, td {
                        padding: 12px 15px;
                        border-bottom: 1px solid #ddd;
                        text-align: left;
                    }
                    th {
                        background-color: #4CAF50;
                        color: white;
                    }
                    tr:hover {
                        background-color: #f5f5f5;
                    }
                    .stats {
                        margin-top: 20px;
                        padding: 15px;
                        background-color: #f9f9f9;
                        border-radius: 5px;
                    }
                </style>
            </head>
            <body>
                <h1>Plantilla del Club de Fútbol New Team</h1>
                
                <h2>Jugadores</h2>
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nombre</th>
                            <th>Apellidos</th>
                            <th>Fecha Nacimiento</th>
                            <th>Posición</th>
                            <th>Dorsal</th>
                            <th>Goles</th>
                            <th>Partidos</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${jugadores.joinToString("\n") { jugador ->
                            """
                            <tr>
                                <td>${jugador.id}</td>
                                <td>${jugador.nombre}</td>
                                <td>${jugador.apellidos}</td>
                                <td>${jugador.fechaNacimiento.format(dateFormatter)}</td>
                                <td>${jugador.posicion}</td>
                                <td>${jugador.dorsal}</td>
                                <td>${jugador.goles}</td>
                                <td>${jugador.partidosJugados}</td>
                            </tr>
                            """.trimIndent()
                        }}
                    </tbody>
                </table>
                
                <div class="stats">
                    <h3>Estadísticas de Jugadores</h3>
                    <p>Total de jugadores: ${jugadores.size}</p>
                    <p>Promedio de goles: ${if (jugadores.isNotEmpty()) String.format("%.2f", jugadores.map { it.goles }.average()) else "0"}</p>
                </div>
                
                <h2>Entrenadores</h2>
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nombre</th>
                            <th>Apellidos</th>
                            <th>Fecha Nacimiento</th>
                            <th>Especialización</th>
                            <th>País de Origen</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${entrenadores.joinToString("\n") { entrenador ->
                            """
                            <tr>
                                <td>${entrenador.id}</td>
                                <td>${entrenador.nombre}</td>
                                <td>${entrenador.apellidos}</td>
                                <td>${entrenador.fechaNacimiento.format(dateFormatter)}</td>
                                <td>${entrenador.especializacion}</td>
                                <td>${entrenador.paisOrigen}</td>
                            </tr>
                            """.trimIndent()
                        }}
                    </tbody>
                </table>
                
                <div class="stats">
                    <h3>Estadísticas Generales</h3>
                    <p>Total de personal: ${personal.size}</p>
                    <p>Jugadores: ${jugadores.size}</p>
                    <p>Entrenadores: ${entrenadores.size}</p>
                </div>
                
                <footer>
                    <p>Informe generado el ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))}</p>
                </footer>
            </body>
            </html>
        """.trimIndent()
    }
}