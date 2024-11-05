package br.udesc.ddm.controlefinanceiro.model

data class AcaoAPI(
    val currency: String,
    val shortName: String,
    val longName: String,
    val regularMarketChange: Float,
    val regularMarketChangePercent: Float,
    val regularMarketTime: String,
    val regularMarketPrice: Float,
    val regularMarketDayHigh: Float,
    val regularMarketDayRange: String,
    val regularMarketDayLow: Float,
    val regularMarketVolume: Float,
    val regularMarketPreviousClose: Float,
    val regularMarketOpen: Float,
    val fiftyTwoWeekRange: String,
    val fiftyTwoWeekLow: Float,
    val fiftyTwoWeekHigh: Float,
    val symbol: String,
    val priceEarnings: Float,
    val earningsPerShare: Float,
    val logourl: String
)

data class RespostaAPI(
    val results: List<AcaoAPI>,
    val requestedAt : String,
    val took: String
)