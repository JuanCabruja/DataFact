package com.datafact.api

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.datafact.models.{PipelineConfig, PipelineRunResult}
import com.datafact.pipeline.PipelineRunner
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._

import scala.concurrent.ExecutionContext

class ApiRoutes(storage: PipelineStorage, runner: PipelineRunner)(implicit ec: ExecutionContext) {

  val routes: Route = pathPrefix("pipelines") {
    concat(
      // POST /pipelines - Save pipeline configuration
      pathEnd {
        post {
          entity(as[PipelineConfig]) { config =>
            storage.savePipeline(config)
            complete(StatusCodes.Created, config)
          }
        }
      },
      // POST /pipelines/{id}/run - Run pipeline
      path(Segment / "run") { id =>
        post {
          storage.getPipeline(id) match {
            case Some(config) =>
              onSuccess(runner.runPipeline(config)) { result =>
                storage.saveLastResult(result)
                complete(StatusCodes.OK, result)
              }
            case None =>
              complete(StatusCodes.NotFound, Map("error" -> s"Pipeline with id '$id' not found"))
          }
        }
      },
      // GET /pipelines/{id}/last-report - Get last execution report
      path(Segment / "last-report") { id =>
        get {
          storage.getLastResult(id) match {
            case Some(result) =>
              complete(StatusCodes.OK, result)
            case None =>
              complete(StatusCodes.NotFound, Map("error" -> s"No execution report found for pipeline '$id'"))
          }
        }
      }
    )
  }
}
