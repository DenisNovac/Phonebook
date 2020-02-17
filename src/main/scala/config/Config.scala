package config

/** Шаблон для конфигураций */
case class Config (
  persistent: Boolean,
  appHost: String ,
  appPort: String,
  dbHost: String,
  dbPort: String,
  db: String,
  dbUser: String,
  dbPassword: String) {

  override def toString: String = persistent match {
    case true => s"""
                    |Persistent storage: $persistent
                    |App bind: $appHost:$appPort
                    |DB bind: $dbHost:$dbPort:$db
                    |DB creds: $dbUser:***
                    |""".stripMargin

    case false => s"""
                     |Persistent storage: $persistent
                     |App bind: $appHost:$appPort
                     |""".stripMargin
  }

}
