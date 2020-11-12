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

  /** Alignment result */
  case class AlignmentResult(
    targetMatchId: String,
    result: String
  )
}

/** BioJava wrapper */
class BioJava {
  import BioJava._

  /**
    * See https://biojava.org/wiki/BioJava%3ACookBook4.0/ for more info.
    *
    * Defaults: https://biojava.org/wiki/BioJava:CookBook3:PSA_DNA
    *
    * @param query            Query string e.g. `ATGC`
    * @param target           Target string e.g `ATCGATG`
    * @param targetId         Target ID e.g. `NC_000852`
    * @param gapPenalty       Gap penalty
    * @param extensionPenalty Extension penalty
    * @param outputFormat     Result format
    * @return
    */
  def pairwiseAlignment(
    query: String,
    target: String,
    targetId: String,
    gapPenalty: Int = 5,
    extensionPenalty: Int = 2,
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
