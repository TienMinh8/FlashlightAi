plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.flashlightai"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.flashlightai"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    // Loại bỏ warning về duplicate resources
    lint {
        abortOnError = false
        checkReleaseBuilds = false
        disable += "DuplicateDefinition"
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity:1.8.2")
    implementation("androidx.core:core:1.12.0")
    
    // ViewPager2 for intro slides
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    
    // Animations
    implementation("androidx.dynamicanimation:dynamicanimation:1.0.0")
    
    // Navigation Component
    implementation("androidx.navigation:navigation-fragment:2.7.6")
    implementation("androidx.navigation:navigation-ui:2.7.6")
    
    // Preferences
    implementation("androidx.preference:preference:1.2.1")
    
    // Google AdMob for monetization
    implementation("com.google.android.gms:play-services-ads:22.6.0")
    implementation("com.google.android.gms:play-services-ads-lite:22.6.0")
    implementation("com.google.android.gms:play-services-ads-identifier:18.0.1")
    implementation("com.google.android.gms:play-services-base:18.3.0")
    
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}