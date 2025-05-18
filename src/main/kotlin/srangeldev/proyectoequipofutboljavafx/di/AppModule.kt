package srangeldev.proyectoequipofutboljavafx.di

import org.koin.dsl.module
import srangeldev.proyectoequipofutboljavafx.newteam.cache.Cache
import srangeldev.proyectoequipofutboljavafx.newteam.cache.CacheImpl
import srangeldev.proyectoequipofutboljavafx.newteam.models.Personal
import srangeldev.proyectoequipofutboljavafx.newteam.repository.PersonalRepository
import srangeldev.proyectoequipofutboljavafx.newteam.repository.PersonalRespositoryImpl
import srangeldev.proyectoequipofutboljavafx.newteam.repository.UserRepository
import srangeldev.proyectoequipofutboljavafx.newteam.repository.UserRepositoryImpl
import srangeldev.proyectoequipofutboljavafx.newteam.service.PersonalService
import srangeldev.proyectoequipofutboljavafx.newteam.service.PersonalServiceImpl
import srangeldev.proyectoequipofutboljavafx.newteam.storage.PersonalStorage
import srangeldev.proyectoequipofutboljavafx.newteam.storage.PersonalStorageImpl
import srangeldev.proyectoequipofutboljavafx.newteam.storage.PersonalStorageJson
import srangeldev.proyectoequipofutboljavafx.viewmodels.PersonalViewModel

private const val CACHE_SIZE = 5

/**
 * Módulo principal de la aplicación para la inyección de dependencias con Koin
 */
val appModule = module {
    // Repositories
    single<UserRepository> { UserRepositoryImpl() }
    single<PersonalRepository> { PersonalRespositoryImpl() }

    // Storage
    single<PersonalStorage> { PersonalStorageImpl() }

    // Cache
    single<Cache<Int, Personal>> { CacheImpl(CACHE_SIZE) }

    // Services
    single<PersonalService> { 
        PersonalServiceImpl(
            storage = get(),
            repository = get(),
            cache = get()
        ) 
    }

    // Storage implementations
    single { PersonalStorageJson() }

    // ViewModels
    factory { PersonalViewModel(get()) }
}
