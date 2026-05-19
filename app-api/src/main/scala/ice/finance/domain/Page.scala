package ice.finance.domain

final case class Page[T](items: List[T], page: Int, pageSize: Int, total: Int, totalPages: Int)
