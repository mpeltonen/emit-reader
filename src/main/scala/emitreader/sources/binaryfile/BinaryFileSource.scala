package emitreader.sources.binaryfile

import java.nio.file.{Files, Paths}

import akka.actor.ActorRef
import akka.util.ByteString
import emitreader.domain.EmitDataSource

class BinaryFileSource(flowSourceActor: ActorRef, viewModel: BinaryFileSourceViewModel) extends EmitDataSource {
  viewModel.sendDataClickCount.onChange {
    val bytes = Files.readAllBytes(Paths.get(viewModel.fileName()))
    flowSourceActor ! ByteString.fromArray(bytes)
  }
}
