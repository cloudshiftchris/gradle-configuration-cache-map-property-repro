plugins {
    `java-library`
}

version = "0.1.0-SNAPSHOT"

group = "cloudshift.repro"

/*
    Reproduction of issue with configuration cache / MapProperty / ValueSource providers

    Configuration cache fails to load when placing a ValueSource in a MapProperty.

    This input: mapProp.put("foo", providers.systemProperty("user.name")) causes the below exception on subsequent attempts
    to reload the configuration cache.

    Appears to be this code: org.gradle.api.internal.provider.MapCollectors.EntryWithValueFromProvider.calculateExecutionTimeValue
    ...for any 'fixed value' providers (ValueSources).


org.gradle.api.GradleException: Could not load the value of field `transformer` of `org.gradle.api.internal.provider.TransformBackedProvider` bean found
    in field `providers` of `org.gradle.api.internal.provider.DefaultMapProperty$CollectingProvider` bean
    found in field `__mapProp__` of task `:myTask` of type `Build_gradle$MyTask`.
	at org.gradle.configurationcache.serialization.beans.BeanPropertyReaderKt.readPropertyValue(BeanPropertyReader.kt:108)
	at org.gradle.configurationcache.serialization.beans.BeanPropertyReader.readStateOf(BeanPropertyReader.kt:67)
	at org.gradle.configurationcache.serialization.codecs.BeanCodec.readBeanOf(BeanCodec.kt:72)
	at org.gradle.configurationcache.serialization.codecs.BeanCodec.decode(BeanCodec.kt:47)
	at org.gradle.configurationcache.serialization.codecs.BindingsBackedCodec.decode(BindingsBackedCodec.kt:59)
	at org.gradle.configurationcache.serialization.codecs.FixedValueReplacingProviderCodec.decodeValue(ProviderCodecs.kt:119)
	at org.gradle.configurationcache.serialization.codecs.FixedValueReplacingProviderCodec.decodeProvider(ProviderCodecs.kt:108)
	at org.gradle.configurationcache.serialization.codecs.ProviderCodec.decode(ProviderCodecs.kt:139)
	at org.gradle.configurationcache.serialization.codecs.BindingsBackedCodec.decode(BindingsBackedCodec.kt:59)
	at org.gradle.configurationcache.serialization.DefaultReadContext.read(Contexts.kt:259)
	at org.gradle.configurationcache.serialization.CombinatorsKt.readCollectionInto(Combinators.kt:238)
	at org.gradle.configurationcache.serialization.codecs.CollectionCodecsKt$collectionCodec$2.invokeSuspend(CollectionCodecs.kt:137)
	at org.gradle.configurationcache.serialization.codecs.CollectionCodecsKt$collectionCodec$2.invoke(CollectionCodecs.kt)
	at org.gradle.configurationcache.serialization.codecs.CollectionCodecsKt$collectionCodec$2.invoke(CollectionCodecs.kt)
	at org.gradle.configurationcache.serialization.CombinatorsKt$codec$1.decode(Combinators.kt:90)
	at org.gradle.configurationcache.serialization.codecs.BindingsBackedCodec.decode(BindingsBackedCodec.kt:59)
	at org.gradle.configurationcache.serialization.DefaultReadContext.read(Contexts.kt:259)
	at org.gradle.configurationcache.serialization.beans.BeanPropertyReaderKt.readPropertyValue(BeanPropertyReader.kt:102)
	at org.gradle.configurationcache.serialization.beans.BeanPropertyReader.readStateOf(BeanPropertyReader.kt:67)
	at org.gradle.configurationcache.serialization.codecs.BeanCodec.readBeanOf(BeanCodec.kt:72)
	at org.gradle.configurationcache.serialization.codecs.BeanCodec.decode(BeanCodec.kt:47)
	at org.gradle.configurationcache.serialization.codecs.BindingsBackedCodec.decode(BindingsBackedCodec.kt:59)
	at org.gradle.configurationcache.serialization.codecs.FixedValueReplacingProviderCodec.decodeValue(ProviderCodecs.kt:119)
	at org.gradle.configurationcache.serialization.codecs.MapPropertyCodec.decode(ProviderCodecs.kt:351)
	at org.gradle.configurationcache.serialization.codecs.BindingsBackedCodec.decode(BindingsBackedCodec.kt:59)
	...
Caused by: java.lang.ClassNotFoundException: org.gradle.api.internal.provider.MapCollectors$EntryWithValueFromProvider$$Lambda$2491/0x0000000801b1ea28
	at org.gradle.configurationcache.serialization.DefaultReadContext.readClass(Contexts.kt:285)
	at org.gradle.configurationcache.serialization.codecs.BeanCodec.decode(BeanCodec.kt:44)
	at org.gradle.configurationcache.serialization.CombinatorsKt$reentrant$1$decodeLoop$1.invokeSuspend(Combinators.kt:165)
	at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
	at kotlin.coroutines.ContinuationKt.startCoroutine(Continuation.kt:115)
	at org.gradle.configurationcache.serialization.CombinatorsKt$reentrant$1.decodeLoop(Combinators.kt:166)
	at org.gradle.configurationcache.serialization.CombinatorsKt$reentrant$1.decode(Combinators.kt:130)
	at org.gradle.configurationcache.serialization.codecs.BindingsBackedCodec.decode(BindingsBackedCodec.kt:59)
	at org.gradle.configurationcache.serialization.DefaultReadContext.read(Contexts.kt:259)
	at org.gradle.configurationcache.serialization.beans.BeanPropertyReaderKt.readPropertyValue(BeanPropertyReader.kt:102)

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
