package helper

import com.typesafe.config.{Config, ConfigFactory}

object ConfigParser:
  private val config: Config = ConfigFactory.load("proxy.conf")

  def getLambdaLlmUrl: String = config.getString("grpc-proxy.lambda_resource_url")
  
  def getGrpcPort: Int = config.getInt("grpc-proxy.grpc_port")