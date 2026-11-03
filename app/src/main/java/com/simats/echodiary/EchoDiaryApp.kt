package com.simats.echodiary

import android.app.Application
import com.simats.echodiary.data.local.SessionManager
import com.simats.echodiary.data.network.RetrofitClient
import com.simats.echodiary.data.repository.AuthRepository
import com.simats.echodiary.data.repository.AuthRepositoryImpl

class EchoDiaryApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: EchoDiaryApp
            private set

        fun getAuthRepository(): AuthRepository {
            val api = RetrofitClient.getApi(instance)
            val sessionManager = SessionManager.getInstance(instance)
            return AuthRepositoryImpl(api, sessionManager)
        }

        fun getSessionManager(): SessionManager {
            return SessionManager.getInstance(instance)
        }
    }
}

