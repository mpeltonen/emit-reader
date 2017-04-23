package emitreader.targets.rogainmanager

import java.net.InetSocketAddress
import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorRef, Props}
import akka.io.Tcp._
import akka.io.{IO, Tcp}
import akka.util.ByteString

import scala.concurrent.duration.FiniteDuration

class RogainManagerClient(remote: InetSocketAddress, listener: ActorRef, log: ActorRef) extends Actor {
  import context.{dispatcher, system}

  self ! "connect"

  override def receive: Receive = disconnected

  private def disconnected: Receive = {
    case "connect" => {
      log ! s"Connecting to Rogain Manager at ${remote}"
      IO(Tcp) ! Connect(remote, timeout = Some(FiniteDuration(5, TimeUnit.SECONDS)))
    }
    case CommandFailed(_: Connect) =>
      log ! s"Connection failed. Retrying in 5 seconds..."
      scheduleReconnect()
    case c @ Connected(remote, local) =>
      log ! s"Connected to ${remote}"
      val connection = sender()
      connection ! Register(self)
      context.become(connected(connection))
  }

  private def connected(connection: ActorRef): Receive = {
    case data: ByteString => connection ! Write(data)
    case CommandFailed(w: Write) => log ! "Error sending data to Rogain Manager"
    case Received(data) => listener ! data
    case _: ConnectionClosed => {
      log ! s"Connection closed. Reconnecting in 5 seconds..."
      context.become(disconnected)
      scheduleReconnect()
    }
  }

  def scheduleReconnect() = system.scheduler.scheduleOnce(FiniteDuration(5, TimeUnit.SECONDS), self, "connect")
}

object RogainManagerClient {
  def props(remote: InetSocketAddress, listener: ActorRef, log: ActorRef) = Props(classOf[RogainManagerClient], remote, listener, log)
}
