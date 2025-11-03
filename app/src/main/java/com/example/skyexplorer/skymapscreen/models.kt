package com.example.skyexplorer.skymapscreen

data class Star(
    val id: Int,
    val name: String,
    /** RA/Dec w stopniach (jeżeli masz w godzinach: RA_deg = RA_hours * 15.0) */
    val ra: Double,
    val dec: Double,
    val magnitude: Double,
    // liczone w runtime:
    var alt: Double = Double.NaN,  // wysokość [deg]
    var az: Double = Double.NaN    // azymut [deg], 0=N, 90=E
)

data class Constellation(
    val name: String,
    /** listy par ID gwiazd: [ [id1,id2], [id2,id3], ... ] */
    val lines: List<List<Int>>
)
