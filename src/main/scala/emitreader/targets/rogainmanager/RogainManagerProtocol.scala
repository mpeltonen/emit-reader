package emitreader.targets.rogainmanager

import java.nio.charset.Charset
import java.time.{Instant, LocalDateTime, ZoneId}

import akka.actor.ActorRef
import akka.util.ByteString
import emitreader.domain.{EmitData, Punch}

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

  def createSendDataCommand(emitData: EmitData): ByteString = {
    def toLocaDateTime(epochMillis: Long): LocalDateTime = {
      Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).toLocalDateTime()
    }

    def toRogainManagerTimestampCentiSec(localDateTime: LocalDateTime): Int = {
      toRogainManagerTimestampSec(localDateTime) * 100
    }

    def toRogainManagerTimestampSec(localDateTime: LocalDateTime): Int = {
      (localDateTime.getHour() * 3600 + localDateTime.getMinute * 60 + localDateTime.getSecond)
    }

    def rogainManagerFinishTime(): Int = {
      val finishDateTime = toLocaDateTime(emitData.lastControlPunchTime)
      toRogainManagerTimestampCentiSec(finishDateTime)
    }

    def rogainManagerPunchingTime(punch: Punch): Int = {
      val punchDateTime = toLocaDateTime(emitData.punchingTime(punch))
      toRogainManagerTimestampSec(punchDateTime)
    }

    val punchData = emitData.punches
      .filter(_.controlCode != emitData.readoutControlCode)
      .map(p => (p.controlCode, rogainManagerPunchingTime(p)))
      .map(p => s"${p._1};${p._2}")

    val data = s"${emitData.cardId};-1;-1;${rogainManagerFinishTime()};6;${punchData.size};${punchData.mkString(";")}"
    val cmd = ByteString(s"s ${data}\r\n")

    logCommand(cmd)
    cmd
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
