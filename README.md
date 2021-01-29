# kotlin-js-experiment

Run the project with `./gradlew run --continuous`. It will start a browser for you automatically,
showing the [index.html](src/main/resources/index.html) file automatically.
It will also recompile any changes made to the `main.kt` file on the fly, reloading
the page once the recompilation is done.

Simply open the project in your Intellij IDEA (Community edition is enough) and start
experimenting.

Demoes:

* Type-safe JSON parsing
* Coroutines accessing JavaScript Promises

## More

Please read the excellent tutorial [Building Web Applications with React and Kotlin/JS](https://play.kotlinlang.org/hands-on/Building%20Web%20Applications%20with%20React%20and%20Kotlin%20JS/08_Using_an_External_REST_API)
on how to use coroutines with JavaScript Promises.

I'm using the [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization)
API to deserialize JSON into data classes, since that will also perform a validation,
and supports deserializing to `List` as well (as opposed to just `Array`).
