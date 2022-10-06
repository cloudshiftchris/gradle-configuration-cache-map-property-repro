plugins {
    `java-library`
}

version = "0.1.0-SNAPSHOT"

group = "cloudshift.repro"

/*


 */

val myTask by tasks.registering(MyTask::class) {
    // fails
    mapProp.put("foo", providers.systemProperty("user.name"))

    // fails
  //  mapProp.put("foo", providers.environmentVariable("user.name"))

    // works
   // mapProp.put("foo", provider { "foo" })

    // works
    //mapProp.put("foo", provider { System.getenv("user.name") })
}

tasks.named("build") {
    dependsOn(myTask)
}

abstract class MyTask : DefaultTask() {
    @get:Input
    abstract val mapProp : MapProperty<String,String>
}
