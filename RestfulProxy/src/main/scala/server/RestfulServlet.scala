package server

import org.scalatra._
import client.GrpcClient
import helper.CreateLogger

class RestfulServlet extends ScalatraServlet:
  private val logger = CreateLogger(classOf[RestfulServlet])

  post("/query") {
    val prompt = params("prompt")
    val maxTokens = params("maxTokens")
    logger.info(s"Received POST request with prompt=\"$prompt\", maxTokens=\"$maxTokens\"")
    val generation = GrpcClient.grpcForGeneration(prompt, maxTokens.toInt)
    logger.info(s"Got generation: $generation")
    generation
  }

