package lib.src.main.scala

import org.biojava.nbio.alignment.Alignments.PairwiseSequenceAlignerType
import org.biojava.nbio.alignment.{Alignments, SimpleGapPenalty}
import org.biojava.nbio.core.alignment.matrices.SubstitutionMatrixHelper
import org.biojava.nbio.core.alignment.template.Profile.StringFormat.CLUSTALW
import org.biojava.nbio.core.alignment.template.{Profile, SequencePair}
import org.biojava.nbio.core.sequence.DNASequence
import org.biojava.nbio.core.sequence.compound.{AmbiguityDNACompoundSet, NucleotideCompound}
import scala.util.Try

object BioJava {

  case class AlignmentResult(
    targetMatchId: String,
    targetMatchStartIdx: Int,
    targetMatchEndIdx: Int,
    result: String
  )

  def pairwiseAlignment(
    query: String,
    target: String,
    targetId: String,
    gapPenalty: Int = 5,
    extensionPenalty: Int = 2,
    ouputFormat: Profile.StringFormat = CLUSTALW
  ): Option[AlignmentResult] = {
    Try {
      val t = new DNASequence(target, AmbiguityDNACompoundSet.getDNACompoundSet)
      val q = new DNASequence(query, AmbiguityDNACompoundSet.getDNACompoundSet)
      val matrix = SubstitutionMatrixHelper.getNuc4_4
      val gap = new SimpleGapPenalty()
      gap.setOpenPenalty(gapPenalty)
      gap.setExtensionPenalty(extensionPenalty)
      val psa = Alignments.getPairwiseAlignment(q, t, PairwiseSequenceAlignerType.GLOBAL, gap, matrix)

      val queryLength = query.length
      val result = if (psa.getNumIdenticals == queryLength) {
        Some(
          AlignmentResult(
            targetId,
            psa.getIndexInTargetForQueryAt(1), // 1-based indexing
            psa.getIndexInTargetForQueryAt(queryLength),
            psa.toString(ouputFormat)
          )
        )
      } else {
        None
      }

      psa.getQuery.clearCache() // TODO
      psa.getTarget.clearCache()

      result
    }.toOption.flatten

  }
}
