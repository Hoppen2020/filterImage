plugins {
    id("com.android.library")
}

val openCVersionName = "4.5.2"
val openCVersionCode = ((4 * 100 + 5) * 100 + 2) * 10 + 0

println("OpenCV: " + openCVersionName + " " + project.buildscript.sourceFile)

android {
    compileSdk = 26

    defaultConfig {
        minSdk = 21
        targetSdk = 26

//        versionCode(openCVersionCode)
//        versionName(openCVersionName)

        externalNativeBuild {
            cmake {
                arguments("-DANDROID_STL=c++_shared")
                targets("opencv_jni_shared")
            }
        }
    }

    buildTypes {
        named("debug") {
            packagingOptions {
                doNotStrip("**/*.so")// controlled by OpenCV CMake scripts
            }
        }
        named("release") {
            packagingOptions {
                doNotStrip("**/*.so")// controlled by OpenCV CMake scripts
            }
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.txt")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_6
        targetCompatibility = JavaVersion.VERSION_1_6
    }

    sourceSets {
        named("main") {
            jniLibs.srcDir("native/libs")
            java.srcDir("java/src")
            aidl.srcDir("java/src")
            res.srcDir("java/res")
            manifest.srcFile("java/AndroidManifest.xml")
        }
    }

    externalNativeBuild {
        cmake {
            path(project.projectDir.toString() + "/libcxx_helper/CMakeLists.txt")
        }
    }
}

dependencies {
}
