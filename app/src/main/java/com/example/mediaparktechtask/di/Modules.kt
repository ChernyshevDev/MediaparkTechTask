package com.example.mediaparktechtask.di

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mediaparktechtask.data.providers.ImageDownloaderImpl
import com.example.mediaparktechtask.data.providers.NewsProviderImpl
import com.example.mediaparktechtask.domain.contract.ImageDownloader
import com.example.mediaparktechtask.domain.contract.NewsProvider
import com.example.mediaparktechtask.presentation.MainActivity
import com.example.mediaparktechtask.presentation.news_page.NewsPageFragment
import com.example.mediaparktechtask.presentation.news_page.NewsPageViewModel
import com.example.mediaparktechtask.presentation.search_page.SearchPageFragment
import com.example.mediaparktechtask.presentation.search_page.SearchPageViewModel
import com.example.mediaparktechtask.presentation.search_page.filters_pages.SearchInFragment
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import kotlin.reflect.KClass

@Module
abstract class FragmentModule {
    @ContributesAndroidInjector
    internal abstract fun contributeNewsPageFragment(): NewsPageFragment

    @ContributesAndroidInjector
    internal abstract fun contributeSearchPageFragment(): SearchPageFragment

    @ContributesAndroidInjector
    internal abstract fun contributesSearchInFragment(): SearchInFragment
}

@Module
abstract class ActivityModule {
    @Binds
    internal abstract fun provideContext(app: Application): Context

    @ContributesAndroidInjector
    internal abstract fun bindsMainActivity(): MainActivity
}

@Module
internal class ProvidersModule {
    @Provides
    fun providesNewsProvider(
        newsProviderImpl: NewsProviderImpl
    ): NewsProvider = newsProviderImpl

    @Provides
    fun providesImageDownloaderProvider(
        imageDownloaderImpl: ImageDownloaderImpl
    ): ImageDownloader = imageDownloaderImpl
}

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(NewsPageViewModel::class)
    internal abstract fun bindsNewsPageViewModel(newsPageViewModel: NewsPageViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchPageViewModel::class)
    internal abstract fun bindsSearchPageViewModel(searchPageViewModel: SearchPageViewModel): ViewModel

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}

@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@MapKey
internal annotation class ViewModelKey(val value: KClass<out ViewModel>)