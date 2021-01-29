# kotlin-js-experiment

Start with `./gradlew run --continuous`. It will start a browser for you automatically,
showing the [index.html](src/main/resources/index.html) file automatically.

Edit the `main.kt` file and watch Gradle recompile it automatically, reloading
the page on the fly.

Please read the excellent tutorial [Building Web Applications with React and Kotlin/JS](https://play.kotlinlang.org/hands-on/Building%20Web%20Applications%20with%20React%20and%20Kotlin%20JS/08_Using_an_External_REST_API)
on how to use coroutines with promises.

I'm using the [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization)
API to deserialize JSON into data classes, since that will also perform a validation,
and supports deserializing to `Lists` as well.
