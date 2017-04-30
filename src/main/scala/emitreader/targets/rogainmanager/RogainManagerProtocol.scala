package emitreader.targets.rogainmanager

import java.nio.charset.Charset

import akka.actor.ActorRef
import akka.util.ByteString
import emitreader.domain.EmitData

class RogainManagerProtocol(log: ActorRef) {
  private var initCommands = List[String]()

  def resetProtocol(): Unit = {
    initCommands = List("n EmitReader\r\n", "i 0\r\n", "si\r\n")
  }

  def responseHandler(response: ByteString): Option[ByteString] = {
    logResponse(response)
    val cmd = response.decodeString(Charset.defaultCharset()) match {
      case s if s.startsWith("R*M Welcome") => {
        resetProtocol()
        popInitCmd()
      }
      case s if s.startsWith("OK") || s.startsWith("I ") => {
        if (initCommands.isEmpty) {
          log ! "Ready to send Emit data to Rogain Manager"
        }
        popInitCmd()
      }
      case _ => None
    }
    cmd.foreach(logCommand)
    cmd
  }

  def toSendDataCommand(emitData: EmitData): ByteString = {
    ByteString()
  }

  private def popInitCmd(): Option[ByteString] = {
    val cmd = initCommands.headOption
    if (cmd.isDefined) {
      initCommands = initCommands.tail
    }
    cmd.map(ByteString.fromString)
  }

  private def logResponse(bs: ByteString): Unit = {
    logByteString("<==", bs)
  }

  private def logCommand(bs: ByteString): Unit = {
    logByteString("==>", bs)
  }

  private def logByteString(prefix: String, bs: ByteString): Unit = {
    log ! s"${prefix} ${bs.decodeString(Charset.defaultCharset()).replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r")}"
  }
}
