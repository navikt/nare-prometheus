package no.nare

import io.prometheus.client.CollectorRegistry
import no.nav.evaluationLabel
import no.nav.idLabel
import no.nav.nare.core.evaluations.Evaluering.Companion.ja
import no.nav.nare.core.evaluations.Evaluering.Companion.kanskje
import no.nav.nare.core.evaluations.Evaluering.Companion.nei
import no.nav.nare.core.evaluations.Resultat
import no.nav.nare.core.specifications.Spesifikasjon
import no.nav.prometheus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MetricsTest {

    val registry: CollectorRegistry = CollectorRegistry()

    @Test
    fun `en evaluering skal kunne rapportere sine resultat`() {
        val e = prometheus(registry) { høyereEnnTi.evaluer(12) }

        assertThat(e.resultat).isEqualTo(Resultat.JA)

        val sampleValue: Double = registry.getSampleValue("nare_result",
                arrayOf(idLabel, evaluationLabel),
                arrayOf(høyereEnnTi.identitet, Resultat.JA.name))

        assertThat(sampleValue).isEqualTo(1.0)
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