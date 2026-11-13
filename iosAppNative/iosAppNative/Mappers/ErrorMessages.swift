import Foundation
import Shared

/// Error text shown to the user. Same wording as the Compose app.
func errorMessage(_ error: ErrorApiModel) -> String {
    switch error {
    case let error as ErrorApiModel.ApiServerErrorWithCode:
        String(localized: "server error (\(error.responseCode))")
    case is ErrorApiModel.ApiNullBody:
        String(localized: "empty response from the server")
    case is ErrorApiModel.ApiGenericServerError:
        String(localized: "server error")
    case is ErrorApiModel.SocketTimeOutError:
        String(localized: "the server took too long to respond")
    case is ErrorApiModel.NoInternetError:
        String(localized: "no internet connection")
    default:
        String(localized: "something went wrong")
    }
}
