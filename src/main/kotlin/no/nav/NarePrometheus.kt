package no.nav

import io.prometheus.client.CollectorRegistry
import io.prometheus.client.Counter
import no.nav.nare.core.evaluations.Evaluering

class NarePrometheus(registry: CollectorRegistry) {
    val idLabel = "identifikator"
    val evaluationLabel = "evaluation"

    private val telleverk: Counter = Counter
            .build("nare_result", "for storing the results of nare evaluations")
            .labelNames(idLabel, evaluationLabel)
            .register(registry)

    fun tellEvaluering(function: () -> Evaluering): Evaluering {
        val evaluering: Evaluering = function()
        evaluering.tell()
        return evaluering
    }

    private fun Evaluering.tell() {
        telleverk
                .labels(this.identifikator, this.resultat.name)
                .inc()
        this.children.forEach { it.tell() }
    }
}


