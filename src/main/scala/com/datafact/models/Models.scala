package com.datafact.models

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto._

case class SourceConfig(
  sourceType: String,
  path: String,
  format: String,
  options: Map[String, String] = Map.empty
)

object SourceConfig {
  implicit val decoder: Decoder[SourceConfig] = deriveDecoder[SourceConfig]
  implicit val encoder: Encoder[SourceConfig] = deriveEncoder[SourceConfig]
}

case class ValidationConfig(
  rules: List[String],
  failOnError: Boolean = true
)

object ValidationConfig {
  implicit val decoder: Decoder[ValidationConfig] = deriveDecoder[ValidationConfig]
  implicit val encoder: Encoder[ValidationConfig] = deriveEncoder[ValidationConfig]
}

case class TransformConfig(
  transformationType: String,
  columns: List[String],
  expression: Option[String] = None,
  parameters: Map[String, String] = Map.empty
)

object TransformConfig {
  implicit val decoder: Decoder[TransformConfig] = deriveDecoder[TransformConfig]
  implicit val encoder: Encoder[TransformConfig] = deriveEncoder[TransformConfig]
}

case class OutputConfig(
  outputType: String,
  path: String,
  format: String,
  mode: String = "append",
  options: Map[String, String] = Map.empty
)

object OutputConfig {
  implicit val decoder: Decoder[OutputConfig] = deriveDecoder[OutputConfig]
  implicit val encoder: Encoder[OutputConfig] = deriveEncoder[OutputConfig]
}

case class PipelineConfig(
  id: String,
  name: String,
  source: SourceConfig,
  validations: List[ValidationConfig],
  transformations: List[TransformConfig],
  output: OutputConfig
)

object PipelineConfig {
  implicit val decoder: Decoder[PipelineConfig] = deriveDecoder[PipelineConfig]
  implicit val encoder: Encoder[PipelineConfig] = deriveEncoder[PipelineConfig]
}

case class PipelineRunResult(
  pipelineId: String,
  status: String,
  startTime: Long,
  endTime: Long,
  recordsProcessed: Long,
  errors: List[String] = List.empty,
  message: String
)

object PipelineRunResult {
  implicit val decoder: Decoder[PipelineRunResult] = deriveDecoder[PipelineRunResult]
  implicit val encoder: Encoder[PipelineRunResult] = deriveEncoder[PipelineRunResult]
}
