package srangeldev.proyectoequipofutboljavafx.newteam.di

import org.jdbi.v3.core.Jdbi
import org.koin.core.module.Module
import org.koin.dsl.module
import srangeldev.proyectoequipofutboljavafx.newteam.dao.*
import srangeldev.proyectoequipofutboljavafx.newteam.database.provideJdbi
import srangeldev.proyectoequipofutboljavafx.newteam.repository.*

/**
 * Módulo de inyección de dependencias para la base de datos
 */
val databaseModule: Module = module {
    // Proporcionar instancia de Jdbi
    single { provideJdbi() }

    // DAOs
    single { provideUserDao(get()) }
    single { provideEquipoDao(get()) }
    single { providePersonalDao(get()) }
    single { provideEntrenadorDao(get()) }
    single { provideJugadorDao(get()) }
    single { provideConvocatoriaDao(get()) }
    single { provideJugadorConvocadoDao(get()) }

    // Repositorios
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<ConvocatoriaRepository> { ConvocatoriaRepositoryImpl(get(), get(), get()) }
    single<EquipoRepository> { EquipoRepositoryImpl(get()) }
    single<PersonalRepository> { PersonalRepositoryImpl(get(), get(), get()) }
    // Los demás repositorios se actualizarán a medida que se vayan migrando
}
