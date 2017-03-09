// Example: Reverse proxy
//
// This example use both a __Server__ and a __Client__ to create an
// HTTP reverse proxy.
import lol.http._

import scala.concurrent._
import ExecutionContext.Implicits.global

// Let's proxy [github.com](http://github.com) 😼.
object ReverseProxy {
  def main(args: Array[String]): Unit = {

    // We need an HTTP client connected to github. The connection is
    // done in HTTPS, so we need to specify the port and scheme.
    val githubClient = Client("github.com", 443, "https")
    
    // Now we start an HTTP server.
    Server.listen(8888) {

      // If the request is a __GET /__, we redirect the
      // browser to __/explore__. That's the _only_ feature of
      // our reverse proxy!
      case GET at url"/" =>
        Redirect("/explore")

      // In any other case, we pass the request to the client we just
      // created, so it is sent to github, and the response is written
      // back to the browser.
      case request =>
        githubClient {
          // We need to change the `Host` header, so the github server will
          // accept the request.
          request.addHeaders(Headers.Host -> h"github.com")
        }
    }
      
    println("Proxying github on http://localhost:8888...")
  }
}