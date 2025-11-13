package com.datafact.api

import com.datafact.models.{PipelineConfig, PipelineRunResult}

import scala.collection.concurrent.TrieMap

class PipelineStorage {
  private val pipelines = TrieMap[String, PipelineConfig]()
  private val lastResults = TrieMap[String, PipelineRunResult]()

  def savePipeline(config: PipelineConfig): Unit = {
    pipelines.put(config.id, config)
  }

  def getPipeline(id: String): Option[PipelineConfig] = {
    pipelines.get(id)
  }

  def saveLastResult(result: PipelineRunResult): Unit = {
    lastResults.put(result.pipelineId, result)
  }

  def getLastResult(pipelineId: String): Option[PipelineRunResult] = {
    lastResults.get(pipelineId)
  }

  def getAllPipelines(): List[PipelineConfig] = {
    pipelines.values.toList
  }
}
