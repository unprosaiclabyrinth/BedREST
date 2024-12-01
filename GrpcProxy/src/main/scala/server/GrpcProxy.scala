package server

import helper.CreateLogger
import helper.ConfigParser.*
import io.grpc.ServerBuilder
import llmquery.LambdaLlmQueryGrpc

import scala.concurrent.ExecutionContext.Implicits.global

object GrpcProxy:
  private val logger = CreateLogger(classOf[GrpcProxy.type])

  @main def runProxy(): Unit =
    val server = ServerBuilder.forPort(getGrpcPort)
      .addService(LambdaLlmQueryGrpc.bindService(new GrpcServiceImpl, global))
      .build

    logger.info(s"gRPC server starting on port $getGrpcPort...")
    try {
      server.start()
      server.awaitTermination()
    } catch
      case e => /* terminate gracefully */