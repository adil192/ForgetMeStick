plugins {
	id("gg.essential.multi-version")
	id("gg.essential.defaults")
}

val modVersion: String by project
val mavenGroup: String by project
version = modVersion
group = mavenGroup

val minecraftVersion = project.property("minecraft_version") as String
val yarnMappings = project.property("yarn_mappings") as String
val loaderVersion = project.property("loader_version") as String
val fabricVersion12101: String by project
val fabricVersion12104: String by project
val fabricVersion12001: String by project

val javaVersion = when {
	project.platform.mcMinor >= 21 -> JavaVersion.VERSION_21
	else -> JavaVersion.VERSION_17
}

val finalJarsDir = "${project.rootDir}/jars"

base {
	val archiveBase: String by project
	archivesName.set("$archiveBase-${platform.loaderStr}-${getMinecraftVersionsForFileName()}")
}

repositories {
	// Add repositories to retrieve artifacts from in here.
	// You should only use this when depending on other mods because
	// Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
	// See https://docs.gradle.org/current/userguide/declaring_repositories.html
	// for more information about repositories.
}

dependencies {
	val fabricVersion = when (project.platform.mcVersionStr) {
		"1.21.4" -> fabricVersion12104
		"1.21.1" -> fabricVersion12101
		else -> fabricVersion12001
	}
	modImplementation("net.fabricmc.fabric-api:fabric-api:${fabricVersion}")
}

java {
	// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
	// if it is present.
	// If you remove this line, sources will not be generated.
	withSourcesJar()

	sourceCompatibility = javaVersion
	targetCompatibility = javaVersion
}

tasks {
	processResources {
		inputs.property("java", javaVersion.majorVersion)
		inputs.property("version", project.version)
		inputs.property("mcVersionStr", project.platform.mcVersionStr)
		filesMatching(listOf("fabric.mod.json")) {
			expand(
				mapOf(
					"version" to modVersion,
					"minecraftVersions" to getMinecraftVersionsForFabric(),
					"mcVersionStr" to project.platform.mcVersionStr,
					"java" to javaVersion.majorVersion,
				)
			)
		}
	}

	withType<Jar> {
		from(rootProject.file("LICENSE"))
	}

	register<Copy>("copyJars") {
		File(finalJarsDir).mkdir()
		from(remapJar.get().archiveFile)
		into(finalJarsDir)
		from(remapSourcesJar.get().archiveFile)
		into(finalJarsDir)
	}

	build {
		dependsOn("copyJars")
	}

	clean {
		delete(finalJarsDir)
	}
}


fun getMinecraftVersionsForFileName(): String {
	return when (project.platform.mcVersionStr) {
		"1.21.4" -> "1.21.2-1.21.5"
		"1.21.1" -> "1.21.0-1.21.1"
		"1.20.1" -> "1.20.x"
		else -> project.platform.mcVersionStr
	}
}

fun getMinecraftVersionsForFabric(): String {
	return when (project.platform.mcVersionStr) {
		"1.21.4" -> "~1.21.2"
		"1.21.1" -> ">=1.21 <1.21.2"
		"1.20.1" -> "~1.20"
		else -> "~${project.platform.mcVersionStr}"
	}
}
