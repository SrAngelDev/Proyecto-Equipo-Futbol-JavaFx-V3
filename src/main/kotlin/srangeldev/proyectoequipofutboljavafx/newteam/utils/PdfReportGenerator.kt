package srangeldev.proyectoequipofutboljavafx.newteam.utils

import com.itextpdf.text.*
import com.itextpdf.text.pdf.*
import srangeldev.proyectoequipofutboljavafx.newteam.models.Convocatoria
import srangeldev.proyectoequipofutboljavafx.newteam.models.Entrenador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Personal
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Utilidad para generar informes PDF de la plantilla del equipo.
 */
object PdfReportGenerator {

    private val GREEN_COLOR = BaseColor(76, 175, 80) // #4CAF50
    private val WHITE_COLOR = BaseColor.WHITE
    private val LIGHT_GRAY_COLOR = BaseColor(240, 240, 240)

    /**
     * Genera un informe PDF de la plantilla del equipo.
     * 
     * @param personal Lista de personal (jugadores y entrenadores).
     * @param outputPath Ruta donde se guardará el archivo PDF.
     * @return La ruta del archivo PDF generado.
     */
    fun generateReport(personal: List<Personal>, outputPath: String): String {
        val file = File(outputPath)
        val document = Document(PageSize.A4)
        val writer = PdfWriter.getInstance(document, FileOutputStream(file))

        document.open()

        try {
            // Filtrar jugadores y entrenadores
            val jugadores = personal.filterIsInstance<Jugador>()
            val entrenadores = personal.filterIsInstance<Entrenador>()

            // Título principal
            val titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20f, GREEN_COLOR)
            val title = Paragraph("Plantilla del Club de Fútbol New Team", titleFont)
            title.alignment = Element.ALIGN_CENTER
            title.spacingAfter = 20f
            document.add(title)

            // Sección de jugadores
            val sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16f, GREEN_COLOR)
            val jugadoresTitle = Paragraph("Jugadores", sectionFont)
            jugadoresTitle.spacingBefore = 10f
            jugadoresTitle.spacingAfter = 10f
            document.add(jugadoresTitle)

            // Tabla de jugadores
            val jugadoresTable = PdfPTable(8)
            jugadoresTable.widthPercentage = 100f

            // Establecer anchos relativos de las columnas
            val widths = floatArrayOf(0.5f, 1.5f, 1.5f, 1.2f, 1.0f, 0.7f, 0.7f, 0.7f)
            jugadoresTable.setWidths(widths)

            // Cabecera de la tabla
            val headers = arrayOf("ID", "Nombre", "Apellidos", "Fecha Nacimiento", "Posición", "Dorsal", "Goles", "Partidos")
            val headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12f, WHITE_COLOR)

            headers.forEach { header ->
                val cell = PdfPCell(Phrase(header, headerFont))
                cell.backgroundColor = GREEN_COLOR
                cell.horizontalAlignment = Element.ALIGN_CENTER
                cell.verticalAlignment = Element.ALIGN_MIDDLE
                cell.setPadding(5f)
                jugadoresTable.addCell(cell)
            }

            // Datos de jugadores
            val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val cellFont = FontFactory.getFont(FontFactory.HELVETICA, 11f)

            jugadores.forEach { jugador ->
                jugadoresTable.addCell(PdfPCell(Phrase(jugador.id.toString(), cellFont)))
                jugadoresTable.addCell(PdfPCell(Phrase(jugador.nombre, cellFont)))
                jugadoresTable.addCell(PdfPCell(Phrase(jugador.apellidos, cellFont)))
                jugadoresTable.addCell(PdfPCell(Phrase(jugador.fechaNacimiento.format(dateFormatter), cellFont)))
                jugadoresTable.addCell(PdfPCell(Phrase(jugador.posicion.toString(), cellFont)))
                jugadoresTable.addCell(PdfPCell(Phrase(jugador.dorsal.toString(), cellFont)))
                jugadoresTable.addCell(PdfPCell(Phrase(jugador.goles.toString(), cellFont)))
                jugadoresTable.addCell(PdfPCell(Phrase("N/A", cellFont)))
            }

            document.add(jugadoresTable)

            // Estadísticas de jugadores
            val statsFont = FontFactory.getFont(FontFactory.HELVETICA, 12f)
            val statsTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14f, GREEN_COLOR)

            val jugadoresStats = Paragraph()
            jugadoresStats.add(Paragraph("Estadísticas de Jugadores", statsTitleFont))
            jugadoresStats.add(Paragraph("Total de jugadores: ${jugadores.size}", statsFont))
            jugadoresStats.add(Paragraph("Promedio de goles: ${if (jugadores.isNotEmpty()) String.format("%.2f", jugadores.map { it.goles }.average()) else "0"}", statsFont))

            jugadoresStats.spacingBefore = 10f
            jugadoresStats.spacingAfter = 20f

            // Crear un rectángulo gris claro para las estadísticas
            val statsCb = writer.directContent
            val statsRect = Rectangle(
                document.left(),
                document.bottom() + document.bottomMargin() + 100f,
                document.right(),
                document.bottom() + document.bottomMargin() + 180f
            )
            statsRect.backgroundColor = LIGHT_GRAY_COLOR
            statsCb.rectangle(statsRect)
            statsCb.fill()

            document.add(jugadoresStats)

            // Sección de entrenadores
            val entrenadoresTitle = Paragraph("Entrenadores", sectionFont)
            entrenadoresTitle.spacingBefore = 10f
            entrenadoresTitle.spacingAfter = 10f
            document.add(entrenadoresTitle)

            // Tabla de entrenadores
            val entrenadoresTable = PdfPTable(6)
            entrenadoresTable.widthPercentage = 100f

            // Establecer anchos relativos de las columnas
            val entrenadoresWidths = floatArrayOf(0.5f, 1.5f, 1.5f, 1.2f, 1.5f, 1.0f)
            entrenadoresTable.setWidths(entrenadoresWidths)

            // Cabecera de la tabla
            val entrenadoresHeaders = arrayOf("ID", "Nombre", "Apellidos", "Fecha Nacimiento", "Especialización", "País de Origen")

            entrenadoresHeaders.forEach { header ->
                val cell = PdfPCell(Phrase(header, headerFont))
                cell.backgroundColor = GREEN_COLOR
                cell.horizontalAlignment = Element.ALIGN_CENTER
                cell.verticalAlignment = Element.ALIGN_MIDDLE
                cell.setPadding(5f)
                entrenadoresTable.addCell(cell)
            }

            // Datos de entrenadores
            entrenadores.forEach { entrenador ->
                entrenadoresTable.addCell(PdfPCell(Phrase(entrenador.id.toString(), cellFont)))
                entrenadoresTable.addCell(PdfPCell(Phrase(entrenador.nombre, cellFont)))
                entrenadoresTable.addCell(PdfPCell(Phrase(entrenador.apellidos, cellFont)))
                entrenadoresTable.addCell(PdfPCell(Phrase(entrenador.fechaNacimiento.format(dateFormatter), cellFont)))
                entrenadoresTable.addCell(PdfPCell(Phrase(entrenador.especializacion.toString(), cellFont)))
                entrenadoresTable.addCell(PdfPCell(Phrase(entrenador.paisOrigen, cellFont)))
            }

            document.add(entrenadoresTable)

            // Estadísticas generales
            val generalStats = Paragraph()
            generalStats.add(Paragraph("Estadísticas Generales", statsTitleFont))
            generalStats.add(Paragraph("Total de personal: ${personal.size}", statsFont))
            generalStats.add(Paragraph("Jugadores: ${jugadores.size}", statsFont))
            generalStats.add(Paragraph("Entrenadores: ${entrenadores.size}", statsFont))

            generalStats.spacingBefore = 10f
            generalStats.spacingAfter = 20f

            document.add(generalStats)

            // Pie de página
            val footerFont = FontFactory.getFont(FontFactory.HELVETICA, 10f)
            val footer = Paragraph("Informe generado el ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))}", footerFont)
            footer.alignment = Element.ALIGN_CENTER
            footer.spacingBefore = 20f
            document.add(footer)

        } finally {
            document.close()
        }

        return file.absolutePath
    }

    /**
     * Genera un informe PDF de una convocatoria.
     * 
     * @param convocatoria La convocatoria a mostrar en el informe.
     * @param jugadores Lista de jugadores convocados.
     * @param entrenadores Lista de entrenadores de la convocatoria.
     * @param outputPath Ruta donde se guardará el archivo PDF.
     * @return La ruta del archivo PDF generado.
     */
    fun generateConvocatoriaReport(
        convocatoria: Convocatoria,
        jugadores: List<Jugador>,
        entrenadores: List<Entrenador>,
        outputPath: String
    ): String {
        val file = File(outputPath)
        val document = Document(PageSize.A4)
        val writer = PdfWriter.getInstance(document, FileOutputStream(file))

        document.open()

        try {
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val titulares = jugadores.filter { convocatoria.titulares.contains(it.id) }
            val suplentes = jugadores.filter { !convocatoria.titulares.contains(it.id) }

            // Organizar entrenadores por especialización
            val entrenadorPrincipal = entrenadores.find { it.especializacion == Entrenador.Especializacion.ENTRENADOR_PRINCIPAL }
            val entrenadorAsistente = entrenadores.find { it.especializacion == Entrenador.Especializacion.ENTRENADOR_ASISTENTE }
            val entrenadorPorteros = entrenadores.find { it.especializacion == Entrenador.Especializacion.ENTRENADOR_PORTEROS }

            // Título principal
            val titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20f, GREEN_COLOR)
            val title = Paragraph("Convocatoria - ${convocatoria.fecha.format(dateFormatter)}", titleFont)
            title.alignment = Element.ALIGN_CENTER
            title.spacingAfter = 20f
            document.add(title)

            // Información de la convocatoria
            val infoFont = FontFactory.getFont(FontFactory.HELVETICA, 12f)
            val infoTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14f, GREEN_COLOR)
            val boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12f)

            val infoConvocatoria = Paragraph()
            infoConvocatoria.add(Paragraph("Información de la Convocatoria", infoTitleFont))
            infoConvocatoria.add(Paragraph("Fecha: ${convocatoria.fecha.format(dateFormatter)}", infoFont))
            infoConvocatoria.add(Paragraph("Descripción: ${convocatoria.descripcion}", infoFont))
            infoConvocatoria.add(Paragraph("Cuerpo Técnico:", boldFont))

            if (entrenadorPrincipal != null) {
                infoConvocatoria.add(Paragraph("- Entrenador Principal: ${entrenadorPrincipal.nombre} ${entrenadorPrincipal.apellidos}", infoFont))
            }
            if (entrenadorAsistente != null) {
                infoConvocatoria.add(Paragraph("- Entrenador Asistente: ${entrenadorAsistente.nombre} ${entrenadorAsistente.apellidos}", infoFont))
            }
            if (entrenadorPorteros != null) {
                infoConvocatoria.add(Paragraph("- Entrenador de Porteros: ${entrenadorPorteros.nombre} ${entrenadorPorteros.apellidos}", infoFont))
            }

            infoConvocatoria.spacingBefore = 10f
            infoConvocatoria.spacingAfter = 20f

            document.add(infoConvocatoria)

            // Sección de titulares
            val sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16f, GREEN_COLOR)
            val titularesTitle = Paragraph("Once Titular", sectionFont)
            titularesTitle.spacingBefore = 10f
            titularesTitle.spacingAfter = 10f
            document.add(titularesTitle)

            // Tabla de titulares
            val titularesTable = PdfPTable(4)
            titularesTable.widthPercentage = 100f

            // Establecer anchos relativos de las columnas
            val titularesWidths = floatArrayOf(0.5f, 1.5f, 1.5f, 1.0f)
            titularesTable.setWidths(titularesWidths)

            // Cabecera de la tabla
            val titularesHeaders = arrayOf("Dorsal", "Nombre", "Apellidos", "Posición")
            val headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12f, WHITE_COLOR)

            titularesHeaders.forEach { header ->
                val cell = PdfPCell(Phrase(header, headerFont))
                cell.backgroundColor = GREEN_COLOR
                cell.horizontalAlignment = Element.ALIGN_CENTER
                cell.verticalAlignment = Element.ALIGN_MIDDLE
                cell.setPadding(5f)
                titularesTable.addCell(cell)
            }

            // Datos de titulares
            val cellFont = FontFactory.getFont(FontFactory.HELVETICA, 11f)

            titulares.sortedBy { it.dorsal }.forEach { jugador ->
                titularesTable.addCell(PdfPCell(Phrase(jugador.dorsal.toString(), cellFont)))
                titularesTable.addCell(PdfPCell(Phrase(jugador.nombre, cellFont)))
                titularesTable.addCell(PdfPCell(Phrase(jugador.apellidos, cellFont)))
                titularesTable.addCell(PdfPCell(Phrase(jugador.posicion.toString(), cellFont)))
            }

            document.add(titularesTable)

            // Sección de suplentes
            val suplentesTitle = Paragraph("Suplentes", sectionFont)
            suplentesTitle.spacingBefore = 10f
            suplentesTitle.spacingAfter = 10f
            document.add(suplentesTitle)

            // Tabla de suplentes
            val suplentesTable = PdfPTable(4)
            suplentesTable.widthPercentage = 100f
            suplentesTable.setWidths(titularesWidths)

            // Cabecera de la tabla - Reutilizamos los mismos headers que para titulares
            titularesHeaders.forEach { header ->
                val cell = PdfPCell(Phrase(header, headerFont))
                cell.backgroundColor = GREEN_COLOR
                cell.horizontalAlignment = Element.ALIGN_CENTER
                cell.verticalAlignment = Element.ALIGN_MIDDLE
                cell.setPadding(5f)
                suplentesTable.addCell(cell)
            }

            // Datos de suplentes
            suplentes.sortedBy { it.dorsal }.forEach { jugador ->
                suplentesTable.addCell(PdfPCell(Phrase(jugador.dorsal.toString(), cellFont)))
                suplentesTable.addCell(PdfPCell(Phrase(jugador.nombre, cellFont)))
                suplentesTable.addCell(PdfPCell(Phrase(jugador.apellidos, cellFont)))
                suplentesTable.addCell(PdfPCell(Phrase(jugador.posicion.toString(), cellFont)))
            }

            document.add(suplentesTable)

            // Estadísticas de la convocatoria
            val statsFont = FontFactory.getFont(FontFactory.HELVETICA, 12f)
            val statsTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14f, GREEN_COLOR)

            val convocatoriaStats = Paragraph()
            convocatoriaStats.add(Paragraph("Estadísticas de la Convocatoria", statsTitleFont))
            convocatoriaStats.add(Paragraph("Total de jugadores convocados: ${jugadores.size}", statsFont))
            convocatoriaStats.add(Paragraph("Titulares: ${titulares.size}", statsFont))
            convocatoriaStats.add(Paragraph("Suplentes: ${suplentes.size}", statsFont))
            convocatoriaStats.add(Paragraph("Porteros: ${jugadores.count { it.posicion == Jugador.Posicion.PORTERO }}", statsFont))
            convocatoriaStats.add(Paragraph("Defensas: ${jugadores.count { it.posicion == Jugador.Posicion.DEFENSA }}", statsFont))
            convocatoriaStats.add(Paragraph("Centrocampistas: ${jugadores.count { it.posicion == Jugador.Posicion.CENTROCAMPISTA }}", statsFont))
            convocatoriaStats.add(Paragraph("Delanteros: ${jugadores.count { it.posicion == Jugador.Posicion.DELANTERO }}", statsFont))

            convocatoriaStats.spacingBefore = 10f
            convocatoriaStats.spacingAfter = 20f

            document.add(convocatoriaStats)

            // Pie de página
            val footerFont = FontFactory.getFont(FontFactory.HELVETICA, 10f)
            val footer = Paragraph("Informe generado el ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))}", footerFont)
            footer.alignment = Element.ALIGN_CENTER
            footer.spacingBefore = 20f
            document.add(footer)

        } finally {
            document.close()
        }

        return file.absolutePath
    }
}
