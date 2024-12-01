package server

import helper.ConfigParser.*
import helper.CreateLogger
import org.eclipse.jetty.ee10.webapp.WebAppContext
import org.eclipse.jetty.server.Server

object WebApp:
  private val logger = CreateLogger(classOf[WebApp.type])

  private def buildWebServer: Server =
    val server = new Server(getThisServerPort)
    logger.info(s"Created webapp server listening on port $getThisServerPort.")
    val context = new WebAppContext
    context.setContextPath("/")
    context.setBaseResourceAsString(getBaseResourcePathAsString)
    context.addServlet(new RestfulServlet, "/*")

    server.setHandler(context)
    server

  @main def runWebServer(): Unit =
    val server = buildWebServer
    server.start()
    try
      server.join()
    catch
      case e => /* terminate gracefully */