package emitreader.targets.binaryfile

import java.io.FileOutputStream

import akka.stream.scaladsl.Sink
import akka.util.ByteString
import emitreader.domain.EmitDataTarget

class BinaryFileTarget(localViewModel: BinaryFileTargetViewModel) extends EmitDataTarget[ByteString] {

  override def getEmitDataSink(): Sink[ByteString, _] = Sink.foreach(frame => {
    val fileName =s"/tmp/emit-${System.currentTimeMillis()}.bin"
    val outFile = new FileOutputStream(fileName)
    outFile.write(frame.toArray)
    outFile.close()
    Console.println(s"Wrote Emit data to file ${fileName}")
  })
}
