# ktypst

Kotlin-first building blocks for working with the Typst CLI. 
The core module models Typst commands and outputs, while optional modules add a command-line backend, reusable drawing primitives, and structure/layout helpers.

## Modules

- **ktypst (core)**: Command models, output types, and DSLs to build compile/query requests. See [docs/README.md](docs/README.md) and [docs/examples/core/compile-and-query.kt](docs/examples/core/compile-and-query.kt).
- **backend:command**: Executes Typst by spawning the `typst` binary on your PATH. See [docs/backend-command/README.md](docs/backend-command/README.md) and [docs/examples/backend-command/command-backend.kt](docs/examples/backend-command/command-backend.kt).
- **stdlib**: Reusable Typst primitives (lengths, colors, paints, CeTZ shapes). See [docs/stdlib/README.md](docs/stdlib/README.md) and [docs/examples/stdlib/visualize.kt](docs/examples/stdlib/visualize.kt).
- **structures**: Graph and tree DSLs plus layout helpers built on top of stdlib. See [docs/structures/README.md](docs/structures/README.md) and [docs/examples/structures/graph-and-tree.kt](docs/examples/structures/graph-and-tree.kt).

## Quick start

```kotlin
import kotlinx.coroutines.runBlocking
import me.lian.hsc.ktypst.backend.command.typst
import me.lian.hsc.ktypst.data.command.Input
import me.lian.hsc.ktypst.data.command.compile.Output
import me.lian.hsc.ktypst.data.output.Status
import java.nio.file.Path

fun main() = runBlocking {
    val result = typst {
        input = Input.Content("#set page(width: 120pt, height: 60pt)\nHello Typst")
        output = Output.File(Path.of("hello.pdf"))
    }

    check(result.status == Status.Success) { result.error ?: "Unknown error" }
}
```

> Note: The `backend:command` module requires the Typst CLI (`typst`) to be installed and available on your PATH.

## License

MIT. See [LICENCE](LICENCE).
