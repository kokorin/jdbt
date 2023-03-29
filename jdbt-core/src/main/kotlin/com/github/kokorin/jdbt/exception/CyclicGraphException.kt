package com.github.kokorin.jdbt.exception

class CyclicGraphException(val cycle: List<String>) :
    RuntimeException("Graph cycle: ${cycle.joinToString(" -> ")}")