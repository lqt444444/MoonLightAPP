// build.gradle (Module: app)
// 确保在你的 build.gradle 文件中有这些配置

plugins {
    id("com.android.application")
}

android {
    namespace = "bbs.yuchen.icu" // 已修改包名
    compileSdk = 34

    defaultConfig {
        applicationId = "bbs.yuchen.icu" // 已修改应用ID
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false

            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_15
        targetCompatibility = JavaVersion.VERSION_15
    }
    // 启用 View Binding 来更方便地访问视图
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // 确保你添加了最新的Material Design库
    implementation("com.google.android.material:material:1.12.0")

    // 其他默认依赖
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

