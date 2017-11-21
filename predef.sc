interp.load.ivy(
  "com.lihaoyi" %
  s"ammonite-shell_${scala.util.Properties.versionNumberString}" %
  ammonite.Constants.version
)

val versions = Map(
  "scala" -> "2.12"
    , "scalaMinor" -> ".3"
    , "scalaParser" -> "1.0.4"
    , "scalaTest" -> "3.0.1"
    , "scalaXml" -> "1.0.6"
    , "spark" -> "1.6.2"
    , "sprayJSON" -> "1.3.2"
    , "asm" -> "4.0"
    , "pegdown" -> "1.6.0"
    , "deeplearning4j" -> "0.4-rc3.10"
    , "jackson" -> "2.7.4"
    , "nd4j" -> "0.4-rc3.10"
    , "aws" -> "1.11.198"
    , "azure" -> "4.3.0"
    , "akka" -> "2.5.6"
    , "akkaHttp" -> "10.0.10"
    , "slf4j" -> "1.7.2"
    , "logback" -> "1.0.9"
    , "ant" -> "1.8.4"
    , "jna" -> "4.3.0"
    , "commons_io" -> "2.5"
    , "jwt" -> "0.4.5"
    , "metrohash" -> "1.0.0"
    , "pojava" -> "3.0.0"
    , "univocity" -> "2.5.4"
    , "tika" -> "1.16"
    , "bouncycastle_pg" -> "1.57"
    , "bouncycastle_pkix" -> "1.57"
    , "bouncycastle_tls" -> "1.57"
    , "bouncycastle_prov" -> "1.57"
    , "re2j" -> "1.1"
    , "spire" -> "0.14.1"
)

interp.load.ivy("com.typesafe.akka" % s"""akka-actor_${versions("scala")}""" % versions("akka"))
interp.load.ivy("com.typesafe.akka" % s"""akka-cluster_${versions("scala")}""" % versions("akka"))
interp.load.ivy("com.typesafe.akka" % s"""akka-cluster-metrics_${versions("scala")}""" % versions("akka"))
interp.load.ivy("com.typesafe.akka" % s"""akka-slf4j_${versions("scala")}""" % versions("akka"))
interp.load.ivy("com.typesafe.akka" % s"""akka-stream_${versions("scala")}""" % versions("akka"))
interp.load.ivy("com.typesafe.akka" % s"""akka-http_${versions("scala")}""" % versions("akkaHttp"))

interp.load.ivy("io.spray" % s"""spray-json_${versions("scala")}""" % versions("sprayJSON"))

interp.load.ivy("com.amazonaws" % "aws-java-sdk-s3" % versions("aws"))
interp.load.ivy("com.amazonaws" % "aws-java-sdk-sts" % versions("aws"))
interp.load.ivy("com.amazonaws" % "aws-java-sdk-autoscaling" % versions("aws"))

@
val shellSession = ammonite.shell.ShellSession()
import shellSession._
import ammonite.ops._
import ammonite.shell._
import ammonite.interp._

ammonite.shell.Configure(interp, repl, wd)

val csJarPath = scala.sys.env.get("CS_DEVELOPMENT") match {
  case None => cwd
  case Some(path) => ammonite.ops.Path(path)
}

val jars = ls.rec! csJarPath |? (_.ext == "jar")
interp.load.cp(jars)

import akka.Done
import akka.util.{ByteString,Timeout}
import akka.util.ByteString
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer,IOResult,ThrottleMode}
import akka.stream.scaladsl.{Broadcast,Concat,Flow,Keep,Source,Sink,FileIO}
import akka.http.scaladsl.model.Uri
import scala.collection.immutable.{TreeMap,TreeSet}
import scala.concurrent.{Await,Future,Promise}
import scala.concurrent.duration.{Duration,FiniteDuration}
import scala.util.{Failure,Success,Try}

implicit lazy val system = ActorSystem("test")
implicit lazy val materializer = ActorMaterializer()
import materializer.executionContext
implicit lazy val timeout = akka.util.Timeout(30, java.util.concurrent.TimeUnit.SECONDS)
implicit val dur = FiniteDuration(30, "seconds")
