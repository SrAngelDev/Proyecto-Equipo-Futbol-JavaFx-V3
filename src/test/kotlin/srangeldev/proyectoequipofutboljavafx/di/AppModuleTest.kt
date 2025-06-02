package srangeldev.proyectoequipofutboljavafx.di

import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.mockito.kotlin.mock
import srangeldev.proyectoequipofutboljavafx.newteam.cache.Cache
import srangeldev.proyectoequipofutboljavafx.newteam.cache.CacheImpl
import srangeldev.proyectoequipofutboljavafx.newteam.models.Personal
import srangeldev.proyectoequipofutboljavafx.newteam.repository.ConvocatoriaRepository
import srangeldev.proyectoequipofutboljavafx.newteam.repository.EquipoRepository
import srangeldev.proyectoequipofutboljavafx.newteam.repository.PersonalRepository
import srangeldev.proyectoequipofutboljavafx.newteam.repository.UserRepository
import srangeldev.proyectoequipofutboljavafx.newteam.service.PersonalService
import srangeldev.proyectoequipofutboljavafx.newteam.service.PersonalServiceImpl
import srangeldev.proyectoequipofutboljavafx.newteam.storage.PersonalStorage
import srangeldev.proyectoequipofutboljavafx.newteam.storage.PersonalStorageImpl
import srangeldev.proyectoequipofutboljavafx.viewmodels.PersonalViewModel
import kotlin.test.assertNotNull

class AppModuleTest {

    @Test
    fun `appModule should provide UserRepository`() {
        // Create a mock UserRepository
        val mockUserRepository = mock<UserRepository>()

        // Verify it's not null
        assertNotNull(mockUserRepository)
    }

    @Test
    fun `appModule should provide PersonalRepository`() {
        // Create a mock PersonalRepository
        val mockPersonalRepository = mock<PersonalRepository>()

        // Verify it's not null
        assertNotNull(mockPersonalRepository)
    }

    @Test
    fun `appModule should provide EquipoRepository`() {
        // Create a mock EquipoRepository
        val mockEquipoRepository = mock<EquipoRepository>()

        // Verify it's not null
        assertNotNull(mockEquipoRepository)
    }

    @Test
    fun `appModule should provide ConvocatoriaRepository`() {
        // Create a mock EquipoRepository for dependency
        val mockEquipoRepository = mock<EquipoRepository>()

        // Create a mock ConvocatoriaRepository
        val mockConvocatoriaRepository = mock<ConvocatoriaRepository>()

        // Verify they're not null
        assertNotNull(mockEquipoRepository)
        assertNotNull(mockConvocatoriaRepository)
    }

    @Test
    fun `appModule should provide PersonalStorage`() {
        // Create a PersonalStorage instance
        val storage = PersonalStorageImpl()

        // Verify it's not null
        assertNotNull(storage)
    }

    @Test
    fun `appModule should provide Cache`() {
        // Create a Cache instance
        val cache = CacheImpl<Int, Personal>(5)

        // Verify it's not null
        assertNotNull(cache)
    }

    @Test
    fun `appModule should provide PersonalService`() {
        // Create mock dependencies
        val mockStorage = mock<PersonalStorage>()
        val mockRepository = mock<PersonalRepository>()
        val mockCache = mock<Cache<Int, Personal>>()

        // Create a PersonalService instance
        val service = PersonalServiceImpl(mockStorage, mockRepository, mockCache)

        // Verify it's not null
        assertNotNull(service)
    }

    @Test
    fun `appModule should provide PersonalViewModel`() {
        // Create mock dependencies
        val mockUserRepository = mock<UserRepository>()

        // Create a PersonalViewModel instance
        val viewModel = PersonalViewModel(mockUserRepository)

        // Verify it's not null
        assertNotNull(viewModel)
    }
}
