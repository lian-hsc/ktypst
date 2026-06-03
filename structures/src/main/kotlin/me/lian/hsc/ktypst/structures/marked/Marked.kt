package me.lian.hsc.ktypst.structures.marked

import lian.hsc.ktypst.stdlib.layout.Length
import lian.hsc.ktypst.stdlib.visualize.Point
import me.lian.hsc.ktypst.backend.TypstBackend
import me.lian.hsc.ktypst.data.command.Input
import me.lian.hsc.ktypst.data.output.Status
import me.lian.hsc.ktypst.dsl.typstQuery
import me.lian.hsc.ktypst.structures.layout.Box
import me.lian.hsc.ktypst.structures.layout.LayoutPart
import me.lian.hsc.ktypst.structures.layout.buildLayoutPart
import tools.jackson.databind.json.JsonMapper
import tools.jackson.databind.node.ArrayNode

private val jsonMapper = JsonMapper()

suspend fun marked(document: String, backend: TypstBackend): LayoutPart = buildLayoutPart {
    unit = Length.Unit.Points

    val content = $$$"""
        #let mark(name, shift: 1em) = context [
          #let here = here().position()
          #metadata((
            name: name,
            x: (here.x).to-absolute(),
            y: (here.y - shift).to-absolute(),
          )) <marker>
        ]


        $$$document

        #place(top + left, mark("$$start-of-document$$", shift: 0em))
        #place(bottom + right, mark("$$end-of-document$$", shift: 0em))
    """.trimIndent()

    setContent(content)

    val queryResult = typstQuery(backend) {
        input = Input.Content(
            """
            #set page(width: auto, height: auto, margin: 0em)

            $content
        """.trimIndent()
        )
        selector = "<marker>"
        field = "value"
    }

    check(queryResult.status != Status.Success) {
        "Query failed with status ${queryResult.status}: ${queryResult.error}"
    }

    println(queryResult.result!!)

    val json = jsonMapper.readTree(queryResult.result!!) as? ArrayNode
        ?: error("Could not parse JSON")

    for (node in json) {
        val name = node["name"].asString()
        val x = node["x"].asString().removeSuffix("pt").toDouble()
        val y = node["y"].asString().removeSuffix("pt").toDouble()

        +Box(name = name, center = Point(x, y), width = 0.0, height = 0.0)
    }
}
