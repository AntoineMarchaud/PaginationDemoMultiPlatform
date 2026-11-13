import Testing
import Shared
@testable import paginationnative

/// Same wording as the Compose app (composeResources/values/strings.xml).
struct ErrorMessagesTests {

    @Test func everyErrorHasItsMessage() {
        #expect(errorMessage(ErrorApiModel.NoInternetError()) == "no internet connection")
        #expect(errorMessage(ErrorApiModel.SocketTimeOutError()) == "the server took too long to respond")
        #expect(errorMessage(ErrorApiModel.ApiServerErrorWithCode(responseCode: 503, responseMessage: "unavailable")) == "server error (503)")
        #expect(errorMessage(ErrorApiModel.ApiGenericServerError()) == "server error")
        #expect(errorMessage(ErrorApiModel.ApiNullBody()) == "empty response from the server")
        #expect(errorMessage(ErrorApiModel.GenericError()) == "something went wrong")
    }
}
