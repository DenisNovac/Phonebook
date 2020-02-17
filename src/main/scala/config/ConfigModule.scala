package config
import org.log4s._
import pureconfig._
import pureconfig.generic.auto._



trait ConfigModule {
  private val logger = getLogger("ConfigModule")
  lazy val config: Config = ConfigSource.file("application.conf").loadOrThrow[Config]

  def logConfig(): Unit = {
    logger.info(s"Config file were found: $config")
  }

}
