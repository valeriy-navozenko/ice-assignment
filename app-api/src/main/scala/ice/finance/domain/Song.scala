package ice.finance.domain

final case class Song private (id: Long, title: String, author: String, progress: BigDecimal)

object Song {

  def of(id: Long, title: String, author: String, progress: BigDecimal): Either[String, Song] = {
    val trimmedTitle  = title.trim
    val trimmedAuthor = author.trim
    if (id <= 0) Left(s"song id must be positive, got $id")
    else if (trimmedTitle.isEmpty) Left(s"song $id has a blank title")
    else if (trimmedAuthor.isEmpty) Left(s"song $id has a blank author")
    else if (progress < 0 || progress > 1) Left(s"song $id progress must be in [0, 1], got $progress")
    else Right(new Song(id, trimmedTitle, trimmedAuthor, progress))
  }
}
