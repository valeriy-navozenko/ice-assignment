package ice.finance.api

import ice.finance.domain.Pagination
import org.http4s.dsl.impl.OptionalQueryParamDecoderMatcher

/** Reusable `?page=&pageSize=` query-string handling for any paginated route. */
object PageQuery {

  private val PageQueryParamName: String     = "page"
  private val PageSizeQueryParamName: String = "pageSize"

  object Page     extends OptionalQueryParamDecoderMatcher[Int](PageQueryParamName)
  object PageSize extends OptionalQueryParamDecoderMatcher[Int](PageSizeQueryParamName)

  def page(maybePage: Option[Int]): Int =
    maybePage.filter(_ > 0).getOrElse(Pagination.MinPage)

  def pageSize(maybePageSize: Option[Int]): Int =
    maybePageSize.filter(_ > 0).getOrElse(Pagination.Default)
}
