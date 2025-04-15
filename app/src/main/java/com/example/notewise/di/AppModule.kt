package com.example.notewise.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.notewise.data.local.dao.NoteDao
import com.example.notewise.data.local.db.NoteDatabase
import com.example.notewise.data.remote.api.HuggingFaceTextApi
import com.example.notewise.domain.repository.NoteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application): NoteDatabase {
        return Room.databaseBuilder(
            app,
            NoteDatabase::class.java,
            "note_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideNoteDao(db: NoteDatabase): NoteDao {
        return db.noteDao()
    }

    @Provides
    @Singleton
    fun provideNoteRepository(dao: NoteDao): NoteRepository {
        return NoteRepository(dao)
    }

    @Provides
    @Singleton
    @Named("huggingface_api")
    fun provideHuggingFaceApi(): HuggingFaceTextApi {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://api-inference.huggingface.co")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HuggingFaceTextApi::class.java)
    }

    // provide context
    @Provides
    @Singleton
    fun provideContext(app: Application): Context {
        return app.applicationContext
    }
}
