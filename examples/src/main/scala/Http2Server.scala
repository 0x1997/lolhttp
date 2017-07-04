import lol.http._
import lol.html._

import scala.concurrent._
import ExecutionContext.Implicits.global

object Http2Server {
  def main(args: Array[String]): Unit = {
    val ssl = SSL.serverCertificate(
      certificatePath = "src/main/resources/server.crt",
      privateKeyPath = "src/main/resources/server-pkcs8.key",
      privateKeyPassword = "lol"
    )
    Server.listen(8443, ssl = Some(ssl), options = ServerOptions(protocols = Set(HTTP, HTTP2), debug = None)) {
      case req @ url"/" =>
        Ok(
          html"""
            <h1>Hello ${req.protocol}</h1>
            <form action="/upload" method="POST" enctype="multipart/form-data">
              <input type="file" name="lol" />
              <input type="submit" />
            </form>
          """
        )
      case req @ url"/upload" =>
        req.readAs[Array[Byte]].map { bytes =>
          println(s"READ ${bytes.size} BYTES")
          Ok(s"${bytes.size} bytes")
        }
      case _ =>
        NotFound
    }

    println("Listening on https://localhost:8443...")
  }
}
