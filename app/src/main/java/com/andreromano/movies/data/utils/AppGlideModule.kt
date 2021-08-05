package com.andreromano.movies.data.utils

import android.content.Context
import android.util.Log
import com.andreromano.movies.R
import com.andreromano.movies.common.ResultKt
import com.andreromano.movies.data.ApiConfigurationRepository
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Priority
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.data.HttpUrlFetcher
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.InputStream

sealed class ImageNetworkPath(open val path: String) {
    data class Backdrop(override val path: String) : ImageNetworkPath(path)
    data class Logo(override val path: String) : ImageNetworkPath(path)
    data class Poster(override val path: String) : ImageNetworkPath(path)
    data class Profile(override val path: String) : ImageNetworkPath(path)
    data class Still(override val path: String) : ImageNetworkPath(path)
}

@GlideModule
class AppGlideModule : AppGlideModule(), KoinComponent {

    private val apiConfigurationRepository: ApiConfigurationRepository by inject()

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setLogLevel(Log.ERROR)
        builder.setDefaultRequestOptions(
            RequestOptions()
                .placeholder(R.drawable.ic_baseline_image_24)
                .error(R.drawable.ic_alert)
                .fallback(R.drawable.ic_baseline_image_not_supported_24)
        )
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.append(
            ImageNetworkPath::class.java,
            InputStream::class.java,
            ApiImageLoaderFactory(apiConfigurationRepository),
        )
    }
}

class ApiImageLoaderFactory(
    private val apiConfigurationRepository: ApiConfigurationRepository,
) : ModelLoaderFactory<ImageNetworkPath, InputStream> {
    override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<ImageNetworkPath, InputStream> =
        ApiImageLoader(apiConfigurationRepository)

    override fun teardown() {}
}

class ApiImageLoader(
    private val apiConfigurationRepository: ApiConfigurationRepository,
) : ModelLoader<ImageNetworkPath, InputStream> {
    override fun buildLoadData(model: ImageNetworkPath, width: Int, height: Int, options: Options): ModelLoader.LoadData<InputStream> =
        ModelLoader.LoadData(
            ObjectKey(model),
            ApiImageDataFetcher(apiConfigurationRepository, model, width, height)
        )

    override fun handles(model: ImageNetworkPath): Boolean = true
}

class ApiImageDataFetcher(
    private val apiConfigurationRepository: ApiConfigurationRepository,
    private val model: ImageNetworkPath,
    private val width: Int,
    private val height: Int,
) : DataFetcher<InputStream> {
    private var fetcher: HttpUrlFetcher? = null

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        runBlocking {
            when (val configResult = apiConfigurationRepository.getConfiguration()) {
                is ResultKt.Success -> {
                    // TODO: Fetch images according to width and height, check available image sizes in apiConfiguration
                    val glideUrl = GlideUrl(configResult.data.images.secureBaseUrl + "original" + model.path)
                    val httpUrlFetcher = HttpUrlFetcher(glideUrl, 2500)
                    fetcher = httpUrlFetcher
                    httpUrlFetcher.loadData(priority, callback)
                }
                is ResultKt.Failure -> callback.onLoadFailed(Exception(configResult.error))
            }
        }
    }

    override fun getDataClass(): Class<InputStream> = InputStream::class.java

    override fun cleanup() {
        fetcher?.cleanup()
    }

    override fun getDataSource(): DataSource = DataSource.REMOTE

    override fun cancel() {
        fetcher?.cancel()
    }
}