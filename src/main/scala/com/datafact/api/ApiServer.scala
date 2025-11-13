package com.datafact.api

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import com.datafact.pipeline.SimulatedPipelineRunner

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object ApiServer {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "datafact-api")
    implicit val executionContext: ExecutionContextExecutor = system.executionContext

    val storage = new PipelineStorage()
    val runner = new SimulatedPipelineRunner()
    val apiRoutes = new ApiRoutes(storage, runner)

    val route: Route = apiRoutes.routes

    val host = "0.0.0.0"
    val port = 8080

    val bindingFuture = Http().newServerAt(host, port).bind(route)

    bindingFuture.onComplete {
      case scala.util.Success(binding) =>
        println(s"Server online at http://${binding.localAddress.getHostString}:${binding.localAddress.getPort}/")
        println("Press RETURN to stop...")
      case scala.util.Failure(ex) =>
        println(s"Failed to bind to $host:$port")
        ex.printStackTrace()
        system.terminate()
    }

    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}
