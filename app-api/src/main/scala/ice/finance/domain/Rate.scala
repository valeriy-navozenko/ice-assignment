package ice.finance.domain

/** Commission rate for a half-open `[fromInclusive, toExclusive)` amount band. */
final case class Rate(fromInclusive: BigDecimal, toExclusive: BigDecimal, fraction: BigDecimal) {
  def contains(amount: BigDecimal): Boolean =
    amount >= fromInclusive && amount < toExclusive
}
