package srangeldev.proyectoequipofutboljavafx.cache

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import org.lighthousegames.logging.logging
import srangeldev.proyectoequipofutboljavafx.config.AppConfig
import srangeldev.proyectoequipofutboljavafx.models.Personal
import java.util.concurrent.TimeUnit

fun provideCache(config: AppConfig): Cache<Long, Personal> {
    val logger = logging()
    logger.debug { "Inicializando Cache con capacidad ${config.cacheCapacity} y duracion ${config.cacheExpiration} s" }
    return Caffeine.newBuilder()
        .maximumSize(config.cacheCapacity)
        .expireAfterWrite(
            config.cacheExpiration,
            TimeUnit.SECONDS
        )
        .build()
}