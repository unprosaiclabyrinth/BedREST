import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import com.typesafe.config.{Config, ConfigFactory}

class ConfigSpec extends AnyFlatSpec with Matchers {
  private val config: Config = ConfigFactory.load("proxy.conf")

  "The root config" should "have the URL of the resource in AWS Lambda in its path" in {
    config.getConfig("grpc-proxy").hasPath("lambda_resource_url") should be (true)
  }

  it should "have the port of this server (self) in its path" in {
    config.getConfig("grpc-proxy").hasPath("grpc_port") should be (true)
  }

  "The port that this server runs on" can "be 50051" in {
    config.getConfig("grpc-proxy").getInt("grpc_port") should be (50051)
  }
}