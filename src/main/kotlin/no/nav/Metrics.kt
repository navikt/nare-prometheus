package no.nav

import io.prometheus.client.CollectorRegistry
import io.prometheus.client.Counter
import no.nav.nare.core.evaluations.Evaluering

val idLabel = "identifikator"
val evaluationLabel = "evaluation"
fun prometheus(registry: CollectorRegistry, function: () -> Evaluering): Evaluering {
    val evaluering: Evaluering = function()

    val counter: Counter = Counter
            .build("nare_result", "for storing the results of nare evaluations")
            .labelNames(idLabel, evaluationLabel)
            .register(registry)

    counter
            .labels(evaluering.identifikator, evaluering.resultat.name)
            .inc()
    return evaluering
}
