package emitreader.config

import java.io.{File, FileInputStream, FileOutputStream}
import java.util.Properties

import emitreader.config.Config.Keys.KeyType

object Config {
  private lazy val configDir: File = {
    val dir = new File(s"${System.getProperty("user.home")}${File.separator}.emitreader")
    if (!dir.exists()) dir.mkdirs()
    dir
  }

  private lazy val configFile: File = {
    val file = new File(s"${configDir.getAbsolutePath}/emitreader.properties")
    if (!file.exists()) file.createNewFile()
    file
  }

  private lazy val properties: Properties = {
    val props = new Properties()
    props.load(new FileInputStream(configFile))
    props
  }

  def setValue(key: KeyType, value: String): Unit = {
    properties.setProperty(key.toString, value)
    properties.store(new FileOutputStream(configFile), "")
  }

  def getValue(key: KeyType, default: String = ""): String = properties.getProperty(key.toString, default)

  object Keys extends Enumeration {
    type KeyType = Value
    val SourceType, TargetType = Value
  }
}
