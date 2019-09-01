package controllers

import javax.inject._

import play.api.libs.json._
import play.api.mvc._
import play.api.mvc.WebSocket.MessageFlowTransformer

import scala.concurrent.{ ExecutionContext, Future }

import lila.ws._
import lila.ws.ipc.{ ClientIn, ClientOut }

@Singleton
class SocketController @Inject() (val controllerComponents: ControllerComponents)(implicit
    ec: ExecutionContext,
    siteServer: SiteServer
) extends BaseController {

  import ClientIn.jsonWrite
  import ClientOut.jsonRead
  private implicit val clientTransformer: MessageFlowTransformer[ClientOut, ClientIn] =
    MessageFlowTransformer.jsonMessageFlowTransformer[ClientOut, ClientIn]

  def site(sri: String): WebSocket =
    WebSocket.acceptOrResult[ClientOut, ClientIn] { req =>
      val flag = req.target getQueryParameter "flag" map Flag.apply
      siteServer.connect(req, Sri(sri), flag) map Right.apply
    }
}
