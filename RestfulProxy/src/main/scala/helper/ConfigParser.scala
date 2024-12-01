package helper

import com.typesafe.config.{Config, ConfigFactory}

object ConfigParser:
  private val config: Config = ConfigFactory.load("proxy.conf")
  
  def getThisServerPort: Int = config.getInt("restfulProxy.this_server_port")

  def getGrpcHost: String = config.getString("restfulProxy.grpc_server_host")
  
  def getGrpcPort: Int = config.getInt("restfulProxy.grpc_server_port")
  
  def getBaseResourcePathAsString: String = config.getString("restfulProxy.base_resource_path_string")
