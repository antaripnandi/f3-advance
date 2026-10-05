import java.io.File
import java.util.zip.ZipFile

plugins {
    id("dev.kikugie.loom-back-compat")
    id("maven-publish")
}

val modId = "f3advanced"
val mcVersion = sc.current.version

group = "com.f3advanced"
version = "0.1.1+mc$mcVersion"

repositories {
    maven("https://maven.fabricmc.net/")
    maven("https://maven.terraformersmc.com/releases/")
    maven("https://maven.shedaniel.me/")
    mavenCentral()
}

val fabricApiVersions = mapOf(
    "1.20" to "0.83.0+1.20",
    "1.20.1" to "0.92.6+1.20.1",
    "1.20.2" to "0.91.6+1.20.2",
    "1.20.3" to "0.91.1+1.20.3",
    "1.20.4" to "0.97.3+1.20.4",
    "1.20.5" to "0.97.8+1.20.5",
    "1.20.6" to "0.100.8+1.20.6",
    "1.21" to "0.102.0+1.21",
    "1.21.1" to "0.116.14+1.21.1",
    "1.21.2" to "0.106.1+1.21.2",
    "1.21.3" to "0.107.3+1.21.3",
    "1.21.4" to "0.119.4+1.21.4",
    "1.21.5" to "0.128.2+1.21.5",
    "1.21.6" to "0.128.2+1.21.6",
    "1.21.7" to "0.129.0+1.21.7",
    "1.21.8" to "0.136.1+1.21.8",
    "1.21.9" to "0.134.0+1.21.9",
    "1.21.10" to "0.139.0+1.21.10",
    "1.21.11" to "0.141.5+1.21.11",
    "26.1" to "0.155.2+26.1.2",
    "26.1.1" to "0.155.2+26.1.2",
    "26.1.2" to "0.155.2+26.1.2",
    "26.2" to "0.156.0+26.2",
    "26.3" to "0.161.0+26.3",
)

val modMenuVersions = mapOf(
    "1.20" to "7.2.2",
    "1.20.1" to "7.2.2",
    "1.20.2" to "8.0.1",
    "1.20.3" to "9.0.0",
    "1.20.4" to "9.2.0",
    "1.20.5" to "10.0.0",
    "1.20.6" to "10.0.0",
    "1.21" to "11.0.4",
    "1.21.1" to "11.0.4",
    "1.21.2" to "12.0.0",
    "1.21.3" to "12.0.0",
    "1.21.4" to "13.0.4",
    "1.21.5" to "14.0.0",
    "1.21.6" to "15.0.0",
    "1.21.7" to "15.0.0",
    "1.21.8" to "15.0.2",
    "1.21.9" to "16.0.0",
    "1.21.10" to "17.0.0",
    "1.21.11" to "17.0.1-beta.1",
    "26.1" to "18.0.0",
    "26.1.1" to "18.0.0",
    "26.1.2" to "18.0.0",
    "26.2" to "18.0.0",
    "26.3" to "18.0.0",
)

val clothConfigVersions = mapOf(
    "1.20" to "11.1.136",
    "1.20.1" to "11.1.136",
    "1.20.2" to "12.0.119",
    "1.20.3" to "13.0.138",
    "1.20.4" to "13.0.138",
    "1.20.5" to "14.0.139",
    "1.20.6" to "14.0.139",
    "1.21" to "15.0.140",
    "1.21.1" to "15.0.140",
    "1.21.2" to "16.0.141",
    "1.21.3" to "16.0.141",
    "1.21.4" to "17.0.144",
    "1.21.5" to "18.0.145",
    "1.21.6" to "19.0.147",
    "1.21.7" to "19.0.147",
    "1.21.8" to "19.0.147",
    "1.21.9" to "20.0.150",
    "1.21.10" to "21.0.152",
    "1.21.11" to "21.11.153",
    "26.1" to "26.1.154",
    "26.1.1" to "26.1.154",
    "26.1.2" to "26.1.154",
    "26.2" to "26.1.154",
    "26.3" to "26.1.154",
)

val javaVersion = when {
    sc.current.parsed >= "26.1" -> 25
    sc.current.parsed >= "1.20.5" -> 21
    sc.current.parsed >= "1.18" -> 17
    else -> 21
}

base {
    archivesName.set("$modId-$mcVersion")
}

loom {
    splitEnvironmentSourceSets()
    mods {
        create(modId) {
            sourceSet(sourceSets["main"])
            sourceSet(sourceSets["client"])
        }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:$mcVersion")
    loomx.applyMojangMappings()
    modImplementation("net.fabricmc:fabric-loader:0.19.3")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${fabricApiVersions[mcVersion] ?: error("Missing Fabric API version for $mcVersion")}")
    val localModMenu = rootProject.file("work/libs/modmenu-${modMenuVersions[mcVersion]}.jar")
    val localCloth = rootProject.file("work/libs/cloth-config-${clothConfigVersions[mcVersion]}-fabric.jar")
    if (localModMenu.exists() && localCloth.exists()) {
        modCompileOnly(files(localModMenu))
        modRuntimeOnly(files(localModMenu))
        modCompileOnly(files(localCloth))
        modRuntimeOnly(files(localCloth))
    } else {
        modCompileOnly("com.terraformersmc:modmenu:${modMenuVersions[mcVersion] ?: error("Missing Mod Menu version for $mcVersion")}")
        modRuntimeOnly("com.terraformersmc:modmenu:${modMenuVersions[mcVersion] ?: error("Missing Mod Menu version for $mcVersion")}")
        modCompileOnly("me.shedaniel.cloth:cloth-config-fabric:${clothConfigVersions[mcVersion] ?: error("Missing Cloth Config version for $mcVersion")}")
        modRuntimeOnly("me.shedaniel.cloth:cloth-config-fabric:${clothConfigVersions[mcVersion] ?: error("Missing Cloth Config version for $mcVersion")}")
    }
}

val minecraftDependencyRanges = mapOf(
    "1.21.1" to ">=1.21 <=1.21.1",
    "1.21.4" to ">=1.21.2 <=1.21.4",
    "1.21.5" to "1.21.5",
    "1.21.8" to ">=1.21.6 <=1.21.8",
    "1.21.11" to ">=1.21.9 <=1.21.11",
    "26.1.1" to ">=26.1 <=26.1.1",
    "26.1.2" to "26.1.2",
    "26.2" to "26.2",
    "26.3" to "26.3",
)

tasks.processResources {
    val mcRange = minecraftDependencyRanges[mcVersion] ?: mcVersion
    inputs.property("version", project.version)
    inputs.property("minecraft_version", mcRange)
    filesMatching("fabric.mod.json") {
        expand(
            "version" to project.version,
            "minecraft_version" to mcRange
        )
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(javaVersion)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(javaVersion))
    withSourcesJar()
}

tasks.register<Copy>("copyBuiltJarToOutputs") {
    dependsOn(loomx.modJar)
    from(loomx.modJar.flatMap { it.archiveFile })
    into(rootProject.layout.projectDirectory.dir("outputs"))
}

val jarPath = File("C:\\Users\\banta\\.gradle\\caches\\fabric-loom\\minecraftMaven\\net\\minecraft\\minecraft-clientonly-deobf\\26.1.2\\minecraft-clientonly-deobf-26.1.2.jar")
if (jarPath.exists() && mcVersion == "26.1.2") {
    println("=== DIAGNOSTIC START ===")
    ZipFile(jarPath).use { zip ->
        val list = listOf(
            "net/minecraft/client/gui/components/DebugScreenOverlay.class",
            "net/minecraft/client/gui/GuiGraphicsExtractor.class",
            "net/minecraft/client/gui/screens/Screen.class"
        )
        for (name in list) {
            val entry = zip.getEntry(name) ?: continue
            println("--- $name ---")
            val reader = org.objectweb.asm.ClassReader(zip.getInputStream(entry))
            val node = org.objectweb.asm.tree.ClassNode()
            reader.accept(node, 0)
            node.methods.forEach { method ->
                println("${method.name}${method.desc}")
            }
        }
    }
    println("=== DIAGNOSTIC END ===")
}
