package emitreader.targets.rogainmanager

import akka.actor.{Actor, Props}
import akka.util.ByteString

class RogainManagerListener(handler: ByteString => Unit) extends Actor {
  override def receive: Receive = {
    case bs: ByteString => handler(bs)
  }
}

object RogainManagerListener {
  def props(handler: ByteString => Unit) = Props(classOf[RogainManagerListener], handler)
}
