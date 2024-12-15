plugins {
  `java-library`
  id("io.papermc.paperweight.userdev") version "1.7.7"
  id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "filip.bedwars"
version = "1.1"
description = "Highly customizable Bedwars plugin"

java {
  // Configure the java toolchain. This allows gradle to auto-provision JDK 17 on systems that only have JDK 8 installed for example.
  toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
  mavenCentral()
  maven("https://repo.onarandombox.com/content/groups/public/")
}

dependencies {
  paperweight.paperDevBundle("1.20.4-R0.1-SNAPSHOT")
  compileOnly("com.onarandombox.multiversecore:multiverse-core:4.3.2")
}

tasks {
  // Configure reobfJar to run when invoking the build task
  assemble {
    dependsOn(reobfJar)
  }

  compileJava {
    options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything

    // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
    // See https://openjdk.java.net/jeps/247 for more information.
    options.release.set(17)
  }
  javadoc {
    options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
  }
  processResources {
    filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
    val props = mapOf(
      "name" to project.name,
      "version" to project.version,
      "description" to project.description,
      "api-version" to "1.17"
    )
    inputs.properties(props)
    filesMatching("plugin.yml") {
      expand(props)
    }
  }
  runServer {
    minecraftVersion("1.17.1")
    downloadPlugins {
      github("Multiverse", "Multiverse-Core", "4.3.14", "multiverse-core-4.3.14.jar")
    }
  }
}
