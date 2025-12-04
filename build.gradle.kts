plugins {
    id("com.android.application") version "8.9.1" apply false  // ← Atualizado e estável em nov/2025
    id("com.android.library") version "8.2.1" apply false
    id("org.jetbrains.kotlin.android") version "2.0.20" apply false  // Atualize Kotlin também para compatibilidade
    id("com.google.devtools.ksp") version "2.0.20-1.0.25" apply false  // KSP compatível
    id("androidx.navigation.safeargs.kotlin") version "2.7.7" apply false
}

