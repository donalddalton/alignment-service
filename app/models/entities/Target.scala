package models.entities

/**
  * A sequence to search against.
  *
  * @param id e.g `NC_000852`
  * @param sequence e.g. `ATGC`
  */
case class Target(
  id: String,
  sequence: String
)
