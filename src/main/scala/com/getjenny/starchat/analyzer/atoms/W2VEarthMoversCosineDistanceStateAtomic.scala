package com.getjenny.starchat.analyzer.atoms

import com.getjenny.analyzer.util.VectorUtils._
import com.getjenny.starchat.analyzer.utils.TextToVectorsTools._
import com.getjenny.starchat.analyzer.utils.TextToVectorsTools

import com.getjenny.analyzer.analyzers._
import com.getjenny.starchat.entities.TextTerms
import com.getjenny.analyzer.atoms.AbstractAtomic
import com.getjenny.starchat.analyzer.utils.EmDistance

import scala.concurrent.{Await, ExecutionContext, Future}
import com.getjenny.starchat.services._

import ExecutionContext.Implicits.global
import com.getjenny.analyzer.expressions.{AnalyzersData, Result}

/**
  * Created by angelo on 11/04/17.
  */

class W2VEarthMoversCosineDistanceStateAtomic(val arguments: List[String], restricted_args: Map[String, String])
  extends AbstractAtomic  {
  /**
    * cosine distance between sentences renormalized at [0, 1]: (cosine + 1)/2
    *
    * state_lost_password_cosine = Cosine("lost password")
    * state_lost_password_cosine.evaluate("I'm desperate, I've lost my password")
    *
    */

  val state: String = arguments.head
  val termService: TermService.type = TermService

  implicit class Crosstable[X](xs: Traversable[X]) {
    def cross[Y](ys: Traversable[Y]) = for { x <- xs; y <- ys } yield (x, y)
  }

  override def toString: String = "similarCosEmdState(\"" + state + "\")"

  val analyzerService: AnalyzerService.type = AnalyzerService

  val index_name = restricted_args("index_name")

  val queries_sentences: Option[DecisionTableRuntimeItem] =
    AnalyzerService.analyzers_map(index_name).analyzer_map.get(state)
  if (queries_sentences.isEmpty) {
    val message = toString + " : state not found on states map"
    analyzerService.log.error(message)
    throw AnalyzerInitializationException(message)
  } else {
    analyzerService.log.info(toString + " : initialized")
  }

  val queries_vectors: List[Option[TextTerms]] = queries_sentences.get.queries.map(item => Option{item})

  val isEvaluateNormalized: Boolean = true
  def evaluate(query: String, data: AnalyzersData = AnalyzersData()): Result = {
    val query_vectors = termService.textToVectors(index_name = index_name, text = query)
    val emd_dist_queries = queries_vectors.map(q => {
      val dist = EmDistance.distanceCosine(q , query_vectors)
      dist
    })

    val emd_dist = if (emd_dist_queries.nonEmpty) emd_dist_queries.max else 0.0
    Result(score=emd_dist)
  }

  // Similarity is normally the cosine itself. The threshold should be at least

  // angle < pi/2 (cosine > 0), but for synonyms let's put cosine > 0.6, i.e. self.evaluate > 0.8
  override val match_threshold: Double = 0.8
}
