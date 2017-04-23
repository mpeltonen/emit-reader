package emitreader.targets.rogainmanager

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import akka.actor.{Actor, Props}

import scala.collection.mutable
import scalafx.application.Platform

class RogainManagerUiLogger(log: mutable.Buffer[CharSequence]) extends Actor {
  override def receive: Receive = {
    case cs: CharSequence => {
      val timeStamp = LocalDateTime.now.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
      Platform.runLater(log += s"${timeStamp} - ${cs.toString.trim}")
    }
  }
}

object RogainManagerUiLogger {
  def props(log: mutable.Buffer[CharSequence]) = Props(classOf[RogainManagerUiLogger], log)
}



