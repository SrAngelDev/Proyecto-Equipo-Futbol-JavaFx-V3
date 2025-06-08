package srangeldev.proyectoequipofutboljavafx.newteam.validator

import srangeldev.proyectoequipofutboljavafx.newteam.exceptions.PersonalException
import srangeldev.proyectoequipofutboljavafx.newteam.models.Convocatoria
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import java.time.LocalDate

/**
 * Clase para validar los datos de una convocatoria.
 */
class ConvocatoriaValidator : Validator<Convocatoria> {
    
    /**
     * Valida los datos de la convocatoria.
     * @param convocatoria El objeto Convocatoria a validar
     * @throws PersonalException.PersonalNotFoundException si el id es negativo
     * @throws PersonalException.PersonalStorageException si la descripción está vacía
     * @throws PersonalException.PersonalStorageException si la fecha es anterior a la fecha actual
     * @throws PersonalException.PersonalStorageException si el equipoId o entrenadorId son negativos
     */
    override fun validate(convocatoria: Convocatoria) {
        if (convocatoria.id < 0) {
            throw PersonalException.PersonalNotFoundException(convocatoria.id)
        }
        
        if (convocatoria.descripcion.isEmpty()) {
            throw PersonalException.PersonalStorageException("La descripción no puede estar vacía")
        }
        
        if (convocatoria.fecha.isBefore(LocalDate.now())) {
            throw PersonalException.PersonalStorageException("La fecha de la convocatoria no puede ser anterior a la fecha actual")
        }
        
        if (convocatoria.equipoId <= 0) {
            throw PersonalException.PersonalStorageException("El ID del equipo debe ser positivo")
        }
        
        if (convocatoria.entrenadorId <= 0) {
            throw PersonalException.PersonalStorageException("El ID del entrenador debe ser positivo")
        }
        
        // Validar número máximo de jugadores
        if (convocatoria.jugadores.size > 18) {
            throw PersonalException.PersonalStorageException("Una convocatoria no puede tener más de 18 jugadores")
        }
        
        // Validar que todos los titulares estén en la lista de convocados
        if (convocatoria.titulares.size > 11) {
            throw PersonalException.PersonalStorageException("No puede haber más de 11 titulares")
        }
        
        if (!convocatoria.jugadores.containsAll(convocatoria.titulares)) {
            throw PersonalException.PersonalStorageException("Todos los titulares deben estar en la lista de convocados")
        }
        
        // Validar que no haya IDs duplicados en la lista de jugadores
        if (convocatoria.jugadores.size != convocatoria.jugadores.distinct().size) {
            throw PersonalException.PersonalStorageException("No puede haber jugadores duplicados en la convocatoria")
        }
        
        // Validar que no haya IDs duplicados en la lista de titulares
        if (convocatoria.titulares.size != convocatoria.titulares.distinct().size) {
            throw PersonalException.PersonalStorageException("No puede haber titulares duplicados en la convocatoria")
        }
    }
}