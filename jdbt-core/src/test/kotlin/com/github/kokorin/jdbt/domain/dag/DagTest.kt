package com.github.kokorin.jdbt.domain.dag

import com.github.kokorin.jdbt.exception.CyclicGraphException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DagTest {
    private fun dag(vararg edges: Pair<String, String>): Dag<String> =
        Dag(
            nodes = edges.flatMap { listOf(it.first, it.second) }.toSet(),
            edges = edges.groupBy({ it.first }, { it.second }).mapValues { it.value.toSet() }
        )

    @Test
    fun nodesPostOrder() {
        assertThat(
            dag(
                "1" to "2",
                "2" to "3",
            ).nodesPostOrder()
        ).containsExactly("3", "2", "1")

        assertThat(
            dag(
                "1" to "2.1",
                "1" to "2.2",
                "2.1" to "3",
                "2.2" to "3",
            ).nodesPostOrder()
        ).containsExactlyInAnyOrder("1", "2.1", "2.2", "3")
            .also {
                it.first().isEqualTo("3")
                it.last().isEqualTo("1")
            }
    }

    @Test
    fun `Dag with cycle can't be created`() {
        assertThat(
            assertThrows<CyclicGraphException> { dag("1" to "1") }.cycle
        ).containsExactly("1", "1")

        val ex = assertThrows<CyclicGraphException> {
            dag(
                "1" to "2",
                "2" to "1",
            )
        }
        assertThat(
            ex.cycle
        ).contains("1", "2")
            .hasSize(3)

        assertThat(ex.cycle.first()).isEqualTo(ex.cycle.last())

        val ex2 = assertThrows<CyclicGraphException> {
            dag(
                "1" to "2",
                "2" to "3",
                "3" to "4",
                "4" to "2",
            )
        }
        assertThat(
            ex2.cycle
        ).contains("2", "3", "4")
            .hasSize(4)

        assertThat(ex2.cycle.first()).isEqualTo(ex2.cycle.last())

    }
}
