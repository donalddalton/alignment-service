package lib.biojava

import org.biojava.nbio.alignment.Alignments.PairwiseSequenceAlignerType
import org.biojava.nbio.alignment.{Alignments, SimpleGapPenalty}
import org.biojava.nbio.core.alignment.matrices.SubstitutionMatrixHelper
import org.biojava.nbio.core.alignment.template.Profile.StringFormat.CLUSTALW
import org.biojava.nbio.core.alignment.template.{Profile, SequencePair}
import org.biojava.nbio.core.sequence.DNASequence
import org.biojava.nbio.core.sequence.compound.{AmbiguityDNACompoundSet, NucleotideCompound}
import scala.util.Try

object BioJava {

  /**
    *
    * @param targetMatchId
    * @param result
    */
  case class AlignmentResult(
    targetMatchId: String,
    result: String
  )

  /**
    *
    * @param query
    * @param target
    * @param targetId
    * @param gapPenalty
    * @param extensionPenalty
    * @param outputFormat
    * @return
    */
  def pairwiseAlignment(
    query: String,
    target: String,
    targetId: String,
    gapPenalty: Int = 10,
    extensionPenalty: Int = 1,
    outputFormat: Profile.StringFormat = CLUSTALW
  ): Option[AlignmentResult] = {
    Try {
      val t = new DNASequence(target, AmbiguityDNACompoundSet.getDNACompoundSet)
      val q = new DNASequence(query, AmbiguityDNACompoundSet.getDNACompoundSet)
      val matrix = SubstitutionMatrixHelper.getNuc4_4
      val gap = new SimpleGapPenalty()
      gap.setOpenPenalty(gapPenalty)
      gap.setExtensionPenalty(extensionPenalty)
      val psa: SequencePair[DNASequence, NucleotideCompound] =
        Alignments.getPairwiseAlignment(q, t, PairwiseSequenceAlignerType.GLOBAL, gap, matrix)
      // only return a match if every compound in the query matches a compound in the target
      val result = if (psa.getNumIdenticals == query.length) {
        val resultString = s"Query length: ${q.getLength}\n" +
          s"Target length: ${t.getLength}\n" +
          s"Match length: ${psa.getLength}\n" +
          s"Identicals/Query length: ${psa.getNumIdenticals}/${q.getLength}\n" +
          s"%Identity: ${psa.getPercentageOfIdentity(true) * 100}\n" +
          s"${psa.toString(outputFormat)}"
        Some(
          AlignmentResult(
            targetId,
            resultString
          )
        )
      } else {
        None
      }

      result
    }.toOption.flatten
  }
}
