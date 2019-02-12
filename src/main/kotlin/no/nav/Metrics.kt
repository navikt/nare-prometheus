package no.nav

import io.prometheus.client.CollectorRegistry
import io.prometheus.client.Counter
import no.nav.nare.core.evaluations.Evaluering


class Metrics(val registry: CollectorRegistry) {
    val idLabel = "identifikator"
    val evaluationLabel = "evaluation"

    val counter: Counter = Counter
            .build("nare_result", "for storing the results of nare evaluations")
            .labelNames(idLabel, evaluationLabel)
            .register(registry)

    fun prometheus(function: () -> Evaluering): Evaluering {
        val evaluering: Evaluering = function()
        counter
                .labels(evaluering.identifikator, evaluering.resultat.name)
                .inc()
        return evaluering
    }
}


