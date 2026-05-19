package ice.finance.api.validator

import ice.finance.domain._
import weaver.FunSuite

object CommissionValidatorSpec extends FunSuite {

  test("rejects an empty services array") {
    val request = CommissionRequest("client-001", List.empty)
    CommissionValidator.validate(request) match {
      case Left(_: AppError.InvalidRequest) => success
      case other                            => failure(s"expected InvalidRequest, got $other")
    }
  }

  test("rejects non-positive service ids") {
    val request = CommissionRequest("client-001", List(ServiceItem(0L, BigDecimal(100))))
    CommissionValidator.validate(request) match {
      case Left(_: AppError.InvalidRequest) => success
      case other                            => failure(s"expected InvalidRequest, got $other")
    }
  }

  test("rejects a blank clientId") {
    val request = CommissionRequest("   ", List(ServiceItem(1L, BigDecimal(100))))
    CommissionValidator.validate(request) match {
      case Left(_: AppError.InvalidRequest) => success
      case other                            => failure(s"expected InvalidRequest, got $other")
    }
  }

  test("rejects duplicate service ids") {
    val request = CommissionRequest(
      "client-001",
      List(ServiceItem(1L, BigDecimal(100)), ServiceItem(1L, BigDecimal(50)))
    )
    CommissionValidator.validate(request) match {
      case Left(_: AppError.InvalidRequest) => success
      case other                            => failure(s"expected InvalidRequest, got $other")
    }
  }

  test("rejects an amount above 1,000,000 with AmountOutOfRange (no value echoed)") {
    val request = CommissionRequest("client-001", List(ServiceItem(1L, BigDecimal("1000001"))))
    CommissionValidator.validate(request) match {
      case Left(error: AppError.AmountOutOfRange) =>
        expect.all(error.serviceId == 1L, !error.message.contains("1000001"))
      case other => failure(s"expected AmountOutOfRange, got $other")
    }
  }
}
