package server

import helper.ConfigParser.*
import helper.CreateLogger
import llmquery.{LambdaLlmQueryGrpc, LambdaLlmRequest, LambdaLlmResponse}
import sttp.client4.*
import sttp.client4.logging.slf4j.Slf4jLoggingBackend
import sttp.client4.upicklejson.default.*
import sttp.model.MediaType
import upickle.default.*

import scala.concurrent.Future

class GrpcServiceImpl extends LambdaLlmQueryGrpc.LambdaLlmQuery:
  private val logger = CreateLogger(classOf[GrpcServiceImpl])
  private val backend = Slf4jLoggingBackend(DefaultSyncBackend())
  
  case class LllmRequestProxy(prompt: String, maxTokens: Int)
  case class LllmResponseProxy(generation: String)

  implicit val llmRequestRw: ReadWriter[LllmRequestProxy] = macroRW[LllmRequestProxy]
  implicit val llmResponseRw: ReadWriter[LllmResponseProxy] = macroRW[LllmResponseProxy]

  override def generateText(grpcRequest: LambdaLlmRequest): Future[LambdaLlmResponse] =
    logger.info(s"Got gRPC request prompt=\"${grpcRequest.prompt}, maxTokens=${grpcRequest.maxTokens}. Sending HTTP to lambda.")
    val httpRequest = basicRequest
      .post(uri"$getLambdaLlmUrl")
      .contentType(MediaType.ApplicationJson)
      .body(LllmRequestProxy(prompt = grpcRequest.prompt, maxTokens = grpcRequest.maxTokens))
      .response(asJson[LllmResponseProxy])
    
    httpRequest.send(backend).body match {
      case Left(e) =>
        logger.error(s"Got HTTP response exception: \n$e")
        Future.successful(LambdaLlmResponse())
      case Right(r) =>
        logger.info(s"Got response from Lambda LLM:: \"${r.generation}\".")
        Future.successful(LambdaLlmResponse(generation = r.generation))
    }
