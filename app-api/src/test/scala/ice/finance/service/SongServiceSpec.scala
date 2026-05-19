package ice.finance.service

import cats.effect.IO
import ice.finance.domain.Pagination
import ice.finance.domain.Song
import weaver.SimpleIOSuite

object SongServiceSpec extends SimpleIOSuite {

  private val sample: List[Song] = List(
    Song.of(1, "A", "X", BigDecimal("0.1")).toOption.get,
    Song.of(2, "B", "Y", BigDecimal("0.2")).toOption.get,
    Song.of(3, "C", "Z", BigDecimal("0.3")).toOption.get
  )

  private val service = SongService.fromList[IO](sample)

  test("findPage returns the page with full metadata") {
    service.findPage(1, 25).map { result =>
      expect.all(
        result.items.map(_.id) == List(1, 2, 3),
        result.page == 1,
        result.pageSize == 25,
        result.total == 3,
        result.totalPages == 1
      )
    }
  }

  test("findPage clamps a pageSize larger than the maximum") {
    service.findPage(1, 10_000).map(result => expect(result.pageSize == Pagination.Max))
  }

  test("findPage clamps an out-of-range page to the last available page") {
    service.findPage(99, 25).map(result => expect(result.page == 1))
  }

  test("loadFromResource parses the bundled songs.json into 100 validated songs") {
    SongService.loadFromResource[IO]().flatMap(_.total).map(total => expect(total == 100))
  }
}
