package no.nare

import io.prometheus.client.CollectorRegistry
import no.nav.Metrics
import no.nav.nare.core.evaluations.Evaluering.Companion.ja
import no.nav.nare.core.evaluations.Evaluering.Companion.kanskje
import no.nav.nare.core.evaluations.Evaluering.Companion.nei
import no.nav.nare.core.evaluations.Resultat
import no.nav.nare.core.specifications.Spesifikasjon
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MetricsTest {

    val registry: CollectorRegistry = CollectorRegistry()

    @Test
    fun `en evaluering skal kunne rapportere sine resultat`() {
        val metrics = Metrics(registry)
        val e = metrics.prometheus { høyereEnnTi.evaluer(12) }

        assertThat(e.resultat).isEqualTo(Resultat.JA)

        val sampleValue: Double = _sample(registry, høyereEnnTi.identitet, Resultat.JA.name)

        assertThat(sampleValue).isEqualTo(1.0)
    }

    @Test
    fun `det skal være mulig å rapportere resultater flere ganger`() {
        val metrics = Metrics(registry)
        val e = metrics.prometheus { høyereEnnTi.evaluer(12) }
        val f = metrics.prometheus { høyereEnnTi.evaluer(8) }
    }

    @Test
    fun `alle noder i en evaluering skal telles`() {
        val metrics = Metrics(registry)
        val e = metrics.prometheus { partallOverTi.evaluer(11) }

        assertThat(e.resultat).isEqualTo(Resultat.NEI)

        val partallOverTiEvaluering: Double = _sample(registry, partallOverTi.identitet, Resultat.NEI.name)
        assertThat(partallOverTiEvaluering).isEqualTo(1.0)

        val partallEvaluering: Double = _sample(registry, partall.identitet, Resultat.NEI.name)
        assertThat(partallEvaluering).isEqualTo(1.0)

        val høyereEnnTiEvaluering: Double = _sample(registry, høyereEnnTi.identitet, Resultat.JA.name)
        assertThat(høyereEnnTiEvaluering).isEqualTo(1.0)
    }

    @Test
    fun `sample-value skal øke når samme node evalueres til det samme flere ganger`() {
        val metrics = Metrics(registry)
        metrics.prometheus { partallOverTi.evaluer(12) }
        metrics.prometheus { partallOverTi.evaluer(8) }
        val partallEvaluering: Double = _sample(registry, partall.identitet, Resultat.JA.name)
        assertThat(partallEvaluering).isEqualTo(2.0)
    }

    fun _sample(registry: CollectorRegistry, id: String, resultat: String): Double {
        return registry.getSampleValue("nare_result",
                arrayOf("identifikator", "evaluation"),
                arrayOf(id, resultat))
    }

}

val høyereEnnTi = Spesifikasjon<Int>(
        beskrivelse = "Er tallet høyere enn 10?",
        identitet = "§§ 8-30 8-29"
) { tall ->
    when {
        tall > 10 -> ja("tallet er høyere enn 10")
        tall < 10 -> nei("tallet er lavere enn 10")
        else -> kanskje("logikken her tar ikke høyde for likhet")
    }
}
val partall = Spesifikasjon<Int>(
        beskrivelse = "Er tallet et partall?",
        identitet = "§ 8-2 første ledd"
) { tall ->
    when {
        tall % 2 == 0 -> ja("Tallet er et partall")
        tall < 0 -> kanskje("logikken tar ikke høyde for negative tall")
        else -> nei("Tallet er et oddetall")
    }
}
val partallOverTi = høyereEnnTi og partall