package fr.hammons.xperimenta

import bleep.BleepCodegenScript
import bleep.Commands
import bleep.Started
import bleep.model.CrossProjectName
import bleep.model.ProjectName
import org.openjdk.jmh.generators.bytecode.JmhBytecodeGenerator
import bleep.internal.FileUtils
import java.nio.file.Files

object GenJmh extends BleepCodegenScript("GenJmh") {
  def run(
      started: Started,
      commands: Commands,
      targets: List[Target],
      args: List[String]
  ): Unit =
    val benchmarksProject = CrossProjectName(ProjectName("core-benches"), None)

    targets.foreach { target =>
      val classesDir = started.projectPaths(benchmarksProject).classes

      FileUtils.deleteDirectory(target.sources)
      Files.createDirectories(target.sources)
      FileUtils.deleteDirectory(target.resources)
      Files.createDirectories(target.resources)

      JmhBytecodeGenerator.main(
        Array(
          classesDir.toString(),
          target.sources.toString(),
          target.resources.toString(),
          "default"
        )
      )
    }
}
