package emitreader.targets.rogainmanager

import java.nio.charset.Charset

import akka.actor.{Actor, ActorRef, Props}
import akka.util.ByteString

class RogainManagerListener(log: ActorRef) extends Actor {
  override def receive: Receive = {
    case bs: ByteString => log ! bs.decodeString(Charset.defaultCharset())
  }
}

object RogainManagerListener {
  def props(log: ActorRef) = Props(classOf[RogainManagerListener], log)
}
