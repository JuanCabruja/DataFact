package com.datafact.pipeline

import com.datafact.models.{PipelineConfig, PipelineRunResult}

import scala.concurrent.Future

trait PipelineRunner {
  def runPipeline(config: PipelineConfig): Future[PipelineRunResult]
}

class SimulatedPipelineRunner extends PipelineRunner {
  override def runPipeline(config: PipelineConfig): Future[PipelineRunResult] = {
    val startTime = System.currentTimeMillis()
    
    // Simulate pipeline execution
    Thread.sleep(100) // Simulate some processing time
    
    val endTime = System.currentTimeMillis()
    val result = PipelineRunResult(
      pipelineId = config.id,
      status = "SUCCESS",
      startTime = startTime,
      endTime = endTime,
      recordsProcessed = scala.util.Random.nextInt(10000) + 1000,
      errors = List.empty,
      message = s"Pipeline '${config.name}' executed successfully"
    )
    
    Future.successful(result)
  }
}
