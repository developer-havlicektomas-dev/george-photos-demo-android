package dev.havlicektomas.photosapp.core.presentation

import dev.havlicektomas.photosapp.R
import dev.havlicektomas.photosapp.core.domain.util.DataError

fun DataError.toUiText(): UiText = when (this) {
    DataError.Network.NO_INTERNET -> UiText.StringResource(R.string.error_no_internet)
    DataError.Network.REQUEST_TIMEOUT -> UiText.StringResource(R.string.error_request_timeout)
    DataError.Network.TOO_MANY_REQUESTS -> UiText.StringResource(R.string.error_too_many_requests)
    DataError.Network.SERVER_ERROR -> UiText.StringResource(R.string.error_server)
    DataError.Network.SERVICE_UNAVAILABLE -> UiText.StringResource(R.string.error_service_unavailable)
    DataError.Network.SERIALIZATION -> UiText.StringResource(R.string.error_serialization)
    DataError.Network.UNAUTHORIZED -> UiText.StringResource(R.string.error_unauthorized)
    DataError.Network.FORBIDDEN -> UiText.StringResource(R.string.error_forbidden)
    DataError.Network.NOT_FOUND -> UiText.StringResource(R.string.error_not_found)
    DataError.Network.PAYLOAD_TOO_LARGE -> UiText.StringResource(R.string.error_payload_too_large)
    DataError.Network.CONFLICT -> UiText.StringResource(R.string.error_conflict)
    DataError.Network.BAD_REQUEST -> UiText.StringResource(R.string.error_bad_request)
    DataError.Network.UNKNOWN -> UiText.StringResource(R.string.error_unknown)
}
